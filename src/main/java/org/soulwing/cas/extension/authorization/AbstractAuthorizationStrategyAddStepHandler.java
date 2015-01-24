/*
 * File created on Jan 24, 2015 
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
import org.jboss.msc.service.ServiceName;
import org.soulwing.cas.service.authorization.AuthorizationService;

/**
 * An abstract base for add step handlers for authorization strategies.
 *
 * @author Carl Harris
 */
abstract class AbstractAuthorizationStrategyAddStepHandler
    extends AbstractAddStepHandler {

  /**
   * {@inheritDoc}
   */
  @Override
  protected final void performRuntime(OperationContext context,
      ModelNode operation, ModelNode model,
      ServiceVerificationHandler verificationHandler,
      List<ServiceController<?>> newControllers)
      throws OperationFailedException {
    
    ModelNode address = operation.get(ModelDescriptionConstants.ADDRESS);
    
    String strategyName = address.asPropertyList().get(address.asInt() - 1)
        .getValue().asString();     
    
    performRuntime(context, operation, model, 
        findTargetService(context, operation), strategyName);
    
    super.performRuntime(context, operation, model, verificationHandler,
        newControllers);
  }

  protected abstract void performRuntime(OperationContext context,
      ModelNode operation, ModelNode model, AuthorizationService service,
      String strategyName) 
          throws OperationFailedException;

  private AuthorizationService findTargetService(OperationContext context,
      ModelNode operation) throws OperationFailedException {
    ServiceName serviceName = AuthorizationServiceControl.name(
        operation.get(ModelDescriptionConstants.ADDRESS), -1);
    ServiceController<?> controller = context.getServiceRegistry(true)
        .getRequiredService(serviceName);    
    return (AuthorizationService) controller.getService().getValue();
  }

}
