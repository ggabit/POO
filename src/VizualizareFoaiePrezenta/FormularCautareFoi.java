
package VizualizareFoaiePrezenta;

import CreareCont.Cont;
import CreareCont.ManagerCont;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class FormularCautareFoi extends JFrame{
    private Cont contCurent;
    private ControllerVizualizareFoi cvf;
    private JButton b1,b2;
    private JTextField tf1,tf2,tf3;
    private  JPanel p,p1,p3;
    private GridBagConstraints gbc;
    
    public FormularCautareFoi(ControllerVizualizareFoi cvf, Cont c){
        this.contCurent=c;
        this.cvf=cvf;
        setTitle("Formular căutare foaie prezență");
        
        FormularCautareFoi.AscultatorButoane ab=new FormularCautareFoi.AscultatorButoane();
        p=new JPanel(new GridLayout(2,1,10,10));
        JLabel lbl = new JLabel("Vizualizare foaie de prezență");
        lbl.setFont(new Font("Arial", Font.BOLD, 22));
        lbl.setHorizontalAlignment(JLabel.CENTER);
        p.add(lbl);
        p.setOpaque(false);
        
        p1=new JPanel(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        tf1=new JTextField(13);
        tf2=new JTextField(13);
        p1.add(new JLabel("Luna"),gbc);
        gbc.gridx++;
        p1.add(tf1,gbc);
        gbc.gridx--;
        gbc.gridy++;
        p1.add(new JLabel("Anul"),gbc);
        gbc.gridx++;
        p1.add(tf2,gbc);
        gbc.gridx--;
        gbc.gridy++;
        p1.setOpaque(false);
        
        b1=new JButton("ok");
        b2=new JButton("cancel");
        p3=new JPanel();
        p3.add(b1);
        p3.add(b2);
        b1.addActionListener(ab);
        b2.addActionListener(ab);
        p3.setOpaque(false);
        
        this.add(p,BorderLayout.NORTH);
        this.add(p1,BorderLayout.CENTER);
        this.add(p3,BorderLayout.SOUTH);
        this.getContentPane().setBackground(Color.decode("#bbd9f7"));
    }
    // pentru profesori => luna (foile proprii)
    // pentru directori => luna si/sau numele : daca e din dept bun, daca exista, daca e complet
    
    public void afiseazaProf(){
        this.setSize(500,500);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
    }
    
    public void afiseazaDir(){
        tf3=new JTextField(13);
        p1.add(new JLabel("Nume și prenume profesor"),gbc);
        gbc.gridx++;
        p1.add(tf3,gbc);
        this.setSize(500,500);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
    }
    
    public void afiseazaAct(){
        p=new JPanel(new GridLayout(2,1,10,10));
        JLabel lbl = new JLabel("Actualizare foaie de prezență");
        lbl.setFont(new Font("Arial", Font.BOLD, 22));
        lbl.setHorizontalAlignment(JLabel.CENTER);
        p.add(lbl);
        JLabel lbl2 = new JLabel("Căutați o foaie:");
        lbl2.setFont(new Font("Arial", Font.BOLD,  15));
        lbl2.setHorizontalAlignment(JLabel.CENTER);
        p.add(lbl2);
        p.setOpaque(false);
        this.add(p,BorderLayout.NORTH);
        
        tf3=new JTextField(13);
        p1.add(new JLabel("Nume și prenume profesor"),gbc);
        gbc.gridx++;
        p1.add(tf3,gbc);
        this.setSize(500,500);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
    }
    
    private class AscultatorButoane implements ActionListener{
        public void actionPerformed(ActionEvent e){
            if(e.getSource()==b1){
                if(contCurent.getRol().equalsIgnoreCase("director")){
                    // nume din tf3
                    cvf.trimiteDateCautare(tf3.getText(),tf1.getText(),tf2.getText());
                }
                else{
                    //nume din contCurent
                    cvf.trimiteDateCautare(ManagerCont.getNume(contCurent.getUsername()),tf1.getText(),tf2.getText());
                }
            }
            else if(e.getSource()==b2){
                JOptionPane.showMessageDialog(null, "Renunțare vizualizare foaie de prezență!");
                FormularCautareFoi.this.dispose();
            }
        }
    }
}

