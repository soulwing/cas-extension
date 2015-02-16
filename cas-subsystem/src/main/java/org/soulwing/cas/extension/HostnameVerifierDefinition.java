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
import org.jboss.as.controller.OperationContext.Stage;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.OperationStepHandler;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.RestartParentResourceAddHandler;
import org.jboss.as.controller.RestartParentResourceRemoveHandler;
import org.jboss.as.controller.SimpleAttributeDefinition;
import org.jboss.as.controller.SimpleAttributeDefinitionBuilder;
import org.jboss.as.controller.SimpleListAttributeDefinition;
import org.jboss.as.controller.SimpleResourceDefinition;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.as.controller.operations.common.Util;
import org.jboss.as.controller.registry.AttributeAccess;
import org.jboss.as.controller.registry.ManagementResourceRegistration;
import org.jboss.as.controller.registry.Resource;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;
import org.jboss.msc.service.ServiceName;

/**
 * 
 * A definition for the server host verifier resource.
 *
 * @author Carl Harris
 */
class HostnameVerifierDefinition extends SimpleResourceDefinition {

  static final SimpleAttributeDefinition HOST =
      new SimpleAttributeDefinitionBuilder(Names.HOST, 
          ModelType.STRING)
              .setAllowExpression(true)
              .setAllowNull(false)
              .build();
              
  static final SimpleListAttributeDefinition HOSTS =
      SimpleListAttributeDefinition.Builder.of(Names.HOSTS, HOST)
          .setAllowNull(true)
          .setAllowExpression(false)
          .setFlags(AttributeAccess.Flag.RESTART_RESOURCE_SERVICES,
              AttributeAccess.Flag.STORAGE_CONFIGURATION)
          .build();

  public static final HostnameVerifierDefinition INSTANCE =
      new HostnameVerifierDefinition();
    
  private HostnameVerifierDefinition() {
    super(Paths.SERVER_HOST_VERIFIER, 
        ResourceUtil.getResolver(
            Names.PROFILE, Names.HOSTNAME_VERIFIER),
        HostnameVerifierAdd.INSTANCE,
        HostnameVerifierRemove.INSTANCE);
  }
  
  public static AttributeDefinition[] attributes() {
    return new AttributeDefinition[] {
        HOSTS
    };
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
    
  static class HostnameVerifierAdd extends RestartParentResourceAddHandler {
    
    static final HostnameVerifierAdd INSTANCE = new HostnameVerifierAdd();
    
    private HostnameVerifierAdd() {
      super(Names.PROFILE);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void updateModel(OperationContext context, ModelNode operation)
        throws OperationFailedException {
      super.updateModel(context, operation);
      context.addStep(createValidateOperation(operation), 
          HostnameVerifierValidationHandler.INSTANCE, Stage.MODEL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void populateModel(ModelNode operation, ModelNode model)
        throws OperationFailedException {
      for (AttributeDefinition attribute : attributes()) {
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
        PathAddress parentAddress, ModelNode parentModel)
        throws OperationFailedException {
      ProfileService.ServiceUtil.installService(context, parentModel, 
          parentAddress);
    }

    private static ModelNode createValidateOperation(
        ModelNode operationToValidate) {
      PathAddress pa = PathAddress.pathAddress(
          operationToValidate.require(ModelDescriptionConstants.OP_ADDR));
      PathAddress verifierAddr = null;
      for (int i = pa.size() - 1; i > 0; i--) {
          PathElement pe = pa.getElement(i);
          if (Names.PROFILE.equals(pe.getKey())) {
              verifierAddr = pa.subAddress(0, i + 1);
              break;
          }
      }

      assert verifierAddr != null : 
        "operationToValidate did not have an address with " + Names.PROFILE;
      return Util.getEmptyOperation("validate-host-verifier", 
          verifierAddr.toModelNode());
    }
    
  }

  static class HostnameVerifierRemove extends RestartParentResourceRemoveHandler {
    
    static final HostnameVerifierRemove INSTANCE = new HostnameVerifierRemove();
    
    private HostnameVerifierRemove() {
      super(Names.PROFILE);
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
        PathAddress parentAddress, ModelNode parentModel)
        throws OperationFailedException {
      ProfileService.ServiceUtil.installService(context, parentModel, 
          parentAddress);
    }

  }

  static class HostnameVerifierValidationHandler 
      implements OperationStepHandler {
    
    static final OperationStepHandler INSTANCE = 
        new HostnameVerifierValidationHandler();
    
    private HostnameVerifierValidationHandler() {      
    }
    
    @Override
    public void execute(OperationContext context, ModelNode operation)
        throws OperationFailedException {
      Resource resource = context.readResource(PathAddress.EMPTY_ADDRESS);
      if (resource.getChildrenNames(Names.HOSTNAME_VERIFIER).size() > 1) {
        throw new OperationFailedException("no more than one "
            + Names.HOSTNAME_VERIFIER + " may be configured");
      }
    }

  }

}
