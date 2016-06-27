/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ContentType;

import com.google.firebase.database.DatabaseReference;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author hallm8
 */
public class Quizzes extends CSV {
    
    private String ref1;
    private String ref2;

    public Quizzes(String location, DatabaseReference ref) {
        super(location, ref);
        
        ref1 = new String();
        ref2 = new String();
    }

    /**
     *
     */
    @Override
    public void gatherBroken() {
        try {
            System.out.println("Gathering a quiz!");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder;
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(location);

            // Normalize the document.  
            doc.getDocumentElement().normalize();

            // Looks like a piece of cake!  mattext contains any body text.
            NodeList bodyText = doc.getElementsByTagName("mattext");

            NodeList title = doc.getElementsByTagName("assessment");

            String theTitle = "Question Database";

            if (title.getLength() > 0) {
                theTitle = title.item(0).getAttributes().getNamedItem("title").getTextContent();
            }
            String newName = theTitle.replace(".", " ");
                newName = newName.replace("$", " ");
                newName = newName.replace("[", " ");
                newName = newName.replace("]", " ");
                DatabaseReference newQuizRef = ref.child(newName);

            for (int i = 0; i < bodyText.getLength(); i++) {
                
                DatabaseReference quizQuestion = newQuizRef.child("Question " + i + 1);
                String text = bodyText.item(i).getTextContent();
                org.jsoup.nodes.Document jDoc = Jsoup.parse(text);
                Elements hrefs = jDoc.getElementsByTag("a");

                for (Element href : hrefs) {
                    if (href.attr("href").contains("http:")
                            || href.attr("href").contains("https:")
                            || href.attr("href").contains(".html")
                            || href.attr("href").contains("/d2l/lms/quizzing/")) {
                        // Do nothing.
                    } else {
                        numBroken++;
                        // With these three identifiers, we can have Andrew
                        // link directly to the question... which will save
                        // massive amounts of time.
                        quizQuestion.child("Identifier").child(bodyText.item(i).
                                getAttributes().getNamedItem("ident").
                                getTextContent().replace("OBJ_", ""));
                        
                        quizQuestion.child("Reference").child(bodyText.item(i).
                                getAttributes().getNamedItem("label").
                                getTextContent());
                    }
                }
            }

            if (numBroken > 0) {

                // Push to firebase
                

                //System.out.println(newName);
                DatabaseReference numBrokRef = newQuizRef.child("Number Broken");
                numBrokRef.setValue(numBroken);
                newQuizRef.child("checked").setValue("False");
                newQuizRef.child("Quiz Resource Number").setValue(identifier);
            }
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            System.out.println("Error! Unable to find item!");
            Logger.getLogger(Quizzes.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
