
package VizualizareSituatieProfesori;

import CreareCont.Cont;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;


public class FereastraSituatie extends JFrame{
    private ArrayList<String[]> listaCD;
    private ControllerVizualizareSituatie cvs;
    private Cont contCurent;
    private JButton b1,b2,b3;
    private JButton[][] b;
    private  JPanel p,p1,p2;
    private GridBagConstraints gbc;
    private int n,m;
    private JTable t;
    private String luna,an;
    
    public FereastraSituatie(ControllerVizualizareSituatie cvs,ArrayList<String[]> listaCD, Cont c,String luna, String an){
        this.listaCD=listaCD;
        this.contCurent=c;
        this.cvs=cvs;
        this.luna=luna;
        this.an=an;
        n=listaCD.size();
        setTitle("Fereastră vizualizare situație");
        
        FereastraSituatie.AscultatorButoane ab=new FereastraSituatie.AscultatorButoane();
        p=new JPanel(new GridLayout(1,1,10,10));
        JLabel lbl = new JLabel("Lista profesorilor ce nu și-au generat foaia de prezență pe luna "+luna+" "+an+":");
        lbl.setFont(new Font("Arial", Font.BOLD, 22));
        lbl.setHorizontalAlignment(JLabel.CENTER);
        p.add(lbl);
        p.setOpaque(false);
        
        p1=new JPanel();
        GridBagLayout gridbag =new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        
        this.n=listaCD.size();
        this.m=listaCD.get(0).length;
        Object[] colNames = {"#","Grad","Nume","Prenume","Email"};
        Object[][] data=new Object[n][m+1];
        for(int i=0;i<n;i++){
            data[i][0]=i+1;
            for(int j=0;j<m;j++){
                data[i][j+1] = listaCD.get(i)[j]; // completez cu date din listaCd
            }
        }
        
        DefaultTableModel model = new DefaultTableModel(data,colNames);
        
        t = new JTable(model){
            @Override
            public boolean isCellEditable(int row,int column)
            {   return false;
            }
        };
        
        int rowCount = model.getRowCount();
        t.setPreferredSize(new Dimension(900, (t.getRowHeight()*(rowCount))));
        TableColumnModel columnModel = t.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(8);
        columnModel.getColumn(1).setPreferredWidth(100);
        columnModel.getColumn(2).setPreferredWidth(100);
        columnModel.getColumn(3).setPreferredWidth(100);
        columnModel.getColumn(4).setPreferredWidth(120);
        
        t.getTableHeader().setOpaque(false);
        t.getTableHeader().setBackground(Color.decode("#e6f2ff"));
        
        JTableHeader header = t.getTableHeader();
        p1.setLayout(gridbag);
        p1.add(header,gbc);
        gbc.gridy++;
        p1.add(t,gbc);
        p1.setOpaque(false);
        
        // Etichete deasupra pentru datele profesorilor
        /*p1.add(new JLabel("#"),gbc);
        gbc.gridx++;
        p1.add(new JLabel("Grad"),gbc);
        gbc.gridx++;
        p1.add(new JLabel("Nume"),gbc);
        gbc.gridx++;
        p1.add(new JLabel("Prenume"),gbc);
        gbc.gridx++;
        p1.add(new JLabel("Email"),gbc);
        gbc.gridx++;
        gbc.gridx=0;
        gbc.gridy++;*/
        
        /*for(int i=0;i<n;i++){ // datele foilor
            p1.add(new JLabel(String.valueOf(i+1)+"."),gbc);
            gbc.gridx++;
            for(int j=0;j<4;j++){
                p1.add(new JLabel(listaCD.get(i)[j]),gbc);
                gbc.gridx++;
            }
            gbc.gridy++;
            gbc.gridx=0;
        }*/
        
        b1=new JButton("Salvează fișier");
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
    
    public void afiseaza(){
        this.setSize(1100,600);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
    }
    
    private class AscultatorButoane implements ActionListener{

        public void actionPerformed(ActionEvent e){
            if(e.getSource()==b1){
                    // salveaza fisier
                int dialogButton = JOptionPane.YES_NO_OPTION;
                int dialogResult = JOptionPane.showConfirmDialog (null, "Confirmați salvarea documentului?","Warning",dialogButton);
                if (dialogResult == 0) { // YES
                    cvs.salveazaDoc(listaCD,luna,an);
                }
            }
            else if(e.getSource()==b2){
                // cancel
                cvs.ferViz(true);
                FereastraSituatie.this.dispose();
            }
        }
    }
}
