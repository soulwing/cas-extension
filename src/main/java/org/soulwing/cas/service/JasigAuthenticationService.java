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
package org.soulwing.cas.service;

import static org.soulwing.cas.service.ServiceLogger.LOGGER;

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
  
  private final String name;
 
  /**
   * Constructs a new instance.
   * @param name
   */
  public JasigAuthenticationService(String name) {
    this.name = name;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return name;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IdentityAssertion validateTicket(String requestPath, 
      String queryString, String ticket) 
      throws AuthenticationException {
    
    Configuration config = getConfiguration();
    
    String serviceUrl = constructServiceUrl(requestPath, queryString, config);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("validating ticket '" + ticket + "' for service " + serviceUrl);
    }

    return config.getValidator().validate(ticket, 
        serviceUrl);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String loginUrl(String requestPath, String queryString) {
    
    Configuration config = getConfiguration();
    
    String serviceUrl = constructServiceUrl(requestPath, queryString, config);
    String loginUrl = CommonUtils.constructRedirectUrl(config.getServerUrl(),
        config.getProtocol().getServiceParameterName(),
        serviceUrl, config.isRenew(), false);
    
    return loginUrl;
  }

  private String constructServiceUrl(String requestPath, String queryString,
      Configuration config) {
    URI uri = URI.create(config.getServiceUrl());
    StringBuilder sb = new StringBuilder();
    sb.append(uri.getScheme());
    sb.append("://");
    sb.append(uri.getAuthority());
    sb.append(requestPath);
    
    int location = queryString.indexOf(
        config.getProtocol().getTicketParameterName() + "=");
    if (location > 0) {
      sb.append('?');
      sb.append(queryString.substring(0, location));
    }

    String serviceUrl = sb.toString();
    return serviceUrl;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public MutableConfiguration getConfiguration() {
    return configuration.get().clone();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void reconfigure(Configuration configuration) {
    LOGGER.info("configured service profile '" + 
        getName() + "' using " + configuration);
    this.configuration.set(configuration.clone());
  }

}
