package CreareCont;
import ActualizareFoaiePrezenta.ControllerActualizareFoi;
import GenerareCod.ControllerGenerareCod;
import GenerareFoaiePrezenta.ControllerGenerareFoi;
import ModificareParola.ControllerModificareParola;
import VizualizareFoaiePrezenta.ControllerVizualizareFoi;
import VizualizareSituatieProfesori.ControllerVizualizareSituatie;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class FereastraDirector extends JFrame{
    private JButton b1,b2,b3,b4,b5,b6,b7,b8;
    private Cont contCurent;
    private JPanel p1,p2;
  
    public FereastraDirector(Cont cont){
        this.contCurent=cont;
        setTitle("Fereastra director");
        
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
        //lbl2.setForeground(Color.decode("#dbdbff"));
        p1.add(lbl2);
        p1.add(lbl3);
        p1.setOpaque(false);
        
        p2=new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        b1=new JButton("Creare cont");
        b2=new JButton("Generare foaie prezență");
        b3=new JButton("Vizualizare foaie prezență");
        b4=new JButton("Modificare parolă");
        b5=new JButton("Generare cod acces");
        b7=new JButton("Actualizare foaie prezență");
        b6=new JButton("Logout");
        b8=new JButton("Vizualizare situație");
        AscultatorButoane ab=new AscultatorButoane();
        b1.addActionListener(ab);
        b2.addActionListener(ab);
        b3.addActionListener(ab);
        b4.addActionListener(ab);
        b5.addActionListener(ab);
        b6.addActionListener(ab);
        b7.addActionListener(ab);
        b8.addActionListener(ab);
        p2.add(b1,gbc);
        gbc.gridy++;
        p2.add(b4,gbc);
        gbc.gridy++;
        p2.add(b5,gbc);
        gbc.gridy++;
        p2.add(b2,gbc);
        gbc.gridy++;
        p2.add(b3,gbc);
        gbc.gridy++;
        p2.add(b7,gbc);
        gbc.gridy++;
        p2.add(b8,gbc);
        gbc.gridy++;
        
        p2.add(b6,gbc);
        p2.setOpaque(false);
        
        this.add(p1, BorderLayout.NORTH);
        this.add(p2, BorderLayout.CENTER);
        this.getContentPane().setBackground(Color.decode("#bbd9f7"));
    }
    private class AscultatorButoane implements ActionListener{
        private JFrame f;
        private ControllerCreareCont cc;
        private ControllerModificareParola cmp;
        private ControllerGenerareCod cgc;
        private ControllerGenerareFoi cgf;
        private ControllerVizualizareFoi cvf;
        private ControllerVizualizareSituatie cvs;
        private ControllerActualizareFoi caf;
        public AscultatorButoane(){
            cc=new ControllerCreareCont();
            cmp = new ControllerModificareParola(contCurent);
            cgc = new ControllerGenerareCod(contCurent);
            cgf = new ControllerGenerareFoi();
            cvf = new ControllerVizualizareFoi();
            cvs = new ControllerVizualizareSituatie();
            caf = new ControllerActualizareFoi();
            
        }
        public void actionPerformed(ActionEvent e){
            if(e.getSource()==b1) cc.cereCreareCont(contCurent.getUsername());
            else if(e.getSource()==b6){
                FereastraDirector.this.dispose();
                FereastraPrincipala f=new FereastraPrincipala();
                f.afiseaza();
            }
            else if(e.getSource()==b5)
            {
               cgc.cereGenerareCod();
            }
            else if(e.getSource()==b2){
                cgf.cereGenerareFoaie(contCurent);
            }
            else if(e.getSource()==b4)
            {
                cmp.cereModificareParola();
            }
            else if(e.getSource()==b3)
            {
                cvf.cereVizualizareFoaie(contCurent);
            }
            else if(e.getSource()==b7)
            {
                caf.cereActualizareFoaie(contCurent);
            }
            else if(e.getSource()==b8)
            {
                cvs.cereVizualizareSituatie(contCurent);
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
