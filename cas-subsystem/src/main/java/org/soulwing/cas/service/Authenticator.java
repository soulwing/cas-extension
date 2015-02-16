/*
 * File created on Feb 15, 2015 
 *
 * Copyright (c) 2014 Virginia Polytechnic Institute and State University
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
package org.soulwing.cas.service;

import org.soulwing.cas.api.IdentityAssertion;

/**
 * A object that provides access to the fundamental CAS mechanisms; login
 * URL for redirection and ticket validation.
 *
 * @author Carl Harris
 */
public interface Authenticator {

  /**
   * Gets the protocol that will be used by this authenticator.
   * @return authentication protocol
   */
  AuthenticationProtocol getProtocol();
  
  /**
   * Produces the URL for the CAS server's login function.
   * @param requestPath the path of the request that requires authentication 
   *    for access
   * @param queryString the query string of the request that requires 
   *    authentication
   * @return CAS server login URL
   */
  String loginUrl(String requestPath, String queryString);
  
  /**
   * Validates a service ticket.
   * @param ticket the subject ticket
   * @return an assertion describing the validation result
   * @throws AuthenticationException if the ticket cannot be validated
   */
  IdentityAssertion validateTicket(String requestPath, String queryString, 
      String ticket) throws AuthenticationException;

  /**
   * Gets a flag that indicates whether, after successful ticket validation,
   * a subsequent redirect should be sent for the requested application URL,
   * with all protocol-related parameters removed from the query string.
   * @return
   */
  boolean isPostAuthRedirect();
  
}
