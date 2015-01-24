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
import org.jboss.as.controller.SimpleListAttributeDefinition;
import org.jboss.as.controller.SimpleResourceDefinition;
import org.jboss.as.controller.registry.AttributeAccess;
import org.jboss.as.controller.registry.ManagementResourceRegistration;
import org.jboss.dmr.ModelType;
import org.soulwing.cas.extension.Names;
import org.soulwing.cas.extension.Paths;
import org.soulwing.cas.extension.ResourceUtil;

/**
 * 
 * A definition for the SAML resource.
 *
 * @author Carl Harris
 */
public class SamlDefinition extends SimpleResourceDefinition {

  public static final SamlDefinition INSTANCE =
      new SamlDefinition();
  
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


  private SamlDefinition() {
    super(Paths.SAML, 
        ResourceUtil.getResolver(Names.AUTHORIZATION, Names.SAML),
        SamlAdd.INSTANCE,
        SamlRemove.INSTANCE);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void registerChildren(
      ManagementResourceRegistration resourceRegistration) {
    super.registerChildren(resourceRegistration);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void registerAttributes(
      ManagementResourceRegistration resourceRegistration) {
    super.registerAttributes(resourceRegistration);
    resourceRegistration.registerReadWriteAttribute(ROLE_ATTRIBUTES, 
        null, SamlRoleAttributeHandler.INSTANCE);
  }
    
}
