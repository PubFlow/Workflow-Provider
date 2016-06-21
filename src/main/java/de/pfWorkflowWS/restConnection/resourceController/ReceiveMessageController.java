/**
 * 
 */
package de.pfWorkflowWS.restConnection.resourceController;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.pfWorkflowWS.restConnection.restMessages.ReceiveMessage;


/**
 * @author mad
 *
 */
@RestController
public class ReceiveMessageController {
	
	@RequestMapping(value ="/executeNewWF", method=RequestMethod.POST)
	public ResponseEntity<String> receiveNewWorkflow(@RequestBody ReceiveMessage msg){
		if(msg.getId() == null){
			System.out.println("id is null");
			msg.setId(UUID.randomUUID());
		}
		
		
		//TODO implement handling of wrong wf messages, --> mb as error in the broker (callback with errormsg)  
//		if(!msg.isValid()){
//			myLogger.error("Workflow NOT deployed >> Msg is not valid ");
//			return;
//		}	
		
	System.out.println(msg);
		return new ResponseEntity<String>("angekommen",HttpStatus.OK);
	}

}
