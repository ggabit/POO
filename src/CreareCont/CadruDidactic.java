package CreareCont;

public class CadruDidactic {
    private  Persoana p;
    private String dir; //emailul dir
    private String grad,email,dept;
    
    public CadruDidactic (String nume,String prenume,String grad,String email,String dir){ // pt profesori
        this.grad=grad;
        this.email=email;
        this.dir=dir;
        p=new Persoana(nume,prenume);
    }
    
    public CadruDidactic (String nume,String prenume,String grad,String email,String dir,String dep){ // pt directori
        this.grad=grad;
        this.email=email;
        this.dir=dir;
        this.dept=dep;
        p=new Persoana(nume,prenume);
    }
    
    public String getGrad(){
        return grad;
    }
    
    public String getDir(){
        return dir;
    }
    
    public String getNume(){
        return p.getNume();
    }
    
    public String getPrenume(){
        return p.getPrenume();
    }
    
    public String getDept(){
        return dept;
    }
     
     public String getNumeComplet(){
         return p.getNume()+" "+p.getPrenume();
     }
    
    public String getEmail(){
        return email;
    }
}
