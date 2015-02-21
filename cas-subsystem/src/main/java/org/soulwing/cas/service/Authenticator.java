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
   * @param requestPath path from request URL
   * @param queryString query string from request URL
   * @return an assertion describing the validation result
   * @throws NoTicketEception if {@code queryString} does not contain a
   *    validation ticket
   * @throws AuthenticationException if the ticket cannot be validated
   */
  IdentityAssertion validateTicket(String requestPath, String queryString) 
      throws NoTicketException, AuthenticationException;

  /**
   * Gets a flag that indicates whether, after successful ticket validation,
   * a subsequent redirect should be sent for the requested application URL,
   * with all protocol-related parameters removed from the query string.
   * @return
   */
  boolean isPostAuthRedirect();

  /**
   * Gets the protocol-clean URL for a post-auth redirect.
   * <p>
   * This method removes any protocol-related parameters from the given
   * query string and returns the given request URL with the remaining
   * query string appended to it.
   * 
   * @param requestUrl request URL (scheme, authority, and path)
   * @param queryString subject query string which may contain protocol
   *    parameters
   * @return requestUrl with the remaining query appended to it
   */
  String postAuthUrl(String requestUrl, String queryString);
 
}
