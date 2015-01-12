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

import org.jboss.as.controller.registry.ManagementResourceRegistration;
import org.soulwing.cas.extension.Names;
import org.soulwing.cas.extension.Paths;
import org.soulwing.cas.extension.ResourceUtil;

/**
 * 
 * A definition for the LDAP user search resource.
 *
 * @author Carl Harris
 */
public class UserSearchDefinition extends LdapSearchDefinition {

  public static final UserSearchDefinition INSTANCE =
      new UserSearchDefinition();
  
  private UserSearchDefinition() {
    super(Paths.USER_SEARCH, 
        ResourceUtil.getResolver(
            Names.AUTHORIZATION, Names.LDAP, Names.USER_SEARCH),
        UserSearchAdd.INSTANCE,
        UserSearchRemove.INSTANCE);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void registerAttributes(
      ManagementResourceRegistration resourceRegistration) {
    super.registerAttributes(resourceRegistration);
  }
    
}
