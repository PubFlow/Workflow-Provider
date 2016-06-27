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

package de.pfWorkflowWS.restConnection;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * This is the main class of the Workflow Microservice of PubFlow.
 * Spring Boot is used to handle the Rest-services and the web server.
 * 
 * @author Marc Adolf
 *
 */
@SpringBootApplication
public class RestConnector {

    public static void main(String[] args) throws Exception {
    	//TODO init "list/map" of managed workflows
    	//TODO load saved workflows to enable restart of the application
    	
        SpringApplication.run(RestConnector.class, args);
    }
    
}














