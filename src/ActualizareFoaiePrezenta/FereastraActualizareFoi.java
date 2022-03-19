
package ActualizareFoaiePrezenta;

import CreareCont.Cont;
import TrimitereFoaiePrezentaPrinEmail.ControllerTrimitereFoaie;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

public class FereastraActualizareFoi extends JFrame{
    private ControllerActualizareFoi caf;
    private Cont contCurent;
    private JButton b1,b2,b3;
    private  JPanel p,p1,p2;
    private JTable t;
    private int n,m;
    
    public FereastraActualizareFoi(ControllerActualizareFoi caf, Cont c, ArrayList<String[]> randuri){
        this.caf=caf;
        this.contCurent=c;
        setTitle("Fereastră actuzlizare foaie prezență");
        FereastraActualizareFoi.AscultatorButoane ab=new FereastraActualizareFoi.AscultatorButoane();
        
        p=new JPanel(new GridLayout(2,1,10,10));
        JLabel lbl = new JLabel("Actualizare foaie de prezență");
        lbl.setFont(new Font("Arial", Font.BOLD, 22));
        lbl.setHorizontalAlignment(JLabel.CENTER);
        p.add(lbl);
        JLabel lbl2 = new JLabel("Actualizați participările la proiecte:");
        lbl2.setFont(new Font("Arial", Font.BOLD, 14));
        lbl2.setHorizontalAlignment(JLabel.CENTER);
        p.add(lbl2);
        p.setOpaque(false);
        
        p1=new JPanel();
        GridBagLayout gridbag =new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        
        this.n=randuri.size()-1;
        this.m=randuri.get(0).length;
        Object[] colNames = randuri.get(0); //Activitatea/Proiect + Post/Functia in proiect + zilele 1->31
        Object[][] data=new Object[randuri.size()-1][randuri.get(0).length];
        for(int i=1;i<randuri.size();i++){
            for(int j=0;j<randuri.get(0).length;j++){
                data[i-1][j] = randuri.get(i)[j]; // completez cu date din randuri
            }
        }
        
        DefaultTableModel model = new DefaultTableModel(data,colNames);
        
        t = new JTable(model){
            @Override
            public boolean isCellEditable(int row,int column)
            {   //sa nu pot edita coloanele cu X (pe cele cu C pot)
                if(data[0][column].equals("X"))
                    return false;
                if(row >2)
                 return true;
                else
                  return false;
            }
        };
                
        int rowCount = model.getRowCount();
        t.setPreferredSize(new Dimension(900, (t.getRowHeight()*(rowCount))));
        TableColumnModel columnModel = t.getColumnModel();
        for(int i=0;i<randuri.get(0).length;i++){
            if(i==0) 
                columnModel.getColumn(i).setPreferredWidth(210);
            else if(i==1)
                columnModel.getColumn(i).setPreferredWidth(140);
            else 
                columnModel.getColumn(i).setPreferredWidth(12);
        }
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
        
        this.add(p,BorderLayout.NORTH);
        this.add(p1,BorderLayout.CENTER);
        this.add(p2,BorderLayout.SOUTH);
        this.getContentPane().setBackground(Color.decode("#bbd9f7"));
    }
    
    private class AscultatorButoane implements ActionListener{
        private ControllerTrimitereFoaie ctf;
        
        private AscultatorButoane(){
        ctf = new ControllerTrimitereFoaie();
        }
        public void actionPerformed(ActionEvent e){
            if(e.getSource()==b1){
                // generare fisier nou xlsx (Actualizare)
                // iau datele din jtable
                ArrayList<String[]> date=new ArrayList<String[]>();
                boolean[] isEmpty = new boolean[n-3];
                for(int i=3;i<n;i++){
                    isEmpty[i-3]=true;
                    for(int j=0;j<m;j++){
                        if(!getData(t,i,j).equals("")) isEmpty[i-3]=false;
                    }
                }
                
                for(int i=0;i<n;i++){
                    if(i<3){
                        String[] row = new String[m];
                        for(int j=0;j<m;j++){
                            row[j] = getData(t,i,j);
                        }
                        date.add(row);
                    }
                    else if(i>=3 && !isEmpty[i-3]){
                        String[] proiect = new String[m];
                        for(int j=0;j<m;j++){
                            proiect[j] = getData(t,i,j);
                        }
                        date.add(proiect);
                    }
                }
                caf.trimiteDateGenerare(date);
            }
            else if(e.getSource()==b2){
                int dialogButton = JOptionPane.YES_NO_OPTION;
                int dialogResult = JOptionPane.showConfirmDialog (null, "Confirmați renunțarea la actualizare?","Warning",dialogButton);
                if (dialogResult == 0) { //YES option
                    //JOptionPane.showMessageDialog(null, "Renunțare ");
                    FereastraActualizareFoi.this.dispose();
                }
            }
        }
    }
    
    public String getData(JTable table, int row_index, int col_index){
        return table.getModel().getValueAt(row_index, col_index).toString();
    } 
    
    public void afiseaza(){
        this.setSize(1300,500);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
    }
}
