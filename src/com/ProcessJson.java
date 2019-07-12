package com;
import com.ElementJsonClass;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;
/**
 * @ authour Gongsheng Yuan
 */
public class ProcessJson {


    public List<ElementJsonClass> readJsonFile() throws FileNotFoundException {

        //String path = "D:\\program\\Data\\ParameterCuration\\order.json"; //example in article

        String path = "D:\\program\\Data\\ParameterCuration\\original\\order.json";  //Experiment example
        File file = new File(path);
        BufferedReader reader = new BufferedReader(new FileReader(file));

        List<ElementJsonClass> arrayList = new ArrayList<ElementJsonClass>();  // Save class array

        try {

            String tempString = null;

            while ((tempString = reader.readLine()) != null) {

                ElementJsonClass elementJson = new ElementJsonClass();
                JSONObject jsonObj = JSONObject.fromObject(tempString); //Formatting json object

                elementJson.setOrderId(jsonObj.getString("OrderId"));
                elementJson.setPersonId(jsonObj.getString("PersonId"));
                elementJson.setOrderDate(jsonObj.getString("OrderDate"));
                elementJson.setTotalPrice(jsonObj.getString("TotalPrice"));

                elementJson.setOrderLine(jsonObj.getJSONArray("Orderline"));

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
//        System.out.println( arrayList.get(0).getOrderId() );
//        System.out.println( arrayList.get(0).getPersonId() );
//        System.out.println( arrayList.get(0).getOrderDate() );
//        System.out.println( arrayList.get(0).getTotalPrice() );
//        System.out.println( arrayList.get(0).getOrderLine() );

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
