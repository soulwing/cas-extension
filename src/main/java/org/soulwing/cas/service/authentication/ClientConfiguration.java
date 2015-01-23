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
package org.soulwing.cas.service.authentication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jasig.cas.client.validation.AbstractUrlBasedTicketValidator;
import org.jasig.cas.client.validation.Cas10TicketValidator;
import org.jasig.cas.client.validation.Cas20ProxyTicketValidator;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.jasig.cas.client.validation.Saml11TicketValidator;

/**
 * A {@link Configuration} for a CAS client.
 *
 * @author Carl Harris
 */
public class ClientConfiguration implements MutableConfiguration {

  private AuthenticationProtocol protocol;

  private String serverUrl;

  private String serviceUrl;

  private String proxyCallbackUrl;

  private boolean acceptAnyProxy;

  private boolean allowEmptyProxyChain;

  private boolean renew;

  private List<String[]> allowedProxyChains;
  
  /**
   * Gets the {@code protocol} property.
   * @return property value
   */
  @Override
  public AuthenticationProtocol getProtocol() {
    return protocol;
  }

  /**
   * Sets the {@code protocol} property.
   * @param protocol the value to set
   */
  @Override
  public void setProtocol(AuthenticationProtocol protocol) {
    this.protocol = protocol;
  }

  /**
   * Gets the {@code serverUrl} property.
   * @return property value
   */
  @Override
  public String getServerUrl() {
    return serverUrl;
  }

  /**
   * Sets the {@code serverUrl} property.
   * @param serverUrl the value to set
   */
  @Override
  public void setServerUrl(String serverUrl) {
    this.serverUrl = serverUrl;
  }

  /**
   * Gets the {@code serviceUrl} property.
   * @return property value
   */
  @Override
  public String getServiceUrl() {
    return serviceUrl;
  }

  /**
   * Sets the {@code serviceUrl} property.
   * @param serviceUrl the value to set
   */
  @Override
  public void setServiceUrl(String serviceUrl) {
    this.serviceUrl = serviceUrl;
  }

  /**
   * Gets the {@code proxyCallbackUrl} property.
   * @return property value
   */
  @Override
  public String getProxyCallbackUrl() {
    return proxyCallbackUrl;
  }

  /**
   * Sets the {@code proxyCallbackUrl} property.
   * @param proxyCallbackUrl the value to set
   */
  @Override
  public void setProxyCallbackUrl(String proxyCallbackUrl) {
    this.proxyCallbackUrl = proxyCallbackUrl;
  }

  /**
   * Gets the {@code acceptAnyProxy} property.
   * @return property value
   */
  @Override
  public boolean isAcceptAnyProxy() {
    return acceptAnyProxy;
  }

  /**
   * Sets the {@code acceptAnyProxy} property.
   * @param acceptAnyProxy the value to set
   */
  @Override
  public void setAcceptAnyProxy(boolean acceptAnyProxy) {
    this.acceptAnyProxy = acceptAnyProxy;
  }

  /**
   * Gets the {@code allowEmptyProxyChain} property.
   * @return property value
   */
  @Override
  public boolean isAllowEmptyProxyChain() {
    return allowEmptyProxyChain;
  }

  /**
   * Sets the {@code allowEmptyProxyChain} property.
   * @param allowEmptyProxyChain the value to set
   */
  @Override
  public void setAllowEmptyProxyChain(boolean allowEmptyProxyChain) {
    this.allowEmptyProxyChain = allowEmptyProxyChain;
  }

  /**
   * Gets the {@code allowedProxyChains} property.
   * @return property value
   */
  @Override
  public List<String[]> getAllowedProxyChains() {
    return allowedProxyChains;
  }

  /**
   * Sets the {@code allowedProxyChains} property.
   * @param allowedProxyChains the value to set
   */
  @Override
  public void setAllowedProxyChains(List<String[]> allowedProxyChains) {
    this.allowedProxyChains = Collections.unmodifiableList(allowedProxyChains);
  }

  /**
   * Gets the {@code renew} property.
   * @return property value
   */
  @Override
  public boolean isRenew() {
    return renew;
  }

  /**
   * Sets the {@code renew} property.
   * @param renew the value to set
   */
  @Override
  public void setRenew(boolean renew) {
    this.renew = renew;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public AuthenticationTicketValidator getValidator() {
    AbstractUrlBasedTicketValidator validator = newValidator();
    validator.setRenew(isRenew());
    return new JasigTicketValidator(validator);
  }

  private AbstractUrlBasedTicketValidator newValidator() {
    switch (protocol) {
      case CAS1_0:
        return new Cas10TicketValidator(getServerUrl());
      
      case CAS2_0:
        Cas20ServiceTicketValidator validator = null;
        if (isAcceptAnyProxy() || isAllowEmptyProxyChain() 
            || !getAllowedProxyChains().isEmpty()) {
          validator = new Cas20ProxyTicketValidator(getServerUrl());
        }
        else {
          validator = new Cas20ServiceTicketValidator(getServerUrl());
        }
        
        validator.setProxyCallbackUrl(getProxyCallbackUrl());
        return validator;
        
      case SAML1_1:
        return new Saml11TicketValidator(getServerUrl());
        
      default:
        throw new IllegalArgumentException("unrecognized protocol");
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public MutableConfiguration clone() {
    try {
      MutableConfiguration clone = (MutableConfiguration) super.clone();
      if (getAllowedProxyChains() != null) {
        List<String[]> allowedProxyChains = new ArrayList<>();
        allowedProxyChains.addAll(getAllowedProxyChains());
        clone.setAllowedProxyChains(allowedProxyChains);
      }
      return clone;
    }
    catch (CloneNotSupportedException ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return String.format("%s::protocol=%s"
        + " serverUrl=%s serviceUrl=%s proxyCallbackUrl=%s"
        + " acceptAnyProxy=%s allowEmptyProxyChain=%s renew=%s",
        getClass().getSimpleName(), 
        protocol, serverUrl, serviceUrl, proxyCallbackUrl, 
        acceptAnyProxy, allowEmptyProxyChain, renew);
  }
 
  
}
