package com.revisionCards;

import info.movito.themoviedbapi.model.movies.MovieDb;
import info.movito.themoviedbapi.tools.TmdbException;

import java.io.IOException;
import java.util.List;

public class addLanguage {
    public void addLanguages(MovieDb movie, List<String> batchQueries) {
      stringFixer stringFixer = new stringFixer();
        batchQueries.add("MERGE (:Language {name: '"+movie.getOriginalLanguage()+"'})");
        batchQueries.add("MATCH (m:Movie {title: '" + stringFixer.fixString(movie.getTitle()) + "'}), (l:Language {name: '" + movie.getOriginalLanguage() + "'}) MERGE (m)-[:HAS_LANGUAGE]->(l)");
    }
}
