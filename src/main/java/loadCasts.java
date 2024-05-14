import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class loadCasts extends DefaultHandler{

    static final int LIMIT = 500;

    private List<film> films;
    private film currentFilm;
    private String xmlPath;
    private String tempVal;
    private int processedAmount;

    public loadCasts(String xmlPath) {
//        SAXParserFactory factory = SAXParserFactory.newInstance();
        this.films = new ArrayList<>();
        this.currentFilm = null;
        this.xmlPath = xmlPath;
        this.processedAmount = 0;
//        this.goodActors = 0;
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
        System.out.println("No of curr films '" + films.size() + "'.");
    }
    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("filmc")) {
            //create a new direcor instance
            this.currentFilm = new film();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }
    public void endElement(String uri, String localName, String qName) throws SAXException {
        this.processedAmount++;
//        System.out.println(qName);
        String chars = tempVal;
        if(qName.equalsIgnoreCase("filmc")){
            if(this.currentFilm != null){
                //check if exiting film exists:
                for(int i = 0;i<this.films.size();i++){
                    if(this.films.get(i).getId() == this.currentFilm.getId()){
                        //append all existing film casts to this new film:
                        List<cast> casts = this.currentFilm.getCasts();
                        for(int j = 0;j<casts.size();j++){
                            this.films.get(i).addCast(casts.get(j));
                        }
                        this.currentFilm = null;
                        return;
                    }

                }
                this.films.add(this.currentFilm);
            }
            this.currentFilm = null;
        }

        if(this.currentFilm == null){
            return;
        }

        if(qName.equalsIgnoreCase("f")){
            this.currentFilm.setID(chars);
        }
        if(qName.equalsIgnoreCase("a")){
            if(chars == null || chars.isBlank()){
                System.out.println("ERROR: actor name is blank");
                return;
            }
            this.currentFilm.addCast(new cast(chars));
        }

    }
    //prepare for insertions:
    //relationship: [0] filmID, [1] name
    public List<List<String>> exportCasts(){
        List<List<String>> re = new ArrayList<>();
        for(int i = 0;i<this.films.size();i++){
            film f = this.films.get(i);
            //handle duplicate casts:
            List<String> casts =  new ArrayList<>();
            List<cast> f_casts = f.getCasts();
            for(int j = 0;j<f_casts.size();j++){
                if(casts.contains(f_casts.get(j).getName())){
                    continue;
                }
                if(f_casts.get(j).getName().isBlank()){
                    System.out.println("Found a record of blank name under movie " + f.getId() + " !");
                }
                casts.add(f_casts.get(j).getName());
            }
            for(int j = 0;j<casts.size();j++){
                List<String> col = new ArrayList<>();
                col.add(f.getId());
                col.add(casts.get(j));
                re.add(col);
            }
        }
        return re;

    }
    public void printAmount(){
        List<List<String>> c = exportCasts();
        System.out.println("Total " + c.size() + " casts");
    }
    public void runProgram(){
        this.parseDocument();
        this.printAmount();

    }

    public static void main(String[] args) {
        loadCasts spe = new loadCasts("./casts124.xml");
        spe.runProgram();

    }



}
