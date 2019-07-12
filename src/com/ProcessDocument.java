package com;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @ authour Gongsheng Yuan
 */
public class ProcessDocument {

    int fileNumber = 0;                                 // count how many files we produce
    //String folder_path_name = "D:\\program\\XMLtoDocument";  //Where would transformed files be put? // This kind of pathname is for Windows, Unix is for "/home/.../XMLtoDocument"
    String folder_path_name = "D:\\program\\Data\\trytry"; //read file
    String out_folder_path_name = "D:\\program\\Data\\trytry"; //output file
    String file_name = "xml1.txt";
    List<String> final_contents = new ArrayList<String>();

    public void readDocument() throws IOException{
        try{
            File file = new File( folder_path_name + "\\" + file_name);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = "";
            while ((line = bufferedReader.readLine()) != null){
                if (line.equals("next")) {
                    //////////////////////////////////////output///////////////////////////
                    this.fileNumber ++;
                    //String file_name = "document" + this.fileNumber + ".txt";
                    String file_name = "document" + this.fileNumber;
                    createFileAndoutputData(file_name, this.final_contents);
                    this.final_contents.clear();

                }else {
                    this.cleanAndStem(line.trim());
                }

            }

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
        File output_file = new File(out_folder_path_name + "\\" + file_name);

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
            FileWriter fileWriter = new FileWriter(output_file, true);
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

        //\\p{Alnum} means digital or alphabetic character  (Regular expression in Java)
        //[^abc] means any character except a, b and c.
        String clean = str.toLowerCase().replaceAll("[^\\p{Alnum}]", " ").replaceAll("\\s\\s+", " ");
        // here we just split a word into two words.(like micro-robot becomes micro robot)

        String[] words = clean.split("\\s+");

        for (String w : words){
            addElementtoList((String) stemmer.stem(w));
        }
    }

    // add processed word to ArrayList
    public void addElementtoList(String element) {
        this.final_contents.add(element);
    }



}
