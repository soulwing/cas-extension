/*
 * File created on Jan 22, 2015 
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
package org.soulwing.cas.extension.authentication;

import org.jboss.as.controller.AbstractWriteAttributeHandler;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.SimpleAttributeDefinition;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.dmr.ModelNode;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceName;
import org.soulwing.cas.extension.SubsystemExtension;
import org.soulwing.cas.service.authentication.AuthenticationService;
import org.soulwing.cas.service.authentication.MutableConfiguration;

/**
 * An abstract base for writable authentication resource attributes.
 *
 * @author Carl Harris
 */
abstract class AbstractAuthenticationAttributeHandler<T>
    extends AbstractWriteAttributeHandler<T> {

  protected AbstractAuthenticationAttributeHandler(
      SimpleAttributeDefinition definition) {
    super(definition);
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  protected final boolean applyUpdateToRuntime(
      OperationContext context,
      ModelNode operation,
      String attributeName,
      ModelNode resolvedValue,
      ModelNode currentValue,
      org.jboss.as.controller.AbstractWriteAttributeHandler.HandbackHolder<T> handbackHolder)
      throws OperationFailedException {

    AuthenticationService service = findTargetService(context, operation);
    MutableConfiguration config = service.getConfiguration().clone();
    T handback = applyUpdateToConfiguration(attributeName, resolvedValue, config);
    handbackHolder.setHandback(handback);
    service.reconfigure(config);
    context.stepCompleted();
    SubsystemExtension.logger.info("reconfigured : " + config);
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final void revertUpdateToRuntime(OperationContext context,
      ModelNode operation, String attributeName, ModelNode valueToRestore,
      ModelNode valueToRevert, T handback) throws OperationFailedException {
    
    AuthenticationService service = findTargetService(context, operation);
    MutableConfiguration config = service.getConfiguration().clone();
    revertUpdateToConfiguration(attributeName, valueToRestore, config, handback);
    service.reconfigure(config);
    context.stepCompleted();   
  }

  protected abstract T applyUpdateToConfiguration(String attributeName, 
      ModelNode value, MutableConfiguration config) 
          throws OperationFailedException;

  protected abstract void revertUpdateToConfiguration(String attributeName, 
      ModelNode value, MutableConfiguration config, T handback) 
          throws OperationFailedException;

  private AuthenticationService findTargetService(OperationContext context,
      ModelNode operation) throws OperationFailedException {
    ModelNode address = operation.get(ModelDescriptionConstants.ADDRESS);
    String target = address.asPropertyList().get(address.asInt() - 1)
        .getValue().asString();
    
    ServiceName serviceName = AuthenticationServiceControl.name(target);
    ServiceController<?> controller = context.getServiceRegistry(true)
        .getRequiredService(serviceName);    
    AuthenticationService service = (AuthenticationService) 
        controller.getService().getValue();
    
    return service;
  }


}
