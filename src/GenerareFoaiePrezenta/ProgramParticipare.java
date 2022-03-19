
package GenerareFoaiePrezenta;


public class ProgramParticipare {
    private String idPart;
    private String[] ore;
    
    public ProgramParticipare(String idP, String[] ore){
        idPart=idP;
        this.ore=ore;
    }
    
    public String getIdParticipare(){
        return idPart;
    }
    
    public String[] getOre(){
        return ore;
    }
}
