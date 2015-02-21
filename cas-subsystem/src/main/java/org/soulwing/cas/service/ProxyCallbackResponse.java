/*
 * File created on Feb 21, 2015 
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

/**
 * An exception thrown to indicate that a proxy callback handler failed to
 * handle a callback.
 *
 * @author Carl Harris
 */
public class ProxyCallbackResponse {

  private final int status;
  private final String message;
  
  public ProxyCallbackResponse() {
    this(200, "");
  }
  
  /** 
   * Constructs a new instance.
   * @param status HTTP status code for the callback response
   * @param message message for the callback response body
   */
  public ProxyCallbackResponse(int status, String message) {
    this.status = status;
    this.message = message;
  }

  /**
   * Gets the {@code status} property.
   * @return property value
   */
  public int getStatus() {
    return status;
  }

  /**
   * Gets the {@code message} property.
   * @return property value
   */
  public String getMessage() {
    return message;
  }

}
