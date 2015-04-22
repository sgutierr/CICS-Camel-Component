package org.jboss.fuse.cics.component;

import java.util.Map;
import org.apache.camel.Consumer;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriPath;


/**
 * CICS Endpoint  cics://serverName[:port]/program[?options]
 * Author: sgutierr@redhat.com
 * Dependecy with camel-core version 2.16-SNAPSHOT
 */

@UriEndpoint(scheme = "cics", title = "CICS", syntax = "cics:server/program", producerOnly = true)
public class CICSEndpoint extends DefaultEndpoint {

    private static final String DEFAULT_SIZE = "100";

    @UriParam
    private String userId;
	@UriParam
	private String password;
    @UriParam
    private String sslPassword;
    @UriParam
    private String sslKeyring;
	@UriParam (defaultValue = DEFAULT_SIZE)
    private int commAreaSize;
    @UriParam (defaultValue="2006")
    private int port;
	@UriPath @Metadata(required = "true")
    private String server;
	@UriPath @Metadata(required = "true")
    private String program;

    private Map<String, Object> parameters;

	public CICSEndpoint() {
    }

	public CICSEndpoint(String uri, CICSComponent component, String remaining, Map<String, Object> parameters) {
        super(uri, component);
        setServer(remaining);
        setProgram(remaining);
        this.parameters=parameters;
	}
	
    public CICSEndpoint(String endpointUri) {
        super(endpointUri);
    }

    public Producer createProducer() throws Exception {
        return new CICSProducer(this);
    }
    
    /**
     * Optional parameters to the {@link java.sql.Statement}.
     * <p/>
     * For example to set maxRows, fetchSize etc.
     *
     * @param parameters parameters which will be set using reflection
     */
      
    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    public Consumer createConsumer(Processor processor) throws Exception {
    return new CICSConsumer(this, processor);
    }

    public boolean isSingleton() {
        return true;
    }
    
 // Implement the following methods, only if you need to set exchange properties.
    //
    public Exchange createExchange() { 
        return this.createExchange(getExchangePattern());
    }

    public Exchange createExchange(ExchangePattern pattern) {
        Exchange result = new DefaultExchange(getCamelContext(), pattern);
        // Set exchange properties
    
        return result;
    }

    public String getScheme() {
        return "cics";
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSslPassword() {
        return sslPassword;
    }

    public void setSslPassword(String sslPassword) {
        this.sslPassword = sslPassword;
    }

    public String getSslKeyring() {
        return sslKeyring;
    }

    public void setSslKeyring(String sslKeyring) {
        this.sslKeyring = sslKeyring;
    }

    public int getCommAreaSize() {
        return commAreaSize;
    }

    public void setCommAreaSize(int commAreaSize) {
        this.commAreaSize = commAreaSize;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        int end = server.indexOf("/");
        this.server = server.substring(0,end);
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {

        int start = program.indexOf("/");
        this.program = program.substring(start+1);
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String toString() {
        return "user=[" + this.getUserId() +
                "], server=["        + this.getServer() +
                "], password=["       + this.getPassword() +
                "], program=["      + this.getProgram() +
                "], comArea=["    + this.getCommAreaSize() + "]";
    }
}
