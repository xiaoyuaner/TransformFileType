/**
 * @ authour Gongsheng Yuan
 */
import com.ElementJsonClass;
import com.ProcessJson;
import com.csvreader.CsvReader;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import opennlp.tools.stemmer.snowball.SnowballStemmer;
import org.apache.commons.math3.linear.*;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
public class OutputWordSpaceInvertedWordandDocument {
    public static int MatrixDimension = 50;

    public static void main(String[] args) throws IOException {
        //Map<String, List<String>> order = new HashMap<String, List<String>>();
        //Map<String, List<String>> personHashMap = new HashMap<String, List<String>>();
        //Map<String, List<String>> person_graph = new HashMap<String, List<String>>();
        //Map<String, List<String>> person_graph_order = new HashMap<String, List<String>>();
        //Map<String, List<String>> person_order_feedback = new HashMap<>();

        long startTime = System.currentTimeMillis();   //获取开始时间

        List<String> temp_wordSpace = new ArrayList<>();
        Map<String, Integer> wordSpace = new HashMap<>();

        String outFolder_pathName_invertedListWord = "D:\\program\\Data\\wordspaceinvertedwordanddocument\\invertedListWord";
        String outFolder_pathName_invertedListDensityMatrix = "D:\\program\\Data\\wordspaceinvertedwordanddocument\\invertedListDensityMatrix";
        String outFolder_pathName_invertedListDocument = "D:\\program\\Data\\wordspaceinvertedwordanddocument\\invertedListDocument";
        String outFolder_pathName_wordSpace = "D:\\program\\Data\\wordspaceinvertedwordanddocument\\wordSpace";


        ////////////////////////////////////////////For reading stop words//////////////////////////////////////////////
        Set<String> stopwords = new HashSet<String>();      // for storing stop words
        String stopwordFile = "D:\\program\\Data\\stopwords\\stopwords_en.txt";

        //Read stop words from file into HashSet
        try {
            File file = new File(stopwordFile);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stopwords.add(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("stop words");
        //System.out.println(stopwords);

        //////////////////////////////////////////////read order json file////////////////////////////////////////////////////
        List<ElementJsonClass> jsonClassList = new ArrayList<ElementJsonClass>();
        ProcessJson json = new ProcessJson();
        jsonClassList = json.readJsonFile();   //read json file, and store them in a list of json class defined by us.


        Map<String, List<String>> order = new HashMap<String, List<String>>();


        for (int i = 0; i < jsonClassList.size(); i++) {

            List<String> map_value = new ArrayList<>();
            ElementJsonClass tmp = jsonClassList.get(i);

            //String string = "order date " + tmp.getOrderDate() + " ";
            //String string = "personid " + tmp.getPersonId() + " ";
            String string = "Orderline ";

            //string = string + "total price " + tmp.getTotalPrice().toString() + " ";   //add TotalPrice

            JSONArray orderLine = tmp.getOrderLine();

            for (int j = 0; j < orderLine.size(); j++) {

                JSONObject jb = (JSONObject) orderLine.get(j);

                string += "title " + jb.get("title").toString() + " ";

                //string += "price " + jb.get("price").toString() + " ";

                string += "brand " + jb.get("brand").toString() + " ";

            }
            map_value.addAll(cleanAndStem(string, stopwords));

            LinkedHashSet<String> set = new LinkedHashSet<String>(map_value.size());                       //remove duplicate
            set.addAll(map_value);

            temp_wordSpace.removeAll(set);
            temp_wordSpace.addAll(set);                                                              // add to word space


            order.put(tmp.getOrderId(), map_value);
        }
        System.out.println("order");
        //System.out.println(order);

        //////////////////////////////////////////read person csv file////////////////////////////////////////////////
        List<String[]> csvPersonList = new ArrayList<String[]>();

        String filePath_Person = "D:\\program\\Data\\ParameterCuration\\original\\person_0_0.csv";
        CsvReader reader1 = new CsvReader(filePath_Person, '|', Charset.forName("GBK"));
        //reader.readHeaders(); // This means that we need to skip header. If you need the header, just delete this sentence.
        while (reader1.readRecord()) {            // read data line-by-line except header
            csvPersonList.add(reader1.getValues());
        }
        reader1.close();

//        for (int r = 0; r < csvPersonList.size(); r++) {
//            for (int c = 0; c < csvPersonList.get(r).length; c++) {
//                String cell = csvPersonList.get(r)[c];
//                System.out.println(cell + "\t");
//            }
//
//        }

        Map<String, List<String>> personHashMap = new HashMap<String, List<String>>();                           //{33=[33], 56=[56], 101=[101], 145=[145]}
        String[] person_attributes = csvPersonList.get(0);

        for (int i = 1; i < csvPersonList.size(); i++) {

            String first_cell = csvPersonList.get(i)[0];                                        //Get the the first value (person_id)
            ArrayList<String> person_information = new ArrayList<>();
            personHashMap.put(first_cell, person_information);

            for (int j = 1; j < 4; j++) {                                                       // We only need to use the first four columns(attributes).

                String cell = person_attributes[j] + " " + csvPersonList.get(i)[j];             //attribute name + attribute value
                List<String> person_information_list = cleanAndStem(cell, stopwords);


                LinkedHashSet<String> set = new LinkedHashSet<String>(person_information_list.size());                    //remove duplicate
                set.addAll(person_information_list);

                temp_wordSpace.removeAll(set);                                                                       // add to word space
                temp_wordSpace.addAll(set);

                personHashMap.get(first_cell).addAll(person_information_list);
                //personHashMap.get(first_cell).add(cell);

            }
        }

        csvPersonList.clear();                                                                  //Clear the list of Person
//        for (int c = 0; c < person_attributes.length; c++) {
//            String cell = person_attributes[c];
//            System.out.println(cell);
//        }

        System.out.println("person");
        //System.out.println(personHashMap);                                                          //{1=[id, 1, custom, id, 33, total, price, 135, item, product, id, 85, brand, nike, product, id, 86, brand, adida]}

        //////////////////////////////////////////read person_know_person csv file/////////////////////////////////////
        List<String[]> csvPersonKnowPersonList = new ArrayList<String[]>();

        String filePath_PersonKnowPerson = "D:\\program\\Data\\ParameterCuration\\original\\person_knows_person_0_0.csv";
        CsvReader reader3 = new CsvReader(filePath_PersonKnowPerson, '|', Charset.forName("GBK"));
        //reader3.readHeaders(); // This means that we need to skip header. If you need the header, just delete this sentence.
        while (reader3.readRecord()) {            // read data line-by-line except header
            csvPersonKnowPersonList.add(reader3.getValues());
        }
        reader3.close();

//        System.out.println(csvPersonKnowPersonList.get(0).length);
//
//        for (int r = 0; r < csvPersonKnowPersonList.size(); r++) {
//            for (int c = 0; c < csvPersonKnowPersonList.get(r).length; c++) {
//                String cell = csvPersonKnowPersonList.get(r)[c];
//                System.out.println(cell + "\t");
//            }
//
//        }

        Map<String, List<String>> friendRelationship = new HashMap<String, List<String>>();                    //{33=[56], 56=[33, 101], 101=[56, 145], 145=[101]}

        for (int i = 1; i < csvPersonKnowPersonList.size(); i++) {
            String first_cell = csvPersonKnowPersonList.get(i)[0];                          //Get the the first value
            String second_cell = csvPersonKnowPersonList.get(i)[1];                         //Get the the second value

            ////////////Add the second value into the list of first person////////////
            if (!friendRelationship.containsKey(first_cell)) {
                ArrayList<String> friends = new ArrayList<>();
                friends.add(second_cell);
                friendRelationship.put(first_cell, friends);
            } else {
                friendRelationship.get(first_cell).add(second_cell);
            }

            ////////////Add the first value into the list of second person////////////
            if (!friendRelationship.containsKey(second_cell)) {
                ArrayList<String> friends = new ArrayList<>();
                friends.add(first_cell);
                friendRelationship.put(second_cell, friends);
            } else {
                friendRelationship.get(second_cell).add(first_cell);
            }
        }


        csvPersonKnowPersonList.clear();                                                    //Clear the list of PersonknowPerson

        System.out.println("friendRelationship");
        //System.out.println(friendRelationship);

        ////////////////////////////////////////////read feedback csv file////////////////////////////////////////////
        List<String[]> csvFeedbackList = new ArrayList<String[]>();                             //{8656=4, 8533=5, 87101=4, 87145=3, 88145=5, 89145=4} 8656 the key is productId+personId. The value is rating.

        String filePath_Feedback = "D:\\program\\Data\\ParameterCuration\\original\\feedback2.csv";
        CsvReader reader2 = new CsvReader(filePath_Feedback, '|', Charset.forName("GBK"));
        //reader2.readHeaders(); // This means that we need to skip header. If you need the header, just delete this sentence.
        while (reader2.readRecord()) {            // read data line-by-line except header
            csvFeedbackList.add(reader2.getValues());
        }
        reader2.close();

//        System.out.println(csvFeedbackList.get(0).length);
//
//        for (int i = 0; i < csvFeedbackList.size(); i++) {
//            for (int j = 0; j < csvFeedbackList.get(i).length; j++) {
//                String cell = csvFeedbackList.get(i)[j];
//                System.out.println(cell + "\t");
//            }
//
//        }

        Map<String, Integer> feedback = new HashMap<String, Integer>();           //The key is productId+personId. The value is rating.

        for (int i = 0; i < csvFeedbackList.size(); i++) {
            String cell = "";
            for (int j = 0; j < 2; j++) {
                cell += csvFeedbackList.get(i)[j];
            }
            char ra = csvFeedbackList.get(i)[2].charAt(0);           //rating

            String str = String.valueOf(ra);
            int rate = Integer.parseInt(str);
            feedback.put(cell, rate);

            if (!temp_wordSpace.contains(str))                            // add to word space, this is number, we don't need stem, just to test whether temp word space contains it. If it is word, we need stem first.
                temp_wordSpace.addAll(cleanAndStem(str, stopwords));

        }

        System.out.println("feedback");
        //System.out.println(feedback);


        //////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //String relation_word = cleanAndStem("relation", stopwords).get(0);
        //String graph_word = cleanAndStem("graph", stopwords).get(0);
        //String jason_word = cleanAndStem("jason", stopwords).get(0);
        String rate_word = cleanAndStem("rate", stopwords).get(0);
        String friend_word = cleanAndStem("friend", stopwords).get(0);


        /*if (!temp_wordSpace.contains(relation_word))         // add to word space.
            temp_wordSpace.add(relation_word);
        if (!temp_wordSpace.contains(graph_word))         // add to word space.
            temp_wordSpace.add(graph_word);
        if (!temp_wordSpace.contains(jason_word))         // add to word space.
            temp_wordSpace.add(jason_word);*/
        if (!temp_wordSpace.contains(rate_word))         // add to word space. Feedback file hasn't attribute name. So we need add the attribute "rate" into word space.
            temp_wordSpace.add(rate_word);
        if (!temp_wordSpace.contains(friend_word))
            temp_wordSpace.add(friend_word);
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////


        //////////////////////////// Join: person -- personknowperson (person_graph) ///////////////////////////////////////

        Map<String, List<String>> person_graph = new HashMap<String, List<String>>();           //The result of joining between person and personknowperson

        for (Map.Entry<String, List<String>> entry : personHashMap.entrySet()) {
            String map_key = entry.getKey();                                                        //5296
            List<String> map_value = new ArrayList<>();
            map_value.addAll(entry.getValue());                                                     //[Rafael, Oliveira, male, 1987-06-08]


            List<String> friends = friendRelationship.get(map_key);                                 //5296=[5386, 5409, 5447, 5507, 8620, 2199023256462, 2199023256638, 4398046512650, 6597069771841, 6597069773471, 8796093032121, 13194139535588, 13194139541987, 13194139542240, 15393162796864, 15393162797836, 19791209301379, 19791209310563, 28587302326094, 30786325579314, 32985348844052]

//            System.out.println(map_key);
//            System.out.println(friends);
            if (friends != null) {

                map_value.add(friend_word);

                for (int i = 0; i < friends.size(); i++) {
                    String tmp_key = friends.get(i);
//                System.out.println(tmp_key);
                    List<String> tmp_value = personHashMap.get(tmp_key);
                    if (tmp_value != null) {
                        map_value.addAll(tmp_value);
                    } //if
                } //for
            } //if


            person_graph.put(map_key, map_value);

        }


        System.out.println("person_graph");
        //System.out.println(person_graph);                                                       //{33=[person, id, 33, friend, 56], 56=[person, id, 56, friend, 33, 101], 101=[person, id, 101, friend, 56, 145], 145=[person, id, 145, friend, 101]}


        ////////////////////////////////// Join: person_graph -- order  /////////////////////////////////////////////

        Map<String, List<String>> person_graph_order = new HashMap<String, List<String>>();           //The result of joining between persongraph and jason

        //    for ( Map.Entry<String, List<String>> entry : person_graph.entrySet() ) {
        //        String map_key = entry.getKey();                                                        //5296
        //        List<String> map_value = new ArrayList<>();
        //[Rafael, Oliveira, male, 1987-06-08]

        //map_value.add(jason_word);                                       // For showing in the result. ////////////////////////////////////////////////////////////////////////addddddd word

        for (int i = 0; i < jsonClassList.size(); i++) {
            ElementJsonClass tmp = jsonClassList.get(i);
//                        if (tmp.getPersonId().equals(map_key)) {
//                    String string = tmp.getOrderDate() + " ";                       //order date
//                    JSONArray orderLine = tmp.getOrderLine();
//
//                    for(int j = 0; j < orderLine.size(); j++){
//
//                        JSONObject jb = (JSONObject)orderLine.get(j);
//
//                        string += jb.get("title").toString() + " ";                 //title
//
//                        string += jb.get("price").toString() + " ";                 //price
//
//                        string += jb.get("brand").toString() + " ";                 //brand
//
//                    }
//                    map_value.addAll(cleanAndStem(string, stopwords));

            List<String> map_value = new ArrayList<>();

            String key_string = tmp.getPersonId().toString();
            map_value.addAll(person_graph.get(key_string));

            key_string = tmp.getOrderId().toString();                                //Map<String, List<String>> order = new HashMap<String, List<String>>();
            map_value.addAll(order.get(key_string));


            //map_value.add(graph_word);                                       // For showing in the result. ////////////////////////////////////////////////////////////////////////addddddd word

            if (!map_value.isEmpty()) {
                person_graph_order.put(key_string, map_value);
            }
        }

        System.out.println("person_graph_order");
        //System.out.println(person_graph_order);

        ////////////////////////////////// Join: order -- feedback /////////////////////////////////////////////

        List<String[]> order_feedback = new ArrayList<>();           //The result of joining between order and feedback


        for (int i = 0; i < jsonClassList.size(); i++) {
            ElementJsonClass tmp = jsonClassList.get(i);

            String orderId = tmp.getOrderId().toString();                                    //order id
            String personId = tmp.getPersonId().toString();                                    //person id
            //String orderDate = tmp.getOrderDate().toString();                                  //order date
            //String totalPrice = tmp.getTotalPrice().toString();
            JSONArray orderLine = tmp.getOrderLine();

            String[] strings = new String[3];                            //strings[0] = personId;  strings[1] = content of order; strings[3] = orderId
            strings[0] = personId;
            strings[1] = "";
            strings[2] = orderId;

            int flag = 0;                                              //Test whether this is join

            for (int j = 0; j < orderLine.size(); j++) {

                JSONObject jb = (JSONObject) orderLine.get(j);

                String asin = jb.get("asin").toString();                     //product id

                String key = asin + personId;                               //We need to know that not all the products are rated.

                String title = jb.get("title").toString();                      //title

                //String price = jb.get("price").toString();                      //price

                String brand = jb.get("brand").toString();                      //brand

                if (feedback.get(key) != null) {                           //We need to know that not all the products are rated.

                    flag = 1;                                                       //表示有连接存在 ////这个过程避免order里面只有评价和商品被列出，没有被评价的商品没有被列出，使得order不完整

                    //strings[1] += "product" + " " + "id" + asin + " " + "brand" + " " + brand + " " + "custom" + " " + "id" + " " + personId + " " + "product" + " " + "id" + " " + asin + " " + "rate" + " " + String.valueOf(feedback.get(key)) + " ";

                    //strings[1] += "title " + title + " " + "price " + price + " " + "brand " + brand + " ";
                    strings[1] += "title " + title + " " + "brand " + brand + " ";

                    //strings[1] += relation_word + " ";                              // For showing in the result. ////////////////////////////////////////////////////////////////////////addddddd word
                    strings[1] += "rate " + String.valueOf(feedback.get(key)) + " ";

                } else {
                    //strings[1] += "title " + title + " " + "price " + price + " " + "brand " + brand + " ";
                    strings[1] += "title " + title + " " + "brand " + brand + " ";
                }

            }

            if (flag == 1) {
                //String ss = "total" + " " + "price" + " " + tmp.getTotalPrice().toString() + " " + "item" + " " + strings[1];
                //String ss = jason_word + " " + "order date " + orderDate + " " + "total price " + totalPrice + " " + "item " + strings[1];   // For showing in the result. ////////////////////////////////////////////////////////////////////////addddddd word
                //String ss = "personid " + personId + " " + "Orderline " + strings[1];
                String ss = "Orderline " + strings[1];
                strings[1] = ss;
                order_feedback.add(strings);     //strings will be dealt (stem) in the Join(person--order--feedback)
            }

        }

        System.out.println("order_feedback");
//        for (int i = 0; i < order_feedback.size(); i++) {
//            String cell1 = order_feedback.get(i)[0];
//            String cell2 = order_feedback.get(i)[1];
//            String cell3 = order_feedback.get(i)[2];
//            System.out.println(cell1 + " " + cell2 + " " + cell3);                                     //id 1 custom id 33 total price 135 item product id 85 brand nike product id 86 brand adida custom id 33 product id 85 rate 5
//        }

        ////////////////////////////////// Join: person -- order -- feedback /////////////////////////////////////////////
        /////person_order_feedback


        Map<String, List<String>> person_order_feedback = new HashMap<>();              //{orderId=[id, 1, custom, id, 33, total, price, 135, item, product, id, 85, brand, nike, product, id, 86, brand, adida, custom, id, 33, product, id, 85, rate, 5, person, id, 33, friend, 56]}

        for (int i = 0; i < order_feedback.size(); i++) {
            String personId = order_feedback.get(i)[0];

            List<String> person = person_graph.get(personId);
            if (person != null) {
                //person_order_feedback.addAll(person);
                List<String> tmp_list = new ArrayList<>();

                //tmp_list.add(jason_word);                               // For showing in the result. ////////////////////////////////////////////////////////////////////////addddddd word
                tmp_list.addAll(cleanAndStem(order_feedback.get(i)[1], stopwords));


                LinkedHashSet<String> set = new LinkedHashSet<String>(tmp_list.size());                       //remove duplicate
                set.addAll(tmp_list);

                temp_wordSpace.removeAll(set);
                temp_wordSpace.addAll(set);                                                              // add to word space


                //tmp_list.add(relation_word);                               // For showing in the result. ////////////////////////////////////////////////////////////////////////addddddd word
                tmp_list.addAll(person);

                person_order_feedback.put(order_feedback.get(i)[2], tmp_list);
            }

        }

        System.out.println("person_order_feedback");
        //System.out.println(person_order_feedback);

        order_feedback.clear();//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////clear
        jsonClassList.clear();//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////clear

        int wordSpace_size = temp_wordSpace.size();                     //The size of word space

        for (int i = 0; i < wordSpace_size; i++) {
            String string = temp_wordSpace.get(i);
            wordSpace.put(string, i + 1);                                    //用二进制表示的时候，是从 1 开始标注, 而不是0，为了避免 count 为0.

        }


        //temp_wordSpace.clear();

        System.out.println("The size of word space");
        System.out.println(wordSpace_size);
        System.out.println(wordSpace);


        //////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////        Constructing density matrix        /////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //////////////////////                         Output                          ///////////////////////////////
        //////////////////////   Map<Integer, RealMatrix> inverted_list_densityMatrix  ///////////////////////////////
        //////////////////////   Map<Integer, List<String>>  inverted_list_document    ///////////////////////////////
        //////////////////////   Map<String, List<Integer>> inverted_list_word         ///////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////   After constructing, we output it to save space.


        Map<Integer, RealMatrix> inverted_list_densityMatrix = new HashMap<>();                     //"Integer" for the No. of density matrices, "List" for density matrices about the specified No..
        Map<Integer, List<String>> inverted_list_document = new HashMap<>();                       //"Integer" for the No. of document, "List" for document about the specified No..


        CreateFolder(outFolder_pathName_invertedListWord);
        CreateFolder(outFolder_pathName_invertedListDensityMatrix);
        CreateFolder(outFolder_pathName_invertedListDocument);
        CreateFolder(outFolder_pathName_wordSpace);


        //////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////      Constructing word inverted list      /////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////

        for (Map.Entry<String, Integer> entry : wordSpace.entrySet()) {                          //Initiate inverted list of word.

            String file_name = outFolder_pathName_invertedListWord + "\\" + entry.getKey();
            OutputInvertedListWord(file_name);
            System.out.println(entry.getKey());

        }



        ////////////////////////////////////////////////////////////////////output order

        int count = 0;                                                                                                      //The No. of density matrices and document.


        for (Map.Entry<String, List<String>> entry : order.entrySet()) {

            List<String> map_value = entry.getValue();

            inverted_list_document.put(count, map_value);                                                                   // Add the document into the inverted list.


//            List<RealMatrix> matrices = TransformSingleProjector(wordSpace, map_value);                                   //Store matrices of single word and dependencies.
//            List<RealMatrix> matrices_dependencies = TransformDependentProjectors(wordSpace, map_value);
//            if (matrices_dependencies.size() > 0) {
//                matrices.addAll(matrices_dependencies);
//                matrices_dependencies.clear();
//            }
//            RealMatrix density_matrix = DensityMatrix(matrices, wordSpace_size);
//            inverted_list_densityMatrix.put(count, density_matrix);                                                         // Add the density matrix into the inverted list.
//            matrices.clear();
//
//            OutputDensityMatrix(outFolder_pathName_invertedListDensityMatrix, inverted_list_densityMatrix);
//            inverted_list_densityMatrix.clear();        // for saving space

            OutputDocument(outFolder_pathName_invertedListDocument, inverted_list_document);


            for (Map.Entry<Integer, List<String>> entry1 : inverted_list_document.entrySet()) {

                int key1 = entry1.getKey();
                List<String> value1 = entry1.getValue();

                int size1 = value1.size();

                for (int i = 0; i < size1; i++) {
                    //inverted_list_word.get(value1.get(i)).add(key1);

                    String file_name = outFolder_pathName_invertedListWord + "\\" + value1.get(i);
                    OutputInvertedListWord1(file_name, key1);
                }
            }


            inverted_list_document.clear();

            System.out.println("输出order " + String.valueOf(count));
            count++;

        }

        order.clear();


        //////////////////////////////////////////////////////////////////output person_graph
        for (Map.Entry<String, List<String>> entry : person_graph.entrySet()) {

            List<String> map_value = entry.getValue();

            inverted_list_document.put(count, map_value);                                                                   // Add the document into the inverted list.

//            List<RealMatrix> matrices = TransformSingleProjector(wordSpace, map_value);                                   //Store matrices of single word and dependencies.
//            List<RealMatrix> matrices_dependencies = TransformDependentProjectors(wordSpace, map_value);
//            if (matrices_dependencies.size() > 0) {
//                matrices.addAll(matrices_dependencies);
//                matrices_dependencies.clear();
//            }
//
//            RealMatrix density_matrix = DensityMatrix(matrices, wordSpace_size);
//            inverted_list_densityMatrix.put(count, density_matrix);                                                         // Add the density matrix into the inverted list.
//            matrices.clear();
//
//            OutputDensityMatrix(outFolder_pathName_invertedListDensityMatrix, inverted_list_densityMatrix);
//            inverted_list_densityMatrix.clear();        // for saving space
            OutputDocument(outFolder_pathName_invertedListDocument, inverted_list_document);

            for (Map.Entry<Integer, List<String>> entry1 : inverted_list_document.entrySet()) {

                int key1 = entry1.getKey();
                List<String> value1 = entry1.getValue();

                int size1 = value1.size();

                for (int i = 0; i < size1; i++) {
                    //inverted_list_word.get(value1.get(i)).add(key1);

                    String file_name = outFolder_pathName_invertedListWord + "\\" + value1.get(i);
                    OutputInvertedListWord1(file_name, key1);
                }
            }

            inverted_list_document.clear();
            System.out.println("输出 person_graph" + String.valueOf(count));
            count++;

        }

        person_graph.clear();


        //////////////////////////////////////////////////////////////////output person_graph_order
        for (Map.Entry<String, List<String>> entry : person_graph_order.entrySet()) {

            List<String> map_value = entry.getValue();

            inverted_list_document.put(count, map_value);                                                                   // Add the document into the inverted list.

//            List<RealMatrix> matrices = TransformSingleProjector(wordSpace, map_value);                                   //Store matrices of single word and dependencies.
//            List<RealMatrix> matrices_dependencies = TransformDependentProjectors(wordSpace, map_value);
//            if (matrices_dependencies.size() > 0) {
//                matrices.addAll(matrices_dependencies);
//                matrices_dependencies.clear();
//            }
//
//
//            RealMatrix density_matrix = DensityMatrix(matrices, wordSpace_size);
//            inverted_list_densityMatrix.put(count, density_matrix);                                                         // Add the density matrix into the inverted list.
//            matrices.clear();
//
//            OutputDensityMatrix(outFolder_pathName_invertedListDensityMatrix, inverted_list_densityMatrix);
//            inverted_list_densityMatrix.clear();     // for saving space
            OutputDocument(outFolder_pathName_invertedListDocument, inverted_list_document);

            for (Map.Entry<Integer, List<String>> entry1 : inverted_list_document.entrySet()) {

                int key1 = entry1.getKey();
                List<String> value1 = entry1.getValue();

                int size1 = value1.size();

                for (int i = 0; i < size1; i++) {
                    //inverted_list_word.get(value1.get(i)).add(key1);

                    String file_name = outFolder_pathName_invertedListWord + "\\" + value1.get(i);
                    OutputInvertedListWord1(file_name, key1);
                }
            }

            inverted_list_document.clear();
            System.out.println("输出person_graph_order " + String.valueOf(count));
            count++;

        }

        person_graph_order.clear();


        //////////////////////////////////////////////////////////////////output person_order_feedback
        for (Map.Entry<String, List<String>> entry : person_order_feedback.entrySet()) {

            List<String> map_value = entry.getValue();

            inverted_list_document.put(count, map_value);                                                                   // Add the document into the inverted list.

//            List<RealMatrix> matrices = TransformSingleProjector(wordSpace, map_value);                                   //Store matrices of single word and dependencies.
//            List<RealMatrix> matrices_dependencies = TransformDependentProjectors(wordSpace, map_value);
//            if (matrices_dependencies.size() > 0) {
//                matrices.addAll(matrices_dependencies);
//                matrices_dependencies.clear();
//            }
//
//            RealMatrix density_matrix = DensityMatrix(matrices, wordSpace_size);
//            inverted_list_densityMatrix.put(count, density_matrix);                                                         // Add the density matrix into the inverted list.
//            matrices.clear();
//
//            OutputDensityMatrix(outFolder_pathName_invertedListDensityMatrix, inverted_list_densityMatrix);
//            inverted_list_densityMatrix.clear();        //for saving space
            OutputDocument(outFolder_pathName_invertedListDocument, inverted_list_document);

            for (Map.Entry<Integer, List<String>> entry1 : inverted_list_document.entrySet()) {

                int key1 = entry1.getKey();
                List<String> value1 = entry1.getValue();

                int size1 = value1.size();

                for (int i = 0; i < size1; i++) {
                    //inverted_list_word.get(value1.get(i)).add(key1);

                    String file_name = outFolder_pathName_invertedListWord + "\\" + value1.get(i);
                    OutputInvertedListWord1(file_name, key1);
                }
            }

            inverted_list_document.clear();
            System.out.println("输出 person_order_feedback" + String.valueOf(count));
            count++;

        }

        person_order_feedback.clear();


        //////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //////////////////////                         Output                          ///////////////////////////////
        //////////////////////   Map<Integer, RealMatrix> inverted_list_densityMatrix  ///////////////////////////////
        //////////////////////   Map<Integer, List<String>>  inverted_list_document    ///////////////////////////////
        //////////////////////   Map<String, List<Integer>> inverted_list_word         ///////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////


//        String outFolder_pathName_invertedListWord = "D:\\program\\Data\\invertedListWord";
//        String outFolder_pathName_invertedListDensityMatrix = "D:\\program\\Data\\invertedListDensityMatrix";
//        String outFolder_pathName_invertedListDocument = "D:\\program\\Data\\invertedListDocument";


        //////////////////////////////////////////////////////////////////Output word space
        ///////////////////////////////////////////////////Map<String,Integer> wordSpace = new HashMap<>();
        String fileName_WordSpace = outFolder_pathName_wordSpace + "\\" + "wordSpace";
        OutputWordSpace(fileName_WordSpace, temp_wordSpace);


        System.out.println("Done");


        long endTime = System.currentTimeMillis(); //获取结束时间
        System.out.println("程序运行时间： " + (endTime - startTime) + "ms");


    }  // main()

    private static void OutputInvertedListWord1(String file_name, int key1) {
        //****************************************Create file*******************************************
        File output_file = new File(file_name);

        if (output_file.exists()) {
            //System.out.println("The file has existed");
        } else {
            try {
                output_file.createNewFile();     // Create file, this is different form creating folder
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //****************************************Output*******************************************
        try {
            FileWriter fileWriter = new FileWriter(output_file, true);


            fileWriter.write(key1 + "\t");


            fileWriter.close(); // Close file stream

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void OutputWordSpace(String file_name, List<String> content) {

        //****************************************Create file*******************************************
        File output_file = new File(file_name);

        if (output_file.exists()) {
            System.out.println("The file has existed");
        } else {
            try {
                output_file.createNewFile();     // Create file, this is different form creating folder
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //****************************************Output*******************************************
        try {
            FileWriter fileWriter = new FileWriter(output_file, true);

            for (int i = 0; i < content.size(); i++) {
                fileWriter.write(content.get(i) + "\t");
            }

            fileWriter.close(); // Close file stream

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void OutputInvertedListWord(String file_name) {

        //****************************************Create file*******************************************
        File output_file = new File(file_name);

        if (output_file.exists()) {
            System.out.println("The file has existed");
        } else {
            try {
                output_file.createNewFile();     // Create file, this is different form creating folder
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        //****************************************Output*******************************************
        /*
        try {
            FileWriter fileWriter = new FileWriter(output_file, true);

            for (int i = 0; i < content.size(); i++) {
                fileWriter.write(content.get(i)+"\t");
            }

            fileWriter.close(); // Close file stream

        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////Output document

    private static void OutputDocument(String outFolder_pathName_invertedListDocument, Map<Integer, List<String>> inverted_list_document) {

        for (Map.Entry<Integer, List<String>> entry : inverted_list_document.entrySet()) {

            String file_name = outFolder_pathName_invertedListDocument + "\\" + entry.getKey().toString();
            List<String> content = entry.getValue();


            //*************************Create file***************************
            File output_file = new File(file_name);

            if (output_file.exists()) {
                System.out.println("The file has existed");
            } else {
                try {
                    output_file.createNewFile();     // Create file, this is different form creating folder
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //*****************************Output*******************
            try {
                FileWriter fileWriter = new FileWriter(output_file, true);

                for (int i = 0; i < content.size(); i++) {
                    fileWriter.write(content.get(i) + "\t");
                }

                fileWriter.close(); // Close file stream

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////Output density matrix
    private static void OutputDensityMatrix(String outFolder_pathName_invertedListDensityMatrix, Map<Integer, RealMatrix> inverted_list_densityMatrix) {

        for (Map.Entry<Integer, RealMatrix> entry : inverted_list_densityMatrix.entrySet()) {

            String file_name = outFolder_pathName_invertedListDensityMatrix + "\\" + entry.getKey().toString();
            double[][] array = entry.getValue().getData();


            //********************Create file***********************
            File output_file = new File(file_name);

            if (output_file.exists()) {
                System.out.println("The file has existed");
            } else {
                try {
                    output_file.createNewFile();     // Create file, this is different form creating folder
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //**********************Output****************************
            try {
                FileWriter fileWriter = new FileWriter(output_file, false);

                for (int i = 0; i < array.length; i++) {
                    for (int j = 0; j < array[i].length; j++) {
                        if (array[i][j] != 0) {
                            int a = i + 1;
                            int b = j + 1;
                            fileWriter.write(a + "\t" + b + "\t" + array[i][j] + "\r\n");
                            //fileWriter.write(String.valueOf(a) + "\t" + String.valueOf(b) + "\t" + String.valueOf(array[i][j]) );
                        }
                    }
                    //fileWriter.write("\r\n");
                }

                fileWriter.close(); // Close file stream

            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }

    private static void CreateFolder(String folder_name) {

        File folderName = new File(folder_name);

        if (folderName.exists()) {
            System.out.println("The folder has existed");
        } else {
            folderName.mkdir(); // Create folder /home/...
        }
    }

    private static RealMatrix DensityMatrix(List<RealMatrix> matrices, int wordSpace_size) {

        double coefficient = 0.5;

        //RealMatrix matrix1 = MatrixUtils.createRealIdentityMatrix(wordSpace_size);
        RealMatrix matrix1 = MatrixUtils.createRealIdentityMatrix(MatrixDimension);

        RealMatrix matrix2 = RPRAlgorithm(matrix1, matrices);

        for (int i = 0; i < 15; i++) {
            if (Math.abs(likelihoodValue(matrix2, matrices) - likelihoodValue(matrix1, matrices)) < Math.pow(10, -4)) {
                break;
            } else {
                matrix1 = matrix2;
                matrix2 = RPRAlgorithm(matrix1, matrices);
                RealMatrix left = matrix1.scalarMultiply(1 - coefficient);
                RealMatrix right = matrix2.scalarMultiply(coefficient);
                matrix2 = left.add(right);
                //System.out.println(i);
            }
        }
        return matrix2;
    }

    private static double likelihoodValue(RealMatrix pMatrix, List<RealMatrix> matrices) {

        int matrices_size = matrices.size();
        double value = 1.0;
        for (int i = 0; i < matrices_size; i++) {
            value *= pMatrix.multiply(matrices.get(i)).getTrace();
        }
        return value;
    }

    private static RealMatrix RPRAlgorithm(RealMatrix pMatrix, List<RealMatrix> matrices) {

        int dimension = MatrixDimension; //pMatrix.getColumnDimension();

        //RealMatrix rMatrix = MatrixUtils.createRealMatrix(dimension, dimension);
        RealMatrix rMatrix = new OpenMapRealMatrix(dimension, dimension);

        int matrices_size = matrices.size();

        for (int i = 0; i < matrices_size; i++) {
            //System.out.println("calculate RPR" + String.valueOf(i));
            RealMatrix iMatrix = matrices.get(i);

            RealMatrix tmpMatrix = pMatrix.multiply(iMatrix);
            double trace = tmpMatrix.getTrace();
            if (trace != 0)
                rMatrix = rMatrix.add(iMatrix.scalarMultiply(1.0 / trace));
        }


        RealMatrix Q = rMatrix.multiply(pMatrix).multiply(rMatrix);
        double Z = Q.getTrace();
        if (Z != 0)
            pMatrix = Q.scalarMultiply(1.0 / Z);

        return pMatrix;
    }

    private static List<RealMatrix> TransformDependentProjectors(Map<String, Integer> wordSpace, List<String> map_value) {

        int map_value_size = map_value.size();
        int wordSpace_size = wordSpace.size();
        List<RealMatrix> matrices = new ArrayList<>();

        if (map_value_size < 2)
            return matrices;
        for (int i = 0; i + 1 < map_value_size; i++) {
            String[] strings = (String[]) map_value.subList(i, i + 2).toArray(new String[2]);                           //subList() just gets the content i and i + 1;

            RealVector dependenciesVector1 = new OpenMapRealVector(MatrixDimension);                                     //For constructing dependencies' s projector, this is a initial vector.

            //Here we process words in the dependencies one by one.
            for (int j = 0; j < 2; j++) {
                int position1 = wordSpace.get(strings[j]);

                if (position1 <= wordSpace_size) {
                    RealVector dependenciesComponent1 = SingleTermRepreOfDepen(position1, 2);
                    dependenciesVector1 = dependenciesVector1.add(dependenciesComponent1);
                } else {
                    System.out.println("space is small");
                }
            }

            RealMatrix matrix1 = dependenciesVector1.outerProduct(dependenciesVector1);
            matrices.add(matrix1);
        }

        if (map_value_size < 3)
            return matrices;
        for (int i = 0; i + 2 < map_value_size; i++) {
            String[] strings = (String[]) map_value.subList(i, i + 3).toArray(new String[3]);                           //subList() just gets the content i and i + 1;

            RealVector dependenciesVector2 = new OpenMapRealVector(MatrixDimension);                                     //For constructing dependencies' s projector, this is a initial vector.

            //Here we process words in the dependencies one by one.
            for (int j = 0; j < 3; j++) {
                int position2 = wordSpace.get(strings[j]);

                if (position2 <= wordSpace_size) {
                    RealVector dependenciesComponent2 = SingleTermRepreOfDepen(position2, 3);
                    dependenciesVector2 = dependenciesVector2.add(dependenciesComponent2);
                } else {
                    System.out.println("space is small");
                }
            }

            RealMatrix matrix2 = dependenciesVector2.outerProduct(dependenciesVector2);
            matrices.add(matrix2);
        }

        return matrices;
    }

    private static RealVector SingleTermRepreOfDepen(int wordPosition, int numberOfDepen) {
        String binaryOfInteger = Integer.toBinaryString(wordPosition);
        int stringLength = binaryOfInteger.length();

        //RealMatrix projector = new OpenMapRealMatrix(wordSpace_size, wordSpace_size);                               // The starting number of matrix is 0.

        RealVector vector = new OpenMapRealVector(MatrixDimension);

        int count = 0;

        for (int j = 0; j < stringLength; j++) {

            char number = binaryOfInteger.charAt(j);
            //int m = Integer.parseInt(String.valueOf(number));

            if (number == '1') {
                vector.setEntry(j, 1.0);
                count++;
            }

        }
        for (int j = 0; j < stringLength; j++) {
            double d = vector.getEntry(j);
            vector.setEntry(j, d * 1.0 / (Math.sqrt(count) * Math.sqrt(numberOfDepen)));
        }

        return vector;

    }

    private static List<RealMatrix> TransformSingleProjector(Map<String, Integer> wordSpace, List<String> map_value) {

        int map_value_size = map_value.size();
        int wordSpace_size = wordSpace.size();
        List<RealMatrix> matrices = new ArrayList<>();

        for (int i = 0; i < map_value_size; i++) {
            String word = map_value.get(i);
            int wordPosition = wordSpace.get(word);

            if (wordPosition <= wordSpace_size) {

                RealVector vector = SingleTermRepresentation(wordPosition);
                RealMatrix projector = vector.outerProduct(vector);
                matrices.add(projector);

            } else
                System.out.println("word space is small");


        }

        return matrices;
    }

    private static RealVector SingleTermRepresentation(int wordPosition) {

        String binaryOfInteger = Integer.toBinaryString(wordPosition);
        int stringLength = binaryOfInteger.length();

        //RealMatrix projector = new OpenMapRealMatrix(wordSpace_size, wordSpace_size);                               // The starting number of matrix is 0.

        RealVector vector = new OpenMapRealVector(MatrixDimension);

        int count = 0;

        for (int j = 0; j < stringLength; j++) {

            char number = binaryOfInteger.charAt(j);
            //int m = Integer.parseInt(String.valueOf(number));

            if (number == '1') {
                vector.setEntry(j, 1.0);
                count++;
            }

        }
        for (int j = 0; j < stringLength; j++) {
            double d = vector.getEntry(j);
            vector.setEntry(j, d * 1.0 / Math.sqrt(count));
        }

        return vector;

    }

    //Map<String, List<String>> order = new HashMap<String, List<String>>();
    //Map<String, List<String>> personHashMap = new HashMap<String, List<String>>();
    //Map<String, List<String>> person_graph = new HashMap<String, List<String>>();
    //Map<String, List<String>> person_graph_order = new HashMap<String, List<String>>();
    //Map<String, List<String>> person_order_feedback = new HashMap<>();;

    //clean word (delete all the elements which are not digital or letter)
    //stem word
    //add these words into final list
    //cleanAndStem(qName);
    private static List<String> cleanAndStem(String str, Set<String> stopwords) {

        SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);

        List<String> list = new ArrayList<String>();

        String lowerCase_str = str.toLowerCase();
        String[] first_words = lowerCase_str.split("\\s+");

        StringBuilder result = new StringBuilder();
        for (String w : first_words) {
            if (!stopwords.contains(w)) {
                result.append(w);
                result.append(" ");
            }

        }


        String clean = result.toString().trim();
        if (clean.isEmpty()) {
            return list;
        }


        //\\p{Alnum} means digital or alphabetic character  (Regular expression in Java)
        //[^abc] means any character except a, b and c.
        //String clean = str.toLowerCase().replaceAll("[^\\p{Alnum}]", " ").replaceAll("\\s\\s+", " ");
        // here we just split a word into two words.(like micro-robot becomes micro robot)

        clean = clean.replaceAll("[^\\p{Alnum}]", " ").replaceAll("\\s\\s+", " ");

        String[] second_words = clean.split("\\s+");


        for (String w : second_words) {
            if (!stopwords.contains(w)) {
                String string = stemmer.stem(w).toString();
                list.add(string);
            }
        }

        return list;
    }


} //class Transform
