package CreareCont;

import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;
public class ManagerCont {
   
    private static Conexiune conex=Conexiune.getInstanta();
    private static Statement stmt=conex.getStatement();
    
   
    public static boolean memoreaza(Cont c){
      try{
            PreparedStatement ps=conex.getConexiune().prepareStatement("insert into conturi(rol,username,parola,idCadruDidactic) values(?,?,?,?);");
            System.out.println("Se memoreaza contul: "+c.getSomeInfo());

            String s=c.getInfo();
            String[] itemi=s.split(" ");
            ps.setString(1,itemi[0]);
            ps.setString(2,itemi[1]);
            ps.setString(3,hashPassword(itemi[2]));
            ps.setString(4,c.getCdId());
            ps.executeUpdate();
            return true;
        }catch(SQLException e){e.printStackTrace();}
        return false;
    }
    
    public static boolean verificaEmail(String email){
        try{
            String comanda1="select * from  conturi;"; 
            ResultSet rs1=stmt.executeQuery(comanda1);
            if(rs1.next()==false){
                return false;
            }
            String comanda="select username from conturi where username= \""+email+"\";";
            ResultSet rs=stmt.executeQuery(comanda);
            if(rs.next()){
                return true;
            }
        }catch(SQLException e){e.printStackTrace();}
        return false;
    }
    
    public static String verificaUsernameLogare(String u,String p){
        String hash="";
        try{
            String comanda1="select parola from conturi where (username= \""+u+"\");"; 
            ResultSet rs1=stmt.executeQuery(comanda1);
            if(rs1.next()) {hash=rs1.getString(1);
            }
            else return "";
            
            if (checkPassword(p,hash)){
                String comanda="select rol from conturi where (username= \""+u+"\");";
                ResultSet rs=stmt.executeQuery(comanda);
                if(rs.next()){
                    //System.out.println(rs.getString(1));
                    return rs.getString(1);
                }
            } else System.out.println("Nu se potrivesc");
        }catch(SQLException e){e.printStackTrace();}
        return "";
        
        // in clar
        /*try{
            String comanda1="select * from conturi;"; 
            ResultSet rs1=stmt.executeQuery(comanda1);
            if(rs1.next()==false){
                return "";
            }
            String comanda="select rol from conturi where (username= \""+u+"\" and parola=\"" +p+"\");";
            ResultSet rs=stmt.executeQuery(comanda);
            if(rs.next()){
                System.out.println(rs.getString(1));
                return rs.getString(1);
            }
        }catch(SQLException e){e.printStackTrace();}
        return "";*/
    }
    
    public static Boolean memoreazaParola(Cont c, String p){
        try{
            PreparedStatement ps=conex.getConexiune().prepareStatement("update conturi SET parola=?  WHERE `username` =\""+c.getUsername()+"\";");
            System.out.println("Se modifică parola contului: "+c.getSomeInfo());

            ps.setString(1,hashPassword(p));
            ps.executeUpdate();
            return true;
        }catch(SQLException e){e.printStackTrace();}
        return false;
    }
    
    public static String getNume(String email){
        try{
            String comanda="select nume,prenume from persoane p inner join cadredidactice cd on p.idPersoana=cd.idPersoana where email= \""+email+"\";";
            ResultSet rs=stmt.executeQuery(comanda);
            if(rs.next()){
                return rs.getString(1)+" "+rs.getString(2);
            }
        }catch(SQLException e){e.printStackTrace();}
        return "";
    }
    
    public static String[] getCDInfo(String id){
        try{
            String comanda="select nume,prenume,denumire from (persoane p inner join cadredidactice cd on p.idPersoana=cd.idPersoana)"
                    +" inner join departamente d on d.idDepartament=cd.idDepartament"
                    +" where idCadruDidactic= \""+id+"\";";
            ResultSet rs=stmt.executeQuery(comanda);
            if(rs.next()){
                String[] info=new String[2];
                info[0] = rs.getString("nume")+" "+rs.getString("prenume");
                info[1] = rs.getString("denumire");
                return info;
            }
        }catch(SQLException e){e.printStackTrace();}
        return null;
    }
    
    // Insert persoane si cadre didactice
    public static void insertP(Persoana p){
        try{
            PreparedStatement ps=conex.getConexiune().prepareStatement("insert into persoane(nume,prenume) values(?,?);");
            ps.setString(1,p.getNume());
            ps.setString(2,p.getPrenume());
            ps.executeUpdate();
        }catch(SQLException e){}
    }
    
    public static void insertCdProf(CadruDidactic cd,Persoana p){ //pt prof
        try{
            PreparedStatement ps=conex.getConexiune().prepareStatement("insert into cadredidactice(grad,email,idPersoana,idDirector,idDepartament) values(?,?,?,?,?);");
            ps.setString(1,cd.getGrad());
            ps.setString(2,cd.getEmail());
            ps.setString(3,getPersoanaId(p));
            ps.setString(4, getDirId(cd));
            ps.setString(5, getDirDeptId(cd));
            ps.executeUpdate();
        }catch(SQLException e){}
    }
    
    public static void insertCdDir(CadruDidactic cd,Persoana p){  //pt dir
        try{
            PreparedStatement ps=conex.getConexiune().prepareStatement("insert into cadredidactice(grad,email,idPersoana,idDepartament) values(?,?,?,?);");
            ps.setString(1,cd.getGrad());
            ps.setString(2,cd.getEmail());
            ps.setString(3,getPersoanaId(p));
            ps.setString(4, cd.getDept());
            ps.executeUpdate();
        }catch(SQLException e){}
    }
    
