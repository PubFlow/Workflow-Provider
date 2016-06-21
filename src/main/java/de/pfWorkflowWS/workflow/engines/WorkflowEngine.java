package de.pfWorkflowWS.workflow.engines;

import java.util.List;

import de.pfWorkflowWS.exceptions.WFException;
import de.pfWorkflowWS.workflow.common.WFParameterList;
import de.pfWorkflowWS.workflow.common.WFType;
import de.pfWorkflowWS.workflow.entity.PubFlow;



abstract public class WorkflowEngine implements Runnable{
	
	
	/**
	 * Method to deploy a new Publication Workflow in a Workflow Engine
	 * 
	 * @param wf (PubFlow) : The workflow to deploy
	 * @throws WFException
	 */
	public abstract void deployWF(PubFlow wf) throws WFException;
	
	/**
	 * Starts the pubflow with the given ID
	 * @param wfID (long) : the ID of the workflow to start
	 * @param params (WFParameter ...) : The list of the parameters needed by the Workflow
	 * @throws WFException
	 */
	public abstract void setParams(WFParameterList params) throws WFException;
	
	/**
	 * Method to undeploy a deployed pubflow
	 * 
	 * @param wfID (long) : the ID of the workflow
	 * @throws WFException
	 */
	public abstract void undeployWF(long wfID) throws WFException;
	
	/**
	 * Method to stop a running Pubflow
	 * 
	 * @param wfID (long) : the ID of the PubFlow
	 * @throws WFException
	 */
	public abstract void stopWF(long wfID) throws WFException;
	
	public abstract List<WFType> getCompatibleWFTypes();
}
