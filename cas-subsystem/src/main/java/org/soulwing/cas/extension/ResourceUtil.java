/*
 * File created on Dec 18, 2014 
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
package org.soulwing.cas.extension;

import org.jboss.as.controller.descriptions.StandardResourceDescriptionResolver;

/**
 * DESCRIBE THE TYPE HERE.
 *
 * @author Carl Harris
 */
public class ResourceUtil {

  static final String RESOURCE_NAME = String.format("%s.LocalDescriptions",
      SubsystemExtension.class.getPackage().getName());

  public static StandardResourceDescriptionResolver getResolver(
      String... segments) {
    StringBuilder sb = new StringBuilder();
    sb.append(Names.SUBSYSTEM_NAME);
    for (String segment : segments) {
      sb.append('.');
      sb.append(segment);
    }

    String key = sb.toString();
    return new StandardResourceDescriptionResolver(key, 
        RESOURCE_NAME, SubsystemExtension.class.getClassLoader(), true, false);
  }

  public static String spinalToCamel(String spinal) {
    StringBuilder sb = new StringBuilder();
    boolean upper = false;
    for (int i = 0; i < spinal.length(); i++) {
      char c = spinal.charAt(i);
      if (c == '-') {
        upper = true;
      }
      else if (upper) {
        sb.append(Character.toUpperCase(c));
        upper = false;
      }
      else {
        sb.append(c);
      }
    }
    return sb.toString();
  }

}
