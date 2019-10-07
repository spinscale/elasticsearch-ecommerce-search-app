package elasticsearch.ecommerce.app.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import elasticsearch.ecommerce.app.entities.Product;
import io.micronaut.http.HttpStatus;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.open.OpenIndexRequest;
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CloseIndexRequest;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.io.Streams;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Singleton
public class ProductIndexService {

    // Found via
    // for i in $(jot 1000) ; do curl -s -X HEAD "https://picsum.photos/id/$i/200/200" -w '%{http_code} %{url_effective}\n' | tee -a /tmp/head.log ; done
    // for i in $(grep "^404" /tmp/head.log | cut -d '/' -f 5) ; do echo -n "$i, " ; done
    private static List<Integer> NON_EXISTING_IMAGE_IDS = List.of(86, 97, 105, 138, 148, 150, 205, 207, 224, 226, 245, 246, 262, 285, 286,
            298, 303, 332, 333, 346, 359, 394, 414, 422, 438, 462, 463, 470, 489, 540, 561, 578, 587, 589, 592, 595, 597, 601, 624, 632,
            636, 644, 647, 673, 697, 706, 707, 708, 709, 710, 711, 712, 713, 714, 720, 725, 734, 745, 746, 747, 748, 749, 750, 751, 752,
            753, 754, 759, 761, 762, 763, 771, 792, 801, 812, 843, 850, 854, 895, 897, 899, 917, 920, 934, 956, 963, 968);

    private static final int BRANDS_MAX = 10;
    private static final long MAX_BULK_SIZE_IN_BYTES = new ByteSizeValue(5, ByteSizeUnit.MB).getBytes();
    private static final String INDEX = "products";
    private static final Faker faker = Faker.instance(Locale.GERMAN);
    private static final Logger LOG = LoggerFactory.getLogger(ProductIndexService.class);

    private final RestHighLevelClient client;
    private final ObjectMapper mapper;

    @Inject
    public ProductIndexService(RestHighLevelClient client, ObjectMapper mapper) {
        this.client = client;
        this.mapper = mapper;
    }

    /**
     * Create some random products, the user can specify how many
     *
     * @param count Number of products to be created
     * @throws IOException
     */
    public CompletableFuture<HttpStatus> indexProducts(int count) throws IOException {
        return CompletableFuture.supplyAsync(() -> {
            try {

                Set<String> brands = new HashSet<>();
                while (brands.size() < BRANDS_MAX) {
                    brands.add(faker.company().name());
                }
                String[] brandsArray = brands.toArray(new String[0]);

                boolean exists = client.indices().exists(new GetIndexRequest(INDEX), RequestOptions.DEFAULT);
                if (exists) {
                    client.indices().delete(new DeleteIndexRequest(INDEX), RequestOptions.DEFAULT);
                }

                try (Reader readerSettings = new InputStreamReader(this.getClass().getResourceAsStream("/index-settings.json"));
                     Reader readerMappings = new InputStreamReader(this.getClass().getResourceAsStream("/index-mappings.json"))) {
                    String settings = Streams.copyToString(readerSettings);
                    String mapping = Streams.copyToString(readerMappings);
                    CreateIndexRequest createIndexRequest = new CreateIndexRequest(INDEX).settings(settings, XContentType.JSON).mapping(mapping, XContentType.JSON);
                    client.indices().create(createIndexRequest, RequestOptions.DEFAULT);
                }

                BulkRequest request = new BulkRequest();
                for (int i = 0; i < count; i++) {
                    String productName = faker.commerce().productName();
                    // This is to replace german prices with a comma with a proper decimal space...
                    double price = Double.valueOf(faker.commerce().price(1, 1000).replace(",", "."));
                    String material = faker.commerce().material();
                    String color = faker.color().name();
                    String id = faker.number().digits(20);
                    String brand = faker.options().nextElement(brandsArray);
                    // no text, we would need to deal with spaces and umlauts
                    int productImageId = faker.number().numberBetween(1, 1000);
                    while (NON_EXISTING_IMAGE_IDS.contains(productImageId)) {
                        productImageId = faker.number().numberBetween(1, 1000);
                    }
                    String productImage = "https://picsum.photos/id/" + productImageId + "/200/200?blur=1";
                    String brandLogo = faker.company().logo();
                    Date lastUpdated = faker.date().past(365, TimeUnit.DAYS);
                    int remainingStock = faker.number().numberBetween(0, 10);
                    int commission = faker.number().numberBetween(5, 20);

                    Product product = new Product(productName, price, color, material, id, productImage, brand, brandLogo, lastUpdated, remainingStock, commission);
                    IndexRequest indexRequest = new IndexRequest(INDEX, "_doc", id);
                    indexRequest.source(mapper.writeValueAsString(product), XContentType.JSON);
                    request.add(indexRequest);

                    if (request.estimatedSizeInBytes() > MAX_BULK_SIZE_IN_BYTES) {
                        BulkResponse response = client.bulk(request, RequestOptions.DEFAULT);
                        LOG.info("Indexed [{}] documents in [{}]", response.getItems().length, response.getTook());
                        request = new BulkRequest();
                    }
                }

                request.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
                BulkResponse response = client.bulk(request, RequestOptions.DEFAULT);
                LOG.info("Finished indexing run. Indexed [{}] documents in [{}]", response.getItems().length, response.getTook());
                return HttpStatus.OK;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public CompletableFuture<HttpStatus> configureSynonyms(String synonyms) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                client.indices().close(new CloseIndexRequest(INDEX), RequestOptions.DEFAULT);
                Settings settings = Settings.builder()
                        .putList("index.analysis.filter.my_synonym_filter.synonyms", synonyms.split("\n"))
                        .build();
                client.indices().putSettings(new UpdateSettingsRequest(INDEX).settings(settings), RequestOptions.DEFAULT);
                client.indices().open(new OpenIndexRequest().indices(INDEX), RequestOptions.DEFAULT);
                return HttpStatus.OK;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
