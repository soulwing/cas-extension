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
import io.undertow.server.handlers.Cookie;
import io.undertow.server.handlers.CookieImpl;
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

  public static final String NOT_AUTHORIZED_MESSAGE = 
      "identity manager does not recognize user '%s'";

  public static final String STATUS_COOKIE = "cas-status";

  public static final int MAX_RETRIES = 2;

  
  
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
      
      Account account = authorize(credential, securityContext);
      
      resetRetryCount(exchange);
      
      exchange.putAttachment(CasAttachments.CREDENTIAL_KEY, credential);
      if (authenticator.isPostAuthRedirect()) {
        exchange.putAttachment(CasAttachments.POST_AUTH_REDIRECT_KEY, 
            true);
      }        
        
      securityContext.authenticationComplete(account, MECHANISM_NAME, true);
      return AuthenticationMechanismOutcome.AUTHENTICATED;
    }
    catch (NoTicketException ex) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("no ticket present; authentication not attempted");
      }
      return AuthenticationMechanismOutcome.NOT_ATTEMPTED;
    }
    catch (AuthorizationException ex) {
      exchange.putAttachment(CasAttachments.AUTH_FAILED_KEY, 403);
      securityContext.authenticationFailed(ex.getMessage(), MECHANISM_NAME);
      return AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
    }
    catch (AuthenticationException | RuntimeException ex) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("authentication failed: " + ex.getMessage());
      }
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
    
    Integer failedStatus = 
        exchange.getAttachment(CasAttachments.AUTH_FAILED_KEY);
    if (failedStatus != null) {
      exchange.removeAttachment(CasAttachments.AUTH_FAILED_KEY);
      resetRetryCount(exchange);
      return new ChallengeResult(false, failedStatus);
    }
        
    if (getRetryCount(exchange) >= MAX_RETRIES) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("authentication failed: too many retries");
      }
      resetRetryCount(exchange);
      return new ChallengeResult(false, 401);
    }
    
    Authenticator authenticator = exchange.getAttachment(
        CasAttachments.AUTHENTICATOR_KEY);
    
    String url = authenticator.loginUrl(exchange.getRequestPath(), 
        exchange.getQueryString());
    
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("responding with redirect to '" + url + "'");
    }
    
    exchange.getResponseHeaders().put(
        HttpString.tryFromString("Location"), url);
    
    return new ChallengeResult(true, 302);
  }

  /**
   * Gets the authentication retry count from a session cookie.
   * @param exchange subject exchange
   * @return current retry count
   */
  private int getRetryCount(HttpServerExchange exchange) {
    int retries = 0;
    
    Cookie cookie = exchange.getRequestCookies().get(STATUS_COOKIE);
    if (cookie == null) {
      cookie = newCookie();
    }
    else {
      try {
        String value = cookie.getValue();
        retries = Integer.parseInt(value) + 1;
        if (retries < 0 || retries > MAX_RETRIES) {
          retries = MAX_RETRIES;
        }
      }
      catch (NumberFormatException ex) {
        retries = MAX_RETRIES;
      }
      finally {
        cookie.setValue(Integer.toString(retries));
      }
    }

    cookie.setHttpOnly(true);
    cookie.setSecure(true);
    exchange.getResponseCookies().put(STATUS_COOKIE, cookie);
    return retries;
  }

  /**
   * Resets the authentication retry count in the session cookie.
   * @param exchange the subject exchange
   */
  private void resetRetryCount(HttpServerExchange exchange) {
    Cookie cookie = newCookie();
    cookie.setValue("-1");
    exchange.getResponseCookies().put(STATUS_COOKIE, cookie);
  }

  /**
   * Creates a new cookie for the authentication retry count.
   * @return cookie
   */
  private Cookie newCookie() {
    Cookie cookie;
    cookie = new CookieImpl(STATUS_COOKIE, "0");
    cookie.setHttpOnly(true);
    cookie.setSecure(true);
    return cookie;
  }

  /**
   * Authorizes the user associated with the given assertion credential via
   * the container's identity manager.
   * @param credential the subject user credential
   * @param securityContext security context
   * @return authorized user's account object
   * @throws AuthorizationException if the user is not authorized
   */
  private Account authorize(IdentityAssertionCredential credential,
      SecurityContext securityContext) throws AuthorizationException {
    
    String name = credential.getIdentityAssertion().getPrincipal().getName();
  
    Account account = securityContext.getIdentityManager().verify(
        name, credential);
    
    if (account == null) {
      String message = String.format(NOT_AUTHORIZED_MESSAGE, name);
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug(message);
      }
      throw new AuthorizationException(message);
    }
      
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("authorization successful: " 
          + " user=" + account.getPrincipal().getName()
          + " roles=" + account.getRoles());
    }
    
    return account;
  }

}
