package com.jLepps;

import info.movito.themoviedbapi.model.movies.MovieDb;

import java.util.List;

public class addLanguage {
    public void addLanguages(MovieDb movie, List<String> batchQueries, String title) {
        var original = movie.getOriginalLanguage();
        batchQueries.add("MERGE (:Language {name: '"+original+"'})");
        batchQueries.add("MATCH (m:Movie {title: '" + title + "'}), (l:Language {name: '" + original + "'}) MERGE (m)-[:HAS_LANGUAGE]->(l)");
    }
}
