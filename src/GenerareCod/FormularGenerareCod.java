
package GenerareCod;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class FormularGenerareCod extends JFrame{
    private JTextField tf1;
    private JButton b1,b2;
    private ControllerGenerareCod cgc;
    private JPanel p,p1,p2;
    
    public FormularGenerareCod(ControllerGenerareCod cgc){
        this.cgc=cgc;
        setTitle("Generare cod logare");
        FormularGenerareCod.AscultatorButoane ab=new FormularGenerareCod.AscultatorButoane();
        
        p=new JPanel(new GridLayout(1,1,10,10));
        JLabel lbl = new JLabel("Generare cod pentru logare");
        lbl.setFont(new Font("Arial", Font.BOLD, 22));
        lbl.setHorizontalAlignment(JLabel.CENTER);
        p.add(lbl);
        p.setOpaque(false);
        
        p1=new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        p1.add(new JLabel("Introduceți adresa de email"),gbc);
        gbc.gridy++;
        tf1=new JTextField(16);
        p1.add(tf1,gbc);
        p1.setOpaque(false);
        
        b1=new JButton("Ok");
        b2=new JButton("Cancel");
        p2=new JPanel();
        p2.add(b1);
        p2.add(b2);
        p2.setOpaque(false);
        b1.addActionListener(ab);
        b2.addActionListener(ab);
        
        this.add(p,BorderLayout.NORTH);
        this.add(p1,BorderLayout.CENTER);
        this.add(p2,BorderLayout.SOUTH);
        this.getContentPane().setBackground(Color.decode("#bbd9f7"));
    }
    
    private class AscultatorButoane implements ActionListener{
        public void actionPerformed(ActionEvent e){
            if(e.getSource()==b1){
                    cgc.trimiteDate(tf1.getText());
            }else if(e.getSource()==b2){
                FormularGenerareCod.this.dispose();
            }
        }
    }
    public void afiseaza(){
        this.setSize(500,500);
        this.setVisible(true);
        setLocationRelativeTo(null);
    }
}
