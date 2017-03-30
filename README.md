# Workflow-Provider

The Workflow Provider is needed to run the PubFlow project. They workflow provider communicates with PubFlow via a OAuth connection between the workflow server and the jira server.

## How to run the workflow provider
1. Replace the id_rsa and id_rsa.pub with your own keypair (RSA-SHA1).
2. Add the public key to the the OAuth config of the PubFlow Jira Plugin.
3. Open the properties file in src/main/resources/oauth.properties.
4. Change all properties to your PubFlow authentication configuration.

5. run in terminal: mvn spring-boot:run
