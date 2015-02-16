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

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

/**
 * A factory that produces {@link Authenticator} objects.
 *
 * @author Carl Harris
 */
public class AuthenticatorFactory {

  /**
   * Constructs a new authenticator.
   * @param config protocol configuration 
   * @param sslContext SSL context or {@code null} to indicate that the 
   *    default SSL context should be used
   * @param hostnameVerifier hostname verifier or {@code null} to indicate
   *    that the default hostname verifier should be used
   * @return authenticator
   */
  public static Authenticator newInstance(Configuration config, 
      SSLContext sslContext, HostnameVerifier hostnameVerifier) {
    return new JasigAuthenticator(config, sslContext, hostnameVerifier);
  }
  
}
