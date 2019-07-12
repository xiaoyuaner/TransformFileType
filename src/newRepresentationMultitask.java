import opennlp.tools.stemmer.snowball.SnowballStemmer;
import org.apache.commons.math3.linear.*;

import java.io.*;
import java.util.*;

/**
 * @ authour Gongsheng Yuan
 */
public class newRepresentationMultitask {

    public static int MatrixDimension = 50;

    public static void main(String[] args) throws IOException {
        //Map<String, List<String>> order = new HashMap<String, List<String>>();
        //Map<String, List<String>> personHashMap = new HashMap<String, List<String>>();
        //Map<String, List<String>> person_graph = new HashMap<String, List<String>>();
        //Map<String, List<String>> person_graph_order = new HashMap<String, List<String>>();
        //Map<String, List<String>> person_order_feedback = new HashMap<>();

        long startTime =System.currentTimeMillis();   //获取开始时间


        ////////////////////////////////////////////For reading wordSpace //////////////////////////////////////////////
        Map<String,Integer> wordSpace = new HashMap<>();
        String wordSpaceFile = "D:\\program\\Data\\wordspaceinvertedwordanddocument\\wordSpace\\wordSpace";

        //Read stop words from file into Map
        try {
            File file = new File(wordSpaceFile);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = "";
            int count = 1;

            while ((line = bufferedReader.readLine()) != null){

                String[] words = line.split("\\s+");
                for (String w : words){
                    wordSpace.put(w, count);
                    count ++;

                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int wordSpace_size = wordSpace.size();

        System.out.println("word space");
        System.out.println(wordSpace);


        String documentfolder = "D:\\multitask\\input19";
        String outFolder_pathName_invertedListDensityMatrix = "D:\\program\\Data\\invertedListDensityMatrix50\\output19";
        CreateFolder(outFolder_pathName_invertedListDensityMatrix);



        ////////////////////////////////////////////For producing density matrix //////////////////////////////////////////////


        //List documentNameList = new ArrayList();
        try {
            File folder = new File(documentfolder);
            String[] fileNameList = folder.list();

            for (int i = 0; i < fileNameList.length; i++) {

                String fileName = documentfolder + "\\" + fileNameList[i];

                File file = new File(fileName);
                FileReader fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String line = "";
                List<String> map_value = new ArrayList<>();

                while ((line = bufferedReader.readLine()) != null){
                    String[] words = line.split("\\s+");
                    for (String w : words){
                        map_value.add(w);
                    }
                }


                List<RealMatrix> matrices = TransformSingleProjector( wordSpace, map_value );                                   //Store matrices of single word and dependencies.
                List<RealMatrix> matrices_dependencies = TransformDependentProjectors( wordSpace, map_value );
                if (matrices_dependencies.size() > 0) {
                    matrices.addAll(matrices_dependencies);
                    matrices_dependencies.clear();
                }

                RealMatrix density_matrix = DensityMatrix(matrices, wordSpace_size);

                Map<Integer, RealMatrix> inverted_list_densityMatrix = new HashMap<>();
                int count = Integer.parseInt(fileNameList[i]);

                inverted_list_densityMatrix.put(count, density_matrix);                                                         // Add the density matrix into the inverted list.
                matrices.clear();

                OutputDensityMatrix(outFolder_pathName_invertedListDensityMatrix, inverted_list_densityMatrix);
                inverted_list_densityMatrix.clear();        // for saving space
                System.out.println(fileNameList[i]);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }




        System.out.println("Done");


        long endTime = System.currentTimeMillis(); //获取结束时间
        System.out.println("程序运行时间： "+(endTime - startTime)+"ms");







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
                fileWriter.write(content.get(i)+"\t");
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
                        if(array[i][j] != 0) {
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
            if (Math.abs(likelihoodValue(matrix2, matrices) - likelihoodValue(matrix1, matrices)) < Math.pow(10, -4)){
                break;
            }
            else {
                matrix1 = matrix2;
                matrix2 = RPRAlgorithm(matrix1, matrices);
                RealMatrix left = matrix1.scalarMultiply( 1 - coefficient);
                RealMatrix right = matrix2.scalarMultiply( coefficient );
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

            RealVector dependenciesVector1 = new OpenMapRealVector(MatrixDimension);                                     //For constructing dependencies' s projector, this is a initial vector.

            //Here we process words in the dependencies one by one.
            for (int j = 0; j < 2; j++) {
                int position1 = wordSpace.get(strings[j]);

                if (position1 <= wordSpace_size) {
                    RealVector dependenciesComponent1 = SingleTermRepreOfDepen(position1, 2);
                    dependenciesVector1 = dependenciesVector1.add(dependenciesComponent1);
                }
                else {
                    System.out.println("space is small");
                }
            }

            RealMatrix matrix1 = dependenciesVector1.outerProduct(dependenciesVector1);
            matrices.add(matrix1);
        }

        if (map_value_size < 3 )
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
                }
                else {
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
            vector.setEntry(j, d * 1.0 / ( Math.sqrt(count) * Math.sqrt(numberOfDepen) ) );
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

            if (wordPosition <= wordSpace_size ) {

                RealVector vector = SingleTermRepresentation(wordPosition);
                RealMatrix projector = vector.outerProduct(vector);
                matrices.add(projector);

            }
            else
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
}
