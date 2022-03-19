package GenerareFoaiePrezenta;

import CreareCont.Cont;
import CreareCont.ManagerCont;
import TrimitereFoaiePrezentaPrinEmail.ControllerTrimitereFoaie;
import java.awt.Desktop;
import java.util.*;
import javax.swing.JOptionPane;
import java.io.*;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.text.*;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.hwpf.usermodel.*;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.util.*;
import org.apache.poi.xwpf.usermodel.*;

public class ControllerGenerareFoi {
    private FormularGenerareFoaie fgf;
    private FereastraIncarcareDoc fid;
    private Cont contCurent;
    private String luna,an,cdId,idLL;
    private Participare p;
    
    public void cereGenerareFoaie(Cont c){
        this.contCurent=c;
        fgf=new FormularGenerareFoaie(this,contCurent);
        if(contCurent.getRol().equalsIgnoreCase("director")){
            fgf.afiseazaDir();
        }
        else fgf.afiseazaProf();
    }
    
    public void trimiteDateGenerare(String nume,String luna,String an){
        if(verificaDate(nume,luna,an)){
            if(ManagerCont.verificaNume(nume)){
                boolean ok=true;
                if(contCurent.getRol().equalsIgnoreCase("director")){
                    String deptId=ManagerCont.getDeptId(contCurent.getUsername());
                    if(!ManagerCont.verificaDirUserNume(ManagerCont.getCdId(contCurent.getUsername()),deptId, nume)){
                        ok=false;
                        JOptionPane.showMessageDialog(null, "Cadrul didactic nu face parte din departamentul dumneavoastră!");
                    }
                }
                if(ok==true){
                    this.luna=luna;
                    this.an=an;
                    fgf.dispose();
                    // am trecut de verificari
                    ManagerCont mc = new ManagerCont();
                    String[] item=nume.split(" ");
                    String cdId= mc.getCdId(item[0], item[1]);
                    this.cdId=cdId;
                    ManagerFoiPrezenta mf = ManagerFoiPrezenta.getInstance();
                    // documente personale necesare lunar
                    String[] docLun = mf.docExLun(nume,luna,an).split(" ");
                    // calendar academic, universal necesar anual
                    Boolean ca = mf.verificaCA(Integer.parseInt(an),luna);
                    String documenteNec = docNec(docLun,ca);
                    fgf.dispose();
                    fid = new FereastraIncarcareDoc(this,contCurent,documenteNec,cdId);
                    fid.afiseaza();
                }
            }
            else JOptionPane.showMessageDialog(null, "Cadru didactic inexistent!");
        }
    }
    
