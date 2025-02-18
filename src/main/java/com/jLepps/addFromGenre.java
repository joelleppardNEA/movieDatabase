package com.jLepps;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.core.Genre;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import info.movito.themoviedbapi.tools.TmdbException;
import info.movito.themoviedbapi.tools.builders.discover.DiscoverMovieParamBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class addFromGenre {
    public void addFromGenre(TmdbApi tmdbApi) throws TmdbException {
        List<Genre> list;
        try {
          list = tmdbApi.getGenre().getMovieList("en-us");
        } catch (TmdbException e) {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i).getName() + " :-: " + list.get(i).getId());
        }
        System.out.println("---------------");
        System.out.println("here is the list of genres to pick from,");
        System.out.println("please enter the ID next to the name");
        System.out.println("---------------");
        Scanner scan = new Scanner(System.in);
        List<Integer> genreInt = new ArrayList<>();
        genreInt.add(scan.nextInt());
        DiscoverMovieParamBuilder builder = new DiscoverMovieParamBuilder();

        List<MovieResultsPage> movies = Collections.singletonList(tmdbApi.getDiscover().getMovie(builder));
        for (int i = 0; i < movies.size(); i++) {
            System.out.println(movies.get(i).getId());
        }
    }

    ///TODO
    /// - get list of movie IDs in the genre picked
    /// - call the process class to add all of the new movies


}
