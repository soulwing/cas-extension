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
import org.jboss.dmr.ModelNode;
import org.jboss.msc.service.ServiceController;

/**
 * An add step handler for the SAML authorization resource.
 *
 * @author Carl Harris
 */
public class SamlAdd extends AbstractAddStepHandler {

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
    super.performRuntime(context, operation, model, verificationHandler,
        newControllers);
  }
  
}
