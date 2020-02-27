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
package org.soulwing.cas.deployment;

import java.net.URI;

import org.jboss.msc.inject.Injector;
import org.jboss.msc.service.AbstractService;
import org.jboss.msc.value.InjectedValue;
import org.soulwing.cas.extension.Profile;
import org.soulwing.cas.service.AuthenticationService;
import org.soulwing.cas.service.Authenticator;
import org.soulwing.cas.service.AuthenticatorFactory;
import org.soulwing.cas.service.Configuration;
import org.soulwing.cas.service.ProxyCallbackHandler;
import org.soulwing.cas.service.ProxyCallbackHandlerFactory;
import org.soulwing.cas.service.ProxyCallbackResponse;

/**
 * An MSC service that provides an {@link AuthenticationService} to a
 * deployment.
 *
 * @author Carl Harris
 */
class DeploymentAuthenticationService
    extends AbstractService<AuthenticationService>
    implements AuthenticationService {

  private final ProxyCallbackHandler proxyCallbackHandler =
      ProxyCallbackHandlerFactory.newInstance();

  private final InjectedValue<Profile> profile =
      new InjectedValue<>();
    
  public Injector<Profile> getProfileInjector() {
    return profile;
  }
    
  /**
   * {@inheritDoc}
   */
  @Override
  public AuthenticationService getValue() throws IllegalStateException {
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Authenticator newAuthenticator(String contextPath) {
    return AuthenticatorFactory.newInstance(profile.getValue(),
        proxyCallbackUrl(contextPath), proxyCallbackHandler);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isProxyCallbackPath(String path) {
    return profile.getValue().getProxyCallbackPath().equals(path);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ProxyCallbackResponse handleProxyCallback(String query) {
    return proxyCallbackHandler.onProxyCallback(query);
  }

  private String proxyCallbackUrl(String contextPath) {
    Configuration config = profile.getValue();
    if (config.getProxyCallbackPath() == null) return null;
    URI uri = URI.create(config.getServiceUrl());
    StringBuilder sb = new StringBuilder();
    sb.append(uri.getScheme());
    sb.append("://");
    sb.append(uri.getAuthority());
    sb.append(contextPath);
    String proxyCallbackPath = config.getProxyCallbackPath();
    if (!proxyCallbackPath.startsWith("/") && !contextPath.endsWith("/")) {
      sb.append("/");
    }
    sb.append(proxyCallbackPath);
    return sb.toString();
  }

  public Configuration getConfiguration() {
    return profile.getValue();
  }
}
