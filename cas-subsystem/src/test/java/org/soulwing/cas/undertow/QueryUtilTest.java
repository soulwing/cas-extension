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
package org.soulwing.cas.undertow;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.jmock.Expectations.*;
import static org.junit.Assert.*;

import org.junit.Test;
import org.soulwing.cas.undertow.QueryUtil;

/**
 * Unit tests for {@link QueryUtil}.
 *
 * @author Carl Harris
 */
public class QueryUtilTest {

  @Test
  public void testRemoveParameterWhenQueryEmpty() throws Exception {
    assertThat(QueryUtil.removeParameter("name", ""), 
        is(""));
  }

  @Test
  public void testRemoveParameterWhenNoOtherParams() throws Exception {
    assertThat(QueryUtil.removeParameter("name", "name=value"), 
        is(""));
  }

  @Test
  public void testRemoveParameterWhenFirstParam() throws Exception {
    assertThat(QueryUtil.removeParameter("name1", "name1=value1&name2=value2"), 
        is("name2=value2"));
  }

  @Test
  public void testRemoveParameterWhenLastParam() throws Exception {
    assertThat(QueryUtil.removeParameter("name2", "name1=value1&name2=value2"), 
        is("name1=value1"));
  }
  
  @Test
  public void testRemoveParameterInMiddle() throws Exception {
    assertThat(QueryUtil.removeParameter
        ("name2", "name1=value1&name2=value2&name3=value3"), 
        is("name1=value1&name3=value3"));
  }

  @Test
  public void testRemoveEachParameter() throws Exception {
    assertThat(QueryUtil.removeEachParameter("name", "name=value1&name=value2"),
        is(""));
  }

}
