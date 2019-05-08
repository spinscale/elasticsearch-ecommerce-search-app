package elasticsearch.ecommerce.app.controller;

import elasticsearch.ecommerce.app.service.ProductIndexService;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.QueryValue;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Controller("/admin")
public class AdminController {

    private final ProductIndexService indexService;

    @Inject
    public AdminController(ProductIndexService indexService) {
        this.indexService = indexService;
    }

    @Post("/index_data")
    public CompletableFuture<HttpStatus> index(@QueryValue Integer numberOfProducts) throws IOException {
        if (numberOfProducts <= 0) {
            numberOfProducts = 50000;
        }

        return indexService.indexProducts(numberOfProducts);
    }

    @Post(value = "configure_synonyms", consumes = MediaType.APPLICATION_JSON)
    public CompletableFuture<HttpStatus> index(@Body Map<String, String> synonyms) throws IOException {
        return indexService.configureSynonyms(synonyms.get("synonyms"));
    }

}
