/*
 * File created on Feb 16, 2015 
 *
 * Copyright (c) 2014 Virginia Polytechnic Institute and State University
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

import org.jboss.as.controller.AbstractWriteAttributeHandler;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.dmr.ModelNode;
import org.jboss.msc.service.ServiceName;

/**
 * A write attribute handler for the security realm attribute.
 * <p>
 * This attribute must be handled separately from the other attributes of
 * {@link ProfileDefinition} because it is associated with its own service,
 * and does not need to restart the services of its parent profile resource.
 *
 * @author Carl Harris
 */
public class SecurityRealmWriteAttributeHandler
    extends AbstractWriteAttributeHandler<Void> {

  public static final SecurityRealmWriteAttributeHandler INSTANCE =
      new SecurityRealmWriteAttributeHandler();
  
  private SecurityRealmWriteAttributeHandler() {
    super(ProfileDefinition.SECURITY_REALM);
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean applyUpdateToRuntime(
      OperationContext context,
      ModelNode operation,
      String attributeName,
      ModelNode resolvedValue,
      ModelNode currentValue,
      org.jboss.as.controller.AbstractWriteAttributeHandler.HandbackHolder<Void> handbackHolder)
      throws OperationFailedException {
    
    PathAddress profileAddress = PathAddress.pathAddress(
        operation.get(ModelDescriptionConstants.OP_ADDR));

    WrapperSSLContextService.ServiceUtil.removeService(context, profileAddress);
    
    WrapperSSLContextService.ServiceUtil.installService(context, profileAddress, 
        resolvedValue.isDefined() ? resolvedValue.asString() : null);    
   
    return context.isResourceServiceRestartAllowed();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void revertUpdateToRuntime(OperationContext context,
      ModelNode operation, String attributeName, ModelNode valueToRestore,
      ModelNode valueToRevert, Void handback) throws OperationFailedException {

    PathAddress profileAddress = PathAddress.pathAddress(
        operation.get(ModelDescriptionConstants.OP_ADDR));

    WrapperSSLContextService.ServiceUtil.removeService(context, profileAddress);
    
    WrapperSSLContextService.ServiceUtil.installService(context, profileAddress, 
        valueToRestore.isDefined() ? valueToRestore.asString() : null);    
  }

  
}
