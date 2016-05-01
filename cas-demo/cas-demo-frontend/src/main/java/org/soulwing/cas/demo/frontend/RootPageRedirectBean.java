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

import javax.inject.Named;
import javax.enterprise.context.RequestScoped;

/**
 * A bean that redirects from the root page of the application to the
 * greeting form.
 *
 * @author Carl Harris
 */
@Named
@RequestScoped
public class RootPageRedirectBean {

  public String redirect() {
    return "greeting";
  }

}
