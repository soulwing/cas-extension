/*
 * File created on May 21, 2014 
 *
 * Copyright 2007-2014 Carl Harris, Jr.
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

import io.undertow.security.idm.Account;
import io.undertow.security.idm.Credential;
import io.undertow.security.idm.IdentityManager;

import java.util.Set;

import org.soulwing.cas.service.authentication.AuthenticationException;
import org.soulwing.cas.service.authentication.AuthenticationService;
import org.soulwing.cas.service.authentication.IdentityAssertion;
import org.soulwing.cas.service.authorization.AuthorizationService;

/**
 * An {@link IdentityManager} that supports CAS authentication.
 *
 * @author Carl Harris
 */
public class CasIdentityManager implements IdentityManager {

  private final AuthenticationService authenticationService;
  private final AuthorizationService authorizationService;
  
  /**
   * Constructs a new instance.
   * @param authenticationService
   * @param authorizationService
   */
  public CasIdentityManager(AuthenticationService authenticationService, 
      AuthorizationService authorizationService) {
    this.authenticationService = authenticationService;
    this.authorizationService = authorizationService;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Account verify(Account account) {
    if (account instanceof CasAccount) {
      CasAccount casAccount = (CasAccount) account;
      if (!casAccount.isValid()) return null;
      // TODO -- might not want to refresh roles on each call to verify
      IdentityAssertion assertion = casAccount.getAssertion();
      Set<String> roles = authorizationService.getApplicableRoles(assertion);
      return new CasAccount(assertion, roles);
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Account verify(Credential credential) {
    if (!(credential instanceof CasTicketCredential)) {
      throw new IllegalArgumentException();
    }

    String ticket = ((CasTicketCredential) credential).getTicket();
    try {
      IdentityAssertion assertion = authenticationService.validateTicket(ticket);
      Set<String> roles = authorizationService.getApplicableRoles(assertion);
      return new CasAccount(assertion, roles);
    }
    catch (AuthenticationException ex) {
      return null;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Account verify(String user, Credential credential) {
    throw new UnsupportedOperationException();
  }

}
