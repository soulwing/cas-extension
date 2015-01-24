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

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.dmr.ModelNode;
import org.soulwing.cas.service.authorization.AuthorizationService;
import org.soulwing.cas.service.authorization.SamlAuthorizationConfig;
import org.soulwing.cas.service.authorization.SamlAuthorizationStrategy;

/**
 * An add step handler for the SAML authorization resource.
 *
 * @author Carl Harris
 */
public class SamlAdd extends AbstractAuthorizationStrategyAddStepHandler {

  public static final SamlAdd INSTANCE = 
      new SamlAdd();
  
  private SamlAdd() {    
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void populateModel(ModelNode operation, ModelNode model)
      throws OperationFailedException {
    SamlDefinition.ROLE_ATTRIBUTES.validateAndSet(operation, model);
    super.populateModel(operation, model);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void performRuntime(OperationContext context,
      ModelNode operation, ModelNode model, AuthorizationService service,
      String strategyName) throws OperationFailedException {
    
    Set<String> roleAttributes = new LinkedHashSet<>();
    List<ModelNode> list = SamlDefinition.ROLE_ATTRIBUTES
        .resolveModelAttribute(context, model).asList();
    for (ModelNode value : list) {
      roleAttributes.add(value.asString());
    }

    SamlAuthorizationStrategy strategy = new SamlAuthorizationStrategy();
    SamlAuthorizationConfig config = strategy.getConfiguration();
    config.setRoleAttributes(roleAttributes);
    strategy.reconfigure(config);
    service.putStrategy(strategyName, strategy);
  }

  
}
