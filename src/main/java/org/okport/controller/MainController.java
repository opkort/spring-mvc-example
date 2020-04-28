package org.okport.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import java.sql.*;

@Controller
public class MainController {
    private final String url = "jdbc:postgresql://localhost/postgres";

    @PostConstruct
    void init() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) { e.printStackTrace(); }
    }

    @ResponseBody
    @RequestMapping(value="/hello/{name}", method = RequestMethod.GET)
    public String hello(@PathVariable("name") String value) {
        try (
                Connection connection = DriverManager.getConnection(url, "postgres", "postgres");
                Statement statement = connection.createStatement();
                ResultSet result = statement.executeQuery(
                        "SELECT * from foo;"
                );
        ){
            while (result.next()) {
                System.out.println(result.getInt("value"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return "hello, " + value;
    }

    @ResponseBody
    @RequestMapping(value="/values", method = RequestMethod.GET)
    public String read() {
        try {
                Connection connection = DriverManager.getConnection(url, "postgres", "postgres");
                Statement statement = connection.createStatement();
                ResultSet result = statement.executeQuery(
                        "select * from values"
                );
                String returnString = "";
                int columns = result.getMetaData().getColumnCount();
                while(result.next()){
                    for (int i = 1; i <= columns; i++){
                        returnString = returnString + result.getString(i) + "\n";
                    }
                    System.out.println();
                }
                return returnString;
        } catch (SQLException e) { e.printStackTrace(); }
        return "";
    }

    @ResponseBody
    @RequestMapping(value="upsert/value/{id}/{name}", method = RequestMethod.GET)
    public String update(@PathVariable int id, @PathVariable String name) {
        try {
            Connection connection = DriverManager.getConnection(url, "postgres", "postgres");
            //Statement statement = connection.createStatement();
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO values (id, name) VALUES (?, ?) \n" +
                            "ON CONFLICT (id) \n" +
                            "DO \n" +
                            "UPDATE \n" +
                            "SET id = EXCLUDED.id, name = EXCLUDED.name; ");
            statement.setInt(1, id);
            statement.setString(2, name );
            statement.execute();
            System.out.println("row:  | " + id + " | " + name + " |  was added");
            //return ("row:" + id + " | " + name + "added");
        } catch (SQLException e) { e.printStackTrace(); }
        return ("row:  | " + id + " | " + name + " |  was added");
    }

    @ResponseBody
    @RequestMapping(value="delete/value/{id}", method = RequestMethod.GET)
    public String delete(@PathVariable int id) {
        try {
            Connection connection = DriverManager.getConnection(url, "postgres", "postgres");
            //Statement statement = connection.createStatement();
            PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM values \n" +
                        "WHERE id=?;");
            statement.setInt(1, id);
            statement.execute();
            System.out.println("row with id : | " + id + " |  was deleted");
            //return ("row:" + id + " | " + name + "added");
        } catch (SQLException e) { e.printStackTrace(); }
        return ("row with id : | " + id + " |  was deleted");
    }
}