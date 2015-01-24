/*
 * File created on Jan 24, 2015 
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
package org.soulwing.cas.service.authorization;

/**
 * A configuration for an {@link AuthorizationService}.
 *
 * @author Carl Harris
 */
public interface AuthorizationConfig extends Cloneable {

  /**
   * Gets the role that is assigned to every user. 
   * @return role name
   */
  String getDefaultRole();
  
  /**
   * Sets the role that is assigned to all users.
   * @param defaultRole the role name to set
   */
  void setDefaultRole(String defaultRole);
  
  /**
   * Clones this configuration.
   * @return mutable copy of this configuration
   */
  AuthorizationConfig clone();
  
}
