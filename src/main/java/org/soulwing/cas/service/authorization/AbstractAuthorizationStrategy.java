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

import java.util.concurrent.atomic.AtomicReference;

/**
 * An abstract base for {@link AuthorizationStrategy} implementations.
 *
 * @author Carl Harris
 */
abstract class AbstractAuthorizationStrategy<T> 
    implements AuthorizationStrategy<T> {

  protected AtomicReference<T> configuration = new AtomicReference<T>();
  
  /**
   * {@inheritDoc}
   */
  @Override
  public final void reconfigure(T configuration) {
    this.configuration.set(configuration);
    onReconfigure(configuration);
  }

  protected void onReconfigure(T configuration) {    
  }
  
}
