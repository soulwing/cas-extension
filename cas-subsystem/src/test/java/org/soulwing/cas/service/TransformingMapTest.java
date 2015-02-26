/*
 * File created on Feb 25, 2015 
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
package org.soulwing.cas.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;

import java.util.Collections;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.cas.api.Transformer;

/**
 * Unit tests for {@link TransformingMap}.
 *
 * @author Carl Harris
 */
public class TransformingMapTest {

  private static final String KEY = "key";

  private static final String VALUE = "value";

  @Rule
  public final JUnitRuleMockery context = new JUnitRuleMockery();
  
  @Mock
  private Transformer<Object, Object> transformer;
  
  @Test
  public void testIdentityTransform() throws Exception {
    TransformingMap map = new TransformingMap(
        Collections.<String, Object>singletonMap(KEY, VALUE), 
        Collections.<String, Transformer<Object, Object>>emptyMap());
    
    assertThat(map.get(KEY), is(sameInstance((Object) VALUE)));
  }

  @Test
  public void testDefinedTransform() throws Exception {
    TransformingMap map = new TransformingMap(
        Collections.<String, Object>singletonMap(KEY, VALUE), 
        Collections.singletonMap(KEY, transformer));
    
    context.checking(new Expectations() {
      {
        oneOf(transformer).transform(VALUE);
        will(returnValue(VALUE));
      }
    });
    
    assertThat(map.get(KEY), is(sameInstance((Object) VALUE)));
  }

}
