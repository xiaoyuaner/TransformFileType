package Text;/*
 *@ authour Gongsheng Yuan
 */

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Test_for_readandwrithe_Arraylist_onebyone {

    public static void main(String[] args) throws Exception{

        //**************************************Create folder************************************************
        // This kind of pathname is for Windows, Unix is for "/home/.../XMLtoDocument"
        File folderName = new File("D:\\program\\XMLtoDocument");

        if (folderName.exists()) {
            System.out.println("The folder XMLtoDocument has existed");
        } else {
            folderName.mkdir(); // Create folder 创建文件夹/home/.../XMLtoDocument
        }

        //**************************************Create File************************************************
        String fileName = "GuessWhoIam";

        File write_file = new File("D:\\program\\XMLtoDocument\\" + fileName);

        if (write_file.exists()) {
            System.out.println("The file has existed");
        } else {
            try {
                write_file.createNewFile();     // Create file, this is different form creating folder文件的创建，注意与文件夹创建的区别
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        /**
            * According orginal author http://blog.csdn.net/xtj332, the simplest method for writing/reading is to use  class FileWriter
            * (Its parent classes are java.io.OutputStreamWriter——>java.io.Writer——>java.lang.Object in turn);
            */

        // **************************************Now we begin write data to file**************************************
        try {
            FileWriter fileWriter = new FileWriter(write_file, true);
            //String s = new String("This is a test!  \n" + "aaaa");
            //fileWriter.write(s);
            ArrayList<String> input_list = new ArrayList<String>();
            input_list.add("We");
            input_list.add("I");
            input_list.add("am");
            input_list.add("Chinese");
            input_list.add("ha");
            input_list.add("ha");

            for(String term: input_list){
                fileWriter.write(term);
            }

            fileWriter.close(); // 关闭数据流

        } catch (IOException e) {
            e.printStackTrace();
        }

        /*
         * Using this kind of method, we just add new contents in the end of file.
         * If you want to delete all the data in the file, and then put your data in the file,
         * you should use FileWriter fileWriter=new FileWriter(write_file) when you create FileWriter.
        */

        // **************************************Now we begin read data from file**************************************
        try {
            FileReader fileReader = new FileReader(write_file);
            String s = null;
            char ch;
            try {
                char[] c = new char[100];
                fileReader.read(c,0,2); // Just want to see the value got from file（single char？int？or String?）
                System.out.println(c);
                fileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
         /**
            *  Want to see the value got from file（single char？int？or String?）, we need to know the different usages about read()
            *  1. int read()                Get single char
            *  2. int read(char[] cbuf)     Put char into array, we can transform char array into character string
            *  3.  int read(char[] cbuf,int off,int len)    Read characters into a part of an array
            *  All of these three methods will return a int value. The function is that when it arrive at the end of the stream, it will return -1.
        */

        // **************************************Disadvantage**************************************
        /*
            *The disadvantage is that there is not space in the content;
            * The result： IamChinesehaha
         */
        int ii = 1;
        String ss = "document" + ii;
        System.out.println(ss);

}

    }
