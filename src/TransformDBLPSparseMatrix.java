import com.ElementJsonofDBLP;
import com.ProcessJsonofDBLP;
import com.SparseVector;
import com.csvreader.CsvReader;
import opennlp.tools.stemmer.snowball.SnowballStemmer;
//import org.apache.commons.math3.linear.*;
import com.SparseMatrix;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

import static java.lang.System.exit;
import static java.lang.System.setOut;

/**
 * @ authour Gongsheng Yuan
 */

// min_pre = 0.2
public class TransformDBLPSparseMatrix {
    public static void main(String[] args) throws IOException {

        long startTime =System.currentTimeMillis();   //获取开始时间

        List<String> temp_wordSpace = new ArrayList<>();
        Map<String,Integer> wordSpace = new HashMap<>();

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
        //System.out.println(stopwords);

        //////////////////////////////////////////////read paper json file////////////////////////////////////////////////////
        List<ElementJsonofDBLP> jsonClassList = new ArrayList<ElementJsonofDBLP>();
        ProcessJsonofDBLP json = new ProcessJsonofDBLP();               //file path in the ProcessJsonofDBLP.java
        jsonClassList = json.readJsonFile();                            //read json file, and store them in a list of json class defined by us.

        Map<String, List<String>> paper = new HashMap<String, List<String>>();


        for (int i = 0; i < jsonClassList.size(); i++) {

            List<String> map_value = new ArrayList<>();
            ElementJsonofDBLP tmp = jsonClassList.get(i);

            String string = "paper id " + tmp.getId() + " " + "title " + tmp.getTitle();



            map_value.addAll(cleanAndStem(string, stopwords));

            LinkedHashSet<String> set = new LinkedHashSet<String>(map_value.size());                       //remove duplicate
            set.addAll(map_value);

            temp_wordSpace.removeAll(set);
            temp_wordSpace.addAll(set);                                                              // add to word space


            paper.put(tmp.getId(), map_value);
        }

        jsonClassList.clear();                                                    //Clear the list

        System.out.println("paper");
        System.out.println(paper);


        ///////////////////////// read author_paper csv file join paper based on paper id /////////////////////////
        List<String[]> csvAuthorPaperList = new ArrayList<String[]>();

        String filePath_AuthorPaper = "D:\\program\\Data\\dblp\\author_paper.csv";
        CsvReader reader3 = new CsvReader(filePath_AuthorPaper, '|', Charset.forName("UTF-8"));
        //reader3.readHeaders(); // This means that we need to skip header. If you need the header, just delete this sentence.
        while (reader3.readRecord()) {            // read data line-by-line except header
            csvAuthorPaperList.add(reader3.getValues());
        }
        reader3.close();

//        System.out.println(csvAuthorPaperList.get(0).length);
//        for (int r = 0; r < csvAuthorPaperList.size(); r++) {
//            for (int c = 0; c < csvAuthorPaperList.get(r).length; c++) {
//                String cell = csvAuthorPaperList.get(r)[c];
//                System.out.println(cell + "\t");
//            }
//        }

        Map<String, List<String>> authorPaper = new HashMap<String, List<String>>();                                                //author paper  Map<String, List<String>> authorPaper

        for (int i = 1; i < csvAuthorPaperList.size(); i++) {
            String newSentence = "author paper ";
            String cell_name = "";                                                                              //Get the attribute name in the first line
            String cell = "";

            for (int j = 0; j < csvAuthorPaperList.get(i).length; j++) {

                cell_name = csvAuthorPaperList.get(0)[j];
                cell = csvAuthorPaperList.get(i)[j];                                                            //Get the value of cell

                if ( 1 == j){
                    List<String> paperContent = paper.get(cell);
                    for (int k = 0; k < paperContent.size(); k++) {
                        newSentence += paperContent.get(k) + " ";
                    }
                    continue;
                }

                newSentence += cell_name + " " + cell + " ";

            }

            List<String> map_value = new ArrayList<>();
            map_value.addAll(cleanAndStem(newSentence, stopwords));

            LinkedHashSet<String> set = new LinkedHashSet<String>(map_value.size());                       //remove duplicate
            set.addAll(map_value);

            temp_wordSpace.removeAll(set);
            temp_wordSpace.addAll(set);                                                              // add to word space
            authorPaper.put(csvAuthorPaperList.get(i)[0], map_value);
        }

        csvAuthorPaperList.clear();                                                                  //Clear the list

        System.out.println("Author Paper");
        System.out.println(authorPaper);


        //////////////////////////////////////////read friend csv file/////////////////////////////////////
        List<String[]> csvFriendList = new ArrayList<String[]>();

        String filePath_Friend = "D:\\program\\Data\\dblp\\friend.csv";
        CsvReader reader4 = new CsvReader(filePath_Friend, '|', Charset.forName("UTF-8"));
        //reader3.readHeaders(); // This means that we need to skip header. If you need the header, just delete this sentence.
        while (reader4.readRecord()) {            // read data line-by-line except header
            csvFriendList.add(reader4.getValues());
        }
        reader4.close();

        Map<String, List<String>> friendRelationship = new HashMap<String, List<String>>();           //////////////////////friendRelationship

        for (int i = 0; i < csvFriendList.size(); i++) {
            String first_cell = csvFriendList.get(i)[0];                                              //Get the the first value
            String second_cell = csvFriendList.get(i)[1];                                             //Get the the second value

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


        csvFriendList.clear();                                                    //Clear the list

        System.out.println("friendRelationship");
        System.out.println(friendRelationship);


        ////////////////////////////////////////////Join three data model ////////////////////////////////////////////   图没加图名字

        List<List<String>> threeDataJoin = new ArrayList<>();
        String friend_word = cleanAndStem("friend", stopwords).get(0);

        for ( Map.Entry<String, List<String>> entry : friendRelationship.entrySet() ) {
            String map_key = entry.getKey();                                                       //Gianluca Colucci

            List<String> map_value = new ArrayList<>();
//            System.out.println(map_key);
//            System.out.println(authorPaper.get(map_key));
            if (authorPaper.get(map_key) != null)
                map_value.addAll(authorPaper.get(map_key));                                            //=[Lorenzo Grespan, Paolo Fiorini]


            List<String> friends = friendRelationship.get(map_key);

//            System.out.println(map_key);
//            System.out.println(friends);

            if (friends != null) {

                map_value.add( friend_word );

                for (int i = 0; i < friends.size(); i++) {
                    //String tmp_key = friends.get(i);
                    List<String> tmp_value = authorPaper.get(friends.get(i));
                    if (tmp_value != null) {
                        map_value.addAll(tmp_value);
                    }
                }
            }


            LinkedHashSet<String> set = new LinkedHashSet<String>(map_value.size());                       //remove duplicate
            set.addAll(map_value);

            temp_wordSpace.removeAll(set);
            temp_wordSpace.addAll(set);

            threeDataJoin.add(map_value);
        }

        System.out.println("threeDataJoin");
        System.out.println(threeDataJoin);


        int wordSpace_size = temp_wordSpace.size();                     //The size of word space

        for (int i = 0; i < wordSpace_size; i++) {
            String string = temp_wordSpace.get(i);
            wordSpace.put(string, i);                                   //word space

        }


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
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////

        String outFolder_pathName_invertedListWord = "D:\\program\\Data\\dblpInvertedList\\invertedListWord";
        String outFolder_pathName_invertedListDensityMatrix = "D:\\program\\Data\\dblpInvertedList\\invertedListDensityMatrix";
        String outFolder_pathName_invertedListDocument = "D:\\program\\Data\\dblpInvertedList\\invertedListDocument";
        String outFolder_pathName_wordSpace = "D:\\program\\Data\\dblpInvertedList\\wordSpace";

        CreateFolder(outFolder_pathName_invertedListWord);
        CreateFolder(outFolder_pathName_invertedListDensityMatrix);
        CreateFolder(outFolder_pathName_invertedListDocument);
        CreateFolder(outFolder_pathName_wordSpace);


        Map<Integer, SparseMatrix> inverted_list_densityMatrix = new HashMap<>();                     //"Integer" for the No. of density matrices, "List" for density matrices about the specified No..
        Map<Integer, List<String>> inverted_list_document = new HashMap<>();                       //"Integer" for the No. of document, "List" for document about the specified No..


        //////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////      Constructing word inverted list      /////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////

        for ( Map.Entry<String, Integer> entry : wordSpace.entrySet() ) {                          //Initiate inverted list of word.

            String file_name = outFolder_pathName_invertedListWord + "\\" + entry.getKey();
            OutputInvertedListWord(file_name);

        }

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////Transform paper into density matrix
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////

        int count = 0;                                                                                                      //The No. of density matrices and document.


        for ( Map.Entry<String, List<String>> entry : paper.entrySet() ) {

            List<String> map_value = entry.getValue();

            inverted_list_document.put(count, map_value);                                                                   // Add the document into the inverted list.


            List<SparseMatrix> matrices = TransformSingleProjector( wordSpace, map_value );                                   //Store matrices of single word and dependencies.

            List<SparseMatrix> matrices_dependencies = TransformDependentProjectorsColocation( wordSpace, map_value );
            if (matrices_dependencies.size() > 0) {
                matrices.addAll(matrices_dependencies);
                matrices_dependencies.clear();
            }
            SparseMatrix density_matrix = DensityMatrix(matrices, wordSpace_size);
            inverted_list_densityMatrix.put(count, density_matrix);                                                         // Add the density matrix into the inverted list.改变density matrix 存储形式
            matrices.clear();

            OutputDensityMatrix(outFolder_pathName_invertedListDensityMatrix, inverted_list_densityMatrix);
            inverted_list_densityMatrix.clear();        // for saving space
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

            System.out.println("输出paper " + String.valueOf(count));
            count++;

        }

        paper.clear();



        //////////////////////////////////////////////////////////////////Transform authorPaper into density matrix
        for ( Map.Entry<String, List<String>> entry : authorPaper.entrySet() ) {

            List<String> map_value = entry.getValue();

            inverted_list_document.put(count, map_value);                                                                   // Add the document into the inverted list.

            List<SparseMatrix> matrices = TransformSingleProjector( wordSpace, map_value );                                   //Store matrices of single word and dependencies.
//            List<RealMatrix> matrices_dependencies = TransformDependentProjectors( wordSpace, map_value );
            List<SparseMatrix> matrices_dependencies = TransformDependentProjectorsColocation( wordSpace, map_value );
            if (matrices_dependencies.size() > 0) {
                matrices.addAll(matrices_dependencies);
                matrices_dependencies.clear();
            }

            SparseMatrix density_matrix = DensityMatrix(matrices, wordSpace_size);
            inverted_list_densityMatrix.put(count, density_matrix);                                                         // Add the density matrix into the inverted list.
            matrices.clear();

            OutputDensityMatrix(outFolder_pathName_invertedListDensityMatrix, inverted_list_densityMatrix);
            inverted_list_densityMatrix.clear();        // for saving space
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
            System.out.println("输出 author Paper" + String.valueOf(count));
            count++;

        }

        authorPaper.clear();

        //////////////////////////////////////////////////////////////////Transform three data model into density matrix
        int size_threeDataJoin = threeDataJoin.size();

        for (int ii = 0; ii < size_threeDataJoin; ii++) {
            List<String> map_value =threeDataJoin.get(ii);
            inverted_list_document.put(count, map_value);                                                                   // Add the document into the inverted list.

            List<SparseMatrix> matrices = TransformSingleProjector( wordSpace, map_value );                                   //Store matrices of single word and dependencies.
            List<SparseMatrix> matrices_dependencies = TransformDependentProjectors( wordSpace, map_value );
            if (matrices_dependencies.size() > 0) {
                matrices.addAll(matrices_dependencies);
                matrices_dependencies.clear();
            }


            SparseMatrix density_matrix = DensityMatrix(matrices, wordSpace_size);
            inverted_list_densityMatrix.put(count, density_matrix);                                                         // Add the density matrix into the inverted list.
            matrices.clear();

            OutputDensityMatrix(outFolder_pathName_invertedListDensityMatrix, inverted_list_densityMatrix);
            inverted_list_densityMatrix.clear();     // for saving space
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
            System.out.println("输出 three data model " + String.valueOf(count));
            count++;
        }

        threeDataJoin.clear();


        //////////////////////////////////////////////////////////////////Output word space
        ///////////////////////////////////////////////////Map<String,Integer> wordSpace = new HashMap<>();

        OutputWordSpace(outFolder_pathName_wordSpace, temp_wordSpace);
        System.out.println("Done");

        long endTime = System.currentTimeMillis(); //获取结束时间
        System.out.println("程序运行时间： "+(endTime - startTime)+"ms");

    }  // main()

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private static List<SparseMatrix> TransformDependentProjectorsColocation(Map<String, Integer> wordSpace, List<String> map_value) {

        double min_pre = 0.2;

        int map_value_size = map_value.size();
        int wordSpace_size = wordSpace.size();

        List<SparseMatrix> matrices = new ArrayList<>();
        List<List<String>> allDependencies = new ArrayList<>();
        List<Integer> allDependenciesNumber = new ArrayList<>();

        //count the number of each word
        Map<String, Integer> single_word = new HashMap<>();
        for (int i = 0; i < map_value_size; i++) {
            String string = map_value.get(i);
            if (!single_word.containsKey(string)){
                single_word.put(string, 1);
            }else {
                single_word.put(string, single_word.get(string) + 1);
            }
        }



        if (map_value_size < 2 )
            return matrices;
        for (int i = 0; i + 1 < map_value_size; i++) {
            //String[] strings = (String[]) map_value.subList(i, i + 2).toArray(new String[2]);                           //subList() just gets the content i and i + 1;
            int j = 0;
            for (; j < allDependencies.size(); j++) {
                if (allDependencies.get(j).containsAll( map_value.subList(i, i + 2)))
                    break;
            }
            if (j < allDependencies.size()){
                allDependenciesNumber.set(j, allDependenciesNumber.get(j) + 1);
            }else if(j == allDependencies.size()){
                    allDependencies.add(map_value.subList(i, i + 2));
                    allDependenciesNumber.add(1);
            }
        }

        for (int i = 0; i < allDependencies.size(); i++) {
            List<String> tmp_dep = allDependencies.get(i);
            double denominator = 0;
            double participateIndex = 1.0;

            for (int j = 0; j < tmp_dep.size(); j++) {
                String str = tmp_dep.get(j);
                double participateRatio = allDependenciesNumber.get(i) * 1.0 / single_word.get(str);
                denominator +=  single_word.get(str);

                if (participateRatio < participateIndex)
                    participateIndex = participateRatio;
            }

            if (participateIndex >= min_pre){

                SparseVector dependenciesVector1 = new SparseVector(wordSpace_size);                                     //For constructing dependencies' s projector, this is a initial vector.

                //Here we process words in the dependencies one by one.
                for (int j = 0; j < tmp_dep.size(); j++) {
                    SparseVector dependenciesComponent1 = new SparseVector(wordSpace_size);
                    int position1 = wordSpace.get(tmp_dep.get(j));
                    if (position1 < wordSpace_size) {
                        double numerator = single_word.get(tmp_dep.get(j));
                        dependenciesComponent1.setEntry( position1, Math.sqrt(numerator/denominator) );          //1.0 / Math.sqrt(tmp_dep.size())
                    }
                    else
                        System.out.println("space is small");
                    dependenciesVector1 = dependenciesVector1.Add(dependenciesVector1, dependenciesComponent1);
                }

                SparseMatrix matrix1 = dependenciesVector1.Multiply(dependenciesVector1, dependenciesVector1);
                //System.out.println(matrix1.Triples);
                matrices.add(matrix1);
            }

        }

        allDependencies.clear();
        allDependenciesNumber.clear();


        if (map_value_size < 3 )
            return matrices;
        for (int i = 0; i + 2 < map_value_size; i++) {
            int j = 0;
            for (; j < allDependencies.size(); j++) {
                if (allDependencies.get(j).containsAll( map_value.subList(i, i + 3) ))
                    break;
            }
            if (j < allDependencies.size()){
                allDependenciesNumber.set(j, allDependenciesNumber.get(j) + 1);
            }else if(j == allDependencies.size()){
                allDependencies.add(map_value.subList(i, i + 3));
                allDependenciesNumber.add(1);
            }
        }

        for (int i = 0; i < allDependencies.size(); i++) {
            List<String> tmp_dep = allDependencies.get(i);
            double denominator = 0;
            double participateIndex = 1.0;
            for (int j = 0; j < tmp_dep.size(); j++) {
                String str = tmp_dep.get(j);
                double participateRatio = allDependenciesNumber.get(i) * 1.0 / single_word.get(str);
                denominator +=  single_word.get(str);

                if (participateRatio < participateIndex)
                    participateIndex = participateRatio;
            }

            if (participateIndex >= min_pre){

                SparseVector dependenciesVector2 = new SparseVector(wordSpace_size);                                     //For constructing dependencies' s projector, this is a initial vector.

                //Here we process words in the dependencies one by one.
                for (int j = 0; j < tmp_dep.size(); j++) {
                    SparseVector dependenciesComponent2 = new SparseVector(wordSpace_size);
                    int position2 = wordSpace.get(tmp_dep.get(j));
                    if (position2 < wordSpace_size) {
                        double numerator = single_word.get(tmp_dep.get(j));
                        dependenciesComponent2.setEntry(position2, Math.sqrt(numerator/denominator));                       //1.0 / Math.sqrt(tmp_dep.size())
                    }
                    else
                        System.out.println("space is small");
                    dependenciesVector2 = dependenciesVector2.Add(dependenciesVector2, dependenciesComponent2);
                }

                SparseMatrix matrix2 = dependenciesVector2.Multiply(dependenciesVector2, dependenciesVector2);
                matrices.add(matrix2);
            }

        }

        //System.out.println("Finish transform dependencies projector");
        return matrices;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private static void OutputInvertedListWord1(String file_name, int key1) {
        //****************************************Create file*******************************************
        File output_file = new File(file_name);

        //****************************************Output*******************************************
        try {
            FileWriter fileWriter = new FileWriter(output_file, true);


            fileWriter.write(key1 + "\t");


            fileWriter.close(); // Close file stream

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private static void OutputWordSpace(String outFolder_pathName_wordSpace, List<String> content) {

        //****************************************Create file*******************************************
        String file_name = outFolder_pathName_wordSpace + "\\" + "wordSpace";
        File output_file = new File(file_name);

        if (output_file.exists()) {
            System.out.println("The word space file has existed");
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
                fileWriter.write(content.get(i)+"\t");
            }

            fileWriter.close(); // Close file stream

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private static void OutputInvertedListWord(String file_name) {

        //****************************************Create file*******************************************
        File output_file = new File(file_name);

        if (output_file.exists()) {
            System.out.println("The inverted list word file has existed");
        } else {
            try {
                output_file.createNewFile();     // Create file, this is different form creating folder
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /*//****************************************Output*******************************************
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
    private static void OutputDensityMatrix(String outFolder_pathName_invertedListDensityMatrix, Map<Integer, SparseMatrix> inverted_list_densityMatrix) {

        for (Map.Entry<Integer, SparseMatrix> entry : inverted_list_densityMatrix.entrySet()) {

            String file_name = outFolder_pathName_invertedListDensityMatrix + "\\" + entry.getKey().toString();
            SparseMatrix tmpMatrix = entry.getValue();


            //********************Create file***********************
            File output_file = new File(file_name);

            if (output_file.exists()) {
                System.out.println("The density file has existed");
                exit(1);
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

                Map<ArrayList<Integer>, Double> triples = tmpMatrix.getTriples();
                Iterator<Map.Entry<ArrayList<Integer>, Double>> it = triples.entrySet().iterator();

                while(it.hasNext()){
                    Map.Entry<ArrayList<Integer>, Double> entry1 = it.next();
                    ArrayList<Integer> position = entry1.getKey();

                    int a = position.get(0) + 1;
                    int b = position.get(1) + 1;

                    fileWriter.write(a + "\t" + b + "\t" + entry1.getValue() + "\r\n");

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

    private static SparseMatrix DensityMatrix(List<SparseMatrix> matrices, int wordSpace_size) {

        double coefficient = 0.5;

        //RealMatrix matrix1 = MatrixUtils.createRealIdentityMatrix(wordSpace_size);
        SparseMatrix matrix1 = new SparseMatrix();
        matrix1 = matrix1.IdentityMatrix(wordSpace_size);
        //System.out.println(matrix1.Triples);

        SparseMatrix matrix2 = RPRAlgorithm(matrix1, wordSpace_size, matrices);

        for (int i = 0; i < 15; i++) {
            if (Math.abs(likelihoodValue(matrix2, matrices) - likelihoodValue(matrix1, matrices)) < Math.pow(10, -4)){
                break;
            }
            else {
                matrix1 = matrix2;
                //System.out.println(matrix1.Triples);
                matrix2 = RPRAlgorithm(matrix1, wordSpace_size, matrices);
                //System.out.println(matrix2.Triples);
                SparseMatrix left = matrix1.ScalarMultiply(matrix1, 1 - coefficient);
                SparseMatrix right = matrix2.ScalarMultiply(matrix2, coefficient );
                matrix2 = left.Add(left, right);
                //System.out.println( "calculate density matrix" + String.valueOf(i));
            }
        }
        //System.out.println("density matrix ");
        //System.out.println(matrix2.Triples);
        //exit(1);
        return matrix2;
    }

    private static double likelihoodValue(SparseMatrix pMatrix, List<SparseMatrix> matrices) {

        int matrices_size = matrices.size();
        double value = 1.0;
        for (int i = 0; i < matrices_size; i++) {
            value *= pMatrix.Multiply(pMatrix, matrices.get(i)).getTrace();
        }
        //System.out.println("likelihoodValue" + String.valueOf(value));
        return value;
    }

    private static SparseMatrix RPRAlgorithm(SparseMatrix pMatrix, int wordSpace_size, List<SparseMatrix> matrices) {

        //int dimension = pMatrix.getColumnDimension();
        //RealMatrix rMatrix = MatrixUtils.createRealMatrix(dimension, dimension);
        SparseMatrix rMatrix = new SparseMatrix(wordSpace_size, wordSpace_size);
        int matrices_size = matrices.size();


        //System.out.println("matrices size " + String.valueOf(matrices_size));

        for (int i = 0; i < matrices_size; i++) {
            SparseMatrix iMatrix = matrices.get(i);
            //System.out.println(iMatrix.Triples);

            SparseMatrix tmpMatrix = pMatrix.Multiply(pMatrix, iMatrix);

            //System.out.println(tmpMatrix.Triples);

            double trace = tmpMatrix.getTrace();

            //System.out.println(tmpMatrix.Triples);
            //System.out.println("TRACE"+ trace);
            //System.out.println("TRACE"+ trace);
            //System.exit(1);

            if (trace != 0) {
                SparseMatrix newiMatrix = iMatrix.ScalarMultiply(iMatrix, 1.0 / trace);
                rMatrix = rMatrix.Add(rMatrix, newiMatrix);
            }
        }



        SparseMatrix Q = rMatrix.Multiply(rMatrix, pMatrix);
        //Q.Print();
        Q = Q.Multiply(Q, rMatrix);
        //Q.Print();

        double Z = Q.getTrace();

        if (Z != 0)
            pMatrix = Q.ScalarMultiply(Q, 1.0/Z);

        //pMatrix.Print();

        //exit(1);

        return pMatrix;
    }

    private static List<SparseMatrix> TransformDependentProjectors(Map<String, Integer> wordSpace, List<String> map_value) {

        int map_value_size = map_value.size();
        int wordSpace_size = wordSpace.size();
        List<SparseMatrix> matrices = new ArrayList<>();

        if (map_value_size < 2 )
            return matrices;
        for (int i = 0; i + 1 < map_value_size; i++) {
            String[] strings = (String[]) map_value.subList(i, i + 2).toArray(new String[2]);                           //subList() just gets the content i and i + 1;

            SparseVector dependenciesVector1 = new SparseVector(wordSpace_size);                                     //For constructing dependencies' s projector, this is a initial vector.

            //Here we process words in the dependencies one by one.
            for (int j = 0; j < 2; j++) {
                SparseVector dependenciesComponent1 = new SparseVector(wordSpace_size);
                int position1 = wordSpace.get(strings[j]);
                if (position1 < wordSpace_size)
                    dependenciesComponent1.setEntry(position1, 1.0/Math.sqrt(2));
                else
                    System.out.println("space is small");
                dependenciesVector1 = dependenciesVector1.Add(dependenciesVector1, dependenciesComponent1);
            }

            SparseMatrix matrix1 = dependenciesVector1.Multiply(dependenciesVector1, dependenciesVector1);
            matrices.add(matrix1);
        }

        if (map_value_size < 3 )
            return matrices;
        for (int i = 0; i + 2 < map_value_size; i++) {
            String[] strings = (String[]) map_value.subList(i, i + 3).toArray(new String[3]);                           //subList() just gets the content i and i + 1;

            SparseVector dependenciesVector2 = new SparseVector(wordSpace_size);                                     //For constructing dependencies' s projector, this is a initial vector.

            //Here we process words in the dependencies one by one.
            for (int j = 0; j < 3; j++) {
                SparseVector dependenciesComponent2 = new SparseVector(wordSpace_size);
                int position2 = wordSpace.get(strings[j]);
                if (position2 < wordSpace_size)
                    dependenciesComponent2.setEntry(position2, 1.0/Math.sqrt(3));
                else
                    System.out.println("space is small");
                dependenciesVector2 = dependenciesVector2.Add(dependenciesVector2, dependenciesComponent2);
            }

            SparseMatrix matrix2 = dependenciesVector2.Multiply(dependenciesVector2, dependenciesVector2);
            matrices.add(matrix2);
        }
        return matrices;
    }

    private static List<SparseMatrix> TransformSingleProjector(Map<String, Integer> wordSpace, List<String> map_value) {

        int map_value_size = map_value.size();
        int wordSpace_size = wordSpace.size();
        List<SparseMatrix> matrices = new ArrayList<>();

        for (int i = 0; i < map_value_size; i++) {
            String word = map_value.get(i);
            int wordPosition = wordSpace.get(word);
            SparseMatrix projector = new SparseMatrix(wordSpace_size, wordSpace_size);                               // The starting number of matrix is 0.
            if (wordPosition < wordSpace_size)
                projector.setEntry(wordPosition, wordPosition, 1.0);
            else
                System.out.println("word space is small");

            //System.out.println(projector.Triples);
            matrices.add(projector);
        }

        //System.out.println("Finish transform single projector");
        return matrices;
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









}  //class Transform
