package AnuntareParolaUitata;

import CreareCont.ManagerCont;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import javax.swing.JOptionPane;

public class ControllerParolaUitata {
    private FormularParolaUitata f;
    
    public ControllerParolaUitata(){
    }
    
    public void anuntaParolaUitata(){
       f=new FormularParolaUitata(this);
       f.afiseaza();
    }
    
    public void trimiteDate(String e){
        if(ManagerCont.verificaEmail(e)){
            int dialogButton = JOptionPane.YES_NO_OPTION;
            int dialogResult = JOptionPane.showConfirmDialog (null, "Confirmați înștiințarea directorului prin email?","Warning",dialogButton);
            if (dialogResult == 0) { //YES 
                String dirId = ManagerCont.getDirId(e);
                String emailDir = ManagerCont.getEmail(dirId);
                if(emailDir.equals(""))
                    trimiteEmailDir(e,e);
                else trimiteEmailDir(emailDir,e);
                JOptionPane.showMessageDialog(null, "Trimis cu succes!");
                f.dispose();
            }else{ //NO 
                JOptionPane.showMessageDialog(null, "Renunțare!");
                f.dispose();
            }
        } else JOptionPane.showMessageDialog(null, "Nu există un cont cu acest email!");
    }
    
    public void trimiteEmailDir(String director, String user){
      final String to = director;
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
          message.setSubject("Înștiințare parolă uitată");
          message.setText("Utilizatorul "+user+" solicită generarea unui nou cod de acces la cont.");
          System.out.println("sending...");
          Transport.send(message);
          System.out.println("Sent message successfully!");
      } catch (MessagingException mex) {
          mex.printStackTrace();
      }  
    }
}
