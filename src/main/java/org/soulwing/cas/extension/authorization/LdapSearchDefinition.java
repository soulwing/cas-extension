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

import org.jboss.as.controller.OperationStepHandler;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.SimpleAttributeDefinition;
import org.jboss.as.controller.SimpleAttributeDefinitionBuilder;
import org.jboss.as.controller.SimpleListAttributeDefinition;
import org.jboss.as.controller.SimpleResourceDefinition;
import org.jboss.as.controller.descriptions.ResourceDescriptionResolver;
import org.jboss.as.controller.operations.validation.EnumValidator;
import org.jboss.as.controller.registry.AttributeAccess;
import org.jboss.as.controller.registry.ManagementResourceRegistration;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;
import org.soulwing.cas.extension.Names;

/**
 * 
 * A definition for the authentication resource.
 *
 * @author Carl Harris
 */
public abstract class LdapSearchDefinition extends SimpleResourceDefinition {

  static final SimpleAttributeDefinition BASE =
      new SimpleAttributeDefinitionBuilder(Names.BASE, ModelType.STRING)
              .setAllowExpression(true)
              .setAllowNull(false)
              .setFlags(AttributeAccess.Flag.RESTART_NONE,
                  AttributeAccess.Flag.STORAGE_CONFIGURATION)
              .setXmlName(Names.BASE)
              .build();

  static final SimpleAttributeDefinition FILTER =
      new SimpleAttributeDefinitionBuilder(Names.FILTER, ModelType.STRING)
              .setAllowExpression(true)
              .setAllowNull(true)
              .setFlags(AttributeAccess.Flag.RESTART_NONE,
                  AttributeAccess.Flag.STORAGE_CONFIGURATION)
              .build();

  static final SimpleAttributeDefinition SCOPE =
      new SimpleAttributeDefinitionBuilder(Names.SCOPE, ModelType.STRING)
              .setAllowExpression(false)
              .setAllowNull(true)
              .setDefaultValue(new ModelNode().set(LdapScope.subtree.name()))
              .setValidator(new EnumValidator<>(LdapScope.class, true, false))
              .setFlags(AttributeAccess.Flag.RESTART_NONE,
                  AttributeAccess.Flag.STORAGE_CONFIGURATION)
              .build();

  static final SimpleAttributeDefinition ROLE_ATTRIBUTE =
      new SimpleAttributeDefinitionBuilder(Names.ROLE_ATTRIBUTE, 
          ModelType.STRING)
              .setAllowExpression(true)
              .setAllowNull(false)
              .build();
              
  static final SimpleListAttributeDefinition ROLE_ATTRIBUTES =
      SimpleListAttributeDefinition.Builder.of(Names.ROLE_ATTRIBUTES, 
          ROLE_ATTRIBUTE)
          .setAllowNull(false)
          .setAllowExpression(false)
          .setFlags(AttributeAccess.Flag.RESTART_NONE,
              AttributeAccess.Flag.STORAGE_CONFIGURATION)
          .build();

  
  /**
   * Constructs a new instance.
   * @param pathElement
   * @param descriptionResolver
   * @param addHandler
   * @param removeHandler
   */
  public LdapSearchDefinition(PathElement pathElement,
      ResourceDescriptionResolver descriptionResolver,
      OperationStepHandler addHandler, OperationStepHandler removeHandler) {
    super(pathElement, descriptionResolver, addHandler, removeHandler);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void registerAttributes(
      ManagementResourceRegistration resourceRegistration) {
    super.registerAttributes(resourceRegistration);
    resourceRegistration.registerReadWriteAttribute(BASE, 
        null, LdapBaseHandler.INSTANCE);
    resourceRegistration.registerReadWriteAttribute(FILTER, 
        null, LdapFilterHandler.INSTANCE);
    resourceRegistration.registerReadWriteAttribute(SCOPE, 
        null, LdapScopeHandler.INSTANCE);
    resourceRegistration.registerReadWriteAttribute(ROLE_ATTRIBUTES, 
        null, LdapRoleAttributeHandler.INSTANCE);
  }
    
}
