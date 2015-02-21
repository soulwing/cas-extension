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

import org.jasig.cas.client.proxy.Cas20ProxyRetriever;
import org.jasig.cas.client.proxy.ProxyGrantingTicketStorage;
import org.jasig.cas.client.ssl.HttpURLConnectionFactory;
import org.jasig.cas.client.validation.AbstractUrlBasedTicketValidator;
import org.jasig.cas.client.validation.Cas10TicketValidator;
import org.jasig.cas.client.validation.Cas20ProxyTicketValidator;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.jasig.cas.client.validation.ProxyList;
import org.jasig.cas.client.validation.Saml11TicketValidator;
import org.jasig.cas.client.validation.TicketValidator;
import org.soulwing.cas.ssl.HttpsURLConnectionFactory;


/**
 * A factory that produces {@link Authenticator} objects.
 *
 * @author Carl Harris
 */
public class AuthenticatorFactory {

  /**
   * Constructs a new authenticator.
   * @param config protocol configuration 
   * @param proxyCallbackUrl proxy callback URL
   * @param proxyCallbackHandler proxy ticket handler
   * @return authenticator
   */
  public static Authenticator newInstance(Configuration config,
      String proxyCallbackUrl, ProxyCallbackHandler proxyCallbackHandler) {
    
    HttpsURLConnectionFactory connectionFactory = 
        new HttpsURLConnectionFactory(config.getSslContext(), 
            config.getHostnameVerifier());
    
    TicketValidator validator = createTicketValidator(config, 
        connectionFactory);

    if (validator instanceof Cas20ServiceTicketValidator) {
      configureServiceValidator(config, proxyCallbackUrl, proxyCallbackHandler,
          connectionFactory, (Cas20ServiceTicketValidator) validator);
    }
    if (validator instanceof Cas20ProxyTicketValidator) {
      configureProxyValidator(config, (Cas20ProxyTicketValidator)
          validator);
    }
    if (validator instanceof Saml11TicketValidator) {
      configureSamlValidator(config, (Saml11TicketValidator) validator);
    }
    return new JasigAuthenticator(config, validator);
  }
  
  private static TicketValidator createTicketValidator(Configuration config, HttpURLConnectionFactory connectionFactory) {
    AbstractUrlBasedTicketValidator validator = newTicketValidator(config);
    validator.setEncoding(config.getEncoding());
    validator.setRenew(config.isRenew());
    validator.setURLConnectionFactory(connectionFactory);
    
    return validator;
  }

  private static void configureServiceValidator(Configuration config,
      String proxyCallbackUrl, ProxyCallbackHandler proxyCallbackHandler,
      HttpURLConnectionFactory connectionFactory, 
      Cas20ServiceTicketValidator validator) {
    if (proxyCallbackUrl != null) {
      validator.setProxyCallbackUrl(proxyCallbackUrl);
      validator.setProxyGrantingTicketStorage(
          (ProxyGrantingTicketStorage) proxyCallbackHandler.getStorage());
      validator.setProxyRetriever(new Cas20ProxyRetriever(
          config.getServerUrl(), config.getEncoding(), 
          connectionFactory));
    }
  }
  
  private static void configureSamlValidator(Configuration config,
      Saml11TicketValidator validator) {
    validator.setTolerance(config.getClockSkewTolerance());
  }

  private static void configureProxyValidator(Configuration config,
      Cas20ProxyTicketValidator validator) {
    validator.setAcceptAnyProxy(config.isAcceptAnyProxy());
    validator.setAllowEmptyProxyChain(config.isAllowEmptyProxyChain());
    validator.setAllowedProxyChains(
        new ProxyList(config.getAllowedProxyChains()));
  }
  
  private static AbstractUrlBasedTicketValidator newTicketValidator(
      Configuration config) {
    switch (config.getProtocol()) {
      case CAS1_0:
        return new Cas10TicketValidator(config.getServerUrl());
      
      case CAS2_0:
        if (config.isAcceptAnyProxy() 
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

}
