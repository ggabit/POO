package ModificareParola;
import CreareCont.Cont;
import CreareCont.ManagerCont;
import java.io.*;
import javax.swing.JOptionPane;


public class ControllerModificareParola {
    private Cont contCurent;
    private FormularModificareParola f;
    
    public ControllerModificareParola(Cont c){
        this.contCurent=c;
    }
    
    public void cereModificareParola(){
       f=new FormularModificareParola(this);
       f.afiseaza();
    }
    
    public void trimiteDate(String p1, String p2)throws IOException{
        if(!p1.equals(p2)) 
            JOptionPane.showMessageDialog(null, "Parolele nu coincid!");
        else if (!p1.matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,}$")) 
            JOptionPane.showMessageDialog(null, "Parola trebuie să conțină cel putin o literă mare,o literă mică și o cifră (minim 8 caractere)!");
        else if(p1.equals(contCurent.getParola()))
            JOptionPane.showMessageDialog(null, "Noua parolă coincide cu vechea parolă!");
        else{
            int dialogButton = JOptionPane.YES_NO_OPTION;
            int dialogResult = JOptionPane.showConfirmDialog (null, "Confirmați schimbarea parolei?","Warning",dialogButton);
            if (dialogResult == 0) { //YES option
                if(ManagerCont.memoreazaParola(contCurent,p1)==true){
                    String s=contCurent.getInfo();
                    String[] itemi=s.split(" ");
                    contCurent = new Cont(itemi[0],itemi[1],p1);
                    System.out.println("S-a actualizat parola contului:"+contCurent.getSomeInfo());
                    JOptionPane.showMessageDialog(null, "S-a actualizat parola contului: "+contCurent.getSomeInfo());
                    f.dispose();
                }
            }
            else{ //NO option
                JOptionPane.showMessageDialog(null, "Renunțare la schimbarea parolei!");
                f.dispose();
            }
        }     
    }
    
    public void addCont(Cont c) 
        throws IOException {
          String str = c.getInfo();
          BufferedWriter writer = new BufferedWriter(new FileWriter("conturi", true));
          writer.append(' ');
          writer.append(str);

          writer.close();
        }
}
