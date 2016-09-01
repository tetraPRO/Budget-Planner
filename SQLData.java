
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author capphil1
 */
public class SQLData {
    
    static final String JDBC_DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
    static final String DB_URL = "jdbc:derby://localhost:1527/Budget;Create=true";
    String name = "";
    String pass = ""; 
    Connection conn = null;
    Statement stmt = null;
    PreparedStatement pStmt = null;
    ResultSet rs = null;
    
    /**
     * Constructor takes 2 arguments and builds a connection
     * to the database that is ready to take SQL commands
     * from the various methods.
     * @param uName username of the database
     * @param uPass password of the database
     */
    public SQLData(String uName, String uPass){
        this.name = uName;
        this.pass = uPass;
        
        try{
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, name, pass);
        }catch(SQLException | ClassNotFoundException ex){
            JOptionPane.showMessageDialog(null,  ex);
        }
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    /*
    * Initial method to build table
    */
    public void onStartUp(){
         try {
            DatabaseMetaData meta = conn.getMetaData();
            rs = meta.getTables(null, "ROOT", "BUDGET", null);
            if(!rs.next()){
                stmt = conn.createStatement();
                String sql = "CREATE TABLE BUDGET(Expense VARCHAR(20),  Amount  REAL, "
                        + "Notes VARCHAR(20))";
                stmt.execute(sql);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }finally{
             try{
                 rs.close();
                 if(stmt != null){
                     stmt.close();
                 }
             }catch(SQLException ex){
                 JOptionPane.showMessageDialog(null, ex);
             }
         }
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    /**
     * Takes 3 parameters and inserts those data points into the budget table
     * @param expense
     * @param amount
     * @param notes 
     */
    public void addToBudget(String expense, BigDecimal amount, String notes){
        try{
            String sql = "INSERT INTO BUDGET(EXPENSE, AMOUNT, NOTES) VALUES (\'" +
                    expense + "\', " + amount + ", \'" + notes + "\')";
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
        }catch(SQLException ex){
                 JOptionPane.showMessageDialog(null, ex);
        }finally{
             try{
                 stmt.close();
             }catch(SQLException ex){
                 JOptionPane.showMessageDialog(null, ex);
             }
         }
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    /**
     * Accepts 1 argument as to which table we should be querying.
     * @param table to query
     * @return resultset
     */
    public ResultSet updateTable(String  table){
        try{
            stmt = conn.createStatement();
            String sql = "SELECT  *  FROM " + table;
             rs = stmt.executeQuery(sql);
        }catch(SQLException ex){
            JOptionPane.showMessageDialog(null, ex);
        }return rs;
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    /**
     * returns the total of the amounts in the budget
     * @return total of budget
     */
    public BigDecimal getSumTotal(){
        BigDecimal sum = null;
        try{
            stmt = conn.createStatement();
            String sql = "SELECT SUM(AMOUNT) FROM BUDGET";
            rs = stmt.executeQuery(sql);
            while(rs.next()){
                sum =rs.getBigDecimal(1);
            }
        }catch(SQLException ex){
            JOptionPane.showMessageDialog(null, ex);
        }return sum;
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
}
