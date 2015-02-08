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

import java.util.Date;
import java.util.Map;

import org.jasig.cas.client.validation.Assertion;

/**
 * An {@link IdentityAssertion} that delegates to an {@link Assertion}.
 *
 * @author Carl Harris
 */
class JasigIdentityAssertion implements IdentityAssertion {

  private final Assertion delegate;
  private final UserPrincipal principal; 
  
  /**
   * Constructs a new instance.
   * @param delegate
   */
  public JasigIdentityAssertion(Assertion delegate) {
    this.delegate = delegate;
    this.principal = new JasigUserPrincipal(delegate.getPrincipal());
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
    return delegate.getAttributes();
  }

}
