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
package org.soulwing.cas.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.junit.Test;

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

  @Test
  public void testFindParameterWhenOnlyParameter() throws Exception {
    assertThat(QueryUtil.findParameter("name", "name=value"),
        is(equalTo("value")));
  }

  @Test
  public void testFindParameterWhenFirst() throws Exception {
    assertThat(QueryUtil.findParameter("name", "name=value&other=other"),
        is(equalTo("value")));
  }

  @Test
  public void testFindParameterWhenMiddle() throws Exception {
    assertThat(QueryUtil.findParameter("name", "other=other&name=value&other=other"),
        is(equalTo("value")));
  }

  @Test
  public void testFindParameterWhenLast() throws Exception {
    assertThat(QueryUtil.findParameter("name", "other=other&name=value"),
        is(equalTo("value")));
  }

  @Test
  public void testFindParameterWhenNonePresent() throws Exception {
    assertThat(QueryUtil.findParameter("name", ""),
        is(nullValue()));
  }

  @Test
  public void testFindParameterWhenNotPresent() throws Exception {
    assertThat(QueryUtil.findParameter("name", "other=value"),
        is(nullValue()));
  }

  @Test
  public void testFindParameterWhenSuffixMatchButNotPresent() throws Exception {
    assertThat(QueryUtil.findParameter("name", "other=value&myname=value"),
        is(nullValue()));
  }

  @Test
  public void testFindParameterWhenHasNoValue() throws Exception {
    assertThat(QueryUtil.findParameter("name", "name&other=value"),
        is(nullValue()));
  }
  
}
