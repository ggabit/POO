package GenerareFoaiePrezenta;

import CreareCont.Conexiune;
import CreareCont.ManagerCont;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class ManagerFoiPrezenta {
    // variabila statica instanta de tip ManagerFoiPrezenta
    private static ManagerFoiPrezenta instanta = null;
    private static Conexiune conex=Conexiune.getInstanta();
    private static Statement stmt=conex.getStatement();
    
    // constructor privat
    private ManagerFoiPrezenta()
    {
        
    }
  
    // metoda statica ce creeaza instanta a clasei ManagerFoiPrezenta
    public static ManagerFoiPrezenta getInstance()
    {
        // verific daca valoarea varabilei instanta este null
        if (instanta == null)
            instanta = new ManagerFoiPrezenta();
  
        return instanta;
    }
    
    public static boolean verificaFoaie(String nume, String luna, String an){
        String[] item = nume.split(" ");
        String cdId=ManagerCont.getCdId(item[0],item[1]);
        if(Integer.parseInt(luna)<10){
            an=(Integer.parseInt(an)-1)+"-"+an;
        }
        else{
            an+="-"+(Integer.parseInt(an)+1);
        }
        try{
            String comanda="select idFoaiePrezenta from foiprezenta f inner join lunilucru l on f.idLunaLucru=l.idLunaLucru"
                    +" where l.idCadruDidactic= \""+cdId+"\" and l.numar=\""+
                    luna+"\" and l.anUniversitar=\""+an+"\";";
            ResultSet rs=stmt.executeQuery(comanda);
            if(rs.next()){
                return true;
            }
        }catch(SQLException e){e.printStackTrace();}
        return false;
    }
    
    public static String docExLun(String nume, String luna, String an){
        String[] item = nume.split(" ");
        String cdId=ManagerCont.getCdId(item[0],item[1]);
        String obtinute="";
        if(Integer.parseInt(luna)<10){
            an=(Integer.parseInt(an)-1)+"-"+an;
        }
        else{
            an+="-"+(Integer.parseInt(an)+1);
        }
        try{
            String comanda="select tipDocument from documente"
                    +" where idCadruDidactic= \""+cdId+"\" and luna=\""+
                    luna+"\" and anUniversitar=\""+an+"\" and tipDocument<>\"fpp\";";
            ResultSet rs=stmt.executeQuery(comanda);
            while(rs.next()){
                obtinute+=rs.getString("tipDocument")+" ";
            }
        }catch(SQLException e){e.printStackTrace();}
        return obtinute;
    }
    
    /*public static boolean verificaSF(String nume, String an){
        String[] item = nume.split(" ");
        String cdId=ManagerCont.getCdId(item[0],item[1]);
        try{
            String comanda="select idDocument from documente"
                    +" where idCadruDidactic= \""+cdId+"\" and tipDocument=\"sf\" and YEAR(data)=\""+an+"\";";
            ResultSet rs=stmt.executeQuery(comanda);
            if(rs.next()){
                return true;
            }
        }catch(SQLException e){e.printStackTrace();}
        return false;
    }*/
    
    public static boolean verificaCA(int an,String luna){
        String anStr="";
        if(Integer.parseInt(luna)<10){
            anStr=(an-1)+"-"+an;
        }
        else{
            anStr=an+"-"+(an+1);
        }
        try{
            String comanda="select idDocument from documente"
                    +" where tipDocument=\"ca\" and anUniversitar=\""+anStr+"\";";
            ResultSet rs=stmt.executeQuery(comanda);
            if(rs.next()){
                return true;
            }
        }catch(SQLException e){e.printStackTrace();}
        return false;
    }
    
    public static boolean memoreazaFoaie(FoaiePrezenta f){
        try{
                PreparedStatement ps1=conex.getConexiune().prepareStatement("insert into foiprezenta(nrOrePlataOra,nrOreBaza,alteOre,idLunaLucru) values(?,?,?,?);");

                ps1.setString(1,String.valueOf(f.getNrOrePlataOra()));
                ps1.setString(2,String.valueOf(f.getNrOreBaza()));
                ps1.setString(3,String.valueOf(f.getAlteOre()));
                ps1.setString(4,f.getIdLunaL());
                ps1.executeUpdate();
            
            return true;
        }catch(SQLException e){e.printStackTrace();}
        return false;
    } 
    
   public static boolean verificaParticipare(Participare p){
        try{
            String comanda="select idParticipare from participari"
                    +" where dataIncepere=\""+p.getDataIncepere()+"\" and idCadruDidactic=\""+p.getCdId()+"\" and idProiectCercetare=\""+p.getProiectId()+"\" and functie=\""+p.getFunctie()+"\" ;";
            ResultSet rs=stmt.executeQuery(comanda);
            if(rs.next()){
                return true;
            }
        }catch(SQLException e){e.printStackTrace();}
        return false;
    }
   
   public static int verificaActivitate(String data, String cdId){
        try{
            String comanda="select nrOre from alteActivitati"
                    +" where data=\""+data+"\" and idCadruDidactic=\""+cdId+"\";";
            ResultSet rs=stmt.executeQuery(comanda);
            if(rs.next()){
                return (int) Double.parseDouble(rs.getString("nrOre"));
            }
        }catch(SQLException e){e.printStackTrace();}
        return 0;
    }
    
    public static String memoreazaCA(Document d, int of, int ol, String an){
        try{
            PreparedStatement ps=conex.getConexiune().prepareStatement("insert into documente(tipDocument,anUniversitar,totalOreFizice,totalOreLucrate,idCadruDidactic) values(?,?,?,?,?);");
            System.out.println("Se memoreaza documentul: "+d.getDocPath());

            ps.setString(1,d.getTipDoc());
            ps.setString(2,an);
            ps.setString(3,String.valueOf(of));
            ps.setString(4,String.valueOf(ol));
            ps.setString(5,d.getCdId());
            ps.executeUpdate();
            
            // trebuie sa aflu id-ul calendarului pe care l-am inserat
            String caId = getCAId(an);
            return caId;
        }catch(SQLException e){e.printStackTrace();}
        return "-1";
    }
    
    public static String memoreazaLunaA(LunaCalend luna, String idCA){
        try{
                PreparedStatement ps1=conex.getConexiune().prepareStatement("insert into lunicalendaristice(denumire,numar,oreFizice,oreLucrate,idCalendarAcademic,nrZile) values(?,?,?,?,?,?);");
                //System.out.println("Se memoreaza luna: "+luna.getNumeLuna());
                
                ps1.setString(1,luna.getNumeLuna());
                ps1.setString(2,luna.getNrLuna());
                ps1.setString(3,String.valueOf(luna.getOreFizLuna()));
                ps1.setString(4,String.valueOf(luna.getOreLucrLuna()));
                ps1.setString(5,idCA);
                ps1.setString(6,String.valueOf(luna.getNrZile()));
                ps1.executeUpdate();
                
                // trebuie sa aflu id-ul lunii pe care am inserat-o
                String lunaId = getLunaCalendId(luna.getNrLuna(),idCA);
            
            // parcurg fiecare luna; obtin din ca nr de zile; parcurg zilele din ca => nr, nume, tip
            return lunaId;
        }catch(SQLException e){e.printStackTrace();}
        return "";
    }
    
    public static boolean memoreazaZiuaA(ZiCalend ziua, String idLC){
        try{
                PreparedStatement ps1=conex.getConexiune().prepareStatement("insert into zilecalendaristice(numar,nume,tip,idLunaCalendaristica) values(?,?,?,?);");
                //System.out.println("Se memoreaza ziua: "+ziua.getNrZiCal());
                
                ps1.setString(1,ziua.getNrZiCal());
                ps1.setString(2,ziua.getNumeZiCal());
                ps1.setString(3,ziua.getTipZiCal());
                ps1.setString(4,idLC);
                ps1.executeUpdate();
            
            return true;
        }catch(SQLException e){e.printStackTrace();}
        return false;
    }
    
    public static String memoreazaParticipare(Participare p){
        try{
            String query="insert into participari(dataIncepere,dataIncheiere,nrOreLuna,functie,idCadruDidactic,idProiectCercetare) values(?,?,?,?,?,?);";
            PreparedStatement ps;
            ps = conex.getConexiune().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            try{
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                ps.setString(1, sdf.format(new SimpleDateFormat("yyyy-MM-dd").parse(p.getDataIncepere())));
                ps.setString(2, sdf.format(new SimpleDateFormat("yyyy-MM-dd").parse(p.getDataIncheiere())));
                ps.setString(3, p.getNrOre());
                ps.setString(4, p.getFunctie());
                ps.setString(5, p.getCdId());
                ps.setString(6, p.getProiectId());
                ps.executeUpdate();
                // trebuie sa aflu id-ul participarii pe care am inserat-o
                String pId="";
                ResultSet rs = ps.getGeneratedKeys();
                if(rs.next())
                {
                    pId = String.valueOf(rs.getInt(1));
                }
            return pId;
            }catch(Exception ex){System.out.println(ex.getMessage());}
            
        }catch(SQLException e){e.printStackTrace();}
        return "-1";
    }
    
    public static boolean memoreazaProgram(ProgramParticipare pp){
        try{
                PreparedStatement ps1=conex.getConexiune().prepareStatement("insert into programparticipari(idParticipare,oreLu,oreMa,oreMi,oreJoi,oreVi) values(?,?,?,?,?,?);");
                
                ps1.setString(1,pp.getIdParticipare());
                ps1.setString(2,pp.getOre()[0]);
                ps1.setString(3,pp.getOre()[1]);
                ps1.setString(4,pp.getOre()[2]);
                ps1.setString(5,pp.getOre()[3]);
                ps1.setString(6,pp.getOre()[4]);
                ps1.executeUpdate();
            
            return true;
        }catch(SQLException e){e.printStackTrace();}
        return false;
    }
    
    public static String memoreazaActivitate(String data, String ore, String cdId){
        try{
            String query="insert into alteactivitati(nrOre,data,idCadruDidactic) values(?,?,?);";
            PreparedStatement ps;
            ps = conex.getConexiune().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            try{
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                ps.setString(1, ore);
                ps.setString(2, sdf.format(new SimpleDateFormat("yyyy-MM-dd").parse(data)));
                ps.setString(3, cdId);
                ps.executeUpdate();
                // trebuie sa aflu id-ul activitatii pe care am inserat-o
                String aId="";
                ResultSet rs = ps.getGeneratedKeys();
                if(rs.next())
                {
                    aId = String.valueOf(rs.getInt(1));
                }
            return aId;
            }catch(Exception ex){System.out.println(ex.getMessage());}
            
        }catch(SQLException e){e.printStackTrace();}
        return "-1";
    }
    
    public static boolean memoreazaFPI(Document d, int opo, int ob, String an, String luna){
        try{
            PreparedStatement ps=conex.getConexiune().prepareStatement("insert into documente(luna,tipDocument,anUniversitar,nrOreDeBaza,nrOrePlataOra,idCadruDidactic) values(?,?,?,?,?,?);");
            System.out.println("Se memoreaza documentul: "+d.getDocPath());

            ps.setString(1,luna);
            ps.setString(2,d.getTipDoc());
            ps.setString(3,an);
            ps.setString(4,String.valueOf(ob));
            ps.setString(5,String.valueOf(opo));
            ps.setString(6,d.getCdId());
            ps.executeUpdate();
            return true;
        }catch(SQLException e){e.printStackTrace();}
        return false;
    }
    
    public static String memoreazaRPO(Document d, String oc, String oa, String an, String luna){
        try{
            String query="insert into documente(luna,tipDocument,anUniversitar,oreCurs,oreAplicatii,idCadruDidactic) values(?,?,?,?,?,?);";
            PreparedStatement ps;
            ps = conex.getConexiune().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            System.out.println("Se memoreaza documentul: "+d.getDocPath());

            ps.setString(1,luna);
            ps.setString(2,d.getTipDoc());
            ps.setString(3,an);
            ps.setString(4,oc);
            ps.setString(5,oa);
            ps.setString(6,d.getCdId());
            ps.executeUpdate();
            
            // trebuie sa aflu id-ul rpo pe care l-am memorat
            String idRPO="";
            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next())
            {
                idRPO = String.valueOf(rs.getInt(1));
            }
            return idRPO;
        }catch(SQLException e){e.printStackTrace();}
        return "-1";
    }
    
    public static boolean memoreazaZiRPO(String nrZi, String nrOre, String idRPO){
        try{
            String query="insert into zilerpo(nrZi,nrOre,idReferatPlataOra) values(?,?,?);";
            PreparedStatement ps;
            ps = conex.getConexiune().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            //System.out.println("Se memoreaza ziua: "+nrZi);

            ps.setString(1,nrZi);
            ps.setString(2,nrOre);
            ps.setString(3,idRPO);
            ps.executeUpdate();
            
            return true;
        }catch(SQLException e){e.printStackTrace();}
        return false;
    }
    
    public static boolean memoreazaFPPFormular(String idCd, String idP, int totalOre, String an, String luna){
        try{
            PreparedStatement ps=conex.getConexiune().prepareStatement("insert into documente(luna,tipDocument,anUniversitar,totalOreProiect,idCadruDidactic) values(?,?,?,?,?);");
            //System.out.println("Se memoreaza documentul: "+d.getDocPath());

            ps.setString(1,luna);
            ps.setString(2,"fpp");
            ps.setString(3,an);
            ps.setString(4,String.valueOf(totalOre));
            ps.setString(5,idCd);
            ps.executeUpdate();
            return true;
        }catch(SQLException e){e.printStackTrace();}
        return false;
    }
    
    public static boolean memoreazaFPPDoc(Document d, String idP, int totalOre, String an, String luna){
        try{
            PreparedStatement ps=conex.getConexiune().prepareStatement("insert into documente(luna,tipDocument,anUniversitar,totalOreProiect,idCadruDidactic) values(?,?,?,?,?);");
            System.out.println("Se memoreaza documentul: "+d.getDocPath());

            ps.setString(1,luna);
            ps.setString(2,d.getTipDoc());
            ps.setString(3,an);
            ps.setString(4,String.valueOf(totalOre));
            ps.setString(5,d.getCdId());
            ps.executeUpdate();
            return true;
        }catch(SQLException e){e.printStackTrace();}
        return false;
    }
    
    public static boolean memoreazaParticipare(String incepere, String incheiere, int ol, String functie, String idCd, String idP){
        try{
            // intai memorez fpp, daca nu exista deja pt acest proiect
            //idFPP=memoreazaFPPFormular();
            // daca exista deja, obtin idFPP (selectez idDocument, pt care documentul e referit intr-o participare la proiectul idP)
            
            PreparedStatement ps=conex.getConexiune().prepareStatement("insert into participari(dataIncepere,dataIncheiere,nrOreLuna,functie,idCadruDidactic,idProiectCercetare,idFoaiePontaj) values(?,?,?,?,?,?,?);");
       
            ps.setString(1,incepere);
            ps.setString(2,incheiere);
            ps.setString(3,String.valueOf(ol));
            ps.setString(4,functie);
            ps.setString(5,idCd);
            ps.setString(6,idP);
            //ps.setString(7,idFPP);
            ps.executeUpdate();
            return true;
        }catch(SQLException e){e.printStackTrace();}
        return false;
    }
    
    public static String getCAId(String an){
       try{
            String comanda="select idDocument from documente "
                    +" where anUniversitar= \""+an+"\" and tipDocument=\"ca\";";
            ResultSet rs=stmt.executeQuery(comanda);
            if(rs.next()){
                return rs.getString("idDocument");
            }
        }catch(SQLException e){e.printStackTrace();}
        return "";
   }
    
    public static String getProiectId(String cod){
       try{
            String comanda="select idProiect from proiectecercetare "
                    +" where cod= \""+cod+"\";";
            ResultSet rs=stmt.executeQuery(comanda);
            if(rs.next()){
                return rs.getString("idProiect");
            }
        }catch(SQLException e){e.printStackTrace();}
        return "";
   }
    
    public static String getLunaCalendId(String nr, String idCA){
        char cifra='0';
        if(nr.length()>1){
            cifra = nr.charAt(1);
        }
       try{
            String comanda="select idLuna from lunicalendaristice "
                    +" where (numar= \""+nr+"\" or numar= \""+cifra+"\" or numar= \""+"0"+nr+"\") and idCalendarAcademic=\""+idCA+"\";";
            ResultSet rs=stmt.executeQuery(comanda);
            if(rs.next()){
                return rs.getString("idLuna");
            }
        }catch(SQLException e){e.printStackTrace();}
        return "";
   }
    
    public static String getLunaDenumire(String nr){
       try{
            String comanda="select denumire from lunicalendaristice "
                    +" where numar= \""+nr+"\";";
            ResultSet rs=stmt.executeQuery(comanda);
            if(rs.next()){
                return rs.getString("denumire");
            }
        }catch(SQLException e){e.printStackTrace();}
        return "";
   }
    
    public static String getLunaNrZile(String idLuna){
       try{
            String comanda="select COUNT(idZi) as nr from zilecalendaristice "
                    +" where idLunaCalendaristica= \""+idLuna+"\";";
            ResultSet rs=stmt.executeQuery(comanda);
            if(rs.next()){
                return rs.getString("nr");
            }
        }catch(SQLException e){e.printStackTrace();}
        return "";
   } 
    
    public static String getNumeZi(String idLuna, String nrZi){
       try{
            String comanda="select lower(nume) as nume from zilecalendaristice "
                    +" where idLunaCalendaristica= \""+idLuna+"\" and numar= \""+nrZi+"\";";
            ResultSet rs=stmt.executeQuery(comanda);
            if(rs.next()){
                return rs.getString("nume");
            }
        }catch(SQLException e){e.printStackTrace();}
        return "";
   }
    
    public static String getTipZi(String idLuna, String nrZi){
       try{
            String comanda="select tip from zilecalendaristice "
                    +" where idLunaCalendaristica= \""+idLuna+"\" and numar= \""+nrZi+"\";";
            ResultSet rs=stmt.executeQuery(comanda);
            if(rs.next()){
                return rs.getString("tip");
            }
        }catch(SQLException e){e.printStackTrace();}
        return "";
   }
    
    public static String getTipZiL(String an,String luna, String nrZi){
       try{
            String comanda="select tip from (zilecalendaristice zc inner join lunicalendaristice lc on zc.idLunaCalendaristica=lc.idLuna)"
                    +"inner join documente ca on ca.idDocument=lc.idCalendarAcademic"
                    +" where (lc.denumire= \""+luna+"\" or lc.numar= \""+luna+"\" or concat(\"0\",lc.numar)= \""+luna+"\") and (concat(\"0\",zc.numar)= \""+nrZi+"\" or zc.numar= \""+nrZi+"\") and ca.anUniversitar= \""+an+"\" ;";
            ResultSet rs=stmt.executeQuery(comanda);
            if(rs.next()){
                return rs.getString("tip");
            }
        }catch(SQLException e){e.printStackTrace();}
        return "";
   }
    
    public static String getTipProiect(String idPr){
       try{
            String comanda="select tip from proiectecercetare "
                    +" where idProiect= \""+idPr+"\";";
            ResultSet rs=stmt.executeQuery(comanda);
            if(rs.next()){
                return rs.getString("tip");
            }
        }catch(SQLException e){e.printStackTrace();}
        return "";
   }
    
    public static int getNrProiecte(){
       try{
            String comanda="select COUNT(idProiect) as nrP from proiectecercetare;";
            ResultSet rs=stmt.executeQuery(comanda);
            if(rs.next()){
                return Integer.parseInt(rs.getString("nrP"));
            }
        }catch(SQLException e){e.printStackTrace();}
        return 0;
   }
    
    public static String getPartId(String lpID){
       try{
            String comanda="select idParticipare from lucruproiecte "
                    +" where idLucruProiect= \""+lpID+"\";";
            ResultSet rs=stmt.executeQuery(comanda);
            if(rs.next()){
                return rs.getString("idParticipare");
            }
        }catch(SQLException e){e.printStackTrace();}
        return "";
   }
    
    public static String getLucru(String idPart,String idZi){
       try{
            String comanda="select idLucruProiect from lucruproiecte "
                    +" where idParticipare= \""+idPart+"\" and idZiLucru= \""+idZi+"\";";
            ResultSet rs=stmt.executeQuery(comanda);
            if(rs.next()){
                return rs.getString("idLucruProiect");
            }
        }catch(SQLException e){e.printStackTrace();}
        return "-1";
   }
    
    public static String getNrOreZiRPO(String nrZi,String luna,String an, String cdId){
       try{
            String comanda="select nrOre from zileRPO zr inner join documente d on zr.idReferatPlataOra=d.idDocument"
                    +" where zr.nrZi= \""+nrZi+"\" and d.luna= \""+luna+"\" and d.anUniversitar= \""+an+"\" and d.idCadruDidactic= \""+cdId+"\";";
            ResultSet rs=stmt.executeQuery(comanda);
            if(rs.next()){
                return rs.getString("nrOre");
            }
        }catch(SQLException e){e.printStackTrace();}
        return "-1";
   }
    
    public static int getNrOrePlataOra(String luna,String an, String cdId){
       try{
            String comanda="select nrOrePlataOra from documente"
                    +" where tipDocument= \"fpi\" and (luna= \""+luna+"\" or concat(\"0\",luna)= \""+luna+"\") and anUniversitar= \""+an+"\" and idCadruDidactic= \""+cdId+"\";";
            ResultSet rs=stmt.executeQuery(comanda);
            if(rs.next()){
                return Integer.parseInt(rs.getString("nrOrePlataOra"));
            }
        }catch(SQLException e){e.printStackTrace();}
        return 0;
   }
    
    public static ArrayList<String> getPartZi(String data, String cdId){
        ArrayList<String> partIDs = new ArrayList<String>();
        try{
             String comanda="select idParticipare from participari "
                     +" where idCadruDidactic= \""+cdId+"\" and dataIncepere<= \""+data+"\" and dataIncheiere>= \""+data+"\";";
             ResultSet rs=stmt.executeQuery(comanda);
             while(rs.next()){
                partIDs.add(rs.getString("idParticipare"));
             }
         }catch(SQLException e){e.printStackTrace();}
         return partIDs;
   }
    
    public static String getAlteOreZi(String data, String cdId){
        try{
             String comanda="select nrOre from alteactivitati "
                     +" where idCadruDidactic= \""+cdId+"\" and data= \""+data+"\";";
             ResultSet rs=stmt.executeQuery(comanda);
             if(rs.next()){
                return rs.getString("nrOre");
             }
         }catch(SQLException e){e.printStackTrace();}
         return "0";
   }
    
    public static ArrayList<String> getLucruPZi(String idZL){
        ArrayList<String> lpIDs = new ArrayList<String>();
        try{
             String comanda="select idLucruProiect from lucruproiecte "
                     +" where idZiLucru= \""+idZL+"\";";
             ResultSet rs=stmt.executeQuery(comanda);
             while(rs.next()){
                 lpIDs.add(rs.getString("idLucruProiect"));
             }
         }catch(SQLException e){e.printStackTrace();}
         return lpIDs;
   }
    
    public static String getProgramZi(String oreZi, String partId){
        String partIDs[] = null;
        try{
            // select pp.oreZi from participari p left outer join programproiecte pp on p.idParticipare=pp.idParticipare where idParticipare=partIDs[i]
             String comanda="select "+oreZi+" from participari p left outer join programparticipari pp on p.idParticipare=pp.idParticipare"
                     +" where p.idParticipare= \""+partId+"\";";
             ResultSet rs=stmt.executeQuery(comanda);
             if(rs.next()){
                return rs.getString(oreZi);
             }
         }catch(SQLException e){e.printStackTrace();}
         return "-1";
    }
    
    public static String memoreazaLunaL(LunaLucru ll){
        try{
            String query="insert into lunilucru(denumire,numar,anUniversitar,idCadruDidactic) values(?,?,?,?);";
            PreparedStatement ps;
            ps = conex.getConexiune().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            //System.out.println("Se memoreaza luna: "+ll.getDenumire());

            ps.setString(1,ll.getDenumire());
            ps.setString(2,ll.getNumar());
            ps.setString(3,ll.getAn());
            ps.setString(4,ll.getcdId());
            ps.executeUpdate();
            
            // trebuie sa aflu id-ul lunii pe care am memorat-o
            String idLL="";
            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next())
            {
                idLL = String.valueOf(rs.getInt(1));
            }
            return idLL;
        }catch(SQLException e){e.printStackTrace();}
        return "-1";
    }
    
    public static boolean updateLunaL(LunaLucru ll, String idLL){
        try{
            PreparedStatement ps=conex.getConexiune().prepareStatement("update lunilucru set oreBazaLucrate = ? where idLunaLucru = \""+idLL+"\";");

            ps.setString(1,ll.getOreBaza());
            ps.executeUpdate();
            return true;
        }catch(SQLException e){e.printStackTrace();}
        return false;
    }
    
    public static boolean updateActivitate(String data, String ore, String cdId){
        try{
            PreparedStatement ps=conex.getConexiune().prepareStatement("update alteactivitati set nrOre = ? "
                    +"where idCadruDidactic = \""+cdId+"\" and data = \""+data+"\";");

            ps.setString(1,ore);
            ps.executeUpdate();
            return true;
        }catch(SQLException e){e.printStackTrace();}
        return false;
    }
    
    public static String memoreazaZiL(ZiLucru zl){
        try{
            String query="insert into zilelucru (nrZi,nrOrePlataOra,alteOre,idLunaLucru) values(?,?,?,?);";
            PreparedStatement ps;
            ps = conex.getConexiune().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            //System.out.println("Se memoreaza ziua: "+zl.getNr());

            ps.setString(1,zl.getNr());
            ps.setString(2,zl.getOrePlataOra());
            ps.setString(3,zl.getAlteOre());
            ps.setString(4,zl.getIdLunaL());
            ps.executeUpdate();
            
            // trebuie sa aflu id-ul zilei pe care am memorat-o
            String idZL="";
            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next())
            {
                idZL = String.valueOf(rs.getInt(1));
            }
            return idZL;
        }catch(SQLException e){e.printStackTrace();}
        return "-1";
    }
    
    public static boolean updateZiL(ZiLucru zl){
        try{
            PreparedStatement ps=conex.getConexiune().prepareStatement("update zilelucru set nrOreBaza = ? where idZiLucru = \""+zl.getId()+"\";");

            ps.setString(1,zl.getOreBaza());
            ps.executeUpdate();
            return true;
        }catch(SQLException e){e.printStackTrace();}
        return false;
    }
    
    public static boolean updateLucru(String idL, int ore){
        try{
            PreparedStatement ps=conex.getConexiune().prepareStatement("update lucruproiecte set nrOre = nrOre+? where idLucruProiect = \""+idL+"\";");
            
            ps.setInt(1,ore);
            ps.executeUpdate();
            return true;
        }catch(SQLException e){e.printStackTrace();}
        return false;
    }
    
    public static boolean memoreazaLP(LucruProiect lp){
        try{
            PreparedStatement ps=conex.getConexiune().prepareStatement("insert into lucruproiecte(nrOre,idZiLucru,idParticipare) values(?,?,?);");

            ps.setString(1,lp.getNrOre());
            ps.setString(2,lp.getIdZiLucru());
            ps.setString(3,lp.getIdParticipare());
            ps.executeUpdate();
            return true;
        }catch(SQLException e){e.printStackTrace();}
        return false;
    }
    
    public static boolean existaPOCU(String idZiL){
        try{
            String comanda="select tip from ((proiectecercetare pc inner join participari p on pc.idProiect=p.idProiectCercetare) inner join lucruproiecte lp on p.idParticipare=lp.idParticipare) inner join zilelucru zl on zl.idZiLucru=lp.idZiLucru"
                    +" where zl.idZiLucru= \""+idZiL+"\" and (lower(pc.tip)= \"pocu\" or lower(pc.tip)= \"posdru\") and nrOre<>\""+0+"\";";
            ResultSet rs=stmt.executeQuery(comanda);
            if(rs.next()){
                return true;
            }
        }catch(SQLException e){e.printStackTrace();}
        return false;
    }
    
    public static String getOreLP(String idLP){
        try{
             String comanda="select nrOre from lucruproiecte "
                     +" where idLucruProiect= \""+idLP+"\";";
             ResultSet rs=stmt.executeQuery(comanda);
             if(rs.next()){
                return rs.getString("nrOre");
             }
         }catch(SQLException e){e.printStackTrace();}
         return "-1";
   }
    
    public static int getOreLunaC(String numarL, String anUniv){
        try{    
             String comanda="select lc.oreLucrate from documente d inner join lunicalendaristice lc on d.IdDocument=lc.idCalendarAcademic "
                     +" where d.anUniversitar= \""+anUniv+"\"and lc.numar=\""+numarL+"\";";
             ResultSet rs=stmt.executeQuery(comanda);
             if(rs.next()){
                return Integer.parseInt(rs.getString("oreLucrate"));
             }
         }catch(SQLException e){e.printStackTrace();}
         return 0;
   }
    
    public static int getOreBazaLunaL(String numarL, String anUniv, String cdId){
        try{    
             String comanda="select oreBazaLucrate from lunilucru "
                     +" where idCadruDidactic= \""+cdId+"\"and (numar= \""+numarL+"\" or concat(\"0\",numar)= \""+numarL+"\") and anUniversitar=\""+anUniv+"\";";
             ResultSet rs=stmt.executeQuery(comanda);
             if(rs.next()){
                return Integer.parseInt(rs.getString("oreBazaLucrate"));
             }
         }catch(SQLException e){e.printStackTrace();}
         return 0;
   }
    
    public static int getAlteOre(String idLL){
        try{    
             String comanda="select SUM(alteOre) as nr from zileLucru "
                     +" where idLunaLucru= \""+idLL+"\";";
             ResultSet rs=stmt.executeQuery(comanda);
             if(rs.next()){
                return (int) Double.parseDouble(rs.getString("nr"));
             }
         }catch(SQLException e){e.printStackTrace();}
         return 0;
   }
    
    public static ArrayList<String> getOreZi(String idLL,int nr){
        try{    
             String comanda="select nrOreBaza,nrOrePlataOra,alteOre from zileLucru "
                     +" where idLunaLucru= \""+idLL+"\" and nrZi="+nr+";";
             ResultSet rs=stmt.executeQuery(comanda);
             ArrayList<String> ore=new ArrayList<String>();
             if(rs.next()){
                ore.add(rs.getString("nrOreBaza"));
                ore.add(rs.getString("nrOrePlataOra"));
                ore.add(rs.getString("alteOre"));
             }
             return ore;
         }catch(SQLException e){e.printStackTrace();}
         return null;
   }
    
    public static String getOrePZi(String idLL,int nr,String denumire, String functie){
        try{    
             String comanda="select nrOre from ((zileLucru zl inner join lucruproiecte lp on zl.idZiLucru=lp.idZiLucru)"
                     +" inner join participari p on lp.idParticipare=p.idParticipare) right outer join proiectecercetare pc on pc.idProiect=p.idProiectCercetare"
                     +" where idLunaLucru= \""+idLL+"\" and nrZi="+nr+" and pc.denumire=\""+denumire+"\" and p.functie=\""+functie+"\";";
             ResultSet rs=stmt.executeQuery(comanda);
             int nrOre=0;
             while(rs.next()){
                nrOre+= (int) Double.parseDouble(rs.getString("nrOre"));
             }
             return String.valueOf(nrOre);
         }catch(SQLException e){e.printStackTrace();}
         return "0";
   }
    
    public static ArrayList<String> getFunctiiPr(String idLL){
        try{    
             String comanda="select p.idParticipare,functie from ((participari p inner join lucruproiecte lp on p.idParticipare=lp.idParticipare) "
                     +" inner join zilelucru zl on zl.idZiLucru=lp.idZiLucru) inner join lunilucru ll on ll.idLunaLucru=zl.idLunaLucru"
                     +" where ll.idLunaLucru= \""+idLL+"\" group by p.idParticipare order by p.idParticipare;";
             ResultSet rs=stmt.executeQuery(comanda);
             ArrayList<String> f = new ArrayList<String>();
             while(rs.next()){
                f.add(rs.getString("functie"));
             }
             return f;
         }catch(SQLException e){e.printStackTrace();}
         return null;
   }
    
    public static ArrayList<String> getParticipariPr(String idLL){
        try{    
             String comanda="select p.idParticipare, concat(pc.denumire,\".\",p.functie) as proiect from (((participari p inner join lucruproiecte lp on p.idParticipare=lp.idParticipare) "
                     +" inner join zilelucru zl on zl.idZiLucru=lp.idZiLucru) inner join lunilucru ll on ll.idLunaLucru=zl.idLunaLucru)"
                     +" inner join proiectecercetare pc on pc.idProiect=p.idProiectCercetare"
                     +" where ll.idLunaLucru= \""+idLL+"\" group by proiect order by p.idParticipare;";
             ResultSet rs=stmt.executeQuery(comanda);
             ArrayList<String> p = new ArrayList<String>();
             while(rs.next()){
                p.add(rs.getString("proiect"));
             }
             return p;
         }catch(SQLException e){e.printStackTrace();}
         return null;
   }
    
    public static int getTotalOreZi(String idZi){
        try{    
             String comanda="select nrOrePlataOra, nrOreBaza, alteOre from zilelucru"
                     +" where idZiLucru= \""+idZi+"\";";
             ResultSet rs=stmt.executeQuery(comanda);
             if(rs.next()){
                return (int) (Double.parseDouble(rs.getString("nrOrePlataOra"))+Double.parseDouble(rs.getString("nrOreBaza"))+Double.parseDouble(rs.getString("alteOre")));
             }
         }catch(SQLException e){e.printStackTrace();}
         return 0;
   }
    
    public static ArrayList<String[]> cautaNumeLunaAn(String nume,String luna, String an){
        ArrayList<String[]> listaFoi = new ArrayList<String[]>();
        String[] item = nume.split(" ");
        String cdId=ManagerCont.getCdId(item[0],item[1]);
        if(Integer.parseInt(luna)<10){
            an=(Integer.parseInt(an)-1)+"-"+an;
        }
        else{
            an+="-"+(Integer.parseInt(an)+1);
        }
        try{
             String comanda="select nrOrePlataOra,nrOreBaza,alteOre from foiprezenta f inner join lunilucru l on f.idLunaLucru=l.idLunaLucru"
                    +" where l.idCadruDidactic= \""+cdId+"\" and (l.numar=\""+
                    luna+"\" or l.numar=\""+"0"+
                    luna+"\") and l.anUniversitar=\""+an+"\";";
             ResultSet rs=stmt.executeQuery(comanda);
             while(rs.next()){
                 String[] foaie= {item[0],item[1],luna,an,rs.getString("nrOrePlataOra"),rs.getString("nrOreBaza"),rs.getString("alteOre")};
                 listaFoi.add(foaie);
             }
         }catch(SQLException e){e.printStackTrace();}
         return listaFoi;
   }
    
    public static ArrayList<String[]> cautaNumeLuna(String nume,String luna){
        ArrayList<String[]> listaFoi = new ArrayList<String[]>();
        String[] item = nume.split(" ");
        String cdId=ManagerCont.getCdId(item[0],item[1]);
        try{
             String comanda="select nrOrePlataOra,nrOreBaza,alteOre,l.anUniversitar from foiprezenta f inner join lunilucru l on f.idLunaLucru=l.idLunaLucru"
                    +" where l.idCadruDidactic= \""+cdId+"\" and (l.numar=\""+
                    luna+"\" or l.numar=\""+"0"+
                    luna+"\") order by l.idCadruDidactic,l.anUniversitar,l.numar;";
             ResultSet rs=stmt.executeQuery(comanda);
             while(rs.next()){
                 String[] foaie= {item[0],item[1],luna,rs.getString("l.anUniversitar"),rs.getString("nrOrePlataOra"),rs.getString("nrOreBaza"),rs.getString("alteOre")};
                 listaFoi.add(foaie);
             }
         }catch(SQLException e){e.printStackTrace();}
         return listaFoi;
   }
    
    public static ArrayList<String[]> cautaNumeAn(String nume,String an){
        ArrayList<String[]> listaFoi = new ArrayList<String[]>();
        String[] item = nume.split(" ");
        String cdId=ManagerCont.getCdId(item[0],item[1]);
        String an1=(Integer.parseInt(an)-1)+"-"+an;
        String an2= an+"-"+(Integer.parseInt(an)+1);
        try{
             String comanda="select nrOrePlataOra,nrOreBaza,alteOre,l.anUniversitar,l.numar from foiprezenta f inner join lunilucru l on f.idLunaLucru=l.idLunaLucru"
                    +" where l.idCadruDidactic= \""+cdId+"\" and (l.anUniversitar=\""+an1+"\" or l.anUniversitar=\""+an2+"\") order by l.idCadruDidactic,l.anUniversitar,l.numar;";
             ResultSet rs=stmt.executeQuery(comanda);
             while(rs.next()){
                 String[] foaie= {item[0],item[1],rs.getString("l.numar"),rs.getString("l.anUniversitar"),rs.getString("nrOrePlataOra"),rs.getString("nrOreBaza"),rs.getString("alteOre")};
                 listaFoi.add(foaie);
             }
         }catch(SQLException e){e.printStackTrace();}
         return listaFoi;
   }
    
    public static ArrayList<String[]> cautaNume(String nume){
        ArrayList<String[]> listaFoi = new ArrayList<String[]>();
        String[] item = nume.split(" ");
        String cdId=ManagerCont.getCdId(item[0],item[1]);
        try{
             String comanda="select nrOrePlataOra,nrOreBaza,alteOre,l.anUniversitar,l.numar from foiprezenta f inner join lunilucru l on f.idLunaLucru=l.idLunaLucru"
                    +" where l.idCadruDidactic= \""+cdId+"\" order by l.idCadruDidactic,l.anUniversitar,l.numar;";
             ResultSet rs=stmt.executeQuery(comanda);
             while(rs.next()){
                 String[] foaie= {item[0],item[1],rs.getString("l.numar"),rs.getString("l.anUniversitar"),rs.getString("nrOrePlataOra"),rs.getString("nrOreBaza"),rs.getString("alteOre")};
                 listaFoi.add(foaie);
             }
         }catch(SQLException e){e.printStackTrace();}
         return listaFoi;
   }
    
    public static ArrayList<String[]> cautaLunaAn(String luna, String an){
        ArrayList<String[]> listaFoi = new ArrayList<String[]>();
        if(Integer.parseInt(luna)<10){
            an=(Integer.parseInt(an)-1)+"-"+an;
        }
        else{
            an+="-"+(Integer.parseInt(an)+1);
        }
        try{
             String comanda="select p.nume,p.prenume,nrOrePlataOra,nrOreBaza,alteOre from ((foiprezenta f inner join lunilucru l on f.idLunaLucru=l.idLunaLucru)"
                    +"inner join cadredidactice cd on l.idCadruDidactic=cd.idCadruDidactic) inner join persoane p on p.idPersoana=cd.idPersoana"
                    +" where (l.numar=\""+
                    luna+"\" or l.numar=\""+"0"+
                    luna+"\") and l.anUniversitar=\""+an+"\" order by p.nume,p.prenume,l.anUniversitar,l.numar;";
             ResultSet rs=stmt.executeQuery(comanda);
             while(rs.next()){
                 String[] foaie= {rs.getString("p.nume"),rs.getString("p.prenume"),luna,an,rs.getString("nrOrePlataOra"),rs.getString("nrOreBaza"),rs.getString("alteOre")};
                 listaFoi.add(foaie);
             }
         }catch(SQLException e){e.printStackTrace();}
         return listaFoi;
   }
    
    public static ArrayList<String[]> cautaLuna(String luna){
        ArrayList<String[]> listaFoi = new ArrayList<String[]>();
        try{
             String comanda="select p.nume,p.prenume,nrOrePlataOra,nrOreBaza,alteOre,l.anUniversitar from ((foiprezenta f inner join lunilucru l on f.idLunaLucru=l.idLunaLucru)"
                    +"inner join cadredidactice cd on l.idCadruDidactic=cd.idCadruDidactic) inner join persoane p on p.idPersoana=cd.idPersoana"
                    +" where (l.numar=\""+
                    luna+"\" or l.numar=\""+"0"+
                    luna+"\") order by p.nume,p.prenume,l.anUniversitar,l.numar;";
             ResultSet rs=stmt.executeQuery(comanda);
             while(rs.next()){
                 String[] foaie= {rs.getString("p.nume"),rs.getString("p.prenume"),luna,rs.getString("l.anUniversitar"),rs.getString("nrOrePlataOra"),rs.getString("nrOreBaza"),rs.getString("alteOre")};
                 listaFoi.add(foaie);
             }
         }catch(SQLException e){e.printStackTrace();}
         return listaFoi;
   }
    
    public static ArrayList<String[]> cautaAn(String an){
        ArrayList<String[]> listaFoi = new ArrayList<String[]>();
        String an1=(Integer.parseInt(an)-1)+"-"+an;
        String an2= an+"-"+(Integer.parseInt(an)+1);
        try{
             String comanda="select p.nume,p.prenume,nrOrePlataOra,nrOreBaza,alteOre,l.anUniversitar,l.numar from ((foiprezenta f inner join lunilucru l on f.idLunaLucru=l.idLunaLucru)"
                    +"inner join cadredidactice cd on l.idCadruDidactic=cd.idCadruDidactic) inner join persoane p on p.idPersoana=cd.idPersoana"
                    +" where (l.anUniversitar=\""+an1+"\" or l.anUniversitar=\""+an2+"\") order by p.nume,p.prenume,l.anUniversitar,l.numar;";
             ResultSet rs=stmt.executeQuery(comanda);
             while(rs.next()){
                 String[] foaie= {rs.getString("p.nume"),rs.getString("p.prenume"),rs.getString("l.numar"),rs.getString("l.anUniversitar"),rs.getString("nrOrePlataOra"),rs.getString("nrOreBaza"),rs.getString("alteOre")};
                 listaFoi.add(foaie);
             }
         }catch(SQLException e){e.printStackTrace();}
         return listaFoi;
   }
    
    public static ArrayList<String[]> getSituatieCd(String luna,String an, String deptId){
        ArrayList<String[]> listaCD = new ArrayList<String[]>();
        if(Integer.parseInt(luna)<10){
            an=(Integer.parseInt(an)-1)+"-"+an;
        }
        else{
            an+="-"+(Integer.parseInt(an)+1);
        }
        try{
             String comanda="SELECT cd1.grad,p1.nume,p1.prenume,cd1.email from (cadredidactice cd1 inner join persoane p1 on p1.idPersoana=cd1.idPersoana)\n"
                            +"WHERE NOT EXISTS (select cd.grad,p.nume,p.prenume,cd.email,f.idFoaiePrezenta from ((cadredidactice cd inner join persoane p on p.idPersoana=cd.idPersoana)\n"
                            +"left outer join lunilucru l on l.idCadruDidactic=cd.idCadruDidactic) left outer join foiprezenta f on  f.idLunaLucru=l.idLunaLucru\n"
                            +"where l.anUniversitar=\""+an+"\" and (l.numar=\"0"+luna+"\" or l.numar=\""+luna+"\") and cd.idCadruDidactic=cd1.idCadruDidactic) and cd1.idDepartament=\""+deptId+"\"\n"
                            +"order by p1.nume,p1.prenume;";
             ResultSet rs=stmt.executeQuery(comanda);
             while(rs.next()){
                 String[] profesor= {rs.getString("cd1.grad"),rs.getString("p1.nume"),rs.getString("p1.prenume"),rs.getString("cd1.email")};
                 listaCD.add(profesor);
             }
         }catch(SQLException e){e.printStackTrace();}
         return listaCD;
   }
    
   public static boolean existaProiect(String nume){
       try{    
             String comanda="select idProiect from proiectecercetare "
                     +" where UPPER(denumire)= UPPER(\""+nume+"\");";
             ResultSet rs=stmt.executeQuery(comanda);
             if(rs.next()){
                return true;
             }
         }catch(SQLException e){e.printStackTrace();}
       return false;
   }
   
   public static boolean estePosdru(String nume){
       try{    
             String comanda="select tip from proiectecercetare "
                     +" where UPPER(denumire)= \""+nume.toUpperCase()+"\";";
             ResultSet rs=stmt.executeQuery(comanda);
             if(rs.next()){
                 if(rs.getString("tip").equals("POSDRU"))
                return true;
             }
         }catch(SQLException e){e.printStackTrace();}
       return false;
   }
   
   public static boolean estePocu(String nume){
       try{    
             String comanda="select tip from proiectecercetare "
                     +" where UPPER(denumire)= \""+nume.toUpperCase()+"\";";
             ResultSet rs=stmt.executeQuery(comanda);
             if(rs.next()){
                 if(rs.getString("tip").equals("POCU"))
                return true;
             }
         }catch(SQLException e){e.printStackTrace();}
       return false;
   }
}
