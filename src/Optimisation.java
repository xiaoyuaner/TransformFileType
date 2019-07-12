import com.ElementJsonClass;
import com.ProcessJsonofEcommence;
import com.csvreader.CsvReader;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

/**
 * @ authour Gongsheng Yuan
 */
public class Optimisation {
    public static void main(String[] args) throws FileNotFoundException {

        List<String[]> csvQueryHistory = new ArrayList<String[]>();
        try {

            String path_queryHistory = "D:\\program\\Data\\optimization dataset\\query history.txt";  //Experiment example
            CsvReader reader_queryhistory = new CsvReader(path_queryHistory, ' ', Charset.forName("UTF-8"));
            //reader3.readHeaders(); // This means that we need to skip header. If you need the header, just delete this sentence.
            while (reader_queryhistory.readRecord()) {            // read data line-by-line except header
                csvQueryHistory.add(reader_queryhistory.getValues());
            }
            reader_queryhistory.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < csvQueryHistory.size(); i++) {
            for (int j = 0; j < csvQueryHistory.get(i).length; j++) {
                System.out.println(csvQueryHistory.get(i)[j]);
            }
        }

        ////////////////////////////////////////////For reading stop words//////////////////////////////////////////////
        Set<String> stopwords = new HashSet<String>(); // for storing stop words

        try {
            String stopwordFile = "D:\\program\\Data\\stopwords\\stopwords_en.txt"; //Read stop words from file into HashSet
            File file = new File(stopwordFile);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = "";
            while ((line = bufferedReader.readLine()) != null){
                stopwords.add(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("stopwords");
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////




        List<String> schema = new ArrayList<>();                                    //For calculating schema weights
        Map<String, List<String>> schema_value = new HashMap<>();                   //For calculating value weights


        //int[][] assignments = hgAlgorithmAssignments(test3, "max");



        //////////////////////////////////////////////read order json file////////////////////////////////////////////////////
        String tmpStr = "order";
        schema.add(tmpStr);

        tmpStr = "order.order line.title";
        schema.add(tmpStr);
        schema_value.put(tmpStr, new ArrayList<String>());

//            tmpStr = "order.order line.price";
//            schema.add(tmpStr);
//            schema_value.put(tmpStr, new ArrayList<String>());

        tmpStr = "order.order line.brand";
        schema.add(tmpStr);
        schema_value.put(tmpStr, new ArrayList<String>());



        List<ElementJsonClass> jsonClassList = new ArrayList<>();
        ProcessJsonofEcommence json = new ProcessJsonofEcommence();               //file path in the ProcessJsonofEcommence.java
        jsonClassList = json.readJsonFile();                            //read json file, and store them in a list of json class defined by us.

        for (int i = 0; i < jsonClassList.size(); i++) {

            ElementJsonClass tmp = jsonClassList.get(i);
            JSONArray orderLine = tmp.getOrderLine();

            for(int j = 0; j < orderLine.size(); j++){

                JSONObject jb = (JSONObject)orderLine.get(j);

                String string = jb.get("title").toString();
                List<String> map_value = new ArrayList<>();
                map_value.addAll(cleanAndStem(string, stopwords));
                LinkedHashSet<String> set_title = new LinkedHashSet<String>(map_value.size());                    //remove duplicate
                set_title.addAll(map_value);
                List<String> temp_title = schema_value.get("order.order line.title");
                temp_title.removeAll(set_title);
                temp_title.addAll(set_title);
                schema_value.put("order.order line.title", temp_title);
                map_value.clear();


                string = jb.get("brand").toString();
                map_value.addAll(cleanAndStem(string, stopwords));
                LinkedHashSet<String> set_brand = new LinkedHashSet<String>(map_value.size());                    //remove duplicate
                set_brand.addAll(map_value);
                List<String> temp_brand = schema_value.get("order.order line.brand");
                temp_brand.removeAll(set_brand);
                temp_brand.addAll(set_brand);
                schema_value.put("order.order line.brand", temp_brand);
                map_value.clear();

            }
        }
        jsonClassList.clear();                                                                  //Clear the list of Person
        System.out.println("order");


        //////////////////////////////////////////////read person////////////////////////////////////////////////////
        tmpStr = "person";
        schema.add(tmpStr);

        tmpStr = "person.first name";
        schema.add(tmpStr);
        schema_value.put(tmpStr, new ArrayList<String>());

        tmpStr = "person.last name";
        schema.add(tmpStr);
        schema_value.put(tmpStr, new ArrayList<String>());

        tmpStr = "person.gender";
        schema.add(tmpStr);
        schema_value.put(tmpStr, new ArrayList<String>());


        List<String[]> csvPersonList = new ArrayList<String[]>();
        try {
            String filePath_Person = "D:\\program\\Data\\ParameterCuration\\original\\person.csv";
            CsvReader reader1 = new CsvReader(filePath_Person, '|', Charset.forName("UTF-8"));
            //reader.readHeaders(); // This means that we need to skip header. If you need the header, just delete this sentence.
            while (reader1.readRecord()) {            // read data line-by-line except header
                csvPersonList.add(reader1.getValues());
            }
            reader1.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] person_attributes = csvPersonList.get(0);
        for (int i = 1; i < csvPersonList.size(); i++) {
            for (int j = 1; j < 4; j++) {                                                       // We only need to use the first three columns(attributes).
                String cell = person_attributes[j];
                String string = csvPersonList.get(i)[j];
                List<String> map_value = new ArrayList<>();

                switch (cell) {
                    case "firstName":
                        map_value.addAll(cleanAndStem(string, stopwords));
                        LinkedHashSet<String> set_firstName = new LinkedHashSet<String>(map_value.size());                    //remove duplicate
                        set_firstName.addAll(map_value);
                        List<String> temp_firstName = schema_value.get("person.first name");
                        temp_firstName.removeAll(set_firstName);
                        temp_firstName.addAll(set_firstName);
                        schema_value.put("person.first name", temp_firstName);
                        break;
                    case "lastName":
                        map_value.addAll(cleanAndStem(string, stopwords));
                        LinkedHashSet<String> set_lastName = new LinkedHashSet<String>(map_value.size());                    //remove duplicate
                        set_lastName.addAll(map_value);
                        List<String> temp_lastName = schema_value.get("person.last name");
                        temp_lastName.removeAll(set_lastName);
                        temp_lastName.addAll(set_lastName);
                        schema_value.put("person.last name", temp_lastName);
                        break;
                    case "gender":
                        map_value.addAll(cleanAndStem(string, stopwords));
                        LinkedHashSet<String> set_gender = new LinkedHashSet<String>(map_value.size());                    //remove duplicate
                        set_gender.addAll(map_value);
                        List<String> temp_gender = schema_value.get("person.gender");
                        temp_gender.removeAll(set_gender);
                        temp_gender.addAll(set_gender);
                        schema_value.put("person.gender", temp_gender);
                        break;
                }
            }
        }

        csvPersonList.clear();                                                                  //Clear the list of Person
        System.out.println("person");

        //////////////////////////////////////////read person_know_person csv file/////////////////////////////////////
        tmpStr = "friend";
        schema.add(tmpStr);
        System.out.println("friendRelationship");
        //System.out.println(friendRelationship);


        ////////////////////////////////////////////read feedback csv file////////////////////////////////////////////
        tmpStr = "feedback";
        schema.add(tmpStr);

        tmpStr = "feedback.rate";
        schema.add(tmpStr);
        schema_value.put(tmpStr, new ArrayList<String>());

        String feedbackRate = "perfect great good bad poor";
        List<String> map_value = new ArrayList<>();
        map_value.addAll(cleanAndStem(feedbackRate, stopwords));
        //LinkedHashSet<String> set_feedback = new LinkedHashSet<String>(map_value.size());                    //remove duplicate
        //set_feedback.addAll(map_value);
        //List<String> temp_feedback = schema_value.get("feedback.rate");
        //temp_feedback.removeAll(set_feedback);
        //temp_feedback.addAll(set_feedback);
        schema_value.put("feedback.rate", map_value);

        System.out.println("feedback");
        //System.out.println(feedback);



        ////////////////////////////////////////////////////////////////////////////////
        //column size is chema.size() + schema_value.size()

        for (int j = 0; j < csvQueryHistory.size(); j++) {
            double[][] schemaWeightMatrix = new double[csvQueryHistory.get(j).length][schema.size()];	//Row is the number of keywords, column is the number of schema.

            //schema Weight Matrix assignment
            for (int k = 0; k < schemaWeightMatrix.length; k++) {                                     //每个query查询历史中有schemaWeightMatrix.length个关键字
                for (int l = 0; l < schemaWeightMatrix[k].length; l++) {                              //each keyword 需要跟每个schema进行计算距离

                    schemaWeightMatrix[k][l] = schemaWeightFunction(csvQueryHistory.get(j)[k], schema.get(l));
                    // the first parameter is keyword (第j行中第k个的关键字), the second parameter is schema


                }
            }       //weightMatrix assignment

        }   //loop about csvQueryHistory


        for (int i = 0; i < csvQueryHistory.size(); i++) {
            for (int j = 0; j < csvQueryHistory.get(i).length; j++) {
                System.out.println(csvQueryHistory.get(i)[j]);
            }
        }







    }///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static double schemaWeightFunction(String s, String s1) {


    }


    private static  List<String> cleanAndStem(String str, Set<String> stopwords) {

        SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);

        List<String> list = new ArrayList<String>();

        String lowerCase_str = str.toLowerCase();
        String[] first_words = lowerCase_str.split("\\s+");

        StringBuilder result = new StringBuilder();
        for (String w : first_words){
            if (!stopwords.contains(w)) {
                result.append(w);
                result.append(" ");
            }
        }


        String clean = result.toString().trim();
        if (clean.isEmpty()){
            return list;
        }


        //\\p{Alnum} means digital or alphabetic character  (Regular expression in Java)
        //[^abc] means any character except a, b and c.
        //String clean = str.toLowerCase().replaceAll("[^\\p{Alnum}]", " ").replaceAll("\\s\\s+", " ");
        // here we just split a word into two words.(like micro-robot becomes micro robot)

        clean = clean.replaceAll("[^\\p{Alnum}]", " ").replaceAll("\\s\\s+", " ");

        String[] second_words = clean.split("\\s+");


        for (String w : second_words){
            if (!stopwords.contains(w)) {
                String string = stemmer.stem(w).toString();
                list.add(string);
            }
        }

        return list;
    }
}