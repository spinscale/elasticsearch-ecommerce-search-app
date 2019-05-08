package elasticsearch.ecommerce.app.entities;

import org.elasticsearch.common.Strings;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;

import java.util.ArrayList;
import java.util.List;

public class Query {

    private String query;
    private Integer from = 0;
    private List<Filter> filters = new ArrayList<>();

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<Filter> getFilters() {
        return filters;
    }

    public void setFilters(List<Filter> filters) {
        this.filters = filters;
    }

    public Integer getFrom() {
        return from;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }

    public static final class Filter {
        private String key;
        private String value;
        private String from;
        private String to;
        private String type;

        public String getType() {
            return type;
        }

        public String getFrom() {
            return from;
        }

        public String getTo() {
            return to;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        public QueryBuilder toQuery() {
            if ("term".equals(type)) {
                return QueryBuilders.termQuery(this.key + ".keyword", this.value);
            } else if ("range".equals(type)) {
                return createRangeQueryBuilder(key, from, to);
            } else {
                throw new RuntimeException("Unknown type: " + type);
            }
        }

        private RangeQueryBuilder createRangeQueryBuilder(String name, String from, String to) {
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(name);
            if (Strings.isEmpty(from) == false) {
                rangeQueryBuilder.from(from);
            }
            if (Strings.isEmpty(to) == false) {
                rangeQueryBuilder.to(to);
            }

            return rangeQueryBuilder;
        }
    }
}
