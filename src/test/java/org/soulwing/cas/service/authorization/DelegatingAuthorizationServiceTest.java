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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

import java.util.Collections;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.cas.service.authentication.IdentityAssertion;

/**
 * Unit tests for {@link DelegatingAuthorizationService}.
 *
 * @author Carl Harris
 */
public class DelegatingAuthorizationServiceTest {

  private static final String SERVICE_NAME = "someServiceName";
  
  private static final String STRATEGY_NAME = "someStrategyName";
  
  private static final String DEFAULT_ROLE = "defaultRole";
  
  private static final String OTHER_ROLE = "otherRole";

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  @Mock
  private AuthorizationStrategy<?> strategy;
  
  @Mock
  private IdentityAssertion assertion;

  private ConcreteAuthorizationConfig config = 
      new ConcreteAuthorizationConfig();
  
  private DelegatingAuthorizationService service = 
      new DelegatingAuthorizationService(SERVICE_NAME);
  
  @Before
  public void setUp() throws Exception {
    config.setDefaultRole(DEFAULT_ROLE);
    service.reconfigure(config);
    service.putStrategy(STRATEGY_NAME, strategy);
  }

  @Test
  public void testGetApplicableRoles() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(strategy).getApplicableRoles(assertion);
        will(returnValue(Collections.singleton(OTHER_ROLE)));
      }
    });
    
    assertThat(service.getApplicableRoles(assertion), 
        contains(DEFAULT_ROLE, OTHER_ROLE));
  }

}
