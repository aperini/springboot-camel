package com.springbootcamel.api;

import com.springbootcamel.model.Hello;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
class RestApi extends RouteBuilder {

    @Value("${springbootcamel.api.path}")
    String contextPath;

    @Value("${server.port}")
    String serverPort;

    @Override
    public void configure() {
        CamelContext context = new DefaultCamelContext();

        restConfiguration()
                .contextPath(contextPath)
                .port(serverPort)
                .enableCORS(true)

                // Adds Swagger documentation to the URI, title, and version
                // This Swagger context is itself a Camel route
                // by default served at http://localhost:8080/camel/api-doc
                .apiContextPath("/api-doc")
                .apiProperty("api.title", "Test REST API")
                .apiProperty("api.version", "v1")
                .apiContextRouteId("doc-api")
                .component("servlet")

                // The binding mode allows and converts arguments to our API
                .bindingMode(RestBindingMode.json);

        rest("/api/")
                // identification of the route inside the CamelContext
                .id("api-route")
                .consumes("application/json")
                // adds an operation to the API, generating a “POST /bean” endpoint
                .post("/hello")
                .bindingMode(RestBindingMode.json_xml)
                .type(Hello.class)
                //  creates a bridge to another route, it tells Camel to search inside its context/engine
                .to("direct:remoteService");

        from("direct:remoteService")
                // creates a link to the rest().to() above, because it consumes from the Camel context messages
                .routeId("direct-route")
                .tracing()
                .log(">>> ${body.id}")
                .log(">>> ${body.name}")
                // a route receives parameters and then converts, transforms and process these parameters
                // other conversions are available, including extraction as Java primitives (or objects) and sending it down to a persistence layer
                .transform().simple("Hello ${in.body.name}")
                // it sends these parameters to another route that forwards the result to the desired output
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200));
    }
}
