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

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.oauth.common.signature.RSAKeySecret;
import org.springframework.security.oauth.common.signature.RSA_SHA1SignatureMethod;
import org.springframework.security.oauth.consumer.BaseProtectedResourceDetails;
import org.springframework.security.oauth.consumer.OAuthConsumerToken;
import org.springframework.security.oauth.consumer.OAuthRequestFailedException;
import org.springframework.security.oauth.consumer.ProtectedResourceDetails;
import org.springframework.security.oauth.consumer.client.CoreOAuthConsumerSupport;
import org.springframework.security.oauth.consumer.net.DefaultOAuthURLStreamHandlerFactory;

/**
 * Configuration for an 0-legged OAuth1 consumer for the PubFlow project. The
 * config file can be found in the resources folder.
 * 
 * @author abar
 *
 */
@Configuration
@PropertySource("classpath:oauth.properties")
public class ServiceJiraAuthentication {
	/**
	 * Url of the Jira server.
	 */
	@Value("${pubflow.SERVER_URL}")
	private String SERVER_URL;

	/**
	 * Url for Jira's oauth servlet.
	 */
	@Value("${pubflow.SERVLET_URL}")
	private String SERVLET_URL;

	/**
	 * Authentication url of Jira.
	 */
	@Value("${pubflow.URL_OAUTH_AUTHZ}")
	private String URL_OAUTH_AUTHZ;

	/**
	 * Url for the request token.
	 */
	@Value("${pubflow.URL_OAUTH_REQUEST}")
	private String URL_OAUTH_REQUEST;

	/**
	 * Consumer key registered in Jira.
	 */
	@Value("${pubflow.CONSUMER_KEY}")
	private String CONSUMER_KEY;

	/**
	 * Consumer key to exchange with Jira
	 */
	private PrivateKey CONSUMER_SECRET;

	/**
	 * Public RSA-SHA1 key
	 */
	private PublicKey PUBLIC_KEY;

	/**
	 * Private RSA-SHA1 key
	 */
	private final String SIGNATURE_METHOD = RSA_SHA1SignatureMethod.SIGNATURE_NAME;

	/**
	 * Request token to enchange for an access token
	 */
	private OAuthConsumerToken requestToken;

	/**
	 * Empty access token, since 0-legged OAuth is used.
	 */
	private final OAuthConsumerToken accessToken = new OAuthConsumerToken();

	/**
	 * Resource to connect to
	 */
	private ProtectedResourceDetails resource;

	/**
	 * Initilize the with PubFlow connected instance.
	 * 
	 * @return
	 */
	public ServiceJiraAuthentication createConfiguration() {
		return new ServiceJiraAuthentication();
	}

