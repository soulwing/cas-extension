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

import org.jboss.as.controller.AbstractAddStepHandler;
import org.jboss.as.controller.AbstractRemoveStepHandler;
import org.jboss.as.controller.AttributeDefinition;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.RestartParentWriteAttributeHandler;
import org.jboss.as.controller.SimpleAttributeDefinition;
import org.jboss.as.controller.SimpleAttributeDefinitionBuilder;
import org.jboss.as.controller.SimpleResourceDefinition;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.as.controller.operations.validation.EnumValidator;
import org.jboss.as.controller.registry.AttributeAccess;
import org.jboss.as.controller.registry.ManagementResourceRegistration;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceName;
import org.soulwing.cas.service.AuthenticationProtocol;

/**
 *
 * A definition for the configuration profile resource.
 *
 * @author Carl Harris
 */
public class ProfileDefinition extends SimpleResourceDefinition {

  static final SimpleAttributeDefinition PROTOCOL =
      new SimpleAttributeDefinitionBuilder(Names.PROTOCOL,
          ModelType.STRING)
              .setAllowExpression(true)
              .setAllowNull(true)
              .setDefaultValue(new ModelNode().set(AuthenticationProtocol.CAS2_0.toString()))
              .setValidator(new EnumValidator<>(AuthenticationProtocol.class, true, false))
              .setFlags(AttributeAccess.Flag.RESTART_RESOURCE_SERVICES,
                  AttributeAccess.Flag.STORAGE_CONFIGURATION)
              .build();

  static final SimpleAttributeDefinition ENCODING =
      new SimpleAttributeDefinitionBuilder(Names.ENCODING,
          ModelType.STRING)
              .setAllowExpression(true)
              .setAllowNull(true)
              .setDefaultValue(new ModelNode().set("UTF-8"))
              .setFlags(AttributeAccess.Flag.RESTART_RESOURCE_SERVICES,
                  AttributeAccess.Flag.STORAGE_CONFIGURATION)
              .build();

  static final SimpleAttributeDefinition SERVICE_URL =
      new SimpleAttributeDefinitionBuilder(Names.SERVICE_URL,
          ModelType.STRING)
              .setAllowExpression(true)
              .setAllowNull(false)
              .setFlags(AttributeAccess.Flag.RESTART_RESOURCE_SERVICES,
                  AttributeAccess.Flag.STORAGE_CONFIGURATION)
              .build();

  static final SimpleAttributeDefinition SERVER_URL =
      new SimpleAttributeDefinitionBuilder(Names.SERVER_URL,
          ModelType.STRING)
              .setAllowExpression(true)
              .setAllowNull(false)
              .setFlags(AttributeAccess.Flag.RESTART_RESOURCE_SERVICES,
                  AttributeAccess.Flag.STORAGE_CONFIGURATION)
              .build();

  static final SimpleAttributeDefinition PROXY_CALLBACK_ENABLED =
      new SimpleAttributeDefinitionBuilder(Names.PROXY_CALLBACK_ENABLED,
          ModelType.BOOLEAN)
              .setAllowExpression(true)
              .setAllowNull(true)
              .setDefaultValue(new ModelNode(false))
              .setFlags(AttributeAccess.Flag.RESTART_RESOURCE_SERVICES,
                  AttributeAccess.Flag.STORAGE_CONFIGURATION)
              .build();

  static final SimpleAttributeDefinition PROXY_CALLBACK_PATH =
      new SimpleAttributeDefinitionBuilder(Names.PROXY_CALLBACK_PATH,
          ModelType.STRING)
              .setAllowExpression(true)
              .setAllowNull(true)
              .setDefaultValue(new ModelNode().set("/casProxyCallback"))
              .setFlags(AttributeAccess.Flag.RESTART_RESOURCE_SERVICES,
                  AttributeAccess.Flag.STORAGE_CONFIGURATION)
              .build();

  static final SimpleAttributeDefinition ACCEPT_ANY_PROXY =
      new SimpleAttributeDefinitionBuilder(Names.ACCEPT_ANY_PROXY,
          ModelType.BOOLEAN)
              .setAllowExpression(false)
              .setAllowNull(true)
              .setDefaultValue(new ModelNode(false))
              .setFlags(AttributeAccess.Flag.RESTART_RESOURCE_SERVICES,
                  AttributeAccess.Flag.STORAGE_CONFIGURATION)
              .build();

  static final SimpleAttributeDefinition ALLOW_EMPTY_PROXY_CHAIN =
      new SimpleAttributeDefinitionBuilder(Names.ALLOW_EMPTY_PROXY_CHAIN,
          ModelType.BOOLEAN)
              .setAllowExpression(false)
              .setAllowNull(true)
              .setDefaultValue(new ModelNode(false))
              .setFlags(AttributeAccess.Flag.RESTART_RESOURCE_SERVICES,
                  AttributeAccess.Flag.STORAGE_CONFIGURATION)
              .build();

  static final SimpleAttributeDefinition ORIGINAL_REQUEST_PATH_HEADER =
      new SimpleAttributeDefinitionBuilder(Names.ORIGINAL_REQUEST_PATH_HEADER,
          ModelType.STRING)
          .setAllowExpression(true)
          .setAllowNull(true)
          .setFlags(AttributeAccess.Flag.RESTART_RESOURCE_SERVICES,
              AttributeAccess.Flag.STORAGE_CONFIGURATION)
          .build();

