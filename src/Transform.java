import com.ProcessJson;
import com.ElementJsonClass;
import com.csvreader.CsvReader;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import opennlp.tools.stemmer.snowball.SnowballStemmer;
import org.apache.commons.math3.linear.*;

import java.io.*;
import java.util.*;

import java.nio.charset.Charset;

/**
 * @ authour Gongsheng Yuan
 */
public class Transform {
    public static void main(String[] args) throws IOException {
        //Map<String, List<String>> order = new HashMap<String, List<String>>();
        //Map<String, List<String>> personHashMap = new HashMap<String, List<String>>();
        //Map<String, List<String>> person_graph = new HashMap<String, List<String>>();
        //Map<String, List<String>> person_graph_order = new HashMap<String, List<String>>();
        //Map<String, List<String>> person_order_feedback = new HashMap<>();
        List<String> temp_wordSpace = new ArrayList<>();
        Map<String,Integer> wordSpace = new HashMap<>();

        String outFolder_pathName_invertedListWord = "D:\\program\\Data\\invertedListWord";
        String outFolder_pathName_invertedListDensityMatrix = "D:\\program\\Data\\invertedListDensityMatrix";
        String outFolder_pathName_invertedListDocument = "D:\\program\\Data\\invertedListDocument";
        String outFolder_pathName_wordSpace = "D:\\program\\Data\\wordSpace";


        ////////////////////////////////////////////For reading stop words//////////////////////////////////////////////
        Set<String> stopwords = new HashSet<String>(); // for storing stop words
        String stopwordFile = "D:\\program\\Data\\stopwords\\stopwords_en.txt";

        //Read stop words from file into HashSet
        try {
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
        System.out.println(stopwords);

        //////////////////////////////////////////////read order json file////////////////////////////////////////////////////
        List<ElementJsonClass> jsonClassList = new ArrayList<ElementJsonClass>();
        ProcessJson json = new ProcessJson();
        jsonClassList = json.readJsonFile();   //read json file, and store them in a list of json class defined by us.

//        System.out.println(jsonClassList.get(0).getOrderId());
//        System.out.println(jsonClassList.get(0).getPersonId());
//        System.out.println(jsonClassList.get(0).getOrderDate());
//        System.out.println(jsonClassList.get(0).getTotalPrice());
//        System.out.println(jsonClassList.get(0).getOrderLine());

        Map<String, List<String>> order = new HashMap<String, List<String>>();


        for (int i = 0; i < jsonClassList.size(); i++) {

            List<String> map_value = new ArrayList<>();
            ElementJsonClass tmp = jsonClassList.get(i);

            String string = tmp.getOrderDate() + " ";
            JSONArray orderLine = tmp.getOrderLine();

            for(int j = 0; j < orderLine.size(); j++){

                JSONObject jb = (JSONObject)orderLine.get(j);

                string += jb.get("title").toString() + " ";

                string += jb.get("price").toString() + " ";

                string += jb.get("brand").toString() + " ";

            }
            map_value.addAll(cleanAndStem(string, stopwords));

            LinkedHashSet<String> set = new LinkedHashSet<String>(map_value.size());                    //remove duplicate
            set.addAll(map_value);

            temp_wordSpace.removeAll(set);
            temp_wordSpace.addAll(set);                                                              // add to word space


            order.put(tmp.getOrderId(), map_value);
        }
        System.out.println("order");
        System.out.println(order);

        //////////////////////////////////////////read person csv file////////////////////////////////////////////////
        List<String[]> csvPersonList = new ArrayList<String[]>();

        String filePath_Person = "D:\\program\\Data\\ParameterCuration\\person_0_0.csv";
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

        Map<String, List<String>> personHashMap = new HashMap<String, List<String>>();
        //String[] person_attributes = csvPersonList.get(0);

        for (int i = 1; i < csvPersonList.size(); i++) {

            String first_cell = csvPersonList.get(i)[0];                                        //Get the the first value (person_id)
            ArrayList<String> person_information = new ArrayList<>();
            personHashMap.put(first_cell, person_information);

            for (int j = 1; j < 5; j++) {                                                       // We only need to use the first five columns(attributes).

                String cell = csvPersonList.get(i)[j];
                List<String> person_information_list = cleanAndStem(cell, stopwords);


                LinkedHashSet<String> set = new LinkedHashSet<String>(person_information_list.size());                    //remove duplicate
                set.addAll(person_information_list);

                temp_wordSpace.removeAll(set);                              // add to word space
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
        System.out.println(personHashMap);

        //////////////////////////////////////////read person_know_person csv file/////////////////////////////////////
        List<String[]> csvPersonKnowPersonList = new ArrayList<String[]>();

        String filePath_PersonKnowPerson = "D:\\program\\Data\\ParameterCuration\\person_knows_person_0_0.csv";
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

        Map<String, List<String>> friendRelationship = new HashMap<String, List<String>>();

        for (int i = 1; i < csvPersonKnowPersonList.size(); i++) {
            String first_cell = csvPersonKnowPersonList.get(i)[0];                          //Get the the first value
            String second_cell = csvPersonKnowPersonList.get(i)[1];                         //Get the the second value

            ////////////Add the second value into the list of first person////////////
            if (!friendRelationship.containsKey(first_cell)) {
                ArrayList<String> friends = new ArrayList<>();
                friends.add(second_cell);
                friendRelationship.put(first_cell,friends);
            }
            else {
                friendRelationship.get(first_cell).add(second_cell);
            }
            ////////////Add the first value into the list of second person////////////
            if (!friendRelationship.containsKey(second_cell)) {
                ArrayList<String> friends = new ArrayList<>();
                friends.add(first_cell);
                friendRelationship.put(second_cell,friends);
            }
            else {
                friendRelationship.get(second_cell).add(first_cell);
            }
        }


        csvPersonKnowPersonList.clear();                                                    //Clear the list of PersonknowPerson

        System.out.println("friendRelationship");
        System.out.println(friendRelationship);

        ////////////////////////////////////////////read feedback csv file////////////////////////////////////////////
        List<String[]> csvFeedbackList = new ArrayList<String[]>();

        String filePath_Feedback = "D:\\program\\Data\\ParameterCuration\\feedback.csv";
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
            char ra = csvFeedbackList.get(i)[2].charAt(1);
            String str = String.valueOf(ra);
            int rate = Integer.parseInt(str);
            feedback.put(cell, rate);

            if (!temp_wordSpace.contains(str))                                  // add to word space
                temp_wordSpace.addAll(cleanAndStem(str,stopwords));

        }

        System.out.println("feedback");
        System.out.println(feedback);

        ////////////////////////////////////////////////////////为了生成example,要删掉/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        if (!temp_wordSpace.containsAll(cleanAndStem("person", stopwords)))                                  // add to word space
            temp_wordSpace.addAll(cleanAndStem("person", stopwords));
        if (!temp_wordSpace.containsAll(cleanAndStem("id", stopwords)))                                  // add to word space
            temp_wordSpace.addAll(cleanAndStem("id", stopwords));
        if (!temp_wordSpace.containsAll(cleanAndStem("friend", stopwords)))                                  // add to word space
            temp_wordSpace.addAll(cleanAndStem("friend", stopwords));
        if (!temp_wordSpace.containsAll(cleanAndStem("rate", stopwords)))                                  // add to word space
            temp_wordSpace.addAll(cleanAndStem("rate", stopwords));


        ////////////////////////////////////////////////////////为了生成example,要删掉/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


        //////////////////////////// Join: person -- personknowperson (person_graph) ///////////////////////////////////////

        Map<String, List<String>> person_graph = new HashMap<String, List<String>>();           //The result of joining between person and personknowperson

        for ( Map.Entry<String, List<String>> entry : personHashMap.entrySet() ) {
            String map_key = entry.getKey();                                                        //5296
            List<String> map_value = new ArrayList<>();
            ////////////////////////////////////////////////////////为了生成example,要删掉/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            map_value.add("person");
            map_value.add("id");
            ////////////////////////////////////////////////////////为了生成example,要删掉/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            map_value.addAll(entry.getValue());                                                     //[Rafael, Oliveira, male, 1987-06-08]

            ////////////////////////////////////////////////////////为了生成example,要删掉/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            map_value.add("friend");
            ////////////////////////////////////////////////////////为了生成example,要删掉/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            List<String> friends = friendRelationship.get(map_key);                                 //5296=[5386, 5409, 5447, 5507, 8620, 2199023256462, 2199023256638, 4398046512650, 6597069771841, 6597069773471, 8796093032121, 13194139535588, 13194139541987, 13194139542240, 15393162796864, 15393162797836, 19791209301379, 19791209310563, 28587302326094, 30786325579314, 32985348844052]

//            System.out.println(map_key);
//            System.out.println(friends);

            for (int i = 0; i < friends.size(); i++) {
                String tmp_key = friends.get(i);
//                System.out.println(tmp_key);
                List<String> tmp_value = personHashMap.get(tmp_key);
                if (tmp_value != null) {
                    map_value.addAll(tmp_value);
                }
            }

            person_graph.put(map_key, map_value);

        }


        System.out.println("person_graph");
        System.out.println(person_graph);
        ////////////////////////////////// Join: person_graph -- order  /////////////////////////////////////////////

        Map<String, List<String>> person_graph_order = new HashMap<String, List<String>>();           //The result of joining between persongraph and jason

        for ( Map.Entry<String, List<String>> entry : person_graph.entrySet() ) {
            String map_key = entry.getKey();                                                        //5296
            List<String> map_value = new ArrayList<>();
                                                                 //[Rafael, Oliveira, male, 1987-06-08]

            for (int i = 0; i < jsonClassList.size(); i++) {
                ElementJsonClass tmp = jsonClassList.get(i);
                if (tmp.getPersonId().equals(map_key)) {
                    String string = tmp.getOrderDate() + " ";                       //order date
                    JSONArray orderLine = tmp.getOrderLine();

                    for(int j = 0; j < orderLine.size(); j++){

                        JSONObject jb = (JSONObject)orderLine.get(j);

                        string += jb.get("title").toString() + " ";                 //title

                        string += jb.get("price").toString() + " ";                 //price

                        string += jb.get("brand").toString() + " ";                 //brand

                    }
                    map_value.addAll(cleanAndStem(string, stopwords));
                    //map_value.add(string);
                }
            }

            if (!map_value.isEmpty()) {
                map_value.addAll(entry.getValue());
                person_graph_order.put(map_key, map_value);
            }
        }

        System.out.println("person_graph_order");
        System.out.println(person_graph_order);

        ////////////////////////////////// Join: order -- feedback /////////////////////////////////////////////

        List<String[]> order_feedback = new ArrayList<>();           //The result of joining between order and feedback
        String[] strings = new String[2];

        for (int i = 0; i < jsonClassList.size(); i++) {
            ElementJsonClass tmp = jsonClassList.get(i);

            String orderId = tmp.getOrderId();                                    //order id
            String personId = tmp.getPersonId();                                    //person id
            String orderDate = tmp.getOrderDate();                                  //order date

            JSONArray orderLine = tmp.getOrderLine();
            strings[0] = personId;
            strings[1] = "";
            int flag = 0;  //表示是否有连接存在

            for(int j = 0; j < orderLine.size(); j++){

                JSONObject jb = (JSONObject)orderLine.get(j);

                String asin = jb.get("asin").toString();                  //product id

                String key = asin + personId;  //不一定买的物品都有评价？

                if (feedback.get(key) != null ){

                    ///////////////////////这个过程会出现order里面只有评价和商品被列出，没有被评价的商品没有被列出，使得order不完整
                    flag = 1;   //表示有连接存在

                    String title = jb.get("title").toString();                      //title

                    String price = jb.get("price").toString();                      //price

                    String brand = jb.get("brand").toString();                      //brand


                    //strings[1] = orderDate + " " + title + " " + price + " " + brand + " " + String.valueOf(feedback.get(key));
                    ////////////////////////////////////////////////////////为了生成example,要删掉/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    //strings[1] = orderDate + " " + title + " " + price + " " + brand + " " + "custom" + " " + "id" + " " + personId + " " + "product" + " " + "id" + " " + asin + " " + "rate" + " " + String.valueOf(feedback.get(key));
                    //version 2//strings[1] += "product" + " " + "id" + asin + " " + "brand" + " " + brand + " " + "custom" + " " + "id" + " " + personId + " " + "product" + " " + "id" + " " + asin + " " + "rate" + " " + String.valueOf(feedback.get(key)) + " ";
                    strings[1] += "product" + " " + "id" + asin + " " + "brand" + " " + brand + " " + "custom" + " " + "id" + " " + personId + " " + "product" + " " + "id" + " " + asin + " " + "rate" + " " + String.valueOf(feedback.get(key)) + " ";
                    ////////////////////////////////////////////////////////为了生成example,要删掉/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                }
                else{
                    strings[1] += "product" + " " + "id" + asin + " " + "brand" + " " + jb.get("brand").toString() + " ";
                }

            }

            if ( flag == 1) {
                String ss = "id" + " " + orderId + " " + "custom" + " " + "id" + " " + personId + " " + "total" + " " + "price" + " " + tmp.getTotalPrice().toString() + " " + "item" + " " + strings[1];
                strings[1] = ss;
                order_feedback.add(strings);
            }
        }

        System.out.println("order_feedback");
        for (int i = 0; i < order_feedback.size(); i++) {
                String cell = order_feedback.get(i)[1];
                System.out.println(cell);
        }

        ////////////////////////////////// Join: person -- order -- feedback /////////////////////////////////////////////

        /////person_order_feedback
        Map<String, List<String>> person_order_feedback = new HashMap<>();

        for (int i = 0; i < order_feedback.size(); i++) {
            String personId = order_feedback.get(i)[0];

            List<String> person = person_graph.get(personId);
            if (  person!= null ){
                //person_order_feedback.addAll(person);
                List<String> tmp_list = cleanAndStem(order_feedback.get(i)[1], stopwords);
                tmp_list.addAll(person);
                person_order_feedback.put(personId, tmp_list);
            }

        }

        System.out.println("person_order_feedback");
        System.out.println(person_order_feedback);




        int wordSpace_size = temp_wordSpace.size();                     //The size of word space

        for (int i = 0; i < wordSpace_size; i++) {
            String string = temp_wordSpace.get(i);
            wordSpace.put(string, i);                                   //word space

        }


        //temp_wordSpace.clear();

        System.out.println("The size of word space");
        System.out.println(wordSpace_size);
        System.out.println(wordSpace);


        //////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////        Constructing density matrix        /////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////


        Map<Integer, RealMatrix> inverted_list_densityMatrix = new HashMap<>();                     //"Integer" for the No. of density matrices, "List" for density matrices about the specified No..
        Map<Integer, List<String>>  inverted_list_document = new HashMap<>();                       //"Integer" for the No. of document, "List" for document about the specified No..


        //Map<String, List<String>> order = new HashMap<String, List<String>>();
        //Map<String, List<String>> person_graph = new HashMap<String, List<String>>();
        //Map<String, List<String>> person_graph_order = new HashMap<String, List<String>>();
        //Map<String, List<String>> person_order_feedback = new HashMap<>();


        ////////////////////////////////////////////////////////////////////Transform order into density matrix

        int count = 0;                                                                                                      //The No. of density matrices and document.


        for ( Map.Entry<String, List<String>> entry : order.entrySet() ) {

            List<String> map_value = entry.getValue();

            inverted_list_document.put(count, map_value);                                                                   // Add the document into the inverted list.


            List<RealMatrix> matrices = TransformSingleProjector( wordSpace, map_value );                                   //Store matrices of single word and dependencies.
            List<RealMatrix> matrices_dependencies = TransformDependentProjectors( wordSpace, map_value );
            matrices.addAll(matrices_dependencies);

            RealMatrix density_matrix = DensityMatrix(matrices, wordSpace_size);
            inverted_list_densityMatrix.put(count, density_matrix);                                                         // Add the density matrix into the inverted list.

            count++;

        }


        //////////////////////////////////////////////////////////////////Transform person_graph into density matrix
        for ( Map.Entry<String, List<String>> entry : person_graph.entrySet() ) {

            List<String> map_value = entry.getValue();

            inverted_list_document.put(count, map_value);                                                                   // Add the document into the inverted list.

            List<RealMatrix> matrices = TransformSingleProjector( wordSpace, map_value );                                   //Store matrices of single word and dependencies.
            matrices.addAll(TransformDependentProjectors( wordSpace, map_value ));

            RealMatrix density_matrix = DensityMatrix(matrices, wordSpace_size);
            inverted_list_densityMatrix.put(count, density_matrix);                                                         // Add the density matrix into the inverted list.

            count++;

        }

        //////////////////////////////////////////////////////////////////Transform person_graph_order into density matrix
        for ( Map.Entry<String, List<String>> entry : person_graph_order.entrySet() ) {

            List<String> map_value = entry.getValue();

            inverted_list_document.put(count, map_value);                                                                   // Add the document into the inverted list.

            List<RealMatrix> matrices = TransformSingleProjector( wordSpace, map_value );                                   //Store matrices of single word and dependencies.
            matrices.addAll(TransformDependentProjectors( wordSpace, map_value ));

            RealMatrix density_matrix = DensityMatrix(matrices, wordSpace_size);
            inverted_list_densityMatrix.put(count, density_matrix);                                                         // Add the density matrix into the inverted list.

            count++;

        }

        //////////////////////////////////////////////////////////////////Transform person_order_feedback into density matrix
        for ( Map.Entry<String, List<String>> entry : person_order_feedback.entrySet() ) {

            List<String> map_value = entry.getValue();

            inverted_list_document.put(count, map_value);                                                                   // Add the document into the inverted list.

            List<RealMatrix> matrices = TransformSingleProjector( wordSpace, map_value );                                   //Store matrices of single word and dependencies.
            matrices.addAll(TransformDependentProjectors( wordSpace, map_value ));

            RealMatrix density_matrix = DensityMatrix(matrices, wordSpace_size);
            inverted_list_densityMatrix.put(count, density_matrix);                                                         // Add the density matrix into the inverted list.

            count++;

        }




        //////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////      Constructing word inverted list      /////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////

        Map<String, List<Integer>> inverted_list_word = new HashMap<>();                            //"String" for word, "List" for the No. of density matrices and document.

        for ( Map.Entry<String, Integer> entry1 : wordSpace.entrySet() ) {                          //Initiate inverted list of word.

            String key1 = entry1.getKey();
            List<Integer> integers= new ArrayList<>();
            inverted_list_word.put(key1, integers);

        }

        for ( Map.Entry<String, Integer> entry1 : wordSpace.entrySet() ) {

            String key1 = entry1.getKey();

            for ( Map.Entry<Integer, List<String>> entry2 : inverted_list_document.entrySet() ) {

                int key2 = entry2.getKey();
                List<String> value2 = entry2.getValue();

                if ( value2.contains(key1)){
                    inverted_list_word.get(key1).add(key2);
                }
            }

        }


        //////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //////////////////////                         Output                          ///////////////////////////////
        //////////////////////   Map<Integer, RealMatrix> inverted_list_densityMatrix  ///////////////////////////////
        //////////////////////   Map<Integer, List<String>>  inverted_list_document    ///////////////////////////////
        //////////////////////   Map<String, List<Integer>> inverted_list_word         ///////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////


//        String outFolder_pathName_invertedListWord = "D:\\program\\Data\\invertedListWord";
//        String outFolder_pathName_invertedListDensityMatrix = "D:\\program\\Data\\invertedListDensityMatrix";
//        String outFolder_pathName_invertedListDocument = "D:\\program\\Data\\invertedListDocument";

        CreateFolder(outFolder_pathName_invertedListWord);
        CreateFolder(outFolder_pathName_invertedListDensityMatrix);
        CreateFolder(outFolder_pathName_invertedListDocument);
        CreateFolder(outFolder_pathName_wordSpace);

        //////////////////////////////////////////////////////////////////Output density matrix
        for (Map.Entry<Integer, RealMatrix> entry : inverted_list_densityMatrix.entrySet()) {

            String file_name = outFolder_pathName_invertedListDensityMatrix + "\\" + entry.getKey().toString();
            double[][] array = entry.getValue().getData();
            OutputDensityMatrix(file_name, array);
        }

        //////////////////////////////////////////////////////////////////Output document
        for (Map.Entry<Integer, List<String>> entry : inverted_list_document.entrySet()) {

            String file_name = outFolder_pathName_invertedListDocument + "\\" + entry.getKey().toString();
            List<String> content = entry.getValue();
            OutputDocument(file_name, content);
        }

        //////////////////////////////////////////////////////////////////Output inverted list word
        for (Map.Entry<String, List<Integer>> entry : inverted_list_word.entrySet()) {

            String file_name = outFolder_pathName_invertedListWord + "\\" + entry.getKey();
            List<Integer> content = entry.getValue();
            OutputInvertedListWord(file_name, content);
        }

        //////////////////////////////////////////////////////////////////Output word space
        ///////////////////////////////////////////////////Map<String,Integer> wordSpace = new HashMap<>();
        String fileName_WordSpace = outFolder_pathName_wordSpace + "\\" + "wordSpace";
        OutputWordSpace(fileName_WordSpace, temp_wordSpace);



        System.out.println("Done");










    }  // main()

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
            FileWriter fileWriter = new FileWriter(output_file, false);

            for (int i = 0; i < content.size(); i++) {
                fileWriter.write(content.get(i)+"\t");
            }

            fileWriter.close(); // Close file stream

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void OutputInvertedListWord(String file_name, List<Integer> content) {

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
            FileWriter fileWriter = new FileWriter(output_file, false);

            for (int i = 0; i < content.size(); i++) {
                fileWriter.write(content.get(i)+"\t");
            }

            fileWriter.close(); // Close file stream

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void OutputDocument(String file_name, List<String> content) {

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
            FileWriter fileWriter = new FileWriter(output_file, false);

            for (int i = 0; i < content.size(); i++) {
                fileWriter.write(content.get(i)+"\t");
            }

            fileWriter.close(); // Close file stream

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void OutputDensityMatrix(String file_name, double[][] array) {

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
            FileWriter fileWriter = new FileWriter(output_file, false);

            for (int i = 0; i < array.length; i++) {
                for (int j = 0; j < array[i].length; j++) {
                    fileWriter.write(array[i][j]+"\t");
                }
                fileWriter.write("\r\n");
            }

            fileWriter.close(); // Close file stream

        } catch (IOException e) {
            e.printStackTrace();
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

        RealMatrix matrix1 = MatrixUtils.createRealIdentityMatrix(wordSpace_size);

        RealMatrix matrix2 = RPRAlgorithm(matrix1, matrices);

        for (int i = 0; i < 50; i++) {
            if (Math.abs(likelihoodValue(matrix2, matrices) - likelihoodValue(matrix1, matrices)) < Math.pow(10, -4)){
                break;
            }
            else {
                matrix1 = matrix2;
                matrix2 = RPRAlgorithm(matrix1, matrices);
                RealMatrix left = matrix1.scalarMultiply( 1 - coefficient);
                RealMatrix right = matrix2.scalarMultiply( coefficient );
                matrix2 = left.add(right);
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

        int dimension = pMatrix.getColumnDimension();

        RealMatrix rMatrix = MatrixUtils.createRealMatrix(dimension, dimension);
        int matrices_size = matrices.size();

        for (int i = 0; i < matrices_size; i++) {
            RealMatrix iMatrix = matrices.get(i);
            double trace = pMatrix.multiply(iMatrix).getTrace();
            if (trace != 0)
                rMatrix = rMatrix.add(iMatrix.scalarMultiply(1.0/trace));
        }

        RealMatrix Q = rMatrix.multiply(pMatrix).multiply(rMatrix);
        double Z = Q.getTrace();
        if (Z != 0)
            pMatrix = Q.scalarMultiply(1.0/Z);

        return pMatrix;
    }

    private static List<RealMatrix> TransformDependentProjectors(Map<String, Integer> wordSpace, List<String> map_value) {

        int map_value_size = map_value.size();
        int wordSpace_size = wordSpace.size();
        List<RealMatrix> matrices = new ArrayList<>();

        if (map_value_size < 2 )
            return matrices;
        for (int i = 0; i + 1 < map_value_size; i++) {
            String[] strings = (String[]) map_value.subList(i, i + 2).toArray(new String[2]);                           //subList() just gets the content i and i + 1;

            RealVector dependenciesVector1 = new OpenMapRealVector(wordSpace_size);                                     //For constructing dependencies' s projector, this is a initial vector.

            //Here we process words in the dependencies one by one.
            for (int j = 0; j < 2; j++) {
                RealVector dependenciesComponent1 = new OpenMapRealVector(wordSpace_size);
                int position1 = wordSpace.get(strings[j]);
                if (position1 < wordSpace_size)
                    dependenciesComponent1.setEntry(position1, 1.0/Math.sqrt(2));
                dependenciesVector1 = dependenciesVector1.add(dependenciesComponent1);
            }

            RealMatrix matrix1 = dependenciesVector1.outerProduct(dependenciesVector1);
            matrices.add(matrix1);
        }

        if (map_value_size < 3 )
            return matrices;
        for (int i = 0; i + 2 < map_value_size; i++) {
            String[] strings = (String[]) map_value.subList(i, i + 3).toArray(new String[3]);                           //subList() just gets the content i and i + 1;

            RealVector dependenciesVector2 = new OpenMapRealVector(wordSpace_size);                                     //For constructing dependencies' s projector, this is a initial vector.

            //Here we process words in the dependencies one by one.
            for (int j = 0; j < 3; j++) {
                RealVector dependenciesComponent2 = new OpenMapRealVector(wordSpace_size);
                int position2 = wordSpace.get(strings[j]);
                if (position2 < wordSpace_size)
                    dependenciesComponent2.setEntry(position2, 1.0/Math.sqrt(3));
                dependenciesVector2 = dependenciesVector2.add(dependenciesComponent2);
            }

            RealMatrix matrix2 = dependenciesVector2.outerProduct(dependenciesVector2);
            matrices.add(matrix2);
        }

        return matrices;
    }

    private static List<RealMatrix> TransformSingleProjector(Map<String, Integer> wordSpace, List<String> map_value) {

        int map_value_size = map_value.size();
        int wordSpace_size = wordSpace.size();
        List<RealMatrix> matrices = new ArrayList<>();

        for (int i = 0; i < map_value_size; i++) {
            String word = map_value.get(i);
            int wordPosition = wordSpace.get(word);
            RealMatrix projector = new OpenMapRealMatrix(wordSpace_size, wordSpace_size);                               // The starting number of matrix is 0.
            if (wordPosition < wordSpace_size)
                projector.setEntry(wordPosition, wordPosition, 1);
            matrices.add(projector);
        }

        return matrices;
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









}  //class Transform
