
package GenerareFoaiePrezenta;

public class FoaiePrezenta {
    private String idLunaL;
    private int nrOreBaza;
    private int nrOrePlataOra;
    private int alteOre;
    
    public FoaiePrezenta(String l, int ob, int opo,int ao){
        idLunaL=l;
        nrOreBaza = ob;
        nrOrePlataOra = opo;
        alteOre = ao;
    }
    
    public int getNrOreBaza(){
        return nrOreBaza;
    }
    
    public int getNrOrePlataOra(){
        return nrOrePlataOra;
    }
    
    public int getAlteOre(){
        return alteOre;
    }
    
    public String getIdLunaL(){
        return idLunaL;
    }
}
