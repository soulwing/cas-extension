/*
 * File created on Dec 22, 2014 
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

import org.soulwing.cas.service.authentication.IdentityAssertion;

/**
 * A service that supports authorization by using various resources/strategies 
 * to produce a set of roles that apply to a user. 
 *
 * @author Carl Harris
 */
public interface AuthorizationService {

  /**
   * Gets the name of this authorization service.
   * @return unqualified service name
   */
  String getName();
  
  /**
   * Gets the set of roles that are applicable for a given user.
   * @param assertion an identity assertion for the subject user
   * @return set of role names
   */
  Set<String> getApplicableRoles(IdentityAssertion assertion);
  
  /**
   * Gets the configuration of this service.
   * @return configuration
   */
  AuthorizationConfig getConfiguration();

  /**
   * Reconfigures this service.
   * @param configuration the configuration to apply
   */
  void reconfigure(AuthorizationConfig configuration);

  /**
   * Adds a strategy to this service.
   * @param name name of the strategy
   * @param strategy strategy implementation
   */
  void putStrategy(String name, AuthorizationStrategy<?> strategy);
  
  /**
   * Removes a strategy from this service.
   * @param name name of the strategy to remove
   */
  void removeStrategy(String name);
  
}
