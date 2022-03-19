package CreareCont;
import Logare.ControllerLogare;
import java.awt.*;

import javax.swing.*;
import java.awt.event.*;

public class FereastraPrincipala extends JFrame{
    private JButton b1;

    public FereastraPrincipala(){
        setTitle("E-Prezenta");

        b1=new JButton("Logare");
        JPanel p1=new JPanel();
        JLabel lbl = new JLabel("E-Prezență");
        lbl.setFont(new Font("Arial", Font.BOLD, 22));
        lbl.setHorizontalAlignment(JLabel.CENTER);
        
        p1.add(lbl);
        JLabel lbl2 = new JLabel("Sistem de gestiune a foilor de prezenţǎ de cǎtre cadrele didactice");
        lbl2.setFont(new Font("Arial", Font.ITALIC, 15));
        lbl2.setHorizontalAlignment(JLabel.CENTER);
        
        p1.add(lbl2);
        p1.setLayout(new GridLayout(2,1,10,10));
        JPanel p2=new JPanel();
        p2.add(b1);

        p1.setOpaque(false);
        p2.setOpaque(false);
        AscultatorButoane ab=new AscultatorButoane();
        b1.addActionListener(ab);
        b1.setBackground(Color.decode("#9999ff"));

        this.add(p1,BorderLayout.NORTH);
        this.add(p2, BorderLayout.SOUTH);
        
        p1.setOpaque(false);
        p2.setOpaque(false);

        this.getContentPane().setBackground(Color.decode("#bbd9f7"));
        //#ccccff
    }
    private class AscultatorButoane implements ActionListener{
        private ControllerLogare cl;
        public AscultatorButoane(){
             cl=new ControllerLogare();
        }

        public void actionPerformed(ActionEvent e){
            if(e.getSource()==b1){
                cl.cereLogare();
                FereastraPrincipala.this.dispose();
            }
        }
    }

    public void afiseaza(){
        this.setSize(600,400);
        setVisible(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }
}
