package GenerareCod;

import CreareCont.*;
import java.io.*;
import java.security.SecureRandom;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.swing.*;

public class ControllerGenerareCod {
    private FormularGenerareCod f;
    private Cont contCurent;
    
    public ControllerGenerareCod(Cont cont){
        this.contCurent=cont;
    }
    
    public void cereGenerareCod(){
       f=new FormularGenerareCod(this);
       f.afiseaza();
    }
    
    public void trimiteDate(String e){
        if(!e.equals(""))
        {
            if(ManagerCont.verificaEmail(e)){
                String dirId = ManagerCont.getCdId(contCurent.getUsername());
                String deptId=ManagerCont.getDeptId(contCurent.getUsername());
                if(ManagerCont.verificaDirUser(dirId,deptId,e)){
                    int dialogButton = JOptionPane.YES_NO_OPTION;
                    int dialogResult = JOptionPane.showConfirmDialog (null, "Confirmați generarea noului cod de acces pentru contul "+e+"?","Warning",dialogButton);
                    if (dialogResult == 0) { //YES option
                        String codAcces = genereazaCodAcces(10);
                        Cont c = new Cont("Profesor",e,codAcces);
                        if(ManagerCont.memoreazaParola(c,codAcces)==true){
                            System.out.println("S-a actualizat parola contului:"+c.getSomeInfo());
                            JOptionPane.showMessageDialog(null, "S-a actualizat parola contului: "+c.getSomeInfo());

                            f.dispose();
                            trimiteEmailUser(e,codAcces);
                            JOptionPane.showMessageDialog(null, "Noile date au fost trimise cu succes!");
                            f.dispose();
                        }
                    }else{ //NO option
                        JOptionPane.showMessageDialog(null, "Renunțare!");
                        f.dispose();
                    }
                } else JOptionPane.showMessageDialog(null, "Userul nu face parte din departamentul dumneavoastră!");
            } else JOptionPane.showMessageDialog(null, "Nu există un cont cu acest email!");
        } else JOptionPane.showMessageDialog(null, "Completați emailul!");
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
    
    public void trimiteEmailUser(String user, String parola){
      final String to = user;
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
          message.setSubject("Date autentificare E-Prezență");
          message.setText("A fost generat un nou cod de acces al contului dumneavoastră. Username: "+user+", parolă: "+parola+".");
          System.out.println("se trimite...");
          Transport.send(message);
          System.out.println("Mesaj trimis cu succes!");
      } catch (MessagingException mex) {
          mex.printStackTrace();
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
