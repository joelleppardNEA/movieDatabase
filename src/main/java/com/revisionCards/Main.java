package com.revisionCards;

import info.movito.themoviedbapi.tools.TmdbException;

import java.io.IOException;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException, TmdbException, IOException {

//        Database database = new Database();
//        try {
//            database.accessDatabase();
//        } catch (ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }


        neo4j neo4j = new neo4j();
        neo4j.connect();
 //       neo4j.clearDB();
      neo4j.addNodes();


//       // neo4j.query("CREATE (:Movie {title: 'test tickles', year: 2000})");
        neo4j.closeDriver();

    }

}
