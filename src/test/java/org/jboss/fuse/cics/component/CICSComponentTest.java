package org.jboss.fuse.cics.component;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class CICSComponentTest extends CamelTestSupport {

    /*
    @Test
    public void testCICS() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMinimumMessageCount(1);       
        
        assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {
                from("cics:serverName1/program1?sslKeyring=sslKeyring1&sslPassword=sslPassword1&userId=userId1&commAreaSize=0&port=100")
                  .to("cics:serverName1/program1?sslKeyring=sslKeyring1&sslPassword=sslPassword1&userId=userId1&commAreaSize=0&port=100")
                  .to("mock:result");
            }
        };
    }
    */
}
