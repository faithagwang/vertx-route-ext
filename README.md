vertx-route-ext aka 'vest'
==========================

Taking inspiration from the reference implementation of Jaxrs "Jersey", this implementation of Jaxrs for vertx is named "Vest".

A library that can be used to easily create routes with minimal code in vertx. This initial implementation will support creating REST services in vertx using jax-rs 2.0 (JSR339). The idea around this implementation is to create a framework on vertx, meaning that you would not be required to embed an implemenation such as Jersey, but rather leverage off the existing vertx platform.

Current Support
===============
- @Path (without regular expressions)
- @POST, @PUT, @GET, @DELETE, @OPTION and @HEAD
- @Context limited support for vertx and jsonobject
- Package scanning Application
- Multisource scanning Application
- @PathParam
- @HeaderParam Extracts the value of a header.
- @QueryParam Extracts the value of a URI query parameter.

TODO
====
- Application singletons
- Application shared property map
- @Consumes
- @Produces
- @MatrixParam Extracts the value of a URI matrix parameter.
- @CookieParam Extracts the value of a cookie.
- @Context Injects an instance of a supported resource.
- Exceptions as per specification
- Cache Control
- Context Buildup
- UriInfo implementation

Notes
=====
1. Section 3.7 (Matching Requests to Resource Methods) will be ignored and matching requests will be delegated to the RouteMatcher.
