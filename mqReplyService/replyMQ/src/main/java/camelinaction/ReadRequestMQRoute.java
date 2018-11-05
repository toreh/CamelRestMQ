package camelinaction;

import org.apache.camel.builder.RouteBuilder;
import static org.apache.activemq.camel.component.ActiveMQComponent.activeMQComponent;

public class ReadRequestMQRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {
        from("activemq:queue:requestQueue?jmsMessageType=Text")
                .log("Received message on requestQueue: ${body}")
                .to("seda:makeReply");
    }
}