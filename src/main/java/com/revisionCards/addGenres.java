package com.revisionCards;

import info.movito.themoviedbapi.model.movies.MovieDb;
import info.movito.themoviedbapi.tools.TmdbException;

import java.io.IOException;
import java.util.List;

public class addGenres {
    public void addGenres(MovieDb movie, List<String> batchQueries) {
        stringFixer stringFixer = new stringFixer();
        var genres = movie.getGenres();
        for (int i = 0; i < genres.size(); i++) {
            String genreName = genres.get(i).getName();
            int genreID = genres.get(i).getId();
            batchQueries.add("MERGE (g:Genre {ID: "+genreID+", name: '"+genreName+"'})");
            batchQueries.add("WITH '"+stringFixer.fixString(movie.getTitle())+"' AS movieTitle MATCH (m:Movie {title: movieTitle}) MERGE (g:Genre {ID: "+genreID+", name: '"+genreName+"'}) MERGE (m) -[:HAS_GENRE]->(g)");
        }
    }
}
