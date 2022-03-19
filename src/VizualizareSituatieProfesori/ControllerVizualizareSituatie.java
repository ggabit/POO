
package VizualizareSituatieProfesori;

import CreareCont.Cont;
import CreareCont.ManagerCont;
import GenerareFoaiePrezenta.ManagerFoiPrezenta;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.JOptionPane;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STJc;


public class ControllerVizualizareSituatie {
    private Cont contCurent;
    private FormularVizualizareSituatie fvs;
    private String luna,an;
    
    public void cereVizualizareSituatie(Cont c){
        this.contCurent=c;
        fvs=new FormularVizualizareSituatie(this,contCurent);
        fvs.afiseaza();
    } 
    
    public void trimiteDateVizualizare(String luna,String an){
        ManagerFoiPrezenta mf = ManagerFoiPrezenta.getInstance();
        ArrayList<String[]> listaCD= new ArrayList<String[]>(); // grad, nume, prenume, email
        if(verificaDateVizualizare(luna,an)){
            String deptId=ManagerCont.getDeptId(contCurent.getUsername());
            listaCD=mf.getSituatieCd(luna,an,deptId);
            if(listaCD.isEmpty()){
                JOptionPane.showMessageDialog(null, "Toți profesorii din departament și-au realizat foile de prezență pe luna "+getDenumireLuna(luna)+" "+an+".");
                fvs.afiseaza();
            }
            else{
            FereastraSituatie fs = new FereastraSituatie(this,listaCD,contCurent,getDenumireLuna(luna),an);
            fvs.setVisible(false);
            fs.afiseaza();
            }
        }
    }
    
    public boolean verificaDateVizualizare(String luna,String an){
        // completare campuri
        if(luna.equals("") || an.equals("")){
            JOptionPane.showMessageDialog(null, "Completați criteriile de căutare!");
            return false;
        }
        int nrLuna, nrAn;
        
        try{
            nrLuna = Integer.parseInt(luna);
        }catch (NumberFormatException e)
        {
           nrLuna = -1;
           JOptionPane.showMessageDialog(null, "Luna invalidă!");
           return false;
        }
        if(nrLuna>12 || nrLuna<1){
        JOptionPane.showMessageDialog(null, "Lună invalidă!");
        return false;
        }
        
        try{
          nrAn = Integer.parseInt(an);
        }catch (NumberFormatException e)
        {
           nrAn = -1;
           JOptionPane.showMessageDialog(null, "An invalid!");
           return false;
        }
        if(nrAn>Calendar.getInstance().get(Calendar.YEAR) || nrAn<Calendar.getInstance().get(Calendar.YEAR)-5){ 
            JOptionPane.showMessageDialog(null, "An invalid!");
            return false;
        }
        
        return true;
    }
    
    public void salveazaDoc(ArrayList<String[]> listaCd, String luna, String an){
        String info[] = ManagerCont.getCDInfo(contCurent.getCdId());
        String dept = info[1];
        String deptStr = dept.replaceAll("\\s", "");
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");  
        Date date = new Date();  
        String data=formatter.format(date);
        try{
            // Document gol
            XWPFDocument document = new XWPFDocument(); 
            
            // pun titlul
            XWPFParagraph titluPar = document.createParagraph();
            titluPar.setAlignment(ParagraphAlignment.CENTER);  
            XWPFRun titluRun = titluPar.createRun();
            titluRun.setText("Situația lunii "+luna+" "+an+" pentru departamentului de "+dept+", elaborată la data de "+data);
            titluRun.setFontSize(15);
            titluRun.setFontFamily("Times New Roman");
            titluRun.addBreak();
            titluRun.addBreak();
            titluRun.addBreak();
            titluRun.addBreak();
            
            XWPFParagraph par = document.createParagraph();
            par.setAlignment(ParagraphAlignment.LEFT);  
            XWPFRun parRun = par.createRun();
            parRun.setText("Lista profesorilor ce nu și-au generat foile de prezență:");
            parRun.setFontSize(11);
            parRun.setFontFamily("Times New Roman");
            parRun.addBreak();
            
             // creez tabelul
            XWPFTable table = document.createTable();
            
            // creez header-ul
            XWPFTableRow tableH = table.getRow(0);
            
            tableH.getCell(0).removeParagraph(0);
            tableH.getCell(0).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(500));
            XWPFRun run = tableH.getCell(0).addParagraph().createRun();
            setRun(run, "Times New Roman", 12, "000000", "#", true, false);
            tableH.getCell(0).getParagraphs().get(0).setAlignment(ParagraphAlignment.CENTER); 
            
            tableH.addNewTableCell();
            tableH.getCell(1).removeParagraph(0);
            tableH.getCell(1).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(1000));
            run = tableH.getCell(1).addParagraph().createRun();
            setRun(run, "Times New Roman", 12, "000000", "Grad", true, false);
            tableH.getCell(1).getParagraphs().get(0).setAlignment(ParagraphAlignment.CENTER); 
            
