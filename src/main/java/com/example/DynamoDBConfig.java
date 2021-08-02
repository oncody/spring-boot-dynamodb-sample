package com.example;

import java.io.IOException;
import java.net.ServerSocket;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.local.main.ServerRunner;
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;

import org.apache.commons.lang3.StringUtils;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableDynamoDBRepositories(basePackages = "com.example.repositories")
public class DynamoDBConfig {

  private String amazonDynamoDBEndpoint = "http://localhost:8000/";
  private String amazonAWSAccessKey = "key";
  private String amazonAWSSecretKey = "key2";

  @Bean
  public AmazonDynamoDB amazonDynamoDB() {
    try {
      System.out.println("Creating local dynamoDB");
      System.setProperty("sqlite4java.library.path", "native-libs");

      System.out.println("Finding port");
      final String port = getAvailablePort();
      System.out.println("Port found: " + port);

      System.out.println("Creating server");
      DynamoDBProxyServer server = ServerRunner
          .createServerFromCommandLineArgs(new String[] { "-inMemory", "-port", port });
      System.out.println("Server created");

      System.out.println("Starting server");
      server.start();
      System.out.println("Server started");

      System.out.println("Instantiating client");
      AmazonDynamoDB amazonDynamoDB = new AmazonDynamoDBClient(new BasicAWSCredentials("access", "secret"));
      System.out.println("Client instantiated");

      System.out.println("Setting endpoint");
      amazonDynamoDB.setEndpoint("http://localhost:" + port);
      System.out.println("Endpoint set");

      // AmazonDynamoDB amazonDynamoDB = new
      // AmazonDynamoDBClient(amazonAWSCredentials());
      // if (!StringUtils.isEmpty(amazonDynamoDBEndpoint)) {
      // amazonDynamoDB.setEndpoint(amazonDynamoDBEndpoint);
      // }

      return amazonDynamoDB;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Bean
  public AWSCredentials amazonAWSCredentials() {
    return new BasicAWSCredentials(amazonAWSAccessKey, amazonAWSSecretKey);
  }

  private String getAvailablePort() {
    try (final ServerSocket serverSocket = new ServerSocket(0)) {
      return String.valueOf(serverSocket.getLocalPort());
    } catch (IOException e) {
      throw new RuntimeException("Available port was not found", e);
    }
  }
}