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
package org.soulwing.cas.api;

import java.io.Serializable;
import java.security.Principal;
import java.util.Map;

/**
 * A {@link Principal} with additional CAS-specific API.
 *
 * @author Carl Harris
 */
public interface UserPrincipal extends Principal, Serializable {

  /**
   * Gets the map of attribute name-value pairs that further describe the
   * user.
   * @return attribute map
   */
  Map<String, Object> getAttributes();
  
  /**
   * Generates a proxy ticket for use in accessing another service.
   * @param service name (URL) of the service to access
   * @return ticket that may be used to authenticate access by this user
   *    to the given {@code service}
   * @throws IllegalStateException if no proxy granting ticket is available.
   */
  String generateProxyTicket(String service) throws IllegalStateException;

  /**
   * Gets the CAS logout URL.
   * @return URL to which the user can be redirect for CAS logout
   */
  String getLogoutUrl();

  /**
   * Gets the CAS logout URL that includes a reference back to an application
   * path that should be offered to the user as a post-logout action.
   * @param path application path (including the context path)
   * @return URL to which the user can be redirect for CAS logout
   */
  String generateLogoutUrl(String path);

  /**
   * Gets the underlying delegate principal.
   * <p>
   * The returned object is an instance of a class defined by the CAS client
   * implementation; e.g. for the JASIG CAS Client the returned object is
   * an instance of {@link org.jasig.cas.client.Authentication.AttributePrincipal}
   * @return delegate principal
   */
  Object getDelegate();
  
  
}
