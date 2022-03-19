package CreareCont;

import java.io.*;
import java.security.SecureRandom;
import javax.swing.JOptionPane;
import org.apache.commons.validator.routines.*;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
//import javax.activation.*;

public class ControllerCreareCont {
    private FormularCreareCont form1;
    private FereastraCont form2;
    private String rol;
    private String director;
    
    public void cereCreareCont(String director){
       this.director=director;
       form1=new FormularCreareCont(this);
       form1.afiseaza();
    }
    
    public void trimiteCategorie(String rol){
        this.rol=rol;
    }
    
    public void trimiteDate(String nume,String prenume,String grad,String email,String idDept){
        if(verificareCompletareCampuri(nume,prenume,grad,email)){
          if(verificaSintacticEmail(email)){
              if(!ManagerCont.verificaEmail(email)){
                    CadruDidactic cd;
                    Persoana p;
                    if(rol.equals("Profesor")){
                        cd=new CadruDidactic(nume,prenume,grad,email,director);
                        p=new Persoana(nume,prenume);
                        ManagerCont.insertP(p);
                        ManagerCont.insertCdProf(cd,p);
                        String parola = genereazaCodAcces(10);
                        Cont c=new Cont(rol,email,parola,cd); 
                        form2 =new FereastraCont(c);
                        int dialogButton = JOptionPane.YES_NO_OPTION;
                            int dialogResult = JOptionPane.showConfirmDialog (null, "Confirmați crearea contului?","Warning",dialogButton);
                            if (dialogResult == 0) {
                                confirmaCreareCont(c);
                            }
                    } 
                    else{ //director
                        if(!ManagerCont.verificaExistentaDirector(idDept)){
                            cd=new CadruDidactic(nume,prenume,grad,email,director,idDept);
                            p=new Persoana(nume,prenume);
                            ManagerCont.insertP(p);
                            ManagerCont.insertCdDir(cd,p);
                            String parola = genereazaCodAcces(10);
                            Cont c=new Cont(rol,email,parola,cd); 
                            form2 =new FereastraCont(c);
                            int dialogButton = JOptionPane.YES_NO_OPTION;
                            int dialogResult = JOptionPane.showConfirmDialog (null, "Confirmați crearea contului?","Warning",dialogButton);
                            if (dialogResult == 0) {
                                confirmaCreareCont(c);
                            }
                        }
                         else JOptionPane.showMessageDialog(null, "Există deja un cont de director pentru acest departament!");
                    }
              }
              else JOptionPane.showMessageDialog(null, "Există deja un cont cu această adresă de email!");
          }
          else JOptionPane.showMessageDialog(null, "Introduceți o adresă de email validă!");
        }
        else JOptionPane.showMessageDialog(null, "Completați toate câmpurile!");
    }
    
    public void confirmaCreareCont(Cont c){
        if(ManagerCont.memoreaza(c)==true){
            //System.out.println("S-a memorat contul:"+c.getSomeInfo());
            JOptionPane.showMessageDialog(null, "S-a memorat contul: "+c.getSomeInfo());
            //addCont(c);  // scriere in fisier, de eliminat!!
            String s=c.getInfo();
            String[] item=s.split(" ");
            trimiteEmailUtilizatorului(item[1],item[2]);
            form2.afiseaza();
        }
        form1.dispose();
    }
    
    public boolean verificaSintacticEmail(String email){
        EmailValidator validator = EmailValidator.getInstance();

        if (validator.isValid(email)) 
            return true;
        return false;
    }
    
    public boolean verificareCompletareCampuri(String nume,String prenume,String grad,String email){
        if(nume.equals("") || prenume.equals("") || grad.equals("") || email.equals(""))
            return false;
        return true;
    }
    
    public String genereazaCodAcces(int len){
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
 
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
 
        for (int i = 0; i < len; i++) {
            int randomIndex = random.nextInt(chars.length());
            sb.append(chars.charAt(randomIndex));
        }
        return sb.toString();
    }
    
    public void addCont(Cont c) 
        throws IOException {
          String str = c.getInfo();
          BufferedWriter writer = new BufferedWriter(new FileWriter("conturi", true));
          writer.append(' ');
          writer.append(str);

          writer.close();
        }
    
    public void trimiteEmailUtilizatorului(String username, String parola){
      final String to = username;
      final String from = "gabitza_cuzic@yahoo.com";

      String host = "smtp.mail.yahoo.com";
      Properties properties = System.getProperties();

      properties.put("mail.smtp.host", host);
      properties.put("mail.smtp.port", "587");
      properties.put("mail.smtp.starttls.enable", "true");
      properties.put("mail.smtp.auth", "true");

      Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
          protected PasswordAuthentication getPasswordAuthentication() {
              return new PasswordAuthentication("gabitza_cuzic", "uywvplpbqekorsgq");
          }
      });

      //session.setDebug(true);
      try {
          MimeMessage message = new MimeMessage(session);

          message.setFrom(new InternetAddress(from));
          message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
          message.setSubject("Informații cont EPrezenta");
          message.setText("Contul dumneavoastră a fost creat. Username: "+username+", Parola: "+parola);
          System.out.println("sending...");
          Transport.send(message);
          System.out.println("Sent message successfully!");
      } catch (MessagingException mex) {
          mex.printStackTrace();
      }  
    }
}
