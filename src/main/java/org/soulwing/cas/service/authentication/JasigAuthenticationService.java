/*
 * File created on Jan 22, 2015 
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

import java.util.concurrent.atomic.AtomicReference;

import org.jasig.cas.client.util.CommonUtils;

/**
 * An implementation of the {@link AuthenticationService} that delegates to
 * the JASIG CAS Client.
 *
 * @author Carl Harris
 */
class JasigAuthenticationService implements AuthenticationService {

  private final AtomicReference<Configuration> configuration =
      new AtomicReference<Configuration>(new ClientConfiguration());
  
  /**
   * {@inheritDoc}
   */
  @Override
  public IdentityAssertion validateTicket(String ticket, String service) 
      throws AuthenticationException {
    return getConfiguration().getValidator().validate(ticket, service);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String loginUrl(String requestUrl) {
    Configuration config = getConfiguration();
    
    // TODO -- need to replace the path in the service URL with the path of
    // the request URL
    String serviceUrl = requestUrl;
    
    return CommonUtils.constructRedirectUrl(config.getServerUrl(), 
        serviceParameterName(config), serviceUrl, config.isRenew(), 
        false);
  }

  private static String serviceParameterName(Configuration config) {
    if (config.getProtocol() == AuthenticationProtocol.SAML1_1) {
      return "TARGET";
    }
    return "service";
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public Configuration getConfiguration() {
    return configuration.get();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void reconfigure(Configuration configuration) {
    this.configuration.set(configuration);
  }

}
