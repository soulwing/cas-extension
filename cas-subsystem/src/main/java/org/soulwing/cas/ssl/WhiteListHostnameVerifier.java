/*
 * File created on Feb 16, 2015 
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
package org.soulwing.cas.ssl;

import java.util.Arrays;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * A {@link HostnameVerifier} that allows any host on a given white list.
 *
 * @author Carl Harris
 */
class WhiteListHostnameVerifier implements HostnameVerifier {

  private final String[] allowedHosts;
  
  /**
   * Constructs a new instance.
   * @param allowedHosts
   */
  public WhiteListHostnameVerifier(String[] allowedHosts) {
    this.allowedHosts = allowedHosts;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean verify(String hostname, SSLSession session) {
    for (String allowedHost : allowedHosts) {
      if (hostname.equalsIgnoreCase(allowedHost)) {
        return true;
      }
    }
    return false;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return String.format("%s(hosts=%s)", 
        HostnameVerifierType.WHITE_LIST, Arrays.asList(allowedHosts));
  }

}
