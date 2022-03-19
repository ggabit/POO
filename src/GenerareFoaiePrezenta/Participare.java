
package GenerareFoaiePrezenta;

public class Participare {
    private String incepere;
    private String incheiere;
    private String functie;
    private String nrOre;
    private String cdId;
    private String prId;
    
    public Participare(String incep, String inch, String f, String no, String cd, String p){
        incepere=incep;
        incheiere=inch;
        functie=f;
        nrOre=no;
        cdId=cd;
        prId=p;
    }
    
    public String getDataIncepere(){
        return incepere;
    }
    
    public String getDataIncheiere(){
        return incheiere;
    }
    
    public String getFunctie(){
        return functie;
    }
    
    public String getNrOre(){
        return nrOre;
    }
    
    public String getCdId(){
        return cdId;
    }
    
    public String getProiectId(){
        return prId;
    }
}
