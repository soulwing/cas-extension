/*
 * File created on Jun 23, 2015
 *
 * Copyright (c) 2015 Carl Harris, Jr
 * and others as noted
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.soulwing.cas.demo.frontend;

import javax.inject.Inject;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;

/**
 * A backing bean for the result form.
 *
 * @author Carl Harris
 */
@Named
@RequestScoped
public class GreetingFormBean {

  @Inject
  protected GreetingService greetingService;

  private String name;
  private GreetingModel result;

  /**
   * Gets the {@code name} property.
   * @return property value
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the {@code name} property.
   * @param name the property value to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the {@code result} property.
   * @return property value
   */
  public GreetingModel getResult() {
    return result;
  }

  /**
   * Action method that handles form submission.
   * @return outcome id
   */
  public String submit() {
    result = greetingService.generateGreeting(name);
    return "success";
  }

}
