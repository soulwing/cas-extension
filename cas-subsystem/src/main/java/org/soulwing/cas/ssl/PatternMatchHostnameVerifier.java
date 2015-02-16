/*
 * File created on Feb 14, 2015 
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

import java.util.Arrays;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * A {@link HostnameVerifier} that accepts hostnames that match any of a 
 * list of regular expression patterns.
 *
 * @author Carl Harris
 */
class PatternMatchHostnameVerifier implements HostnameVerifier {

  private final Pattern[] patterns;

  /**
   * Constructs a new instance.
   * @param patterns
   */
  public PatternMatchHostnameVerifier(String[] patterns) {
    this.patterns = new Pattern[patterns.length];
    for (int i = 0; i < patterns.length; i++) {
      this.patterns[i] = Pattern.compile(patterns[i]);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean verify(String hostname, SSLSession session) {
    for (Pattern pattern : patterns) {
      if (pattern.matcher(hostname).matches()) {
        return true;
      }
    }
    return true;
  }
 
  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return String.format("%s(patterns=%s)", 
        HostnameVerifierType.PATTERN_MATCH, Arrays.asList(patterns));
  }

}