            tableH.addNewTableCell();
            tableH.getCell(2).removeParagraph(0);
            tableH.getCell(2).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(1500));
            run = tableH.getCell(2).addParagraph().createRun();
            setRun(run, "Times New Roman", 12, "000000", "Nume", true, false);
            tableH.getCell(2).getParagraphs().get(0).setAlignment(ParagraphAlignment.CENTER); 
            
            tableH.addNewTableCell();
            tableH.getCell(3).removeParagraph(0);
            tableH.getCell(3).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(1500));
            run = tableH.getCell(3).addParagraph().createRun();
            setRun(run, "Times New Roman", 12, "000000", "Prenume", true, false);
            tableH.getCell(3).getParagraphs().get(0).setAlignment(ParagraphAlignment.CENTER); 
            
            tableH.addNewTableCell();
            tableH.getCell(4).removeParagraph(0);
            tableH.getCell(4).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(3500));
            run = tableH.getCell(4).addParagraph().createRun();
            setRun(run, "Times New Roman", 12, "000000", "Email", true, false);
            tableH.getCell(4).getParagraphs().get(0).setAlignment(ParagraphAlignment.CENTER); 

            for(int i=0;i<listaCd.size();i++){
                XWPFTableRow row = table.createRow();
                XWPFTableCell cell0 = row.getCell(0);
                cell0.removeParagraph(0);
                XWPFRun run2 = cell0.addParagraph().createRun();
                setRun(run2, "Times New Roman", 11, "000000", String.valueOf(i+1), false, false);
                for(int j=0;j<4;j++){
                 XWPFTableCell cell = row.getCell(j+1);
                 cell.removeParagraph(0);
                 run2 = cell.addParagraph().createRun();
                 setRun(run2, "Times New Roman", 11, "000000", listaCd.get(i)[j], false, false);
                }
              
            }
            setTableAlign(table, ParagraphAlignment.CENTER);
            
            
            // Scriu documentul
            FileOutputStream fileout = new FileOutputStream( new File("Situatie"+deptStr+luna+an+".docx"));
            document.write(fileout);
            fileout.close();
            
        }catch(Exception ex){System.out.println(ex.getMessage());}
        JOptionPane.showMessageDialog(null, "Fișier salvat cu succes!");
    }
    
    private static void setRun (XWPFRun run , String fontFamily , int fontSize , String colorRGB , String text , boolean bold , boolean addBreak) {
        run.setFontFamily(fontFamily);
        run.setFontSize(fontSize);
        run.setColor(colorRGB);
        run.setText(text);
        run.setBold(bold);
        if (addBreak) run.addBreak();
    }
    
    public static void setTableAlign(XWPFTable table,ParagraphAlignment align) {
    CTTblPr tblPr = table.getCTTbl().getTblPr();
    CTJc jc = (tblPr.isSetJc() ? tblPr.getJc() : tblPr.addNewJc());
    STJc.Enum en = STJc.Enum.forInt(align.getValue());
    jc.setVal(en);
    }
    
    public void ferViz(boolean viz){
        fvs.setVisible(viz);
    }
    
    public String getAn(String anUniv,String luna){
        String an;
        String[] item = anUniv.split("-");
        if(Integer.parseInt(luna)<10){
            an=item[1];
        }
        else{
            an=item[0];
        }
        return an;
    }
    
    public String getDenumireLuna(String luna){
        String lunaDen="";
        if(luna.charAt(0)=='0') luna=luna.substring(1);
        switch (luna){
            case "1":
                lunaDen="Ianuarie";
                break;
            case "2":
                lunaDen="Februarie";
                break;
            case "3":
                lunaDen="Martie";
                break;
            case "4":
                lunaDen="Aprilie";
                break;
            case "5":
                lunaDen="Mai";
                break;
            case "6":
                lunaDen="Iunie";
                break;
            case "7":
                lunaDen="Iulie";
                break;
            case "8":
                lunaDen="August";
                break;
            case "9":
                lunaDen="Septembrie";
                break;
            case "10":
                lunaDen="Octombrie";
            case "11":
                lunaDen="Noiembrie";
                break;
            case "12":
                lunaDen="Decembrie";
                break;
        }
        return lunaDen;
    }
}
