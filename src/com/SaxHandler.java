package com;/*
 * @authour Gongsheng Yuan
 */

import opennlp.tools.stemmer.snowball.SnowballStemmer;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.*;
import java.util.*;

public class SaxHandler extends DefaultHandler{

    int fileNumber = 0;                                 // count how many files we produce
    //String folder_path_name = "D:\\program\\XMLtoDocument";  //Where would transformed files be put? // This kind of pathname is for Windows, Unix is for "/home/.../XMLtoDocument"
    //String folder_path_name = "D:\\program\\Data\\Sigmod(XML)";
    String out_folder_path_name = "D:\\program\\Data\\XML";
    Stack<String> stack = new Stack<String>();    // for element match, so we need a stack(Judging a complete xml paragraph when it finish)

//    ////////////////////////////////////////////For reading stop words////////////////////////////////////////////
//    Set<String> stopwords = new HashSet<String>(); // for storing stop words
//    String stopwordFile = "D:\\program\\Data\\stopwords\\stopwords_en.txt";
//    ////////////////////////////////////////////For reading stop words////////////////////////////////////////////
     private List<String> final_contents = null;

    /*
     *Begin to parse xml file
     */
    @Override
    public void startDocument() throws SAXException {
        super.startDocument();

        System.out.println("Start to parse xml document");
        this.final_contents = new ArrayList<String>();

        //Create folder
        createFolder(this.out_folder_path_name);

//        //Read stop words from file into HashSet
//        if (this.stopwordFile != null) {
//            try {
//                File file = new File( stopwordFile);
//                FileReader fileReader = new FileReader(file);
//                BufferedReader bufferedReader = new BufferedReader(fileReader);
//                String line = "";
//                while ((line = bufferedReader.readLine()) != null){
//                    this.stopwords.add(line);
//                }
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }

    }


    /**
     * Create folder
     */

    private void createFolder(String folder_name) {

        File folderName = new File(folder_name);

        if (folderName.exists()) {
            System.out.println("The folder XML has existed");
        } else {
            folderName.mkdir(); // Create folder /home/.../XMLtoDocument
        }
    }

    /*
       *Parsing xml file is over.
     */
    @Override
    public void endDocument() throws SAXException {
        super.endDocument();

        System.out.println("Parsing xml is over");
    }

    /*
     *Begin to parse xml element
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);

        if (qName.equals("sigmod")){

        }
        else{
            stack.push(qName);

            // clean word (delete all the elements which are not digital or letter)
            //stem word
            //add these words into final list
            cleanAndStem(qName);

            // ***************************************************************************************************
            // To get attribute of tag, we need to traverse Attributes to get "attribute-name" and "attribute-value", respectively.

            int len = attributes.getLength();

            for(int i = 0; i < len; i++) {
                //Through Attributes.getQName(int index), we can get attribute name.
                cleanAndStem(attributes.getQName(i));
                //System.out.println("，属性值-->" + attributes.getQName(i));

                //Through Attributes.getValue(int index), we can get attribute value.
                cleanAndStem(attributes.getValue(i));
                //System.out.println("，属性值-->" + attributes.getValue(i));
            }
        }

//        tag = qName;
//        if (lookfor.equals(tag)) {
//            temp_contents = new ArrayList<String>();
//        }
    }



    /*
     *   Parsing xml element is over.
     */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);

        if (!stack.isEmpty()){
            String str = stack.peek();
            if (str.equals(qName)){
                stack.pop();
                if (stack.isEmpty()){   // When stack is empty, we can get a complete part of XML.
                    //Output text, empty list
                    fileNumber ++;
                    //String file_name = "xml" + fileNumber + ".txt";
                    String file_name = "xml" + fileNumber;
                    createFileAndoutputData(file_name, final_contents);
                    final_contents.clear();
                }
            }else {
                System.out.println("There is something wrong about element");
            }

        }
//        if ( temp_contents != null ){  把列表里的元素变成字符串
//            StringBuilder result = new StringBuilder();
//            for (String str : temp_contents){
//                result.append(str);
//                result.append(" ");
//            }
//            final_contents.add(result.toString());
//            temp_contents = null;
//        }
//
//        tag = null;

    }


    /**
     * Create file
     * Transform list into string
     * Output string into txt
     * @param file_name
     * @param contents this list store strings which are the content.
     */
    private void createFileAndoutputData(String file_name, List<String> contents) {

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

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);

        String str = new String(ch, start, length);

        // We use stemAndClean to clean and stem word.
        cleanAndStem(str);



//        if (lookfor.equals(tag)) {
//            String str = new String(ch, start, length);  //如果是我们想要找的内容
//            temp_contents.add(str);
//        }
    }

    /**
     * clean word (delete all the elements which are not digital or letter)
     * stem word
     * add these words into final list
     * @param str
     */
    private void cleanAndStem(String str) {

        SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);

        String lowerCase_str = str.toLowerCase();
        String[] first_words = lowerCase_str.split("\\s+");

        StringBuilder result = new StringBuilder();
        for (String w : first_words){
            result.append(w);
            result.append(" ");
//            if (!stopwords.contains(w)) {
//                result.append(w);
//                result.append(" ");
//            }

        }

        String clean = result.toString().trim();


        //\\p{Alnum} means digital or alphabetic character  (Regular expression in Java)
        //[^abc] means any character except a, b and c.
        //String clean = str.toLowerCase().replaceAll("[^\\p{Alnum}]", " ").replaceAll("\\s\\s+", " ");
        // here we just split a word into two words.(like micro-robot becomes micro robot)

        clean = clean.replaceAll("[^\\p{Alnum}]", " ").replaceAll("\\s\\s+", " ");

        String[] second_words = clean.split("\\s+");

        //clean = "";
        for (String w : second_words){
            addElementtoList((String) stemmer.stem(w));
            //clean += stemmer.stem(w) + " ";
//            if (!stopwords.contains(w)) {
//                addElementtoList((String) stemmer.stem(w));
//            }
        }

        return;
    }

    // add processed word to ArrayList
    private void addElementtoList(String element) {
        this.final_contents.add(element);
    }


}
