package com;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @ authour Gongsheng Yuan
 */
public class ProcessQuery {

    //String folder_path_name = "D:\\program\\XMLtoDocument";  //Where would transformed files be put? // This kind of pathname is for Windows, Unix is for "/home/.../XMLtoDocument"
    String folder_PathName = "D:\\program\\Data\\dataset3InvertedList\\query"; //read file
    String outFolder_PathName = "D:\\program\\Data\\dataset3InvertedList\\query"; //output file
    String file_Name_read = "query4.txt";
    Set<String> stopwords = new HashSet<String>(); // for storing stop words
    List<String> final_contents = new ArrayList<String>();

    public void readDocument() throws IOException {
        try{

            ///////////////////////////////////////////For reading stop words//////////////////////////////////////////

            String stopwordFile = "D:\\program\\Data\\stopwords\\stopwords_en.txt";

            //Read stop words from file into HashSet
            try {
                File file1 = new File(stopwordFile);
                FileReader fileReader1 = new FileReader(file1);
                BufferedReader bufferedReader1 = new BufferedReader(fileReader1);
                String line1 = "";
                while ((line1 = bufferedReader1.readLine()) != null){
                    stopwords.add(line1);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //////////////////////////////////////////////////////////////////////////////////////////////////////////
            File file = new File( folder_PathName + "\\" + file_Name_read);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = "";
            while ((line = bufferedReader.readLine()) != null){
                this.cleanAndStem(line.trim());
            }


            ////////////////////////////////////////////////////////output/////////////////////////////////////////////

            String file_Name_write = "query";
            createFileAndoutputData(file_Name_write, this.final_contents);
            this.final_contents.clear();


        }catch (IOException e){
            e.printStackTrace();
        }
    }


    /**
     * Create file
     * Transform list into string
     * Output string into txt
     * @param file_name
     * @param contents this list store strings which are the content.
     */
    public void createFileAndoutputData(String file_name, List<String> contents) {
        //****************************************Create file*******************************************
        File output_file = new File(outFolder_PathName + "\\" + file_name);

        if (output_file.exists()) {
            System.out.println("The file has existed");
        } else {
            try {
                output_file.createNewFile();     // Create file, this is different form creating folder
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //****************************************Transform list into string*******************************************
        String output = null;
        if ( !contents.isEmpty() ){
            StringBuilder result = new StringBuilder();
            for (String str : contents){
                result.append(str);
                result.append(" ");
            }
            output = result.toString().trim();
        }

        //****************************************Output*******************************************
        try {
            FileWriter fileWriter = new FileWriter(output_file, false);
            fileWriter.write(output);

            fileWriter.close(); // Close file stream

        } catch (IOException e) {
            e.printStackTrace();
        }
    }





    /**
     * clean word (delete all the elements which are not digital or letter)
     * stem word
     * add these words into final list
     * @param str
     */
    public void cleanAndStem(String str) {

        SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);

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
            return;
        }
        //\\p{Alnum} means digital or alphabetic character  (Regular expression in Java)
        //[^abc] means any character except a, b and c.
        clean = clean.replaceAll("[^\\p{Alnum}]", " ").replaceAll("\\s\\s+", " ");
        // here we just split a word into two words.(like micro-robot becomes micro robot)

        String[] words = clean.split("\\s+");

        for (String w : words){
            if (!stopwords.contains(w)) {
                addElementtoList((String) stemmer.stem(w));
            }
        }
    }

    // add processed word to ArrayList
    public void addElementtoList(String element) {
        this.final_contents.add(element);
    }
}
