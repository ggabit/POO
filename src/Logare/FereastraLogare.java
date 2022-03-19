package Logare;

import AnuntareParolaUitata.ControllerParolaUitata;
import CreareCont.FereastraPrincipala;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class FereastraLogare extends JFrame{
    private JTextField tf1;
    private TextField tf2;
    private JButton b1,b2,b3;
    private ControllerLogare cl;
    private ControllerParolaUitata cpu;
    private  JPanel p1,p,p2;
    
    public FereastraLogare(ControllerLogare cl){
        setTitle("Logare");
        this.cl=cl;
        p=new JPanel(new GridBagLayout());
        p2=new JPanel(new GridLayout(2,1,10,10));
        JLabel lbl = new JLabel("E-Prezență");
        lbl.setFont(new Font("Arial", Font.BOLD, 22));
        lbl.setHorizontalAlignment(JLabel.CENTER);
        //lbl.setForeground(Color.decode("#dbdbff"));
        p2.add(lbl);
        JLabel lbl2 = new JLabel("Logare");
        lbl2.setFont(new Font("Arial", Font.ITALIC, 17));
        lbl2.setHorizontalAlignment(JLabel.CENTER);
        p2.add(lbl2);
        tf1=new JTextField(16);
        tf2=new TextField(22);
        tf2.setEchoChar('*');
        b1=new JButton("Ok");
        b2=new JButton("Cancel");
        b3=new JButton("Parolă uitată");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        p.add(new JLabel("Username"),gbc);
        gbc.gridy++;
        p.add(new JLabel("Parola"),gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridx++;
        p.add(tf1,gbc);
        gbc.gridy++;
        p.add(tf2,gbc);
        
        p1=new JPanel();
        p1.add(b1);
        p1.add(b2);
        p1.add(b3);
        p1.setOpaque(false);
        p.setOpaque(false);
        p2.setOpaque(false);
        AscultatorButoane ab=new AscultatorButoane();
        b1.addActionListener(ab);
        b2.addActionListener(ab);
        b3.addActionListener(ab);
        this.add(p2, BorderLayout.NORTH);
        this.add(p, BorderLayout.CENTER);
        this.add(p1, BorderLayout.SOUTH);
        this.getContentPane().setBackground(Color.decode("#bbd9f7"));
    }
    private class AscultatorButoane implements ActionListener{
        public void actionPerformed(ActionEvent e){
            if(e.getSource()==b1){
               cl.trimiteDate(tf1.getText(),tf2.getText());
            }else if(e.getSource()==b2){
                FereastraLogare.this.dispose();
                FereastraPrincipala f=new FereastraPrincipala();
                f.afiseaza();
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            }else{
                cpu = new ControllerParolaUitata();
                cpu.anuntaParolaUitata();
            }
        }
    }
    public void afiseaza(){
        this.setSize(600,300);
        this.setVisible(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }
}
