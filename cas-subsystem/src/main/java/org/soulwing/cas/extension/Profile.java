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
package org.soulwing.cas.extension;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.jboss.msc.inject.Injector;
import org.jboss.msc.value.InjectedValue;
import org.soulwing.cas.service.AuthenticationProtocol;
import org.soulwing.cas.service.Configuration;

/**
 * A {@link Configuration} for a CAS client.
 *
 * @author Carl Harris
 */
public class Profile implements Configuration {

  private final InjectedValue<SSLContext> sslContext =
      new InjectedValue<>();
  
  private String securityRealm;
  
  private HostnameVerifier hostnameVerifier;
  
  private AuthenticationProtocol protocol;
  
  private String encoding;

  private String serverUrl;

  private String serviceUrl;

  private String proxyCallbackUrl;

  private boolean acceptAnyProxy;

  private boolean allowEmptyProxyChain;

  private boolean renew;
  
  private long clockSkewTolerance;
  
  private boolean postAuthRedirect;
  
  private Map<String, List<String>> allowedProxyChains = new LinkedHashMap<>();
  
  /**
   * Gets the {@code sslContext} property.
   * @return property value
   */
  public Injector<SSLContext> getSslContextInjector() {
    return sslContext;
  }

  /**
   * Gets the SSL context.
   * @return SSL context
   */
  public SSLContext getSslContext() {
    if (getSecurityRealm() == null) return null;
    return sslContext.getValue();
  }
  
  /**
   * Gets the {@code securityRealm} property.
   * @return property value
   */
  public String getSecurityRealm() {
    return securityRealm;
  }

  /**
   * Sets the {@code securityRealm} property.
   * @param securityRealm the value to set
   */
  public void setSecurityRealm(String securityRealm) {
    this.securityRealm = securityRealm;
  }

  /**
   * Gets the {@code hostnameVerifier} property.
   * @return property value
   */
  public HostnameVerifier getHostnameVerifier() {
    return hostnameVerifier;
  }

  /**
   * Sets the {@code hostnameVerifier} property.
   * @param hostnameVerifier the value to set
   */
  public void setHostnameVerifier(HostnameVerifier hostnameVerifier) {
    this.hostnameVerifier = hostnameVerifier;
  }

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
  public void setProtocol(AuthenticationProtocol protocol) {
    this.protocol = protocol;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getEncoding() {
    return encoding;
  }

  /**
   * Sets the {@code encoding} property.
   * @param encoding the value to set
   */
  public void setEncoding(String encoding) {
    this.encoding = encoding;
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
  public void setAllowEmptyProxyChain(boolean allowEmptyProxyChain) {
    this.allowEmptyProxyChain = allowEmptyProxyChain;
  }

  /**
   * Gets the {@code allowedProxyChains} property.
   * @return property value
   */
  @Override
  public List<String[]> getAllowedProxyChains() {
    List<String[]> chains = new ArrayList<>();
    for (List<String> chain : allowedProxyChains.values()) {
      chains.add(chain.toArray(new String[chain.size()]));
    }
    return chains;
  }

  /**
   * {@inheritDoc}
   */
  public void putAllowedProxyChain(String name, List<String> chain) {
    allowedProxyChains.put(name, chain);
  }

  /**
   * {@inheritDoc}
   */
  public void removeAllowedProxyChain(String name) {
    allowedProxyChains.remove(name);
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
  public void setRenew(boolean renew) {
    this.renew = renew;
  }

  
  /**
   * {@inheritDoc}
   */
  @Override
  public long getClockSkewTolerance() {
    return clockSkewTolerance;
  }

  /**
   * {@inheritDoc}
   */
  public void setClockSkewTolerance(long clockSkewTolerance) {
    this.clockSkewTolerance = clockSkewTolerance;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isPostAuthRedirect() {
    return postAuthRedirect;
  }

  /**
   * {@inheritDoc}
   */
  public void setPostAuthRedirect(boolean postAuthRedirect) {
    this.postAuthRedirect = postAuthRedirect;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return String.format("{ protocol=%s encoding=%s"
        + " serverUrl=%s serviceUrl=%s proxyCallbackUrl=%s"
        + " acceptAnyProxy=%s allowEmptyProxyChain=%s allowedProxyChains=%s"
        + " renew=%s clockSkewTolerance=%d postAuthRedirect=%s"
        + " securityRealm=%s hostnameVerifier=%s }",
        protocol, encoding, serverUrl, serviceUrl, proxyCallbackUrl, 
        acceptAnyProxy, allowEmptyProxyChain, allowedProxyChains,         
        renew, clockSkewTolerance, postAuthRedirect,
        securityRealm != null ? securityRealm : "(none)", 
        hostnameVerifier != null ? 
            hostnameVerifier.getClass().getSimpleName() : "(none)");
  }

}
