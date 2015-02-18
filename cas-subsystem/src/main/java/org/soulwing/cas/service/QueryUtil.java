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


/**
 * Static utility methods.
 *
 * @author Carl Harris
 */
class QueryUtil {

  /**
   * Remove all protocol-related query parameters in the given query string.
   * @param protocol the subject protocol
   * @param query the subject query string
   * @return {@code query} with all protocol-related parameters removed
   */
  public static String removeProtocolParameters(
      AuthenticationProtocol protocol, String query) {
    query = removeEachParameter(protocol.getTicketParameterName(), query);
    query = removeEachParameter(protocol.getServiceParameterName(), query);
    return query;
  }

  /**
   * Removes all instances of a parameter name (and corresponding value) from 
   * a query string.
   * @param param name of the parameter to remove.
   * @param query the subject query
   * @return query with the first instance of {@code param} removed from it 
   */
  public static String removeEachParameter(String param, String query) {
    String result = QueryUtil.removeParameter(param, query);
    while (!result.equals(query)) {
      query = result;
      result = QueryUtil.removeParameter(param, query);
    }
    return result;
  }
  
  /**
   * Removes a single instance of a parameter name-value pair from a query 
   * string.
   * @param param name of the parameter to remove.
   * @param query the subject query
   * @return query with the first instance of {@code param} removed from it 
   */
  public static String removeParameter(String param, String query) {
    String token = param + "=";
    int i = query.indexOf(token);
    while (i > 0 && query.charAt(i - 1) != '&') {
      i = query.indexOf(token, i + 1);
    }
    if (i == -1) return query;    
    int j = query.indexOf('&', i);
    if (j == -1) {
      return query.substring(0, i == 0 ? i : i - 1);
    }
    return query.substring(0, i) + query.substring(j + 1);
  }

  /**
   * Finds a single instance of a parameter name-value pair in a query 
   * string.
   * @param param name of the parameter to find.
   * @param query the subject query
   * @return value associated with {@code param} if found, or {@code null} if
   *    {@code param} does not appear in {@code query}
   */
  public static String findParameter(String param, String query) {
    String token = param + "=";
    int i = query.indexOf(token);
    if (i == -1) return null;
    
    while (i > 0 && query.charAt(i - 1) != '&') {
      i = query.indexOf(token, i + 1);
    }
    if (i == -1) return null;
    
    int j = query.indexOf('&', i);
    if (j == -1) {
      j = query.length();
    }
    
    return query.substring(query.indexOf('=', i) + 1, j);
  }

}
