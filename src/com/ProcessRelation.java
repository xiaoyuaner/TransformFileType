package com;

import opennlp.tools.stemmer.snowball.SnowballStemmer;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProcessRelation {

    int fileNumber = 0;                                 // count how many files we produce
    //String folder_path_name = "D:\\program\\XMLtoDocument";  //Where would transformed files be put? // This kind of pathname is for Windows, Unix is for "/home/.../XMLtoDocument"
    String folder_path_name = "D:\\program\\Data\\example(Relation)";
    String out_folder_path_name = "D:\\program\\Data\\Relation";
    List<String> final_contents = new ArrayList<String>();


    public void readXlsx() throws IOException
    {

        File excelFile = new File(folder_path_name + "\\order.xlsx");
        //XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(excelFile));
        XSSFWorkbook xwb = new XSSFWorkbook(new FileInputStream(excelFile));
        XSSFSheet sheet = xwb.getSheetAt(0);
        Row row;
        String cell;
        List<String> titleName = new ArrayList<String>();  // store the first row about title
        String outPutString = "";

        //////////////////////////////////////// read first row ////////////////////////////////////////
        int firstRow = sheet.getFirstRowNum();
        row = sheet.getRow(firstRow);
        for (int j = row.getFirstCellNum(); j < row.getPhysicalNumberOfCells(); j++)
        {
            cell = row.getCell(j).toString();
            titleName.add(cell);
        }

        //////////////////////////////////////// read what follows ////////////////////////////////////////
        for (int i = firstRow + 1; i < sheet.getPhysicalNumberOfRows(); i++)
        {
            row = sheet.getRow(i);
            outPutString = "";      // Initial
            for (int j = row.getFirstCellNum(); j < row.getPhysicalNumberOfCells(); j++)
            {
                cell = row.getCell(j).toString();
                if ( !cell.isEmpty() ){
                    outPutString += titleName.get(j) + " " + cell + " ";
                }
            }
            System.out.println(outPutString);
            this.cleanAndStem(outPutString.trim());

            //////////////////////////////////////output///////////////////////////
            this.fileNumber ++;
            //String file_name = "relation" + this.fileNumber + ".txt";
            String file_name = "relation" + this.fileNumber;
            createFileAndoutputData(file_name, this.final_contents);
            this.final_contents.clear();

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
