package com.jLepps;

import info.movito.themoviedbapi.model.movies.MovieDb;

import java.io.IOException;
import java.util.List;

public class addMovie {
    public void addMovies(MovieDb movie, List<String> batchQueries,String title) throws IOException {
        var popularity = movie.getPopularity();
        var releaseDate = movie.getReleaseDate();
        batchQueries.add("MERGE (:Movie {title: '" + title + "', releaseDate: '" + releaseDate + "', popularity: "+popularity+"})");
    }
}
