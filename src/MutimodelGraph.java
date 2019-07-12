///**
// * @ authour Gongsheng Yuan
// */
//
//import com.ElementJsonClass;
//import com.ProcessJson;
//import com.csvreader.CsvReader;
//import net.sf.json.JSONArray;
//import net.sf.json.JSONObject;
//import opennlp.tools.stemmer.snowball.SnowballStemmer;
//
//import java.io.*;
//import java.nio.charset.Charset;
//import java.util.*;
//
//
//public class MutimodelGraph {
//    public static void main(String[] args) throws IOException {
//
//        List<String> temp_wordSpace = new ArrayList<>();
//        Map<String, Integer> wordSpace = new HashMap<>();
//
//        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//        int nodeNumber = 0;
//        Map<String, Integer> positionInMatrix_Json = new HashMap<String, Integer>();
//        Map<String, String> nodeInformation = new HashMap<>();
//
//        ////////////////////////////////////////////For reading stop words//////////////////////////////////////////////
//        Set<String> stopwords = new HashSet<String>(); // for storing stop words
//        String stopwordFile = "D:\\program\\Data\\stopwords\\stopwords_en.txt";
//
//        //Read stop words from file into HashSet
//        try {
//            File file = new File(stopwordFile);
//            FileReader fileReader = new FileReader(file);
//            BufferedReader bufferedReader = new BufferedReader(fileReader);
//            String line = "";
//            while ((line = bufferedReader.readLine()) != null) {
//                stopwords.add(line);
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        System.out.println("stopwords");
//        System.out.println(stopwords);
//
//        //////////////////////////////////////////////read order json file////////////////////////////////////////////////////
//        List<ElementJsonClass> jsonClassList = new ArrayList<ElementJsonClass>();
//        ProcessJson json = new ProcessJson();
//        jsonClassList = json.readJsonFile();   //read json file, and store them in a list of json class defined by us.
//
////        System.out.println(jsonClassList.get(0).getOrderId());
////        System.out.println(jsonClassList.get(0).getPersonId());
////        System.out.println(jsonClassList.get(0).getOrderDate());
////        System.out.println(jsonClassList.get(0).getTotalPrice());
////        System.out.println(jsonClassList.get(0).getOrderLine());
//
//        Map<String, List<String>> order = new HashMap<String, List<String>>();
//
//
//        for (int i = 0; i < jsonClassList.size(); i++) {
//
//            List<String> map_value = new ArrayList<>();
//            ElementJsonClass tmp = jsonClassList.get(i);
//
//
//            ///每个元素是一个结点
//
//            String string = tmp.getOrderDate() + " ";
//            JSONArray orderLine = tmp.getOrderLine();
//
//            for (int j = 0; j < orderLine.size(); j++) {
//
//                JSONObject jb = (JSONObject) orderLine.get(j);
//
//                string += jb.get("title").toString() + " ";
//
//                string += jb.get("price").toString() + " ";
//
//                string += jb.get("brand").toString() + " ";
//
//            }
//            map_value.addAll(cleanAndStem(string, stopwords));
//
//            LinkedHashSet<String> set = new LinkedHashSet<String>(map_value.size());                    //remove duplicate
//            set.addAll(map_value);
//
//            temp_wordSpace.removeAll(set);
//            temp_wordSpace.addAll(set);                                                              // add to word space
//
//
//            order.put(tmp.getOrderId(), map_value);
//        }
//        System.out.println("order");
//        System.out.println(order);
//
//        //////////////////////////////////////////read person csv file////////////////////////////////////////////////
//        List<String[]> csvPersonList = new ArrayList<String[]>();
//
//        String filePath_Person = "D:\\program\\Data\\ParameterCuration\\person_0_0.csv";
//        CsvReader reader1 = new CsvReader(filePath_Person, '|', Charset.forName("GBK"));
//        //reader.readHeaders(); // This means that we need to skip header. If you need the header, just delete this sentence.
//        while (reader1.readRecord()) {            // read data line-by-line except header
//            csvPersonList.add(reader1.getValues());
//        }
//        reader1.close();
//
////        for (int r = 0; r < csvPersonList.size(); r++) {
////            for (int c = 0; c < csvPersonList.get(r).length; c++) {
////                String cell = csvPersonList.get(r)[c];
////                System.out.println(cell + "\t");
////            }
////
////        }
//
//        Map<String, List<String>> personHashMap = new HashMap<String, List<String>>();
//        //String[] person_attributes = csvPersonList.get(0);
//
//        for (int i = 1; i < csvPersonList.size(); i++) {
//
//            String first_cell = csvPersonList.get(i)[0];                                        //Get the the first value (person_id)
//            ArrayList<String> person_information = new ArrayList<>();
//            personHashMap.put(first_cell, person_information);
//
//            for (int j = 1; j < 5; j++) {                                                       // We only need to use the first five columns(attributes).
//
//                String cell = csvPersonList.get(i)[j];
//                List<String> person_information_list = cleanAndStem(cell, stopwords);
//
//
//                LinkedHashSet<String> set = new LinkedHashSet<String>(person_information_list.size());                    //remove duplicate
//                set.addAll(person_information_list);
//
//                temp_wordSpace.removeAll(set);                              // add to word space
//                temp_wordSpace.addAll(set);
//
//                personHashMap.get(first_cell).addAll(person_information_list);
//                //personHashMap.get(first_cell).add(cell);
//
//            }
//        }
//
//        csvPersonList.clear();                                                                  //Clear the list of Person
////        for (int c = 0; c < person_attributes.length; c++) {
////            String cell = person_attributes[c];
////            System.out.println(cell);
////        }
//
//        System.out.println("person");
//        System.out.println(personHashMap);
//
//        //////////////////////////////////////////read person_know_person csv file/////////////////////////////////////
//        List<String[]> csvPersonKnowPersonList = new ArrayList<String[]>();
//
//        String filePath_PersonKnowPerson = "D:\\program\\Data\\ParameterCuration\\person_knows_person_0_0.csv";
//        CsvReader reader3 = new CsvReader(filePath_PersonKnowPerson, '|', Charset.forName("GBK"));
//        //reader3.readHeaders(); // This means that we need to skip header. If you need the header, just delete this sentence.
//        while (reader3.readRecord()) {            // read data line-by-line except header
//            csvPersonKnowPersonList.add(reader3.getValues());
//        }
//        reader3.close();
//
////        System.out.println(csvPersonKnowPersonList.get(0).length);
////
////        for (int r = 0; r < csvPersonKnowPersonList.size(); r++) {
////            for (int c = 0; c < csvPersonKnowPersonList.get(r).length; c++) {
////                String cell = csvPersonKnowPersonList.get(r)[c];
////                System.out.println(cell + "\t");
////            }
////
////        }
//
//        Map<String, List<String>> friendRelationship = new HashMap<String, List<String>>();
//
//        for (int i = 1; i < csvPersonKnowPersonList.size(); i++) {
//            String first_cell = csvPersonKnowPersonList.get(i)[0];                          //Get the the first value
//            String second_cell = csvPersonKnowPersonList.get(i)[1];                         //Get the the second value
//
//            ////////////Add the second value into the list of first person////////////
//            if (!friendRelationship.containsKey(first_cell)) {
//                ArrayList<String> friends = new ArrayList<>();
//                friends.add(second_cell);
//                friendRelationship.put(first_cell, friends);
//            } else {
//                friendRelationship.get(first_cell).add(second_cell);
//            }
//            ////////////Add the first value into the list of second person////////////
//            if (!friendRelationship.containsKey(second_cell)) {
//                ArrayList<String> friends = new ArrayList<>();
//                friends.add(first_cell);
//                friendRelationship.put(second_cell, friends);
//            } else {
//                friendRelationship.get(second_cell).add(first_cell);
//            }
//        }
//
//
//        csvPersonKnowPersonList.clear();                                                    //Clear the list of PersonknowPerson
//
//        System.out.println("friendRelationship");
//        System.out.println(friendRelationship);
//
//        ////////////////////////////////////////////read feedback csv file////////////////////////////////////////////
//        List<String[]> csvFeedbackList = new ArrayList<String[]>();
//
//        String filePath_Feedback = "D:\\program\\Data\\ParameterCuration\\feedback.csv";
//        CsvReader reader2 = new CsvReader(filePath_Feedback, '|', Charset.forName("GBK"));
//        //reader2.readHeaders(); // This means that we need to skip header. If you need the header, just delete this sentence.
//        while (reader2.readRecord()) {            // read data line-by-line except header
//            csvFeedbackList.add(reader2.getValues());
//        }
//        reader2.close();
//
////        System.out.println(csvFeedbackList.get(0).length);
////
////        for (int i = 0; i < csvFeedbackList.size(); i++) {
////            for (int j = 0; j < csvFeedbackList.get(i).length; j++) {
////                String cell = csvFeedbackList.get(i)[j];
////                System.out.println(cell + "\t");
////            }
////
////        }
//
//        Map<String, Integer> feedback = new HashMap<String, Integer>();           //The key is productId+personId. The value is rating.
//
//        for (int i = 0; i < csvFeedbackList.size(); i++) {
//            String cell = "";
//            for (int j = 0; j < 2; j++) {
//                cell += csvFeedbackList.get(i)[j];
//            }
//            char ra = csvFeedbackList.get(i)[2].charAt(1);
//            String str = String.valueOf(ra);
//            int rate = Integer.parseInt(str);
//            feedback.put(cell, rate);
//
//            if (!temp_wordSpace.contains(str))                                  // add to word space
//                temp_wordSpace.addAll(cleanAndStem(str, stopwords));
//
//        }
//
//        System.out.println("feedback");
//        System.out.println(feedback);
//
//        ////////////////////////////////////////////////////////为了生成example,要删掉/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//        if (!temp_wordSpace.containsAll(cleanAndStem("person", stopwords)))                                  // add to word space
//            temp_wordSpace.addAll(cleanAndStem("person", stopwords));
//        if (!temp_wordSpace.containsAll(cleanAndStem("id", stopwords)))                                  // add to word space
//            temp_wordSpace.addAll(cleanAndStem("id", stopwords));
//        if (!temp_wordSpace.containsAll(cleanAndStem("friend", stopwords)))                                  // add to word space
//            temp_wordSpace.addAll(cleanAndStem("friend", stopwords));
//        if (!temp_wordSpace.containsAll(cleanAndStem("rate", stopwords)))                                  // add to word space
//            temp_wordSpace.addAll(cleanAndStem("rate", stopwords));
//
//
//        ////////////////////////////////////////////////////////为了生成example,要删掉/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//
//        //////////////////////////// Join: person -- personknowperson (person_graph) ///////////////////////////////////////
//
//        Map<String, List<String>> person_graph = new HashMap<String, List<String>>();           //The result of joining between person and personknowperson
//
//        for ( Map.Entry<String, List<String>> entry : personHashMap.entrySet() ) {
//            String map_key = entry.getKey();                                                        //5296
//            List<String> map_value = new ArrayList<>();
//            ////////////////////////////////////////////////////////为了生成example,要删掉/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//            map_value.add("person");
//            map_value.add("id");
//            ////////////////////////////////////////////////////////为了生成example,要删掉/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//            map_value.addAll(entry.getValue());                                                     //[Rafael, Oliveira, male, 1987-06-08]
//
//            ////////////////////////////////////////////////////////为了生成example,要删掉/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//            map_value.add("friend");
//            ////////////////////////////////////////////////////////为了生成example,要删掉/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//            List<String> friends = friendRelationship.get(map_key);                                 //5296=[5386, 5409, 5447, 5507, 8620, 2199023256462, 2199023256638, 4398046512650, 6597069771841, 6597069773471, 8796093032121, 13194139535588, 13194139541987, 13194139542240, 15393162796864, 15393162797836, 19791209301379, 19791209310563, 28587302326094, 30786325579314, 32985348844052]
//
////            System.out.println(map_key);
////            System.out.println(friends);
//
//            for (int i = 0; i < friends.size(); i++) {
//                String tmp_key = friends.get(i);
////                System.out.println(tmp_key);
//                List<String> tmp_value = personHashMap.get(tmp_key);
//                if (tmp_value != null) {
//                    map_value.addAll(tmp_value);
//                }
//            }
//
//            person_graph.put(map_key, map_value);
//
//        }
//
//
//        System.out.println("person_graph");
//        System.out.println(person_graph);
//    }
//
//
//    private static  List<String> cleanAndStem(String str, Set<String> stopwords) {
//
//        SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
//
//        List<String> list = new ArrayList<String>();
//
//        String lowerCase_str = str.toLowerCase();
//        String[] first_words = lowerCase_str.split("\\s+");
//
//        StringBuilder result = new StringBuilder();
//        for (String w : first_words){
//            if (!stopwords.contains(w)) {
//                result.append(w);
//                result.append(" ");
//            }
//
//        }
//
//
//        String clean = result.toString().trim();
//        if (clean.isEmpty()){
//            return list;
//        }
//
//
//        //\\p{Alnum} means digital or alphabetic character  (Regular expression in Java)
//        //[^abc] means any character except a, b and c.
//        //String clean = str.toLowerCase().replaceAll("[^\\p{Alnum}]", " ").replaceAll("\\s\\s+", " ");
//        // here we just split a word into two words.(like micro-robot becomes micro robot)
//
//        clean = clean.replaceAll("[^\\p{Alnum}]", " ").replaceAll("\\s\\s+", " ");
//
//        String[] second_words = clean.split("\\s+");
//
//
//        for (String w : second_words){
//            if (!stopwords.contains(w)) {
//                String string = stemmer.stem(w).toString();
//                list.add(string);
//            }
//        }
//
//        return list;
//    }
//
//}
