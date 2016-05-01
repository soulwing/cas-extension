cas-extension
=============

[![Build Status](https://travis-ci.org/soulwing/cas-extension.svg?branch=master)](https://travis-ci.org/soulwing/cas-extension)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.soulwing.cas/cas-extension/badge.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3Aorg.soulwing.cas%20a%3Acas-extension*)

A Wildfly extension module that supports Java web application authentication
using the Central Authentication Service (CAS) protocol.  

This extension provides container-managed support for CAS, such that web 
applications can use the Java EE standard security mechanisms; for example, 
declarative roles and constraints in `web.xml` and the `@RolesAllowed` bean
annotation. 

This extension consists of two major components.  The first is a Wildfly 
subsystem that monitors web application deployment events and attaches support 
for the CAS authentication protocol to those deployments that include a CAS
deployment descriptor.  

The second major component of the extension is a standard JAAS `LoginModule` 
that participates in Wildfly's security subsystem.  The login module 
provides the mechanism for communicating the result of a CAS authentication 
exchange into Wildfly's built-in security stack.  In addition to providing 
support for using SAML attributes as user roles for authorization, the login 
module also allows the use of CAS in combination with Wildfly's built-in 
features such as LDAP-based authorization, role name mapping, etc.

This extension uses the JASIG Java CAS Client library to perform the CAS 
protocol operations, and exposes most of the features of the client through 
configuration attributes expressed as part of the Wildfly management model.

Installation
------------

The module and its dependencies must be installed in the `modules` directory 
of your Wildfly server.  Thanks to Wildfly's modular design, the installed 
modules remain isolated in your server's configuration, and will never be seen 
by applications that do not require CAS support.  Moreover, the library
components needed by this extension will not appear on your 
application's class loader, avoiding any potential for conflict.

### From Source

Clone this repository and then run Maven at the top level of the source tree.
```
mvn -P wildfly8 clean install
```

Install the extension.
```
tar -C ${WILDFLY_HOME} -zxpvf cas-modules/target/cas-modules-{VERSION}-modules.tar.gz
```

### Using a Pre-Built Binary

If you don't want to build it yourself, you can download a current binary:

* [cas-modules-1.0.5-modules.tar.gz] (https://oss.sonatype.org/content/groups/public/org/soulwing/cas/cas-modules/1.0.5/cas-modules-1.0.5-modules.tar.gz) ([PGP] (https://oss.sonatype.org/content/groups/public/org/soulwing/cas/cas-modules/1.0.5/cas-modules-1.0.5-modules.tar.gz.asc) [SHA1] (https://oss.sonatype.org/content/groups/public/org/soulwing/cas/cas-modules/1.0.5/cas-modules-1.0.5-modules.tar.gz.sha1) [MD5] (https://oss.sonatype.org/content/groups/public/org/soulwing/cas/cas-modules/1.0.5/cas-modules-1.0.5-modules.tar.gz.md5))
* [cas-modules-1.0.5-modules.zip] 
(https://oss.sonatype.org/content/groups/public/org/soulwing/cas/cas-modules/1.0.5/cas-modules-1.0.5-modules.zip) ([PGP] (https://oss.sonatype.org/content/groups/public/org/soulwing/cas/cas-modules/1.0.5/cas-modules-1.0.5-modules.zip.asc) [SHA1] (https://oss.sonatype.org/content/groups/public/org/soulwing/cas/cas-modules/1.0.5/cas-modules-1.0.5-modules.zip.sha1) [MD5] (https://oss.sonatype.org/content/groups/public/org/soulwing/cas/cas-modules/1.0.5/cas-modules-1.0.5-modules.zip.md5))

Install the `tar.gz` binary using:

```
tar -C ${WILDFLY_HOME} -xpvf cas-modules-1.0.5-modules.tar.gz
```

Install the `zip` binary using:

```
unzip cas-modules-1.0.5-modules.zip -d ${WILDFLY_HOME}
```

Configuration
-------------

The recommended approach to configuring the CAS extension is to use the 
Wildfly (JBoss) command line interface.  See the Wildfly documentation for 
instructions on how to run the command line interface.  The remainder of this 
document provides commands that are used at the CLI prompt.

The following CLI commands are used to create the subsystem resource and a 
Wildfly security domain for applications that use CAS authentication.  Note
that the last command given here is `reload`.  The container must be reloaded
to activate these components.  

```
batch
/extension=org.soulwing.cas:add(module=org.soulwing.cas)
/subsystem=cas:add
/subsystem=security/security-domain=cas:add
/subsystem=security/security-domain=cas/authentication=classic:add
/subsystem=security/security-domain=cas/authentication=classic/login-module=IdentityAssertion:add(module=org.soulwing.cas, code=org.soulwing.cas.jaas.IdentityAssertionLoginModule, flag=required)
run-batch
reload
```

Once the CAS subsystem has been added to your Wildfly configuration, it is not
necessary to reload (e.g. using the CLI) or restart Wildfly in order to change
the CAS configuration.  See [Avoiding the Need to Restart/Reload] 
(#avoiding-the-need-to-restartreload) for more information.


CAS Configuration Profiles
--------------------------

Within the CAS subsystem you can create one or more configuration profiles.
Each profile fully describes the properties needed for the CAS subsystem to
exchange authentication requests with a given CAS server.  You might use 
multiple profiles to differentiate servers for *production* versus 
*development* or to distinguish different authentication realms in a hosted
environment.

By convention, a profile named *default* is used for application deployments 
that don't specify a particular configuration profile.  See [Application 
Deployment] (#application-deployment) to learn how an application can specify a 
CAS configuration profile.

The following CLI command creates the default configuration profile using the 
CAS 2 protocol with a CAS server named cas.example.org.

```
/subsystem=cas/cas-profile=default:add(server-url=https://cas.example.org, service-url=https://webapp.example.org)
```

Additional profiles can be created using the same basic syntax, substituting 
*cas-profile=default* with *cas-profile=some-other-name*.

### Profile Attributes

The complete set of configurable profile attributes is described below.  You
can also view the description of these attributes using the 
`:read-resource-description ` CLI operation on a CAS configuration profile.

* *server-url* -- URL for the CAS server
* *service-url* -- URL for the application/service/container
* *protocol* -- authentication protocol; CAS-1.0, CAS-2.0 (default), SAML-1.1
* *proxy-callback-enabled* -- flag indicating whether a proxy granting ticket
  should be requested with each ticket validation (CAS-2.0 only)
* *proxy-callback-path* -- URL path for proxy granting ticket callbacks
  (CAS-2.0 only); default is `/casProxyCallback`
* *accept-any-proxy* -- flag indicating whether any proxy is acceptable; 
  true or false (default)
* *allow-empty-proxy-chain* -- flag indicating whether an empty proxy chain
  should be allowed; true or false (default)
* *renew* -- flag indicating whether users should be forced to 
  re-authenticate before being admitted to CAS-enabled applications using this
  profile; true or false (default)
* *clock-skew-tolerance* -- indicates the amount of clock skew to tolerate 
  when evaluating the validity of a SAML protocol response; milliseconds,  
  default is 1000 ms
* *post-auth-redirect* -- flag indicating whether, after a successful 
  authentication exchange, a redirect should be sent for the original
  request URL with all of the protocol query parameters removed; 
  true (default) or false
* *security-realm* -- (Wildfly 9 only) specifies the name of a Security Realm
  to use to obtain SSL truststore and/or keystore configuration to use when
  communicating with the CAS server

### Profile Sub-Resources

A configuration profile supports three kinds of sub-resources:

* *allowed-proxy-chain* -- specifies a named chain of allowed proxy URLs to 
  be used when validating proxy authentication tickets; you can define as 
  many proxy chains as needed; see [Using Proxy Authentication] 
  (#using-proxy-authentication)
* *hostname-verifier* -- (Wildfly 9 only) specifies a hostname verifier to use
  when communicating with the CAS server (often needed when the CAS server is
  using a self-signed certificate)
* *attribute-transform* -- specifies one or more transform functions to 
  apply to SAML response attributes; see [Using SAML] (#using-saml)

#### Configuring a Hostname Verifier

When creating an HTTPS connection to the CAS server, the underlying SSL 
support verifies that the name of the host matches a name presented on
the certificate.  When connecting to a CAS server whose certificate does
not match the host name, you can use a hostname verifier to allow the
connection.  

Three types of hostname verification are supported by the CAS subsystem:

* *allow-any* -- no verification is performed; any hostname is allowed
* *white-list* -- the server hostname is compared to the entries in a 
  configured list of hostnames to allow
* *pattern-match* -- the server hostname is matched to one of the entries
  in a configured list of regular expression patterns

To add any of the supported verifiers to the *default* profile, use *one* of
 the following CLI commands:

```
/subsystem=cas/cas-profile=default/hostname-verifier=allow-any:add{allow-resource-service-restart=true}
/subsystem=cas/cas-profile=default/hostname-verifier=white-list:add(hosts=[cas.example.org]){allow-resource-service-restart=true}
/subsystem=cas/cas-profile=default/hostname-verifier=pattern-match:add(hosts=[".*\.example.org$", "^localhost$"]){allow-resource-service-restart=true}

```

To remove a configured hostname verifier, specify the verifier type in the
remove command.  For example, to remove the *pattern-match* verifier, use
this command:

```
/subsystem=cas/cas-profile=default/hostname-verifier=pattern-match:remove{allow-resource-service-restart=true}
```

Using SAML
----------

Recent versions of the CAS server support authentication using SAML.  One of
the most significant advantages of using SAML is that the response payload
can include attributes that further describe the authenticated user.  These
attribute values are often used as roles authorizing the user to perform
certain kinds of operations in an application.

The JAAS login module provided with this extension includes support for using
SAML response attributes as role names.  In order to take advantage of this 
support you must do both of the following:

1. Use a configuration profile that specifies the SAML-1.1 protocol.
2. Configure the login module with the names of the SAML response attributes
   whose values are to be used as role names.
   
   
### Specifying the SAML-1.1 Protocol

You can modify the configuration of an existing profile using the 
`:write-attribute` CLI operation on the profile:

```
/subsystem=cas/cas-profile=default:write-attribute(name=protocol, value=SAML-1.1){allow-resource-service-restart=true}
```

Of course, you can also specify the protocol attribute when creating a 
configuration profile:

```
/subsystem=cas/cas-profile=default-saml:add(protocol=SAML-1.1, server-url=...)
```

### Specifying Role Attributes

The *role-attributes* module option is used to specify a list of 
attribute names whose values will be be mapped to role names by the 
*IdentityAssertion* login module.  In this example, the multi-valued attributes 
*eduPersonAffiliation* and *groupMembership* are assumed to contain the names
of roles that should be granted to the authenticated user.

You can modify the configuration of the *IdentityAssertion* module in the
default CAS security domain using the `:write-attribute` CLI command:

```
/subsystem=security/security-domain=cas/authentication=classic/login-module=IdentityAssertion:write-attribute(name="module-options", value={ role-attributes="eduPersonAffiliation, groupMembership" }){allow-resource-service-restart=true}
```

Of course, you can also specify the *role-attributes* module option when
creating a security domain:

```
/subsystem=security/security-domain=cas/authentication=classic/login-module=IdentityAssertion:add(module=org.soulwing.cas, code=org.soulwing.cas.jaas.IdentityAssertionLoginModule, flag=required, module-options={ role-attributes="eduPersonAffiliation, groupMembership" })
```

### Transforming Role Attribute Values

You may wish to apply one or more transformation functions to attribute values
returned in the SAML payload.  For example, group membership attributes in
the response may be LDAP distinguished names, but you may prefer to use just
the common name component of the group name as a role name.  Or perhaps you
wish to perform a regular expression pattern substitution on returned 
attribute values. You can configure `attribute-transform` resources in a CAS
profile in order to meet these needs.

Each configured attribute transform applies to a specific named SAML response
attribute.  Each defined transform can specify one or more transformation
functions to apply to each value of the given attribute name.  The extension
provides three built-in transformers (see [Built-In Transformers] 
(#built-in-transformers)).  Additionally, you may define your own transformers; 
see [Custom Transformers] (#custom-transformers).

Suppose you wish to apply a transform to a SAML response attribute
named `groupMembership` to replace an LDAP distinguished name with its common
name component. You would start by creating the attribute transform
resource in the corresponding CAS profile.  For example:

```
/subsystem=cas/cas-profile=default/attribute-transform=groupMembership:add
```

After creating the attribute transform resource, you would then add the 
`DistinguishedToSimpleName` transformer to it.  Suppose that each LDAP
group name contains a `GID` attribute that specifies the simple name of 
the group.  You would then create the transformer using the following
command:

```
/subsystem=cas/cas-profile=default/attribute-transform=groupMembership/transformer=DistinguishedToSimpleName:add(options={name-component=GID})
```

You can apply more than one transformer to a given attribute.  For example,
to perform a pattern replacement and flatten case for the `eduPersonAffiliation`
attribute, we could define an attribute transform as follows:

```
/subsystem=cas/cas-profile=default/attribute-transform=eduPersonAffiliation:add
/subsystem=cas/cas-profile=default/attribute-transform=eduPersonAffiliation/transformer=ReplacePattern:add(options={pattern="^VT-", replacement=""})
/subsystem=cas/cas-profile=default/attribute-transform=eduPersonAffiliation/transformer=FlattenCase:add
```

#### Built-In Transformers

##### ReplacePattern

The `ReplacePattern` transformer uses a regular expression pattern to match
text in an attribute value and replace it.

|  Option     | Description                                   | Default      |
| ----------- |---------------------------------------------- | ------------ |
| pattern     | Specifies the regular expression to match (the supported syntax is the same as that of `java.util.regex.Pattern`) | (none)       |
| replacement | Specifies the replacement text; use $n (n = 1, 2, ...) to refer to groups specified in the pattern | empty string |
| replace-all | Specifies that all instances of the given pattern should be replaced | false        | 

##### FlattenCase

The `FlattenCase` transformer flattens character case in an attribute value.

|  Option     | Description                                   | Default      |
| ----------- |---------------------------------------------- | ------------ |
| use-upper-case | Specifies that upper case should be used | false |
 
##### DistinguishedToSimpleName

The `DistinguishedToSimpleName` transformer replaces a LDAP distinguished name
with one of its component names.

|  Option     | Description                                   | Default      |
| ----------- |---------------------------------------------- | ------------ |
| name-component | Specifies the name component to extract from a distinguished name | CN |
| fail-on-error | Flag indicating whether invalid LDAP names should throw an error | false |

#### Custom Transformers

You can define your own transformers by extending the `AttributeTransformer`
class in the `cas-api` Maven module.  Install your custom transformer as a
Wildfly module that depends on the `org.soulwing.cas.api` module.  You can
then add your transformer to an attribute transform, by specifying the `code`
and `module` parameters to the transformer add operation.  

For example, suppose that you've implemented a transformer whose fully 
qualified class name is `org.example.FooTransformer` and installed it 
in a Wildfly module named `org.foo.transformer`.  You could then add it
to an attribute transform as follows:

```
/subsystem=cas/cas-profile=default/attribute-transform=someAttributeName/transformer=Foo:add(code=org.example.FooTransformer, module=org.example.transformer, options={...})
```

Options specified in the add operation are passed to your transformer's
`initialize` method.

The name of the transformer (specified as `Foo` in this example) is not 
significant, and could be anything that would be meaningful to someone looking
at the transform configuration.
 

Delegating Authorization to a Security Realm
--------------------------------------------

Security Realms are a Wildfly management concept that is intended to simplify
the configuration needed to manage users and roles.  A security realm can
perform password-based, certificate-based, or other authentication, and can 
support role-based authorization using simple properties files or custom
plugins.

You can configure the *security domain* used for CAS such that it delegates
authorization to a *security realm*.  This can be done in addition to (or as 
an alternative to) using SAML attributes as roles used for authorization.

Assuming that you have already configured the your `cas` security domain,
you can reconfigure the domain to support realm delegation as follows:

```
batch
/subsystem=security/security-domain=cas/authentication=classic/login-module=IdentityAssertion:remove
/subsystem=security/security-domain=cas/authentication=classic/login-module=DelegatingIdentityAssertion:add(code=org.soulwing.cas.jaas.DelegatingIdentityAssertionLoginModule, module=org.soulwing.cas, flag=required)
run-batch --headers={allow-resource-service-restart=true}
```

By default, this configuration delegates to the security realm named
`ApplicationRealm`.  If you wish to use a different realm (perhaps creating
your own realm for this purpose) you can specify the `realm` module option.
The following command changes the delegate realm name to 
`CasAuthorizationRealm`.

```
/subsystem=security/security-domain=cas/authentication=classic/login-module=DelegatingIdentityAssertion:write-attribute(module-options={realm:CasAuthorizationRealm, role-attributes=...}){allow-resource-service-restart=true}
```

Note that you must also specify the `role-attributes` module option (as
previously described) if you wish to use SAML attributes as authorization
roles.

Of course, you can also specify the `module-options` attribute when adding
the `DelegatingIdentityAssertion`.


Application Deployment
----------------------

Web applications indicate their intent to use CAS authentication by means of a 
deployment descriptor named `/WEB-INF/cas.xml`.  The deployment descriptor
identifies the CAS configuration profile that will be used by the application.

```
<?xml version="1.0" encoding="UTF-8"?>
<cas xmlns="urn:soulwing.org:cas:1.0">
  <profile>cas-prod</profile>
</cas>
```

The CAS deployment descriptor can be an empty file (or an empty root element), 
indicating that CAS authentication should be enabled using the profile named 
*default*.

In addition to the CAS deployment descriptor, the application must specify 
the appropriate CAS-enabled security domain in the `/WEB-INF/jboss-web.xml` 
deployment descriptor.

```
<?xml version="1.0" encoding="UTF-8"?>
<jboss-web 
  xmlns="http://www.jboss.com/xml/ns/javaee" 
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://www.jboss.com/xml/ns/javaee 
  http://www.jboss.org/j2ee/schema/jboss-web_8_0.xsd"
  version="8.0">
  
  <security-domain>cas</security-domain>
  
</jboss-web>
```

Note that the *cas* security domain specified here was created in an earlier 
configuration step.  If you have created more than one security domain for 
CAS, specify the appropriate domain name here.

### Using the Application API

In some web applications, it may be desirable to have direct access to the
`UserPrincipal` created by the CAS subsystem.  For example, in applications 
that take responsibility for access control using a framework like Spring 
Security, or applications that use CAS proxy authentication to gain access 
to backend services.

Using the application API provided by the CAS subsystem, you can cast the
return value of `javax.servlet.http.HttpServletRequest.getUserPrincipal` to
the `UserPrincipal` object provided in the `cas-api` module.  This principal
includes methods to get the attributes associated with the user (e.g. from 
a SAML authentication response) as well as a method to obtain a proxy
authentication ticket.

```
public interface UserPrincipal extends Principal, Serializable {

  /**
   * Gets the map of attribute name-value pairs that further describe the
   * user.
   * @return attribute map
   */
  Map<String, Object> getAttributes();
  
  /**
   * Generates a proxy ticket for use in accessing another service.
   * @param service name (URL) of the service to access
   * @return ticket that may be used to authenticate access by this user
   *    to the given {@code service}
   * @throws IllegalStateException if no proxy granting ticket is available.
   */
  String generateProxyTicket(String service) throws IllegalStateException;
  
  /**
   * Gets the CAS logout URL.
   * @return URL to which the user can be redirected for CAS logout
   */
  String getLogoutUrl();

  /**
   * Gets the CAS logout URL that includes a reference back to an application
   * path that should be offered to the user as a post-logout action.
   * @param path application path (including the context path)
   * @return URL to which the user can be redirected for CAS logout
   */
  String generateLogoutUrl(String path);
  
  /**
   * Gets the underlying delegate principal.
   * <p>
   * The returned object is an instance of a class defined by the CAS client
   * implementation; e.g. for the JASIG CAS Client the returned object is
   * an instance of {@link org.jasig.cas.client.Authentication.AttributePrincipal}
   * @return delegate principal
   */
  Object getDelegate();
  
}
```

Assuming that your application uses a Maven build, you will need to add a 
compile-time dependency on the `cas-api` module, as shown below.  Note the
use of the `provided` scope, which makes the API classes available at 
compile time, but does not include the module in your application's. 
`/WEB-INF/lib`.

```
<dependency>
  <groupId>org.soulwing.cas</groupId>
  <artifactId>cas-api</artfiactId>
  <version>1.0.1</version>
  <scope>provided</scope>
</dependency>
```

In order to have access to this API at runtime, your CAS deployment descriptor
must include the `<add-api-dependencies/>` element, as shown below.  When this
element is present, the CAS subsystem will add the necessary classes to your
application's runtime class path.

```
<?xml version="1.0" encoding="UTF-8"?>
<cas xmlns="urn:soulwing.org:cas:1.0">
  ...
  <add-api-dependencies/>
  ...
</cas>
```

Using Proxy Authentication
--------------------------

CAS proxy authentication is designed to allow a front-end web application to
access back-end services on behalf of CAS-authenticated user.  The front-end
web application uses CAS to authenticate the user.  When it needs to access
a back-end service as the logged in user, it requests a proxy authentication
ticket for the user from the CAS server, and presents the proxy ticket to 
the back-end service.  The back-end service presents the ticket to the CAS 
server for validation, just as it would have done if the user had (or could)
access the service directly using a browser.

The extension provides full support for CAS proxy authentication, including
support for requesting a proxy granting ticket callback from the CAS server, 
requesting proxy authentication tickets from the CAS server, and validation 
of proxy authentication tickets presented to an application.  

### Requesting Proxy Granting Ticket Callbacks

> Note that **most CAS servers require the requesting application to be 
> explicitly authorized to use proxy authentication**.  Contact your CAS 
> server administrator before trying use this feature.

In order to request proxy authentication tickets from the CAS server, a
front-end application must request a *proxy granting ticket (PGT) callback* 
when redirecting the user to the CAS server for authentication.  If an 
authorized application requests a PGT callback, the CAS server sends
the proxy granting ticket to the application by means of an HTTP request to
the URL specified by the application.  

PGT callbacks are enabled by setting the `proxy-callback-enabled` profile 
attribute to `true`.  For example, to enable PGT callbacks in the *default* 
profile, use the following CLI command:

```
/subsystem=cas/cas-profile=default:write-attribute(name=proxy-callback-enabled, value=true)
```

You can also set this attribute when adding a new profile; it is set to 
*false* by default.

The URL passed to the CAS server for the PGT callback is derived from the
context path of a deployed CAS-enabled application.  Suppose we have deployed
a CAS-enabled web application with a context path of `/myapp`.  Suppose that
the *service-url* attribute of the CAS profile used with this application is
`https://webapp.example.org`.  The default PGT callback URL that would be
derived for an authentication request to `/myapp` would then be:

```
https://webapp.example.org/myapp/casProxyCallback
```

The CAS extension in running in Wildfly listens for HTTP requests for this
URL to accept the proxy granting ticket from the CAS server on behalf of the
web application at `/myapp`.  Note that the application need not (and in 
fact cannot) handle these HTTP requests itself -- it is handled by the CAS
extension in a manner that is completely transparent to the application.  

If you wish to use something other than `/casProxyCallback` to distinguish 
PGT callback requests, configure the *proxy-callback-path* attribute in the
profile.  

### Requesting Proxy Authentication Tickets

> An application that wishes to request proxy authentication tickets from
> the CAS server must use a CAS profile in which PGT callbacks have been 
> enabled (as shown in the previous section).  Moreover, the application's
> CAS deployment descriptor must include the `<add-api-dependencies/>` element
> to enable the necessary API (as discussed in [Using the Application API]).

When an application needs a proxy authentication ticket to access a backend
service for a given user, it gets the authenticated principal from the 
associated `HttpServletRequest`, casts the principal to 
`org.soulwing.cas.api.UserPrincipal` and invokes the `generateProxyTicket`
method to obtain a proxy ticket:

```
import javax.servlet.http.HttpServletRequest;
import org.soulwing.cas.api.UserPrincipal;
...
HttpServletRequest request = ...
String backendServiceUrl = ...

UserPrinicpal principal = (UserPrincipal) request.getUserPrincipal();
String ticket = principal.generateProxyTicket(backendServiceUrl); 
``` 

### Validating Proxy Authentication Tickets

When configuring a CAS profile for a backend service application that 
wishes to accept proxy authentication tickets, you must enable proxy ticket
validation by either adding one or more allowed proxy chains or by enabling
the *accept-any-proxy* profile attribute.

#### Configuring Allowed Proxy Chains

To add an allowed proxy chain to the *default* profile, use the following
CLI command.  The name of the chain added by this command is *example*.

```
/subsystem=cas/cas-profile=default/allowed-proxy-chain=example:add(proxies=[https://webapp.example.org])
```

This configuration allows a front-end application with the service URL
`https://webapp.example.org` to act as a proxy for users of a backend 
service associated with this profile.

To remove the *example* proxy chain from the *default* profile, use the
follwing CLI command.

```
/subsystem=cas/cas-profile=default/allowed-proxy-chain=example:remove
```

#### Accepting Any Proxy

In some configurations, you may wish to allow any application to proxy for
users of a given backend service.  In this case, enable the `accept-any-proxy`
profile attribute.  For example, to enable this attribute in the *default*
profile, use this CLI command:

```
/subsystem=cas/cas-profile=default:write-attribute(name=accept-any-proxy, value=true)
```

You can also enable this attribute when creating the profile.

#### Accepting Service Tickets and Proxy Tickets

Sometimes, a given application may provide both a browser-based user interface
as well as services to be used by other applications.  For example, a 
blogging application might provide a user interface to allow users to post
and read blog entries, while also providing a REST API to allow blog feeds 
to be integrated in the UI of other applications.  In this case, you may wish
to allow either ordinary service tickets or proxy tickets to be validated.

If you have configured the application's CAS profile with one or more allowed 
proxy chains, you will find that ordinary service tickets (i.e those issued 
when a user attempts to access the application directly) are not accepted.  
In this case, you must enable the *allow-empty-proxy-chain* profile
attribute.  For example, in the default profile we could configure this as
follows:

```
/subsystem=cas/cas-profile=default:write-attribute(name=allow-empty-proxy-chain, value=true)
```

With this configuration, in addition to accepting tickets whose chain of
proxies matches one of those explicitly allowed by the profile, tickets that
have no proxy chain (i.e. ordinary service tickets) will be accepted.

If the application's profile is configured to allow any proxy, then it is
not necessary to enable the `allow-empty-proxy-chain` profile attribute.


Avoiding the Need to Restart/Reload
-----------------------------------

Configuration profiles can be modified through the CLI without the need to 
reload (e.g. via the CLI) or restart Wildfly.  You simply need to include 
the `allow-resource-service-restart=true` header with the command(s) that 
should be applied without the need for reload.  Configuration profile changes
that include this header will be *immediately* applied to deployments that 
are attached to the subject profile.

For example, to change the CAS server URL without the need to reload, use
this command:

```
/subsystem=cas/cas-profile=default:write-attribute(name=server-url, value="https://cas2.example.org"){allow-resource-service-restart=true}
```

When changing more than one configuration characteristic, you may wish to use
the CLI's *batch* facility.  By executing commands in a batch, you ensure that
the updated configuration is not applied to any deployment attached to the
subject profile until all commands in the batch have completed:

```
batch
/subsystem=cas/cas-profile=default:write-attribute(name=server-url, value="https://localhost:8443")
/subsystem=cas/cas-profile=default:write-attribute(name=security-realm, value="LocalhostSslRealm")
/subsystem=cas/cas-profile=default/hostname-verifier=white-list:add(hosts=[localhost])
run-batch --headers={allow-resource-service-restart=true}
```



  

 
