package com.jLepps;

import info.movito.themoviedbapi.model.movies.MovieDb;

import java.io.IOException;
import java.util.List;

public class addMovie {
    public void addMovies(MovieDb movie, List<String> batchQueries) throws IOException {
        stringFixer stringFixer = new stringFixer();
        var title = stringFixer.fixString(movie.getTitle());
        var popularity = movie.getPopularity();
        System.out.println(title);
        var releaseDate = movie.getReleaseDate();
        batchQueries.add("MERGE (:Movie {title: '" + title + "', releaseDate: '" + releaseDate + "', popularity: "+popularity+"})");
    }
}
