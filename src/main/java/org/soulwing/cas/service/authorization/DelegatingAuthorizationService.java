/*
 * File created on Jan 24, 2015 
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
package org.soulwing.cas.service.authorization;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.soulwing.cas.service.authentication.IdentityAssertion;

/**
 * An {@link AuthorizationService} that delegates to a collection of 
 * {@link AuthorizationStrategy} objects.
 *
 * @author Carl Harris
 */
class DelegatingAuthorizationService implements AuthorizationService {

  private final AtomicReference<AuthorizationConfig> configuration =
      new AtomicReference<AuthorizationConfig>(new ConcreteAuthorizationConfig());
  
  private final String name;
  private final List<AuthorizationStrategy> strategies;
  
  /**
   * Constructs a new instance.
   * @param name unqualified service name
   */
  public DelegatingAuthorizationService(String name) {
    this(name, loadStrategies());
  }

  protected DelegatingAuthorizationService(String name, 
      List<AuthorizationStrategy> strategies) {
    this.name = name;
    this.strategies = strategies;
  }
  
  private static List<AuthorizationStrategy> loadStrategies() {
    List<AuthorizationStrategy> strategies = new ArrayList<>();
    for (AuthorizationStrategy strategy : 
      ServiceLoader.load(AuthorizationStrategy.class)) {
      strategies.add(strategy);
    }
    return strategies;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return name;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Set<String> getApplicableRoles(IdentityAssertion assertion) {
    AuthorizationConfig config = configuration.get();
    Set<String> roles = new LinkedHashSet<>();
    roles.add(config.getDefaultRole());
    for (AuthorizationStrategy strategy : strategies) {
      roles.addAll(strategy.getApplicableRoles(assertion));
    }
    return roles;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public AuthorizationConfig getConfiguration() {
    return configuration.get().clone();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void reconfigure(AuthorizationConfig configuration) {
    AuthorizationConfig clone = configuration.clone();
    this.configuration.set(clone);
    for (AuthorizationStrategy strategy : strategies) {
      strategy.reconfigure(clone);
    }
  }

}
