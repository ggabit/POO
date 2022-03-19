
package GenerareFoaiePrezenta;

import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Locale;
import javax.swing.*;

public class FormularAlteActivitati extends JFrame{
    private ControllerGenerareFoi cgf;
    private JButton b1,b2;
    private JTextField tf1;
    private  JPanel p,p1,p2;
    private JDateChooser dc1;
    
    public FormularAlteActivitati(ControllerGenerareFoi cgf){
        setTitle("Formular Activități suplimentare");
        this.cgf=cgf;
        FormularAlteActivitati.AscultatorButoane ab=new FormularAlteActivitati.AscultatorButoane();
        
        p=new JPanel(new GridLayout(1,1,10,10));
        JLabel lbl = new JLabel("Adăugare activitate suplimentară");
        lbl.setFont(new Font("Arial", Font.BOLD, 22));
        lbl.setHorizontalAlignment(JLabel.CENTER);
        p.add(lbl);
        p.setOpaque(false);
        
        p1=new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        dc1 = new JDateChooser();
        dc1.setLocale(Locale.getDefault());
        dc1.setPreferredSize(new Dimension(145,20));
        p1.add(new JLabel("Dată activitate"),gbc);
        gbc.gridy++;
        p1.add(new JLabel("Număr de ore"),gbc);
        gbc.gridx++;
        tf1=new JTextField(10);
        p1.add(tf1,gbc);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridx++;
        p1.add(dc1,gbc);
        p1.setOpaque(false);
        
        b1=new JButton("Adaugă");
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
                    if(dc1.getDate()==null){
                        cgf.trimiteDateActivitate("",tf1.getText(),FormularAlteActivitati.this);
                    }
                    else{
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        String formatted = format.format(dc1.getDate());
                        cgf.trimiteDateActivitate(formatted,tf1.getText(),FormularAlteActivitati.this);
                    }
            }
            else if(e.getSource()==b2){
                JOptionPane.showMessageDialog(null, "Renunțare la adăugare activitate suplimentară!");
                FormularAlteActivitati.this.dispose();
            }
        }
    }
    
    public void afiseaza(){
        this.setSize(500,400);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
    }
}
