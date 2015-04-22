package org.jboss.fuse.cics.component;


import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The CICS Producer.
 *  Author: sgutierr@redhat.com
 */

public class CICSProducer extends DefaultProducer {
    private static final Logger LOG = LoggerFactory.getLogger(CICSProducer.class);
    private CICSEndpoint endpoint;

    public void process(Exchange exchange) throws Exception {
        System.out.println(exchange.getIn().getBody());  
        processCTGProcedure(exchange);
    }

    public void processCTGProcedure(Exchange exchange) throws Exception{
    	CICSAdapter cicsAdapter=null;
        byte[] result=null;
    	//ToDO
    	String operation = exchange.getIn().getBody(String.class);
    	try {
            //Initialize CICS adapater
    		cicsAdapter = new CICSAdapter(getEndpoint().getServer(),getEndpoint().getSslKeyring(),getEndpoint().getSslPassword(),getEndpoint().getPort());
    	       if (LOG.isDebugEnabled()) {
                   LOG.debug ("Invoking CICSAdapter: " + cicsAdapter.toString());
               }
     		cicsAdapter.openGateway();
            result=cicsAdapter.runTransaction(getEndpoint());
            cicsAdapter.destroy();
            // producer returns a single response, even for methods with List return types
            exchange.getOut().setBody(result);
            // copy headers
            exchange.getOut().setHeaders(exchange.getIn().getHeaders());

        } catch (Exception e){
            try {
                if (cicsAdapter != null) {
                }
            } catch (Exception ex) {
                exchange.setException(e);
            }
            LOG.error(e.toString());

        }

    }
    
    @Override
    public CICSEndpoint getEndpoint() {
        return (CICSEndpoint) super.getEndpoint();
    }

    public void setEndpoint(CICSEndpoint endpoint) {
        this.endpoint = endpoint;
    }

    public CICSProducer(CICSEndpoint endpoint) {
        super(endpoint);
        this.endpoint = endpoint;
    }



}
