package com.revisionCards;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbDiscover;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.TmdbPeople;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import info.movito.themoviedbapi.model.core.ProductionCompany;
import info.movito.themoviedbapi.model.core.ProductionCountry;
import info.movito.themoviedbapi.model.core.responses.TmdbResponseException;
import info.movito.themoviedbapi.model.movies.Credits;
import info.movito.themoviedbapi.model.movies.MovieDb;
import info.movito.themoviedbapi.model.people.Gender;
import info.movito.themoviedbapi.tools.TmdbException;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.neo4j.driver.exceptions.TransientException;

import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class neo4j {

    private static final String URI = "bolt://localhost:7687";
    private static final String USER = "neo4j";
    private static final String PASSWORD = "localhost";
    private static Driver driver;
    Queue<String> BatchQueries = new ConcurrentLinkedQueue<>();
    static TmdbApi tmdbApi = new TmdbApi("eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJjYWM3OGM1ZDc4MWFiNjVmY2RhZTg3Y2YwYjBlNmQ2YSIsIm5iZiI6MTczOTYyNTI5MS4yODYsInN1YiI6IjY3YjA5MzRiZjJlMDg0YWY3ZjM2MjYxZSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.9mjuwxrJVkuMy72Vk0bE2Wibv6auhfPhLnRyWwz_VAQ");
    static TmdbDiscover tmdbDiscover = tmdbApi.getDiscover();
    private static TmdbMovies tmdbMovies;
    public void connect(){

        driver = GraphDatabase.driver(URI, AuthTokens.basic(USER,PASSWORD));
       // outputDB();
    }

    public void addNodes() throws IOException {

        BufferedReader bufferedReader = new BufferedReader(new FileReader("numberUpTo.txt"));
        String stringNumber = bufferedReader.readLine();
        int number = Integer.parseInt(stringNumber);
        bufferedReader.close();
        int numberOfMoviesToBeAdded = 2;
        System.out.println(number);
        TmdbMovies tmdbMovies = tmdbApi.getMovies();
        TmdbPeople tmdbPeople = tmdbApi.getPeople();
            // Test connection
            try (Session session = driver.session()) {
                String testQuery = "RETURN 'Connected to Neo4j' AS message";
                Result result = session.run(testQuery);
                System.out.println(result.single().get("message").asString());
            } catch (Exception e) {
                System.out.println("Failed to connect to Neo4j: " + e.getMessage());
            }
        ExecutorService executorService = Executors.newFixedThreadPool(8);
        List<Future> futures = new ArrayList<>();

        ArrayList<Integer> movieIDs = new ArrayList<>();
        //star wars movie ids from TMDB website and API
        movieIDs.add(11);
        movieIDs.add(140607);
        movieIDs.add(181808);
        movieIDs.add(181812);
        movieIDs.add(1893);
        movieIDs.add(1894);

        for (int i = 0; i < movieIDs.size(); i++) {
            int finalI = i;

                futures.add(executorService.submit(() -> {
                    process(tmdbMovies, tmdbPeople, finalI,movieIDs);
                }));
        }

        for (Future<Void> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        executorService.shutdown();
        int writeNumber = number+numberOfMoviesToBeAdded;

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("numberUpTo.txt"));
        bufferedWriter.write(String.valueOf(writeNumber));
        bufferedWriter.close();
    }

    private void process(TmdbMovies tmdbMovies, TmdbPeople tmdbPeople, int finalI,ArrayList<Integer> movieIDs) {

        try {
          int temp = finalI;
          finalI = movieIDs.get(finalI);

            addActors addActors = new addActors();
            addCollection addCollection = new addCollection();
            addDirector addDirector = new addDirector();
            addGenres addGenres = new addGenres();
            addLanguage addLanguage = new addLanguage();
            addMovie addMovie = new addMovie();
            addProductionCompany addProductionCompany = new addProductionCompany();
            addProductionCountries addProductionCountries = new addProductionCountries();

            List<String> localBatchQueries = new ArrayList<>();
            Credits credits = null;
            MovieDb movie = null;

            credits = tmdbMovies.getCredits(finalI,"en-us");
            movie = tmdbMovies.getDetails(finalI, "en-us");

            addMovie.addMovies(movie,localBatchQueries);
            addActors.addActors(tmdbPeople,credits,movie,localBatchQueries);
            addCollection.addCollection(movie,localBatchQueries);
            addDirector.addDirectors(credits, movie, localBatchQueries);
            addGenres.addGenres(movie,localBatchQueries);
            addLanguage.addLanguages(movie,localBatchQueries);
            addProductionCompany.addProductionCompanies(tmdbMovies,finalI,movie,localBatchQueries);
            addProductionCountries.addCountries(tmdbMovies,finalI,movie,localBatchQueries);
            System.out.println(localBatchQueries);
            queryDatabase queryDatabase = new queryDatabase();
            queryDatabase.executeBatchWithRetry(localBatchQueries,driver);
            finalI = temp;
        } catch (TmdbResponseException e){
            System.out.println("parsed");
        }
        catch (TmdbException e) {
            System.out.println("parsed");
        }catch (IOException e) {
            System.out.println("random error idek: " + e.getMessage());
        }
    }

    public void closeDriver(){
        driver.close();
    }


}
