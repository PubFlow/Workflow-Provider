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
package de.pfWorkflowWS.restConnection.restMessages;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import de.pfWorkflowWS.workflow.common.WFParameter;

/**
 * Represents the message which is given to the server to initialise a new
 * workflow. The {@link #id} is used to map responses and events.
 * 
 * 
 * @author Marc Adolf
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkflowReceiveMessage {

	/**
	 * Used to map the workflow to other services, events and responses.
	 */
	private String id;
	/**
	 * Parameters used to execute the workflow.
	 */
	private List<WFParameter> workflowParameters;
	/**
	 * URL to response to
	 */
	private String callbackAddress;

	public WorkflowReceiveMessage() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<WFParameter> getWorkflowParameters() {
		return workflowParameters;
	}

	public void setWorkflowParameters(List<WFParameter> workflowParameters) {
		this.workflowParameters = workflowParameters;
	}

	public String getCallbackAddress() {
		return callbackAddress;
	}

	public void setCallbackAddress(String callbackAdress) {
		this.callbackAddress = callbackAdress;
	}

	/**
	 * Checks if the message is valid. This contains only if all necessary
	 * fields are not null.
	 * 
	 * @return true, if id, type, workflow and callbackAddress are not null
	 */
	public Boolean isValid() {
		return id != null && callbackAddress != null;
	}
}