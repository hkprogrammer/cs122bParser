import java.io.IOException;
import java.util.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class loadFilms extends DefaultHandler{

    static final int LIMIT = 500;

    private List<director> directors;
    private film currFilm;
    private String tempVal;
    private director currDirector;
    private cast currCast;
    private String xmlPath;
    private int processedAmount;
    private boolean newDirector;

    public loadFilms(String xmlPath) {
//        SAXParserFactory factory = SAXParserFactory.newInstance();
        this.directors = new ArrayList<director>();
        this.xmlPath = xmlPath;

    }

    private void parseDocument(){

        SAXParserFactory spf = SAXParserFactory.newInstance();
        try{

            SAXParser sp = spf.newSAXParser();

            sp.parse(xmlPath, this);

        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }

    }
    /**
     * Iterate through the list and print
     * the contents
     */
    private void printData() {

        System.out.println("No of curr directors '" + directors.size() + "'.");

    }
    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("directorfilms")) {
            //create a new direcor instance
            this.currDirector = new director();
            newDirector = false;
        }
        if(qName.equalsIgnoreCase("film")){
            this.currFilm = new film();
        }

    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        this.processedAmount++;
//        System.out.println(qName);
        if(qName.equalsIgnoreCase("directorfilms")){
            this.directors.add(currDirector);
            this.currDirector = null;
        }
        if(qName.equalsIgnoreCase("dirname")){
            if(newDirector == false){
                this.currDirector.setName(tempVal);
            }
            newDirector = true;
        }
        if(qName.equalsIgnoreCase("film")){
            if(currDirector == null){
                System.out.println("xmlLoader.xmlLoader.film: " + tempVal + " does not belong to a xmlLoader.xmlLoader.director");
                return;
            }
//            this.currFilm.setDirector(this.currDirector.getName());
            currDirector.addFilm(this.currFilm);
            this.currFilm = null;
        }
        if(qName.equalsIgnoreCase("t")){
            if(this.currFilm == null){
                System.out.println("xmlLoader.title: " + tempVal + " does not belong to a xmlLoader.xmlLoader.director");
                return;
            }
            this.currFilm.setTitle(tempVal);
        }
        if(qName.equalsIgnoreCase("year")){
            if(this.currFilm == null){
                System.out.println("xmlLoader.year: " + tempVal + " does not belong to a xmlLoader.xmlLoader.director");
                return;
            }
            this.currFilm.setYear(tempVal);
        }
        if(qName.equalsIgnoreCase("fid")){
            if(this.currFilm == null){
                System.out.println("xmlLoader.fid: " + tempVal + " does not belong to a xmlLoader.xmlLoader.director");
                return;
            }
            this.currFilm.setID(tempVal);
        }
        if(qName.equalsIgnoreCase("cat")){
            if(this.currFilm == null){
                System.out.println("xmlLoader.cat: " + tempVal + " does not belong to a xmlLoader.xmlLoader.director");
                return;
            }
            this.currFilm.addCategory(tempVal);
        }

    }
    public void print(){

//        for(int i = 0;i<this.directors.size();i++){
//            xmlLoader.director d = this.directors.get(i);
//            d.printall();
//        }

    }

    public List<film> processUniqueFilms(){
        List<film> films = new ArrayList<>();
        Set<String> filmID = new HashSet<>();
        for(int i = 0;i<this.directors.size();i++){
            director d = this.directors.get(i);
            List<film> d_films = d.getFilms();
            if(d.getName().isEmpty()){
                System.out.println("Found a xmlLoader.director name blank");
                continue;
            }
            for(int j = 0;j<d_films.size();j++){
                d_films.get(j).setDirector(d.getName());
                String id = d_films.get(j).getId();
                if(!filmID.contains(id)){
                    //check for duplicates:
                    filmID.add(id);
                    films.add(d_films.get(j));
                }
            }
        }
        return films;
    }
    /**
     *
     *
     *
     * @return list of list, where inner list is [id, title, year, xmlLoader.director]
     */
    public List<List<String>> exportMovies(List<film> uniqueFilmList){
        //transform directors to films:
        List<List<String>> results = new ArrayList<>();
        for(int i = 0;i<uniqueFilmList.size();i++){
            List<String> col = new ArrayList<>();
            film f = uniqueFilmList.get(i);
            col.add(f.getId());
            col.add(f.getTitle());
            col.add(f.getYear());
            col.add(f.getDirector());
            results.add(col);
        }
        return results;
    }
    public List<List<String>> exportGenres(List<film> uniqueFilmList){
        List<List<String>> results = new ArrayList<>();
        for(int i = 0; i<uniqueFilmList.size();i++){
            film f = uniqueFilmList.get(i);
            //retrieve the categories:
            HashSet<String> cats = f.getCategroy();
            if(cats.isEmpty()){
                //no category for this movie:
                continue;
            }
            Iterator<String> it = cats.iterator();
            while(it.hasNext()){
                List<String> col = new ArrayList<>();
                String category = it.next();
                String f_id = f.getId();
                col.add(f_id);
                col.add(category);
                results.add(col);
            }
        }
        return results;

    }

    public void printAmount(){

        System.out.println("Total " + directors.size() + " Directors");
        for(int i = 0; i < directors.size(); i++){
            director d = directors.get(i);
            d.printAmount();
        }

    }
    public void runProgram(){
        this.parseDocument();
//        this.printData();
//        this.print();
        this.printAmount();

//        List<xmlLoader.film> unique = processUniqueFilms();
//        List<List<String>> l = exportMovies(unique);
//        List<List<String>> g = exportGenres(unique);
//        System.out.println("Total films: " + l.size());
//        System.out.println("Total genres: " + g.size());

    }

    public static void main(String[] args) {
        loadFilms spe = new loadFilms("./newXML.xml");
        spe.runProgram();

    }



}
