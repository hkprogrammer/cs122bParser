import java.util.ArrayList;
import java.util.List;

public class director {

    private String name;
    private List<film> films;
    public director(String name) {
        this.name = name;
        this.films = new ArrayList<film>();
    }
    public director(){
        this.name = "";
        this.films = new ArrayList<film>();
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public List<film> getFilms() {
        return films;
    }
    public void addFilm(film f){
        films.add(f);
    }
    public void printall(){
        System.out.println("Director Name: " + this.name);
        for(int i = 0; i<this.films.size(); i++){
            film f = this.films.get(i);
            f.print();
        }
    }
    public void printAmount(){
        System.out.println("Director: " + name + " with " + films.size() + " films");
    }

}
