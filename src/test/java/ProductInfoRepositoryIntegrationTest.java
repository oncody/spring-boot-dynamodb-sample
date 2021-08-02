import java.util.List;
import java.util.concurrent.TimeUnit;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.example.Config;
import com.example.ProductInfo;
import com.example.repositories.ProductInfoRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest(classes = Config.class)
public class ProductInfoRepositoryIntegrationTest {

  AmazonDynamoDB amazonDynamoDB;
  ProductInfoRepository repository;
  private DynamoDBMapper dynamoDBMapper;

  ProductInfoRepositoryIntegrationTest(@Autowired AmazonDynamoDB amazonDynamoDB,
      @Autowired ProductInfoRepository repository) {
    this.amazonDynamoDB = amazonDynamoDB;
    this.repository = repository;
    this.dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);
  }

  @Test
  @Timeout(value = 10, unit = TimeUnit.SECONDS)
  public void givenItemWithExpectedCost_whenRunFindAll_thenItemIsFound() {
    String EXPECTED_COST = "20";
    String EXPECTED_PRICE = "50";
    ProductInfo productInfo = new ProductInfo(EXPECTED_COST, EXPECTED_PRICE);

    CreateTableRequest tableRequest = dynamoDBMapper.generateCreateTableRequest(ProductInfo.class);
    tableRequest.setProvisionedThroughput(new ProvisionedThroughput(1L, 1L));
    amazonDynamoDB.createTable(tableRequest);
    dynamoDBMapper.batchDelete((List<ProductInfo>) repository.findAll());

    List<ProductInfo> resultBefore = (List<ProductInfo>) repository.findAll();
    assertThat(resultBefore.size(), is(equalTo(0)));

    repository.save(productInfo);
    List<ProductInfo> resultAfter = (List<ProductInfo>) repository.findAll();

    assertThat(resultAfter.size(), is(equalTo(1)));
    assertThat(resultAfter.get(0).getCost(), is(equalTo(EXPECTED_COST)));
    assertThat(resultAfter.get(0).getMsrp(), is(equalTo(EXPECTED_PRICE)));
  }
}