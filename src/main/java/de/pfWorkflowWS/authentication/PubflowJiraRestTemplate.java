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

@Component
public class PubflowJiraRestTemplate {

	@Autowired
	private ServiceJiraAuthentication jiraAuthentication;
	
	private OAuthRestTemplate restTemplate;
	
	private final OAuthSecurityContextImpl securityContext = new OAuthSecurityContextImpl();
	
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
	
	private OAuthSecurityContextImpl getSecurityContext() {
		return this.securityContext;
	}
	
	private void createRestTemplate(ProtectedResourceDetails resource) {
		this.restTemplate = new OAuthRestTemplate(resource);
	}
	
	public <T> ResponseEntity<T> postForEntity(String url, Object request, Class<T> responseType, Object... uriVariables)
			throws RestClientException {
		this.setSecurityConext();
		return this.getRestTemplate().postForEntity(url, request, responseType, uriVariables);
	}
	
	public <T> T getForObject(URI url, Class<T> responseType) {
		this.setSecurityConext();
		return this.getRestTemplate().getForObject(url, responseType);
	}
	
	private OAuthRestTemplate getRestTemplate() {
		return this.restTemplate;
	}
	
	private ServiceJiraAuthentication getJiraAuthentication() {
		return this.jiraAuthentication;
	}
}
