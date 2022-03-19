package CreareCont;

import java.sql.*;

public class Cont {
   private CadruDidactic cd;
   private String rol,username,parola;
   private static Conexiune conex=Conexiune.getInstanta();
   private static Statement stmt=conex.getStatement();
    
   public Cont(String r,String u,String p, CadruDidactic cadruD){
       rol=r;
       username=u;
       parola=p;
       cd=cadruD;
   }
   
   public Cont(String r,String u,String p){
       rol=r;
       username=u;
       parola=p;
   }
   
   public String getInfo(){
       return rol+" "+username+" "+parola;
   }
   
   public String getSomeInfo(){
       return rol+" "+username;
   }
   
   public String getUsername(){
       return username;
   }
   
   public String getParola(){
       return parola;
   }
   
   public String getRol(){
       return rol;
   }
   
   public String getCdId(){
       try{
        String comanda="select idCadruDidactic from cadredidactice where email= '"+username+"';";
        ResultSet rs=stmt.executeQuery(comanda);
        while(rs.next()){
            return rs.getString("idCadruDidactic");
        }
         }catch(SQLException e){e.printStackTrace();}
         return "";
   }
   
   public CadruDidactic getCadruDidactic(){
       return cd;
   }
}
