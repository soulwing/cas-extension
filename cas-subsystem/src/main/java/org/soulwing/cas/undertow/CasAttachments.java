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

import io.undertow.util.AttachmentKey;

import org.soulwing.cas.service.Authenticator;

/**
 * Attachment keys used by CAS.
 *
 * @author Carl Harris
 */
interface CasAttachments {

  AttachmentKey<IdentityAssertionCredential> CREDENTIAL_KEY =
      AttachmentKey.create(IdentityAssertionCredential.class);

  AttachmentKey<Boolean> POST_AUTH_REDIRECT_KEY =
      AttachmentKey.create(Boolean.class);
  
  AttachmentKey<Integer> AUTH_FAILED_KEY =
      AttachmentKey.create(Integer.class);
  
  AttachmentKey<Authenticator> AUTHENTICATOR_KEY =
      AttachmentKey.create(Authenticator.class);

}