	/**
	 * Authenticate with the Jira server. Read the private key, get an request
	 * token and exchange it for the access token.
	 */
	public void authenticate() {
		// Read the private and public key

		try {
			this.readKeyPair("");
			this.retrieveRequestToken();
			this.retrieveAccessToken();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Get the request token from Jira.
	 * 
	 * @throws OAuthRequestFailedException
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 */
	public void retrieveRequestToken()
			throws OAuthRequestFailedException, IOException, NoSuchAlgorithmException, NoSuchProviderException {
		// create default Consumer
		final CoreOAuthConsumerSupport localConsumerSupport = new CoreOAuthConsumerSupport();
		localConsumerSupport.setStreamHandlerFactory(new DefaultOAuthURLStreamHandlerFactory());

		// set the resource to connect
		this.setResource(createResource());

		// retrieve request token
		this.requestToken = localConsumerSupport.getUnauthorizedRequestToken(this.getResource(), null);
	}

	/**
	 * Create a resource object with the connection informations.
	 * 
	 * @return a oauth consumer configuration.
	 */
	private ProtectedResourceDetails createResource() {
		BaseProtectedResourceDetails resource = new BaseProtectedResourceDetails();
		resource.setId("pubflow");
		resource.setConsumerKey(this.CONSUMER_KEY);
		resource.setSharedSecret(new RSAKeySecret(this.getPrivateKey(), this.getPublicKey()));
		resource.setSignatureMethod(SIGNATURE_METHOD);
		resource.setUse10a(true);
		resource.setRequestTokenURL(SERVER_URL + SERVLET_URL + URL_OAUTH_REQUEST);
		resource.setUserAuthorizationURL(SERVER_URL + SERVLET_URL + URL_OAUTH_REQUEST);
		resource.setAcceptsAuthorizationHeader(true);

		return resource;
	}

	/**
	 * In 0-legged OAuth1 the access token is empty, since it is not really
	 * needed. But Jira uses it for authentication. Add the token secret and set
	 * the empty token as access token.
	 */
	private void retrieveAccessToken() {
		this.accessToken.setValue("");
		this.accessToken.setSecret(this.getRequestToken().getSecret());
		this.accessToken.setAccessToken(true);
	}

	/**
	 * @param priavteKey
	 */
	private void setPrivateKey(PrivateKey priavteKey) {
		this.CONSUMER_SECRET = priavteKey;
	}

	/**
	 * 
	 * @return
	 */
	private PrivateKey getPrivateKey() {
		return this.CONSUMER_SECRET;
	}

	/**
	 * 
	 * @param publicKey
	 */
	private void setPublicKey(PublicKey publicKey) {
		this.PUBLIC_KEY = publicKey;
	}

	/**
	 * 
	 * @return
	 */
	private PublicKey getPublicKey() {
		return this.PUBLIC_KEY;
	}

	/**
	 * 
	 * @param resource
	 */
	private void setResource(ProtectedResourceDetails resource) {
		this.resource = resource;
	}

	/**
	 * 
	 * @return
	 */
	protected ProtectedResourceDetails getResource() {
		return this.resource;
	}

	/**
	 * Create a pair of private and public key to authenticate with Jira.
	 * 
	 * @param path the path to the id_rsa and id_rsa.pub files.
	 * 
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void readKeyPair(String path)
			throws NoSuchAlgorithmException, NoSuchProviderException, FileNotFoundException, IOException {
		Security.addProvider(new BouncyCastleProvider());

		KeyFactory factory = KeyFactory.getInstance("RSA", "BC");
		try {
			this.setPrivateKey(generatePrivateKey(factory, path + "id_rsa"));
			this.setPublicKey(generatePublicKey(factory, path + "id_rsa.pub"));
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create a private key (RSA-SHA1).
	 * 
	 * @param factory
	 * @param filename
	 * @return
	 * @throws InvalidKeySpecException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private PrivateKey generatePrivateKey(KeyFactory factory, String filename)
			throws InvalidKeySpecException, FileNotFoundException, IOException {
		PemObject pemFile = readPem(filename);
		byte[] content = pemFile.getContent();
		PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(content);
		return factory.generatePrivate(privKeySpec);
	}

	/**
	 * Create a public key (RSA-SHA1).
	 * 
	 * @param factory
	 * @param filename
	 * @return
	 * @throws InvalidKeySpecException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private PublicKey generatePublicKey(KeyFactory factory, String filename)
			throws InvalidKeySpecException, FileNotFoundException, IOException {
		PemObject pemFile = readPem(filename);
		byte[] content = pemFile.getContent();
		X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(content);
		return factory.generatePublic(pubKeySpec);
	}

	/**
	 * Read the keys in Pem format.
	 * 
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	private PemObject readPem(String filename) throws IOException {
		PemReader pemReader = new PemReader(new InputStreamReader(new FileInputStream(filename)));
		try {
			return pemReader.readPemObject();
		} finally {
			pemReader.close();
		}
	}

	/**
	 * 
	 * @return
	 */
	private OAuthConsumerToken getRequestToken() {
		return this.requestToken;
	}

	/**
	 * 
	 * @return
	 */
	protected OAuthConsumerToken getAccessToken() {
		return this.accessToken;
	}

}
