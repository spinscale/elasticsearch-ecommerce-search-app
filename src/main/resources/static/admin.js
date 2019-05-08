var app = new Vue({
    el: '#app',
    data: {
      message: "",
      numberOfProducts: 100000,
      synonyms: 'orangsch => orange\nkfz => autos,\nplastikk => plastik'
    },
    methods: {
      reindex : function() {
        this.message = "Reindexing..."
        axios
          .post("http://localhost:8080/admin/index_data?numberOfProducts=" + this.numberOfProducts)
          .then(response => ( this.message = "" ))
          .catch(error => this.message = "Error reindexing: " + JSON.stringify(error.response.data))
      },
      configure_synonyms: function() {
        this.message = "Configuring synonyms..."
        axios
          .post("http://localhost:8080/admin/configure_synonyms", {synonyms: this.synonyms})
          .then(response => {
            console.log("IN RESPONSE123 ", response)
            this.message = ""
          })
          .catch(error => this.message = "Error updating synonyms: " + JSON.stringify(error.response.data))
       }
    }
});
