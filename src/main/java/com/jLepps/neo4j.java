package com.jLepps;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbDiscover;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.TmdbPeople;
import info.movito.themoviedbapi.model.core.responses.TmdbResponseException;
import info.movito.themoviedbapi.model.movies.Credits;
import info.movito.themoviedbapi.model.movies.MovieDb;
import info.movito.themoviedbapi.tools.TmdbException;
import org.neo4j.driver.*;

import java.io.*;
import java.net.http.HttpClient;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class neo4j {

    private static final String URI = "bolt://localhost:7687";
    private static final String USER = "neo4j";
    private static final String PASSWORD = "localhost";
    private static Driver driver;
    HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)  // Force HTTP/1.1
            .build();
    static TmdbApi tmdbApi = new TmdbApi("eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJjYWM3OGM1ZDc4MWFiNjVmY2RhZTg3Y2YwYjBlNmQ2YSIsIm5iZiI6MTczOTYyNTI5MS4yODYsInN1YiI6IjY3YjA5MzRiZjJlMDg0YWY3ZjM2MjYxZSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.9mjuwxrJVkuMy72Vk0bE2Wibv6auhfPhLnRyWwz_VAQ");
    public void connect(){
        driver = GraphDatabase.driver(URI, AuthTokens.basic(USER,PASSWORD));
        // outputDB();
    }

    public void addNodes() throws IOException {

        BufferedReader bufferedReader = new BufferedReader(new FileReader("numberUpTo.txt"));
        String stringNumber = bufferedReader.readLine();
        int number = Integer.parseInt(stringNumber);
        bufferedReader.close();
        int numberOfMoviesToBeAdded = 200;
        System.out.println(number);
        TmdbMovies tmdbMovies = tmdbApi.getMovies();
        TmdbPeople tmdbPeople = tmdbApi.getPeople();
        ExecutorService executorService = Executors.newFixedThreadPool(8);
        List<Future> futures = new ArrayList<>();

        addFromGenre add = new addFromGenre();
        try {
            add.addFromGenre(tmdbApi,driver);
        } catch (TmdbException e) {
            throw new RuntimeException(e);
        }

        //star wars movie ids from TMDB website and API
//        movieIDs.add(11);
//        movieIDs.add(140607);
//        movieIDs.add(181808);
//        movieIDs.add(181812);
//        movieIDs.add(1893);
//        movieIDs.add(1894);
        for (int i = number; i < number+numberOfMoviesToBeAdded; i++) {
            int finalI = i;

                futures.add(executorService.submit(() -> {
                //    processDefault(tmdbMovies, tmdbPeople, finalI);
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

    public void closeDriver(){
        driver.close();
    }


}
