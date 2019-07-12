import com.ElementJsonofDBLP;
import com.ProcessJsonofDBLP;
import com.csvreader.CsvReader;
import org.apache.commons.math3.linear.OpenMapRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

/**
 * @ authour Gongsheng Yuan
 */
public class GraphKeywordSearchdblp {

    public static void main(String[] args) throws IOException {

        long startTime =System.currentTimeMillis();   //获取开始时间

        List<String[]> csvedge = new ArrayList<>();
        int count = 0;                                                          // No. of node
        Map<String, String> nodes = new HashMap<>();                            // nodes.put(nodes No., content in node);


        //////////////////////////////////////////////read paper json file////////////////////////////////////////////////////
        List<ElementJsonofDBLP> jsonClassList = new ArrayList<ElementJsonofDBLP>();
        ProcessJsonofDBLP json = new ProcessJsonofDBLP();               //file path in the ProcessJsonofDBLP.java
        jsonClassList = json.readJsonFile();                            //read json file, and store them in a list of json class defined by us.


        Map<String, String> paper = new HashMap<>();        //map paper.put(paper id, nodes No)
        for (int i = 0; i < jsonClassList.size(); i++, count++) {

            ElementJsonofDBLP tmp = jsonClassList.get(i);

            //String string1 = "id " + tmp.getId();
            String string1 = "paper id " + tmp.getId();                         //add the collection name into element
            string1 = string1.toLowerCase();
            //String nodesNo1 = "p" + String.valueOf(count);
            String nodesNo1 = String.valueOf(count);
            nodes.put(nodesNo1, string1);
            paper.put(tmp.getId().toLowerCase(), nodesNo1);


            count++;
            //String string2 = "title " + tmp.getTitle();
            String string2 = "paper title " + tmp.getTitle();                         //add the collection name into element
            string2 = string2.toLowerCase();
            //String nodesNo2 = "p" + String.valueOf(count);
            String nodesNo2 = String.valueOf(count);
            nodes.put(nodesNo2, string2);


            csvedge.add(new String[]{nodesNo1, nodesNo2});
        }

        jsonClassList.clear();                                                    //Clear the list

        //System.out.println("paper");
        //System.out.println(paper);

        //////////////////////////////////////////////read author////////////////////////////////////////////////////
        List<String[]> csvAuthor = new ArrayList<>();

        String filePath_Author = "D:\\program\\Data\\dataset3\\author.csv";
        CsvReader reader1 = new CsvReader(filePath_Author, '|', Charset.forName("UTF-8"));

        while (reader1.readRecord()) {            // read data line-by-line except header
            csvAuthor.add(reader1.getValues());
        }
        reader1.close();

        Map<String, String> author_number = new HashMap<>();


        for (int i = 0; i < csvAuthor.size(); i++, count++) {
            String tmp = csvAuthor.get(i)[0];
            tmp = tmp.toLowerCase();
            //String nodesNo1 = "p" + String.valueOf(count);
            String nodesNo1 = String.valueOf(count);
            nodes.put(nodesNo1, tmp);

            author_number.put(tmp, nodesNo1);    //map author_number.put(author name, nodes No)


        }

        //System.out.println(author_number);

        ///////////////////////////////////////////////// read friend /////////////////////////////////////////////////

        List<String[]> csvFriend = new ArrayList<>();

        String filePath_Friend = "D:\\program\\Data\\dataset3\\friend.csv";
        CsvReader reader = new CsvReader(filePath_Friend, '|', Charset.forName("UTF-8"));

        while (reader.readRecord()) {            // read data line-by-line except header
            csvFriend.add(reader.getValues());
        }
        reader.close();

        for (int i = 0; i < csvFriend.size(); i++) {

            String tmp1 = csvFriend.get(i)[0];
            tmp1 = tmp1.toLowerCase();
            String tmp2 = csvFriend.get(i)[1];
            tmp2 = tmp2.toLowerCase();

            String no_tmp1 = author_number.get(tmp1);
            String no_tmp2 = author_number.get(tmp2);
            csvedge.add(new String[] { no_tmp1, no_tmp2 });

        }

        ///////////////////////// read author_paper csv file /////////////////////////
        List<String[]> csvAuthorPaperList = new ArrayList<String[]>();

        String filePath_AuthorPaper = "D:\\program\\Data\\dataset3\\author_paper.csv";
        CsvReader reader3 = new CsvReader(filePath_AuthorPaper, '|', Charset.forName("UTF-8"));
        //reader3.readHeaders(); // This means that we need to skip header. If you need the header, just delete this sentence.
        while (reader3.readRecord()) {            // read data line-by-line except header
            csvAuthorPaperList.add(reader3.getValues());
        }
        reader3.close();

        String[] person_attributes = csvAuthorPaperList.get(0);                    //add the table and attribute name into tuple



        for (int i = 1; i < csvAuthorPaperList.size(); i++, count++) {
            String tuple = "author paper ";                                                                              //add the table and attribute name into tuple
            String cell = "";

            for (int j = 0; j < csvAuthorPaperList.get(i).length; j++) {

                cell = person_attributes[j] + " " + csvAuthorPaperList.get(i)[j];                                                            //Get the value of cell  //add the table and attribute name into tuple
                tuple += cell + " ";
            }
            tuple = tuple.toLowerCase();
            //System.out.println(tuple + "\t");

            //String nodesNo1 = "p" + String.valueOf(count);              //nodesNo1 is node No about tuple
            String nodesNo1 = String.valueOf(count);
            nodes.put(nodesNo1, tuple);


            cell = csvAuthorPaperList.get(i)[0];
            cell = cell.toLowerCase();
            String no_tmp1 = author_number.get(cell);
            csvedge.add(new String[] { no_tmp1, nodesNo1 });


            cell = csvAuthorPaperList.get(i)[1];
            cell = cell.toLowerCase();
            String no_tmp2 = paper.get(cell);
            csvedge.add(new String[] { no_tmp2, nodesNo1 });
        }


        //System.out.println(count);
        RealMatrix adjacencyMatrix = new OpenMapRealMatrix(count, count);

        for (int i = 0; i < csvedge.size(); i++) {

            int node1 = Integer.parseInt(csvedge.get(i)[0]);
            int node2 = Integer.parseInt(csvedge.get(i)[1]);

            if (node1 < count && node2 < count) {
                adjacencyMatrix.setEntry(node1, node2, 1.0);
                adjacencyMatrix.setEntry(node2, node1, 1.0);
            }
            else
                System.out.println("matrix is small");

        }

        RealMatrix adjacencyMatrixPower2 = adjacencyMatrix.multiply(adjacencyMatrix);


        List<List<String>> connectedNode1 = new ArrayList<>();
        for (int i = 0; i < count; i++) {                                       //find r-radius graphs according to lemma 1
            List<String> tmp = new ArrayList<>();
            for (int j = 0; j < count; j++) {
                if (adjacencyMatrix.getEntry(i, j) != 0) {
                    tmp.add(String.valueOf(j));
                }
            }
            tmp.add(String.valueOf(i));

            connectedNode1.add(tmp);
        }

        List<List<String>> connectedNode2 = new ArrayList<>();                  //for the corresponding subgraph, which nodes in this subgraph
        for (int i = 0; i < count; i++) {                                       //find r-radius graphs according to lemma 1
            List<String> tmp = new ArrayList<>();
            for (int j = 0; j < count; j++) {
                if (adjacencyMatrixPower2.getEntry(i, j) != 0) {
                    tmp.add(String.valueOf(j));
                }
            }
            tmp.add(String.valueOf(i));

            connectedNode2.add(tmp);
        }
        //System.out.println(connectedNode2.size());

        ///////////////////////////////judge r-radius graph

        List<Integer> rRadiusGraph = new ArrayList<>();                         //r-radius graph

        for (int i = 0; i < count; i++) {
            int flag = 0;
            for (int j = 0; j < connectedNode2.get(i).size(); j++) {
                String nodeNo = connectedNode2.get(i).get(j);
                int node = Integer.parseInt(nodeNo);
                List<String> nodeSet = connectedNode1.get(node);
                if ( nodeSet.containsAll(connectedNode2.get(i)) ){
                    //System.out.println("flag: 1");
                    flag = 1;
                    break;
                }
            }

            if (flag == 0){
                rRadiusGraph.add(i);
            }

        }
        //System.out.println(rRadiusGraph);
        ///////////////////////////////inverted list

        List<Integer> max_rRadiusGraph = new ArrayList<>();                         //max r-radius graph

        for (int i = 0; i < rRadiusGraph.size(); i++) {
            int rRdiusNode = rRadiusGraph.get(i);
            List<String> nodeInrRadius_i = connectedNode2.get(rRdiusNode);
            int max_flag = 0;
            for (int j = 0; j < nodeInrRadius_i.size(); j++) {
                String node = nodeInrRadius_i.get(j);
                if (rRdiusNode != Integer.parseInt(node)) {
                    List<String> nodeInrRadius_t = connectedNode2.get(Integer.parseInt(node));
                    if (nodeInrRadius_i.equals(nodeInrRadius_t)) {
                        max_flag = 1;
                        break;
                    }
                }
            }
            if (max_flag == 0){
                max_rRadiusGraph.add(rRdiusNode);
            }

        }

        //System.out.println(max_rRadiusGraph);
        //System.out.println("maxGraph" + max_rRadiusGraph.size());


       /* ///////////////////////////////inverted list about keyword, value: No. of max_rRadiusGraph

        Map<String, List<String>> inverted_list_word = new HashMap<>();            // inverted_list_word (keyword, <No. of max_rRadiusGraph>)
        for (Map.Entry<String, String> entry1 : nodes.entrySet()
             ) {
            String key1 = entry1.getKey();                                          //node No.
            String value1 = entry1.getValue();                                      //node value
            String [] arrayValue = value1.split(" ");

            int size1 = arrayValue.length;
            for (int i = 0; i < size1; i++) {

                if (!inverted_list_word.containsKey(arrayValue[i])) {
                    List<String> subGraph = new ArrayList<>();                 // inverted list index content
                    for (int j = 0; j < max_rRadiusGraph.size(); j++) {
                        int rRdiusNode = max_rRadiusGraph.get(j);
                        List<String> nodeInrRadius = connectedNode2.get(rRdiusNode);
                        if (nodeInrRadius.contains(key1)){
                            subGraph.add(String.valueOf(j));
                            inverted_list_word.put(arrayValue[i],subGraph);
                        }
                    }
                }
                else {
                    for (int j = 0; j < max_rRadiusGraph.size(); j++) {
                        int rRdiusNode = max_rRadiusGraph.get(j);
                        List<String> nodeInrRadius = connectedNode2.get(rRdiusNode);
                        if (nodeInrRadius.contains(key1)){
                            inverted_list_word.get(arrayValue[i]).add(String.valueOf(j));
                        }
                    }
                }
            }
        }
        */

        ////////////////////////////////inverted list about keyword, value: No. of node
        //Set<String> words = new HashSet<>();

        int totalIteminGraph = 0;
        Map<String, Integer> termFrequent = new HashMap<>();                                    // term frequent <keyword, frequent>
        Map<String, Set<Integer>> termMaximalR_radius = new HashMap<>();                         // how many maximal r-radius graphs include keyword <keyword, No. of maximal r-radius which includes keyword>

        Map<String, List<String>> inverted_list_word_node = new HashMap<>();                    // inverted_list_word (keyword, <No. of node>)

        for (Map.Entry<String, String> entry1 : nodes.entrySet()
                ) {
            String key1 = entry1.getKey();                                          //node No.
            String value1 = entry1.getValue();                                      //node value
            String [] arrayValue = value1.split(" ");

            int size1 = arrayValue.length;
            totalIteminGraph += size1;                                              // total item in graph

            for (int i = 0; i < size1; i++) {

                //words.add(arrayValue[i]);

                if (!inverted_list_word_node.containsKey(arrayValue[i])) {
                    List<String> node_no = new ArrayList<>();                   // inverted list index content
                    node_no.add(key1);
                    inverted_list_word_node.put(arrayValue[i],node_no);

                    termFrequent.put(arrayValue[i], 1);


                    Set<Integer> noofMaxgraph = new HashSet<>();
                    termMaximalR_radius.put(arrayValue[i], noofMaxgraph);
                    for (int j = 0; j < max_rRadiusGraph.size(); j++) {
                        int noofMaxGraph = max_rRadiusGraph.get(j);
                        List<String> nodesInSubgraph = connectedNode2.get(noofMaxGraph);
                        if (nodesInSubgraph.contains(key1)){
                            termMaximalR_radius.get(arrayValue[i]).add(noofMaxGraph);
                        }
                    }
                }
                else {
                    inverted_list_word_node.get(arrayValue[i]).add(key1);

                    int numberofFrequent = termFrequent.get(arrayValue[i]);
                    termFrequent.put(arrayValue[i], numberofFrequent + 1);

                    for (int j = 0; j < max_rRadiusGraph.size(); j++) {
                        int noofMaxGraph = max_rRadiusGraph.get(j);
                        List<String> nodesInSubgraph = connectedNode2.get(noofMaxGraph);
                        if (nodesInSubgraph.contains(key1)){
                            termMaximalR_radius.get(arrayValue[i]).add(noofMaxGraph);
                        }
                    }
                }
            }
        }


        //////////////////////////////////////////////////////////ntf

        Map<String, Double> ntf = new HashMap<>();                                      // term frequent <keyword, ntf>
        for (Map.Entry<String, Integer> entry1 : termFrequent.entrySet()
                ) {
            String key1 = entry1.getKey();                                              //keyword
            int value1 = entry1.getValue();                                             //the number of times of appearance about keyword

            ntf.put(key1, 1 + Math.log( 1.0 + Math.log( 1.0 + value1)));
        }


        /*
        for (Map.Entry<String, Double> entry1 : ntf.entrySet()
                ) {
            String key1 = entry1.getKey();                                              //keyword
            double value1 = entry1.getValue();                                             //the number of times of appearance about keyword

            System.out.println( key1 + " " + value1);
        }
         */



        //////////////////////////////////////////////////////////idf
        int numberofMax_rRadiusGraph = max_rRadiusGraph.size();
        Map<String, Double> idf = new HashMap<>();                                      // term frequent <keyword, idf>
        for (Map.Entry<String, Set<Integer>> entry1 : termMaximalR_radius.entrySet()
                ) {
            String key1 = entry1.getKey();                                              //keyword
            Set<Integer> value1 = entry1.getValue();                                    //which max_rRadiusGraphs include keyword
            int numberofvalue1inSet = value1.size();

            idf.put(key1, Math.log( (numberofMax_rRadiusGraph + 1.0)/(numberofvalue1inSet + 1.0) ));
        }

        //System.out.println(idf);

        ////////////////////////////////////////////////////ndl
        List<Integer> numberofItemsinEachMax_rRadiusGraph = new ArrayList<>();
        for (int i = 0; i < max_rRadiusGraph.size(); i++) {
            int noofMaxGraph = max_rRadiusGraph.get(i);
            List<String> nodesInSubgraph = connectedNode2.get(noofMaxGraph);
            int tmpNumber = 0;

            for (int j = 0; j < nodesInSubgraph.size(); j++) {

                String noofNode = nodesInSubgraph.get(j);                               //node No.
                String value1 = nodes.get(noofNode);                                    //node value
                String [] arrayValue = value1.split(" ");

                int size1 = arrayValue.length;
                tmpNumber += size1;                                              // total item in graph
            }

            numberofItemsinEachMax_rRadiusGraph.add(tmpNumber);
        }

        int sumallMax_rRadiusIterm = 0;
        for (int i = 0; i < numberofItemsinEachMax_rRadiusGraph.size(); i++) {
            sumallMax_rRadiusIterm += numberofItemsinEachMax_rRadiusGraph.get(i);
        }

        double avg = sumallMax_rRadiusIterm * 1.0 / numberofItemsinEachMax_rRadiusGraph.size();
        double ndl = (1 - 0.2) + 0.2 * (totalIteminGraph)/ (avg);


        //System.out.println(connectedNode2.get(100));

        // System.out.println("avg: " + avg);
        // System.out.println("sumallMax_rRadiusIterm: " + sumallMax_rRadiusIterm);
        // System.out.println("numberofItemsinEachMax_rRadiusGraph: " +  numberofItemsinEachMax_rRadiusGraph.size());
        //System.out.println("ndl: " + ndl);

        //////////////////////////////////////////////////////////////////score IR
        Map<String, Double> scoreIR = new HashMap<>();                                  //scoreIR<string, scoreIR>
        for (Map.Entry<String, Double> entry1 : ntf.entrySet()
                ) {
            String key1 = entry1.getKey();                                              //keyword
            double ntf_value = entry1.getValue();                                             //ntf
            double idf_value = idf.get(key1);

            scoreIR.put(key1, ntf_value * idf_value / ndl);
        }

        //System.out.println(scoreIR);


        /////////////////////////////////////////////////////////
        Map<String, List<List<Double>>> keywordPair = new HashMap<>();

        for (int i = 0; i < max_rRadiusGraph.size(); i++) {
            int noofMaxGraph = max_rRadiusGraph.get(i);
            //System.out.println(noofMaxGraph);
            List<String> nodesInSubgraph = connectedNode2.get(noofMaxGraph);             //nodesInSubgraph denotes which nodes in this r-radius graph

            Map<String, List<String>> wordinNodeinRadius = new HashMap<>();             //<keyword, List<String> denotes the content nodes set in which the content nodes that contain keyword in r-radius graph(noofMaxGraph)

            Set<String> words = new HashSet<>();

            for (int j = 0; j < nodesInSubgraph.size(); j++) {

                String noofNode = nodesInSubgraph.get(j);                               //node No.
                String value1 = nodes.get(noofNode);                                    //node value
                String [] arrayValue = value1.split(" ");

                for (int k = 0; k < arrayValue.length; k++) {
                    words.add(arrayValue[k]);

                    List<String> tmpList = new ArrayList<>();
                    tmpList.addAll(nodesInSubgraph);
                    tmpList.retainAll(inverted_list_word_node.get(arrayValue[k]));
                    wordinNodeinRadius.put(arrayValue[k], tmpList);
                    //if (arrayValue[k].equals("Fawaz"))
                    //System.out.println(wordinNodeinRadius.get("Fawaz"));
                }
            }

//            if (words.contains("fawaz"))
//                System.out.println("include");
//            if (words.contains("alsolami"))
//                System.out.println("include");


            List<String> wordList = new ArrayList<String>(words);

            for (int j = 0; j < wordList.size() - 1; j++) {
                for (int k = j + 1; k < wordList.size(); k++) {
                    String keyword1 = wordList.get(j);
                    String keyword2 = wordList.get(k);
                    String tmpKey1 =  keyword1 + " " + keyword2;
                    String tmpKey2 =  keyword2 + " " + keyword1;

                    List<String> nodeIncludekeyword1 = new ArrayList<>(wordinNodeinRadius.get(keyword1));
                    List<String> nodeIncludekeyword2 = new ArrayList<>(wordinNodeinRadius.get(keyword2));


                    double tmpSimilarity = 0;
                    for (int l = 0; l < nodeIncludekeyword1.size(); l++) {
                        for (int m = 0; m < nodeIncludekeyword2.size(); m++) {                                          //排除相同点

                            double numberofPathLength2 = adjacencyMatrixPower2.getEntry(Integer.parseInt(nodeIncludekeyword1.get(l)), Integer.parseInt(nodeIncludekeyword2.get(m)));
                            if ( numberofPathLength2 != 0.0){
                                tmpSimilarity += numberofPathLength2 * 1.0 / Math.pow(( 2 + 1 ), 2);
                            }

                            double numberofPathLength1 = adjacencyMatrix.getEntry(Integer.parseInt(nodeIncludekeyword1.get(l)), Integer.parseInt(nodeIncludekeyword2.get(m)));
                            if (numberofPathLength1 != 0.0) {
                                tmpSimilarity +=  1.0 / Math.pow(( numberofPathLength1 + 1 ), 2);
                            }

                            //if ( Integer.parseInt(nodeIncludekeyword1.get(l)) == Integer.parseInt(nodeIncludekeyword2.get(m)))
                                //tmpSimilarity +=  1.0 / Math.pow(( 0 + 1 ), 2);

                        }
                    }

                    nodeIncludekeyword1.removeAll(nodeIncludekeyword2);
                    nodeIncludekeyword2.addAll(nodeIncludekeyword1);

                    double similarity = 1.0 / (nodeIncludekeyword2.size()) * tmpSimilarity;

                    //System.out.println("similarity:" + similarity);

                    double score = similarity * (scoreIR.get(keyword1) + scoreIR.get(keyword2));
                    //System.out.println("score:" + score);

                    if ( keywordPair.containsKey( tmpKey1 ) ){
                        List<Double> scoreList = new ArrayList<>();
                        scoreList.add((double) noofMaxGraph);
                        scoreList.add(score);
                        keywordPair.get(tmpKey1).add(scoreList);
                    }
                    if ( keywordPair.containsKey( tmpKey2 ) ){
                        List<Double> scoreList = new ArrayList<>();
                        scoreList.add((double) noofMaxGraph);
                        scoreList.add(score);
                        keywordPair.get(tmpKey2).add(scoreList);
                    }
                    else {
                        List<Double> scoreList = new ArrayList<>();
                        scoreList.add((double) noofMaxGraph);
                        scoreList.add(score);
                        List<List<Double>> tmpScoreList = new ArrayList<>();
                        tmpScoreList.add(scoreList);
                        keywordPair.put(tmpKey1,tmpScoreList);
                    }

                }
            }
        }
        //System.out.println(keywordPair);
        //System.out.println(keywordPair.get("alsolami fawaz"));
        //System.out.println(keywordPair.get("learning machine"));
        //System.out.println(keywordPair.get("learning machine"));
        //System.out.println("Alsolami Fawaz" + keywordPair.get("Alsolami Fawaz"));

/*
        for (Map.Entry<String, List<List<Double>>> entry1 : keywordPair.entrySet()
                ) {
            String key1 = entry1.getKey();                                              //keyword
            List<List<Double>> value1 = entry1.getValue();                                             //the number of times of appearance about keyword

            for (int i = 0; i < value1.size(); i++) {
                List<Double> tmpValue1 = value1.get(i);
                System.out.println( key1 + " " + tmpValue1.get(0) + " " + tmpValue1.get(1));
            }
        }
*/


        List<String> query = new ArrayList<>();

//        String query1 = "lianfa";
//        String query2 = "bai";
//        String query3 = "friend";
//        query.add(query1.toLowerCase());
//        query.add(query2.toLowerCase());
//        query.add(query3.toLowerCase());


//        String query1 = "lianfa";
//        String query2 = "bai";
//        String query3 = "friend";
//        String query4 = "paper";
//        query.add(query1.toLowerCase());
//        query.add(query2.toLowerCase());
//        query.add(query3.toLowerCase());
//        query.add(query4.toLowerCase());


//        query.add("Fawaz");
//        query.add("Alsolami");
//        query.add("title");
//        query.add("rank");

//        String query1 = "Machine";
//        String query2 = "Learning";
//        query.add(query1.toLowerCase());
//        query.add(query2.toLowerCase());


//        query.add("Machine");
//        query.add("learning");


        List<String> queryPair = new ArrayList<>();                         //each element is keyword pair about query

        for (int i = 0; i < query.size() - 1; i++) {
            for (int j = 0; j < query.size(); j++) {
                String keyword1 = query.get(i);
                String keyword2 = query.get(j);
                String tmpKey1 =  keyword1 + " " + keyword2;

                queryPair.add(tmpKey1);
            }
        }

        List<List<Double>> keywordPairScore = new ArrayList<>();

        for (int i = 0; i < queryPair.size(); i++) {
            if (keywordPair.get(queryPair.get(i)) != null){
                keywordPairScore.addAll(keywordPair.get(queryPair.get(i)));
            }else{
                String tmpkeywordPair = queryPair.get(i).toString();
                String [] arrayValue = tmpkeywordPair.split(" ");
                String newKeywordPair = arrayValue[1] + " " + arrayValue[0];

                if (keywordPair.get(newKeywordPair)!= null) {
                    keywordPairScore.addAll(keywordPair.get(newKeywordPair));
                }
            }
        }

        String[] position = new String[count];                     //shadow array for recording the position(which graph) about score
        double[] score = new double[count];


        for (int i = 0; i < keywordPairScore.size(); i++) {
            List<Double> tmpkeyscore = new ArrayList<>(keywordPairScore.get(i));
            int tmpPos = tmpkeyscore.get(0).intValue();
            score[tmpPos] += tmpkeyscore.get(1);
            position[tmpPos] = String.valueOf(tmpPos);
        }

        /*for (int i = 0; i < position.length; i++) {
            if (position[i] != null)
                System.out.println("position:" + position[i]);
        }*/



        for (int i = 0; i < score.length; i++) {
            for (int j = i + 1; j < score.length; j++) {
                if (score[i] < score[j]) {
                    double temp = score[i];
                    score[i] = score[j];
                    score[j] = temp;

                    String tempP = position[i];
                    position[i] = position[j];
                    position[j] = tempP;
                }
            }
        }

        for (int i = 0; i < 30; i++) {
            System.out.println("score:" + score[i]);
            System.out.println("graph No.:" + position[i]);
        }



        ///////////////////////////////////////////////////////////////////////////////////////////for generating steiner graph
        List<String> finalResult = new ArrayList<>();           //For storing final query result

        for (int i = 0; i < 30; i++) {
            if (position[i] != null) {
                List<String> nodesInSubgraph = new ArrayList<>(connectedNode2.get(Integer.parseInt(position[i])));


            List<String> cadidateContent = new ArrayList<>();
            for (int j = 0; j < query.size(); j++) {
                if (inverted_list_word_node.get(query.get(j)) != null) {
                    List<String> tmpContent = new ArrayList<>(inverted_list_word_node.get(query.get(j)));
                    tmpContent.removeAll(cadidateContent);
                    cadidateContent.addAll(tmpContent);
                }
            }

            nodesInSubgraph.retainAll(cadidateContent);             // Now we get content node

            List<List<String>> p_ci = new ArrayList<>();            //P(ci)


            for (int j = 0; j < nodesInSubgraph.size(); j++) {

                RealMatrix changedAdjacencyMatrix = adjacencyMatrix.copy();
                int no1 = Integer.valueOf(nodesInSubgraph.get(j));

                for (int k = 0; k < nodesInSubgraph.size(); k++) {

                    int no2 = Integer.valueOf(nodesInSubgraph.get(k));
                    if ( no1 != no2 ){

                        for (int l = 0; l < count; l++) {
                            changedAdjacencyMatrix.setEntry(no2, l, 0.0);
                        }

                        for (int l = 0; l < count; l++) {
                            changedAdjacencyMatrix.setEntry(l, no2, 0.0);
                        }
                    }
                }


                RealMatrix newAdjacencyMatrix2 = changedAdjacencyMatrix.multiply(changedAdjacencyMatrix);

                List<String> tmp = new ArrayList<>();

                for (int k = 0; k < count; k++) {

                    if (changedAdjacencyMatrix.getEntry(k, no1) != 0) {
                        tmp.add(String.valueOf(k));
                    }

                }

                for (int k = 0; k < count; k++) {

                    if (newAdjacencyMatrix2.getEntry(k, no1) != 0) {
                        tmp.add(String.valueOf(k));
                    }

                }

                p_ci.add(tmp);

            }   // for getting p(c_i)

            List<String> tmpFinalResult = new ArrayList<>();
            for (int j = 0; j < p_ci.size(); j++) {
                for (int k = j + 1; k < p_ci.size(); k++) {
                    List<String> tmpp_ci = new ArrayList<>(p_ci.get(j));
                    tmpp_ci.retainAll(p_ci.get(k));
                    tmpFinalResult.addAll(tmpp_ci);
                }
            }

            Set <String> listUniq = new HashSet<>(tmpFinalResult);

            listUniq.addAll(nodesInSubgraph);

            String queryContext = "";

            for (String str : listUniq){
                String tmpstr = nodes.get(str);
                queryContext = queryContext + tmpstr + " ";
            }

            finalResult.add(queryContext);

            }
        }


        if ( !finalResult.isEmpty() ) {
            for (int i = 0; i < finalResult.size(); i++) {
                System.out.println(finalResult.get(i));
            }
        }

        /*for (int i = 0; i < connectedNode2.get(0).size(); i++) {
            List<String> nodesInSubgraph = new ArrayList<>(connectedNode2.get(0));
            System.out.println(nodes.get(nodesInSubgraph.get(i)));
        }*/
        long endTime = System.currentTimeMillis(); //获取结束时间
        System.out.println("程序运行时间： "+(endTime - startTime)+"ms");


    }
}







