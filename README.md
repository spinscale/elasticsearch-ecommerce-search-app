# Elasticsearch Ecommerce Search App

This repository features a simple and small search UI for fake products in
order to demo Elasticsearch search and aggregation functionality.

![Sample screenshot](/images/overview.png?raw=true "Sample screenshot")

You need docker-compose and java 12 in order to run this. Just clone the
repository and run

```
docker-compose up
# open a new terminal and run
./gradlew run
```

This will start Elasticsearch on port 9200, Kibana on port 5601 and the
micronaut based web application on port 8080.

There are two URLs you can visit. First [the main
URL](http://localhost:8080), which contains the frontend, second the
[administrative URL](http://localhost:8080/admin.html).

## Technologies used

This demo uses several other frameworks to keep it's own code small and
lean.

* [Micronaut](https://micronaut.io) is a JVM based web framework. It has a
  small footprint and very fast startup time. This app usually starts up
  in 1.5s on my rather slow notebook.
* [Vue.js](https://vuejs.org) is a JavaScript framework for the frontend
  helping to write this single page application.
* [bulma](https://bulma.io) is very easy to use, yet good looking CSS
  framework based on flexbox. I've never used it before, but will use it a
  lot more in the future.
* [docker-compose](https://docs.docker.com/compose/) helps running
  multiple docker containers
* [Elasticsearch](https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html)
  is a full text search engine doing all the hard work. Also, the Java
  Application uses the Elasticsearch Java Client to query Elasticsearch.
* [Kibana](https://www.elastic.co/guide/en/kibana/current/index.html) is
  not strictly required, but will help during the demo in case you want to
  execute searches against the Elasticsearch instance.

## Features

First, go to the admin page and click on the `reindex` button. Check the
log output of the gradle window and wait until indexation is finished.

Then go back to the main page and enter something like `autos` in the
search window and you just see some search hits. The fake data generated
by the reindex action is in german by default, but you can change the
locale in the `ProductIndexService` class.

If you are in a search view that allows to select filters, you can filter
by certain product features like brand or material by clicking on them.
You can remove the filter by clicking the `x` in the list of tags.

Pagination is supported as well.

Every query is logged in the webapp, so you can copy it over into the console.

Every search response is directly forwarded back to the browser.

A search request to Elasticsearch is constructed from the data sent to the webapp.
A request can look like this

```
{  
  "query":"autos",
  "from":0,
  "filters": [
    {"key":"material","value":"Stahl","type":"term"},
    {"key":"brand","value":"Cleem GmbH","type":"term"},
    {"key":"stock","value":"1-","type":"range","from":1}
  ]
}
```

The `filters` part will be used to create the aggregation.

### Different search types

You can select between different search types.

#### Search products only

You can select different modes of operation right next to the search bar

##### `Products` view 

![Products only](/images/search-products.png?raw=true "Products only")

This view features a list of products returned by the search query.

##### `Products & Aggregations` view 

![Aggs & counts](/images/search-aggs-filterable.png?raw=true "Aggs & counts")

This view contains aggregations on the left including counts.

##### `Products & Selectable Aggs` view 

![Selectable Aggs](/images/search-aggs-filterable.png?raw=true "Selectable Aggs")

This view allows to filter products by selecting aggregations on the left.

##### `Products & Selectable Filtered Aggs` view 

![Filtered Aggs](/images/search-aggs-filterable.png?raw=true "Filtered Aggs")

This view tries to fix the counts when selecting aggregations.

### Admin interface

The admin interface allows you to maintain synonyms or reindex your data.

![Admin Interface](/images/admin.png?raw=true "Admin interface")

When clicking the button to configure synonyms, the index will be closed, the
synonyms will be applied and then opened again. Note, that there is no error
handling, so if you create invalid synonyms, then you might need to reopen the
index manually.


# TODO

A few things will be added over time, some are just here to show, that
such a small prototype is light years away from a real search
implementation. It rather serves as a basis for discussion.

* Score based on commission
* Rank feature or dense/sparse vector for recommendation?
* Suggestions/Did you mean functionality
* Switch pagination to search after? Implement a hard cut off?
* Packaging the app will result in the HTML files not being found very likely


