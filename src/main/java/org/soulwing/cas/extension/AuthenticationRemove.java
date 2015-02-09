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

import static org.soulwing.cas.extension.ExtensionLogger.LOGGER;

import org.jboss.as.controller.AbstractRemoveStepHandler;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.dmr.ModelNode;
import org.jboss.msc.service.ServiceName;

/**
 * A remove step handler for the authentication resource.
 *
 * @author Carl Harris
 */
class AuthenticationRemove extends AbstractRemoveStepHandler {

  public static final AuthenticationRemove INSTANCE = 
      new AuthenticationRemove();
    
  private AuthenticationRemove() {    
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void performRuntime(OperationContext context,
      ModelNode operation, ModelNode model) throws OperationFailedException {
    
    ServiceName serviceName = AuthenticationServiceControl.name(
        operation.get(ModelDescriptionConstants.ADDRESS));
    
    context.removeService(serviceName);
    LOGGER.info("removed service " + serviceName);
    
    super.performRuntime(context, operation, model);
  }
  
}
