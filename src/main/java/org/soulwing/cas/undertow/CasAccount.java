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

import java.security.Principal;
import java.util.LinkedHashSet;
import java.util.Set;

import org.soulwing.cas.service.authentication.IdentityAssertion;

/**
 * An {@link Account} based on a {@link IdentityAssertion}.
 *
 * @author Carl Harris
 */
public class CasAccount implements Account {

  private final IdentityAssertion assertion;
  private final Set<String> roles = new LinkedHashSet<String>();
  
  /**
   * Constructs a new instance.
   * @param assertion
   * @param roles
   */
  public CasAccount(IdentityAssertion assertion, Set<String> roles) {
    this.assertion = assertion;
    this.roles.addAll(roles);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Principal getPrincipal() {
    return assertion.getPrincipal();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Set<String> getRoles() {
    return roles;
  }

  /**
   * Gets the {@code assertion} property.
   * @return property value
   */
  public IdentityAssertion getAssertion() {
    return assertion;
  }

  /**
   * Tests whether the identity assertion for this account remains valid.
   * @return {@code true} if this assertion is still valid
   */
  public boolean isValid() {
    return assertion.isValid();
  }

}
