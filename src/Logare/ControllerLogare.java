package Logare;

import CreareCont.*;
import javax.swing.JOptionPane;
public class ControllerLogare {
    private FereastraLogare f;
    private Cont contCurent;
    public void cereLogare(){
        f=new FereastraLogare(this);
        f.afiseaza();
    }
    public void trimiteDate(String username,String parola){
       String rol=ManagerCont.verificaUsernameLogare(username,parola);
       Cont contCurent = new Cont(rol,username,parola);
      
       if(rol.equalsIgnoreCase("profesor")){
           this.contCurent = contCurent;
           f.dispose();
           FereastraProfesor f=new FereastraProfesor(contCurent);
           f.afiseaza();
                }
       else if(rol.equalsIgnoreCase("director")){
           this.contCurent = contCurent;
           f.dispose();
           FereastraDirector f=new FereastraDirector(contCurent);
           f.afiseaza();
       }
       else{
           JOptionPane.showMessageDialog(null, "Date incorecte!");
       }
    }
    
    public String getUserCurent(){
        return this.contCurent.getUsername();
    }
}
