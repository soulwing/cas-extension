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

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * A {@link GreetingService} implemented as a client to a backend REST API.
 * <p>
 * The rationale for making this a stateless EJB is based on the fact that
 * JAX-RS {@link Client} objects are heavyweight and not necessarily safe for
 * use by multiple current request threads.  The container will create instances
 * of our service bean and manage concurrency such that each instance of the
 * bean is used by exactly one request thread at any time.  If there isn't an
 * instance available for a request, and the bean pool limit hasn't been reached,
 * the container will create another instance (along with an associated client
 * object).  Otherwise, an incoming request will wait for a bean instance to
 * become available.
 *
 *
 * @author Carl Harris
 */
@Stateless
public class RemoteGreetingService implements GreetingService {

  @Resource(lookup = "serviceUrl")
  protected String serviceUrl;

  @Inject
  protected Client client;

  @Override
  public GreetingModel generateGreeting(String name) {
    Response response = client.target(serviceUrl).path("/greeting")
        .queryParam("name", name)
        .request()
        .accept(MediaType.APPLICATION_JSON)
        .buildGet().invoke();
    try {
      if (response.getStatus() != 200) {
        throw new GreetingServiceException(response.getStatus());
      }
      return response.readEntity(GreetingModel.class);
    }
    finally {
      response.close();
    }
  }

}
