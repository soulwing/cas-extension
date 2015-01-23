/*
 * File created on Dec 23, 2014 
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
package org.soulwing.cas.service.authentication;

/**
 * An enumeration of authentication protocols.
 *
 * @author Carl Harris
 */
public enum AuthenticationProtocol {

  CAS1_0("CAS-1.0"),
  CAS2_0("CAS-2.0"),
  SAML1_1("SAML-1.1");
  
  private final String displayName;

  /**
   * Constructs a new instance.
   * @param displayName
   */
  private AuthenticationProtocol(String displayName) {
    this.displayName = displayName;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return displayName;
  }

  /**
   * Converts a display name for a protocol into an instance from the 
   * enumeration.
   * @param displayName the subject display name
   * @return enum value
   * @throws IllegalArgumentException if {@code displayName} does not 
   *    correspond to a known value in the enumeration
   */
  public static AuthenticationProtocol toObject(String displayName) {
    for (AuthenticationProtocol protocol : values()) {
      if (protocol.toString().equals(displayName)) {
        return protocol;
      }
    }
    throw new IllegalArgumentException("invalid name");
  }

}
