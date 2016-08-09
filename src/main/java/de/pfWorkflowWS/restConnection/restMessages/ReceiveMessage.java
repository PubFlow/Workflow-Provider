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

import java.net.URI;
import java.util.List;
import java.util.UUID;

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
public class ReceiveMessage {

	/**
	 * Used to map the workflow to other services, events and responses.
	 */
	private UUID id;
	/**
	 * The workflow to be executed, as byte array.
	 */
	private byte[] wf;
	/**
	 * The type of the workflow (e.g. BPMN2)
	 */
	private String type;
	/**
	 * Parameters used to execute the workflow.
	 */
	private List<WFParameter> workflowParameters;
	/**
	 * Url to response to
	 */
	private URI callbackAddress;

	public ReceiveMessage() {
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public byte[] getWf() {
		return wf;
	}

	public void setWf(byte[] wf) {
		this.wf = wf;

	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<WFParameter> getWorkflowParameters() {
		return workflowParameters;
	}

	public void setWorkflowParameters(List<WFParameter> workflowParameters) {
		this.workflowParameters = workflowParameters;
	}

	public String toString() {
		return "WF message: id: " + id + " wf: " + wf;
	}

	public URI getCallbackAddress() {
		return callbackAddress;
	}

	public void setCallbackAddress(URI callbackAdress) {
		this.callbackAddress = callbackAdress;
	}

	/**
	 * Checks if the message is valid. This contains only if all necessary
	 * fields are not null.
	 * 
	 * @return true, if id, type, workflow and callbackAddress are not null
	 */
	public Boolean isValid() {
		return id != null && wf != null && type != null && callbackAddress != null;
	}
}