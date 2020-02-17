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
package org.soulwing.cas.service;


/**
 * A service that provides CAS authentication.
 *
 * @author Carl Harris
 */
public interface AuthenticationService {

  /**
   * Creates a new authenticator instance.
   * @param contextPath context path of the requesting application
   * @return authenticator
   */
  Authenticator newAuthenticator(String contextPath);
  
  /**
   * Determines whether a given path matches the proxy callback path.
   * @param path the path to test
   * @return {@code true} if the request is a proxy callback
   */
  boolean isProxyCallbackPath(String path);
  
  /**
   * Handles a proxy granting ticket callback from the CAS server.
   * @param query the query string from the callback request
   * @return proxy callback response
   */
  ProxyCallbackResponse handleProxyCallback(String query);

  /**
   * Returns the configuration for this authentication service.
   * @return configuration instance
   */
  Configuration getConfiguration();
}
