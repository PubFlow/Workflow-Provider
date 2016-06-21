/**
 * 
 */
package de.pfWorkflowWS.restConnection.restMessages;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author mad
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class ResponseMessage {
    private final long id = 1234;
    
    public ResponseMessage(){
    	
    }
    
    public long getId(){
    	return id;
    }
}
