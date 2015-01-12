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

import org.jboss.as.controller.SimpleAttributeDefinition;
import org.jboss.as.controller.SimpleAttributeDefinitionBuilder;
import org.jboss.as.controller.operations.validation.EnumValidator;
import org.jboss.as.controller.registry.AttributeAccess;
import org.jboss.as.controller.registry.ManagementResourceRegistration;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;
import org.soulwing.cas.extension.Names;
import org.soulwing.cas.extension.Paths;
import org.soulwing.cas.extension.ResourceUtil;

/**
 * 
 * A definition for the LDAP group search resource.
 *
 * @author Carl Harris
 */
public class GroupSearchDefinition extends LdapSearchDefinition {

  static final SimpleAttributeDefinition USER_MEMBER_TYPE =
      new SimpleAttributeDefinitionBuilder(Names.USER_MEMBER_TYPE, 
          ModelType.STRING)
              .setAllowExpression(false)
              .setAllowNull(true)
              .setDefaultValue(new ModelNode().set(GroupMemberType.simple.name()))
              .setValidator(new EnumValidator<>(GroupMemberType.class, true, false))
              .setXmlName(Names.USER_MEMBER_TYPE)
              .setFlags(AttributeAccess.Flag.RESTART_NONE,
                  AttributeAccess.Flag.STORAGE_CONFIGURATION)
              .build();

  static final SimpleAttributeDefinition USER_MEMBER_ATTRIBUTE =
      new SimpleAttributeDefinitionBuilder(Names.USER_MEMBER_ATTRIBUTE, 
          ModelType.STRING)
              .setAllowExpression(true)
              .setAllowNull(true)
              .setXmlName(Names.USER_MEMBER_ATTRIBUTE)
              .setFlags(AttributeAccess.Flag.RESTART_NONE,
                  AttributeAccess.Flag.STORAGE_CONFIGURATION)
              .build();

  static final SimpleAttributeDefinition GROUP_MEMBER_TYPE =
      new SimpleAttributeDefinitionBuilder(Names.GROUP_MEMBER_TYPE, 
          ModelType.STRING)
              .setAllowExpression(false)
              .setAllowNull(true)
              .setDefaultValue(new ModelNode().set(GroupMemberType.simple.name()))
              .setValidator(new EnumValidator<>(GroupMemberType.class, true, false))
              .setXmlName(Names.GROUP_MEMBER_TYPE)
              .setFlags(AttributeAccess.Flag.RESTART_NONE,
                  AttributeAccess.Flag.STORAGE_CONFIGURATION)
              .build();
  
  static final SimpleAttributeDefinition GROUP_MEMBER_ATTRIBUTE =
      new SimpleAttributeDefinitionBuilder(Names.GROUP_MEMBER_ATTRIBUTE, 
          ModelType.STRING)
              .setAllowExpression(true)
              .setAllowNull(true)
              .setXmlName(Names.GROUP_MEMBER_ATTRIBUTE)
              .setFlags(AttributeAccess.Flag.RESTART_NONE,
                  AttributeAccess.Flag.STORAGE_CONFIGURATION)
              .build();

  public static final GroupSearchDefinition INSTANCE =
      new GroupSearchDefinition();
  
  private GroupSearchDefinition() {
    super(Paths.GROUP_SEARCH, 
        ResourceUtil.getResolver(
            Names.AUTHORIZATION, Names.LDAP, Names.GROUP_SEARCH),
        GroupSearchAdd.INSTANCE,
        GroupSearchRemove.INSTANCE);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void registerAttributes(
      ManagementResourceRegistration resourceRegistration) {
    super.registerAttributes(resourceRegistration);
    resourceRegistration.registerReadWriteAttribute(USER_MEMBER_TYPE, null, 
        UserMemberTypeHandler.INSTANCE);
    resourceRegistration.registerReadWriteAttribute(USER_MEMBER_ATTRIBUTE, null, 
        UserMemberAttributeHandler.INSTANCE);
    resourceRegistration.registerReadWriteAttribute(GROUP_MEMBER_TYPE, null, 
        GroupMemberTypeHandler.INSTANCE);
    resourceRegistration.registerReadWriteAttribute(GROUP_MEMBER_ATTRIBUTE, null, 
        GroupMemberAttributeHandler.INSTANCE);
  }
    
}
