package VizualizareFoaiePrezenta;

import ActualizareFoaiePrezenta.ControllerActualizareFoi;
import CreareCont.Cont;
import CreareCont.ManagerCont;
import GenerareFoaiePrezenta.ManagerFoiPrezenta;
import java.awt.Desktop;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import javax.swing.JOptionPane;


public class ControllerVizualizareFoi {
    private Cont contCurent;
    private FormularCautareFoi fcf;
    private String luna,an;
    private ControllerActualizareFoi caf;
    
    public void cereVizualizareFoaie(Cont c){
        this.contCurent=c;
        fcf=new FormularCautareFoi(this,contCurent);
        if(contCurent.getRol().equalsIgnoreCase("director")){
            fcf.afiseazaDir();
        }
        else fcf.afiseazaProf();
    }
    
    public void cereVizualizareFoaie(ControllerActualizareFoi caf, Cont c){
        this.contCurent=c;
        this.caf=caf;
        fcf=new FormularCautareFoi(this,contCurent);
        fcf.afiseazaAct();
    }
    
    public void trimiteDateCautare(String nume,String luna,String an){
        ManagerFoiPrezenta mf = ManagerFoiPrezenta.getInstance();
        ArrayList<String[]> listaFoi= new ArrayList<String[]>();
        if(verificaDateCautare(nume,luna,an)){
            if(!nume.equals("")){
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
                        fcf.dispose();
                        if(!luna.equals("")&&!an.equals(""))  listaFoi=mf.cautaNumeLunaAn(nume,luna,an);
                        else if(!luna.equals("")) listaFoi=mf.cautaNumeLuna(nume,luna);
                        else if(!an.equals(""))  listaFoi=mf.cautaNumeAn(nume,an);
                        else listaFoi=mf.cautaNume(nume);
                    }
                }
                else JOptionPane.showMessageDialog(null, "Cadru didactic inexistent!");
            }
            else{
                if(!luna.equals("")&&!an.equals(""))  listaFoi=mf.cautaLunaAn(luna,an);
                else if(!luna.equals("")) listaFoi=mf.cautaLuna(luna);
                else  listaFoi=mf.cautaAn(an);
            }
        }
        if(listaFoi.isEmpty()){
            JOptionPane.showMessageDialog(null, "Nu există foi de prezență ce corespund căutării!");
            fcf.afiseazaProf(); // pentru ca doar afisez (am adaugat deja campul de nume in caz ca e director)
        }
        else{
        FereastraRezultateCautare frc = new FereastraRezultateCautare(this,listaFoi,contCurent);
        if(caf==null)
            frc.afiseaza();
        else frc.afiseazaAct(caf);
        fcf.setVisible(false);
        }
    }
    
    public boolean verificaDateCautare(String nume, String luna,String an){
        // completare campuri
        if(luna.equals("") && an.equals("") && nume.equals("")){
            JOptionPane.showMessageDialog(null, "Completați cel puțin un criteriu de căutare!");
            return false;
        }
        int nrLuna, nrAn;
        
        if(!luna.equals("")){
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
        }
        
        if(!an.equals("")){
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
        }
        
        if(!nume.equals("")){
            String[] item=nume.split(" ");
            //System.out.println("length: "+item.length);
            if(item.length<2){
                JOptionPane.showMessageDialog(null, "Introduceți și nume și prenume!");
                return false;
            }    
        }
        
        return true;
    }
    
    public void formC(boolean viz){
        fcf.setVisible(viz);
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
    
    public void vizualizeaza(String nume,String nrLuna,String anUniv){
        this.an=getAn(anUniv,nrLuna);
        this.luna=nrLuna;
        String lunaDen=getDenumireLuna(nrLuna);
        String path=getPath(nume,lunaDen,an);
        System.out.println("Path: "+path);
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
            else JOptionPane.showMessageDialog(null, "Documentul nu mai există!");
            }  
            catch(Exception e){e.printStackTrace();}
    }
}
