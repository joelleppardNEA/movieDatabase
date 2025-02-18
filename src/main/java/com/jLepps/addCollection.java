package com.jLepps;

import info.movito.themoviedbapi.model.movies.MovieDb;

import java.util.List;

public class addCollection {
    public void addCollection(MovieDb movie, List<String> batchQueries){
        stringFixer stringFixer = new stringFixer();
        if (movie.getBelongsToCollection() != null) {
            var collection = movie.getBelongsToCollection().getName();
            batchQueries.add("MERGE (:Collection {name: '" + stringFixer.fixString(collection) + "' })");
            batchQueries.add("MATCH (m:Movie {title: '" + stringFixer.fixString(movie.getTitle()) + "'}), (c:Collection {name: '" + stringFixer.fixString(collection) + "'}) MERGE (m)-[:BELONGS_TO_COLLECTION]->(c)");
        }
    }
}
