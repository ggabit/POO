package GenerareFoaiePrezenta;

public class ZiLucru {
    private String tip;
    private String nume;
    private String nr;
    private String oreBaza;
    private String restOreBaza;
    private String orePlataOra;
    private String alteOre;
    private String idLunaLucru;
    private String data;
    private String id;
    
    public ZiLucru(String nume, String nr, String tip, String idLL, String opo, String data, String ao){
        this.nume=nume;
        this.tip=tip;
        this.idLunaLucru=idLL;
        this.nr=nr;
        this.orePlataOra=opo;
        this.data=data;
        alteOre=ao;
    }
    
    public String getTip(){
        return tip;
    }
    
    public String getNume(){
        return nume;
    }
    
    public String getNr(){
        return nr;
    }
    
    public String getOrePlataOra(){
        return orePlataOra;
    }
    
    public String getAlteOre(){
        return alteOre;
    }
    
    public String getIdLunaL(){
        return idLunaLucru;
    }
    
    public String getData(){
        return data;
    }
    
    public String getOreBaza(){
        return this.oreBaza;
    }
    
    public String getId(){
        return this.id;
    }
    
    public String getRest(){
        return restOreBaza;
    }
    
    public void setOreBaza(String ob){
        // setez oreleBaza
        this.oreBaza=ob;
    }
    
    public void setRest(int restPrecedent){
        // setez restul = restCurent + restulPrecedent
        if(this.tip.equals("a"))
            this.restOreBaza=String.valueOf((8-Integer.parseInt(this.oreBaza))+restPrecedent);
        else this.restOreBaza=String.valueOf(restPrecedent);
    }
    
    public void setId(String id){
        // setez id-ul
        this.id=id;
    }
}
