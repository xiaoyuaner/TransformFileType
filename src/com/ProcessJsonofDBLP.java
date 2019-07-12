package com;

import net.sf.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @ authour Gongsheng Yuan
 */
public class ProcessJsonofDBLP {


    public List<ElementJsonofDBLP> readJsonFile() throws FileNotFoundException {

        String path = "D:\\program\\Data\\dblp\\paper.json";                //Experiment example
        File file = new File(path);
        BufferedReader reader = new BufferedReader(new FileReader(file));

        List<ElementJsonofDBLP> arrayList = new ArrayList<>();  // Save class array

        try {

            String tempString = null;

            while ((tempString = reader.readLine()) != null) {

                ElementJsonofDBLP elementJson = new ElementJsonofDBLP();
                JSONObject jsonObj = JSONObject.fromObject(tempString); //Formatting json object

                elementJson.setId(jsonObj.getString("id"));
                elementJson.setTitle(jsonObj.getString("title"));

                arrayList.add(elementJson);

            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }

        return arrayList;
    }


//    //设置属性，并保存
//    public boolean setElect(String path,String sets){
//        try {
//            writeFile(path,sets);
//            return true;
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//            return false;
//        }
//    }
//    //把json格式的字符串写到文件
//    public void writeFile(String filePath, String sets) throws IOException {
//        FileWriter fw = new FileWriter(filePath);
//        PrintWriter out = new PrintWriter(fw);
//        out.write(sets);
//        out.println();
//        fw.close();
//        out.close();
//    }


}
