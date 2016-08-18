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

/**
 * This message is used to receive the results of the Workflow  of the Workflow microservice.
 * 
 * @author Marc Adolf
 *
 */
public class ResponseMessage {
	private String id;
    private String result;
    private String errorMessage;
	private String newStatus;

    
    public ResponseMessage(){
    	
    }
    
    public String getId(){
    	return id;
    }
    
    public String getResult() {
  		return result;
  	}

  	public void setResult(String result) {
  		this.result = result;
  	}

  	public String getErrorMessage() {
  		return errorMessage;
  	}

  	public void setErrorMessage(String errorMessage) {
  		this.errorMessage = errorMessage;
  	}

  	public void setId(String id) {
  		this.id = id;
  	}

	public String getNewStatus() {
		return newStatus;
	}

	public void setNewStatus(String newStatus) {
		this.newStatus = newStatus;
	}
}
