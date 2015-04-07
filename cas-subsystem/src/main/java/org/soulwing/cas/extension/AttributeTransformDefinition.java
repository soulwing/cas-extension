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
import org.jboss.as.controller.SimpleResourceDefinition;
import org.jboss.as.controller.registry.ManagementResourceRegistration;
import org.jboss.dmr.ModelNode;
import org.jboss.msc.service.ServiceName;

/**
 * A definition for the {@code attribute-transformer} resource type.
 *
 * @author Carl Harris
 */
public class AttributeTransformDefinition extends SimpleResourceDefinition {

  public static AttributeDefinition[] attributes() {
    return new AttributeDefinition[] {
    };
  }
  
  public static final AttributeTransformDefinition INSTANCE =
      new AttributeTransformDefinition();
  
  private AttributeTransformDefinition() {
    super(Paths.ATTRIBUTE_TRANSFORM, 
        ResourceUtil.getResolver(Names.CAS_PROFILE, Names.ATTRIBUTE_TRANSFORM),
        AttributeTransformAdd.INSTANCE,
        AttributeTransformRemove.INSTANCE);
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

  /**
   * {@inheritDoc}
   */
  @Override
  public void registerChildren(
      ManagementResourceRegistration resourceRegistration) {
    super.registerChildren(resourceRegistration);
    resourceRegistration.registerSubModel(TransformerDefinition.INSTANCE);
  }

  static class AttributeTransformAdd 
      extends RestartParentResourceAddHandler {
    
    static final AttributeTransformAdd INSTANCE =
        new AttributeTransformAdd();
    
    private AttributeTransformAdd() {
      super(Names.CAS_PROFILE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void populateModel(ModelNode operation, ModelNode model)
        throws OperationFailedException {
      for (AttributeDefinition attribute : 
          AttributeTransformDefinition.attributes()) {
        attribute.validateAndSet(operation, model);
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
  
  static class AttributeTransformRemove   
      extends RestartParentResourceRemoveHandler {
    
    static final AttributeTransformRemove INSTANCE =
        new AttributeTransformRemove();
    
    private AttributeTransformRemove() {      
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
