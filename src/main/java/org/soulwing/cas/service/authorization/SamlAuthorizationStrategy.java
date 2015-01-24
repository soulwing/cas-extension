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

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.soulwing.cas.service.authentication.IdentityAssertion;

/**
 * A {@link AuthorizationStrategy} that utilizes the attributes from
 * a SAML assertion.
 *
 * @author Carl Harris
 */
public class SamlAuthorizationStrategy 
    extends AbstractAuthorizationStrategy<SamlAuthorizationConfig> {

  /**
   * Constructs a new instance.
   */
  public SamlAuthorizationStrategy() {
    super(new ConcreteSamlAuthorizationConfig());
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public Set<String> getApplicableRoles(IdentityAssertion assertion) {
    SamlAuthorizationConfig config = configuration.get();
    Map<String, Object> attributes = assertion.getAttributes();
    Set<String> roles = new LinkedHashSet<>();
    for (String roleAttribute : config.getRoleAttributes()) {
      Object value = attributes.get(roleAttribute);
      if (value instanceof Collection) {
        Iterator<?> i = ((Collection<?>) value).iterator();
        while (i.hasNext()) {
          roles.add(i.next().toString());
        }
      }
      else {
        roles.add(value.toString());
      }      
    }
    return roles;
  }

}
