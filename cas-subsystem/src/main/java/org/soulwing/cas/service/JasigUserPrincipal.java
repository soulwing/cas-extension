/*
 * File created on Jan 23, 2015 
 *
 * Copyright (c) 2015 Carl Harris, Jr.
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
package org.soulwing.cas.service;

import java.util.Map;

import org.jasig.cas.client.authentication.AttributePrincipal;
import org.soulwing.cas.api.Transformer;
import org.soulwing.cas.api.UserPrincipal;

/**
 * A {@link UserPrincipal} that delegates to a {@link AttributePrincipal}
 *
 * @author Carl Harris
 */
class JasigUserPrincipal implements UserPrincipal {

  private static final long serialVersionUID = 1805860723380323513L;

  private final AttributePrincipal delegate;
  private final Map<String, Object> attributes;
    
  /**
   * Constructs a new instance.
   * @param delegate
   * @param transformers
   */
  public JasigUserPrincipal(AttributePrincipal delegate, 
      Map<String, Transformer<Object, Object>> transformers) {
    this.delegate = delegate;
    this.attributes = new TransformingMap(delegate.getAttributes(), 
        transformers);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return delegate.getName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, Object> getAttributes() {
    return attributes;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String generateProxyTicket(String service)
      throws IllegalStateException {
    String ticket = delegate.getProxyTicketFor(service);
    if (ticket == null) {
      throw new IllegalStateException("proxy granting ticket unavailable");
    }
    return ticket;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getDelegate() {
    return delegate;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return getName();
  }

}
