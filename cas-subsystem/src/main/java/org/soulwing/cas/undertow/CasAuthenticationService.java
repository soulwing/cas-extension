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
package org.soulwing.cas.undertow;

import javax.net.ssl.SSLContext;

import org.jboss.msc.inject.Injector;
import org.jboss.msc.service.AbstractService;
import org.jboss.msc.value.InjectedValue;
import org.soulwing.cas.extension.Profile;
import org.soulwing.cas.service.AuthenticationService;
import org.soulwing.cas.service.Authenticator;
import org.soulwing.cas.service.AuthenticatorFactory;

/**
 * An MSC service that provides an {@link AuthenticationService}.
 *
 * @author Carl Harris
 */
public class CasAuthenticationService
    extends AbstractService<AuthenticationService>
    implements AuthenticationService {

  private final InjectedValue<Profile> profile =
      new InjectedValue<>();
  
  private final InjectedValue<SSLContext> sslContext =
      new InjectedValue<>();
  
//  private final InjectedValue<HostnameVerifierService> hostnameVerifierService =
//      new InjectedValue<>();
  
  public Injector<Profile> getProfileInjector() {
    return profile;
  }
  
  public Injector<SSLContext> getSslContextInjector() {
    return sslContext;
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
  public Authenticator newAuthenticator() {
    return AuthenticatorFactory.newInstance(profile.getValue(), 
        sslContext.getValue(), 
        null);
  }

}
