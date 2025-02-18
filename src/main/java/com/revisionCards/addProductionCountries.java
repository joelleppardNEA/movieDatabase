package com.revisionCards;

import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.core.ProductionCountry;
import info.movito.themoviedbapi.model.movies.MovieDb;
import info.movito.themoviedbapi.tools.TmdbException;

import java.io.IOException;
import java.util.List;

public class addProductionCountries {
    public void addCountries(TmdbMovies tmdbMovies, int finalI, MovieDb movie, List<String> batchQueries) throws IOException, TmdbException {
       stringFixer stringFixer = new stringFixer();
        List<ProductionCountry> productionCountry = null;
        try {
            productionCountry = tmdbMovies.getDetails(finalI,"en-us").getProductionCountries();
        } catch (TmdbException e) {
            throw new RuntimeException(e);
        }
        for (int i = 0; i < productionCountry.size(); i++) {
            var country = stringFixer.fixString(productionCountry.get(i).getName());
            batchQueries.add("MERGE (:Country {name: '"+country+"'})");
            batchQueries.add("MATCH (m:Movie {title: '" + stringFixer.fixString(movie.getTitle()) + "'}), (c:Country {name: '" +country+ "'}) MERGE (m)-[:FILMED_IN]->(c)");

        }
    }
}
