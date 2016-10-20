package de.pfWorkflowWS.authentication;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
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
	private PrivateKey CONSUMER_SECRET;
	private PublicKey PUBLIC_KEY;
	private final String SIGNATURE_METHOD = RSA_SHA1SignatureMethod.SIGNATURE_NAME;

	private ProtectedResourceDetails resource;
	
	public static void main(String[] args) throws HttpException, IOException {
		ServiceJiraAuthentication auth = new ServiceJiraAuthentication();
		
		try {
			auth.connectWithPubflow();
		} catch (OAuthRequestFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	@Bean
	public void connectWithPubflow() throws OAuthRequestFailedException, IOException, NoSuchAlgorithmException, NoSuchProviderException {
		this.readKeyPair("");
		
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

	private void setPrivateKey(PrivateKey priavteKey) {
		this.CONSUMER_SECRET = priavteKey;
	}

	private PrivateKey getPrivateKey() {
		return this.CONSUMER_SECRET;
	}

	private void setPublicKey(PublicKey publicKey) {
		this.PUBLIC_KEY = publicKey;
	}
	
	private void setResource(ProtectedResourceDetails resource) {
		this.resource = resource;
	}
	
	@Bean
	public ProtectedResourceDetails getResource() {
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

}
