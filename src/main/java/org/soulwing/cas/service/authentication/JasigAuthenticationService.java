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

import java.net.URI;
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
  public IdentityAssertion validateTicket(String ticket) 
      throws AuthenticationException {
    Configuration config = getConfiguration();
    return config.getValidator().validate(ticket, config.getServiceUrl());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String loginUrl(String requestPath, String queryString) {
    Configuration config = getConfiguration();
    String serviceUrl = constructServiceUrl(requestPath, queryString, config);
    return CommonUtils.constructRedirectUrl(config.getServerUrl(),
        config.getProtocol().getServiceParameterName(),
        serviceUrl, config.isRenew(), false);
  }

  private String constructServiceUrl(String requestPath, String queryString,
      Configuration config) {
    URI uri = URI.create(config.getServiceUrl());
    StringBuilder sb = new StringBuilder();
    sb.append(uri.getScheme());
    sb.append(':');
    sb.append(uri.getAuthority());
    sb.append(requestPath);
    sb.append('?');
    sb.append(queryString);

    String serviceUrl = sb.toString();
    return serviceUrl;
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
