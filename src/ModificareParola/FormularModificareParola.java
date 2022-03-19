package ModificareParola;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.*;

public class FormularModificareParola extends JFrame{
    private TextField tf1,tf2;
    private JButton b1,b2;
    private ControllerModificareParola cmp;
    private JPanel p,p1,p2;
    
    public FormularModificareParola(ControllerModificareParola cmp){
        this.cmp=cmp;
        setTitle("Modificare parolă");
        
        p=new JPanel(new GridLayout(1,1,10,10));
        JLabel lbl = new JLabel("Modificare parolă");
        lbl.setFont(new Font("Arial", Font.BOLD, 22));
        lbl.setHorizontalAlignment(JLabel.CENTER);
        p.add(lbl);
        p.setOpaque(false);
        
        p1=new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        tf1=new TextField(20);
        tf2=new TextField(20);
        tf1.setEchoChar('*');
        tf2.setEchoChar('*');
        p1.add(new JLabel("Noua parolă"),gbc);
        gbc.gridx++;
        p1.add(tf1,gbc);
        gbc.gridy++;
        gbc.gridx--;
        p1.add(new JLabel("Confirmați noua parolă"),gbc);
        gbc.gridx++;
        p1.add(tf2,gbc);
        p1.setOpaque(false);
        
        FormularModificareParola.AscultatorButoane ab=new FormularModificareParola.AscultatorButoane();
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
                try {
                    cmp.trimiteDate(tf1.getText(),tf2.getText());
                } catch (IOException ex) {}
            }else if(e.getSource()==b2){
                FormularModificareParola.this.dispose();
            }
        }
    }
    public void afiseaza(){
        this.setSize(500,500);
        this.setVisible(true);
        setLocationRelativeTo(null);
    }
}
