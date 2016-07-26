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

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.pfWorkflowWS.restConnection.restMessages.EventMessage;
import de.pfWorkflowWS.workflow.management.WorkflowEntity;
import de.pfWorkflowWS.workflow.management.WorkflowManager;

/**
 * Accepts or rejects update requests for workflows, which may trigger events in
 * the workflow engine.
 * 
 * @author Marc Adolf
 *
 */

@RestController
public class EventMessageController {

	@RequestMapping(value = "/updateWF", method = RequestMethod.PUT)
	public ResponseEntity<String> receiveNewWorkflow(@RequestBody EventMessage msg) {
		UUID correspondingID = msg.getId();
		WorkflowEntity wfEntity = WorkflowManager.getInstance().lookupWorkflowEntryId(correspondingID);

		if (wfEntity == null) {
			return new ResponseEntity<String>("Workflow not found", HttpStatus.NOT_FOUND);
		}
		
		wfEntity.getEngine().handleEvent(msg);
		return new ResponseEntity<String>("received", HttpStatus.ACCEPTED);

	}
}