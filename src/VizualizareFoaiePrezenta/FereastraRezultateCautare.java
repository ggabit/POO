
package VizualizareFoaiePrezenta;

import ActualizareFoaiePrezenta.ControllerActualizareFoi;
import CreareCont.Cont;
import TrimitereFoaiePrezentaPrinEmail.ControllerTrimitereFoaie;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class FereastraRezultateCautare extends JFrame{
    private ArrayList<String[]> listaFoi;
    private ControllerVizualizareFoi cvf;
    private ControllerActualizareFoi caf;
    private Cont contCurent;
    private JButton b1,b2,b3,b4;
    private JButton[][] b;
    private  JPanel p,p1,p2;
    private GridBagConstraints gbc;
    private int n;
    
    // o sa am la fiecare foaie optiuni: vizualizare, trimitere pe email, actualizare
    public FereastraRezultateCautare(ControllerVizualizareFoi cvf,ArrayList<String[]> listaFoi, Cont c){
        this.listaFoi=listaFoi;
        this.contCurent=c;
        this.cvf=cvf;
        setTitle("Fereastră rezultate căutare foaie prezență");
        this.n=listaFoi.size();
        b = new JButton[n][3];
        
        FereastraRezultateCautare.AscultatorButoane ab=new FereastraRezultateCautare.AscultatorButoane();
        p=new JPanel(new GridLayout(1,1,10,10));
        JLabel lbl = new JLabel("Rezultate căutare foaie prezență");
        lbl.setFont(new Font("Arial", Font.BOLD, 22));
        lbl.setHorizontalAlignment(JLabel.CENTER);
        p.add(lbl);
        p.setOpaque(false);
        
        p1=new JPanel(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        // Etichete deasupra pentru datele foilor
        p1.add(new JLabel("#"),gbc);
        gbc.gridx++;
        p1.add(new JLabel("Nume"),gbc);
        gbc.gridx++;
        p1.add(new JLabel("Prenume"),gbc);
        gbc.gridx++;
        p1.add(new JLabel("Lună"),gbc);
        gbc.gridx++;
        p1.add(new JLabel("An"),gbc);
        gbc.gridx++;
        p1.add(new JLabel("Ore plată cu ora"),gbc);
        gbc.gridx++;
        p1.add(new JLabel("Ore de bază"),gbc);
        gbc.gridx++;
        p1.add(new JLabel("Alte ore"),gbc);
        gbc.gridx++;
        gbc.gridx++;
        p1.add(new JLabel("Opțiuni"),gbc);
        gbc.gridx++;
        gbc.gridx=0;
        gbc.gridy++;
        for(int i=0;i<n;i++){ // datele foilor
            p1.add(new JLabel(String.valueOf(i+1)+"."),gbc);
            gbc.gridx++;
            for(int j=0;j<7;j++){
                p1.add(new JLabel(listaFoi.get(i)[j].toString()),gbc);
                gbc.gridx++;
            }
            b1=new JButton("vizualizare");
            b2=new JButton("trimitere pe email");
            b3=new JButton("actualizare");
            b[i][0] = b1;
            b[i][1] = b2;
            b[i][2] = b3;
            p1.add(b[i][0],gbc);
            gbc.gridx++;
            p1.add(b[i][1],gbc);
            gbc.gridx++;
            p1.add(b[i][2],gbc);
            gbc.gridx = 0;
            gbc.gridy++;
            b[i][0].addActionListener(ab);
            b[i][1].addActionListener(ab);
            b[i][2].addActionListener(ab);
        }
        p1.setOpaque(false);
        
        b4=new JButton("Renunțare");
        p2=new JPanel();
        p2.add(b4);
        b4.addActionListener(ab);
        p2.setOpaque(false);
        
        this.add(p,BorderLayout.NORTH);
        this.add(p1,BorderLayout.CENTER);
        this.add(p2,BorderLayout.SOUTH);
        this.getContentPane().setBackground(Color.decode("#bbd9f7"));
    }
    
    public void afiseaza(){
        this.setSize(1100,600);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
    }
    
    public void afiseazaAct(ControllerActualizareFoi caf){
        this.caf=caf;
        p=new JPanel(new GridLayout(2,1,10,10));
        JLabel lbl = new JLabel("Actualizare foaie de prezență");
        lbl.setFont(new Font("Arial", Font.BOLD, 22));
        lbl.setHorizontalAlignment(JLabel.CENTER);
        p.add(lbl);
        JLabel lbl2 = new JLabel("Selectați foaia pentru actualizare:");
        lbl2.setFont(new Font("Arial", Font.BOLD, 15));
        lbl2.setHorizontalAlignment(JLabel.CENTER);
        p.add(lbl2);
        p.setOpaque(false);
        this.add(p,BorderLayout.NORTH);
        
        this.setSize(1100,600);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
    }
    
    private class AscultatorButoane implements ActionListener{
        private ControllerTrimitereFoaie ctf;
        
        private AscultatorButoane(){
        ctf = new ControllerTrimitereFoaie();
        }
        
        public void actionPerformed(ActionEvent e){
            for(int i=0;i<n;i++){
                if(e.getSource()==b[i][0]){
                    // vizualizare: nume+prenume, luna, anUniv
                    cvf.vizualizeaza(listaFoi.get(i)[0]+listaFoi.get(i)[1],listaFoi.get(i)[2],listaFoi.get(i)[3]);
                }
                else if(e.getSource()==b[i][1]){
                    // email: nume+prenume, luna, anUniv
                    
                    ctf.cereTrimitereFoaie(contCurent,listaFoi.get(i)[0]+" "+listaFoi.get(i)[1],listaFoi.get(i)[2],listaFoi.get(i)[3]);
                }
                else if(e.getSource()==b[i][2]){
                    // actualizare
                    if(caf==null){
                        caf = new ControllerActualizareFoi();
                        caf.alegeFoaie(listaFoi.get(i)[0]+listaFoi.get(i)[1],listaFoi.get(i)[2],listaFoi.get(i)[3], listaFoi.get(i)[0]+" "+listaFoi.get(i)[1]);
                    }
                    else{
                        caf.alegeFoaie(listaFoi.get(i)[0]+listaFoi.get(i)[1],listaFoi.get(i)[2],listaFoi.get(i)[3], listaFoi.get(i)[0]+" "+listaFoi.get(i)[1]);
                    }
                }
            }
            if(e.getSource()==b4){
                    // renuntare
                    FereastraRezultateCautare.this.dispose();
                    cvf.formC(true);
                }
        }
    }
    
}
