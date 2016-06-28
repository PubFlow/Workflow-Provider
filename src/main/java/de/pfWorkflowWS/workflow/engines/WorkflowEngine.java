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
package de.pfWorkflowWS.workflow.engines;

import java.util.List;

import de.pfWorkflowWS.workflow.common.WFParameterList;
import de.pfWorkflowWS.workflow.common.WFType;
import de.pfWorkflowWS.workflow.entity.PubFlow;


/**
 * 
 * @author Peer Brauer
 *
 */
abstract public class WorkflowEngine implements Runnable{
	
	
	/**
	 * Method to deploy a new Publication Workflow in a Workflow Engine
	 * 
	 * @param wf (PubFlow) : The workflow to deploy
	 */
	public abstract void deployWF(PubFlow wf);
	
	/**
	 * Starts the pubflow with the given ID
	 * @param wfID (long) : the ID of the workflow to start
	 * @param params (WFParameter ...) : The list of the parameters needed by the Workflow
	 */
	public abstract void setParams(WFParameterList params);
	
	/**
	 * Method to undeploy a deployed pubflow
	 * 
	 * @param wfID (long) : the ID of the workflow
	 */
	public abstract void undeployWF(long wfID);
	
	/**
	 * Method to stop a running Pubflow
	 * 
	 * @param wfID (long) : the ID of the PubFlow
	 */
	public abstract void stopWF(long wfID);
	
	public abstract List<WFType> getCompatibleWFTypes();
}
