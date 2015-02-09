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
package org.soulwing.cas.extension;

import java.util.List;

import static org.soulwing.cas.extension.ExtensionLogger.LOGGER;

import org.jboss.as.controller.AbstractAddStepHandler;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.ServiceVerificationHandler;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.dmr.ModelNode;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceController.Mode;
import org.jboss.msc.service.ServiceName;
import org.soulwing.cas.service.AuthenticationProtocol;
import org.soulwing.cas.service.AuthenticationService;
import org.soulwing.cas.service.AuthenticationServiceFactory;
import org.soulwing.cas.service.Configuration;
import org.soulwing.cas.service.MutableConfiguration;

/**
 * An add step handler for the authentication resource.
 *
 * @author Carl Harris
 */
class AuthenticationAdd extends AbstractAddStepHandler {

  public static final AuthenticationAdd INSTANCE = 
      new AuthenticationAdd();
  
  private AuthenticationAdd() {    
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void populateModel(ModelNode operation, ModelNode model)
      throws OperationFailedException {
    AuthenticationDefinition.PROTOCOL.validateAndSet(operation, model);
    AuthenticationDefinition.SERVICE_URL.validateAndSet(operation, model);
    AuthenticationDefinition.SERVER_URL.validateAndSet(operation, model);
    AuthenticationDefinition.PROXY_CALLBACK_URL.validateAndSet(operation, model);
    AuthenticationDefinition.ACCEPT_ANY_PROXY.validateAndSet(operation, model);
    AuthenticationDefinition.ALLOW_EMPTY_PROXY_CHAIN.validateAndSet(operation, model);
    AuthenticationDefinition.RENEW.validateAndSet(operation, model);
    AuthenticationDefinition.CLOCK_SKEW_TOLERANCE.validateAndSet(operation, model);
    super.populateModel(operation, model);
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
    
    String resourceName = AuthenticationServiceControl.resourceName(
        operation.get(ModelDescriptionConstants.ADDRESS));
    ServiceName serviceName = AuthenticationServiceControl.name(resourceName);
    
    AuthenticationService service = AuthenticationServiceFactory
        .newInstance(resourceName);
    
    service.reconfigure(applyConfiguration(context, model, 
        service.getConfiguration()));
    
    ServiceController<AuthenticationService> controller = context
        .getServiceTarget()
        .addService(serviceName, new AuthenticationServiceControl(service))
        .addListener(verificationHandler)
        .setInitialMode(Mode.ACTIVE)
        .install();
    

    newControllers.add(controller);
    LOGGER.debug("registered service " + serviceName);
    super.performRuntime(context, operation, model, verificationHandler,
        newControllers);
  }

  private Configuration applyConfiguration(OperationContext context,
      ModelNode model, MutableConfiguration config) 
          throws OperationFailedException {
    config.setProtocol(AuthenticationProtocol.toObject(
        AuthenticationDefinition.PROTOCOL.resolveModelAttribute(context, model)
            .asString()));
    config.setServerUrl(AuthenticationDefinition.SERVER_URL
        .resolveModelAttribute(context, model).asString());
    config.setServiceUrl(AuthenticationDefinition.SERVICE_URL
        .resolveModelAttribute(context, model).asString());
    config.setProxyCallbackUrl(AuthenticationDefinition.PROXY_CALLBACK_URL
        .resolveModelAttribute(context, model).asString());
    config.setAcceptAnyProxy(AuthenticationDefinition.ACCEPT_ANY_PROXY
        .resolveModelAttribute(context, model).asBoolean());
    config.setAllowEmptyProxyChain(AuthenticationDefinition.ALLOW_EMPTY_PROXY_CHAIN
        .resolveModelAttribute(context, model).asBoolean());
    config.setRenew(AuthenticationDefinition.RENEW
        .resolveModelAttribute(context, model).asBoolean());
    config.setClockSkewTolerance(AuthenticationDefinition.CLOCK_SKEW_TOLERANCE
        .resolveModelAttribute(context, model).asLong());
    return config;
  }
  
}
