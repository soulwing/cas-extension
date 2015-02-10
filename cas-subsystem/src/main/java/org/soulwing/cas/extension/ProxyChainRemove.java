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

import org.jboss.as.controller.AbstractRemoveStepHandler;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.dmr.ModelNode;
import org.soulwing.cas.service.AuthenticationService;
import org.soulwing.cas.service.MutableConfiguration;

/**
 * A remove step handler for a proxy chain resource.
 *
 * @author Carl Harris
 */
class ProxyChainRemove extends AbstractRemoveStepHandler {

  public static final ProxyChainRemove INSTANCE = 
      new ProxyChainRemove();
    
  private ProxyChainRemove() {    
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void performRuntime(OperationContext context,
      ModelNode operation, ModelNode model) throws OperationFailedException {
    
    ModelNode address = operation.get(ModelDescriptionConstants.ADDRESS);
    String name = address.asPropertyList().get(address.asInt() - 1)
        .getValue().asString();

    AuthenticationService service = AuthenticationServiceControl
        .locateService(context, address);
    
    MutableConfiguration config = service.getConfiguration().clone();
    config.removeAllowedProxyChain(name);
    service.reconfigure(config);
  }
  
}
