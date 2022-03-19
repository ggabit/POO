
package GenerareFoaiePrezenta;

public class LucruProiect {
    private String idZiLucru;
    private String idParticipare;
    private String nrOre;
    
    public LucruProiect(String idZL, String idP, String no){
        idZiLucru=idZL;
        idParticipare=idP;
        nrOre=no;
    }
    
    public String getIdZiLucru(){
        return idZiLucru;
    }
    
    public String getIdParticipare(){
        return idParticipare;
    }
    
    public String getNrOre(){
        return nrOre;
    }
}
