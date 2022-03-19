package AnuntareParolaUitata;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class FormularParolaUitata extends JFrame{
    private JTextField tf1;
    private JButton b1,b2;
    private ControllerParolaUitata cpu;
    private JPanel p1,p2,p3;
    
    public FormularParolaUitata(ControllerParolaUitata cpu){
        this.cpu=cpu;
        setTitle("Parolă uitată");
        
        p1=new JPanel(new GridLayout(1,1,10,10));
        JLabel lbl = new JLabel("Anunțare parolă uitată");
        lbl.setFont(new Font("Arial", Font.BOLD, 22));
        lbl.setHorizontalAlignment(JLabel.CENTER);
        p1.add(lbl);
        p1.setOpaque(false);
        
        p2=new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        
        tf1=new JTextField(20);
        p2.add(new JLabel("Introduceți adresa de email:"),gbc);
        gbc.gridy++;
        p2.add(tf1,gbc);
        p2.setOpaque(false);
        
        FormularParolaUitata.AscultatorButoane ab=new FormularParolaUitata.AscultatorButoane();
        b1=new JButton("Ok");
        b2=new JButton("Cancel");
        p3=new JPanel();
        p3.add(b1);
        p3.add(b2);
        p3.setOpaque(false);
        b1.addActionListener(ab);
        b2.addActionListener(ab);
        
        this.add(p1,BorderLayout.NORTH);
        this.add(p2,BorderLayout.CENTER);
        this.add(p3,BorderLayout.SOUTH);
        this.getContentPane().setBackground(Color.decode("#bbd9f7"));
    }
    
    private class AscultatorButoane implements ActionListener{
        public void actionPerformed(ActionEvent e){
            if(e.getSource()==b1){
                    cpu.trimiteDate(tf1.getText());
            }else if(e.getSource()==b2){
                FormularParolaUitata.this.dispose();
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
