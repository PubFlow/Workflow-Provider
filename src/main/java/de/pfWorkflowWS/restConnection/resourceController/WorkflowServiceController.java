/**
 * Copyright (C) 2016 Marc Adolf, Arnd Plumhoff
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.pfWorkflowWS.restConnection.resourceController;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.pfWorkflowWS.restConnection.restMessages.ReceiveMessage;
import de.pfWorkflowWS.workflow.jbpm.JBPMWorkflow;
import de.pfWorkflowWS.workflow.jbpm.OCNWorkflow;
import de.pfWorkflowWS.workflow.jbpm.WorkflowJBPMThread;

/**
 * Accepts the incoming request for new workflow executions. The incoming
 * message is represented by {@link ReceiveMessage}.
 * 
 * @author Marc Adolf
 *
 */
@RestController
@RequestMapping("/workflow")
public class WorkflowServiceController {

	@RequestMapping(value = "/OCNWorkflow", method = RequestMethod.POST)
	public ResponseEntity<String> executeOCNWorkflow(@RequestBody ReceiveMessage msg) {
		
		JBPMWorkflow offeredWorkflow = OCNWorkflow.getInstance();

		if (!msg.isValid()) {
			return new ResponseEntity<String>(
					"Message is not valid: it needs an id, a type, a workflow and a callback address",
					HttpStatus.BAD_REQUEST);
		}

		WorkflowJBPMThread worker = new WorkflowJBPMThread(msg,offeredWorkflow);
		worker.start();
		// TODO is it ok to start the thread -> possibly get an answer before we
		// send the response?
		//TODO are duplicate executions of workflows  ok?
		return new ResponseEntity<String>("received", HttpStatus.ACCEPTED);
	}

	@RequestMapping(value = "/EPrintsWorkflow", method = RequestMethod.POST)
	public ResponseEntity<String> executeEPrintsWorkflow(@RequestBody ReceiveMessage msg) {

		// TODO
		return new ResponseEntity<String>("received", HttpStatus.ACCEPTED);
	}

	@RequestMapping(value = "/CVOOWorkflow", method = RequestMethod.POST)
	public ResponseEntity<String> executeCVOOWorkflow(@RequestBody ReceiveMessage msg) {
		// TODO
		return new ResponseEntity<String>("received", HttpStatus.ACCEPTED);
	}

}
