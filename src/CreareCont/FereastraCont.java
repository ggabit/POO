package CreareCont;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.ColorUIResource;

public class FereastraCont extends JFrame{
    private Cont c;
    private  JPanel p,p1;
    private JTextField tf1,tf2,tf3,tf4;
    
    public FereastraCont(Cont c){
        this.c=c;
        setTitle("Fereastra cont");
        
        p=new JPanel(new GridLayout(1,1,10,10));
        JLabel lbl = new JLabel("Cont creat cu succes");
        lbl.setFont(new Font("Arial", Font.BOLD, 22));
        lbl.setHorizontalAlignment(JLabel.CENTER);
        p.add(lbl);
        p.setOpaque(false);
        
        tf1=new JTextField(16);
        tf2=new JTextField(16);
        tf3=new JTextField(16);
        tf4=new JTextField(16);
        UIManager.put("TextField.inactiveBackground", new ColorUIResource(Color.decode("#e6f2ff")));
        CadruDidactic cd = c.getCadruDidactic();
        tf1.setText(cd.getNumeComplet());
        tf2.setText(cd.getGrad());
        String s=c.getInfo();
        String[] itemi=s.split(" ");
        tf3.setText(itemi[1]);
        tf4.setText(itemi[0]);
        
        p1=new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        p1.add(new JLabel("Nume"),gbc);
        gbc.gridx++;
        p1.add(tf1,gbc);
        gbc.gridx--;
        gbc.gridy++;
        p1.add(new JLabel("Grad"),gbc);
        gbc.gridx++;
        p1.add(tf2,gbc);
        gbc.gridx--;
        gbc.gridy++;
        p1.add(new JLabel("Email"),gbc);
        gbc.gridx++;
        p1.add(tf3,gbc);
        gbc.gridx--;
        gbc.gridy++;
        p1.add(new JLabel("Rol"),gbc);
        gbc.gridx++;
        p1.add(tf4,gbc);
        p1.setOpaque(false);
        
        tf1.setEditable(false);
        tf2.setEditable(false);
        tf3.setEditable(false);
        tf4.setEditable(false);
        
        this.add(p,BorderLayout.NORTH);
        this.add(p1,BorderLayout.CENTER);
        this.getContentPane().setBackground(Color.decode("#bbd9f7"));
    }
    
    public void afiseaza(){
       this.setSize(600,600);
       setVisible(true);
       setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
       this.setLocationRelativeTo(null);
   }
}
