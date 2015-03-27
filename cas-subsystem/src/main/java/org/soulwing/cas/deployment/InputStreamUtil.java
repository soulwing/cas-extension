/*
 * File created on Feb 10, 2015 
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
package org.soulwing.cas.deployment;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Static utility methods.
 *
 * @author Carl Harris
 */
class InputStreamUtil {

  public static boolean isEmptyStream(InputStream inputStream) 
      throws IOException {
    Reader reader = null;
    try {
      reader = new InputStreamReader(inputStream);
      char[] buf = new char[8192];
      int numRead = reader.read(buf);
      while (numRead != -1) {
        for (int i = 0; i < numRead; i++) {
          if (!Character.isWhitespace(buf[i])) return false;
        }
        numRead = reader.read(buf);
      }
      return true;
    }
    finally {
      if (reader != null) {
        try {
          reader.close();
        }
        catch (IOException ex) {
          ex.printStackTrace(System.err);
        }
      }
    }
  }

}
