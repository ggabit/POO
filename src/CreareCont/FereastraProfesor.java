package CreareCont;

import ActualizareFoaiePrezenta.ControllerActualizareFoi;
import GenerareFoaiePrezenta.ControllerGenerareFoi;
import ModificareParola.ControllerModificareParola;
import VizualizareFoaiePrezenta.ControllerVizualizareFoi;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class FereastraProfesor extends JFrame{
    private JButton b1,b2,b3,b4,b5;
    private Cont contCurent;
    private JPanel p1,p2;
    
    public FereastraProfesor(Cont cont){
        this.contCurent=cont;
        setTitle("Fereastra profesor");
        
        p1=new JPanel(new GridLayout(3,1,10,10));
        JLabel lbl = new JLabel("E-Prezență");
        lbl.setFont(new Font("Arial", Font.BOLD, 22));
        lbl.setHorizontalAlignment(JLabel.CENTER);
        p1.add(lbl);
        JLabel lbl2 = new JLabel("Sistem de gestiune a foilor de prezenţǎ de cǎtre cadrele didactice");
        lbl2.setFont(new Font("Arial", Font.ITALIC, 15));
        lbl2.setHorizontalAlignment(JLabel.CENTER);
        JLabel lbl3 = new JLabel("Bine ați venit, "+ManagerCont.getNume(contCurent.getUsername())+"!");
        lbl3.setFont(new Font("Arial", Font.PLAIN, 15));
        lbl3.setHorizontalAlignment(JLabel.CENTER);
        
        p1.add(lbl2);
        p1.add(lbl3);
        p1.setOpaque(false);
        
        p2=new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        b1=new JButton("Generare foaie prezență");
        b2=new JButton("Vizualizare foaie prezență");
        b3=new JButton("Modificare parolă");
        b4=new JButton("Logout");
        b5=new JButton("Actualizare foaie prezență");
        AscultatorButoane ab=new AscultatorButoane();
        b1.addActionListener(ab);
        b2.addActionListener(ab);
        b3.addActionListener(ab);
        b4.addActionListener(ab);
        b5.addActionListener(ab);
        p2.add(b3,gbc);
        gbc.gridy++;
        p2.add(b1,gbc);
        gbc.gridy++;
        p2.add(b2,gbc);
        gbc.gridy++;
        p2.add(b5,gbc);
        gbc.gridy++;
        p2.add(b4,gbc);
        this.add(p2);
        p2.setOpaque(false);
        
        this.add(p1, BorderLayout.NORTH);
        this.add(p2, BorderLayout.CENTER);
        this.getContentPane().setBackground(Color.decode("#bbd9f7"));
    }
    
    private class AscultatorButoane implements ActionListener{
        private ControllerModificareParola cmp;
        private ControllerGenerareFoi cgf;
        private ControllerVizualizareFoi cvf;
        private ControllerActualizareFoi caf;
        public AscultatorButoane(){
            //ccf = new ControllerCautareFoi();
            cmp = new ControllerModificareParola(contCurent);
            cgf = new ControllerGenerareFoi();
            cvf = new ControllerVizualizareFoi();
            caf = new ControllerActualizareFoi();
        }
        public void actionPerformed(ActionEvent e){
            if(e.getSource()==b4){
                FereastraProfesor.this.dispose();
                FereastraPrincipala f=new FereastraPrincipala();
                f.afiseaza();
            }
            else if(e.getSource()==b1)
            {
                cgf.cereGenerareFoaie(contCurent);
            }
            else if(e.getSource()==b3)
            {
                cmp.cereModificareParola();
            }
            else if(e.getSource()==b2)
            {
                cvf.cereVizualizareFoaie(contCurent);
            }
            else if(e.getSource()==b5)
            {
                caf.cereActualizareFoaie(contCurent);
            }
           
        }
    }
    
    public void afiseaza(){
       this.setSize(600,600);
       setVisible(true);
       setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
       setLocationRelativeTo(null);
   }
}
