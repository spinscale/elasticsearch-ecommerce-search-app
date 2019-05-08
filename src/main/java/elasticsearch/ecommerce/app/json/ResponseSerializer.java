package elasticsearch.ecommerce.app.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.elasticsearch.client.Response;
import org.elasticsearch.common.io.Streams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Custom serializer for low level requests returned from a search operation
 */
@Singleton
public class ResponseSerializer extends JsonSerializer<Response> {

    private static final Logger LOG = LoggerFactory.getLogger(ResponseSerializer.class);

    @Override
    public void serialize(Response response, JsonGenerator gen, SerializerProvider provider) throws IOException {
        try (Reader in = new InputStreamReader(response.getEntity().getContent())) {
            gen.writeRaw(Streams.copyToString(in));
        }
    }
}
