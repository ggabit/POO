package GenerareFoaiePrezenta;

import CreareCont.Conexiune;
import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;


public class FormularParticipareProiect extends JFrame{
    private ControllerGenerareFoi cgf;
    private JButton b1,b2;
    private JTextField tf1,tf2;
    private  JPanel p,p1,p2;
    private JDateChooser dc1,dc2;
    private JComboBox<ComboItem> cmb;
    private static Conexiune conex=Conexiune.getInstanta();
    private static Statement stmt=conex.getStatement();
    
    public FormularParticipareProiect(ControllerGenerareFoi cgf){
        setTitle("Formular participare proiect");
        this.cgf=cgf;
        FormularParticipareProiect.AscultatorButoane ab=new FormularParticipareProiect.AscultatorButoane();
        
        p=new JPanel(new GridLayout(1,1,10,10));
        JLabel lbl = new JLabel("Adăugare participare proiect");
        lbl.setFont(new Font("Arial", Font.BOLD, 22));
        lbl.setHorizontalAlignment(JLabel.CENTER);
        p.add(lbl);
        p.setOpaque(false);
        
        p1=new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        tf1=new JTextField(13);
        tf2=new JTextField(13);
        dc1 = new JDateChooser();
        dc1.setPreferredSize(new Dimension(145,20));
        dc1.setLocale(Locale.getDefault());
        p1.add(new JLabel("Dată începere"),gbc);
        gbc.gridx++;
        p1.add(dc1,gbc);
        dc2 = new JDateChooser();
        dc2.setPreferredSize(new Dimension(145,20));
        dc2.setLocale(Locale.getDefault());
        gbc.gridx--;
        gbc.gridy++;
        p1.add(new JLabel("Dată încheiere"),gbc);
        gbc.gridx++;
        p1.add(dc2,gbc);
        gbc.gridx--;
        gbc.gridy++;
        p1.add(new JLabel("Funcție"),gbc);
        gbc.gridx++;
        p1.add(tf1,gbc);
        cmb=new JComboBox<ComboItem>();
        try{
            String comanda="select idProiect,denumire from proiectecercetare;";
            ResultSet rs=stmt.executeQuery(comanda);
            while(rs.next()){
                ComboItem c=new ComboItem(rs.getString("denumire"),rs.getString("idProiect"));
                cmb.addItem(c);
            }
        }catch(SQLException e){}
        gbc.gridx--;
        gbc.gridy++;
        p1.add(new JLabel("Proiect"),gbc);
        gbc.gridx++;
        p1.add(cmb,gbc);
        gbc.gridx--;
        gbc.gridy++;
        p1.add(new JLabel("Total ore/lună"),gbc);
        gbc.gridx++;
        p1.add(tf2,gbc);
        p1.setOpaque(false);
        
        b1=new JButton("Adaugă");
        b2=new JButton("Cancel");
        p2=new JPanel();
        p2.add(b1);
        p2.add(b2);
        b1.addActionListener(ab);
        b2.addActionListener(ab);
        p2.setOpaque(false);
        
        this.add(p,BorderLayout.NORTH);
        this.add(p1,BorderLayout.CENTER);
        this.add(p2,BorderLayout.SOUTH);
        this.getContentPane().setBackground(Color.decode("#bbd9f7"));
    }
    
    private class AscultatorButoane implements ActionListener{
        public void actionPerformed(ActionEvent e){
            if(e.getSource()==b1){
                    Object item = cmb.getSelectedItem();
                    String value = ((ComboItem)item).getValue();
                    
                    if(dc1.getDate()==null || dc2.getDate()==null){
                        cgf.trimiteDateParticipare("","",tf1.getText(),tf2.getText(),value,FormularParticipareProiect.this);
                    }
                    else{
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        String formatted1 = format.format(dc1.getDate());
                        //System.out.println(formatted1);
                        String formatted2 = format.format(dc2.getDate());
                        //System.out.println(formatted2);
                        cgf.trimiteDateParticipare(formatted1,formatted2,tf1.getText(),tf2.getText(),value,FormularParticipareProiect.this);
                    }
            }
            else if(e.getSource()==b2){
                //JOptionPane.showMessageDialog(null, "Renunțare la adăugare participare proiect!");
                FormularParticipareProiect.this.dispose();
            }
        }
    }
    
    public void afiseaza(){
        this.setSize(600,500);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
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
