import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.util.*;

public class loadActors extends DefaultHandler{

    static final int LIMIT = 500;

    private List<actor> actors;
    private actor currActor;
    private String xmlPath;
    private String tempVal;
    private film currFilm;
    private int processedAmount;
    private int goodActors;
    public loadActors(String xmlPath) {
//        SAXParserFactory factory = SAXParserFactory.newInstance();
        this.actors = new ArrayList<>();
        this.currActor = null;
        this.xmlPath = xmlPath;
        this.processedAmount = 0;
        this.goodActors = 0;
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
        System.out.println("No of curr films '" + actors.size() + "'.");
    }
    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("actor")) {
            //create a new direcor instance
            this.currActor = new actor();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        this.processedAmount++;
//        System.out.println(qName);
        String chars = tempVal;

        if (qName.equalsIgnoreCase("actor")) {
            if(this.currActor != null){
                this.actors.add(this.currActor);
            }
            this.currActor = null;
        }
        if(this.currActor == null){
            return;
        }
        if(qName.equalsIgnoreCase("stagename")){
            this.currActor.setName(chars);
        }
        if(qName.equalsIgnoreCase("dob")){
            int y = -1;
            try{
                String year = chars;
                if(chars.length()>0 && chars.charAt(chars.length()-1) == '+'){
                    year = year.substring(0, year.length()-1);
                }
                y = Integer.parseInt(year);
                goodActors++;
            }
            catch(Exception e){
//                System.out.println("Cannot process year numerical value for " + tempVal);
            }
            this.currActor.setBirthYear(y);
        }

    }
    //prepare for insertions:
    public List<List<String>> exportActors(){
        List<List<String>> returns = new ArrayList<>();
        HashSet<String> existing = new HashSet<>();
        for(int i = 0;i<this.actors.size();i++){
            actor a = this.actors.get(i);
            if(existing.contains(a.getName())){
                continue;
            }
            existing.add(a.getName());
            List<String> col = new ArrayList<>();
            col.add(a.getName());
            col.add(String.valueOf(a.getBirthYear()));
            returns.add(col);
        }
        return returns;
    }



    public void printAmount(){

        System.out.println("Total " + actors.size() + " actors");
        System.out.println("Good actors " + goodActors );
//        for(int i = 0; i < directors.size(); i++){
//            director d = directors.get(i);
//            d.printAmount();
//        }

    }
    public void runProgram(){
        this.parseDocument();
        this.printAmount();

    }

    public static void main(String[] args) {
        loadActors spe = new loadActors("./actors63.xml");
        spe.runProgram();

    }



}
