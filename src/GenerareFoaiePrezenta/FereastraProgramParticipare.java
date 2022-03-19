package GenerareFoaiePrezenta;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class FereastraProgramParticipare extends JFrame{
    private ControllerGenerareFoi cgf;
    private JTable t;
    private JPanel p1,p2,p3;
    private JButton b1,b2;
    private String partId;
    private Participare p;
    
    public FereastraProgramParticipare(ControllerGenerareFoi cgf, String partId, Participare p){
        setTitle("Fereastră program participare proiect");
        this.cgf=cgf;
        this.partId=partId;
        this.p=p;
        FereastraProgramParticipare.AscultatorButoane ab=new FereastraProgramParticipare.AscultatorButoane();
        
        p3=new JPanel(new GridLayout(2,1,10,10));
        JLabel lbl = new JLabel("Adăugare program participare proiect");
        lbl.setFont(new Font("Arial", Font.BOLD, 22));
        lbl.setHorizontalAlignment(JLabel.CENTER);
        p3.add(lbl);
        JLabel lbl2 = new JLabel("Introduceți numărul de ore pentru fiecare zi:");
        lbl2.setFont(new Font("Arial", Font.BOLD, 14));
        lbl2.setHorizontalAlignment(JLabel.CENTER);
        p3.add(lbl2);
        p3.setOpaque(false);
        
        p1=new JPanel();
        GridBagLayout gridbag =new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        
        Object[] colNames = {"Luni","Marți","Miercuri","Joi","Vineri"};
        Object[][] data = {{"0", "0", "0", "0", "0"}};
        
        DefaultTableModel model = new DefaultTableModel(data,colNames);
        t = new JTable(model);
        t.setPreferredSize(new Dimension(200, 18));
        t.getTableHeader().setOpaque(false);
        t.getTableHeader().setBackground(Color.decode("#e6f2ff"));
        
        JTableHeader header = t.getTableHeader();
        p1.setLayout(gridbag);
        p1.add(header,gbc);
        gbc.gridy++;
        p1.add(t,gbc);
        p1.setOpaque(false);
        
        b1=new JButton("Salvează");
        b2=new JButton("Cancel");
        p2=new JPanel();
        p2.add(b1);
        p2.add(b2);
        b1.addActionListener(ab);
        b2.addActionListener(ab);
        p2.setOpaque(false);
        
        this.add(p3,BorderLayout.NORTH);
        this.add(p1,BorderLayout.CENTER);
        this.add(p2,BorderLayout.SOUTH);
        this.getContentPane().setBackground(Color.decode("#bbd9f7"));
    }
    
    private class AscultatorButoane implements ActionListener{
        public void actionPerformed(ActionEvent e){
            if(e.getSource()==b1){
                String[] ore = new String[5];
                for(int i=0;i<5;i++){
                    ore[i] = t.getValueAt(0, i).toString();
                    System.out.println(ore[i]);
                }
                cgf.trimiteDateProgram(ore, partId,FereastraProgramParticipare.this);
            }
            else if(e.getSource()==b2){
                JOptionPane.showMessageDialog(null, "Renunțare adăugare program!");
                cgf.genereazaProgram(p, partId);
                FereastraProgramParticipare.this.dispose();
            }
        }
    }
    
    public void afiseaza(){
        this.setSize(600,500);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
    }
}
