/*
 * File created on Feb 26, 2015 
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
package org.soulwing.cas.transformer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.Properties;

import org.junit.Test;

/**
 * Unit tests for {@link FlattenCaseTransformer}.
 *
 * @author Carl Harris
 */
public class FlattenCaseTransformerTest {

  private Properties properties = new Properties();
  
  private FlattenCaseTransformer transformer = new FlattenCaseTransformer();
  
  @Test
  public void testFlattenToLowerCase() throws Exception {
    transformer.initialize(properties);
    assertThat(transformer.transform("FOOBAR"), is(equalTo("foobar")));
  }
  
  @Test
  public void testFlattenToUpperCase() throws Exception {
    properties.setProperty(FlattenCaseTransformer.USE_UPPER_CASE, "true");
    transformer.initialize(properties);
    assertThat(transformer.transform("foobar"), is(equalTo("FOOBAR")));
  }

}
