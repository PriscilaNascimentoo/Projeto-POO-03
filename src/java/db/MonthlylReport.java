/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import web.DbListener;

/**
 *
 * @author rlarg
 */
public class MonthlylReport {
    private String year;
    private String month;
    private double costs;
    private double profit;
    
    public static ArrayList<MonthlylReport> getGeneralReport() throws Exception{
        ArrayList<MonthlylReport> list = new ArrayList<>();
        Class.forName("org.sqlite.JDBC");
        Connection con = DriverManager.getConnection(DbListener.URL);
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(""
                + "SELECT "
                + "     strftime('%Y',datetime) as year, "
                + "     strftime('%m',datetime) as month, "
                + "     sum(CASE WHEN value<0 THEN value ELSE 0 END) as costs, "
                + "     sum(CASE WHEN value>0 THEN value ELSE 0 END) as profit "
                + "FROM transactions "
                + "GROUP BY strftime('%Y',datetime), strftime('%m',datetime)"
                + "ORDER BY strftime('%Y',datetime) DESC, strftime('%m',datetime) DESC");
        while(rs.next()){
            list.add(new MonthlylReport(
                        rs.getString("year"), 
                        rs.getString("month"),
                        rs.getDouble("costs"),
                        rs.getDouble("profit")
                    )
                );
        }
        rs.close();
        stmt.close();
        con.close();
        return list;
    }

    public MonthlylReport(String year, String month, double costs, double profit) {
        this.year = year;
        this.month = month;
        this.costs = costs;
        this.profit = profit;
    }

    public double getProfit() {
        return profit;
    }

    public void setProfit(double profit) {
        this.profit = profit;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public double getCosts() {
        return costs;
    }

    public void setCosts(double costs) {
        this.costs = costs;
    }
    
    public double getCostsPercent(){
        return 100 * Math.abs(this.getCosts()) / (Math.abs(this.getCosts())+this.getProfit());
    }
    
    public double getProfitPercent(){
        return 100 * this.getProfit()/ (Math.abs(this.getCosts())+this.getProfit());
    }
    
}