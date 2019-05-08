package elasticsearch.ecommerce.app.factory;

import io.micronaut.context.annotation.Factory;
import org.apache.http.HttpHost;
import org.apache.lucene.util.IOUtils;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;

@Factory
public class HighLevelRestClientFactory {

    private final RestHighLevelClient client;

    @Inject
    public HighLevelRestClientFactory() {
        this.client = new RestHighLevelClient(RestClient.builder(new HttpHost("localhost", 9200, "http")));
    }

    @Singleton
    public RestHighLevelClient getRestHighLevelClient() {
        return client;
    }

    @PreDestroy
    public void closeClient() {
        IOUtils.closeWhileHandlingException(client);
    }
}
