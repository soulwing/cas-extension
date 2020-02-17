/*
 * File created on Feb 21, 2015 
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
package org.soulwing.cas.undertow;

import org.jboss.msc.inject.Injector;
import org.jboss.msc.value.InjectedValue;
import org.soulwing.cas.service.AuthenticationService;
import org.soulwing.cas.service.Authenticator;
import org.soulwing.cas.service.Configuration;
import org.soulwing.cas.service.ProxyCallbackResponse;

/**
 * An {@link AuthenticationService} that delegates to an injected service
 * for a deployment and is shared with all of the Undertow components created
 * by the servlet extension.
 *
 * @author Carl Harris
 */
class CasAuthenticationService implements AuthenticationService {

  private final InjectedValue<AuthenticationService> delegate =
      new InjectedValue<>();

  /**
   * Gets the delegate authentication service injector.
   * @return
   */
  public Injector<AuthenticationService> getServiceInjector() {
    return delegate;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Authenticator newAuthenticator(String contextPath) {
    return delegate.getValue().newAuthenticator(contextPath);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isProxyCallbackPath(String path) {
    return delegate.getValue().isProxyCallbackPath(path);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ProxyCallbackResponse handleProxyCallback(String query) {
    return delegate.getValue().handleProxyCallback(query);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Configuration getConfiguration() {
    return delegate.getValue().getConfiguration();
  }
}
