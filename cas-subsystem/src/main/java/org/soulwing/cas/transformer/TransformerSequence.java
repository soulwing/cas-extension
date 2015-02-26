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

import java.util.ArrayList;
import java.util.List;

import org.soulwing.cas.api.Transformer;

/**
 * A transformer that invokes a sequence of configured transformers.
 *
 * @author Carl Harris
 */
public class TransformerSequence implements Transformer<Object, Object> {

  private final List<Transformer<Object, Object>> transformers =
      new ArrayList<>();
  
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public TransformerSequence(List<Transformer<?, ?>> transformers) {
    for (Transformer transformer : transformers) {
      this.transformers.add(transformer);
    }
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public Object transform(Object value) {
    Object t = value;
    for (Transformer<Object, Object> transformer : transformers) {
      t = transformer.transform(t);
    }
    return t;
  }

  @Override
  public String toString() {
    return transformers.toString();
  }

}
