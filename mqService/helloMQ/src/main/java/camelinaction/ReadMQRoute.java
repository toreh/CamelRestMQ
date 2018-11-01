package camelinaction;

import org.apache.camel.builder.RouteBuilder;
import static org.apache.activemq.camel.component.ActiveMQComponent.activeMQComponent;

public class ReadMQRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {
        from("direct:writeQueue")
                .to("log:?level=INFO&showBody=true")
                .to("activemq:queue:testQueue");
    }
}