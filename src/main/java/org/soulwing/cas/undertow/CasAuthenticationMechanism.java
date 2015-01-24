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

import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.SecurityContext;
import io.undertow.security.idm.Account;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;

import java.util.Deque;

import org.soulwing.cas.service.authentication.AuthenticationService;

/**
 * An {@link AuthenticationMechanism} that uses the CAS protocol.
 * 
 * @author Carl Harris
 */
public class CasAuthenticationMechanism implements AuthenticationMechanism {

  public static final String MECHANISM_NAME = "CAS";
  
  private final AuthenticationService authenticationService;

  /**
   * Constructs a new instance.
   * @param authenticationService
   */
  public CasAuthenticationMechanism(
      AuthenticationService authenticationService) {
    this.authenticationService = authenticationService;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public AuthenticationMechanismOutcome authenticate(
      HttpServerExchange exchange, SecurityContext securityContext) {
    if (!securityContext.isAuthenticationRequired()) {
      return AuthenticationMechanismOutcome.NOT_ATTEMPTED;
    }
    
    Deque<String> tickets = exchange.getQueryParameters().get(
        authenticationService.getConfiguration().getProtocol().getTicketParameterName());
    String ticket = tickets != null ? tickets.peekFirst() : null;
    if (ticket == null) {
//      securityContext.setAuthenticationRequired();
      return AuthenticationMechanismOutcome.NOT_ATTEMPTED;
    }
    Account account = securityContext.getIdentityManager().verify(
        new CasTicketCredential(ticket));
    if (account != null) {
      securityContext.authenticationComplete(account, MECHANISM_NAME, true);
      return AuthenticationMechanismOutcome.AUTHENTICATED;
    }
    else {
      securityContext.setAuthenticationRequired();
      return AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ChallengeResult sendChallenge(HttpServerExchange exchange,
      SecurityContext context) {
    exchange.getResponseHeaders().put(HttpString.tryFromString("Location"),
        authenticationService.loginUrl(exchange.getRequestPath(), 
            exchange.getQueryString()));
    return new ChallengeResult(true, 302);
  }

}
