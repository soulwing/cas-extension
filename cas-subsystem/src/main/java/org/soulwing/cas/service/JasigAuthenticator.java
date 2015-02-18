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

import static org.soulwing.cas.service.ServiceLogger.LOGGER;

import java.net.URI;

import org.jasig.cas.client.util.CommonUtils;
import org.jasig.cas.client.validation.TicketValidationException;
import org.jasig.cas.client.validation.TicketValidator;
import org.soulwing.cas.api.IdentityAssertion;

/**
 * An {@link Authenticator} that delegates to the JASIG CAS client library.
 *
 * @author Carl Harris
 */
public class JasigAuthenticator implements Authenticator {

  private final Configuration config;
  private final TicketValidator validator;
  
  /**
   * Constructs a new instance.
   * @param config
   */
  public JasigAuthenticator(Configuration config, TicketValidator validator) {
    this.config = config;
    this.validator = validator;
  }
   
  /**
   * Gets the {@code validator} property.
   * @return property value
   */
  TicketValidator getValidator() {
    return validator;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isPostAuthRedirect() {
    return config.isPostAuthRedirect();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String loginUrl(String requestPath, String queryString) {
    String serviceUrl = serviceUrl(requestPath, queryString);
    String loginUrl = CommonUtils.constructRedirectUrl(config.getServerUrl(),
        config.getProtocol().getServiceParameterName(),
        serviceUrl, config.isRenew(), false);
    
    return loginUrl;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IdentityAssertion validateTicket(String requestPath,
      String queryString) 
      throws NoTicketException, AuthenticationException {
    
    String ticket = QueryUtil.findParameter(
        config.getProtocol().getTicketParameterName(), queryString);
    if (ticket == null) {
      throw new NoTicketException();
    }

    ticket = CommonUtils.urlEncode(ticket);
    
    String serviceUrl = serviceUrl(requestPath, queryString);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("validating ticket '" + ticket + "' for service " + serviceUrl);
    }

    try {
      return new JasigIdentityAssertion(validator.validate(ticket, serviceUrl));
    }
    catch (TicketValidationException ex) {
      throw new AuthenticationException(ex.getMessage(), ex);
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String postAuthUrl(String requestUrl, String queryString) {
    StringBuilder sb = new StringBuilder();
    sb.append(requestUrl);
    queryString = QueryUtil.removeProtocolParameters(config.getProtocol(), 
        queryString);
    if (!queryString.isEmpty()) {
      sb.append('?');
      sb.append(queryString);
    }
    return sb.toString();
  }

  String serviceUrl(String requestPath, String queryString) {
    URI uri = URI.create(config.getServiceUrl());
    StringBuilder sb = new StringBuilder();
    sb.append(uri.getScheme());
    sb.append("://");
    sb.append(uri.getAuthority());
    sb.append(requestPath);

    queryString = QueryUtil.removeProtocolParameters(config.getProtocol(), 
        queryString);
    
    if (!queryString.isEmpty()) {
      sb.append('?');
      sb.append(queryString);
    }
  
    return sb.toString();
  }

}
