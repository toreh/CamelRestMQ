package camelinaction;

import org.apache.camel.ExchangePattern;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

import static org.apache.activemq.camel.component.ActiveMQComponent.activeMQComponent;

public class MakeReplyMQRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {
        from("seda:makeReply")
        		.log("===================>>>makeReply input message: ${body}")
        		.to("log:?level=INFO&showBody=true")
        		// default seems to be req/reply, ie JmsReplyTo is set to temp-dest and and 
        		// if no msg is received upon a queue write 
        		// there will be a rollback and msg moved to ActiveMQ.DLQ
        		//.setExchangePattern(ExchangePattern.InOut)
        		.setExchangePattern(ExchangePattern.InOnly)
        		
        		// json -> pojo
        		.unmarshal().json(JsonLibrary.Jackson, Order.class)
        		.log("=====>>>POJO: ${body}")        		

        		.process(exchange -> {
                    Order order = exchange.getIn().getBody(Order.class);
                    order.setCustomerName("Hello from processor!!!");
                    })
        		
        		// pojo -> json
        		.marshal().json(JsonLibrary.Jackson, Order.class)

        		.log("=====>>>makeReply write message to queue: ${body}")
        		.log("=====>>>makeReply headers JmsReplyTo:${header.JmsReplyTo} JMSMessageID:${header.JMSMessageID} JMSDestination:${header.JMSDestination}  JMSCorrelationID:${header.JMSCorrelationID } ")

        		.to("log:?level=INFO&showBody=true")
        		
        		// uses Predicate (pluggable interface - evaluates to boolean) to decide routing
        		// eg use header, Simple, Bean language, XPath/JSONPATH
        		.choice()
        			// for message type to Text to avoid Byte[]
        			.when(header("JmsReplyTo").isNotNull()).toD("activemq:queue:${header.JmsReplyTo}?jmsMessageType=Text")
        		.otherwise()
        			.toD("activemq:queue:responseQueue?jmsMessageType=Text")
        		.end();
        		//.setHeader("ReplyTo", simple("temp_${header.JMSMessageID}"))
        		//.log("=====>>>makeReply ReplyTo:${header.ReplyTo}")
        		// .to() does not support dynamic URI eg
        		//.to("activemq:queue:${header.JmsReplyTo}");
        		//.recipientList(simple("activemq:queue:${header.JmsReplyTo}"));
        		//.toD("activemq:queue:${header.ReplyTo}");
    }
}