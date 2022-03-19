package TrimitereFoaiePrezentaPrinEmail;

import CreareCont.Cont;
import CreareCont.ManagerCont;
import VizualizareFoaiePrezenta.ControllerVizualizareFoi;
import java.util.Properties;
import javax.activation.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.swing.JOptionPane;

public class ControllerTrimitereFoaie {
    
    public void cereTrimitereFoaie(Cont contCurent, String nume, String luna, String anUniv){
        ControllerVizualizareFoi cvf =new ControllerVizualizareFoi(); 
        String lunaDen = cvf.getDenumireLuna(luna);
        String an =cvf.getAn(anUniv, luna);
        String[] item=nume.split(" ");
        String cdId= ManagerCont.getCdId(item[0], item[1]);
        nume=item[0]+item[1];
        
        String path=cvf.getPath(nume, lunaDen, an);
        trimiteDateEmailVizualizare(cdId,contCurent,path, lunaDen, an);
    }
    
    public void trimiteDateEmailGenerare(String cdId, Cont contCurent, String path){
        if(contCurent.getRol().equals("Profesor")){     // profesorul cere trimiterea
            String profE=ManagerCont.getEmail(cdId);
            String dirE=ManagerCont.getDirId(profE);
            dirE=ManagerCont.getEmail(dirE);
            trimiteEmailFoaieGenerare(path, profE, dirE);
        }
        else{                                           // dir cere trimiterea
            String profE=ManagerCont.getEmail(cdId);   
            String dirE=ManagerCont.getDirId(profE);
            dirE=ManagerCont.getEmail(dirE);
            if(dirE.equals(""))
                trimiteEmailFoaieGenerare(path,profE,profE);  // directorul isi trimite foaia sa
            else trimiteEmailFoaieGenerare(path,profE,dirE);   // directorul isi trimite foaia unui profesor
        }
    }
    
    public void trimiteDateEmailVizualizare(String cdId, Cont contCurent, String path,String lunaDen,String an){
        if(contCurent.getRol().equals("Profesor")){     // profesorul cere trimiterea
            String profE=ManagerCont.getEmail(cdId);
            String dirE=ManagerCont.getDirId(profE);
            dirE=ManagerCont.getEmail(dirE);
            trimiteEmailFoaieVizualizare(path, profE, dirE,lunaDen,an);
        }
        else{                                           // dir cere trimiterea
            String profE=ManagerCont.getEmail(cdId);   
            String dirE=ManagerCont.getDirId(profE);
            dirE=ManagerCont.getEmail(dirE);
            if(dirE.equals(""))
                trimiteEmailFoaieVizualizare(path,profE,profE,lunaDen,an);  // directorul isi trimite foaia sa
            else trimiteEmailFoaieVizualizare(path,profE,dirE,lunaDen,an);   // directorul isi trimite foaia unui profesor
        }
    }
    
    public void trimiteEmailFoaieVizualizare(String pathFoaie,String user, String director,String lunaDen,String an){
        // daca vrea sa trimit => YES/NO 
        int dialogButton = JOptionPane.YES_NO_OPTION;
        int dialogResult = JOptionPane.showConfirmDialog (null, "Doriți să trimiteți foaia de prezență prin email directorulul de departament?","Warning",dialogButton);
        if (dialogResult == 0) { // YES
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
                message.setSubject("Înștiințare foaie de prezență");
                 
                BodyPart messageBodyPart1 = new MimeBodyPart();  
                messageBodyPart1.setText("Foaia de prezență a utilizatorului "+user+" pentru luna "+lunaDen+" "+an+".");  
    
                MimeBodyPart messageBodyPart2 = new MimeBodyPart();  

                String filename = pathFoaie;
                DataSource source = new FileDataSource(filename);  
                messageBodyPart2.setDataHandler(new DataHandler(source));  
                messageBodyPart2.setFileName(filename);  
   
                Multipart multipart = new MimeMultipart();  
                multipart.addBodyPart(messageBodyPart1);  
                multipart.addBodyPart(messageBodyPart2);  
                
                message.setContent(multipart);  

                System.out.println("sending...");
                Transport.send(message);
                JOptionPane.showMessageDialog(null, "Foaie trimisă cu succes!");
            } catch (MessagingException mex) {mex.printStackTrace();} 
        }
    }
    
    public void trimiteEmailFoaieGenerare(String pathFoaie,String user, String director){
        int dialogButton = JOptionPane.YES_NO_OPTION;
        int dialogResult = JOptionPane.showConfirmDialog (null, "Doriți să trimiteți foaia de prezență prin email directorulul de departament?","Warning",dialogButton);
        if (dialogResult == 0) { // YES
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
                message.setSubject("Înștiințare generare foaie de prezență");
                 
                BodyPart messageBodyPart1 = new MimeBodyPart();  
                messageBodyPart1.setText("Există o nouă foaie de prezență generată pentru utilizatorul "+user+".");  
    
                MimeBodyPart messageBodyPart2 = new MimeBodyPart();  

                String filename = pathFoaie;
                DataSource source = new FileDataSource(filename);  
                messageBodyPart2.setDataHandler(new DataHandler(source));  
                messageBodyPart2.setFileName(filename);  
   
                Multipart multipart = new MimeMultipart();  
                multipart.addBodyPart(messageBodyPart1);  
                multipart.addBodyPart(messageBodyPart2);  
                
                message.setContent(multipart);  

                System.out.println("sending...");
                Transport.send(message);
                JOptionPane.showMessageDialog(null, "Foaie trimisă cu succes!");
            } catch (MessagingException mex) {mex.printStackTrace();} 
        }
    }
}
  