    public static String getDirId(CadruDidactic cd){
       try{
            String comanda="select idCadruDidactic from cadredidactice where email= \""+cd.getDir()+"\";";
            ResultSet rs=stmt.executeQuery(comanda);
            if(rs.next()){
                return rs.getString("idCadruDidactic");
            }
        }catch(SQLException e){e.printStackTrace();}
        return "";
   } 
    
    public static String getDirId(String username){
       try{
            String comanda="select idDirector,idCadruDidactic from cadredidactice where email= \""+username+"\";";
            ResultSet rs=stmt.executeQuery(comanda);
            if(rs.next()){
                return rs.getString("idDirector");
            }
        }catch(SQLException e){e.printStackTrace();}
        return "";
   }
    
    public static String getEmail(String id){
       try{
            String comanda="select email from cadredidactice where idCadruDidactic= \""+id+"\";";
            ResultSet rs=stmt.executeQuery(comanda);
            if(rs.next()){
                return rs.getString("email");
            }
        }catch(SQLException e){e.printStackTrace();}
        return "";
   }
    
    public static String getCdId(String email){
       try{
            String comanda="select idCadruDidactic from cadredidactice where email= \""+email+"\";";
            ResultSet rs=stmt.executeQuery(comanda);
            if(rs.next()){
                return rs.getString("idCadruDidactic");
            }
        }catch(SQLException e){e.printStackTrace();}
        return "";
   }
    
    public static String getCdId(String nume, String prenume){
       try{
            String comanda="select idCadruDidactic from cadredidactice c inner join persoane p on c.idPersoana=p.idPersoana"
                    +" where p.nume= \""+nume+"\" and p.prenume=\""+prenume+"\";";
            ResultSet rs=stmt.executeQuery(comanda);
            if(rs.next()){
                return rs.getString("idCadruDidactic");
            }
        }catch(SQLException e){e.printStackTrace();}
        return "";
   }
    
    public static String getDirDeptId(CadruDidactic cd){
        try{
            String comanda="select idDepartament from cadredidactice where email= \""+cd.getDir()+"\";";
            ResultSet rs=stmt.executeQuery(comanda);
            if(rs.next()){
                return rs.getString("idDepartament");
            }
        }catch(SQLException e){e.printStackTrace();}
        return "";
   }
    
    public static String getDeptId(String email){
        try{
            String comanda="select idDepartament from cadredidactice where email= \""+email+"\";";
            ResultSet rs=stmt.executeQuery(comanda);
            if(rs.next()){
                return rs.getString("idDepartament");
            }
        }catch(SQLException e){e.printStackTrace();}
        return "";
   }
    
    public static boolean verificaExistentaDirector(String dep){
        try{
            String comanda1="select * from  conturi;"; 
            ResultSet rs1=stmt.executeQuery(comanda1);
            if(rs1.next()==false){
                return false;
            }
            String comanda="select email from cadredidactice where idDepartament= \""+dep+"\" and idDirector is null;";
            ResultSet rs=stmt.executeQuery(comanda);
            if(rs.next()){
                return true;
            }
        }catch(SQLException e){e.printStackTrace();}
        return false;
    }
    
    public static boolean verificaDirUser(String dirId,String deptId, String user){ //verific daca userul este din departamentul directorului
        try{
            String comanda="select * from cadredidactice where email= \""+user+"\" and (idDirector= \""+dirId+"\" or idDepartament= \""+deptId+"\");";
            ResultSet rs=stmt.executeQuery(comanda);
            if(rs.next()){
                return true;
            }
        }catch(SQLException e){e.printStackTrace();}
        return false;
    }
    
    public static boolean verificaDirUserNume(String dirId,String deptId, String nume){ //verific daca userul este din departamentul directorului
        String[] item=nume.split(" ");
        try{
            String comanda="select cd.email from cadredidactice cd inner join persoane p on cd.idPersoana=p.idPersoana where p.nume= \""+item[0]+"\""
                    +" and p.prenume= \""+item[1]+"\" and (cd.idDirector= \""+dirId+"\" or cd.idDepartament= \""+deptId+"\") ;";
            ResultSet rs=stmt.executeQuery(comanda);
            if(rs.next()){
                return true;
            }
        }catch(SQLException e){e.printStackTrace();}
        return false;
    }
  
    public static String getPersoanaId(Persoana p){
       try{
            String comanda="select idPersoana from persoane where nume= \""+p.getNume()+"\" and prenume= \""+p.getPrenume()+"\";";
            ResultSet rs=stmt.executeQuery(comanda);
            if(rs.next()){
                return rs.getString("idPersoana");
            }
        }catch(SQLException e){e.printStackTrace();}
        return "";
   }
    
    public static boolean verificaNume(String nume){
        String[] item=nume.split(" ");
        try{
            String comanda1="select * from  conturi;"; 
            ResultSet rs1=stmt.executeQuery(comanda1);
            if(rs1.next()==false){
                return false;
            }
            String comanda="select c.username from (conturi c inner join cadredidactice cd on c.idCadruDidactic=cd.idCadruDidactic)"
                    +" inner join persoane p on cd.idPersoana=p.idPersoana"
                    +" where p.nume= \""+item[0]+"\" and p.prenume= \""+item[1]+"\";";
            ResultSet rs=stmt.executeQuery(comanda);
            if(rs.next()){
                return true;
            }
        }catch(SQLException e){e.printStackTrace();}
        return false;
    }
    
    public static String hashPassword(String plainTextPassword){
		return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
	}
    
    public static boolean checkPassword(String plainTextPassword, String stored_hash) {
		boolean password_verified = false;
		if(null == stored_hash || !stored_hash.startsWith("$2a$")) return false;
			//throw new java.lang.IllegalArgumentException("Invalid hash provided for comparison");
                        

		return password_verified = BCrypt.checkpw(plainTextPassword, stored_hash);
	}
}
