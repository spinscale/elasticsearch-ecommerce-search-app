package elasticsearch.ecommerce.app.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

import java.net.URI;

@Controller
public class MainController {

    @Get
    public HttpResponse redirect() {
        return HttpResponse.redirect(URI.create("/index.html"));
    }

}
