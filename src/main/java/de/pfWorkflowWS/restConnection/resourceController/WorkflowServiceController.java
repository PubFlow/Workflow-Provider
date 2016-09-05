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

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.pfWorkflowWS.restConnection.restMessages.WorkflowReceiveMessage;
import de.pfWorkflowWS.workflow.jbpm.WorkflowJBPMThread;
import de.pfWorkflowWS.workflow.jbpm.availableWorkflows.CVOOJBPMWorkflow;
import de.pfWorkflowWS.workflow.jbpm.availableWorkflows.EPrintsJBPMWorkflow;
import de.pfWorkflowWS.workflow.jbpm.availableWorkflows.JBPMWorkflow;
import de.pfWorkflowWS.workflow.jbpm.availableWorkflows.OCNJBPMWorkflow;
import de.pfWorkflowWS.workflow.jbpm.availableWorkflows.TestFlowJBPMWorkflow;

/**
 * Accepts the incoming request for new Workflow executions. The incoming
 * message is represented by {@link WorkflowReceiveMessage}. Does not wait for
 * the result of the Workflow execution.
 * 
 * @author Marc Adolf
 *
 */
@RestController
@RequestMapping("/workflow")
public class WorkflowServiceController {

	@RequestMapping(value = "/OCNWorkflow", method = RequestMethod.POST)
	public ResponseEntity<String> executeOCNWorkflow(@RequestBody WorkflowReceiveMessage msg) {

		/*
		 * OCN
		 */
		return handleJBPMWorkflow(OCNJBPMWorkflow.getInstance(), msg);

	}

	@RequestMapping(value = "/EPrintsWorkflow", method = RequestMethod.POST)
	public ResponseEntity<String> executeEPrintsWorkflow(@RequestBody WorkflowReceiveMessage msg) {

		/*
		 * EPrints
		 */
		return handleJBPMWorkflow(EPrintsJBPMWorkflow.getInstance(), msg);

	}

	@RequestMapping(value = "/CVOOWorkflow", method = RequestMethod.POST)
	public ResponseEntity<String> executeCVOOWorkflow(@RequestBody WorkflowReceiveMessage msg) {

		/*
		 * CVOO
		 */
		return handleJBPMWorkflow(CVOOJBPMWorkflow.getInstance(), msg);

	}

	@RequestMapping(value = "/TestWorkflow", method = RequestMethod.GET)
	public ResponseEntity<String> executeTestWorkflow(
	// @RequestBody WorkflowReceiveMessage msg
	) {

		/*
		 * Test
		 */
		WorkflowReceiveMessage msg = new WorkflowReceiveMessage();
		msg.setId("testId");
		msg.setCallbackAddress("http://www.example.de");
		return handleJBPMWorkflow(TestFlowJBPMWorkflow.getInstance(), msg);

	}

	/*
	 * Often JBPM Workflows share the same code. This class validates the
	 * message, initializes the JBPM Workflow with its Knowledgebase and starts
	 * a new Thread to execute the workflow. Responses are generated
	 * representing the success. Does not wait for the execution to finish.
	 */
	private ResponseEntity<String> handleJBPMWorkflow(JBPMWorkflow offeredWorkflow, WorkflowReceiveMessage msg) {

		if (!msg.isValid()) {
			return new ResponseEntity<String>("Message is not valid: it needs an id, a type and a callback address",
					HttpStatus.BAD_REQUEST);
		}
		try {
			offeredWorkflow.init();
			WorkflowJBPMThread worker = new WorkflowJBPMThread(msg, offeredWorkflow);
			worker.start();
		} catch (IOException e) {
			e.printStackTrace();
			return new ResponseEntity<String>("Workflow could not be loaded", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<String>("received", HttpStatus.ACCEPTED);
	}

}
