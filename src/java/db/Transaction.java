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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import web.DbListener;

/**
 *
 * @author rlarg
 */
public class Transaction {
    private long rowId;
    private String datetime;
    private String description;
    private String category;
    private double value;
    private String origin;
    
    public static ArrayList<Transaction> getTransactions(int page, int rowsPerPage) throws Exception{
        ArrayList<Transaction> list = new ArrayList<>();
        Class.forName("org.sqlite.JDBC");
        Connection con = DriverManager.getConnection(DbListener.URL);
        String SQL = "SELECT rowid, * FROM transactions ORDER BY datetime DESC LIMIT ? OFFSET ?";
        PreparedStatement stmt = con.prepareStatement(SQL);
        stmt.setLong(1, rowsPerPage);
        stmt.setLong(2, ((page-1)*rowsPerPage));
        ResultSet rs = stmt.executeQuery();
        while(rs.next()){
            list.add(
                new Transaction(
                    rs.getLong("rowid"),
                    rs.getString("datetime"),
                    rs.getString("description"),
                    rs.getString("category"),
                    rs.getDouble("value"),
                    rs.getString("origin")
                )
            );
        }
        rs.close();
        stmt.close();
        con.close();
        return list;
    }
    
    public static ArrayList<Transaction> getFilteredTransactions(int page, int rowsPerPage, String date, String description, String category, String origin) throws Exception{
        ArrayList<Transaction> list = new ArrayList<>();
        Class.forName("org.sqlite.JDBC");
        Connection con = DriverManager.getConnection(DbListener.URL);
        String SQL = "SELECT rowid, * "
                + "FROM transactions "
                + "WHERE 1=1 "
                + (date!=null?" AND datetime LIKE ? ":"")
                + (description!=null?" AND description LIKE ? ":"")
                + (category!=null?" AND category LIKE ? ":"")
                + (origin!=null?" AND origin LIKE ? ":"")
                + " ORDER BY datetime DESC "
                + " LIMIT ? OFFSET ?";
        PreparedStatement stmt = con.prepareStatement(SQL);
        int count = 1;
        if(date!=null) stmt.setString(count++, date+"%");
        if(description!=null) stmt.setString(count++, "%"+description+"%");
        if(category!=null) stmt.setString(count++, "%"+category+"%");
        if(origin!=null) stmt.setString(count++, "%"+origin+"%");
        stmt.setLong(count++, rowsPerPage);
        stmt.setLong(count++, ((page-1)*rowsPerPage));
        ResultSet rs = stmt.executeQuery();
        while(rs.next()){
            list.add(
                new Transaction(
                    rs.getLong("rowid"),
                    rs.getString("datetime"),
                    rs.getString("description"),
                    rs.getString("category"),
                    rs.getDouble("value"),
                    rs.getString("origin")
                )
            );
        }
        rs.close();
        stmt.close();
        con.close();
        return list;
    }
    
    public static Transaction getTransaction(long rowId) throws Exception{
        Transaction transaction = null;
        Class.forName("org.sqlite.JDBC");
        Connection con = DriverManager.getConnection(DbListener.URL);
        String SQL = "SELECT rowid, * FROM transactions WHERE rowid=?";
        PreparedStatement stmt = con.prepareStatement(SQL);
        stmt.setLong(1, rowId);
        ResultSet rs = stmt.executeQuery();
        if(rs.next()){
            transaction = new Transaction(
                rs.getLong("rowid"),
                rs.getString("datetime"),
                rs.getString("description"),
                rs.getString("category"),
                rs.getDouble("value"),
                rs.getString("origin")
            );
        }
        rs.close();
        stmt.close();
        con.close();
        return transaction;
    }
    
    public static void addTransaction(String datetime, String description, String category, double value, String origin) throws Exception{
        Class.forName("org.sqlite.JDBC");
        Connection con = DriverManager.getConnection(DbListener.URL);
        String SQL = "INSERT INTO transactions(datetime, description, category, value, origin) VALUES(?,?,?,round(?,2),?)";
        PreparedStatement stmt = con.prepareStatement(SQL);
        stmt.setString(1, datetime);
        stmt.setString(2, description);
        stmt.setString(3, category);
        stmt.setDouble(4, value);
        stmt.setString(5, origin);
        stmt.execute();
        stmt.close();
        con.close();
    }
    
