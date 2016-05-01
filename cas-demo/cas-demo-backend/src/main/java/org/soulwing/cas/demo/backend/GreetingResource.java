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
package org.soulwing.cas.demo.backend;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * A REST resource that produces personalized greetings.
 *
 * @author Carl Harris
 */
@Path("/greeting")
public class GreetingResource {

  @Inject
  protected GreetingService greetingService;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response generateGreeting(@QueryParam("name") String name) {
    return Response.ok(greetingService.generateGreeting(name)).build();
  }

}
