/**
 * 
 */
package de.pfWorkflowWS.restConnection.resourceController;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.pfWorkflowWS.exceptions.MessageNotValidException;
import de.pfWorkflowWS.restConnection.restMessages.ReceiveMessage;
import de.pfWorkflowWS.workflow.management.WorkflowManager;

/**
 * @author mad
 *
 */
@RestController
public class ReceiveMessageController {

	@RequestMapping(value = "/executeNewWF", method = RequestMethod.POST)
	public ResponseEntity<String> receiveNewWorkflow(@RequestBody ReceiveMessage msg) {

		try {
			//register and start the new workflow
			WorkflowManager.getInstance().addWorkflowEntity(msg);
		} catch (MessageNotValidException e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}

		// TODO implement error handling of workflow errors  


		return new ResponseEntity<String>("received", HttpStatus.ACCEPTED);
	}

}
