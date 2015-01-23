/*
 * File created on Jan 23, 2015 
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
package org.soulwing.cas.service.authentication;

import java.util.Date;
import java.util.Map;

/**
 * An assertion about the identity of a user derived from the validation
 * of a CAS ticket.
 *
 * @author Carl Harris
 */
public interface IdentityAssertion {

  /**
   * Gets a principal that describes the user whose identity is the subject 
   * of this assertion.
   * @return user principal
   */
  UserPrincipal getPrincipal();

  /**
   * Tests whether this assertion remains valid.
   * @return {@code true} if this assertion is valid
   */
  boolean isValid();
  
  /**
   * Gets the timestamp at which the validity of this assertion begins. 
   * @return timestamp
   */
  Date getValidFromDate();

  /**
   * Gets the timestamp at which the validity of this assertion ends. 
   * @return timestamp
   */
  Date getValidUntilDate();

  /**
   * Gets the timestamp at which authentication occurred.
   * @return timestamp
   */
  Date getAuthenticationDate();

  /**
   * Gets the map of attributes name-value pairs associated with the principal
   * @return attribute map
   */
  Map<String, Object> getAttributes();

}
