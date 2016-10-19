package de.pfWorkflowWS.authentication;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth.common.signature.RSAKeySecret;
import org.springframework.security.oauth.common.signature.RSA_SHA1SignatureMethod;
import org.springframework.security.oauth.consumer.BaseProtectedResourceDetails;
import org.springframework.security.oauth.consumer.OAuthConsumerToken;
import org.springframework.security.oauth.consumer.OAuthRequestFailedException;
import org.springframework.security.oauth.consumer.OAuthSecurityContextHolder;
import org.springframework.security.oauth.consumer.OAuthSecurityContextImpl;
import org.springframework.security.oauth.consumer.ProtectedResourceDetails;
import org.springframework.security.oauth.consumer.client.CoreOAuthConsumerSupport;
import org.springframework.security.oauth.consumer.client.OAuthRestTemplate;
import org.springframework.security.oauth.consumer.net.DefaultOAuthURLStreamHandlerFactory;

@Configuration
public class ServiceJiraAuthentication {

	private final String SERVER_URL = "http://riemann:2990/jira";
	private final String SERVLET_URL = "/plugins/servlet";
	private final String SERVER_URL_OAUTH_AUTHZ = SERVER_URL + SERVLET_URL + "/oauth/authorize?oauth_token=%s";
	private final String SERVER_URL_OAUTH_REQUEST = SERVER_URL + SERVLET_URL + "/oauth/request-token";
	private final String SERVER_URL_RESOURCE = SERVER_URL + "/rest/api/latest/issue/PUB-1";
	private final String CONSUMER_KEY = "Alex";
	private String CONSUMER_SECRET;
	private final String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCSoZJX3ueg0I3h4uEtRq6F8mo9oOJMGUNlLY+3KJ0Sm3zkbzKjupHX7ORmcPvnkt3S7gxW4OfwEGTiSlpEJoQnP80hcHp9oaV3mWo4m1ou8QM9nsN4CcGHa3fbgy5pVkou3OBXs3BB3EiHkz/Z0HyJynuyzz0RccR6oWGrNul4kQIDAQAB";
	private final String SIGNATURE_METHOD = RSA_SHA1SignatureMethod.SIGNATURE_NAME;

	private ProtectedResourceDetails resource;
	
	public static void main(String[] args) throws HttpException, IOException {
		ServiceJiraAuthentication auth = new ServiceJiraAuthentication();

		try {
			String key = new String(Files.readAllBytes(Paths.get("id_rsa")));
			key = key.replaceAll("-----BEGIN RSA PRIVATE KEY-----", "");
			key = key.replaceAll("-----END RSA PRIVATE KEY-----", "");
			auth.setPrivateKey(key);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		auth.connectWithPubflow();

	}

	public String getPrivateKey(String filename) throws Exception {
		File f = new File(filename);
		FileInputStream fis = new FileInputStream(f);
		DataInputStream dis = new DataInputStream(fis);
		byte[] keyBytes = new byte[(int) f.length()];
		dis.readFully(keyBytes);
		dis.close();

		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePrivate(spec).toString();
	}
	
	@Bean
	public void connectWithPubflow() throws OAuthRequestFailedException, IOException {
		final CoreOAuthConsumerSupport localConsumerSupport = new CoreOAuthConsumerSupport();
		localConsumerSupport.setStreamHandlerFactory(new DefaultOAuthURLStreamHandlerFactory());

		this.setResource(createResource());

		final OAuthConsumerToken requestToken = localConsumerSupport.getUnauthorizedRequestToken(this.getResource(),
				null);

		this.saveAccessToken(requestToken);

		OAuthRestTemplate restTemplate = new OAuthRestTemplate(this.getResource());
		try {
			restTemplate.getForObject(new URI(SERVER_URL_RESOURCE), String.class);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private ProtectedResourceDetails createResource() {
		BaseProtectedResourceDetails resource = new BaseProtectedResourceDetails();
		resource.setId("pubflow");
		resource.setConsumerKey(this.CONSUMER_KEY);
		resource.setSharedSecret(new RSAKeySecret(this.getPrivateKey(), this.PUBLIC_KEY));
		resource.setSignatureMethod(SIGNATURE_METHOD);
		resource.setUse10a(true);
		resource.setRequestTokenURL(SERVER_URL_OAUTH_REQUEST);
		resource.setUserAuthorizationURL(SERVER_URL_OAUTH_AUTHZ);

		return resource;
	}

	public void saveAccessToken(OAuthConsumerToken requestToken) throws OAuthRequestFailedException, IOException {
		final OAuthConsumerToken token = new OAuthConsumerToken();

		token.setValue("");
		token.setSecret(requestToken.getSecret());
		token.setAccessToken(true);

		final OAuthSecurityContextImpl securityContext = new OAuthSecurityContextImpl();
		final Map<String, OAuthConsumerToken> accessToken = new HashMap<String, OAuthConsumerToken>();
		accessToken.put("pubflow", token);
		securityContext.setAccessTokens(accessToken);
		OAuthSecurityContextHolder.setContext(securityContext);
	}

	private void setPrivateKey(String key) {
		this.CONSUMER_SECRET = key;
	}

	public String getPrivateKey() {
		return this.CONSUMER_SECRET;
	}

	private void setResource(ProtectedResourceDetails resource) {
		this.resource = resource;
	}
	
	@Bean
	public ProtectedResourceDetails getResource() {
		return this.resource;
	}

}
