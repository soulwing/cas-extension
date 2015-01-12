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
package org.soulwing.cas.extension.authentication;

import org.jboss.as.controller.AbstractWriteAttributeHandler;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.dmr.ModelNode;
import org.soulwing.cas.extension.SubsystemExtension;

/** 
 * A write attribute handler for the authentication protocol attribute.
 *
 * @author Carl Harris
 */
class AuthenticationProtocolHandler extends AbstractWriteAttributeHandler<Void> {

  static final AuthenticationProtocolHandler INSTANCE =
      new AuthenticationProtocolHandler();
  
  private AuthenticationProtocolHandler() {
    super(AuthenticationDefinition.PROTOCOL);
  }
  
  @Override
  protected boolean applyUpdateToRuntime(
      OperationContext context,
      ModelNode operation,
      String attributeName,
      ModelNode resolvedValue,
      ModelNode currentValue,
      AbstractWriteAttributeHandler.HandbackHolder<Void> handbackHolder)
      throws OperationFailedException {

    SubsystemExtension.logger.info("setting attribute " + attributeName);
    return false;
  }

  @Override
  protected void revertUpdateToRuntime(OperationContext context,
      ModelNode operation, String attributeName, ModelNode valueToRestore,
      ModelNode valueToRevert, Void handback) throws OperationFailedException {

  }

}
