/**
 * 
 */
package de.pfWorkflowWS.restConnection.restMessages;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import de.pfWorkflowWS.workflow.common.WFParameterList;

/**
 * @author mad
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReceiveMessage {

	private UUID id;
	private byte[] wf;
	private String type;
	private WFParameterList workflowParameters;

	public ReceiveMessage() {
	}

	public ReceiveMessage(UUID id, byte[] wf) {
		this.id = id;
		this.wf = wf;
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

	public WFParameterList getWorkflowParameters() {
		return workflowParameters;
	}

	public void setWorkflowParameters(WFParameterList workflowParameters) {
		this.workflowParameters = workflowParameters;
	}

	public String toString() {
		return "WF message: id: " + id + " wf: " + wf;
	}
}