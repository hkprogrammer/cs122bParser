import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class film {

    private String director;
    private String title;
    private String year;
    private String id;
    private List<cast> casts;
    private HashSet<String> categroy;

    public film(String director, String title, String year, String id) {
        this.director = director;
        this.title = title;
        this.year = year;
        this.casts = new ArrayList<>();
        this.id = id;
        this.categroy = new HashSet<>();
    }
    public film(){
        this.director = null;
        this.title = null;
        this.year = null;
        this.casts = new ArrayList<>();
        this.id = "";
        this.categroy = new HashSet<>();
    }
    public void setID(String id){
        this.id = id;
    }
    public String getId(){
        return this.id;
    }
    public void addCategory(String cat){
        if(this.categroy.contains(cat)){
            return;
        }
        this.categroy.add(cat);
    }
    public HashSet<String> getCategroy(){
        return this.categroy;
    }
    public String getDirector(){
        return director;
    }
    public String getTitle(){
        return title;
    }
    public String getYear(){
        return year;
    }
    public void setDirector(String director){
        this.director = director;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public void setYear(String year){
        this.year = year;
    }
    public void addCast(cast c){
        this.casts.add(c);
    }
    public List<cast> getCasts(){
        return casts;
    }
    public void print(){
        System.out.println("###########\nPrinting Movie:");
        System.out.println("ID: " + id);
        System.out.println("Director: " + director);
        System.out.println("Title: " + title);
        System.out.println("Year: " + year);
//        for(int i = 0;i<this.casts.size();i++){
//            cast c = this.casts.get(i);
//            c.print();
//        }
    }


}
