package GenerareFoaiePrezenta;

public class LunaCalend {
    private String anUniv;
    private String nume;
    private String nr;
    private int nrZile;
    private int oreFiz;
    private int oreLucr;
    
    public LunaCalend(String an, String num, String nr, int of, int ol, int nz ){
        anUniv=an;
        nume=num;
        this.nr=nr;
        oreFiz=of;
        oreLucr=ol;
        nrZile=nz;
    }
    
    public String getAnUniv(){
        return anUniv;
    }
    
    public String getNumeLuna(){
        return nume;
    }
    
    public String getNrLuna(){
        return nr;
    }
    
    public int getOreFizLuna(){
        return oreFiz;
    }
    
    public int getNrZile(){
        return nrZile;
    }
    
    public int getOreLucrLuna(){
        return oreLucr;
    }
}
