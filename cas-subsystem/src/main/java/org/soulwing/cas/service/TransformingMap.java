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

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jboss.as.controller.transform.Transformers;
import org.soulwing.cas.api.Transformer;

/**
 * A {@link Map} that transforms mapped values in a delegate map using 
 * a map of corresponding {@link Transformers}.
 * <p>
 * If the specified transformer map does not contain a value for a key defined
 * in the delegate map, the value of the delegate map is not transformed 
 * (in other words, an identity transform is applied).
 *
 * @author Carl Harris
 */
class TransformingMap extends AbstractMap<String, Object> implements Serializable {

  private static final long serialVersionUID = -217419703730591133L;

  private final Lock lock = new ReentrantLock();
  private final Map<String, Object> delegate;
  private final Map<String, Transformer<Object, Object>> transformers;
  private Set<Map.Entry<String, Object>> entrySet;
  
  /**
   * Constructs a new instance.
   * @param delegate
   */
  public TransformingMap(Map<String, Object> delegate, 
      Map<String, Transformer<Object, Object>> transformers) {
    this.delegate = delegate;
    this.transformers = transformers;
  }

  @Override
  public Set<Map.Entry<String, Object>> entrySet() {
    if (entrySet == null) {
      lock.lock();
      try {
        if (entrySet == null) {
          entrySet = new LinkedHashSet<>();
          for (String key : delegate.keySet()) {
            Transformer<Object, Object> transformer = transformers.get(key);
            Object s = delegate.get(key);            
            Object t = transform(s, transformer);
            entrySet.add(new AbstractMap.SimpleEntry<String, Object>(key, t));
          }
        }
      }
      finally {
        lock.unlock();
      }
    }
    return entrySet;
  }

  private Object transform(Object s, Transformer<Object, Object> transformer) {
    if (transformer == null) return s;
    if (s instanceof Iterable) {
      List<Object> t = new LinkedList<>();
      for (Object obj : (Iterable<?>) s) {
        t.add(transformer.transform(obj));
      }
      return t;
    }
    else {
      return transformer.transform(s);
    }
  }

}
