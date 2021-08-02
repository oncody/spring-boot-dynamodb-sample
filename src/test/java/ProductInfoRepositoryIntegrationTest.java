import java.util.List;
import java.util.concurrent.TimeUnit;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.example.DynamoDBConfig;
import com.example.ProductInfo;
import com.example.repositories.ProductInfoRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

// @RunWith(SpringJUnit4ClassRunner.class)
// @SpringBootTest(classes = Application.class)
// @WebAppConfiguration
// @ActiveProfiles("local")
// @TestPropertySource(properties = { 
// "amazon.dynamodb.endpoint=http://localhost:8000/", 
// "amazon.aws.accesskey=test1", 
// "amazon.aws.secretkey=test231" })
@SpringBootTest(classes = DynamoDBConfig.class)
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

  @BeforeEach
  public void setup() throws Exception {
  }

  @Test
  @Timeout(value = 10, unit = TimeUnit.SECONDS)
  public void givenItemWithExpectedCost_whenRunFindAll_thenItemIsFound() {
    String EXPECTED_COST = "20";
    String EXPECTED_PRICE = "50";
    ProductInfo productInfo = new ProductInfo(EXPECTED_COST, EXPECTED_PRICE);

    System.out.println("Creating table");
    CreateTableRequest tableRequest = dynamoDBMapper.generateCreateTableRequest(ProductInfo.class);
    tableRequest.setProvisionedThroughput(new ProvisionedThroughput(1L, 1L));
    amazonDynamoDB.createTable(tableRequest);
    System.out.println("Table created");

    System.out.println("Deleting records");
    dynamoDBMapper.batchDelete((List<ProductInfo>) repository.findAll());
    System.out.println("Records Deleted");

    System.out.println("Adding record");
    repository.save(productInfo);
    System.out.println("Record added");

    System.out.println("Fetching records");
    List<ProductInfo> result = (List<ProductInfo>) repository.findAll();
    System.out.println("Records fetched");

    assertThat(result.size(), is(greaterThan(0)));
    assertThat(result.get(0).getCost(), is(equalTo(EXPECTED_COST)));
  }
}