    public void updateTransaction(long rowId, String newDatetime, String newDescription, String newCategory, double newValue, String newOrigin) throws Exception{
        Class.forName("org.sqlite.JDBC");
        Connection con = DriverManager.getConnection(DbListener.URL);
        String SQL = "UPDATE transactions "
                + "SET  datetime=?, description=?, category=?, value=round(?,2), origin=? "
                + "WHERE rowid=?";
        PreparedStatement stmt = con.prepareStatement(SQL);
        stmt.setString(1, newDatetime);
        stmt.setString(2, newDescription);
        stmt.setString(3, newCategory);
        stmt.setDouble(4, newValue);
        stmt.setString(5, newOrigin);
        stmt.setLong(6, rowId);
        stmt.execute();
        stmt.close();
        con.close();
    }
    
    public static void removeTransaction(long rowId) throws Exception{
        Class.forName("org.sqlite.JDBC");
        Connection con = DriverManager.getConnection(DbListener.URL);
        String SQL = "DELETE FROM transactions WHERE rowid = ?";
        PreparedStatement stmt = con.prepareStatement(SQL);
        stmt.setLong(1, rowId);
        stmt.execute();
        stmt.close();
        con.close();
    }

    public Transaction(long rowId, String datetime, String description, String category, double value, String origin) {
        this.rowId = rowId;
        this.datetime = datetime;
        this.description = description;
        this.category = category;
        this.value = value;
        this.origin = origin;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public long getRowId() {
        return rowId;
    }

    public void setRowId(long rowId) {
        this.rowId = rowId;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
    
    public static void generateSampleData() {
        //create test data
        try{
            ArrayList<Category> categories = Category.getCategories();
            Calendar calendar = Calendar.getInstance(); 
            calendar.add(Calendar.YEAR, -1);
            for(int i=0; i<=365; i++){
                calendar.add(Calendar.DATE, 1);
                Date date = calendar.getTime();
                String datetime = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm").format(date);
                System.out.println("Inseridno registros de "+datetime);
                if(calendar.get(Calendar.DATE) == 1){
                    try{
                        Transaction.addTransaction(datetime, "Salário", "Receitas", 2000.0+200*(Math.random()-0.5), "XYZ Corporate");
                        System.out.println("Inserido salário");
                    }catch(Exception ex){
                        System.out.println("Erro fazendo o insert do salário "+i+": "+ex.getMessage());
                    }
                }else if(calendar.get(Calendar.DATE) == 10){
                    try{
                        Transaction.addTransaction(datetime, "Aluguel+condomíniio+IPTU", "Moradia", -1000.0+100*(Math.random()-0.5), "Imobiliária Mora Bem");
                        System.out.println("Inserido aluguel");
                    }catch(Exception ex){
                        System.out.println("Erro fazendo o insert do aluguel "+i+": "+ex.getMessage());
                    }
                    try{
                        Transaction.addTransaction(datetime, "Luz", "Moradia", -100.0+20*(Math.random()-0.5), "Cia Piratininga");
                        System.out.println("Inserido luz");
                    }catch(Exception ex){
                        System.out.println("Erro fazendo o insert da luz "+i+": "+ex.getMessage());
                    }
                    try{
                        Transaction.addTransaction(datetime, "Internet+telefone", "Telefonia", -150.0+30*(Math.random()-0.5), "Vivo");
                        System.out.println("Inserido internet");
                    }catch(Exception ex){
                        System.out.println("Erro fazendo o insert da telefonia "+i+": "+ex.getMessage());
                    }
                }else if(calendar.get(Calendar.DATE) == 15){
                    try{
                        Transaction.addTransaction(datetime, "Rendimentos com cações da bolsa", "Receitas", 1000.0*(Math.random()-0.5), "Ibovespa");
                        System.out.println("Inserido rendimentos");
                    }catch(Exception ex){
                        System.out.println("Erro fazendo o insert dos rendimentos "+i+": "+ex.getMessage());
                    }
                }
                try{
                    Transaction.addTransaction(datetime, "Café da manhã", "Alimentação", -10.0+5*(Math.random()-0.5), "Padaria do Manuel");
                    System.out.println("Inserido café");
                }catch(Exception ex){
                    System.out.println("Erro fazendo o insert do café "+i+": "+ex.getMessage());
                }
                try{
                    Transaction.addTransaction(datetime, "Almoço", "Alimentação", -20.0+10*(Math.random()-0.5), "Restaurante mosca frita");
                    System.out.println("Inserido almoço");
                }catch(Exception ex){
                    System.out.println("Erro fazendo o insert do almoço "+i+": "+ex.getMessage());
                }
                try{
                    Transaction.addTransaction(datetime, "Jantar", "Alimentação", -20.0+10*(Math.random()-0.5), "Restaurante mosca cozida");
                    System.out.println("Inserido jantar");
                }catch(Exception ex){
                    System.out.println("Erro fazendo o insert do jantar "+i+": "+ex.getMessage());
                }
            }
        }catch(Exception ex1){
            System.out.println("Erro geral: "+ex1.getMessage());
        }
    }
}