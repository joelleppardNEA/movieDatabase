package com.jLepps;

import info.movito.themoviedbapi.model.movies.Credits;
import info.movito.themoviedbapi.model.movies.MovieDb;

import java.util.List;

public class addDirector {

    public void addDirectors(Credits credits, List<String> batchQueries, String title){
        stringFixer stringFixer = new stringFixer();
        var crew = credits.getCrew();
        for (int i = 0; i < crew.size(); i++) {
            if (crew.get(i).getJob().equalsIgnoreCase("Director")){
                var name = stringFixer.fixString(crew.get(i).getName());
                batchQueries.add("MERGE (:Director {name: '"+name+"'})");
                batchQueries.add("MATCH (m:Movie {title: '" + title + "'}), (d:Director {name: '" + name + "'}) MERGE (d)-[:DIRECTED]->(m)");
            }
        }
    }

}
