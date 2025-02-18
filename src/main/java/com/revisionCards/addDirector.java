package com.revisionCards;

import info.movito.themoviedbapi.model.movies.Credits;
import info.movito.themoviedbapi.model.movies.MovieDb;
import info.movito.themoviedbapi.tools.TmdbException;

import java.io.IOException;
import java.util.List;

public class addDirector {

    public void addDirectors(Credits credits, MovieDb movie, List<String> batchQueries){
        stringFixer stringFixer = new stringFixer();
        var crew = credits.getCrew();
        for (int i = 0; i < crew.size(); i++) {
            if (crew.get(i).getJob().equalsIgnoreCase("Director")){
                var name = stringFixer.fixString(crew.get(i).getName());
                batchQueries.add("MERGE (:Director {name: '"+name+"'})");
                batchQueries.add("MATCH (m:Movie {title: '" + stringFixer.fixString(movie.getTitle()) + "'}), (d:Director {name: '" + name + "'}) MERGE (d)-[:DIRECTED]->(m)");
            }
        }
    }

}
