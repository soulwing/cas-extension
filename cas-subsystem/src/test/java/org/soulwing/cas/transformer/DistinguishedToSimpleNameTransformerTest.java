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
 * Unit tests for {@link DistinguishedToSimpleNameTransformer}.
 *
 * @author Carl Harris
 */
public class DistinguishedToSimpleNameTransformerTest {

  private Properties properties = new Properties();
  
  private DistinguishedToSimpleNameTransformer transformer =
      new DistinguishedToSimpleNameTransformer();
 
  @Test
  public void testWithDefaultNameComponent() throws Exception {
    String name = DistinguishedToSimpleNameTransformer.DEFAULT_NAME_COMPONENT 
        + "=foobar";
    transformer.initialize(properties);
    assertThat(transformer.transform(name), is(equalTo("foobar")));
  }

  @Test
  public void testWithSpecifiedNameComponent() throws Exception {
    String name = "uugid=foobar";

    properties.setProperty(DistinguishedToSimpleNameTransformer.NAME_COMPONENT, 
        "uugid");
    transformer.initialize(properties);
    assertThat(transformer.transform(name), is(equalTo("foobar")));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testComponentNotFoundWhenFailOnError() throws Exception {
    properties.setProperty(DistinguishedToSimpleNameTransformer.FAIL_ON_ERROR, 
        "true");
    transformer.initialize(properties);
    transformer.transform("foo=foo");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidNameWhenFailOnError() throws Exception {
    properties.setProperty(DistinguishedToSimpleNameTransformer.FAIL_ON_ERROR, 
        "true");
    transformer.initialize(properties);
    transformer.transform("foo=bar=baz");
  }

  @Test
  public void testComponentNotFoundWhenNotFailOnError() throws Exception {
    transformer.initialize(properties);
    assertThat(transformer.transform("foo=foo"), is(equalTo("foo=foo")));
  }

  @Test
  public void testInvalidNameWhenNotFailOnError() throws Exception {
    transformer.initialize(properties);
    assertThat(transformer.transform("foo=bar=baz"), is(equalTo("foo=bar=baz")));
  }

}
