package com.revisionCards;

import info.movito.themoviedbapi.tools.TmdbException;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.exceptions.TransientException;

import java.io.IOException;
import java.util.List;

public class queryDatabase {

    public Result query(String query, Driver driver) throws TmdbException, IOException {
        Result result;
        try (Session session = driver.session()){
            result = session.run(query);
        }
        System.out.println("DB queried");
        return result;
    }

    public void executeBatchWithRetry(List<String> batchQueries, Driver driver) {
        if (batchQueries.isEmpty()) return;
        int retryAttempts = 5;
        while (retryAttempts > 0) {
            try (Session session = driver.session()) {
                try (Transaction transaction = session.beginTransaction()) {
                    for (String query : batchQueries) {
                        transaction.run(query);
                    }
                    transaction.commit();
                    break;
                }
            } catch (TransientException e) {
                retryAttempts--;
                System.out.println("Transient lock issue, retrying... Remaining attempts: " + retryAttempts);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public void clearDB(Driver driver) throws IOException, TmdbException {
        query("MATCH (n) DETACH DELETE n", driver);
    }

    private int outputDBMovies(Driver driver) {
        try (Session session = driver.session()) {
            String query = "MATCH (m:Movie) RETURN m.title,m.year";
            var result = session.run(query);
            int count = 0;
            while (result.hasNext()) {
                count++;
                Record record = result.next();
                System.out.println("- " + record.get("m.title").asString());
                System.out.println("- " + record.get("m.year"));
            }
            return count;

        }
    }

    public void getMoviesInCatagory(String genre, Driver driver) throws IOException, TmdbException {
        String query = ("MATCH (g:Genre) return g.name,g.ID");

        Result result;
        try (Session session = driver.session()){
            result = session.run(query);
            while (result.hasNext()){
                Record record = result.next();
                System.out.println(""+record.get("g.name").asString() + " | ID: " + record.get("g.ID"));
            };
        }
    }

}
