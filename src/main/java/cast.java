
import java.util.ArrayList;
import java.util.List;

public class cast {

    private String name;
    private int birthYear;
    private List<film> films;
    public cast(){

    }
    public cast(String name, int birthYear) {
        this.name = name;
        this.birthYear = birthYear;
        this.films = new ArrayList<film>();
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getBirthYear() {
        return birthYear;
    }
    public void setBirthYear(int birthYear) {
        this.birthYear = birthYear;
    }

    public List<film> getFilms() {
        return films;
    }
    public void addFilm(film film) {
        films.add(film);
    }
    public void print(){
        System.out.println("Name: " + name);
        System.out.println("Birth Year: " + birthYear);
    }


}
