package org.jboss.fuse.cics.component;

import java.net.URI;
import java.util.Map;

import org.apache.camel.ComponentConfiguration;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;
import org.apache.camel.util.IntrospectionSupport;

/**
 * Represents the CICS component
 * Author: sgutierr@redhat.com
 */
public class CICSComponent extends DefaultComponent {

    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {

        CICSEndpoint cicsEndpoint = new CICSEndpoint(uri, this, remaining, parameters);
        setProperties(cicsEndpoint, parameters);

        return cicsEndpoint;
    }
}
