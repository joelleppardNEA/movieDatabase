package com.jLepps;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbDiscover;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.TmdbPeople;
import info.movito.themoviedbapi.model.core.Genre;
import info.movito.themoviedbapi.model.core.Movie;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import info.movito.themoviedbapi.model.core.responses.TmdbResponseException;
import info.movito.themoviedbapi.model.movies.Credits;
import info.movito.themoviedbapi.model.movies.MovieDb;
import info.movito.themoviedbapi.tools.TmdbException;
import info.movito.themoviedbapi.tools.builders.discover.DiscoverMovieParamBuilder;
import org.neo4j.driver.Driver;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class addFromGenre {
    addActors addActors = new addActors();
    addCollection addCollection = new addCollection();
    addDirector addDirector = new addDirector();
    addGenres addGenres = new addGenres();
    addLanguage addLanguage = new addLanguage();
    addMovie addMovie = new addMovie();
    addProductionCompany addProductionCompany = new addProductionCompany();
    addProductionCountries addProductionCountries = new addProductionCountries();
    queryDatabase queryDatabase = new queryDatabase();

    public void addFromGenre(TmdbApi tmdbApi, Driver driver) throws TmdbException {
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
        builder.withGenres(genreInt, true);

        MovieResultsPage moviesStack = tmdbApi.getDiscover().getMovie(builder.page(1));
        List<MovieDb> movies = new ArrayList<>();

        movies.clear();
        TmdbMovies tmdbMovies = new TmdbMovies(tmdbApi);
        int n = 2;
        long before = System.currentTimeMillis();
        for (int i = 1; i < n+1; i++) {
            MovieResultsPage tempMovieStack = tmdbApi.getDiscover().getMovie(builder.page(i));
          // batchAddByPage(tempMovieStack,tmdbMovies,tmdbApi,driver);
                tempMovieStack.getResults().parallelStream().forEach(movie -> {
                    process(tempMovieStack,tmdbMovies,tmdbApi,driver,movie.getId());
                });
        }
        long after = System.currentTimeMillis();
        System.out.println("--");
        System.out.println(after - before);
        System.out.println("--");

        System.out.println(movies.size());
        System.out.println(moviesStack.getTotalResults());
    }


    private void batchAddByPage(MovieResultsPage tempMovieStack, TmdbMovies tmdbMovies,TmdbApi tmdbApi,Driver driver) {
        Credits credits;
        MovieDb movie;
        TmdbPeople people;
        for (int i = 0; i < tempMovieStack.getResults().size(); i++) {
            int ID = tempMovieStack.getResults().get(i).getId();
            try {

                List<String> localBatchQueries = new ArrayList<>();
                credits = tmdbMovies.getCredits(ID,"en-us");
                movie = tmdbMovies.getDetails(ID, "en-us");
                people = tmdbApi.getPeople();

                addMovie.addMovies(movie,localBatchQueries);
                addActors.addActors(people,credits,movie,localBatchQueries);
                addCollection.addCollection(movie,localBatchQueries);
                addDirector.addDirectors(credits, movie, localBatchQueries);
                addGenres.addGenres(movie,localBatchQueries);
                addLanguage.addLanguages(movie,localBatchQueries);
                addProductionCompany.addProductionCompanies(tmdbMovies,ID,movie,localBatchQueries);
                addProductionCountries.addCountries(tmdbMovies,ID,movie,localBatchQueries);
                System.out.println(localBatchQueries);
                queryDatabase.executeBatchWithRetry(localBatchQueries,driver);
            } catch (TmdbResponseException e){
                System.out.println("parsed");
            } catch (TmdbException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void process(MovieResultsPage tempMovieStack, TmdbMovies tmdbMovies,TmdbApi tmdbApi,Driver driver, int ID) {

        try {
            Credits credits;
            MovieDb movie;
            TmdbPeople people;

            List<String> localBatchQueries = new ArrayList<>();
            credits = tmdbMovies.getCredits(ID,"en-us");
            movie = tmdbMovies.getDetails(ID, "en-us");
            people = tmdbApi.getPeople();

            addMovie.addMovies(movie,localBatchQueries);
            addActors.addActors(people,credits,movie,localBatchQueries);
            addCollection.addCollection(movie,localBatchQueries);
            addDirector.addDirectors(credits, movie, localBatchQueries);
            addGenres.addGenres(movie,localBatchQueries);
            addLanguage.addLanguages(movie,localBatchQueries);
            addProductionCompany.addProductionCompanies(tmdbMovies,ID,movie,localBatchQueries);
            addProductionCountries.addCountries(tmdbMovies,ID,movie,localBatchQueries);
            System.out.println(localBatchQueries);
            queryDatabase.executeBatchWithRetry(localBatchQueries,driver);
        } catch (TmdbResponseException e){
            System.out.println("parsed");
        } catch (TmdbException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    ///TODO
    /// - get list of movie IDs in the genre picked
    /// - call the process class to add all of the new movies


}
