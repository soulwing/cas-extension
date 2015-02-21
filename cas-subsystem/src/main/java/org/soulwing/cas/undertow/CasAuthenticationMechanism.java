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

import org.soulwing.cas.api.IdentityAssertion;
import org.soulwing.cas.service.AuthenticationException;
import org.soulwing.cas.service.Authenticator;
import org.soulwing.cas.service.NoTicketException;

/**
 * An {@link AuthenticationMechanism} that uses the CAS protocol.
 * 
 * @author Carl Harris
 */
public class CasAuthenticationMechanism implements AuthenticationMechanism {

  public static final String MECHANISM_NAME = "CAS";
  
  private final String contextPath;
  private final CasAuthenticationService authenticationService;
  
  /**
   * Constructs a new instance.
   * @param contextPath 
   * @param authenticationService
   */
  public CasAuthenticationMechanism(
      String contextPath, CasAuthenticationService authenticationService) {
    this.contextPath = contextPath;
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
    
    Authenticator authenticator = authenticationService.newAuthenticator(
        contextPath);
    
    exchange.putAttachment(CasAttachments.AUTHENTICATOR_KEY, authenticator);

    try {
      IdentityAssertion assertion = authenticator.validateTicket(
          exchange.getRequestPath(), exchange.getQueryString());
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
        if (authenticator.isPostAuthRedirect()) {
          exchange.putAttachment(CasAttachments.POST_AUTH_REDIRECT_KEY, 
              true);
        }        
        
        securityContext.authenticationComplete(account, MECHANISM_NAME, true);
        return AuthenticationMechanismOutcome.AUTHENTICATED;
      }

      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("identity manager does not recognize user '"
            + assertion.getPrincipal().getName() + "'");
      }
      
      exchange.putAttachment(CasAttachments.AUTH_FAILED_KEY, 403);
      securityContext.authenticationFailed(
          "identity manager does not recognize user", MECHANISM_NAME);
      
      return AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
    }
    catch (NoTicketException ex) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("no ticket present; authentication not attempted");
      }
      return AuthenticationMechanismOutcome.NOT_ATTEMPTED;
    }
    catch (AuthenticationException ex) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("authentication failed: " + ex.getMessage());
      }

      exchange.putAttachment(CasAttachments.AUTH_FAILED_KEY, 401);
      securityContext.authenticationFailed(ex.getMessage(), MECHANISM_NAME);
      return AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
    }
    catch (RuntimeException ex) {
      LOGGER.error("CAS authentication exception: " + ex, ex);
    }
    
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("authentication not successful");
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
    
    Authenticator authenticator = exchange.getAttachment(
        CasAttachments.AUTHENTICATOR_KEY);
    
    String url = authenticator.loginUrl(exchange.getRequestPath(), 
        exchange.getQueryString());
    
    Integer failedStatus = 
        exchange.getAttachment(CasAttachments.AUTH_FAILED_KEY);
    if (failedStatus != null) {
      exchange.removeAttachment(CasAttachments.AUTH_FAILED_KEY);
      return new ChallengeResult(false, failedStatus);
    }
    
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("responding with redirect to '" + url + "'");
    }
    
    exchange.getResponseHeaders().put(
        HttpString.tryFromString("Location"), url);
    
    return new ChallengeResult(true, 302);
  }

}
