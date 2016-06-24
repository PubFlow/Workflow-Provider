package de.pfWorkflowWS.restConnection;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RestConnector {

    public static void main(String[] args) throws Exception {
    	//TODO init "list/map" of managed workflows
    	//TODO load saved workflows to enable restart of the application
    	
        SpringApplication.run(RestConnector.class, args);
    }
    
}














