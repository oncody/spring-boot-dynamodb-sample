package com.example;

import java.io.IOException;
import java.net.ServerSocket;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.local.main.ServerRunner;
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;

import org.apache.commons.lang3.StringUtils;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableDynamoDBRepositories(basePackages = "com.example.repositories")
public class DynamoDBConfig {

  private final int PORT = 8080;
  private final String DOMAIN = "http://localhost:" + PORT;
  private final String ACCESS_KEY = "access";
  private final String SECRET_KEY = "secret";

  @Bean
  public AmazonDynamoDB amazonDynamoDB() {
    try {
      System.out.println("Creating local dynamoDB");
      System.setProperty("sqlite4java.library.path", "native-libs");

      System.out.println("Finding port");
      // final String port = getAvailablePort();
      // System.out.println("Port found: " + PORT);

      System.out.println("Creating server");
      DynamoDBProxyServer server = ServerRunner.createServerFromCommandLineArgs(new String[] { "-inMemory", "-port", "" + PORT });
      System.out.println("Server created");

      System.out.println("Starting server");
      server.start();
      System.out.println("Server started");

      System.out.println("Instantiating client");
      AmazonDynamoDB amazonDynamoDB = new AmazonDynamoDBClient(new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY));
      System.out.println("Client instantiated");

      System.out.println("Setting endpoint");
      amazonDynamoDB.setEndpoint(DOMAIN);
      System.out.println("Endpoint set");

      // AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder.standard()
      // .withCredentials(new AWSStaticCredentialsProvider(new
      // BasicAWSCredentials("access", "secret")))
      // .withEndpointConfiguration(new
      // AwsClientBuilder.EndpointConfiguration("http://localhost:8080", "us-west-2"))
      // .build();

      // AmazonDynamoDB amazonDynamoDB = new
      // AmazonDynamoDBClient(amazonAWSCredentials());
      // amazonDynamoDB.setEndpoint(amazonDynamoDBEndpoint);

      return amazonDynamoDB;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  // @Bean
  // public AWSCredentials amazonAWSCredentials() {
  // return new BasicAWSCredentials(amazonAWSAccessKey, amazonAWSSecretKey);
  // }

  private String getAvailablePort() {
    try (final ServerSocket serverSocket = new ServerSocket(0)) {
      return String.valueOf(serverSocket.getLocalPort());
    } catch (IOException e) {
      throw new RuntimeException("Available port was not found", e);
    }
  }
}