package camelinaction;

import org.apache.camel.ExchangePattern;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

import static org.apache.activemq.camel.component.ActiveMQComponent.activeMQComponent;

public class WriteMQRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {
        from("direct:writeQueue")
        		// send as json but arrives as xml???
        		// sannsynligvis pga JAXB annoteringer i Order.java. 
        		// Fjernet disse kommer som pojo
        		.log("=====>>>input message: ${body}")
        		.to("log:?level=INFO&showBody=true")
        		// default seems to be req/reply, ie JmsReplyTo is set to temp-dest and and 
        		// if no msg is received upon a queue write 
        		// there will be a rollback and msg moved to ActiveMQ.DLQ
        		.setExchangePattern(ExchangePattern.InOut)
        		//.setExchangePattern(ExchangePattern.InOnly)
        		
        		// crashes
        		// JsonParseException: Unexpected character ('<' (code 60)): expected a valid value (number, String, array, object, 'true', 'false' or 'null')
        		// forventer ikke xml inn .....
        		//.unmarshal().json(JsonLibrary.Jackson, Order.class)
        		//.log("=====>>>POJO: ${body}")        		
        		
        		.log("=====>>>simple partName: " + simple("${body.partName}"))
        		.log("=====>>>simple partName: ${body.partName}")
        		
        		// filter & simple
        		// from("seda:orders").filter().simple("${in.header.foo}").to("seda:fooOrders");
        		
        		.process(exchange -> {
                    Order order = exchange.getIn().getBody(Order.class);
                    System.out.println("==========>>>> java partName: " + order.getPartName());
                    })
        		
        		
        		// type conversion vs format conversion
        		// Camel has automatic type conversion
        		// JSON data format (pojo <-> json)
        		// pojo -> json
        		.marshal().json(JsonLibrary.Jackson, Order.class)
        		.log("=====>>>write message to queue: ${body}")
        		// bra logformat - gir også Exchange info!!!
                .to("log:?level=INFO&showBody=true")
                // bodytype = Byte[]
                .to("activemq:queue:requestQueue?jmsMessageType=Text");
    }
}