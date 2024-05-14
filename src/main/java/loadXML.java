
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.transform.Result;


import java.io.IOException;
import java.io.PrintWriter;

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

public class loadXML {
    public int insertMovies(Connection conn, List<List<String>> listOfFilms) throws SQLException {

        int count = 0;
        int roundCount = 0;
        int totalProceesed =0;
        int totalInserted = 0;

        System.out.println("##############################\nInserting Movies into database");

        for(int i = 0;i<listOfFilms.size();i++){
//            String insertLine = "INSERT INTO movies(id, title, year, xmlLoader.director) VALUES(?, ?, ?, ?);";
            totalProceesed++;
            //this only inserti nto the tabel if the movie does not exist:
            String insertLine = "INSERT INTO movies (id ,title, year, director)\n" +
                    "SELECT ?, ?, ?, ? " +
                    "WHERE NOT EXISTS (\n" +
                    "    SELECT 1 FROM movies WHERE id = ?" +
                    ");";
            List<String> line = listOfFilms.get(i);
            boolean yearNumeric = true;
            int year = 0;
            try{
                year = Integer.parseInt(line.get(2));
                yearNumeric = true;
            }
            catch(Exception e){
                yearNumeric = false;
            }
            if(yearNumeric == false){
                System.out.println("ERROR: Record year not numeric: " + line);
                continue;
            }

            String title = line.get(1);
            String director = line.get(3);
            String id = line.get(0);
            if(title.isBlank()){
                System.out.println("ERROR: title is blank for " + line);
                continue;
            }
            if(director.isBlank()){
                System.out.println("ERROR: Director is blank " + line);
                continue;
            }
            if(id.isBlank()){
                System.out.println("ERROR: ID is blank " + line);
                continue;
            }

            PreparedStatement ps = conn.prepareStatement(insertLine);
            ps.setString(1, line.get(0));
            ps.setString(2, line.get(1));
            ps.setInt(3, year);
            ps.setString(4, line.get(3));
            ps.setString(5, line.get(0));
//            break;
            int a = ps.executeUpdate();
            if(a>0){
//                System.out.println("Successfully inserted movieid " + line.get(1) + ", line :" + line);
                if(count > 500){
                    System.out.println("SUCESS: Inserted " + roundCount * 500 + " records into the database");
                    count = 0;
                    roundCount++;
                }
                count++;
                totalInserted++;
            }
            else{
                System.out.println("ERROR: Existing movieid : " + line.get(1) + " Found");
            }
            ps.close();
        }

        System.out.println("FINISHED: Total Inserted: " + totalInserted + " out of " + totalProceesed + " proccessed");
        return 1;
    }

    public int insertGenres(Connection conn, List<List<String>> genres) throws SQLException{

        System.out.println("##############################\nInserting Genres into database");
        int totalInserted = 0;
        int totalProcessed = 0;
        for(int i = 0;i<genres.size();i++){
            // [1] is genreName, [0] is movie_id
            List<String> genre_in_movies = genres.get(i);
            //use the stored procedure for this:
            /**
             *
             * CALL add_genre("action123", "AA13",@status);
             * SELECT @status;
             */

//            System.out.println(genre_in_movies);
            totalProcessed++;
            String query = "CALL add_genre(?, ?, @status);";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, genre_in_movies.get(1));
            ps.setString(2, genre_in_movies.get(0));

            ps.executeQuery();
            ps.close();
            String callQuery = "SELECT @status as st;";
            PreparedStatement ps1 = conn.prepareStatement(callQuery);
            ResultSet rs = ps1.executeQuery();
            while(rs.next()){
                String status = rs.getString("st");
                if(status.length() > 0){
                    try{
                        int i_status = Integer.parseInt(status);
                        if(i_status < 0){
                            throw new IOException();
                        }
//                        System.out.println("Success inserting genre_in_movies id: "+ i_status);
                        if(totalInserted % 500 == 0){
                            System.out.println("SUCCESS: Inserted " + totalInserted + " genres_in_movies");
                        }

                        totalInserted++;

                    }
                    catch(Exception e){
                        System.out.println("ERROR: failed inserting genre_in_movies for genre " + genre_in_movies.get(0)  + " and movie_id " + genre_in_movies.get(1));
                    }
                }
            }
            ps1.close();
//            ps.close();
        }

        System.out.println("FINISHED: Total Successed Genres_in_movies: " + totalInserted + " out of " + totalProcessed);
        return 1;
    }


    public void runProgram() throws Exception{

        // Incorporate mySQL driver
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection("jdbc:" + Parameters.dbtype + ":///" + Parameters.dbname + "?autoReconnect=true&useSSL=false",
                Parameters.username, Parameters.password);
        try {
            loadFilms spe = new loadFilms("test.xml");
            spe.runProgram();

            List<film> unique = spe.processUniqueFilms();
            List<List<String>> l = spe.exportMovies(unique);
            List<List<String>> g = spe.exportGenres(unique);
            System.out.println("Total films: " + l.size());
            System.out.println("Total genres: " + g.size());

            insertMovies(conn, l);
            insertGenres(conn, g);

//            JsonObject json = new JsonObject();
        } catch (Exception e) {

            // Write error message JSON object to output
            System.out.println(e.getMessage());
        }
        conn.close();

    }
    public static void main(String[] args) {

       try{
           loadXML x = new loadXML();
           x.runProgram();
       }
       catch(Exception e){
           System.out.println("Error while handling program: " + e.getMessage());
           System.out.println(e.getStackTrace());
       }

    }
}
