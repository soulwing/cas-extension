/*
 * File created on Dec 15, 2014 
 *
 * Copyright (c) 2015 Carl Harris, Jr.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.soulwing.cas.extension.authorization;

import java.util.List;

import org.jboss.as.controller.AbstractAddStepHandler;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.ServiceVerificationHandler;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.dmr.ModelNode;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceController.Mode;
import org.jboss.msc.service.ServiceName;
import org.soulwing.cas.extension.SubsystemExtension;
import org.soulwing.cas.service.authorization.AuthorizationConfig;
import org.soulwing.cas.service.authorization.AuthorizationService;
import org.soulwing.cas.service.authorization.AuthorizationServiceFactory;
import org.soulwing.cas.service.authorization.MutableAuthorizationConfig;

/**
 * An add step handler for the authorization resource.
 *
 * @author Carl Harris
 */
public class AuthorizationAdd extends AbstractAddStepHandler {

  public static final AuthorizationAdd INSTANCE = 
      new AuthorizationAdd();
  
  private AuthorizationAdd() {    
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void populateModel(ModelNode operation, ModelNode model)
      throws OperationFailedException {
    AuthorizationDefinition.DEFAULT_ROLE.validateAndSet(operation, model);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void performRuntime(OperationContext context,
      ModelNode operation, ModelNode model,
      ServiceVerificationHandler verificationHandler,
      List<ServiceController<?>> newControllers)
      throws OperationFailedException {
    
    ModelNode localName = operation.get(ModelDescriptionConstants.ADDRESS);
    ServiceName serviceName = AuthorizationServiceControl.name(localName);
    
    AuthorizationService service = AuthorizationServiceFactory.newInstance(
        localName.asString());
    
    service.reconfigure(applyConfiguration(context, model,
        service.getConfiguration()));
    
    ServiceController<AuthorizationService> controller = context
        .getServiceTarget()
        .addService(serviceName, new AuthorizationServiceControl(service))
        .addListener(verificationHandler)
        .setInitialMode(Mode.ACTIVE)
        .install();
    
    SubsystemExtension.logger.info("added authorization service " + serviceName);

    newControllers.add(controller);

    super.performRuntime(context, operation, model, verificationHandler,
        newControllers);
  }

  /**
   * Applies the model configuration to the given authorization config.
   * @param context operation context
   * @param model model containing the configuration to apply
   * @param config target authorization configuration
   * @return configuration with model configuration applied
   * @throws OperationFailedException
   */
  private AuthorizationConfig applyConfiguration(
      OperationContext context, ModelNode model,
      MutableAuthorizationConfig config) throws OperationFailedException {
    config.setDefaultRole(AuthorizationDefinition.DEFAULT_ROLE
        .resolveModelAttribute(context, model).asString());
    return config;
  }
  
}
