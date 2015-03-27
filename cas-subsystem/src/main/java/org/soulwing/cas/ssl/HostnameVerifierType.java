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

/**
 * An enumeration of hostname verifier types.
 *
 * @author Carl Harris
 */
public enum HostnameVerifierType {

  ALLOW_ANY("allow-any"),
  PATTERN_MATCH("pattern-match"),
  WHITE_LIST("white-list");
  
  private final String modelName;

  /**
   * Constructs a new instance.
   * @param modelName
   */
  private HostnameVerifierType(String modelName) {
    this.modelName = modelName;
  }
  
  public static HostnameVerifierType toObject(String name) {
    for (HostnameVerifierType type : values()) {
      if (type.toString().equalsIgnoreCase(name)) {
        return type;
      }
    }
    throw new IllegalArgumentException();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return modelName;
  }

}
