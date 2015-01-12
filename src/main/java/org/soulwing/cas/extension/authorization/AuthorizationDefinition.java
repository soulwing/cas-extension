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

import org.jboss.as.controller.SimpleResourceDefinition;
import org.jboss.as.controller.registry.ManagementResourceRegistration;
import org.soulwing.cas.extension.Names;
import org.soulwing.cas.extension.Paths;
import org.soulwing.cas.extension.ResourceUtil;

/**
 * 
 * A definition for the authorization resource.
 *
 * @author Carl Harris
 */
public class AuthorizationDefinition extends SimpleResourceDefinition {

  public static final AuthorizationDefinition INSTANCE =
      new AuthorizationDefinition();
  
  private AuthorizationDefinition() {
    super(Paths.AUTHORIZATION, 
        ResourceUtil.getResolver(Names.AUTHORIZATION),
        AuthorizationAdd.INSTANCE,
        AuthorizationRemove.INSTANCE);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void registerAttributes(
      ManagementResourceRegistration resourceRegistration) {
    super.registerAttributes(resourceRegistration);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void registerChildren(
      ManagementResourceRegistration resourceRegistration) {
    super.registerChildren(resourceRegistration);
    resourceRegistration.registerSubModel(LdapDefinition.INSTANCE);
    resourceRegistration.registerSubModel(PropertiesDefinition.INSTANCE);
  }
 
  
}
