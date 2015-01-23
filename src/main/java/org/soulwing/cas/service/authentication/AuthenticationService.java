/*
 * File created on Dec 24, 2014 
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


/**
 * A service that authenticates CAS service tickets.
 *
 * @author Carl Harris
 */
public interface AuthenticationService {

  /**
   * Validates a service ticket.
   * @param ticket the subject ticket
   * @param service the target service
   * @return an assertion describing the validation result
   * @throws AuthenticationException if the ticket cannot be validated
   */
  IdentityAssertion validateTicket(String ticket, String service)
      throws AuthenticationException;

  /**
   * Produces the URL for the CAS server's login function.
   * @param requestUrl the URL of the request that requires authentication 
   *    for access
   * @return CAS server login URL
   */
  String loginUrl(String requestUrl);
  
  /**
   * Gets the current configuration of this service.
   * @return configuration
   */
  Configuration getConfiguration();
  
  /**
   * Replaces the current configuration of this service with the given
   * configuration.
   * @param configuration the new configuration for this service
   */
  void reconfigure(Configuration configuration);
  
}
