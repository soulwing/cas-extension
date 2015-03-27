/*
 * File created on Feb 16, 2015 
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
package org.soulwing.cas.ssl;

import javax.net.ssl.HostnameVerifier;

/**
 * A factory that produces {@link HostnameVerifier} objects.
 *
 * @author Carl Harris
 */
public class HostnameVerifierFactory {

  /**
   * Constructs a new hostname verifier.
   * @param type verifier type
   * @param args verifier-specific args
   * @return verifier
   */
  public static HostnameVerifier newInstance(
      HostnameVerifierType type, String... args) {
    switch (type) {
      case ALLOW_ANY:
        return new AllowAnyHostnameVerifier();
      case PATTERN_MATCH:
        return new PatternMatchHostnameVerifier(args);
      case WHITE_LIST:
        return new WhiteListHostnameVerifier(args);
      default:
        throw new IllegalArgumentException("unrecognized verifier type");
    }
  }
  
}
