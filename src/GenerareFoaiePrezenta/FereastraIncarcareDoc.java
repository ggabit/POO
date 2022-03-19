
package GenerareFoaiePrezenta;

import CreareCont.Cont;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;

public class FereastraIncarcareDoc extends JFrame{
    private JLabel nec,lbl;
    private JButton b1,b2,b3,b4;
    private JButton[] b;
    private JLabel[] paths;
    private JTextField tf1,tf2,tf3;
    private  JPanel p1,p2,p3;
    private ControllerGenerareFoi cgf;
    private int n;
    private String[] docTip;
    private String[] docPaths;
    private Cont contCurent;
    private String cdId;
    
    public FereastraIncarcareDoc(ControllerGenerareFoi cgf, Cont c, String docNec, String cdId){
        this.cgf=cgf;
        this.cdId=cdId;
        contCurent=c;
        p3 = new JPanel();
        lbl = new JLabel("Încărcare documente");
        lbl.setFont(new Font("Arial", Font.BOLD, 22));
        lbl.setHorizontalAlignment(JLabel.CENTER);
        p3.add(lbl);
        p3.setOpaque(false);
        
        p1=new JPanel();
        FereastraIncarcareDoc.AscultatorButoane ab=new FereastraIncarcareDoc.AscultatorButoane();
        String[] nec_sp = docNec.split(", ");
        this.n=nec_sp.length;
        
        this.docTip = new String[n];
        this.docTip=docNec.split(", ");
        
        //p1.setLayout(new GridLayout(n+1,2,10,10));
        p1=new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        nec = new JLabel("Sunt necesare următoarele documente: ");
        nec.setFont(new Font("Arial", Font.BOLD, 15));
        p1.add(nec,gbc);
        b = new JButton[n];
        paths = new JLabel[n];
        gbc.gridy++;
        for(int i=0;i<n;i++){
            // poate adaug si un label cu path-ul, il pun textul in actionListener
            b[i]=new JButton("browse");
            paths[i]=new JLabel();
            p1.add(new JLabel(nec_sp[i]),gbc);
            gbc.gridx++;
            p1.add(paths[i],gbc);
            gbc.gridx++;
            p1.add(b[i],gbc);
            b[i].addActionListener(ab);
            gbc.gridx = 0;
            gbc.gridy++;
        }
        p1.add(new JLabel("Doriți să adăugați o participare la proiect?"),gbc);
        b3=new JButton("Adaugă");
        gbc.gridx++;
        p1.add(b3,gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        p1.add(new JLabel("Doriți să adăugați o activitate suplimentară?"),gbc);
        b4=new JButton("Adaugă");
        gbc.gridx++;
        p1.add(b4,gbc);
        p1.setOpaque(false);
        
        b1=new JButton("Ok");
        b2=new JButton("Cancel");
        p2=new JPanel();
        p2.add(b1);
        p2.add(b2);
        p2.setOpaque(false);
        b1.addActionListener(ab);
        b2.addActionListener(ab);
        b3.addActionListener(ab);
        b4.addActionListener(ab);
        
        this.add(p3,BorderLayout.NORTH);
        this.add(p1,BorderLayout.CENTER);
        this.add(p2,BorderLayout.SOUTH);
        
        docPaths = new String[n];
        for(int i=0;i<n;i++){
            docPaths[i]="gol";
        }
        this.getContentPane().setBackground(Color.decode("#bbd9f7"));
    }
    private class AscultatorButoane implements ActionListener{
        public void actionPerformed(ActionEvent e) {
            for(int i=0;i<n;i++){
                if(e.getSource()==b[i]){
                    JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

                    int returnValue = jfc.showOpenDialog(null);

                    if (returnValue == JFileChooser.APPROVE_OPTION) {
                        File selectedFile = jfc.getSelectedFile();
                        //System.out.println(selectedFile.getAbsolutePath());
                        paths[i].setText(selectedFile.getAbsolutePath());
                        docPaths[i]=selectedFile.getAbsolutePath();
                    }
                }
            }
            if(e.getSource()==b3){    
                FormularParticipareProiect f = new FormularParticipareProiect(cgf);
                f.afiseaza();
            }
            else if(e.getSource()==b1){    
                ListaDocumente ld = new ListaDocumente(docPaths,docTip,cdId);
                //System.out.println(ld.toString());
                // trimit lista de documente in cgf
                cgf.incarcaDoc(ld);
            }
            else if(e.getSource()==b2){
                int dialogButton = JOptionPane.YES_NO_OPTION;
                int dialogResult = JOptionPane.showConfirmDialog (null, "Confirmați renunțarea la încărcare?","Warning",dialogButton);
                if (dialogResult == 0) { //YES option
                    //JOptionPane.showMessageDialog(null, "Renunțare la încărcare documente!");
                    FereastraIncarcareDoc.this.dispose();
                    FormularGenerareFoaie f = new FormularGenerareFoaie(cgf,contCurent);
                    if(contCurent.getRol().equalsIgnoreCase("director")){
                        f.afiseazaDir();
                    }
                    else f.afiseazaProf();
                }else{}
            }
            else if(e.getSource()==b4){
                FormularAlteActivitati f = new FormularAlteActivitati(cgf);
                f.afiseaza();
            }
            
        }
    }
    
    
    
    public void afiseaza(){
       this.setSize(1000,600);
       setVisible(true);
       setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
       setLocationRelativeTo(null);
   }
}
