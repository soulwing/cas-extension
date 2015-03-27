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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link InputStreamUtil}.
 *
 * @author Carl Harris
 */
public class InputStreamUtilTest {

  private File file;
  
  @Before
  public void setUp() throws Exception {
    file = File.createTempFile("FileUtil", ".dat");
    file.deleteOnExit();
  }

  @After
  public void tearDown() throws Exception {
    file.delete();
  }
  
  @Test
  public void testZeroLengthFile() throws Exception {
    validateEmptyState(true);
  }

  @Test
  public void testWhitespaceFile() throws Exception {
    PrintWriter writer = new PrintWriter(file);
    writer.println(" \t\r\n");
    char[] buf = new char[16384];
    for (int i = 0; i < 16384; i++) {
      buf[i] = ' ';
    }
    writer.write(buf);
    writer.close();
    validateEmptyState(true);
  }

  @Test
  public void testNonEmptyFile() throws Exception {
    PrintWriter writer = new PrintWriter(file);
    writer.print(".");
    writer.close();
    validateEmptyState(false);
  }
  
  private void validateEmptyState(boolean state) throws IOException {
    InputStream inputStream = new FileInputStream(file);
    try {
      assertThat(InputStreamUtil.isEmptyStream(inputStream), is(state));
    }
    finally {
      inputStream.close();
    }    
  }
}
