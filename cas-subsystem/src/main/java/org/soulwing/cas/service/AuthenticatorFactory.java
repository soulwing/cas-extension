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
   * @return authenticator
   */
  public static Authenticator newInstance(Configuration config) {
    return new JasigAuthenticator(config, createTicketValidator(config));
  }
  
  private static TicketValidator createTicketValidator(Configuration config) {
    AbstractUrlBasedTicketValidator validator = newTicketValidator(config);
    validator.setEncoding(config.getEncoding());
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