  static final SimpleAttributeDefinition RENEW =
      new SimpleAttributeDefinitionBuilder(Names.RENEW,
          ModelType.BOOLEAN)
              .setAllowExpression(false)
              .setAllowNull(true)
              .setDefaultValue(new ModelNode(false))
              .setFlags(AttributeAccess.Flag.RESTART_RESOURCE_SERVICES,
                  AttributeAccess.Flag.STORAGE_CONFIGURATION)
              .build();

  static final SimpleAttributeDefinition CLOCK_SKEW_TOLERANCE =
      new SimpleAttributeDefinitionBuilder(Names.CLOCK_SKEW_TOLERANCE,
          ModelType.LONG)
              .setAllowExpression(true)
              .setAllowNull(true)
              .setDefaultValue(new ModelNode(1000L))
              .setFlags(AttributeAccess.Flag.RESTART_RESOURCE_SERVICES,
                  AttributeAccess.Flag.STORAGE_CONFIGURATION)
              .build();

  static final SimpleAttributeDefinition POST_AUTH_REDIRECT =
      new SimpleAttributeDefinitionBuilder(Names.POST_AUTH_REDIRECT,
          ModelType.BOOLEAN)
              .setAllowExpression(true)
              .setAllowNull(true)
              .setDefaultValue(new ModelNode(true))
              .setFlags(AttributeAccess.Flag.RESTART_RESOURCE_SERVICES,
                  AttributeAccess.Flag.STORAGE_CONFIGURATION)
              .build();

  static final SimpleAttributeDefinition SECURITY_REALM =
	      new SimpleAttributeDefinitionBuilder(Names.SECURITY_REALM,
	          ModelType.STRING)
	              .setAllowExpression(true)
	              .setAllowNull(true)
	              .setDefaultValue(null)
	              .setFlags(AttributeAccess.Flag.RESTART_RESOURCE_SERVICES,
	                  AttributeAccess.Flag.STORAGE_CONFIGURATION)
	              .build();

  public static final ProfileDefinition INSTANCE =
      new ProfileDefinition();

  public static AttributeDefinition[] attributes() {
    return new AttributeDefinition[] {
      PROTOCOL,
      ENCODING,
      SERVER_URL,
      SERVICE_URL,
      PROXY_CALLBACK_ENABLED,
      PROXY_CALLBACK_PATH,
      ACCEPT_ANY_PROXY,
      ALLOW_EMPTY_PROXY_CHAIN,
      ORIGINAL_REQUEST_PATH_HEADER,
      RENEW,
      CLOCK_SKEW_TOLERANCE,
      POST_AUTH_REDIRECT,
      SECURITY_REALM
    };
  }

  private ProfileDefinition() {
    super(Paths.PROFILE,
        ResourceUtil.getResolver(Names.CAS_PROFILE),
        ProfileAdd.INSTANCE,
        ProfileRemove.INSTANCE);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void registerAttributes(
      ManagementResourceRegistration resourceRegistration) {
    for (AttributeDefinition attribute : attributes()) {
      resourceRegistration.registerReadWriteAttribute(attribute, null, 
          ProfileWriteAttributeHandler.INSTANCE);
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
    resourceRegistration.registerSubModel(ProxyChainDefinition.INSTANCE);
    resourceRegistration.registerSubModel(HostnameVerifierDefinition.INSTANCE);
    resourceRegistration.registerSubModel(AttributeTransformDefinition.INSTANCE);
  }

  static class ProfileAdd extends AbstractAddStepHandler {
    
    static final ProfileAdd INSTANCE = 
        new ProfileAdd();
    
    private ProfileAdd() {
      super(ProfileDefinition.attributes());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void performRuntime(OperationContext context,
        ModelNode operation, ModelNode model)
        throws OperationFailedException {

      ServiceController<?> controller = 
          ProfileService.ServiceUtil.installService(context, model, 
              PathAddress.pathAddress(
                  operation.get(ModelDescriptionConstants.OP_ADDR)));

      //,
      //        ServiceVerificationHandler verificationHandler,
      //        List<ServiceController<?>> newControllers
      //newControllers.add(
      //    (ServiceController<?>) controller);
    }

  }

  static class ProfileRemove extends AbstractRemoveStepHandler {

    static final ProfileRemove INSTANCE = new ProfileRemove();
      
    private ProfileRemove() {    
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void performRuntime(OperationContext context,
        ModelNode operation, ModelNode model) throws OperationFailedException {
      ProfileService.ServiceUtil.removeService(context, 
          PathAddress.pathAddress(
              operation.get(ModelDescriptionConstants.OP_ADDR)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void recoverServices(OperationContext context,
        ModelNode operation, ModelNode model) throws OperationFailedException {
      ProfileService.ServiceUtil.installService(context, model, 
          PathAddress.pathAddress(
                operation.get(ModelDescriptionConstants.OP_ADDR)));      
    }
  }
  
  static class ProfileWriteAttributeHandler 
      extends RestartParentWriteAttributeHandler {

    public static ProfileWriteAttributeHandler INSTANCE =
        new ProfileWriteAttributeHandler();
    
    /**
     * Constructs a new instance.
     */
    private ProfileWriteAttributeHandler() {
      super(Names.CAS_PROFILE, ProfileDefinition.attributes());
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

}
