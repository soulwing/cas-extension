/*
 * File created on Feb 13, 2015 
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
package org.soulwing.cas.extension;

import javax.net.ssl.HostnameVerifier;

import org.soulwing.cas.service.HostnameVerifierLocator;

/**
 * A {@link HostnameVerifierLocator} that returns {@code null}.
 *
 * @author Carl Harris
 */
class NullHostnameVerifierLocator implements HostnameVerifierLocator {

  public static final HostnameVerifierLocator INSTANCE =
      new NullHostnameVerifierLocator();
  
  private NullHostnameVerifierLocator() {
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public HostnameVerifier getHostnameVerifier() {
    return null;
  }

}
