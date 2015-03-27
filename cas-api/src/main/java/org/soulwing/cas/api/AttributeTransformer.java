/*
 * File created on Feb 24, 2015 
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
package org.soulwing.cas.api;

import java.util.Properties;

/**
 * A plugin module that transforms SAML response attributes.
 * <p>
 * A concrete implementation of this class must be stateless and thread-safe.
 *
 * @author Carl Harris
 */
public abstract class AttributeTransformer<S, T> implements Transformer<S, T> {

  /**
   * Initializes this transformer prior to use.
   * <p>
   * After an concrete instance is constructed, this method is invoked exactly
   * once to allow the transformer to configure itself according the given 
   * options.  Subsequent to initialization, the transformer may be called
   * on its {@link #transform(Object)} method at any time.
   * <p>
   * The default implementation does nothing. 
   * 
   * @param properties configuration properties
   */
  public void initialize(Properties properties) {    
  }  

}
