package com.jLepps;

import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.core.ProductionCompany;
import info.movito.themoviedbapi.model.movies.MovieDb;
import info.movito.themoviedbapi.tools.TmdbException;

import java.io.IOException;
import java.util.List;

public class addProductionCompany {

    public void addProductionCompanies(TmdbMovies tmdbMovies, int finalI, List<String> batchQueries,String title) throws IOException {
        stringFixer stringFixer = new stringFixer();
        try {
            List<ProductionCompany> productionCompany = tmdbMovies.getDetails(finalI,"en-us").getProductionCompanies();
            for (int i = 0; i < productionCompany.size(); i++) {
                var companyName = stringFixer.fixString(productionCompany.get(i).getName());
                batchQueries.add("MERGE (:Company {name: '"+companyName+"'})");
                batchQueries.add("MATCH (m:Movie {title: '" + title + "'}), (p:Company {name: '" +companyName+ "'}) MERGE (m)-[:PRODUCED_BY]->(p)");

            }
        } catch (TmdbException e) {
            throw new RuntimeException(e);
        }
    }

}
