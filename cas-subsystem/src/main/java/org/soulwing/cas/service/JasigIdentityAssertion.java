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

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import org.jasig.cas.client.validation.Assertion;
import org.soulwing.cas.api.IdentityAssertion;
import org.soulwing.cas.api.Transformer;
import org.soulwing.cas.api.UserPrincipal;

/**
 * An {@link IdentityAssertion} that delegates to an {@link Assertion}.
 *
 * @author Carl Harris
 */
class JasigIdentityAssertion implements IdentityAssertion, Serializable {

  private static final long serialVersionUID = 309684827031727467L;

  private final Authenticator authenticator;
  private final Assertion delegate;
  private final UserPrincipal principal; 
  private final Map<String, Object> attributes;
  
  /**
   * Constructs a new instance.
   * @param delegate
   * @param transformers
   */
  public JasigIdentityAssertion(Authenticator authenticator,
      Assertion delegate, Map<String, Transformer<Object, Object>> transformers) {
    this.authenticator = authenticator;
    this.delegate = delegate;
    this.principal = new JasigUserPrincipal(authenticator,
        delegate.getPrincipal(), transformers);
    this.attributes = new TransformingMap(delegate.getAttributes(), 
        transformers);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UserPrincipal getPrincipal() {
    return principal;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isValid() {
    return delegate.isValid();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Date getValidFromDate() {
    return delegate.getValidFromDate();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Date getValidUntilDate() {
    return delegate.getValidUntilDate();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Date getAuthenticationDate() {
    return delegate.getAuthenticationDate();
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
  public Object getDelegate() {
    return delegate;
  }

}
