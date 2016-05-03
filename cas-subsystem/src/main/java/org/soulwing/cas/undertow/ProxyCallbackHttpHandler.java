/*
 * File created on Feb 10, 2015 
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

import static org.soulwing.cas.undertow.UndertowLogger.LOGGER;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import org.soulwing.cas.service.AuthenticationService;
import org.soulwing.cas.service.ProxyCallbackResponse;

/**
 * An {@link HttpHandler} that handles proxy granting ticket callback from
 * the CAS server.
 *
 * @author Carl Harris
 */
public class ProxyCallbackHttpHandler implements HttpHandler {

  private final HttpHandler next;
  private final AuthenticationService authenticationService;

  
  /**
   * Constructs a new instance.
   * @param next next handler
   * @param authenticationService service delegate
   */
  public ProxyCallbackHttpHandler(HttpHandler next,
      AuthenticationService authenticationService) {
    this.next = next;
    this.authenticationService = authenticationService;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void handleRequest(HttpServerExchange exchange) throws Exception {
    if (authenticationService.isProxyCallbackPath(
        exchange.getRelativePath())) {
      
      if (exchange.isInIoThread()) {
        exchange.dispatch(this);
        return;
      }

      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("proxy callback: " + exchange.getRequestMethod() + " " 
            + exchange.getRequestURL()
            + "?" + exchange.getQueryString() + " (content-length="
            + exchange.getRequestContentLength() + " content-type="
            + exchange.getRequestHeaders().get(Headers.CONTENT_TYPE, 0)
            + ")");
      }
      
      
      ProxyCallbackResponse response = authenticationService
          .handleProxyCallback(exchange.getQueryString());
      
      String message = response.getMessage();
      exchange.setResponseCode(response.getStatus());
      
      if (message.isEmpty())
        exchange.setResponseContentLength(0);
      else {
        exchange.getResponseHeaders().put(
            Headers.CONTENT_TYPE, "text/plain; charset=UTF-8");
        exchange.getResponseSender().send(message + "\r\n");
      }
      
      exchange.endExchange();
      return;
    }
    
    next.handleRequest(exchange);
  }
      
}
