/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package web;

import db.Category;
import db.Transaction;
import db.User;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Web application lifecycle listener.
 *
 * @author rlarg
 */
public class DbListener implements ServletContextListener {
    public static final String URL = "jdbc:sqlite:C:\\Users\\prisc\\financys.db";
    
    public static String exceptionMessage = null;
    
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String step = "Starting database creation";
        try{
            Class.forName("org.sqlite.JDBC");
            Connection con = DriverManager.getConnection(URL);
            Statement stmt = con.createStatement();
            
            String SQL = null;
            
            step = "users Table creation";
            SQL = "CREATE TABLE IF NOT EXISTS users("
                    + "name VARCHAR(200) NOT NULL,"
                    + "login VARCHAR(50) UNIQUE NOT NULL,"
                    + "password_hash LONG,"
                    + "role CARCHAR(20) NOT NULL"
                    + ")";
            stmt.executeUpdate(SQL);
            
            step = "Default users creation";
            if (User.getUsers().isEmpty()){
                SQL = "INSERT INTO users(name, login, password_hash, role) "
                    + "VALUES('Administrador', 'admin', '"+("123456".hashCode())+"', 'ADMIN')";
                stmt.executeUpdate(SQL);
                SQL = "INSERT INTO users(name, login, password_hash, role) "
                    + "VALUES('Fulano da Silva', 'fulano', '"+("123456".hashCode())+"', 'USER')";
                stmt.executeUpdate(SQL);
            }
            
            step = "categories Table creation";
            SQL = "CREATE TABLE IF NOT EXISTS categories("
                    + "name VARCHAR(50) PRIMARY KEY,"
                    + "description VARCHAR(200) NOT NULL"
                    + ")";
            stmt.executeUpdate(SQL);
            
            step = "Default categories creation";
            if (Category.getCategories().isEmpty()){
                SQL = "INSERT INTO categories(name, description) "
                    + "VALUES('Moradia', 'despesas com moradia coo aluguel, IPTU, lúz, água, gás, etc.')";
                stmt.executeUpdate(SQL);
                SQL = "INSERT INTO categories(name, description) "
                    + "VALUES('Alimentação', 'Gastos com restaurantes, mercado, etc.')";
                stmt.executeUpdate(SQL);
                SQL = "INSERT INTO categories(name, description) "
                    + "VALUES('Saúde', 'Despezas com plano de saúde, farmácia, etc')";
                stmt.executeUpdate(SQL);
                SQL = "INSERT INTO categories(name, description) "
                    + "VALUES('Educação', 'Despesas com capacitação e qualificação profisisonal')";
                stmt.executeUpdate(SQL);
                SQL = "INSERT INTO categories(name, description) "
                    + "VALUES('Telefonia', 'Despesas com telefone, internet, etc.')";
                stmt.executeUpdate(SQL);
                SQL = "INSERT INTO categories(name, description) "
                    + "VALUES('Lazer', 'Despesas com viagens, baladas, hobbies, etc.')";
                stmt.executeUpdate(SQL);
                SQL = "INSERT INTO categories(name, description) "
                    + "VALUES('Receitas', 'Recebimentos de salários, rendimentos, etc')";
                stmt.executeUpdate(SQL);
            }
            
            step = "transactions Table creation";
            SQL = "CREATE TABLE IF NOT EXISTS transactions("
                    + "datetime VARCHAR(25) NOT NULL,"
                    + "description VARCHAR(200) NOT NULL,"
                    + "category VARCHAR(50) NOT NULL,"
                    + "value NUMERIC(10,2) NOT NULL,"
                    + "origin VARCHAR(200) NOT NULL,"
                    + "FOREIGN KEY(category) REFERENCES categories(name)"
                    + ")";
            stmt.executeUpdate(SQL);
            
            stmt.close();
            con.close();
        }catch (Exception ex){
            exceptionMessage = step + ": " + ex;
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        
    }
}
