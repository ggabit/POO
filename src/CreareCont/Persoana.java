package CreareCont;

public class Persoana {
  private String nume,prenume;
  
  public Persoana(String nume,String prenume){
      this.nume=nume;
      this.prenume=prenume;
  }

    public String getNume() {
        return nume;
    }

    public String getPrenume() {
        return prenume;
    }
    
    public String toString() {
        return "nume=" + nume + " prenume=" + prenume ;
    }
    
}