    private boolean verificaDate(String nume,String luna,String an){
        // completare campuri
        if(luna.equals("") || an.equals("") || nume.equals("")){
            JOptionPane.showMessageDialog(null, "Completați toate câmpurile!");
            return false;
        }
        int nrLuna, nrAn;
        try{
            nrLuna = Integer.parseInt(luna);
        }catch (NumberFormatException e)
        {
           nrLuna = -1;
           JOptionPane.showMessageDialog(null, "Lună invalidă!");
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
        String[] item=nume.split(" ");
        //System.out.println("length: "+item.length);
        if(item.length<2){
            JOptionPane.showMessageDialog(null, "Introduceți nume și prenume!");
            return false;
        }
        
        // verific daca am generat deja foaie pt prof respectiv pt luna respectiva
        ManagerFoiPrezenta mf = ManagerFoiPrezenta.getInstance();
        if(mf.verificaFoaie(nume,luna,an)){
            JOptionPane.showMessageDialog(null, "A fost deja generată o foaie de prezență pentru acest cadru didactic în luna aleasă!");
            return false;
        }
        return true;
    }
    
    public String docNec(String[] docLunEx, boolean ca){
        String mesaj ="Foaia de pontaj proiect (opțională)";
        boolean rpo=false;
        boolean fpi=false;
        if(!ca) mesaj+=", Calendarul academic";
        for(int i=0;i<docLunEx.length;i++){
            switch (docLunEx[i]) {
              case "rpo":
                rpo=true;
                break;
              case "fpi":
                fpi=true;
                break;
              default:
                mesaj+=", Referatul de plată cu ora, Foaia de pontaj individual";
                return mesaj;
            }
        }
        if(!rpo) mesaj+=", Referatul de plată cu ora";
        if(!fpi) mesaj+=", Foaia de pontaj individual";
        return mesaj;
    }
    
    public void incarcaDoc(ListaDocumente ld){
        if(verificaDoc(ld)){
            int dialogButton = JOptionPane.YES_NO_OPTION;
            int dialogResult = JOptionPane.showConfirmDialog (null, "Confirmați generarea foii de prezență?","Warning",dialogButton);
            if (dialogResult == 0) { //YES
                // Memorez documentele cu ManagerFoiPrezenta
                ManagerFoiPrezenta mf = ManagerFoiPrezenta.getInstance();
                // fac un switch cu cate o metoda de memorare pt fiecare tip de document
                
                String anStr=this.an;
                if(Integer.parseInt(luna)<10){
                    anStr=(Integer.parseInt(an)-1)+"-"+an;
                }
                else{
                    anStr+="-"+(Integer.parseInt(an)+1);
                }
                for(int i=0;i<ld.getLista().size();i++){
                    String path = ld.getLista().get(i).getDocPath();
                    switch (ld.getLista().get(i).getTipDoc()) {
                        case "rpo":
                          String oreCurs =readDocFile(path,"Total ore curs");
                          String oreAplicatii =readDocFile(path,"Total ore aplicaţii (seminar, lucrări practice, proiecte)");
                          // memorez RPO
                          String idRPO=mf.memoreazaRPO(ld.getLista().get(i), oreCurs, oreAplicatii,anStr,luna);
                          
                          // citesc toate zilele lucrate
                          List<List<String>> tableList = tableInWord(path);
                          for(int j=0;j<tableList.size();j++){ // si le memorez
                            List<String> tableRow = tableList.get(j);
                            int nrOre=0;
                            if(tableRow.get(1).equals("")) tableRow.set(1, "0");
                            if(tableRow.get(2).equals("")) tableRow.set(2, "0");
                            try{
                                nrOre= Integer.parseInt(tableRow.get(1))+ Integer.parseInt(tableRow.get(2));
                            }catch(Exception e){}
                            String data[] = tableRow.get(0).split("\\.");
                            mf.memoreazaZiRPO(data[0],String.valueOf(nrOre),idRPO);
                          }
                          break;
                        case "fpi":
                          int nrOrePlataOra=(int) readCellNumDataXLS(33, 15, path);
                          int nrOreBaza=(int) readCellNumDataXLS(38, 22, path);
                          mf.memoreazaFPI(ld.getLista().get(i), nrOrePlataOra, nrOreBaza,anStr,luna);
                          break;
                        case "ca":
                          int an=(int) (readCellNumDataXLS(1, 13, path)-1);
                          String anUniv = (an-1)+"-"+an;
                          int totalOreFizice=(int) readCellNumDataXLS(35, 49, path);
                          int totalOreLucrate=(int) readCellNumDataXLS(37, 49, path);
                          
                          // memorez CA
                          String idCA = mf.memoreazaCA(ld.getLista().get(i), totalOreFizice, totalOreLucrate, anStr);
                          
                          // citesc toate zilele fiecarei luni
                          int[] totalZile = new int[12]; // nr de zile ale fiecarei luni
                          int k=0;
                          for(int j=1;j<46;j+=4){
                              // 33, 32 sau 30 liniile
                              if(xlsContineDate(33, j, path)) totalZile[k]=(int) readCellNumDataXLS(33, j, path);
                              else if(xlsContineDate(32, j, path)) totalZile[k]=(int) readCellNumDataXLS(32, j, path);
                              else if(xlsContineDate(30, j, path)) totalZile[k]=(int) readCellNumDataXLS(30, j, path);
                              k++;
                          }
                          String[] numar={"10","11","12","01","02","03","04","05","06","07","08","09"};
                          String[] denumire={"octombrie","noiembrie","decembrie","ianuarie","februarie","martie","aprilie","mai","iunie","iulie","august","septembrie"};
                          k=0;
                          LunaCalend[] luni = new LunaCalend[12]; //aici retin toate lunile
                          for(int j=1;j<46;j+=4){
                              luni[k] = new LunaCalend(anUniv, denumire[k], numar[k],(int) readCellNumDataXLS(35, j, path),(int) readCellNumDataXLS(37, j, path), totalZile[k]);
                              k++;
                          }
                          k=0;
                          for(int z=1;z<46;z+=4){
                              // memorez luna k
                              String idLC = mf.memoreazaLunaA(luni[k],idCA);
                              
                              ZiCalend[] zile = new ZiCalend[luni[k].getNrZile()]; //aici retin toate zilele lunii k
                              String[] tipZi = new String[luni[k].getNrZile()];
                              int row=3;
                              for(int j=0;j<luni[k].getNrZile();j++){
                                  if(readCellColorXLS(row, z+2, path).equals("0:0:0") || readCellColorXLS(row, z+2, path).equals("FFFF:FFFF:FFFF"))
                                    tipZi[j] = readCellColorXLS(row, z+3, path);
                                  else tipZi[j] = readCellColorXLS(row, z+2, path);
                                  zile[j] = new ZiCalend(String.valueOf((int) readCellNumDataXLS(row, z, path)),readCellStrDataXLS(row, z+2, path),tipZi[j],luni[k]);
                                  zile[j].setTipZiCal();
                                  //memorez ziua j a lunii k
                                  mf.memoreazaZiuaA(zile[j],idLC);
                                  row++;
                              }
                              k++;
                          }
                          break;
                        case "fpp": // implementare viitoara -> fpp
                          //int totalOre =readDocFile(path,"Total ore");
                          //String codP =readDocFileString(path,"Cod proiect");
                          //String idP =getProiectId(codP);
                          //mf.memoreazaFPPDoc(d, idP, totalOre, anStr, luna);
                          break;
                        default:
                          break;
                    }
                }
                // generez LunaLucru si o memorez
                LunaLucru ll=genereazaLunaLucru();
                
                // generez foaia si o memorez cu ManagerFoiPrezenta
                genereazaFoaie(ll);
            }else{
                JOptionPane.showMessageDialog(null, "Renunțare la generarea foii de prezență!");
                //fid.dispose();
            }
        }
    }
    
    public boolean verificaDoc(ListaDocumente ld){
        // verific daca sunt toate (doar fpp poate lipsi)
        String path;
        String tip;
        String extensie;
        
        for(int i=0;i<ld.getLista().size();i++){
            path=ld.getLista().get(i).getDocPath();
            tip = ld.getLista().get(i).getTipDoc();
            if(path.equalsIgnoreCase("gol") && !tip.equalsIgnoreCase("fpp")){
                JOptionPane.showMessageDialog(null, "Încărcați toate documentele necesare!");
                return false;
            }
            else if(path.equalsIgnoreCase("gol")) ld.getLista().remove(i);
        }
        
        // daca sunt toate, pt fiecare document in parte: 
        // verific extensia sa fie buna (sa se termine in .xls sau .docx)
        for(int i=0;i<ld.getLista().size();i++){
            path=ld.getLista().get(i).getDocPath();
            if(!path.equalsIgnoreCase("gol")){
                extensie = ld.getLista().get(0).getExtensie();
                if(!extensie.equals("xls") && !extensie.equals("xlsx") && !extensie.equals("docx") && !extensie.equals("doc")){
                    JOptionPane.showMessageDialog(null, "Încărcați doar documente ȋn format Ms Word sau Ms Excel!");
                    return false;
                }
            }
        }
        
        // verific daca pot extrage datele necesare
        for(int i=0;i<ld.getLista().size();i++){
            if(!verificaDate(ld.getLista().get(i))){
                String doc="";
                switch (ld.getLista().get(i).getTipDoc()) {
                    case "rpo":
                      doc="Referat de plată cu ora";
                      break;
                    case "fpi":
                      doc="Foaie de pontaj individual";
                      break;
                    case "ca":
                      doc="Calendar academic";
                      break;
                    default:
                      doc="Foaie de pontaj proiect";
                }
                JOptionPane.showMessageDialog(null, "Documentul "+doc+" nu conține datele necesare!");
                return false;
            }
        }
        
        for(int i=0;i<ld.getLista().size();i++){
            // verific daca e luna corespunzatoare
            if(!verificaLuna(ld.getLista().get(i))){
                return false;
            }
        }
        return true;
    }
    
    public boolean verificaLuna(Document d){
        String path=d.getDocPath();
        String tip = d.getTipDoc();
        String extensie = d.getExtensie();
        
        try{
            FileInputStream fis=new FileInputStream(new File(path)); 
            if(extensie.equals("xls")){
                // verific tipul de document, apoi iau output-ul necesar
                switch (d.getTipDoc()) {
                    case "fpi":
                        String data=readCellStrDataXLS(13, 0, path);   
                        String luna = data.split("\\.")[1];  
                        System.out.println("Luna fpi: "+luna);
                        if(!luna.equals(this.luna) && !luna.equals("0"+this.luna)){
                            JOptionPane.showMessageDialog(null, "Foaia de potaj idividual nu este pe luna "+this.luna+"!");
                            return false;
                        }
                      break;
                    case "ca":
                        double an2=readCellNumDataXLS(1, 13, path);
                        double an1=an2-1;
                        String anAc=String.valueOf(an1)+String.valueOf(an2);
                        System.out.println("An academic: "+anAc);
                        if(!anAc.equals(getAnUniversitar(this.an,this.luna))){
                            JOptionPane.showMessageDialog(null, "Calendarul academic nu este pe anul specificat!");
                            return false;
                        }
                      break;
                    default:
                      return false;
                }
            }
            else if(extensie.equals("xlsx")){
                    // verific tipul de document, apoi iau output-ul necesar
                     switch (d.getTipDoc()) {
                        case "fpi":
                          String data=readCellStrDataXLS(13, 0, path);   
                          String luna = data.split("\\.")[1];  
                          System.out.println("Luna fpi: "+luna);
                          if(!luna.equals(this.luna) && !luna.equals("0"+this.luna)){
                              JOptionPane.showMessageDialog(null, "Foaia de potaj idividual nu este pe luna "+this.luna+"!");
                              return false;
                          }
                          break;
                        case "ca":
                          double an2=readCellNumDataXLS(1, 13, path);
                          double an1=an2-1;
                          String anAc=String.valueOf(an1)+String.valueOf(an2);
                          System.out.println("An academic: "+anAc);
                          if(!anAc.equals(getAnUniversitar(this.an,this.luna))){
                              JOptionPane.showMessageDialog(null, "Calendarul academic nu este pe anul specificat!");
                              return false;
                          }
                          break;
                        default:
                          return false;
                    }
                }
                else if(extensie.equals("doc")){
                    switch (d.getTipDoc()) {
                        case "rpo":
                          String data =readDocFile(path,"Data");
                          String luna = data.split("\\.")[1];  
                          System.out.println("Luna rpo: "+luna);
                          if(!luna.equals(this.luna) && !luna.equals("0"+this.luna)){
                              JOptionPane.showMessageDialog(null, "Referatul de plată cu ora nu este pe luna "+this.luna+"!");
                              return false;
                          }
                          break;
                        default:
                          return false;
                    }
                }
                else if(extensie.equals("docx")){
                    switch (d.getTipDoc()) {
                        case "rpo":
                          String data =readDocxFile(path,"Data");
                          String luna = data.split("\\.")[1];  
                          System.out.println("Luna rpo: "+luna);
                          if(!luna.equals(this.luna) && !luna.equals("0"+this.luna)){
                              JOptionPane.showMessageDialog(null, "Referatul de plată cu ora nu este pe luna "+this.luna+"!");
                              return false;
                          }
                          break;
                        default:
                          return false;
                    }
                }
        }catch(Exception ex){return false;}
        
        return true;
    }
    
    public boolean verificaDate(Document d){
            String path=d.getDocPath();
            String tip = d.getTipDoc();
            String extensie = d.getExtensie();
            System.out.println(path);
            System.out.println(tip);
            try{
                FileInputStream fis=new FileInputStream(new File(path));  
                if(extensie.equals("xls")){
                    // verific tipul de document, apoi iau output-ul necesar
                    switch (d.getTipDoc()) {
                        case "fpi":
                          double nrOrePlataOra=readCellNumDataXLS(33, 15, path);   
                          //System.out.println("Nr ore plata cu ora: "+nrOrePlataOra);
                          double nrOreBaza=readCellNumDataXLS(38, 22, path);   
                          //System.out.println("Nr ore norma de baza: "+nrOreBaza);
                          break;
                        case "ca":
                          double anAcademic=readCellNumDataXLS(1, 13, path)-1;   
                          //System.out.println("An academic: "+anAcademic);
                          double totalOreFizice=readCellNumDataXLS(35, 49, path);   // totalul
                          //System.out.println("Nr ore fizice: "+totalOreFizice);   
                          double totalOreLucrate=readCellNumDataXLS(37, 49, path);   // efective
                          //System.out.println("Nr ore lucrate: "+totalOreLucrate);
                          for(int i=1;i<46;i+=4){
                              double oreFizLuna = readCellNumDataXLS(35, i, path); // ore fizice fiecare luna
                              double oreLucLuna = readCellNumDataXLS(37, i, path); // ore lucrate fiecare luna
                          }
                          break;
                        default:
                          return false;
                    }
                }
                else if(extensie.equals("xlsx")){
                    // verific tipul de document, apoi iau output-ul necesar
                     switch (d.getTipDoc()) {
                        case "fpi":
                          double nrOrePlataOra=readCellNumDataXLS(33, 15, path);   
                          System.out.println("Nr ore plata cu ora: "+nrOrePlataOra);
                          double nrOreBaza=readCellNumDataXLS(38, 22, path);   
                          System.out.println("Nr ore norma de baza: "+nrOreBaza);
                          break;
                        case "ca":
                          double anAcademic=readCellNumDataXLS(1, 13, path)-1;   
                          System.out.println("An academic: "+anAcademic);
                          double totalOreFizice=readCellNumDataXLS(35, 49, path);   // totalul
                          System.out.println("Nr ore fizice: "+totalOreFizice);   
                          double totalOreLucrate=readCellNumDataXLS(37, 49, path);   // efective
                          System.out.println("Nr ore lucrate: "+totalOreLucrate);
                          break;
                        default:
                          return false;
                    }
                }
                else if(extensie.equals("doc")){
                    switch (d.getTipDoc()) {
                        case "rpo":
                          String oreCurs =readDocFile(path,"Total ore curs");
                          String oreAplicatii =readDocFile(path,"Total ore aplicaţii (seminar, lucrări practice, proiecte)");  
                          System.out.println("Nr ore curs: "+oreCurs);  
                          System.out.println("Nr ore aplicatii: "+oreAplicatii);
                          break;
                        default:
                          return false;
                    }
                }
                else if(extensie.equals("docx")){
                    switch (d.getTipDoc()) {
                        case "rpo":
                          String oreCurs =readDocxFile(path,"Total ore curs");
                          String oreAplicatii =readDocxFile(path,"Total ore aplicaţii (seminar, lucrări practice, proiecte)");  
                          System.out.println("Nr ore curs: "+oreCurs);  
                          System.out.println("Nr ore aplicatii: "+oreAplicatii);
                          break;
                        default:
                          return false;
                    }
                }
            }catch(Exception ex){
                System.out.println(ex.getMessage());
                return false;
            }
        return true;
    }
    
    public double readCellDataXLSX(int vRow, int vColumn, String path){  
        double value; 
        Workbook wb=null; 
        try{   
            FileInputStream fis=new FileInputStream(path);  
            //construiesc un obiect XSSFWorkbook 
            wb=new XSSFWorkbook(fis);  
        }catch(FileNotFoundException e){e.printStackTrace();}  
        catch(IOException e1){e1.printStackTrace();}  
        Sheet sheet=wb.getSheetAt(0);   
        Row row=sheet.getRow(vRow);  
        Cell cell=row.getCell(vColumn);   
        value=cell.getNumericCellValue();      
        return value;                
    }  
    
    public double readCellNumDataXLS(int vRow, int vColumn, String path){  
        double value;           
        Workbook wb=null;           // Workbook null  
        try{   
            FileInputStream fis=new FileInputStream(path);  
            
            wb=new HSSFWorkbook(fis);  
        }catch(FileNotFoundException e){ e.printStackTrace();}
        catch(IOException e1) { e1.printStackTrace();}
        Sheet sheet=wb.getSheetAt(0);     
        Row row=sheet.getRow(vRow);   
        Cell cell=row.getCell(vColumn); 
        value=cell.getNumericCellValue();    
        return value;              
    }
    
    public String readCellStrDataXLS(int vRow, int vColumn, String path){  
        String value;          
        Workbook wb=null;        
        try{  
            FileInputStream fis=new FileInputStream(path);  
 
            wb=new HSSFWorkbook(fis);  
        }catch(FileNotFoundException e){ e.printStackTrace();}
        catch(IOException e1) { e1.printStackTrace();}
        Sheet sheet=wb.getSheetAt(0);     
        Row row=sheet.getRow(vRow);  
        Cell cell=row.getCell(vColumn); 
        value=cell.getStringCellValue();     
        return value;  
    }
    
    public String readCellColorXLS(int vRow, int vColumn, String path){  
        String value;        
        Workbook wb=null;     
        try{  
            FileInputStream fis=new FileInputStream(path);  
            
            wb=new HSSFWorkbook(fis);  
        }catch(FileNotFoundException e){ e.printStackTrace();}
        catch(IOException e1) { e1.printStackTrace();}
        Sheet sheet=wb.getSheetAt(0);    
        Row row=sheet.getRow(vRow); 
        Cell cell=row.getCell(vColumn); 
        Color fillColor = cell.getCellStyle().getFillForegroundColorColor();
        value=((HSSFColor) fillColor).getHexString();   
        return value;
    }
    
    public static String readDocxFile(String path, String mesaj){
        String nrOre="0";
        try{
            File file=new File(path); 
            FileInputStream fis=new FileInputStream(file); 
            XWPFDocument doc =new XWPFDocument(fis); 
            List<XWPFParagraph> getDocParagraphs= doc.getParagraphs(); // toate paragrafele
            int totalParagraphs=getDocParagraphs.size();               // numarul total de paragrafe

            System.out.println("Nr paragrafe: "+totalParagraphs);
            for (XWPFParagraph currentPara : getDocParagraphs){
                if(currentPara.getText().startsWith(mesaj)){
                    System.out.println(currentPara);
                    String[] itemi=currentPara.getText().split(": ");
                    nrOre = itemi[itemi.length-1];
                }
            }
            doc.close(); 
        }
        catch(Exception ex){System.out.println(ex.getMessage());}
        return nrOre;
    }
    
    // citesc din ".doc" 
    public static String readDocFile(String path, String mesaj){
        String nrOre="0";
        try{
            File file=new File(path);
            FileInputStream fis =new FileInputStream(file);
            HWPFDocument doc=new HWPFDocument(fis);

            WordExtractor extractor=new WordExtractor(doc);
            String[] getDocParagraphs= extractor.getParagraphText();
            int totalParagraphs=getDocParagraphs.length;
            //System.out.println("Nr paragrafe: "+totalParagraphs+"\n");
            for (String currentPara : getDocParagraphs){
                if(currentPara.startsWith(mesaj)){
                    //System.out.println(currentPara);
                    String[] itemi=currentPara.split(": ");
                    nrOre = itemi[itemi.length-1];
                }
            }
            extractor.close();
        }catch(Exception ex){System.out.println(ex.getMessage());}
        return nrOre;
    }
    
    public static List<List<String>> tableInWord(String filePath){
        try{
            FileInputStream in = new FileInputStream(filePath);
            if(filePath.toLowerCase().endsWith("docx")){
                XWPFDocument xwpf = new XWPFDocument(in);
                Iterator<XWPFTable> itpre = xwpf.getTablesIterator();
                int total = 0;
                while (itpre.hasNext()) {
                    itpre.next();
                    total += 1;
                }
                Iterator<XWPFTable> it = xwpf.getTablesIterator();
                int set = 1;
                int num = set;
                for (int i = 0; i < set-1; i++) {
                    it.hasNext();
                    it.next();
                }
                List<List<String>> tableList = new ArrayList<>();
                while(it.hasNext()){
                    XWPFTable table = it.next();
                    
                    List<XWPFTableRow> rows = table.getRows();
                    for (int i = 2; i < rows.size(); i++) {
                        XWPFTableRow row = rows.get(i);
                        List<XWPFTableCell> cells = row.getTableCells();
                        List<String> rowList = new ArrayList<>();
                        for (int j = 0; j < 3; j++) {
                            XWPFTableCell cell = cells.get(j);
                            rowList.add(cell.getText());
                            //System.out.print(cell.getText()+"["+i+","+j+"]" + "\t");
                        }
                        tableList.add(rowList);
                        //System.out.println();
                    }
                    while (num < total) {
                        it.hasNext();
                        it.next();
                        num += 1;
                    }
                }
                return tableList;
            }else{
                POIFSFileSystem pfs = new POIFSFileSystem(in);
                HWPFDocument hwpf = new HWPFDocument(pfs);
                Range range = hwpf.getRange();
                TableIterator itpre = new TableIterator(range);
                int total = 0;
                while (itpre.hasNext()) {
                    itpre.next();
                    total += 1;
                }
                TableIterator it = new TableIterator(range);
                int set = 1;
                int num = set;
                for (int i = 0; i < set-1; i++) {
                    it.hasNext();
                    it.next();
                }
                List<List<String>> tableList = new ArrayList<>();
                while (it.hasNext()) {
                    org.apache.poi.hwpf.usermodel.Table tb = (org.apache.poi.hwpf.usermodel.Table) it.next();

                    for (int i = 2; i < tb.numRows(); i++) {
                        List<String> rowList = new ArrayList<>();
                        TableRow tr = tb.getRow(i);
                        for (int j = 0; j < 3; j++) {
                            TableCell td = tr.getCell(j);

                            for(int k = 0; k < td.numParagraphs(); k++){
                                Paragraph para = td.getParagraph(k);
                                String s = para.text();     //scot simboluri speciale
                                if(null != s && !"".equals(s)){
                                    s = s.substring(0, s.length()-1);
                                }
                                rowList.add(s);
                                //System.out.print(s+"["+i+","+j+"]" + "\t");
                            }
                        }
                        tableList.add(rowList);
                        //System.out.println();
                    }
                    while (num < total) {
                        it.hasNext();
                        it.next();
                        num += 1;
                    }
                }
                return tableList;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean xlsContineDate(int vRow, int vColumn, String path){
        Workbook wb=null; 
        try{  
            FileInputStream fis=new FileInputStream(path);  
     
            wb=new HSSFWorkbook(fis);  
        }catch(FileNotFoundException e){ e.printStackTrace();}
        catch(IOException e1) { e1.printStackTrace();}
        Sheet sheet=wb.getSheetAt(0);   
        Row row=sheet.getRow(vRow);  
        Cell cell=row.getCell(vColumn); 
        if (cell == null || cell.getCellType() == Cell.CELL_TYPE_BLANK) {
           return false;
        }
        return true; 
    }
    
    public void trimiteDateParticipare(String incepere, String incheiere, String funct, String nrOreLuna, String pId, FormularParticipareProiect fpp){
        Participare p = new Participare(incepere, incheiere, funct, nrOreLuna, cdId, pId);
        this.p=p;
        if(verificaDateParticipare(p)){
            String nrOreSapt=String.valueOf(Integer.parseInt(nrOreLuna)/4);
            int dialogButton = JOptionPane.YES_NO_OPTION;
            int dialogResult = JOptionPane.showConfirmDialog (null, "Confirmați salvarea participării la proiect?","Warning",dialogButton);
            if (dialogResult == 0) { // doreste participarea => o salvez
                // aici pun codul
                ManagerFoiPrezenta mf = ManagerFoiPrezenta.getInstance();
                String partId=mf.memoreazaParticipare(p);
                if(!partId.equals("-1")){
                    JOptionPane.showMessageDialog(null, "Participare adăugată!");
                    fpp.dispose();
                }

                int dialogButton1 = JOptionPane.YES_NO_OPTION;
                int dialogResult1 = JOptionPane.showConfirmDialog (null, "Doriți să menționați programul participării la proiect?","Warning",dialogButton);
                if (dialogResult1 == 0) { //doreste programul
                    // afisez fereastra de program
                    FereastraProgramParticipare f = new FereastraProgramParticipare(this,partId,p);
                    f.afiseaza();
                }
                else{ // nu doreste program
                    // fac eu programul
                    genereazaProgram(p,partId);
                }
            }
            else{ // nu doreste participarea
                JOptionPane.showMessageDialog(null, "Renunțare la adăugare participare proiect!");
            }
        }
    }
    
    public void trimiteDateProgram(String[] ore, String partId,FereastraProgramParticipare fpp){
        ProgramParticipare pp = new ProgramParticipare(partId ,ore);
        // verificari
        if(verificaDateProgram(pp)){
            int dialogButton = JOptionPane.YES_NO_OPTION;
            int dialogResult = JOptionPane.showConfirmDialog (null, "Confirmați salvarea programului de lucru la proiect?","Warning",dialogButton);
            if (dialogResult == 0) { //YES
                // adaug programul
                ManagerFoiPrezenta mf = ManagerFoiPrezenta.getInstance();
                if(mf.memoreazaProgram(pp)){
                    JOptionPane.showMessageDialog(null, "Program adăugat!");
                    fpp.dispose();
                }
            }
            else{
                JOptionPane.showMessageDialog(null, "Renunțare la adăugare program participare proiect!");
                // il adaug eu
                genereazaProgram(p,partId);
            }
        }
    }
    
    public boolean verificaDateProgram(ProgramParticipare pp){
        int sumaOre=0;
        for(int i=0;i<5;i++){
            int nrOreZi=0;
            
            try{
                nrOreZi = Integer.parseInt(pp.getOre()[i]);
            }catch (NumberFormatException e)
            {
               JOptionPane.showMessageDialog(null, "Introduceți doar valori numerice și întregi!");
               return false;
            }
            if(nrOreZi<0){
                JOptionPane.showMessageDialog(null, "Introduceți doar valori pozitive!");
                return false;
            }
            if(nrOreZi>2){
                JOptionPane.showMessageDialog(null, "Introduceți mai puține ore!");
                return false;
            }
            sumaOre+=nrOreZi;
        }
        
        try{
            Date d1= new SimpleDateFormat("yyyy-MM-dd").parse(p.getDataIncepere());
            Date d2= new SimpleDateFormat("yyyy-MM-dd").parse(p.getDataIncheiere());
            long dif = d2.getTime() - d1.getTime();
            dif= TimeUnit.DAYS.convert(dif, TimeUnit.MILLISECONDS);
            int nrZile=(int) dif +1;
            int nrSapt=(int) nrZile/7;
            if((int) (nrZile%7) > 0) nrSapt+=1;
            if(sumaOre>Integer.parseInt(this.p.getNrOre())/nrSapt){
                JOptionPane.showMessageDialog(null, "Nu depășiți numărul săptămânal de ore!");
                return false;
            }
            else if(sumaOre<Integer.parseInt(this.p.getNrOre())/nrSapt){
                JOptionPane.showMessageDialog(null, "Completați numărul săptămânal de ore!");
                return false;
            }
        }catch(Exception ex){}
        
        return true;
    }
    
    public void genereazaProgram(Participare p, String partId){
        try{
            Date d1= new SimpleDateFormat("yyyy-MM-dd").parse(p.getDataIncepere());
            Date d2= new SimpleDateFormat("yyyy-MM-dd").parse(p.getDataIncheiere());
            long dif = d2.getTime() - d1.getTime();
            dif= TimeUnit.DAYS.convert(dif, TimeUnit.MILLISECONDS);
            int nrZile=(int) dif +1;
            int nrSapt=(int) nrZile/7;
            if((int) (nrZile%7) > 0) nrSapt+=1;
            
            int nrOre = Integer.parseInt(p.getNrOre())/nrSapt;
            String[] ore= new String[5];
            // verific pe cate zile se intinde participarea
            if(nrZile<5){ // daca <5
                if(nrOre<=nrZile){  // maxim o ora pe zi
                    for(int i=0;i<5;i++){
                        if(nrOre>0){
                            ore[i]="1";
                            nrOre--;
                        }
                        else ore[i]="0";
                    }
                }
                else{  // mai mult de o ora pe zi
                    int nrOreZi = nrOre/nrZile;
                    int rest = nrOre%nrZile;
                    // adaug orele impartite
                    for(int i=0;i<5;i++){
                        if(nrOre>0){
                            ore[i]=String.valueOf(nrOreZi);
                            nrOre-=nrOreZi;
                        }
                        else ore[i]="0";
                    }

                    // adaug si restul
                    for(int i=0;i<rest;i++){
                        ore[i]=String.valueOf(Integer.parseInt(ore[i])+1);
                    }
                }
                ProgramParticipare pp = new ProgramParticipare(partId, ore);
                // memorez programul
                ManagerFoiPrezenta mf = ManagerFoiPrezenta.getInstance();
                if(mf.memoreazaProgram(pp)){
                    System.out.println("Program adăugat!");
                }
            }
            else{// daca >5, le pun direct, dar verific dupa ce am generat participarile daca am acoperit totalul de ore, daca nu (e posibil ca ultima saptamana sa nu fie intreaga), mai adaug ore
                if(nrOre<=5){  // maxim o ora pe zi
                    for(int i=0;i<5;i++){
                        if(nrOre>0){
                            ore[i]="1";
                            nrOre--;
                        }
                        else ore[i]="0";
                    }
                }
                else{  // mai mult de o ora pe zi
                    int nrOreZi = nrOre/5;
                    int rest = nrOre%5;
                    // adaug orele impartite
                    for(int i=0;i<5;i++){
                        ore[i]=String.valueOf(nrOreZi);
                    }

                    // adaug si restul
                    for(int i=0;i<rest;i++){
                        ore[i]=String.valueOf(Integer.parseInt(ore[i])+1);
                    }
                }
                ProgramParticipare pp = new ProgramParticipare(partId, ore);
                // memorez programul
                ManagerFoiPrezenta mf = ManagerFoiPrezenta.getInstance();
                if(mf.memoreazaProgram(pp)){
                    System.out.println("Program adăugat!");
                }
            }
        }catch(Exception ex){ex.printStackTrace();}
    }
    
    public void trimiteDateActivitate(String data,String ore,FormularAlteActivitati faa){
        ManagerFoiPrezenta mf = ManagerFoiPrezenta.getInstance();
        if(verificaDateActivitate(data,ore)){
            // verific daca mai am activitate in ziua respectiva, specific cate ore am, intreb daca vrea sa actualizeze
            if(mf.verificaActivitate(data,cdId)>0){
                int dialogButton = JOptionPane.YES_NO_OPTION;
                int dialogResult;
                if(mf.verificaActivitate(data,cdId)==1) 
                    dialogResult = JOptionPane.showConfirmDialog (null, "La data specificată există activitate de o oră."
                        +" Doriti sa actualizați numărul de ore?","Warning",dialogButton);
                else dialogResult = JOptionPane.showConfirmDialog (null, "La data specificată există activitate de "+mf.verificaActivitate(data,cdId)+" ore."
                        +" Doriti sa actualizați numărul de ore?","Warning",dialogButton);
                if (dialogResult == 0) { // doreste actualizarea => o salvez
                    // aici pun codul
                    if(mf.updateActivitate(data,ore,cdId)){
                        JOptionPane.showMessageDialog(null, "Activitate actualizată!");
                        faa.dispose();
                    }
                    else{ // nu doreste participarea
                    JOptionPane.showMessageDialog(null, "Renunțare la actualizare activitate!");
                    }
                }
            }
            else{
                int dialogButton = JOptionPane.YES_NO_OPTION;
                int dialogResult = JOptionPane.showConfirmDialog (null, "Confirmați salvarea activității suplimentare?","Warning",dialogButton);
                if (dialogResult == 0) { // doreste activitatea => o salvez
                    // aici pun codul
                    String actId=mf.memoreazaActivitate(data,ore,cdId);
                    if(!actId.equals("-1")){
                        JOptionPane.showMessageDialog(null, "Activitate adăugată!");
                        faa.dispose();
                    }
                    else{ // nu doreste participarea
                    JOptionPane.showMessageDialog(null, "Renunțare la adăugare participare proiect!");
                    }
                }
            }
        }
    }
    
    public boolean verificaDateActivitate(String data, String ore){
        ManagerFoiPrezenta mf = ManagerFoiPrezenta.getInstance();
        // completare campuri
        if(data.equals("") || ore.equals("")){
            JOptionPane.showMessageDialog(null, "Completați toate câmpurile!");
            return false;
        }
        
        // obtin tipul zilei din CA
        String[] itemi=data.split("-");
        // verific sa nu fie zi libera
        String anStr=getAnUniversitar(itemi[0],itemi[1]);
        if(mf.getTipZiL(anStr,itemi[1],itemi[2]).equals("l")){
            JOptionPane.showMessageDialog(null, "Ziua selectată este liberă!");
            return false;
        }
        
        // numericitate
        int nrOre;
        try{
            nrOre = Integer.parseInt(ore);
        }catch (NumberFormatException e)
        {
           JOptionPane.showMessageDialog(null, "Numărul de ore trebuie să fie numeric și întreg!");
           return false;
        }
        if(nrOre<1){
            JOptionPane.showMessageDialog(null, "Adăugați cel puțin o oră!");
            return false;
        }
        if(nrOre>5){
            JOptionPane.showMessageDialog(null, "Adăugați maxim 4 ore!");
            return false;
        }
        return true;
    }
    
    public boolean verificaDateParticipare(Participare p){
        ManagerFoiPrezenta mf = ManagerFoiPrezenta.getInstance();
        // completare campuri
        if(p.getDataIncepere().equals("") || p.getDataIncheiere().equals("") || p.getFunctie().equals("") || p.getNrOre().equals("")){
            JOptionPane.showMessageDialog(null, "Completați toate câmpurile!");
            return false;
        }
        try{
            Date d1=new SimpleDateFormat("yyyy-MM-dd").parse(p.getDataIncepere()); 
            Date d2=new SimpleDateFormat("yyyy-MM-dd").parse(p.getDataIncheiere());
            
            if(d1.after(d2)){
                JOptionPane.showMessageDialog(null, "Introduceți datele în ordine cronologică!");
                return false;
            }
            
            long dif = d2.getTime() - d1.getTime();
            dif= TimeUnit.DAYS.convert(dif, TimeUnit.MILLISECONDS);
            int nrZile=(int) dif +1;
            int nrSapt=(int) nrZile/7;
            if((int) (nrZile%7) > 0) nrSapt+=1;
            int oreSapt = Integer.parseInt(p.getNrOre())/nrSapt;
            
            // in functie de tipul proiectului
            String tip = mf.getTipProiect(p.getProiectId());
            
            if(tip.equals("POSDRU") && oreSapt>20){
                JOptionPane.showMessageDialog(null, "Ați introdus prea multe ore în intervalul de participare pentru un proiect "+tip+"!");
                return false;
            }
            else if(!tip.equals("POSDRU") && oreSapt>10){
                JOptionPane.showMessageDialog(null, "Ați introdus prea multe ore în intervalul de participare pentru un proiect "+tip+"!");
                return false;
            }
        }catch(Exception ex){ex.printStackTrace();}
        
        int nrOre;
        try{
            nrOre = Integer.parseInt(p.getNrOre());
        }catch (NumberFormatException e)
        {
           JOptionPane.showMessageDialog(null, "Numărul de ore trebuie să fie numeric și întreg!");
           return false;
        }
        if(nrOre<1){
            JOptionPane.showMessageDialog(null, "Adăugați cel puțin o oră pe lună!");
            return false;
        }
        // exista deja participare la acelasi proiect a profului respectiv care incepe la data respectiva
        if(mf.verificaParticipare(p)){
            JOptionPane.showMessageDialog(null, "Există deja o participare la acest proiect a acestui cadru didactic cu aceeași funcție ce începe la data specificată!");
            return false;
        }
        return true;
    }
    
    public String getAnUniversitar(String an,String luna){
        String anStr=an;
        if(Integer.parseInt(luna)<10){
            anStr=(Integer.parseInt(an)-1)+"-"+an;
        }
        else{
            anStr+="-"+(Integer.parseInt(an)+1);
        }
        return anStr;
    }
    
    public LunaLucru genereazaLunaLucru(){
        ManagerFoiPrezenta mf = ManagerFoiPrezenta.getInstance();
        String anStr=getAnUniversitar(an,luna);
        // get idLunaC cu numar=luna
        String idCA = mf.getCAId(anStr);
        String idLunaC = mf.getLunaCalendId(luna,idCA);
        // get nrZile pt idLunaC
        int nrZile= Integer.parseInt(mf.getLunaNrZile(idLunaC));
        
        LunaLucru ll = new LunaLucru(mf.getLunaDenumire(luna),luna,anStr,cdId,nrZile);
        String idLL = mf.memoreazaLunaL(ll);
        this.idLL=idLL;

        // tin minte zilele de lucru
        ZiLucru zileL[]=new ZiLucru[nrZile];
        // calculez si totalul orelor de baza din luna
        int totalOreBaza=0;
        
        // intai memorez toate zilele
        for(int i=0;i<nrZile;i++){
            // get nume,tip where nrZi=i+1
            String nrZi=String.valueOf(i+1);
            String nume=mf.getNumeZi(idLunaC, nrZi);
            String tip=mf.getTipZi(idLunaC, nrZi);
            String nrOrePlataOra;
            
            // caut nrZi in zileRPO, daca nu o gasesc => nrOrePlataOra=""
            if(mf.getNrOreZiRPO(nrZi,luna,anStr, cdId).equals("-1")) nrOrePlataOra="0";
            else nrOrePlataOra=mf.getNrOreZiRPO(nrZi,luna,anStr, cdId);
            
            
            // construiesc data in formatul sql (an.luna.zi)
            String data = an+"-"+luna+"-"+nrZi;
            
            // obtin activitatile suplimentare
            String alteOre=mf.getAlteOreZi(data,cdId);
            
            // generez si memorez o zi de lucru => idZiL
            ZiLucru zl = new ZiLucru(nume, nrZi, tip, idLL, nrOrePlataOra, data, alteOre);
            zileL[i]=zl;
            String idZL = mf.memoreazaZiL(zileL[i]);
            zileL[i].setId(idZL);
        }
        
        // apoi parcurg zilele
        for(int i=0;i<nrZile;i++){
            // get nume,tip where nrZi=i+1
            String nrZi=String.valueOf(i+1);
            String nume=mf.getNumeZi(idLunaC, nrZi);
            String tip=mf.getTipZi(idLunaC, nrZi);
            
            // construiesc data in formatul sql (an.luna.zi)
            String data = an+"-"+luna+"-"+nrZi;

            // get participari in ziua respectiva pt cd-ul respectiv
            ArrayList<String> partIDs = mf.getPartZi(data,cdId);
            //System.out.println("Pariciparile sunt empty: "+partIDs.isEmpty());
            if(!partIDs.isEmpty())
                // parcurg participarile
                for(int j=0;j<partIDs.size();j++){
                    String nrOre="";
                    // daca e wend sau zi libera => setez direct nrOre="", daca e activitate didactica sau concediu e ok:
                    if(zileL[i].getTip().equals("l")) nrOre="";
                    else{
                        // fac un switch pt denumirea coloanei din ProgramParticipari in funtie de numele zilei
                        String oreZi="";
                        switch(zileL[i].getNume()){
                            case "luni":
                                oreZi="OreLu";
                                break;
                            case "marți":
                                oreZi="OreMa";
                                break;
                            case "miercuri":
                                oreZi="OreMi";
                                break;
                            case "joi":
                                oreZi="OreJoi";
                                break;
                            case "vineri":
                                oreZi="OreVi";
                                break;
                        }
                        nrOre=mf.getProgramZi(oreZi,partIDs.get(j));

                        // generez si memorez LucruProiect pt ziua respectiva x participarea respectiva
                        LucruProiect lp = new LucruProiect(zileL[i].getId(), partIDs.get(j), nrOre);
                        mf.memoreazaLP(lp);
                        //System.out.println("Am memorat lucrul la proiect cu nrOre: "+nrOre);
                        //System.out.println("IdParticipare: "+partIDs.get(j)+", nrOre: "+nrOre);
                        // acum am memorat toate orele lucrate la proiecte zilnic
                    }
                }
            // verifica daca exita LucruProiect cu tip POCU in ziua i (din ProiecteCercetare) => 11 sau 12 ore permise:
            int totalOre=11;
            if(mf.existaPOCU(zileL[i].getId())){
                totalOre=12;
            }
            int oreRamase=totalOre-Integer.parseInt(zileL[i].getOrePlataOra())-Integer.parseInt(zileL[i].getAlteOre());   // scad orelePlataOra
            
            // get lucruProiecte in ziua respectiva pt cd-ul respectiv
            ArrayList<String> lpIDs = mf.getLucruPZi(zileL[i].getId());
            
            // daca nu este zi libera, adica poate fi concediu sau activ. didactica
            if(!zileL[i].getTip().equals("l")){ // in ac.didact. si concedii
                // parcurg lucru proiecte
                if(!lpIDs.isEmpty())
                    for(int j=0;j<lpIDs.size();j++){
                        int oreP=Integer.parseInt(mf.getOreLP(lpIDs.get(j)));
                        if(oreRamase>=oreP){  // daca incap orele de proiect
                            oreRamase-=oreP;
                            oreP=0;
                        }
                        else{  // daca sunt prea multe ore de proiecte
                            // completez cat pot
                            oreP-=oreRamase;
                            oreRamase=0; // nu mai am loc
                            // transfer ce nu imi incape zilelor urmatoare
                            //System.out.println("La linia 954, pt part cu id: "+lpIDs.get(j)+", nrOre: "+oreRamase);
                            transferaLucruZi(zileL,i,lpIDs.get(j),oreP);
                            // scad ce nu s-a lucrat azi
                            mf.updateLucru(lpIDs.get(j),(-1*oreP));
                        }
                    }
            }
            else{ // in zilele libere
                // parcurg lucru proiecte
                if(!lpIDs.isEmpty())
                    for(int j=0;j<lpIDs.size();j++){
                        int oreP=Integer.parseInt(mf.getOreLP(lpIDs.get(j)));
                        transferaLucruZi(zileL,i,lpIDs.get(j),oreP);
                        // sterg lucrul din ziua respectiva
                        // mf.stergeLP(lpIDs.get(j))
                        //System.out.println("La linia 969, pt part cu id: "+lpIDs.get(j)+", nrOre: 0");
                        mf.updateLucru(lpIDs.get(j),(-1*oreP));
                        System.out.println("Am facut update cu "+(-1*oreP));
                    }
            }
            
            // caut restul zilei precedente si il transfer zilei curente
            int restPreced=0;
            if(i>0) restPreced=Integer.parseInt(zileL[i-1].getRest());
            
            // daca e wend, zi libera sau concediu => setez direct nrOrePlataOra="" si nrOreBaza="X", daca e activitate didactica e ok:
            //System.out.println("Ziua "+(i+1)+": restPrec="+restPreced);
            if(!tip.equalsIgnoreCase("a")){ // zi libera sau concediu
                // daca nu e tipul "a", transfer doar restul precedent, la oreBaza las "X"
                zileL[i].setOreBaza("0");
                zileL[i].setRest(restPreced);
                mf.updateZiL(zileL[i]);
            }
            else{ // act. didact.
                if(oreRamase<=8){  // setez cat pot din orele de baza si setez restul = restCurent + restulPrecedent
                    //System.out.println("Nu incap toate 8 orele de baza");
                    zileL[i].setOreBaza(String.valueOf(oreRamase));
                    zileL[i].setRest((8-oreRamase)+restPreced);
                    oreRamase=0;
                }
                else{  // daca pot pune toate cele 8 ore, nu am restCurent, dar pot avea restPrecedent
                    oreRamase-=8;
                    if(oreRamase<=restPreced){ //adaug cat pot din restul precedent
                        //System.out.println("Incap toate 8 orele de baza, incape si ceva din restul precedent");
                        restPreced-=oreRamase;
                        zileL[i].setOreBaza(String.valueOf(8+oreRamase));
                        oreRamase=0;
                    }
                    else{ // daca pot pune tot restul precedent
                        //System.out.println("Incap toate 8 orele de baza, incape tot restul precedent");
                        oreRamase-=restPreced;
                        zileL[i].setOreBaza(String.valueOf(8+restPreced));
                        restPreced=0;
                    }
                    zileL[i].setRest(restPreced);
                }
                mf.updateZiL(zileL[i]);
                totalOreBaza+=Integer.parseInt(zileL[i].getOreBaza());
            }
            //System.out.println("oreBaza="+zileL[i].getOreBaza()+", rest="+zileL[i].getRest());
        }
        
        // pentru ultima zi din luna verific daca am rest la nrOreBaza
        int i=nrZile-1;
        // iau nr de ore pe luna si vad daca e pus tot
        int oreLunaCA = mf.getOreLunaC(luna, anStr);
        int r= oreLunaCA-totalOreBaza;
        //System.out.println("Restul: "+r);
        // daca nu este, merg la ziua anterioara si scad din orele de proiecte, sau adaug la orele de baza
        while(r>0 && i>0){
            //System.out.println("Restul: "+r);
            //System.out.println("Ziua: "+i);
            // get lucruProiecte din ziua anterioara pt cd-ul respectiv
            if(zileL[i].getTip().equals("a")){
                ArrayList<String> lpIDs = mf.getLucruPZi(zileL[i].getId());
                if(!lpIDs.isEmpty()){
                    for(int j=0;j<lpIDs.size();j++){ // parcurg lucrul la proiecte din ziua i
                        int nrOre=Integer.parseInt(mf.getOreLP(lpIDs.get(j)));
                        if(nrOre>r){ // daca pot inlocui tot restul, si imi ramane si proiect
                            mf.updateLucru(lpIDs.get(j),(-1*r));
                            //System.out.println("Am scazut din ore: "+(-1*r));
                            zileL[i].setOreBaza(String.valueOf(Integer.parseInt(zileL[i].getOreBaza())+r));
                            mf.updateZiL(zileL[i]);
                            r=0;
                        }
                        else{ // daca nu incape tot restul, pun cat incape si trec la urm proiect !!! doar daca am mai mult de o ora de proiect!!
                            if(mf.existaPOCU(zileL[i].getId()) && nrOre>1){  // daca aveam 12 ore
                                mf.updateLucru(lpIDs.get(j),(-1*nrOre));
                                //System.out.println("Am scazut din ore: "+(-1*nrOre));
                                if(!mf.existaPOCU(zileL[i].getId())){      // acum am 11
                                    zileL[i].setOreBaza(String.valueOf(Integer.parseInt(zileL[i].getOreBaza())+nrOre-1));
                                    mf.updateZiL(zileL[i]);
                                    r-=nrOre-1;
                                }else{  // am tot 12
                                    zileL[i].setOreBaza(String.valueOf(Integer.parseInt(zileL[i].getOreBaza())+nrOre));
                                    mf.updateZiL(zileL[i]);
                                    r-=nrOre;
                                }
                            }
                            else if(!mf.existaPOCU(zileL[i].getId())){  // aveam 11
                                mf.updateLucru(lpIDs.get(j),(-1*nrOre));
                                //System.out.println("Am scazut din ore: "+(-1*nrOre));
                                zileL[i].setOreBaza(String.valueOf(Integer.parseInt(zileL[i].getOreBaza())+nrOre));
                                mf.updateZiL(zileL[i]);
                                r-=nrOre;
                            }
                        }
                        if(r==0) break;
                    }
                }
                else { // nu am proiecte
                    int oreLibere=11-mf.getTotalOreZi(zileL[i].getId());
                    //System.out.println("Total ore ziua "+(i+1)+": "+mf.getTotalOreZi(zileL[i].getId()));
                    //System.out.println("Ore disponibile ziua "+(i+1)+": "+oreLibere);
                    int nrOreBaza =Integer.parseInt(zileL[i].getOreBaza());
                    if(oreLibere>0){ // pot pune
                        if(oreLibere>r){ // incap toate
                            //System.out.println("Ore disponibile ziua "+(i+1)+": "+oreLibere);
                            //System.out.println("Ore rest ziua "+(i+1)+": "+r);
                            zileL[i].setOreBaza(String.valueOf(nrOreBaza+r));
                            mf.updateZiL(zileL[i]);
                            r=0;
                        }
                        else{  // pun cate pot
                            r-=oreLibere;
                            zileL[i].setOreBaza(String.valueOf(nrOreBaza+oreLibere));
                            mf.updateZiL(zileL[i]);
                        }
                    }
                }
            }
            i--;
        }
        ll.setOBL(oreLunaCA);
        
        mf.updateLunaL(ll,idLL);
        return ll;
    }
    
    public void transferaLucruZi(ZiLucru[] zileL,int i,String lpID,int oreP){
        ManagerFoiPrezenta mf = ManagerFoiPrezenta.getInstance();
        int k=i+1;
        String idPart = mf.getPartId(lpID);
        while(oreP>0 && k<zileL.length){ // pana aloc toate orele
            // daca ziua urmatoare nu e libera
            if(!zileL[k].getTip().equals("l")){
            /*int totalOre=11;
            if(mf.existaPOCU(zileL[k+1].getId())){
                totalOre=12;
            }
            int ocupate=mf.getTotalOreZi(zileL[k+1].getId());
            if(totalOre-ocupate>0){ // daca mai am loc */
                // verific daca am lucru la idPart si idZiLucru([k+1])
                if(!mf.getLucru(idPart,zileL[k+1].getId()).equals("-1")){
                    // daca da, fac update nrOre+1
                    String idL = mf.getLucru(idPart,zileL[k+1].getId());
                    mf.updateLucru(idL,1);
                }
                else{
                    // daca nu, fac insert cu lucru la proiectul respectiv cu nrOre=1
                    LucruProiect lp = new LucruProiect(zileL[k+1].getId(),idPart,"1");
                    mf.memoreazaLP(lp);
                }
                oreP--; // am alocat o ora
            }
            k++; // trec la ziua urmatoare
        }
    }
    
    public void genereazaFoaie(LunaLucru ll){
        ManagerFoiPrezenta mf = ManagerFoiPrezenta.getInstance();
        String anStr=getAnUniversitar(an,luna);
        // generez obiectul FoaiePrezenta si il memorez cu ManagerFoiPrezenta
        //System.out.println("OrepOra: "+mf.getOreBazaLunaL(luna,anStr,cdId));
        FoaiePrezenta fp = new FoaiePrezenta(idLL,mf.getOreBazaLunaL(luna,anStr,cdId),mf.getNrOrePlataOra(luna,anStr,cdId),mf.getAlteOre(idLL));
        mf.memoreazaFoaie(fp);
        
        // generez fisierul xlsx
        String info[] = ManagerCont.getCDInfo(cdId);
        String infoRows[] = {"Universitatea Ovidius din Constanta", "Facultatea: Matematica si Informatica", "Departamentul: "+info[1], "Numele și prenumele: "+info[0]};
        ArrayList<String> functii = mf.getFunctiiPr(idLL);
        ArrayList<String> prRows = mf.getParticipariPr(idLL);
        // sa iau din bd toate orele + orele de proiect
        String path=createXLSX(infoRows,prRows,ll.getNrZile(),ll.getDenumire(),an,functii,info[0]);
        
        // afisez foaia (deschid xlsx)
         try{
            File file = new File(path);   
            if(!Desktop.isDesktopSupported()){ 
                System.out.println("not supported");  
                return;  
            }  
            Desktop desktop = Desktop.getDesktop();  
            if(file.exists())  
            desktop.open(file);  
            }  
            catch(Exception e){e.printStackTrace();}  
         
        // o trimit prin email directorului
        ControllerTrimitereFoaie ctf = new ControllerTrimitereFoaie();
        ctf.trimiteDateEmailGenerare(cdId, contCurent, path);
        fid.dispose();
    }
    
    public String createXLSX(String[] infoRows, ArrayList<String> prRows, int nrZile, String luna, String an,ArrayList<String> functii,String nume){
         String[] tabelRows = {"Activitatea din Norma de baza","Activitatea didactică - Plata cu ora","Alte activitati remunerate suplimentar"};
        
        // Creez un Workbook
        Workbook workbook = new XSSFWorkbook(); 

        CreationHelper createHelper = workbook.getCreationHelper();

        // Creez un Sheet
        Sheet sheet = workbook.createSheet("Prezenta");

        // Creez fonturi
        Font infoFont = workbook.createFont();
        infoFont.setFontName("Times New Roman");
        infoFont.setFontHeightInPoints((short) 12);
        
        Font titluFont = workbook.createFont();
        titluFont.setFontName("Times New Roman");
        titluFont.setFontHeightInPoints((short) 15);
        
        Font largeBordFont = workbook.createFont();
        largeBordFont.setFontName("Times New Roman");
        largeBordFont.setFontHeightInPoints((short) 15);
        largeBordFont.setBold(true);
        
        Font smallBordFont = workbook.createFont();
        smallBordFont.setFontName("Times New Roman");
        smallBordFont.setFontHeightInPoints((short) 12);
        smallBordFont.setBold(true);

        // Creez CellStyle-uri cu fonturile
        CellStyle infoCellStyle = workbook.createCellStyle();
        infoCellStyle.setFont(infoFont);
        
        CellStyle titluCellStyle = workbook.createCellStyle();
        titluCellStyle.setFont(titluFont);
        
        CellStyle totalCellStyle = workbook.createCellStyle();
        totalCellStyle.setFont(largeBordFont);
        
        CellStyle bordCellStyle = workbook.createCellStyle();
        bordCellStyle.setBorderTop(BorderStyle.MEDIUM);
        bordCellStyle.setBorderBottom(BorderStyle.MEDIUM);
        bordCellStyle.setBorderLeft(BorderStyle.MEDIUM);
        bordCellStyle.setBorderRight(BorderStyle.MEDIUM);
        
        CellStyle wrapCellStyle = workbook.createCellStyle();
        wrapCellStyle.setFont(titluFont);
        wrapCellStyle.setWrapText(true);
        wrapCellStyle.setAlignment(HorizontalAlignment.CENTER);
        wrapCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        
        CellStyle smallWrapBordCellStyle = workbook.createCellStyle();
        smallWrapBordCellStyle.setFont(smallBordFont);
        smallWrapBordCellStyle.setWrapText(true);
        smallWrapBordCellStyle.setAlignment(HorizontalAlignment.CENTER);
        smallWrapBordCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        smallWrapBordCellStyle.setBorderTop(BorderStyle.MEDIUM);
        smallWrapBordCellStyle.setBorderBottom(BorderStyle.MEDIUM);
        smallWrapBordCellStyle.setBorderLeft(BorderStyle.MEDIUM);
        smallWrapBordCellStyle.setBorderRight(BorderStyle.MEDIUM);
        
        CellStyle largeWrapBordCellStyle = workbook.createCellStyle();
        largeWrapBordCellStyle.setFont(largeBordFont);
        largeWrapBordCellStyle.setWrapText(true);
        largeWrapBordCellStyle.setAlignment(HorizontalAlignment.CENTER);
        largeWrapBordCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        largeWrapBordCellStyle.setBorderTop(BorderStyle.MEDIUM);
        largeWrapBordCellStyle.setBorderBottom(BorderStyle.MEDIUM);
        largeWrapBordCellStyle.setBorderLeft(BorderStyle.MEDIUM);
        largeWrapBordCellStyle.setBorderRight(BorderStyle.MEDIUM);
        
        CellStyle zileCellStyle = workbook.createCellStyle();
        zileCellStyle.setBorderTop(BorderStyle.MEDIUM);
        zileCellStyle.setBorderBottom(BorderStyle.MEDIUM);
        zileCellStyle.setBorderLeft(BorderStyle.THIN);
        zileCellStyle.setBorderRight(BorderStyle.THIN);
        zileCellStyle.setAlignment(HorizontalAlignment.CENTER);
        zileCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        
        CellStyle oreCellStyle = workbook.createCellStyle();
        oreCellStyle.setBorderTop(BorderStyle.THIN);
        oreCellStyle.setBorderBottom(BorderStyle.THIN);
        oreCellStyle.setBorderLeft(BorderStyle.THIN);
        oreCellStyle.setBorderRight(BorderStyle.THIN);  
        oreCellStyle.setAlignment(HorizontalAlignment.CENTER);
        oreCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        
        // Merge pe celule
        CellRangeAddress cellRangeAddress1 = new CellRangeAddress(5, 6, 0, 0);
        sheet.addMergedRegion(cellRangeAddress1);
        CellRangeAddress cellRangeAddress2 = new CellRangeAddress(5, 6, 1, 1);
        sheet.addMergedRegion(cellRangeAddress2);

        Row row1 = sheet.createRow(5);
        Cell cell1 = CellUtil.createCell(row1, 0, "Activitatea/Proiect");
        cell1.setCellStyle(largeWrapBordCellStyle);
        Cell cell2 = row1.createCell(1);
        cell2.setCellValue("Post/Functia in proiect");
        cell2.setCellStyle(smallWrapBordCellStyle);
        
        // pun zilele
        Row row2 = sheet.createRow(6);
        row2.createCell(0).setCellStyle(smallWrapBordCellStyle);
        row2.createCell(1).setCellStyle(smallWrapBordCellStyle);
        int zi=1;
        for(int j = 0; j <nrZile; j++) {
            row2.createCell(j+2).setCellValue(zi++);
            row1.createCell(j+2).setCellStyle(zileCellStyle);
            row2.getCell(j+2).setCellStyle(zileCellStyle);
        }
        row1.createCell(nrZile+2).setCellStyle(bordCellStyle);
        
        CellRangeAddress cellRangeAddress3 = new CellRangeAddress(5, 5, 2, nrZile+2);
        sheet.addMergedRegion(cellRangeAddress3);
        Cell cellT = row2.createCell(nrZile+2);
        cellT.setCellValue("Total");
        cellT.setCellStyle(largeWrapBordCellStyle);
        
        // Headere
        for(int i = 0; i < tabelRows.length; i++) {
            Row row = sheet.createRow(i+7);
            Cell cell = row.createCell(0);
            cell.setCellValue(tabelRows[i]);
            row.createCell(1).setCellValue("");
            row.getCell(1).setCellStyle(smallWrapBordCellStyle);
            cell.setCellStyle(smallWrapBordCellStyle);
            for(int j = 0; j < nrZile; j++)
                row.createCell(j+2).setCellStyle(oreCellStyle);
            row.createCell(nrZile+2).setCellStyle(smallWrapBordCellStyle);
        }
        
        // Participari
        for(int i = 0; i < prRows.size(); i++) {
            Row row = sheet.createRow(i+10);
            Cell cell = row.createCell(0);
            cell.setCellValue(prRows.get(i).split("\\.")[0]);
            cell.setCellStyle(smallWrapBordCellStyle);
            row.createCell(1).setCellValue(functii.get(i));
            row.getCell(1).setCellStyle(smallWrapBordCellStyle);
            for(int j = 0; j < nrZile; j++){
                row.createCell(j+2).setCellStyle(oreCellStyle);
                row.getCell(j+2).setCellType(CellType.NUMERIC);
            }
            row.createCell(nrZile+2).setCellStyle(largeWrapBordCellStyle);
        }
        
        // completare ore
        ManagerFoiPrezenta mf = ManagerFoiPrezenta.getInstance();
        String anStr=getAnUniversitar(this.an,this.luna);
        int[] tot=new int[nrZile];
        for(int i=0;i<nrZile;i++){
            ArrayList<String> ore=mf.getOreZi(idLL,i+1);
            HashMap<String, String> oreP=new HashMap<String, String>();
            for(int j=0;j<prRows.size();j++){
                oreP.put(prRows.get(j), mf.getOrePZi(idLL,i+1,prRows.get(j).split("\\.")[0],functii.get(j)));
            }
            tot[i]=0;
            for(int j=0;j<ore.size();j++){
                if(ore.get(j).equals("0")) sheet.getRow(j+7).getCell(2+i).setCellValue(" ");
                else sheet.getRow(j+7).getCell(2+i).setCellValue(Double.parseDouble(ore.get(j)));
                tot[i]+=Integer.parseInt(ore.get(j));
            }
            if(ore.get(0).equals("0")){
                if(mf.getTipZiL(anStr,luna,String.valueOf(i+1)).equals("c"))
                    sheet.getRow(7).getCell(2+i).setCellValue("C");
                else
                    sheet.getRow(7).getCell(2+i).setCellValue("X");
            }
            for(int j=0;j<oreP.size();j++){
                if(oreP.get(prRows.get(j)).equals("0")) sheet.getRow(j+10).getCell(2+i).setCellValue(" ");
                else sheet.getRow(j+10).getCell(2+i).setCellValue(Double.parseDouble(oreP.get(prRows.get(j)))); // de aici am participarile proiect
                tot[i]+=Integer.parseInt(oreP.get(prRows.get(j)));
            }
        }
        
        // totalul pe zile
        sheet.createRow(prRows.size()+10).createCell(1).setCellValue("Total");
        sheet.getRow(prRows.size()+10).getCell(1).setCellStyle(largeWrapBordCellStyle);
        for(int i=0;i<nrZile+1;i++){
            sheet.getRow(prRows.size()+10).createCell(i+2).setCellStyle(largeWrapBordCellStyle);
            if(i<nrZile) sheet.getRow(prRows.size()+10).getCell(2+i).setCellValue(tot[i]);
        }
        
        // get totalul pe categorii
        int[] tot2 = new int[prRows.size()+3]; 
        int total=0;
        for(int i=0;i<prRows.size()+3;i++){
            tot2[i]=0;
            for(int j = 0; j < nrZile; j++){
                if(sheet.getRow(i+7).getCell(j+2).getCellType()==Cell.CELL_TYPE_NUMERIC)
                    tot2[i]+=(int) sheet.getRow(i+7).getCell(j+2).getNumericCellValue();
            }
        }
        
        // set totalul pe categorii
        for(int i=0;i<prRows.size()+3;i++){
            sheet.getRow(i+7).getCell(nrZile+2).setCellValue(tot2[i]);
            total+=tot2[i];
        }
        sheet.getRow(prRows.size()+10).getCell(nrZile+2).setCellValue(total);
        
        sheet.createRow(prRows.size()+12).createCell(20).setCellValue("Intocmit,");
        sheet.getRow(prRows.size()+12).getCell(20).setCellStyle(titluCellStyle);
        
        // titlu + anexa
        for(int i = 0; i < infoRows.length; i++) {
            Row infoRow = sheet.createRow(i);
            Cell cell = infoRow.createCell(0);
            cell.setCellValue(infoRows[i]);
            cell.setCellStyle(infoCellStyle);
            
            if(i==0){//Anexa 6
                Cell cellAnexa = infoRow.createCell(23); //X
                cellAnexa.setCellValue("Anexa 6");
                cellAnexa.setCellStyle(totalCellStyle);
            }
            if(i==2){//FOAIE INDIVIDUALĂ DE PREZENȚĂ
                Cell cellTitlu = infoRow.createCell(7); //H
                cellTitlu.setCellValue("FOAIE INDIVIDUALĂ DE PREZENȚĂ"); 
                cellTitlu.setCellStyle(titluCellStyle);
            }
            if(i==3){//PE LUNA Octombrie, 2020
                Cell cellTitlu = infoRow.createCell(10); //K
                cellTitlu.setCellValue("PE LUNA "+luna+", "+an); 
                cellTitlu.setCellStyle(titluCellStyle);
            }
        }
        
	// Resize
        sheet.autoSizeColumn(0);
        sheet.setColumnWidth(0, 11000);
        sheet.setColumnWidth(1, 3800);
        sheet.setDefaultRowHeight((short)500);
        for(int i=2;i<=nrZile+1;i++)
            sheet.setColumnWidth(i, 1200);
        //sheet.setColumnWidth(nrZile+2,3000);
        try{
            // scriu output in fisier
            String numeStr= nume.split(" ")[0]+nume.split(" ")[1];
            luna = luna.substring(0, 1).toUpperCase() + luna.substring(1);
            FileOutputStream fileOut = FileUtils.openOutputStream(new File(numeStr+"/FoaiePrezenta"+luna+an+numeStr+".xlsx"));
            //FileOutputStream fileOut = new FileOutputStream(numeStr+"/FoaiePrezenta"+luna+an+numeStr+".xlsx");
            workbook.write(fileOut);
            fileOut.close();
            // close
            workbook.close();
            return numeStr+"/FoaiePrezenta"+luna+an+numeStr+".xlsx";
        }catch(Exception ex){ex.printStackTrace();}
        return"";
    }
}
