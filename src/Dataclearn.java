import com.opencsv.CSVWriter;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONTokener;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @ authour Gongsheng Yuan
 */
public class Dataclearn {
    public static void main(String[] args) {

        try {

            //String path = "D:\\program\\Mine\\TransformFileType\\src\\dblp.json";  //Experiment example
            String path = "D:\\program\\Data\\dblp\\dblpworkshop.json";  //Experiment example
            File file = new File(path);
            BufferedReader br = new BufferedReader(new FileReader(file));


            String tempString = "";
            String context = "";

            while ((tempString = br.readLine()) != null) {
                context = context + tempString + " ";

            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(
                    "D:\\program\\Data\\dblp\\dblpworkshop1.json", true))) {

                bw.write(context);
                bw.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }catch (IOException e) {
            e.printStackTrace();
        }

//        try {
//
//            String path = "D:\\program\\Data\\dblp\\dblpworkshop.json";  //Experiment example
//            //String path = "D:\\program\\Data\\dataset1\\dblp(original file which includes carriage return).json";  //Experiment example
//            File file = new File(path);
//            BufferedReader br = new BufferedReader(new FileReader(file));
//
//            String tempString = "";
//
////            while ((tempString = br.readLine()) != null) {
////                System.out.println((int)tempString.trim().charAt(0));
////                //System.out.println(tempString);
////
////                File writename = new File("D:\\program\\Data\\dblp\\4.json"); // 相对路径，如果没有则要建立一个新的output。txt文件
////                writename.createNewFile(); // 创建新文件
////                BufferedWriter out = new BufferedWriter(new FileWriter(writename));
////                out.write(tempString.substring(1));
////                out.flush(); // 把缓存区内容压入文件
////                out.close(); // 最后记得关闭文
////            }
//
//
//            File writename = new File("D:\\program\\Data\\dblp\\dblpworkshop1.json"); // 相对路径，如果没有则要建立一个新的output。txt文件
//            writename.createNewFile(); // 创建新文件
//            BufferedWriter out = new BufferedWriter(new FileWriter(writename));
//
//            while ((tempString = br.readLine()) != null) {
//                if ((int)tempString.trim().charAt(0) != 123){
//                    out.write(tempString.substring(1)+ "\r\n");
//                }
//                else{
//                    out.write(tempString + "\r\n");
//                }
//
//            }
//
//
//            out.flush(); // 把缓存区内容压入文件
//            out.close(); // 最后记得关闭文
//
//
//        }catch (IOException e) {
//            e.printStackTrace();
//        }



    }
}