Vue.filter('format', function (value) {
  return new Intl.NumberFormat('en-IN', { maximumFractionDigits: 2 }).format(value);
})

var app = new Vue({
    el: '#app',
    data: {
        feature: "products_with_filtered_aggs",
        query: "autoss",
        searchResponse: null,
        filters: [],
        from : 0,
        price_from: null,
        price_to: null,
        features : {
          products_only : {
            url : "products_only"
          },
          products_with_aggs : {
            url : "products_with_aggs"
          },
          products_with_filter_aggs : {
            url : "products_with_aggs"
          },
          products_with_filtered_aggs : {
            url : "products_with_filtered_aggs"
          }
        }
    },
    watch: {
      filters: function(newFilters, oldFilters) {
        this.search();
      },
      feature: function(newFeature, oldFeature) {
        // reset filters
        this.filters.splice(0, this.filters.length);
        this.searchResponse = null
      },
      from: function(newFrom, oldFrom) {
        this.search();
      }
    },
    methods: {
      add_price_filter : function() {
        console.log("GOT PRICE", this.price_from, " TO ", this.price_to)
        index = this.filters.findIndex( function(e) { return e.key == "price" } )
        lower = this.price_from !== null ? this.price_from : ""
        upper = this.price_to !== null ? this.price_to : ""
        filter = { key: 'price', value: lower + "-" + upper, type: 'range', from: lower, to: upper}
        if (index === undefined) {
            this.filters.push(filter)
        } else {
            this.filters.splice(index, 1, filter)
        }
      },
      search : function() {
        url = this.features[this.feature].url
        query = { query: this.query, from: this.from }
        if (this.filters !== null) {
          query.filters = this.filters
        }
        console.log("URL", url, " and query ", JSON.stringify(query))
        axios
          .post("http://localhost:8080/search/" + url, query)
          .then(response => {
            console.log("RESPONSE ", response.data)
            this.searchResponse = response.data
          })
      }
    }
});
