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

import org.junit.Test;
import org.soulwing.cas.api.Transformer;

/**
 * Unit tests for {@link TransformerFactory}.
 *
 * @author Carl Harris
 */
public class TransformerFactoryTest {

  @Test
  public void testIsSimpleJavaIdentifier() throws Exception {
    assertThat(TransformerFactory.isSimpleJavaIdentifier("SomeTransformer"), 
        is(true));
    assertThat(TransformerFactory.isSimpleJavaIdentifier(
        "some.package.SomeTransformer"), is(false));
  }
  
  @Test
  public void testQualifiedTransformerName() throws Exception {
    assertThat(TransformerFactory.qualifiedTransformerName("Foo"),
        is(equalTo(getClass().getPackage().getName() + "." 
            + "Foo" + Transformer.class.getSimpleName())));
    assertThat(TransformerFactory.qualifiedTransformerName("FooTransformer"),
        is(equalTo(getClass().getPackage().getName() + "." 
            + "Foo" + Transformer.class.getSimpleName())));
  }
}
