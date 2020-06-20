/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import web.DbListener;

/**
 *
 * @author rlarg
 */
public class Category {
    private String name;
    private String description;
    
    public static ArrayList<Category> getCategories() throws Exception{
        ArrayList<Category> list = new ArrayList<>();
        Class.forName("org.sqlite.JDBC");
        Connection con = DriverManager.getConnection(DbListener.URL);
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM categories ORDER BY name");
        while(rs.next()){
            list.add(new Category(
                    rs.getString("name"), 
                    rs.getString("description")));
        }
        rs.close();
        stmt.close();
        con.close();
        return list;
    }
    
    public static Category getCategorie(String name) throws Exception{
        Category category = null;
        Class.forName("org.sqlite.JDBC");
        Connection con = DriverManager.getConnection(DbListener.URL);
        String SQL = "SELECT * FROM categories WHERE name=?";
        PreparedStatement stmt = con.prepareStatement(SQL);
        stmt.setString(1, name);
        ResultSet rs = stmt.executeQuery();
        if(rs.next()){
            category = new Category(
                    rs.getString("name"), 
                    rs.getString("description")
            );
        }
        rs.close();
        stmt.close();
        con.close();
        return category;
    }
    
    public static void addCategory(String name, String description) throws Exception{
        Class.forName("org.sqlite.JDBC");
        Connection con = DriverManager.getConnection(DbListener.URL);
        String SQL = "INSERT INTO categories(name, description) VALUES(?,?)";
        PreparedStatement stmt = con.prepareStatement(SQL);
        stmt.setString(1, name);
        stmt.setString(2, description);
        stmt.execute();
        stmt.close();
        con.close();
    }
    
    public void updateCategory(String newName, String newDescription) throws Exception{
        Class.forName("org.sqlite.JDBC");
        Connection con = DriverManager.getConnection(DbListener.URL);
        String SQL = "UPDATE categories "
                + "SET "
                + " name=?, "
                + " description=? "
                + "WHERE name=?";
        PreparedStatement stmt = con.prepareStatement(SQL);
        stmt.setString(1, newName);
        stmt.setString(2, newDescription);
        stmt.setString(3, this.getName());
        stmt.execute();
        stmt.close();
        con.close();
    }
    
    public static void removeCategory(String name) throws Exception{
        Class.forName("org.sqlite.JDBC");
        Connection con = DriverManager.getConnection(DbListener.URL);
        String SQL = "DELETE FROM categories WHERE name = ?";
        PreparedStatement stmt = con.prepareStatement(SQL);
        stmt.setString(1, name);
        stmt.execute();
        stmt.close();
        con.close();
    }

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
}
