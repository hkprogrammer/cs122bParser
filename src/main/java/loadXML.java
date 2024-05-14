
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

    String max_increment(String str){
        //parse the string:
        //find the right most integer:
        if(str.equals("")) {
            return "1";
        }
        int left = -1;
        int right = 0;
        for(int i = 0;i<str.length();i++){
//            System.out.println(str.charAt(i));
            if(Character.isDigit(str.charAt(i)) && left == -1){
//                System.out.println(str.charAt(i));
//                System.out.println("left found!");
//                System.out.println(i);
                left = i;
                right = -1;
            }
            if(Character.isAlphabetic(str.charAt(i)) && right == -1){
                right = i;
            }
        }
        //increment by 1:
//        System.out.println(left);
//        System.out.println(right);
        if(right == -1 && left >= 0){
            right = str.length()-1;
        }
        if(left == -1 || right == -1){
            return "0";
        }

        String val = str.substring(left,right+1);
        int newVal = Integer.parseInt(val)+1;
        String s_val = String.valueOf(newVal);
        String newMax = str.substring(0, left) + s_val + str.substring(right+1, str.length());
        return newMax;

    }



    public int insertMovies(Connection conn, List<List<String>> listOfFilms) throws SQLException {

        int count = 0;
        int roundCount = 0;
        int totalProceesed =0;
        int totalInserted = 0;

        System.out.println("\n\n##############################\nInserting Movies into database");

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

        System.out.println("\n\n##############################\nInserting Genres into database");
        int totalInserted = 0;
        int totalProcessed = 0;
        for(int i = 0;i<genres.size();i++){
            // [1] is genreName, [0] is movie_id
            List<String> genre_in_movies = genres.get(i);
            totalProcessed++;

            //use the stored procedure for this:
            /**
             *
             * CALL add_genre("action123", "AA13",@status);
             * SELECT @status;
             */

            if(genre_in_movies.get(0).isBlank() || genre_in_movies.get(1).isBlank()){
                System.out.println("ERROR: Record is blank in one of its fields: " + genre_in_movies);
                continue;
            }
//            System.out.println(genre_in_movies);
            String query = "CALL add_genre(?, ?, @status);";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, genre_in_movies.get(1));
            ps.setString(2, genre_in_movies.get(0));

            ResultSet rs1 = ps.executeQuery();
            rs1.close();
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
            rs.close();
            ps1.close();
//            ps.close();
        }

        System.out.println("FINISHED: Total Successed Genres_in_movies: " + totalInserted + " out of " + totalProcessed);
        return 1;
    }

    public int insertActors(Connection conn, List<List<String>> actors) throws SQLException{

        System.out.println("\n\n##############################\nInserting Actors into database");
        int totalInserted = 0;
        int totalProcessed = 0;


        //find max for once and keep incrementing afterwards:
        PreparedStatement findmax = conn.prepareStatement("SELECT DISTINCT max(id) as id FROM stars s LIMIT 1;");
        ResultSet rs = findmax.executeQuery();
        String maxId = "1000";
        while(rs.next()){
            maxId = rs.getString("id");
        }
        rs.close();
        findmax.close();
        //max_increment
        if(maxId ==  null || maxId.length() == 0){
            maxId = "0";
        }
        maxId = max_increment(maxId);


        for(int i = 0;i<actors.size();i++) {
            totalProcessed++;
            List<String> row = actors.get(i);
            String name = row.get(0);
            String year = row.get(1);
            if(name == null || year == null){
                System.out.println("ERROR: row is null?? " + row);
                continue;
            }
            if (name.isBlank()) {
                System.out.println("ERROR: Name is blank for row: " + row);
                continue;
            }
            //verify year:
            int y = -1;
            if(year.length() > 0){
                try{
                    y = Integer.parseInt(year);
                }
                catch(Exception e){
//                    System.out.println("ERROR: dob is not an integer")
                }
            }
            PreparedStatement ps = conn.prepareStatement("INSERT INTO stars (id ,name, birthYear) VALUES(?, ?, ?);");
            ps.setString(1, maxId);
            ps.setString(2, name);
            ps.setInt(3, y);
            maxId = max_increment(maxId);
            int a = ps.executeUpdate();
            if(a > 0){
                totalInserted++;
                if(totalInserted % 500 == 0){
                    System.out.println("SUCCESS: Inserted " + totalInserted + " actors into the database");
                }
            }
            else{
                System.out.println("ERROR: Row " + row + " not inserted");
            }
            ps.close();
        }

        System.out.println("FINISHED: Total Inserted: " + totalInserted + " out of " + totalProcessed);
        return 1;
    }

    public int insertCasts(Connection conn, List<List<String>> casts) throws SQLException{
        System.out.println("\n\n##############################\nInserting Casts into database");

        int totalInserted = 0;
        int totalProcessed = 0;

        for(int i = 0;i<casts.size();i++){
            List<String> col = casts.get(i);
            //Since the implementation is up to us, I will ignore the casts who is not already in the database;
            totalProcessed++;
            if (col == null) {
                System.out.println("ERROR: col is null");
                continue;
            }
            if(col.get(0) == null || col.get(1) == null){
                System.out.println("ERROR col is null");
                continue;
            }
            if(col.get(0).isBlank() || col.get(1).isBlank()){
                System.out.println("ERROR: Record is blank in one of its fields: " + col);
                continue;
            }

            System.out.println("before call");
            String query = "CALL add_cast(?, ?, @status);";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, col.get(0));
            ps.setString(2, col.get(1));

            ResultSet rs1 = ps.executeQuery();
            rs1.close();
            ps.close();

//            int iter = 0;
            if(i%500 == 0){
                System.out.println("Attempted Inserted " + totalInserted + " casts into database");
            }
//            while(rs!= null && rs.next() && iter < 5){
//                String status = rs.getString("st");
//                iter++;
//                if(status.length() > 0){
//                    //success found
//                    totalInserted++;
//                    if(totalInserted % 500 == 0){
//                        System.out.println("SUCCESS: Inserted " + totalInserted + " casts into the database");
//                    }
//                }
//                else{
//                    System.out.println("ERROR: Error while inserting " + col + " into the database, likely due to FK issue");
//                }
//            }

        }

        System.out.println("FINISHED: Total Inserted " + totalInserted + " out of " + totalProcessed);
        return 1;
    }



    public void handle_movies_and_genres(Connection conn) throws SQLException{
        loadFilms spe = new loadFilms("mains243.xml");
        spe.runProgram();

        List<film> unique = spe.processUniqueFilms();
        List<List<String>> l = spe.exportMovies(unique);
        List<List<String>> g = spe.exportGenres(unique);
        System.out.println("Total films: " + l.size());
        System.out.println("Total genres: " + g.size());

        insertMovies(conn, l);
        insertGenres(conn, g);

        //this should self destruct and clear up heap memory;
    }

    public void handle_actors(Connection conn) throws SQLException{
        loadActors spe = new loadActors("actors63.xml");
        spe.runProgram();
        List<List<String>> a = spe.exportActors();
        System.out.println("Total actors: " + a.size());
        insertActors(conn, a);


        //this should self destruct too;
    }

    public void handle_casts(Connection conn) throws SQLException{
        loadCasts spe = new loadCasts("casts124.xml");
        spe.runProgram();
        List<List<String>> a = spe.exportCasts();
        System.out.println("Total casts: " + a.size());
        insertCasts(conn, a);
    }


    public void runProgram() throws Exception{
        // Incorporate mySQL driver
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection("jdbc:" + Parameters.dbtype + ":///" + Parameters.dbname + "?autoReconnect=true&useSSL=false",
                Parameters.username, Parameters.password);
        try {

            /**
             *
             *
             * HANDLERS
             *
             *
             */
//            this.handle_movies_and_genres(conn);
//            this.handle_actors(conn);
            this.handle_casts(conn);


//            JsonObject json = new JsonObject();
        } catch (Exception e) {
            // Write error message JSON object to output
//            System.out.println(e.getMessage());
        }
        finally {
            conn.close();
        }
//        conn.close();

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
