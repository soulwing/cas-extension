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
package org.soulwing.cas.service;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * DESCRIBE THE TYPE HERE.
 *
 * @author Carl Harris
 */
public class MockIdentityAssertion implements IdentityAssertion {

  private final Map<String, Object> attributes = new LinkedHashMap<>();
  
  /**
   * Constructs a new instance.
   */
  public MockIdentityAssertion() {
    attributes.put("groupMembership", "valid-user");
    attributes.put("virginiaTechAffiliation", 
        Collections.singletonList("VT-EMPLOYEE"));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UserPrincipal getPrincipal() {
    return new MockPrincipal();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isValid() {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Date getValidFromDate() {
    return new Date(0);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Date getValidUntilDate() {
    return new Date(Integer.MAX_VALUE);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Date getAuthenticationDate() {
    return new Date();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, Object> getAttributes() {
    return attributes;
  }

  public class MockPrincipal implements UserPrincipal {

    private static final long serialVersionUID = 8028827605914126906L;
    private final String name = "someuser";
        
    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
      return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> getAttributes() {
      return attributes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String generateProxyTicket(String service)
        throws IllegalStateException {
      throw new IllegalStateException();
    }
    
  }
}
