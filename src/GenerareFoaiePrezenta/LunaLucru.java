
package GenerareFoaiePrezenta;

public class LunaLucru {
    private String denumire;
    private String anUniv;
    private String numar;
    private String oreBazaLucrate;
    private String cdId;
    private int nrZile;
    
    public LunaLucru(String d, String n, String an, String cdId,int nrZile){
        denumire=d;
        numar=n;
        anUniv=an;
        this.cdId=cdId;
        this.nrZile=nrZile;
    }
    
    public String getDenumire(){
        return denumire;
    }
    
    public String getcdId(){
        return cdId;
    }
    
    public String getAn(){
        return anUniv;
    }
    
    public String getNumar(){
        return numar;
    }
    
    public int getNrZile(){
        return nrZile;
    }
    
    public void setOBL(int ob){
        oreBazaLucrate = String.valueOf(ob);
    }
    
    public String getOreBaza(){
        return oreBazaLucrate;
    }
}
