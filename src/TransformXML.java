/*
 * @authour Gongsheng Yuan
 */
import com.SaxHandler;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;

public class TransformXML {

    public static void main(String[] args) throws Exception{

        // Firstly, we should instance a SAXParserFactory object
        SAXParserFactory factory = SAXParserFactory.newInstance();

        try {
            //Secondly, we need create parser
            SAXParser parser = factory.newSAXParser();

            // Thirdly, we need new a class SaxHander which inherits from DefaultHandler. And we need to override some methods to process our problem.
            // And then we need new a Saxhandler object.
            SaxHandler handler = new SaxHandler();

            // Fourthly, we need to catch file which is need to be analyzed.
            //File file = new File("D:\\program\\Mine\\testxml.xml");
            File file = new File("D:\\program\\Data\\example(XML)\\exampleXML.xml");

            // Fifthly, we begin to analyze, send file to SaxHandler which is regarded as process class analyzing xml
            parser.parse(file, handler);

        }catch (ParserConfigurationException e){
            e.printStackTrace();
        }catch (SAXException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
