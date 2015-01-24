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
 * Unit tests for {@link SamlAuthorizationStrategy}.
 *
 * @author Carl Harris
 */
public class SamlAuthorizationStrategyTest {
  
  private static final String ATTRIBUTE_NAME = "someAttributeName";

  private static final String ATTRIBUTE_VALUE = "someAttributeValue";


  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  @Mock
  private SamlAuthorizationConfig samlConfig;
  
  @Mock
  private AuthorizationConfig config;
  
  @Mock
  private IdentityAssertion assertion;
  
  private SamlAuthorizationStrategy strategy = 
      new SamlAuthorizationStrategy();

  @Before
  public void setUp() throws Exception {
    strategy.reconfigure(config);
    context.checking(new Expectations() {
      {
        allowing(config).getSamlConfig();
        will(returnValue(samlConfig));
      }
    });
  }

  @Test
  public void testWhenAttributeValueIsNotCollection() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(samlConfig).getRoleAttributes();
        will(returnValue(Collections.singleton(ATTRIBUTE_NAME)));
        oneOf(assertion).getAttributes();
        will(returnValue(Collections.singletonMap(ATTRIBUTE_NAME, 
            ATTRIBUTE_VALUE)));        
      }
    });
    
    assertThat(strategy.getApplicableRoles(assertion), 
        contains(ATTRIBUTE_VALUE));
  }

  @Test
  public void testWhenAttributeValueCollection() throws Exception {
    context.checking(new Expectations() {
      {
        oneOf(samlConfig).getRoleAttributes();
        will(returnValue(Collections.singleton(ATTRIBUTE_NAME)));
        oneOf(assertion).getAttributes();
        will(returnValue(Collections.singletonMap(ATTRIBUTE_NAME, 
            Collections.singleton(ATTRIBUTE_VALUE))));        
      }
    });
    
    assertThat(strategy.getApplicableRoles(assertion), 
        contains(ATTRIBUTE_VALUE));
  }

}
