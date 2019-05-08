package elasticsearch.ecommerce.app.controller;

import elasticsearch.ecommerce.app.entities.Query;
import elasticsearch.ecommerce.app.service.ProductQueryService;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import org.elasticsearch.client.Response;

import javax.inject.Inject;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Controller("/search")
public class SearchController {

    private final ProductQueryService service;

    @Inject
    public SearchController(ProductQueryService service) {
        this.service = service;
    }

    @Post(value = "products_only", produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
    public CompletableFuture<Response> searchProductsOnly(@Body Query query) throws IOException {
        return service.searchProductsOnly(query);
    }

    @Post(value = "products_with_aggs", produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
    public CompletableFuture<Response> searchWithAggs(@Body Query query) throws IOException {
        return service.searchWithAggs(query);
    }

    @Post(value = "products_with_filtered_aggs", produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
    public CompletableFuture<Response> searchWithFilteredAggs(@Body Query query) throws IOException {
        return service.searchWithFilteredAggs(query);
    }
}