/*        ////////////////////////////////inverted list about keyword, value: No. of node

        int totalIteminGraph = 0;
        Map<String, Integer> termFrequent = new HashMap<>();                                    // term frequent <keyword, frequent>
        Map<String, Set<Integer>> termR_radius = new HashMap<>();                               // how many r-radius graphs include keyword <keyword, No. of maximal r-radius which includes keyword>

        Map<String, List<String>> inverted_list_word_node = new HashMap<>();                    // inverted_list_word (keyword, <No. of node>)

        for (Map.Entry<String, String> entry1 : nodes.entrySet()
                ) {
            String key1 = entry1.getKey();                                          //node No.
            String value1 = entry1.getValue();                                      //node value
            String [] arrayValue = value1.split(" ");

            int size1 = arrayValue.length;
            totalIteminGraph += size1;                                              // total item in graph

            for (int i = 0; i < size1; i++) {
                if (!inverted_list_word_node.containsKey(arrayValue[i])) {
                    List<String> node_no = new ArrayList<>();                   // inverted list index content
                    node_no.add(key1);
                    inverted_list_word_node.put(arrayValue[i],node_no);

                    termFrequent.put(arrayValue[i], 1);

                    Set<Integer> noofgraph = new HashSet<>();
                    termR_radius.put(arrayValue[i], noofgraph);
                    for (int j = 0; j < rRadiusGraph.size(); j++) {
                        int noofGraph = rRadiusGraph.get(j);
                        List<String> nodesInSubgraph = connectedNode2.get(noofGraph);
                        if (nodesInSubgraph.contains(key1)){
                            termR_radius.get(arrayValue[i]).add(noofGraph);
                        }
                    }
                }
                else {
                    inverted_list_word_node.get(arrayValue[i]).add(key1);

                    int numberofFrequent = termFrequent.get(arrayValue[i]);
                    termFrequent.put(arrayValue[i], numberofFrequent + 1);

                    for (int j = 0; j < rRadiusGraph.size(); j++) {
                        int noofGraph = rRadiusGraph.get(j);
                        List<String> nodesInSubgraph = connectedNode2.get(noofGraph);
                        if (nodesInSubgraph.contains(key1)){
                            termR_radius.get(arrayValue[i]).add(noofGraph);
                        }
                    }
                }
            }
        }

        //////////////////////////////////////////////////////////

        Map<String, Double> ntf = new HashMap<>();                                      // term frequent <keyword, ntf>
        for (Map.Entry<String, Integer> entry1 : termFrequent.entrySet()
                ) {
            String key1 = entry1.getKey();                                              //keyword
            int value1 = entry1.getValue();                                             //the number of times of appearance about keyword

            ntf.put(key1, 1 + Math.log( 1.0 + Math.log( 1.0 + value1)));
        }

        int numberofrRadiusGraph = rRadiusGraph.size();
        Map<String, Double> idf = new HashMap<>();                                      // term frequent <keyword, idf>
        for (Map.Entry<String, Set<Integer>> entry1 : termR_radius.entrySet()
                ) {
            String key1 = entry1.getKey();                                              //keyword
            Set<Integer> value1 = entry1.getValue();                                    //which max_rRadiusGraphs include keyword
            int numberofvalue1inSet = value1.size();

            ntf.put(key1, Math.log( (numberofrRadiusGraph + 1.0)/(numberofvalue1inSet + 1.0) ));
        }

        /////////////////////////////////////////////////////ndl
        List<Integer> numberofItemsinEachrRadiusGraph = new ArrayList<>();
        for (int i = 0; i < rRadiusGraph.size(); i++) {
            int noofMaxGraph = rRadiusGraph.get(i);
            List<String> nodesInSubgraph = connectedNode2.get(noofMaxGraph);
            int tmpNumber = 0;

            for (int j = 0; j < nodesInSubgraph.size(); j++) {

                String noofNode = nodesInSubgraph.get(j);                               //node No.
                String value1 = nodes.get(noofNode);                                    //node value
                String [] arrayValue = value1.split(" ");

                int size1 = arrayValue.length;
                tmpNumber += size1;                                              // total item in graph
            }

            numberofItemsinEachrRadiusGraph.add(tmpNumber);
        }

        int sumallrRadiusIterm = 0;
        for (int i = 0; i < numberofItemsinEachrRadiusGraph.size(); i++) {
            sumallrRadiusIterm += numberofItemsinEachrRadiusGraph.get(i);
        }
        double avg = sumallrRadiusIterm * 1.0 / numberofItemsinEachrRadiusGraph.size();

        double ndl = (1 - 0.2) + 0.2 * (totalIteminGraph)/ (avg);

        //////////////////////////////////////////////////////////////////score IR
        Map<String, Double> scoreIR = new HashMap<>();
        for (Map.Entry<String, Double> entry1 : idf.entrySet()
                ) {
            String key1 = entry1.getKey();                                              //keyword
            double ntf_value = entry1.getValue();                                             //ntf
            double idf_value = idf.get(key1);

            scoreIR.put(key1, ntf_value * idf_value / ndl);
        }

        /////////////////////////////////////////////////////////


*/