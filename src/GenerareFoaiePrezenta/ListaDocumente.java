
package GenerareFoaiePrezenta;

import java.util.*;

public class ListaDocumente {
    private  List<Document> listaDoc;

    public ListaDocumente(String[] paths, String[] doc, String cdId) {
        this.listaDoc = new ArrayList<Document>();
        for(int i=0;i<paths.length;i++){
            String tip;
            switch (doc[i]) {
              case "Referatul de plată cu ora":
                tip="rpo";
                break;
              case "Foaia de pontaj individual":
                tip="fpi";
                break;
              case "Statul de funcțiuni":
                tip="sf";
                break;
              case "Calendarul academic":
                tip="ca";
                break;
              case "Foaia de pontaj proiect (opțională)":
                tip="fpp";
                break;
              default:
                tip="";
                break;
            }
            Document d = new Document(paths[i],tip, cdId);
            listaDoc.add(d);
        }
    }
    
    public List<Document> getLista(){
        return listaDoc;
    }
    
    @Override
    public String toString(){
        String s="";
        for(int i=0;i<listaDoc.size();i++){
            s+=listaDoc.get(i).getDocPath()+" "+listaDoc.get(i).getTipDoc()+", ";
        }
        return s;
    }
}
