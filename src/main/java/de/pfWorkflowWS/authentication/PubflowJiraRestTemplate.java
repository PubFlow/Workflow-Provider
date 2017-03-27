package de.pfWorkflowWS.authentication;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth.consumer.OAuthConsumerToken;
import org.springframework.security.oauth.consumer.OAuthSecurityContextHolder;
import org.springframework.security.oauth.consumer.OAuthSecurityContextImpl;
import org.springframework.security.oauth.consumer.ProtectedResourceDetails;
import org.springframework.security.oauth.consumer.client.OAuthRestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

/**
 * The authenticated rest template for a communication with PubFlow.
 * 
 * @author abar
 *
 */
@Component
public class PubflowJiraRestTemplate {

	/**
	 * The authentication details for the 0-legged OAuth connection with PubFlow.
	 */
	@Autowired
	private ServiceJiraAuthentication jiraAuthentication;
	
	/**
	 * Rest template that is able to make OAuth-authenticated REST requests with the credentials of the provided resource.
	 */
	private OAuthRestTemplate restTemplate;
	
	/**
	 * Security context to determine if the permission for sending data to PubFlow is given.
	 */
	private final OAuthSecurityContextImpl securityContext = new OAuthSecurityContextImpl();
	
	/**
	 * Authenticate with PubFlow and authenticate the rest template to allow sending data.
	 */
	private void setSecurityConext() {
		final Map<String, OAuthConsumerToken> accessTokens = new HashMap<String, OAuthConsumerToken>();
		
		if(restTemplate == null) {
			this.getJiraAuthentication().authenticate();
			this.createRestTemplate(this.getJiraAuthentication().getResource());
		}
		
		accessTokens.put(this.jiraAuthentication.getResource().getId(), this.jiraAuthentication.getAccessToken());
		this.getSecurityContext().setAccessTokens(accessTokens);

		OAuthSecurityContextHolder.setContext(this.getSecurityContext());
	}
	
	/**
	 * 
	 * @return
	 */
	private OAuthSecurityContextImpl getSecurityContext() {
		return this.securityContext;
	}
	
	/**
	 * Create a {@link OAuthRestTemplate}.
	 * @param resource
	 */
	private void createRestTemplate(ProtectedResourceDetails resource) {
		this.restTemplate = new OAuthRestTemplate(resource);
	}
	
	/**
	 * Create a new resource by POSTing the given object to the URI template, and returns the response as ResponseEntity.
	 * 
	 * @param url
	 * @param request
	 * @param responseType
	 * @param uriVariables
	 * @return
	 * @throws RestClientException
	 */
	public <T> ResponseEntity<T> postForEntity(String url, Object request, Class<T> responseType, Object... uriVariables)
			throws RestClientException {
		this.setSecurityConext();
		return this.getRestTemplate().postForEntity(url, request, responseType, uriVariables);
	}
	
	/**
	 * Retrieve a representation by doing a GET on the URL .
	 * 
	 * @param url
	 * @param responseType
	 * @return
	 */
	public <T> T getForObject(URI url, Class<T> responseType) {
		this.setSecurityConext();
		return this.getRestTemplate().getForObject(url, responseType);
	}
	
	/**
	 * 
	 * @return
	 */
	private OAuthRestTemplate getRestTemplate() {
		return this.restTemplate;
	}
	
	/**
	 * 
	 * @return
	 */
	private ServiceJiraAuthentication getJiraAuthentication() {
		return this.jiraAuthentication;
	}
}
