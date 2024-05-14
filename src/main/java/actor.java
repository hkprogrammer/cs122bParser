
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class actor {

    private String name;
    private int birthYear;
    private HashSet<String> films;
    public actor(){
        this.name = "";
        this.birthYear = -1;
        this.films = new HashSet<>();
    }
    public actor(String name, int birthYear) {
        this.name = name;
        this.birthYear = birthYear;
        this.films = new HashSet<>();
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
    public HashSet<String> getFilms() {
        return films;
    }
    public void addFilm(String film) {
        films.add(film);
    }
    public void print(){
        System.out.println("Name: " + name);
        System.out.println("Birth Year: " + birthYear);
    }


}
