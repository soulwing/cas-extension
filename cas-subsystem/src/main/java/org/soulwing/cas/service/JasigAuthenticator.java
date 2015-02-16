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
import org.jasig.cas.client.validation.AbstractUrlBasedTicketValidator;
import org.jasig.cas.client.validation.Cas10TicketValidator;
import org.jasig.cas.client.validation.Cas20ProxyTicketValidator;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.jasig.cas.client.validation.ProxyList;
import org.jasig.cas.client.validation.Saml11TicketValidator;
import org.jasig.cas.client.validation.TicketValidationException;
import org.jasig.cas.client.validation.TicketValidator;
import org.soulwing.cas.api.IdentityAssertion;
import org.soulwing.cas.ssl.HttpsURLConnectionFactory;

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
  public JasigAuthenticator(Configuration config) {
    this.config = config;
    this.validator = createTicketValidator(config);
  }
  
  private static TicketValidator createTicketValidator(Configuration config) {
    AbstractUrlBasedTicketValidator validator = newTicketValidator(config);
    validator.setRenew(config.isRenew());
    validator.setURLConnectionFactory(new HttpsURLConnectionFactory(
        config.getSslContext(), config.getHostnameVerifier()));
    if (validator instanceof Cas20ProxyTicketValidator) {
      ((Cas20ProxyTicketValidator) validator).setAcceptAnyProxy(
          config.isAcceptAnyProxy());
      ((Cas20ProxyTicketValidator) validator).setAllowEmptyProxyChain(
          config.isAllowEmptyProxyChain());
      ((Cas20ProxyTicketValidator) validator).setAllowedProxyChains(
          new ProxyList(config.getAllowedProxyChains()));
    }
    if (validator instanceof Saml11TicketValidator) {
      ((Saml11TicketValidator) validator).setTolerance(
          config.getClockSkewTolerance());
    }
    
    return validator;
  }
  
  private static AbstractUrlBasedTicketValidator newTicketValidator(
      Configuration config) {
    switch (config.getProtocol()) {
      case CAS1_0:
        return new Cas10TicketValidator(config.getServerUrl());
      
      case CAS2_0:
        if (config.isAcceptAnyProxy() || config.isAllowEmptyProxyChain() 
            || !config.getAllowedProxyChains().isEmpty()) {
          return new Cas20ProxyTicketValidator(config.getServerUrl());
        }
        return new Cas20ServiceTicketValidator(config.getServerUrl());
        
      case SAML1_1:
        return new Saml11TicketValidator(config.getServerUrl());
        
      default:
        throw new IllegalArgumentException("unrecognized protocol");
    }    
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public AuthenticationProtocol getProtocol() {
    return config.getProtocol();
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
        getProtocol().getServiceParameterName(),
        serviceUrl, config.isRenew(), false);
    
    return loginUrl;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IdentityAssertion validateTicket(String requestPath,
      String queryString, String ticket) throws AuthenticationException {
    
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

  private String serviceUrl(String requestPath, String queryString) {
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
  
    return sb.toString();
  }

}
