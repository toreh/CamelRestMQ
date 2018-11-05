package camelinaction;

import org.apache.camel.main.Main;
import javax.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;


/**
 * A main class to try to run this example
 */
public class OrderMain {

    public static void main(String[] args) throws Exception {
        Main main = new Main();
        CamelContext ctx = main.getOrCreateCamelContext();
        
        //configure jms component        
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://0.0.0.0:61616");
        ctx.addComponent("activemq", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
        
        // setup a dummy order service ie register bean(add to repo) so it can be called through
        // .to("bean:orderService?method=getOrder(${header.id})") vs
        // .bean(helloBean, "hello")
        //main.bind("orderService", new DummyOrderService());

        main.addRouteBuilder(new MakeReplyMQRoute());
        main.addRouteBuilder(new ReadRequestMQRoute());
        
        System.out.println("***************************************************");
        System.out.println("");
        System.out.println("  consumer part of request/reply MQ ");
        System.out.println("");
        System.out.println("  reads from: requestQueue ");
        System.out.println("  writes to temp queue ");
        System.out.println("");
        System.out.println("  The Swagger API:");
        System.out.println("     http://localhost:8080/api-doc/swagger.json");
        System.out.println("     http://localhost:8080/api-doc/swagger.yaml");
        System.out.println("***************************************************");

        // run the application
        main.run();

    }
}
