package camelinaction;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;

public class AddressRestConsumerRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

/*
 * Virker som denne prøver å starte undertow, noe som feiler ....
 * Skjønner ikke helt hvorfor er 'rest' endpoint avhengig av server???????????
 *

Fra spring-boot ch10 producer:
20:03:00.416 [camelinaction.OrderMain.main()] DEBUG org.apache.camel.impl.DefaultComponent - 
Creating endpoint uri=[rest://get:/orders:/ping?componentName=undertow&description=Order+services&routeId=route1], path=[get:/orders:/ping]

 * java.lang.RuntimeException: java.net.BindException: Cannot assign requested address
        at io.undertow.Undertow.start(Undertow.java:245)
        
   Caused by: java.net.BindException: Cannot assign requested address
   
   løsning tilsyn:
   .component("http4").port(8000)
nei, da slutter rest tjenesten å fungere!!!!
   
 */
    	
    	// use http4 with rest dsl producer
        restConfiguration().producerComponent("undertow");
            //.host("ws.geonorge.no").port(443)
            //.scheme("https");

        from("direct:geonorge")
            //.setHeader("street", "Hoslekroken")
            // use the rest producer to call the rest service
            //.to("rest:get:/AdresseWS/adresse/sok?sokestreng={street}")
            // transform the response to grab a nice human readable
            //.transform().jsonpath("$.results[0].formattedAddress")
            // print the response
        //.to("rest:get:AdresseWS/adresse/sok?sokestreng=hoslekroken")
        .to("rest:get:https?host=ws.geonorge.no@AdresseWS/adresse/sok@sokestreng=hoslekroken")
        .log("Result from address lookup: ${body}");
    	
    }
}
