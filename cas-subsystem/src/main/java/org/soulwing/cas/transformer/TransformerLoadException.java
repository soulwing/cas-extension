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

/**
 * An exception thrown if an error occurs in loading a transformer.
 *
 * @author Carl Harris
 */
public class TransformerLoadException extends Exception {

  private static final long serialVersionUID = 6275559131401712750L;

  public TransformerLoadException(String message) {
    super(message);    
  }

  public TransformerLoadException(String message, Throwable cause) {
    super(message, cause);
  }

}

