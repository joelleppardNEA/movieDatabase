package com.jLepps;

import info.movito.themoviedbapi.TmdbPeople;
import info.movito.themoviedbapi.model.movies.Credits;
import info.movito.themoviedbapi.model.movies.MovieDb;
import info.movito.themoviedbapi.model.people.Gender;
import info.movito.themoviedbapi.tools.TmdbException;

import java.io.IOException;
import java.util.List;

public class addActors {
    public void addActors(TmdbPeople tmdbPeople, Credits credits, List batchQueries,String title) throws IOException, TmdbException {
       stringFixer stringFixer = new stringFixer();
        var cast = credits.getCast();
        int feturedActorCount = 10;
        String query = "MATCH (m:Movie {title: '"+title+"'}) WITH m UNWIND [";
        int added = Math.min(feturedActorCount,cast.size());
        for (int i = 0; i < added; i++) {
            var actor = cast.get(i);
            var ID= actor.getId();
            var name = actor.getName();
            name = stringFixer.fixString(name);
            String gender;
            Double popularity;
            var temp = tmdbPeople.getDetails(ID, "en-us");
            gender = temp.getGender().name();
            popularity = temp.getPopularity();
            query += "{ID:"+ID+", name: '"+name+"', gender: '"+gender+"', popularity: "+popularity+"}";
        if (i!=added-1){
                query += ",";
            }
        }
        query += "] AS Actor MERGE (a:Actor {name: Actor.name}) ON CREATE SET a.ID = Actor.ID, a.gender = Actor.gender, a.popularity = Actor.popularity MERGE (a)-[:ACTED_IN]->(m)";
        batchQueries.add(query);
    }

}
