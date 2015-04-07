/*
 * File created on Feb 24, 2015 
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

import org.jboss.as.controller.AttributeDefinition;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.RestartParentResourceAddHandler;
import org.jboss.as.controller.RestartParentResourceRemoveHandler;
import org.jboss.as.controller.ServiceVerificationHandler;
import org.jboss.as.controller.SimpleAttributeDefinition;
import org.jboss.as.controller.SimpleAttributeDefinitionBuilder;
import org.jboss.as.controller.SimpleMapAttributeDefinition;
import org.jboss.as.controller.SimpleResourceDefinition;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.as.controller.registry.AttributeAccess;
import org.jboss.as.controller.registry.ManagementResourceRegistration;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;
import org.jboss.msc.service.ServiceName;

/**
 * A definition for the {@code attribute-transformer} resource type.
 *
 * @author Carl Harris
 */
public class TransformerDefinition extends SimpleResourceDefinition {

  static final SimpleAttributeDefinition CODE =
      new SimpleAttributeDefinitionBuilder(Names.CODE, 
          ModelType.STRING)
              .setAllowExpression(true)
              .setAllowNull(true)
              .setFlags(AttributeAccess.Flag.RESTART_RESOURCE_SERVICES,
                  AttributeAccess.Flag.STORAGE_CONFIGURATION)
              .build();

  static final SimpleAttributeDefinition MODULE =
      new SimpleAttributeDefinitionBuilder(Names.MODULE, 
          ModelType.STRING)
              .setAllowExpression(true)
              .setAllowNull(true)
              .build();

  static final SimpleMapAttributeDefinition OPTIONS =
      new SimpleMapAttributeDefinition.Builder(Names.OPTIONS, false)
          .setAllowNull(true)
          .setAllowExpression(false)
          .setFlags(AttributeAccess.Flag.RESTART_RESOURCE_SERVICES,
              AttributeAccess.Flag.STORAGE_CONFIGURATION)
          .build();

  public static AttributeDefinition[] attributes() {
    return new AttributeDefinition[] { 
        CODE, MODULE, OPTIONS        
    };
  }
  
  public static final TransformerDefinition INSTANCE =
      new TransformerDefinition();
  
  private TransformerDefinition() {
    super(Paths.TRANSFORMER, 
        ResourceUtil.getResolver(Names.CAS_PROFILE, Names.ATTRIBUTE_TRANSFORM,
            Names.TRANSFORMER),
        TransformerAdd.INSTANCE,
        TransformerRemove.INSTANCE);
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

  static class TransformerAdd 
      extends RestartParentResourceAddHandler {
    
    static final TransformerAdd INSTANCE =
        new TransformerAdd();
    
    private TransformerAdd() {
      super(Names.CAS_PROFILE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void populateModel(ModelNode operation, ModelNode model)
        throws OperationFailedException {
      for (AttributeDefinition attribute : TransformerDefinition.attributes()) {
        attribute.validateAndSet(operation, model);
      }
      ModelNode code = model.get(Names.CODE);
      if (!code.isDefined()) {
        PathAddress address = PathAddress.pathAddress(
            operation.require(ModelDescriptionConstants.OP_ADDR));
        String name = address.getLastElement().getValue();
        code.set(name);
      }
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

    /**
     * {@inheritDoc}
     */
    @Override
    protected ServiceName getParentServiceName(PathAddress parentAddress) {
      return ProfileService.ServiceUtil.profileServiceName(parentAddress);
    }
    
  }
  
  static class TransformerRemove   
      extends RestartParentResourceRemoveHandler {
    
    static final TransformerRemove INSTANCE =
        new TransformerRemove();
    
    private TransformerRemove() {      
      super(Names.CAS_PROFILE);
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

    /**
     * {@inheritDoc}
     */
    @Override
    protected ServiceName getParentServiceName(PathAddress parentAddress) {
      return ProfileService.ServiceUtil.profileServiceName(parentAddress);
    }
    
  }
  
}
