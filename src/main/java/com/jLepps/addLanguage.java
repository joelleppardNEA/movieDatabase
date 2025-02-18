package com.jLepps;

import info.movito.themoviedbapi.model.movies.MovieDb;

import java.util.List;

public class addLanguage {
    public void addLanguages(MovieDb movie, List<String> batchQueries) {
      stringFixer stringFixer = new stringFixer();
        batchQueries.add("MERGE (:Language {name: '"+movie.getOriginalLanguage()+"'})");
        batchQueries.add("MATCH (m:Movie {title: '" + stringFixer.fixString(movie.getTitle()) + "'}), (l:Language {name: '" + movie.getOriginalLanguage() + "'}) MERGE (m)-[:HAS_LANGUAGE]->(l)");
    }
}
