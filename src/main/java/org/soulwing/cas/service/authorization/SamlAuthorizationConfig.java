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

import java.util.Set;

/**
 * A configuration for SAML-assertion-based authorization.
 *
 * @author Carl Harris
 */
public interface SamlAuthorizationConfig extends Cloneable {

  /**
   * Gets the set of role attributes.
   * @return attribute names
   */
  Set<String> getRoleAttributes();
 
  /**
   * Sets the collection of role attribute names.
   * @param roleAttributes set of attribute names
   */
  void setRoleAttributes(Set<String> roleAttributes);
  
  /**
   * Clones this configuration.
   * @return clone of this configuration
   */
  SamlAuthorizationConfig clone();
  
}
