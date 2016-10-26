package de.pfWorkflowWS.authentication;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.annotation.PostConstruct;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth.common.signature.RSAKeySecret;
import org.springframework.security.oauth.common.signature.RSA_SHA1SignatureMethod;
import org.springframework.security.oauth.consumer.BaseProtectedResourceDetails;
import org.springframework.security.oauth.consumer.OAuthConsumerToken;
import org.springframework.security.oauth.consumer.OAuthRequestFailedException;
import org.springframework.security.oauth.consumer.ProtectedResourceDetails;
import org.springframework.security.oauth.consumer.client.CoreOAuthConsumerSupport;
import org.springframework.security.oauth.consumer.net.DefaultOAuthURLStreamHandlerFactory;

@Configuration
public class ServiceJiraAuthentication {

	private final String SERVER_URL = "http://localhost:2990/jira";
	private final String SERVLET_URL = "/plugins/servlet";
	private final String SERVER_URL_OAUTH_AUTHZ = SERVER_URL + SERVLET_URL + "/oauth/authorize?oauth_token=%s";
	private final String SERVER_URL_OAUTH_REQUEST = SERVER_URL + SERVLET_URL + "/oauth/request-token";
	private final String CONSUMER_KEY = "Alex";
	private PrivateKey CONSUMER_SECRET;
	private PublicKey PUBLIC_KEY;
	private final String SIGNATURE_METHOD = RSA_SHA1SignatureMethod.SIGNATURE_NAME;
	private OAuthConsumerToken requestToken;
	private final OAuthConsumerToken accessToken = new OAuthConsumerToken();

	
	private ProtectedResourceDetails resource;
	
	public ServiceJiraAuthentication authenticate() {
//		this.createResource();
//		this.retrieveAccessToken();
		
		return new ServiceJiraAuthentication();
	}
	
	@PostConstruct
	public void retrieveRequestToken() throws OAuthRequestFailedException, IOException, NoSuchAlgorithmException, NoSuchProviderException {
		// Read the private and public key
		this.readKeyPair("");
		
		// create default Consumer
		final CoreOAuthConsumerSupport localConsumerSupport = new CoreOAuthConsumerSupport();
		localConsumerSupport.setStreamHandlerFactory(new DefaultOAuthURLStreamHandlerFactory());

		// set the resource to connect
		this.setResource(createResource());
		
		// retrieve request token
		this.requestToken = localConsumerSupport.getUnauthorizedRequestToken(this.getResource(),
				null);
		
		this.retrieveAccessToken();
	}
	
//	@Bean
	private ProtectedResourceDetails createResource() {
		BaseProtectedResourceDetails resource = new BaseProtectedResourceDetails();
		resource.setId("pubflow");
		resource.setConsumerKey(this.CONSUMER_KEY);
		resource.setSharedSecret(new RSAKeySecret(this.getPrivateKey(), this.getPublicKey()));
		resource.setSignatureMethod(SIGNATURE_METHOD);
		resource.setUse10a(true);
		resource.setRequestTokenURL(SERVER_URL_OAUTH_REQUEST);
		resource.setUserAuthorizationURL(SERVER_URL_OAUTH_AUTHZ);
		resource.setAcceptsAuthorizationHeader(true);

		return resource;
	}

//	@Bean
	private void retrieveAccessToken() {

		this.accessToken.setValue("");
		this.accessToken.setSecret(this.getRequestToken().getSecret());
		this.accessToken.setAccessToken(true);
		
//		return accessToken;
	}

	private void setPrivateKey(PrivateKey priavteKey) {
		this.CONSUMER_SECRET = priavteKey;
	}

	private PrivateKey getPrivateKey() {
		return this.CONSUMER_SECRET;
	}

	private void setPublicKey(PublicKey publicKey) {
		this.PUBLIC_KEY = publicKey;
	}
	
	private PublicKey getPublicKey() {
		return this.PUBLIC_KEY;
	}
	
	private void setResource(ProtectedResourceDetails resource) {
		this.resource = resource;
	}
	
	protected ProtectedResourceDetails getResource() {
		return this.resource;
	}
	
	private void readKeyPair(String path) throws NoSuchAlgorithmException, NoSuchProviderException, FileNotFoundException, IOException {
		Security.addProvider(new BouncyCastleProvider());

		KeyFactory factory = KeyFactory.getInstance("RSA", "BC");
		try {
			this.setPrivateKey(generatePrivateKey(factory, path + "id_rsa"));
			this.setPublicKey(generatePublicKey(factory, path + "id_rsa.pub"));
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
	}
 
	private PrivateKey generatePrivateKey(KeyFactory factory, String filename) throws InvalidKeySpecException, FileNotFoundException, IOException {
		PemObject pemFile = readPem(filename);
		byte[] content = pemFile.getContent();
		PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(content);
		return factory.generatePrivate(privKeySpec);
	}
	
	private PublicKey generatePublicKey(KeyFactory factory, String filename) throws InvalidKeySpecException, FileNotFoundException, IOException {
		PemObject pemFile = readPem(filename);
		byte[] content = pemFile.getContent();
		X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(content);
		return factory.generatePublic(pubKeySpec);
	}
	
	private PemObject readPem(String filename) throws IOException {
		PemReader pemReader = new PemReader(new InputStreamReader(new FileInputStream(filename)));
		try {
			return pemReader.readPemObject();
		} finally {
			pemReader.close();
		}
	}
	
	private OAuthConsumerToken getRequestToken() {
		return this.requestToken;
	}
	
	protected OAuthConsumerToken getAccessToken() {
		return this.accessToken;
	}

}
