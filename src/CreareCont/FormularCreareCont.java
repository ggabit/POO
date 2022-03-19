package CreareCont;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class FormularCreareCont extends JFrame{
    private JButton b1,b2;
    private JLabel lblDept;
    private JTextField tf1,tf2,tf3,tf4;
    private JComboBox cmb;
    private JComboBox<ComboItem> cmb2;
    private  JPanel p,p1,p2,p3;
    private ControllerCreareCont cc;
    private static Conexiune conex=Conexiune.getInstanta();
    private static Statement stmt=conex.getStatement();
    
    public FormularCreareCont(ControllerCreareCont cc){
        setTitle("Formular creare cont");
        this.cc=cc;
        
        p=new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lbl = new JLabel("Creare cont");
        lbl.setFont(new Font("Arial", Font.BOLD, 22));
        lbl.setHorizontalAlignment(JLabel.CENTER);
        gbc.gridx++;
        p.add(lbl,gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        p.add(new JLabel("rol"),gbc);
        gbc.gridx++;
        cmb=new JComboBox();
        cmb.addItem("Cadru didactic");
        cmb.addItem("Director de departament");
        cmb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                JComboBox cmb = (JComboBox) event.getSource();
                Object selected = cmb.getSelectedItem();
                String command = event.getActionCommand();
                if (command.equals("comboBoxChanged")) {
                    if(selected.equals("Cadru didactic")){
                        lblDept.setVisible(false);
                        cmb2.setVisible(false);
                        cc.trimiteCategorie("Profesor");
                    }else{
                        lblDept.setVisible(true);
                        cmb2.setVisible(true);
                        cc.trimiteCategorie("Director");
                    }
                }
            }
        });
        p.add(cmb,gbc);
        p.setOpaque(false);
        
        cmb2=new JComboBox<ComboItem>();
        try{
            String comanda="select idDepartament,denumire from departamente;";
            ResultSet rs=stmt.executeQuery(comanda);
            while(rs.next()){
                ComboItem c=new ComboItem(rs.getString("denumire"),rs.getString("idDepartament"));
                cmb2.addItem(c);
            }
        }catch(SQLException e){}
        cmb2.setSize(400, 100);
        
        tf1=new JTextField(16);
        tf2=new JTextField(16);
        tf3=new JTextField(16);
        tf4=new JTextField(16);
        b1=new JButton("Ok");
        b2=new JButton("cancel");
        
        p1=new JPanel();
        p1=new JPanel(new GridBagLayout());
        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.insets = new Insets(10,10,10,10);
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        p1.add(new JLabel("Nume"),gbc1);
        gbc1.gridx++;
        p1.add(tf1,gbc1);
        gbc1.gridx--;
        gbc1.gridy++;
        p1.add(new JLabel("Prenume"),gbc1);
        gbc1.gridx++;
        p1.add(tf2,gbc1);
        gbc1.gridx--;
        gbc1.gridy++;
        p1.add(new JLabel("Grad"),gbc1);
        gbc1.gridx++;
        p1.add(tf3,gbc1);
        gbc1.gridx--;
        gbc1.gridy++;
        p1.add(new JLabel("Email"),gbc1);
        gbc1.gridx++;
        p1.add(tf4,gbc1);
        p1.setOpaque(false);
        gbc1.gridx--;
        gbc1.gridy++;
        lblDept = new JLabel("Departament");
        p1.add(lblDept,gbc1);
        gbc1.gridx++;
        p1.add(cmb2,gbc1);
        lblDept.setVisible(false);
        cmb2.setVisible(false);
        p1.setOpaque(false);
        
        FormularCreareCont.AscultatorButoane ab=new FormularCreareCont.AscultatorButoane();
        p3=new JPanel();
        p3.add(b1);
        p3.add(b2);
        p3.setOpaque(false);
        b1.addActionListener(ab);
        b2.addActionListener(ab);
        
        this.add(p,BorderLayout.NORTH);
        this.add(p1,BorderLayout.CENTER);
        this.add(p3,BorderLayout.SOUTH);
        this.getContentPane().setBackground(Color.decode("#bbd9f7"));
    }
    
    public void afiseaza(){
        this.setSize(600,600);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
    }
    
    public void afiseazaFormularProf()
    {
        p2.setVisible(false);
    }
    
    public void afiseazaFormularDir()
    {
        p2.setVisible(true);
    }
    
    private class AscultatorButoane implements ActionListener{
        public void actionPerformed(ActionEvent e){
            if(e.getActionCommand().equals("Ok")){
                Object item = cmb2.getSelectedItem();
                String value = ((FormularCreareCont.ComboItem)item).getValue();
                if(((String)cmb.getSelectedItem()).equals("Director de departament")){
                    cc.trimiteCategorie("Director");
                    cc.trimiteDate(tf1.getText(),tf2.getText(),tf3.getText(),tf4.getText(),value);
                }
                else{
                    cc.trimiteCategorie("Profesor");
                    cc.trimiteDate(tf1.getText(),tf2.getText(),tf3.getText(),tf4.getText(),"");
                }
            }else{
                FormularCreareCont.this.dispose();
            }
        }
    }
    
    public class ComboItem{
        private String text;
        private String value;

        public ComboItem(String key, String value){
            this.text = key;
            this.value = value;
        }

        @Override
        public String toString(){
            return text;
        }

        public String getText(){
            return text;
        }

        public String getValue(){
            return value;
        }
    }
    
}
