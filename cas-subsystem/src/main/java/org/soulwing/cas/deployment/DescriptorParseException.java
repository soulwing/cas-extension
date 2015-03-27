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

/**
 * An exception thrown to indicate an error in parsing the CAS deployment
 * descriptor.
 *
 * @author Carl Harris
 */
public class DescriptorParseException extends Exception {

  private static final long serialVersionUID = -1030787506635340100L;

  /**
   * Constructs a new instance.
   * @param message
   * @param cause
   */
  public DescriptorParseException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructs a new instance.
   * @param message
   */
  public DescriptorParseException(String message) {
    super(message);
  }

  /**
   * Constructs a new instance.
   * @param cause
   */
  public DescriptorParseException(Throwable cause) {
    super(cause);
  }

}
