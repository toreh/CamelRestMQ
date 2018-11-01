package camelinaction;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;

public class AddressRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        from("direct:tore")
        .transform().simple("Hoslekroken 7");    
        
        from("direct:address")
        //input parameter automatically set as header parameter
        .log("Doing address lookup: ${header.street}" )
        //.to("jetty:https://ws.geonorge.no/AdresseWS/adresse/sok?sokestreng=${header.street}")   
        //.to("jetty:https://ws.geonorge.no/AdresseWS/adresse/sok?sokestreng=hoslekroken&bridgeEndpoint=true")
        .setHeader("Content-Type", constant("application/json"))
        .setHeader("Accept", constant("application/json"))
        .setHeader(Exchange.HTTP_METHOD, constant("GET"))
        //.recipientList(simple("http://<remoteHost>:8080/api/contact/${header.contactId}?bridgeEndpoint=true"))
        .recipientList(simple("https://ws.geonorge.no/AdresseWS/adresse/sok?sokestreng=hoslekroken"))
        .log("Result from address lookup: ${body}");

    }
}
