import java.util.List;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
    @SpringBootTest
public class ProductInfoRepositoryIntegrationTest {

  AmazonDynamoDB amazonDynamoDB; 
  ProductInfoRepository repository;
  private DynamoDBMapper dynamoDBMapper;

  ProductInfoRepositoryIntegrationTest(@Autowired AmazonDynamoDB amazonDynamoDB, @Autowired ProductInfoRepository repository) {
    this.amazonDynamoDB = amazonDynamoDB;
    this.repository = repository;
  }

    @BeforeEach
    public void setup() throws Exception {
    }

    @Test
    public void givenItemWithExpectedCost_whenRunFindAll_thenItemIsFound() { 
      String EXPECTED_COST = "20";
      String EXPECTED_PRICE = "50";
      dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);
      
      CreateTableRequest tableRequest = dynamoDBMapper.generateCreateTableRequest(ProductInfo.class);
      tableRequest.setProvisionedThroughput(new ProvisionedThroughput(1L, 1L));
      amazonDynamoDB.createTable(tableRequest);
      
      dynamoDBMapper.batchDelete(
        (List<ProductInfo>)repository.findAll());
        ProductInfo productInfo = new ProductInfo(EXPECTED_COST, EXPECTED_PRICE);
        repository.save(productInfo); 
        List<ProductInfo> result = (List<ProductInfo>) repository.findAll();

        assertThat(result.size(), is(greaterThan(0)));
        assertThat(result.get(0).getCost(), is(equalTo(EXPECTED_COST))); 
    }
}