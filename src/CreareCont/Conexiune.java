
package CreareCont;

import java.sql.*;
public class Conexiune {
    private static Conexiune c;
    private Connection con;
    private Statement stmt;
    private String numeBD;
    private Conexiune(){
        numeBD="foiprezentabd";
        try{
        con=DriverManager.getConnection("jdbc:mysql://localhost:3306/"+numeBD, "root", "Gabriela99##");
        stmt=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
        }catch(SQLException e){System.out.println("eroare la conexiune"+e.getMessage());}
    }
    public static Conexiune getInstanta(){
        if(c==null)c=new Conexiune();
        return c;
      }
      public Statement getStatement(){
          return stmt;
      }
      public Connection getConexiune(){
          return con;
      }
      public void inchideConexiune(){
          try{
          con.close();
          stmt.close();
          }catch(SQLException e){System.out.println("eroare la inchidere"+e.getMessage());}
      }
      public DatabaseMetaData getMetaData(){
         DatabaseMetaData mt=null;
          try{
          mt=con.getMetaData();
          }catch(SQLException e){e.printStackTrace();}
          return mt;
      }
}
