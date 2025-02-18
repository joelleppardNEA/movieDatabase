package com.revisionCards;

import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.TmdbPeople;
import info.movito.themoviedbapi.model.movies.Credits;
import info.movito.themoviedbapi.model.movies.MovieDb;
import info.movito.themoviedbapi.model.people.Gender;
import info.movito.themoviedbapi.tools.TmdbException;

import java.io.IOException;
import java.util.List;

public class addActors {
    public void addActors(TmdbPeople tmdbPeople, Credits credits, MovieDb movie, List batchQueries) throws IOException, TmdbException {
       stringFixer stringFixer = new stringFixer();
        var cast = credits.getCast();
        int feturedActorCount = 10;
        String query = "MATCH (m:Movie {title: '"+stringFixer.fixString(movie.getTitle())+"'}) WITH m UNWIND [";
        int added = Math.min(feturedActorCount,cast.size());
        for (int i = 0; i < added; i++) {
            var actor = cast.get(i);
            var ID= actor.getId();
            var name = actor.getName();
            name = stringFixer.fixString(name);
            Gender gender;
            Double popularity;
            try {
                gender = tmdbPeople.getDetails(ID, "en-us").getGender();
                popularity = tmdbPeople.getDetails(ID, "en-us").getPopularity();
            } catch (TmdbException e) {
                throw new RuntimeException(e);
            }
            query += "{ID:"+ID+", name: '"+name+"', gender: '"+gender.name()+"', popularity: "+popularity+"}";
        if (i!=added-1){
                query += ",";
            }
        }
        query += "] AS Actor MERGE (a:Actor {ID: Actor.ID}) SET a.name = Actor.name, a.gender = Actor.gender, a.popularity = Actor.popularity MERGE (a)-[:ACTED_IN]->(m)";
        batchQueries.add(query);
    }

}
