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

import static org.soulwing.cas.undertow.UndertowLogger.LOGGER;
import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.SecurityContext;
import io.undertow.security.idm.Account;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;

import java.util.Deque;

import org.soulwing.cas.api.IdentityAssertion;
import org.soulwing.cas.service.AuthenticationException;
import org.soulwing.cas.service.AuthenticationService;

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
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("no authentication ticket; authentication not attempted");
      }
      return AuthenticationMechanismOutcome.NOT_ATTEMPTED;
    }
    
    try {
      String query = QueryUtil.removeProtocolParameters(
          authenticationService.getConfiguration().getProtocol(),
          exchange.getQueryString());

      IdentityAssertion assertion = authenticationService.validateTicket(
          exchange.getRequestPath(), query, ticket);
      IdentityAssertionCredential credential = 
          new IdentityAssertionCredential(assertion);
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("valid ticket");
      }
   
      Account account = securityContext.getIdentityManager()
          .verify(assertion.getPrincipal().getName(), credential);
      if (account != null) {
        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("authentication complete: " 
              + " user=" + account.getPrincipal().getName()
              + " roles=" + account.getRoles());
        }
        
        exchange.putAttachment(CasAttachments.CREDENTIAL_KEY, credential);
        if (authenticationService.getConfiguration().isPostAuthRedirect()) {
          exchange.putAttachment(CasAttachments.POST_AUTH_REDIRECT_KEY, 
              authenticationService.getConfiguration().getProtocol());
        }
                
        securityContext.authenticationComplete(account, MECHANISM_NAME, true);
        return AuthenticationMechanismOutcome.AUTHENTICATED;
      }
    }
    catch (AuthenticationException ex) {
      LOGGER.info("authentication failed: " + ex);
    }
    
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("authentication not successful for ticket '"
          + ticket + "'");
    }
    securityContext.setAuthenticationRequired();
    return AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ChallengeResult sendChallenge(HttpServerExchange exchange,
      SecurityContext context) {
    String url = authenticationService.loginUrl(exchange.getRequestPath(), 
        exchange.getQueryString());
    
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("responding with redirect to '" + url + "'");
    }
    
    exchange.getResponseHeaders().put(HttpString.tryFromString("Location"), 
        url);
    return new ChallengeResult(true, 302);
  }

}
