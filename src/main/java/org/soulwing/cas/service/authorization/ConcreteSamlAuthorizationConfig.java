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

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A concrete {@link SamlAuthorizationConfig} implementation.
 *
 * @author Carl Harris
 */
class ConcreteSamlAuthorizationConfig implements SamlAuthorizationConfig {

  private final Set<String> roleAttributes = new LinkedHashSet<>();
  
  /**
   * {@inheritDoc}
   */
  @Override
  public Set<String> getRoleAttributes() {
    return Collections.unmodifiableSet(roleAttributes);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setRoleAttributes(Set<String> roleAttributes) {
    this.roleAttributes.addAll(roleAttributes);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SamlAuthorizationConfig clone() {
    SamlAuthorizationConfig clone = new ConcreteSamlAuthorizationConfig();
    clone.setRoleAttributes(roleAttributes);
    return clone;
  }

}
