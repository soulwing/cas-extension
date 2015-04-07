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

import org.jboss.as.controller.AttributeDefinition;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.RestartParentResourceAddHandler;
import org.jboss.as.controller.RestartParentResourceRemoveHandler;
import org.jboss.as.controller.ServiceVerificationHandler;
import org.jboss.as.controller.SimpleAttributeDefinition;
import org.jboss.as.controller.SimpleAttributeDefinitionBuilder;
import org.jboss.as.controller.SimpleListAttributeDefinition;
import org.jboss.as.controller.SimpleResourceDefinition;
import org.jboss.as.controller.registry.AttributeAccess;
import org.jboss.as.controller.registry.ManagementResourceRegistration;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;
import org.jboss.msc.service.ServiceName;

/**
 * 
 * A definition for the proxy chain resource.
 *
 * @author Carl Harris
 */
class ProxyChainDefinition extends SimpleResourceDefinition {

  static final SimpleAttributeDefinition PROXY =
      new SimpleAttributeDefinitionBuilder(Names.PROXY, 
          ModelType.STRING)
              .setAllowExpression(true)
              .setAllowNull(false)
              .build();
              
  static final SimpleListAttributeDefinition PROXIES =
      SimpleListAttributeDefinition.Builder.of(Names.PROXIES, PROXY)
          .setAllowNull(false)
          .setAllowExpression(false)
          .setFlags(AttributeAccess.Flag.RESTART_RESOURCE_SERVICES,
              AttributeAccess.Flag.STORAGE_CONFIGURATION)
          .build();

  public static final ProxyChainDefinition INSTANCE =
      new ProxyChainDefinition();
  
  public static AttributeDefinition[] attributes() {
    return new AttributeDefinition[] { 
        PROXIES
    };
  }

  private ProxyChainDefinition() {
    super(Paths.PROXY_CHAIN, 
        ResourceUtil.getResolver(
            Names.CAS_PROFILE, Names.PROXY_CHAIN),
        ProxyChainAdd.INSTANCE,
        ProxyChainRemove.INSTANCE);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void registerAttributes(
      ManagementResourceRegistration resourceRegistration) {
    for (AttributeDefinition attribute : attributes()) {
      resourceRegistration.registerReadWriteAttribute(attribute, null, 
          ProfileDefinition.ProfileWriteAttributeHandler.INSTANCE);
    }
    super.registerAttributes(resourceRegistration);
  }

  
  static class ProxyChainAdd extends RestartParentResourceAddHandler {

    static final ProxyChainAdd INSTANCE = new ProxyChainAdd();
    
    private ProxyChainAdd() {
      super(Names.CAS_PROFILE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void populateModel(ModelNode operation, ModelNode model)
        throws OperationFailedException {
      for (AttributeDefinition attribute : ProxyChainDefinition.attributes()) {
        attribute.validateAndSet(operation, model);
      }    
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ServiceName getParentServiceName(PathAddress parentAddress) {
      return ProfileService.ServiceUtil.profileServiceName(parentAddress);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void recreateParentService(OperationContext context,
        PathAddress parentAddress, ModelNode parentModel,
        ServiceVerificationHandler verificationHandler)
        throws OperationFailedException {
      ProfileService.ServiceUtil.installService(context, parentModel, 
          parentAddress);
    }

  }
  
  static class ProxyChainRemove extends RestartParentResourceRemoveHandler {

    static final ProxyChainRemove INSTANCE = new ProxyChainRemove();
      
    private ProxyChainRemove() {
      super(Names.CAS_PROFILE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ServiceName getParentServiceName(PathAddress parentAddress) {
      return ProfileService.ServiceUtil.profileServiceName(
          parentAddress);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void recreateParentService(OperationContext context,
        PathAddress parentAddress, ModelNode parentModel,
        ServiceVerificationHandler verificationHandler)
        throws OperationFailedException {
      ProfileService.ServiceUtil.installService(context, parentModel, 
          parentAddress);
    }

  }

}
