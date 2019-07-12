import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.opencsv.CSVWriter;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONTokener;

/**
 * @ authour Gongsheng Yuan
 */
public class GenerateData {

    public static void main(String[] args) {
       /* // 读取nameID.txt文件中的NAMEID字段（key）对应值（value）并存储
        ArrayList<String> list = new ArrayList<String>();
        BufferedReader brname;
        try {
            brname = new BufferedReader(new FileReader("src/json/nameID.txt"));// 读取NAMEID对应值
            String sname = null;
            while ((sname = brname.readLine()) != null) {
                // System.out.println(sname);
                list.add(sname);// 将对应value添加到链表存储
            }
            brname.close();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }*/


        // 读取原始json文件并进行操作和输出
        try {

            //String path = "D:\\program\\Mine\\TransformFileType\\src\\dblp.json";  //Experiment example
            String path = "D:\\program\\Data\\dblp\\dblpworkshop1.json";  //Experiment example
            File file = new File(path);
            BufferedReader br = new BufferedReader(new FileReader(file));
            //FileInputStream fis = new FileInputStream(path);
            //InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            //BufferedReader br = new BufferedReader(isr);


            //BufferedReader br = new BufferedReader(new FileReader(
            //        "D:\\program\\Mine\\TransformFileType\\src\\dblp.json"));// 读取原始 json 文件

            String tempString = null;
            String tmp_author = null;
            String tmp_title = null;
            String tmp_id = null;
            List<String> authorList = new ArrayList<>();                           //for outputing author.csv
            List<String[]> friend = new ArrayList<String[]>();

            while ((tempString = br.readLine()) != null) {

                try {

                    JSONObject jsonObj = JSONObject.fromObject(tempString); //Formatting json object // 创建一个包含原始json串的json对象
                    //tmp_author = tmp_author + tempString + " ";

                    JSONArray paperList = new JSONArray();  // For output json file (paper.json)

                    JSONObject result = jsonObj.getJSONObject("result");// 找到 jsonObj 的 hits 对象
                    JSONObject hits = result.getJSONObject("hits");// 找到 jsonObj 的 hits 对象
                    JSONArray hit = hits.getJSONArray("hit");// 找到 hit 的json数组


                    for (int i = 0; i < hit.size(); i++) {
                    //for (int i = 0; i < 50; i++) {

                        ///////////////////////////////read json////////////////////////////////////////////
                        JSONObject array_obj = hit.getJSONObject(i);// 获取 hit 数组的第i个json对象

                        tmp_id = array_obj.getString("@id");// 获取 hit 数组的第i个json对象 array_obj_i 中的 @id 字符串
                        System.out.println(tmp_id);


                        JSONObject info = array_obj.getJSONObject("info");// 找到 info 的json对象
                        JSONObject info_authors = info.getJSONObject("authors");// 读取 info 对象里的 authors 对象
                        Object listArray = new JSONTokener(info_authors.getString("author")).nextValue();

                        List<String> nameListOutput = new ArrayList<>();

                        if (listArray instanceof JSONArray) {
                            JSONArray author = (JSONArray)listArray;
                            for (int j = 0; j < author.size(); j++) {

                                tmp_author = author.getString(j);
                                nameListOutput.add(tmp_author);

                                if(!authorList.contains(tmp_author))
                                    authorList.add(tmp_author);

                                System.out.println(tmp_author);

                            }
                        }else  {
                            tmp_author = info_authors.getString("author");// 找到 info_authors 的json对象
                            nameListOutput.add(tmp_author);                     // For outputing author_paper.csv

                            if(!authorList.contains(tmp_author))                // For outputing author.csv
                                authorList.add(tmp_author);

                            System.out.println(tmp_author);
                        }

                        tmp_title = info.getString("title");// 找到 info 中的 title 字符串

                        System.out.println(tmp_title);



                        /////////////////////////////// write json (paper.json)//////////////////////////////////////////

                        JSONObject paperDetails = new JSONObject();
                        paperDetails.put("id", tmp_id);
                        paperDetails.put("title", tmp_title);
                        try (BufferedWriter bw_json = new BufferedWriter(new FileWriter(
                                "D:\\program\\Data\\dblp\\paper.json", true))) {

                            bw_json.write(paperDetails.toString());
                            bw_json.newLine();
                            bw_json.flush();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        /////////////////////////////// write relation (author_paper.csv)/////////////////////////////////

                        File bw_relation_file = new File("D:\\program\\Data\\dblp\\author_paper.csv");

                        try {
                            // create FileWriter object with file as parameter
                            FileWriter bw_relation = new FileWriter(bw_relation_file, true);

                            // create CSVWriter with '|' as separator
                            CSVWriter writer = new CSVWriter(bw_relation, '|',
                                    CSVWriter.NO_QUOTE_CHARACTER,
                                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                                    CSVWriter.DEFAULT_LINE_END);

                            // create a List which contains String array
                            List<String[]> data = new ArrayList<String[]>();
                            //data.add(new String[] { "person name", "id", "rank" });

                            for (int j = 0; j < nameListOutput.size(); j++) {
                                data.add(new String[] { nameListOutput.get(j), tmp_id, String.valueOf(j + 1) });
                            }


                            writer.writeAll(data);

                            // closing writer connection
                            writer.close();
                        }
                        catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        ////////////////////////////////////////////////////////////////////////////////////////////////
                        for (int j = 0; j < nameListOutput.size()-1; j++)
                            for (int m = j + 1; m < nameListOutput.size(); m++){
                                friend.add(new String[] { nameListOutput.get(j), nameListOutput.get(m) });
                            }



                    }  //for

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }  //while

            /////////////////////////////// write graph1 (author.csv)/////////////////////////////////

            File graphFile1 = new File("D:\\program\\Data\\dblp\\author.csv");// 输出新的 graph1 文件


            try {
                // create FileWriter object with file as parameter
                FileWriter bw_graph1 = new FileWriter(graphFile1, true);

                // create CSVWriter with '|' as separator
                CSVWriter writer = new CSVWriter(bw_graph1, '|',
                        CSVWriter.NO_QUOTE_CHARACTER,
                        CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                        CSVWriter.DEFAULT_LINE_END);

                List<String[]> data = new ArrayList<String[]>();
                for (int j = 0; j < authorList.size(); j++) {
                    data.add(new String[] { authorList.get(j) });
                }

                writer.writeAll(data);

                // closing writer connection
                writer.close();
            }
            catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            /////////////////////////////// write graph2 (friend.csv)/////////////////////////////////

            File graphFile2 = new File("D:\\program\\Data\\dblp\\friend.csv");// 输出新的 graph2 文件

            try {
                FileWriter bw_graph2 = new FileWriter(graphFile2, true);
                List<String[]> data = new ArrayList<String[]>();
                // create CSVWriter with '|' as separator
                CSVWriter writer = new CSVWriter(bw_graph2, '|',
                        CSVWriter.NO_QUOTE_CHARACTER,
                        CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                        CSVWriter.DEFAULT_LINE_END);

                // create a List which contains String array
                //List<String[]> data = new ArrayList<String[]>();
                int number_friend = 0;
                String[] tmp_friend1;
                String[] tmp_friend2;


                for (int j = 0; j < friend.size()-1; j++) {
                    int flag = 0;
                    tmp_friend1 = friend.get(j);
                    for (int m = j + 1; m < friend.size(); m++) {

                        tmp_friend2 = friend.get(m);

                        if (tmp_friend1[0].equals(tmp_friend2[0]) && tmp_friend1[1].equals(tmp_friend2[1]) || tmp_friend1[0].equals(tmp_friend2[1]) && tmp_friend1[1].equals(tmp_friend2[0])) {
                            flag = 1;
                            break;
                        }
                    }
                    if (flag == 0){
                        number_friend ++;
                        data.add(tmp_friend1);
                    }
                }//for




                ///////////////////////////////////////////////random friend/////////////////////////////////////////

                int new_friend = (int) ((int) number_friend * 0.1);
                for (int j = 0; j < new_friend; j++) {

                    int flag = 1;

                    while(flag == 1) {
                        flag = 0;
                        Random rand = new Random();
                        int n1 = rand.nextInt(authorList.size());
                        int n2 = rand.nextInt(authorList.size());

                        while (n1 == n2) {
                            n2 = rand.nextInt(authorList.size());
                        }

                        tmp_friend1 = new String[]{authorList.get(n1), authorList.get(n2)};


                        for (int i = 0; i < data.size(); i++) {
                            tmp_friend2 = data.get(i);

                            if (tmp_friend1[0].equals(tmp_friend2[0]) && tmp_friend1[1].equals(tmp_friend2[1]) || tmp_friend1[0].equals(tmp_friend2[1]) && tmp_friend1[1].equals(tmp_friend2[0])) {
                                flag = 1;
                                break;
                            }
                        }
                        if (flag == 0) {
                            data.add(tmp_friend1);
                        }

                    }//while
                }//for


                writer.writeAll(data);
                // closing writer connection
                writer.close();
            }
            catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            /////////////////////////////////////////////////////////////
            br.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}


