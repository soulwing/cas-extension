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
import io.undertow.util.HttpString;

import org.soulwing.cas.service.AuthenticationProtocol;

/**
 * An {@link HttpHandler} that sends a redirect to the request URL if the 
 * URL contains the CAS protocol's ticket parameter and the 
 * {@link POST_AUTH_ATTACHMENT_KEY} attachment is set.
 *
 * @author Carl Harris
 */
public class PostAuthRedirectHandler implements HttpHandler {

  private final HttpHandler next;
  
  /**
   * Constructs a new instance.
   * @param next
   */
  public PostAuthRedirectHandler(HttpHandler next) {
    this.next = next;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void handleRequest(HttpServerExchange exchange) throws Exception {
    AuthenticationProtocol protocol = exchange.getAttachment(
        CasAttachments.POST_AUTH_REDIRECT_KEY);
    
    if (protocol != null) {

      exchange.removeAttachment(CasAttachments.POST_AUTH_REDIRECT_KEY);
      LOGGER.debug("found post auth redirect key for protocol " + protocol);
      
      String query = exchange.getQueryString();
      if (query != null && !query.isEmpty()) {
        query = QueryUtil.removeProtocolParameters(protocol, query);
        
        String url = exchange.getRequestURL();
        if (!query.isEmpty()) {
          url = url + "?" + query;
        }
        
        LOGGER.debug("sending redirect to " + url);
        exchange.setResponseCode(302);
        exchange.getResponseHeaders().put(HttpString.tryFromString("Location"), 
            url);
        exchange.endExchange();
        return;
      }

    }

    next.handleRequest(exchange);
  }

}
