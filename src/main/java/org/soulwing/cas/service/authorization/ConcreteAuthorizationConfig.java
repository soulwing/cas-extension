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

/**
 * A concrete {@link MutableAuthorizationConfig} implementation.
 *
 * @author Carl Harris
 */
class ConcreteAuthorizationConfig implements MutableAuthorizationConfig {

  private String defaultRole;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String getDefaultRole() {
    return defaultRole;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setDefaultRole(String defaultRole) {
    this.defaultRole = defaultRole;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public MutableAuthorizationConfig clone() {
    try {
      return (MutableAuthorizationConfig) super.clone();
    }
    catch (CloneNotSupportedException ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return String.format("%s::defaultRole=%s", getClass().getSimpleName(),
        defaultRole);
  }

}
