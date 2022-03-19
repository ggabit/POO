
package GenerareFoaiePrezenta;

public class Document {
    private String path;
    private String tipDoc;
    private String extensie;
    private String cdId;
    
    public Document(String p, String t, String cdId){
        path=p;
        tipDoc=t;
        String[] item = path.split("\\.");
        this.extensie = item[item.length-1];
        this.cdId=cdId;
    }
    
    public String getDocPath(){
        return path;
    }
    
    public String getTipDoc(){
        return tipDoc;
    }
    
    public String getExtensie(){
        return extensie;
    }
    
    public String getCdId(){
        return cdId;
    }
}
