import java.util.List;
import java.util.concurrent.TimeUnit;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.example.Config;
import com.example.Product;
import com.example.repo.ProductRepo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest(classes = Config.class)
public class DynamoTest {

  AmazonDynamoDB amazonDynamoDB;
  ProductRepo repository;
  private DynamoDBMapper dynamoDBMapper;

  DynamoTest(@Autowired AmazonDynamoDB amazonDynamoDB, @Autowired ProductRepo repository) {
    this.amazonDynamoDB = amazonDynamoDB;
    this.repository = repository;
    this.dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);
  }

  @Test
  @Timeout(value = 10, unit = TimeUnit.SECONDS)
  public void testProductTable() {
    String EXPECTED_COST = "20";
    String EXPECTED_PRICE = "50";

    CreateTableRequest tableRequest = dynamoDBMapper.generateCreateTableRequest(Product.class);
    tableRequest.setProvisionedThroughput(new ProvisionedThroughput(1L, 1L));
    amazonDynamoDB.createTable(tableRequest);
    dynamoDBMapper.batchDelete((List<Product>) repository.findAll());

    List<Product> resultBefore = (List<Product>) repository.findAll();
    assertThat(resultBefore.size(), is(equalTo(0)));

    repository.save(new Product(EXPECTED_COST, EXPECTED_PRICE));
    List<Product> resultAfter = (List<Product>) repository.findAll();

    assertThat(resultAfter.size(), is(equalTo(1)));

    Product productFound = resultAfter.get(0);
    assertThat(productFound.getCost(), is(equalTo(EXPECTED_COST)));
    assertThat(productFound.getPrice(), is(equalTo(EXPECTED_PRICE)));
  }
}