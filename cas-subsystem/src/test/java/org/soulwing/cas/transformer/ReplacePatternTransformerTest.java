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
import static org.hamcrest.Matchers.is;

import java.util.Properties;

import org.junit.Test;

/**
 * Unit tests for {@link ReplacePatternTransformer}.
 *
 * @author Carl Harris
 */
public class ReplacePatternTransformerTest {

  private Properties properties = new Properties();
  private ReplacePatternTransformer transformer = 
      new ReplacePatternTransformer();
  
  @Test
  public void testReplacePatternWithDefaultReplacement() throws Exception {
    properties.setProperty(ReplacePatternTransformer.PATTERN, "foo");
    transformer.initialize(properties);
    assertThat(transformer.transform("foo"), is(""));    
  }

  @Test
  public void testReplaceAllWithDefaultReplacement() throws Exception {
    properties.setProperty(ReplacePatternTransformer.PATTERN, "foo");
    properties.setProperty(ReplacePatternTransformer.REPLACE_ALL, "true");
    transformer.initialize(properties);
    assertThat(transformer.transform("foofoo"), is(""));    
  }

  @Test
  public void testReplacePatternWithSpecifiedReplacement() throws Exception {
    properties.setProperty(ReplacePatternTransformer.PATTERN, "foo");
    properties.setProperty(ReplacePatternTransformer.REPLACEMENT, "bar");
    transformer.initialize(properties);
    assertThat(transformer.transform("foo"), is("bar"));    
  }

  @Test
  public void testReplaceAllWithSpecifiedReplacement() throws Exception {
    properties.setProperty(ReplacePatternTransformer.PATTERN, "foo");
    properties.setProperty(ReplacePatternTransformer.REPLACEMENT, "bar");
    properties.setProperty(ReplacePatternTransformer.REPLACE_ALL, "true");
    transformer.initialize(properties);
    assertThat(transformer.transform("foofoo"), is("barbar"));    
  }

}

