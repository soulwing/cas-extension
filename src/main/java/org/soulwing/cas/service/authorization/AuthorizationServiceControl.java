/*
 * File created on Dec 22, 2014 
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

import org.jboss.msc.service.AbstractService;

/**
 * A JBoss MSC service that manages an {@link AuthorizationService}.
 *
 * @author Carl Harris
 */
public class AuthorizationServiceControl 
    extends AbstractService<AuthorizationService> {

  private final AuthorizationService delegate;
  
  /**
   * Constructs a new instance.
   * @param delegate
   */
  public AuthorizationServiceControl(AuthorizationService delegate) {
    this.delegate = delegate;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public AuthorizationService getValue() throws IllegalStateException {
    return delegate;
  }

}
