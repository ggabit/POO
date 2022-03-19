
package GenerareFoaiePrezenta;


public class ZiCalend {
    private String nr;
    private String nume;
    private String tip;
    private LunaCalend lc;
    
    public ZiCalend(String nr, String num, String tip, LunaCalend lc){
        this.nr=nr;
        nume=num;
        this.tip=tip;
        this.lc=lc;
    }
    
    public String getNrZiCal(){
        return nr;
    } 
    
    public String getNumeZiCal(){
        return nume;
    }
    
    public String getTipZiCal(){
        return tip;
    }
    
    public LunaCalend getLunaZiCal(){
        return lc;
    }
    
    public void setTipZiCal(){
        switch(this.tip){
            case "0:8080:0": //verde
                this.tip="a";
                break;
            case "FFFF:FFFF:0": //galben
                this.tip="a";
                break;
            case "9696:9696:9696": //mov
                this.tip="a";
                break;
            case "FFFF:CCCC:9999":  //rosu
                this.tip="l";
                break;
            case "0:CCCC:FFFF":  //albastru
                this.tip="c";
                break;
            default:
                this.tip="a";
                break;
        }
    }
}
