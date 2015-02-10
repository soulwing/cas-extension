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

import java.util.ArrayList;
import java.util.List;

import org.jboss.as.controller.AbstractAddStepHandler;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.ServiceVerificationHandler;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.dmr.ModelNode;
import org.jboss.msc.service.ServiceController;
import org.soulwing.cas.service.AuthenticationService;
import org.soulwing.cas.service.MutableConfiguration;

/**
 * An add step handler for a proxy chain resource.
 *
 * @author Carl Harris
 */
class ProxyChainAdd extends AbstractAddStepHandler {

  public static final ProxyChainAdd INSTANCE = 
      new ProxyChainAdd();
  
  private ProxyChainAdd() {    
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void populateModel(ModelNode operation, ModelNode model)
      throws OperationFailedException {
    ProxyChainDefinition.PROXIES.validateAndSet(operation, model);
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
    
    List<String> chain = new ArrayList<>();
    for (ModelNode element : ProxyChainDefinition.PROXIES
        .resolveModelAttribute(context, model).asList()) {
      chain.add(element.asString());
    }
    
    ModelNode address = operation.get(ModelDescriptionConstants.ADDRESS);
    String name = address.asPropertyList().get(address.asInt() - 1)
        .getValue().asString();

    AuthenticationService service = AuthenticationServiceControl
        .locateService(context, address);
    
    MutableConfiguration config = service.getConfiguration().clone();
    config.putAllowedProxyChain(name, chain);
    service.reconfigure(config);
    
    super.performRuntime(context, operation, model, verificationHandler,
        newControllers);
  }
  
}
