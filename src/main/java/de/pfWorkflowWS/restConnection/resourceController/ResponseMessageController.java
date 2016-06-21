/**
 * 
 */
package de.pfWorkflowWS.restConnection.resourceController;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.pfWorkflowWS.restConnection.restMessages.ResponseMessage;


/**
 * @author mad
 *
 */
@RestController
public class ResponseMessageController {
	
	@RequestMapping("/response")
	public ResponseMessage receive(){
		return new ResponseMessage();
	}

}
