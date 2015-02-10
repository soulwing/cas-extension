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

import org.jboss.as.controller.SimpleAttributeDefinition;
import org.jboss.as.controller.SimpleAttributeDefinitionBuilder;
import org.jboss.as.controller.SimpleResourceDefinition;
import org.jboss.as.controller.operations.validation.EnumValidator;
import org.jboss.as.controller.registry.AttributeAccess;
import org.jboss.as.controller.registry.ManagementResourceRegistration;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;
import org.soulwing.cas.service.AuthenticationProtocol;

/**
 * 
 * A definition for the configuration profile resource.
 *
 * @author Carl Harris
 */
public class ProfileDefinition extends SimpleResourceDefinition {

  public static final ProfileDefinition INSTANCE =
      new ProfileDefinition();
  
  static final SimpleAttributeDefinition PROTOCOL =
      new SimpleAttributeDefinitionBuilder(Names.PROTOCOL, 
          ModelType.STRING)
              .setAllowExpression(false)
              .setAllowNull(true)
              .setDefaultValue(new ModelNode().set(AuthenticationProtocol.CAS2_0.toString()))
              .setValidator(new EnumValidator<>(AuthenticationProtocol.class, true, false))
              .setFlags(AttributeAccess.Flag.RESTART_NONE,
                  AttributeAccess.Flag.STORAGE_CONFIGURATION)
              .build();

  static final SimpleAttributeDefinition SERVICE_URL =
      new SimpleAttributeDefinitionBuilder(Names.SERVICE_URL,
          ModelType.STRING)
              .setAllowExpression(true)
              .setAllowNull(false)
              .setFlags(AttributeAccess.Flag.RESTART_NONE,
                  AttributeAccess.Flag.STORAGE_CONFIGURATION)
              .build();

  static final SimpleAttributeDefinition SERVER_URL =
      new SimpleAttributeDefinitionBuilder(Names.SERVER_URL,
          ModelType.STRING)
              .setAllowExpression(true)
              .setAllowNull(false)
              .setFlags(AttributeAccess.Flag.RESTART_NONE,
                  AttributeAccess.Flag.STORAGE_CONFIGURATION)
              .build();

  static final SimpleAttributeDefinition PROXY_CALLBACK_URL =
      new SimpleAttributeDefinitionBuilder(Names.PROXY_CALLBACK_URL,
          ModelType.STRING)
              .setAllowExpression(true)
              .setAllowNull(true)
              .setFlags(AttributeAccess.Flag.RESTART_NONE,
                  AttributeAccess.Flag.STORAGE_CONFIGURATION)
              .build();

  static final SimpleAttributeDefinition ACCEPT_ANY_PROXY =
      new SimpleAttributeDefinitionBuilder(Names.ACCEPT_ANY_PROXY,
          ModelType.BOOLEAN)
              .setAllowExpression(false)
              .setAllowNull(true)
              .setDefaultValue(new ModelNode(false))
              .setFlags(AttributeAccess.Flag.RESTART_NONE,
                  AttributeAccess.Flag.STORAGE_CONFIGURATION)
              .build();

  static final SimpleAttributeDefinition ALLOW_EMPTY_PROXY_CHAIN =
      new SimpleAttributeDefinitionBuilder(Names.ALLOW_EMPTY_PROXY_CHAIN,
          ModelType.BOOLEAN)
              .setAllowExpression(false)
              .setAllowNull(true)
              .setDefaultValue(new ModelNode(false))
              .setFlags(AttributeAccess.Flag.RESTART_NONE,
                  AttributeAccess.Flag.STORAGE_CONFIGURATION)
              .build();

  static final SimpleAttributeDefinition RENEW =
      new SimpleAttributeDefinitionBuilder(Names.RENEW,
          ModelType.BOOLEAN)
              .setAllowExpression(false)
              .setAllowNull(true)
              .setDefaultValue(new ModelNode(false))
              .setFlags(AttributeAccess.Flag.RESTART_NONE,
                  AttributeAccess.Flag.STORAGE_CONFIGURATION)
              .build();

  static final SimpleAttributeDefinition CLOCK_SKEW_TOLERANCE =
      new SimpleAttributeDefinitionBuilder(Names.CLOCK_SKEW_TOLERANCE,
          ModelType.LONG)
              .setAllowExpression(true)
              .setAllowNull(true)
              .setDefaultValue(new ModelNode(1000L))
              .setFlags(AttributeAccess.Flag.RESTART_NONE,
                  AttributeAccess.Flag.STORAGE_CONFIGURATION)
              .build();

  static final SimpleAttributeDefinition POST_AUTH_REDIRECT =
      new SimpleAttributeDefinitionBuilder(Names.POST_AUTH_REDIRECT,
          ModelType.BOOLEAN)
              .setAllowExpression(true)
              .setAllowNull(true)
              .setDefaultValue(new ModelNode(true))
              .setFlags(AttributeAccess.Flag.RESTART_NONE,
                  AttributeAccess.Flag.STORAGE_CONFIGURATION)
              .build();

  private ProfileDefinition() {
    super(Paths.PROFILE, 
        ResourceUtil.getResolver(
            Names.PROFILE),
        ProfileAdd.INSTANCE,
        ProfileRemove.INSTANCE);
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public void registerAttributes(
      ManagementResourceRegistration resourceRegistration) {
    super.registerAttributes(resourceRegistration);
    resourceRegistration.registerReadWriteAttribute(PROTOCOL, null, 
        ProtocolHandler.INSTANCE);
    resourceRegistration.registerReadWriteAttribute(SERVICE_URL, null, 
        ServiceUrlHandler.INSTANCE);
    resourceRegistration.registerReadWriteAttribute(SERVER_URL, null, 
        ServerUrlHandler.INSTANCE);
    resourceRegistration.registerReadWriteAttribute(PROXY_CALLBACK_URL, null, 
        ProxyCallbackUrlHandler.INSTANCE);
    resourceRegistration.registerReadWriteAttribute(ACCEPT_ANY_PROXY, null, 
        AcceptAnyProxyHandler.INSTANCE);
    resourceRegistration.registerReadWriteAttribute(ALLOW_EMPTY_PROXY_CHAIN, null, 
        AllowEmptyProxyChainHandler.INSTANCE);
    resourceRegistration.registerReadWriteAttribute(RENEW, null, 
        RenewHandler.INSTANCE);
    resourceRegistration.registerReadWriteAttribute(CLOCK_SKEW_TOLERANCE, null, 
        ClockSkewToleranceHandler.INSTANCE);
    resourceRegistration.registerReadWriteAttribute(POST_AUTH_REDIRECT, null, 
        PostAuthRedirectHandler.INSTANCE);
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
 
}
