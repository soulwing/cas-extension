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
import org.jboss.as.controller.AbstractRemoveStepHandler;
import org.jboss.as.controller.AttributeDefinition;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.ServiceVerificationHandler;
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
              .setAllowExpression(false)
              .setAllowNull(true)
              .setDefaultValue(new ModelNode().set(AuthenticationProtocol.CAS2_0.toString()))
              .setValidator(new EnumValidator<>(AuthenticationProtocol.class, true, false))
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

  static final SimpleAttributeDefinition PROXY_CALLBACK_URL =
      new SimpleAttributeDefinitionBuilder(Names.PROXY_CALLBACK_URL,
          ModelType.STRING)
              .setAllowExpression(true)
              .setAllowNull(true)
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
      SERVER_URL,
      SERVICE_URL,
      PROXY_CALLBACK_URL,
      ACCEPT_ANY_PROXY,
      ALLOW_EMPTY_PROXY_CHAIN,
      RENEW,
      CLOCK_SKEW_TOLERANCE,
      POST_AUTH_REDIRECT,
      SECURITY_REALM
    };
  }

  private ProfileDefinition() {
    super(Paths.PROFILE,
        ResourceUtil.getResolver(Names.PROFILE),
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
    resourceRegistration.unregisterAttribute(SECURITY_REALM.getName());
    resourceRegistration.registerReadWriteAttribute(SECURITY_REALM, null,
        SecurityRealmWriteAttributeHandler.INSTANCE);
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
  }

  private static List<ServiceController<?>> installServices(
      OperationContext context, ModelNode operation, ModelNode model) 
      throws OperationFailedException {
    
    List<ServiceController<?>> newControllers = new ArrayList<>();
    PathAddress profileAddress = PathAddress.pathAddress(
        operation.get(ModelDescriptionConstants.OP_ADDR));

    ServiceController<?> profileController = 
        ProfileService.ServiceUtil.installService(context, model, 
            profileAddress); 
    
    newControllers.add(profileController);
    
    ModelNode securityRealm = ProfileDefinition.SECURITY_REALM
        .resolveModelAttribute(context, model);
    
    ServiceController<?> sslContextController = 
        WrapperSSLContextService.ServiceUtil.installService(
            context, profileAddress, 
            securityRealm.isDefined() ? securityRealm.asString() : null);
    
    newControllers.add(sslContextController);

    return newControllers;
  }
  
  private static void removeServices(OperationContext context, 
      ModelNode operation, ModelNode model)  throws OperationFailedException {
    PathAddress profileAddress = PathAddress.pathAddress(
        operation.get(ModelDescriptionConstants.OP_ADDR));

    ProfileService.ServiceUtil.removeService(context, 
        profileAddress);
    
    WrapperSSLContextService.ServiceUtil.removeService(context, profileAddress);

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
        ModelNode operation, ModelNode model,
        ServiceVerificationHandler verificationHandler,
        List<ServiceController<?>> newControllers)
        throws OperationFailedException {

      newControllers.addAll(installServices(context, operation, model));
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
      removeServices(context, operation, model);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void recoverServices(OperationContext context,
        ModelNode operation, ModelNode model) throws OperationFailedException {
      installServices(context, operation, model);      
    }
  }
  
}
