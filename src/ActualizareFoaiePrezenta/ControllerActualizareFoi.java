
package ActualizareFoaiePrezenta;

import CreareCont.Cont;
import CreareCont.ManagerCont;
import GenerareFoaiePrezenta.ManagerFoiPrezenta;
import TrimitereFoaiePrezentaPrinEmail.ControllerTrimitereFoaie;
import VizualizareFoaiePrezenta.ControllerVizualizareFoi;
import java.io.*;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ControllerActualizareFoi {
    private Cont contCurent;
    private String an,luna,anUniv,nume,cdId;
    
    public void cereActualizareFoaie(Cont c){
        this.contCurent=c;
        
        ControllerVizualizareFoi cvf = new ControllerVizualizareFoi();
        cvf.cereVizualizareFoaie(this, contCurent);
    }
    
    public void alegeFoaie(String nume,String nrLuna,String anUniv, String numeSp){
        ArrayList<String[]> randuri = new ArrayList<String[]>();
        this.nume=numeSp;
        this.an=getAn(anUniv,nrLuna);
        this.anUniv=anUniv;
        this.luna=nrLuna;
        String[] item=numeSp.split(" ");
        String cdId= ManagerCont.getCdId(item[0], item[1]);
        this.cdId=cdId;
        String lunaDen=getDenumireLuna(nrLuna);
        String path=getPath(nume,lunaDen,an);
        
        randuri = getDateTabel(path);
        FereastraActualizareFoi faf = new FereastraActualizareFoi(this, contCurent, randuri);
        faf.afiseaza();
    }
    
    public ArrayList<String[]> getDateTabel(String path){
        ArrayList<String[]> randuri = new ArrayList<String[]>();
        ManagerFoiPrezenta mf = ManagerFoiPrezenta.getInstance();
        
        String idCA = mf.getCAId(anUniv);
        String idLunaC = mf.getLunaCalendId(luna,idCA);
        // get nrZile pt idLunaC
        int nrZile= Integer.parseInt(mf.getLunaNrZile(idLunaC));
        int n = 2+nrZile; // nr de coloane = 2+ nr de zile din luna
        
        // header
        String[] header = new String[n];
        header[0]=readCellDataXLSX(5, 0,path);
        header[1]=readCellDataXLSX(5, 1,path);
        for(int i=2;i<n;i++){
            header[i]=readCellDataXLSX(6, i,path);
        }
        randuri.add(header);
        
        // ore baza, plata ora, alte ore
        for(int i=7;i<10;i++){
            String[] rand = new String[n];
            for(int j=0;j<n;j++){
                rand[j]=readCellDataXLSX(i, j,path);
            }
            randuri.add(rand);
        }
        
        int m=8; // nr de linii
        String value = readCellDataXLSX(m+1, 0,path);
        while(!value.equals("")){  // daca am proiect in m+1
            m++;                  // m devine m+1
            value = readCellDataXLSX(m+1, 0,path);
        }
        
        // ore proiecte
        if(m>9)
            for(int i=10;i<m+1;i++){
                String[] rand = new String[n];
                for(int j=0;j<n;j++){
                    rand[j]=readCellDataXLSX(i, j,path);
                }
                randuri.add(rand);
            }
        
        int nrPr = mf.getNrProiecte();
        int k;
        if(m-9<nrPr){
            k=9+nrPr; // nr total de randuri
            // de la m la k randuri goale
            for(int i=m;i<k;i++){
                String[] rand = new String[n];
                for(int j=0;j<n;j++){
                    rand[j]="";
                }
                randuri.add(rand);
            }
            
        } // daca nu, atunci aveam ore la toate proiectele, nu am altele de adaugat
        return randuri;
    }
    
    public String readCellDataXLSX(int vRow, int vColumn, String path){  
        String value;         
        Workbook wb=null;   
        try{   
            FileInputStream fis=new FileInputStream(path);  
            wb=new XSSFWorkbook(fis);  
        }catch(FileNotFoundException e){e.printStackTrace();}  
        catch(IOException e1){e1.printStackTrace();}  
        Sheet sheet=wb.getSheetAt(0);    
        Row row=sheet.getRow(vRow);  
        Cell cell=row.getCell(vColumn);  
        DataFormatter df = new DataFormatter();
        value = df.formatCellValue(cell);
        //value=cell.getStringCellValue();  
        return value;           
    }
    
    public void trimiteDateGenerare(ArrayList<String[]> date){
        // verificaDate
        if(verificaDate(date)){
            genereazaFoaie(date);
            
        }
    }
    
    public boolean verificaDate(ArrayList<String[]> date){
        // exista proiectul?
        // proiecte am de la i=3 in jos
        ManagerFoiPrezenta mf = ManagerFoiPrezenta.getInstance();
        for(int i=3;i<date.size();i++){
            if(!mf.existaProiect(date.get(i)[0]) && !date.get(i)[0].equals("")){
                JOptionPane.showMessageDialog(null, "Nu există un proiect cu numele "+date.get(i)[0]+"!");
                return false;
            }
        }
        
        // sa nu introduca un proiect de doua ori
        ArrayList<String> numeP = new ArrayList<String>();
        for(int i=3;i<date.size();i++){
            if(numeP.contains(date.get(i)[0].toLowerCase())){
                JOptionPane.showMessageDialog(null, "Nu puteți introduce un proiect de mai multe ori!");
                return false;
            }
            // completare nume si functie
            if(!date.get(i)[0].equals("")) numeP.add(date.get(i)[0].toLowerCase());
            else{
                JOptionPane.showMessageDialog(null, "Introduceți numele proiectului!");
                return false;
            }
            if(date.get(i)[1].equals("")){
                JOptionPane.showMessageDialog(null, "Introduceți funcția din cadrul proiectului!");
                return false;
            }
        }
        
        // am introdus cel putin o ora
        for(int i=3;i<date.size();i++){
            boolean amOre=false;
            for(int j=2;j<date.get(0).length;j++){
                if(!date.get(i)[j].equals("") && !date.get(i)[j].equals(" ") && !date.get(i)[j].equals("0"))
                    amOre=true;
            }
            if(!amOre){
                JOptionPane.showMessageDialog(null, "Introduceți cel puțin o oră la proiectul "+date.get(i)[0]+"!");
                return false;
            }
        }
        
        // orele sunt cifre; 11 sau 12 ore pe zi?
        int sum=0;
        int oreP=0;
        int[] nrOreZi=new int[date.get(0).length-2];
        for(int j=2;j<date.get(0).length;j++){
            nrOreZi[j-2]=11;
            for(int i=3;i<date.size();i++){
                sum=0;
                if(!date.get(i)[j].equals("") && !date.get(i)[j].equals(" ")){
                    try{
                            oreP = Integer.parseInt(date.get(i)[j]);
                        }catch (NumberFormatException e)
                        {
                           JOptionPane.showMessageDialog(null, "Introduceți numărul de ore pozitiv și întreg!");
                           return false;
                        }
                    if(oreP<0){
                        JOptionPane.showMessageDialog(null, "Numărul de ore trebuie să fie întreg și pozitiv!");
                        return false;
                    }
                }
                else oreP = 0;
                if((mf.estePocu(date.get(i)[0]) || mf.estePosdru(date.get(i)[0]) && oreP>0))
                    nrOreZi[j-2]=12;
            }
        }
        
        // daca exista POCU sau nu => cate ore am voie pe zi
        for(int i=3;i<date.size();i++){
            for(int j=2;j<date.get(0).length;j++){
              if(!date.get(i)[j].equals("") && !date.get(i)[j].equals(" ")){
                  oreP = Integer.parseInt(date.get(i)[j]);
              }
              else oreP = 0;
              if(oreP>4 && mf.estePosdru(date.get(i)[0])){
              JOptionPane.showMessageDialog(null, "Nu puteți introduce mai mult de 4 ore pe zi pentru proiectul\"+date.get(i)[0]+\"!");
              return false;
              }
              else if(oreP>2 && !mf.estePosdru(date.get(i)[0])){
                  JOptionPane.showMessageDialog(null, "Nu puteți introduce mai mult de 2 ore pe zi pentru proiectul"+date.get(i)[0]+"!");
                  return false;
              }
            }
        }
        
        // sa nu depasesc maximul de ore
        for(int j=2;j<date.get(0).length;j++){
            sum=0;
            for(int i=0;i<date.size();i++){
                int ore=0;
                if(!date.get(i)[j].equals("") && !date.get(i)[j].equals(" ") && !date.get(i)[j].equals("X") && !date.get(i)[j].equals("C"))
                    try{
                            ore = Integer.parseInt(date.get(i)[j]);
                        }catch (NumberFormatException e){}
                sum+=ore;
            }
            if(sum>nrOreZi[j-2]){
                    JOptionPane.showMessageDialog(null, "Nu puteți introduce mai mult de "+nrOreZi[j-2]+" ore în ziua "+(j-1)+"!");
                    return false;
                }
        }
        return true;
    }
    
    public void genereazaFoaie(ArrayList<String[]> date){
        // scriu in xlsx
        int nrZile =date.get(0).length-2;
        String numeLuna = getDenumireLuna(luna);
        String info[] = ManagerCont.getCDInfo(cdId);
        String infoRows[] = {"Universitatea Ovidius din Constanta", "Facultatea: Matematica si Informatica", "Departamentul: "+info[1], "Numele și prenumele: "+info[0]};

        String path = createXLSX(infoRows, date, nrZile, numeLuna);
        
        // trimit email
        ControllerTrimitereFoaie ctf = new ControllerTrimitereFoaie();
        ctf.trimiteDateEmailVizualizare(cdId, contCurent, path, numeLuna,an);
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
    
    public String getPath(String nume, String lunaDen, String an){
        String path=nume+"\\FoaiePrezenta"+lunaDen+an+nume+".xlsx";
        return path;
    }
    
    public String createXLSX(String[] infoRows, ArrayList<String[]> date, int nrZile, String luna){
         String[] tabelRows = {"Activitatea din Norma de baza","Activitatea didactică - Plata cu ora","Alte activitati remunerate suplimentar"};
        
        Workbook workbook = new XSSFWorkbook(); 
        CreationHelper createHelper = workbook.getCreationHelper();

        Sheet sheet = workbook.createSheet("Prezenta");

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
        for(int i = 3; i < date.size(); i++) {
            Row row = sheet.createRow(i-3+10);
            Cell cell = row.createCell(0);
            cell.setCellValue(date.get(i)[0]);
            cell.setCellStyle(smallWrapBordCellStyle);
            row.createCell(1).setCellValue(date.get(i)[1]);
            row.getCell(1).setCellStyle(smallWrapBordCellStyle);
            for(int j = 0; j < nrZile; j++){
                row.createCell(j+2).setCellStyle(oreCellStyle);
                row.getCell(j+2).setCellType(CellType.NUMERIC);
            }
            row.createCell(nrZile+2).setCellStyle(largeWrapBordCellStyle);
        }
        
        // completare ore
        int[] tot=new int[nrZile];
        for(int i=0;i<nrZile;i++){
            ArrayList<String> ore= new ArrayList<String>();  // oreBaza, plataOre, alteOre pt fiecare zi
            ore.add(date.get(0)[i+2]);
            ore.add(date.get(1)[i+2]);
            ore.add(date.get(2)[i+2]);
            
            ArrayList<String> oreP=new ArrayList<String>();
            for(int j=3;j<date.size();j++){
                oreP.add(date.get(j)[i+2]);
            }
            
            tot[i]=0;
            for(int j=0;j<ore.size();j++){
                if(ore.get(j).equals("") || ore.get(j).equals(" ")) sheet.getRow(j+7).getCell(2+i).setCellValue(" ");
                else if(ore.get(j).equals("X") || ore.get(j).equals("C")) sheet.getRow(7).getCell(2+i).setCellValue(ore.get(0));
                else {
                    sheet.getRow(j+7).getCell(2+i).setCellValue(Double.parseDouble(ore.get(j)));
                    tot[i]+=Integer.parseInt(ore.get(j));
                }
            }
            
            for(int j=0;j<oreP.size();j++){
                
                if(oreP.get(j).equals("") || oreP.get(j).equals(" ")) sheet.getRow(j+10).getCell(2+i).setCellValue(" ");
                else {
                    sheet.getRow(j+10).getCell(2+i).setCellValue(Double.parseDouble(oreP.get(j))); // de aici am participarile proiect
                    tot[i]+=Integer.parseInt(oreP.get(j));
                } 
            }
        }
        
        // totalul pe zile
        sheet.createRow((date.size()-3)+10).createCell(1).setCellValue("Total");
        sheet.getRow((date.size()-3)+10).getCell(1).setCellStyle(largeWrapBordCellStyle);
        for(int i=0;i<nrZile+1;i++){
            sheet.getRow((date.size()-3)+10).createCell(i+2).setCellStyle(largeWrapBordCellStyle);
            if(i<nrZile) sheet.getRow((date.size()-3)+10).getCell(2+i).setCellValue(tot[i]);
        }
        
        // get totalul pe categorii
        int[] tot2 = new int[(date.size()-3)+3]; 
        int total=0;
        for(int i=0;i<(date.size()-3)+3;i++){
            tot2[i]=0;
            for(int j = 0; j < nrZile; j++){
                if(sheet.getRow(i+7).getCell(j+2).getCellType()==Cell.CELL_TYPE_NUMERIC)
                    tot2[i]+=(int) sheet.getRow(i+7).getCell(j+2).getNumericCellValue();
            }
        }
        
        // set totalul pe categorii
        for(int i=0;i<(date.size()-3)+3;i++){
            sheet.getRow(i+7).getCell(nrZile+2).setCellValue(tot2[i]);
            total+=tot2[i];
        }
        sheet.getRow((date.size()-3)+10).getCell(nrZile+2).setCellValue(total);
        
        sheet.createRow((date.size()-3)+12).createCell(20).setCellValue("Intocmit,");
        sheet.getRow((date.size()-3)+12).getCell(20).setCellStyle(titluCellStyle);
        
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
            // output
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
