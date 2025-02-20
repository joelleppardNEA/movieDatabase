package com.jLepps;

import info.movito.themoviedbapi.model.movies.MovieDb;

import java.util.List;

public class addGenres {
    public void addGenres(MovieDb movie, List<String> batchQueries, String title) {
        var genres = movie.getGenres();
        for (int i = 0; i < genres.size(); i++) {
            String genreName = genres.get(i).getName();
            int genreID = genres.get(i).getId();
            batchQueries.add("WITH '"+title+"' AS movieTitle MATCH (m:Movie {title: movieTitle}) MERGE (g:Genre {ID: "+genreID+", name: '"+genreName+"'}) MERGE (m)-[:HAS_GENRE]->(g)");
        }
    }
}
