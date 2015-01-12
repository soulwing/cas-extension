/*
 * File created on Dec 22, 2014 
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
package org.soulwing.cas.deployment;

import java.io.InputStream;

/**
 * A parser for the CAS web application descriptor.
 *
 * @author Carl Harris
 */
interface DescriptorParser {

  /**
   * Parses the descriptor from the given input stream.
   * @param inputStream input stream from which descriptor will be read
   * @return application configuration obtained from the descriptor
   * @throws DescriptorParseException if an error occurs in parsing the
   *    descriptor
   */
  AppConfiguration parse(InputStream inputStream) 
      throws DescriptorParseException;
  
  /**
   * Pushes a reader onto the top of the parser's stack.
   * @param reader the reader to push
   */
  void push(DescriptorReader reader);
  
  /**
   * Pops a reader from the top of the parser's stack.
   */
  void pop();
  
  /**
   * Stops the parse with out flagging an error.
   */
  void stop();
  
  
}
