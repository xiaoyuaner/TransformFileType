import com.csvreader.CsvReader;
import com.opencsv.CSVWriter;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

/**
 * @ authour Gongsheng Yuan
 */
public class AddingAuthorNointoData {
    public static void main(String[] args) throws IOException {


        List<String[]> csvAuthorPaperList = new ArrayList<>();

        String filePath_AuthorPaper = "D:\\program\\Data\\dataset3\\author.csv";
        CsvReader reader3 = new CsvReader(filePath_AuthorPaper, '|', Charset.forName("UTF-8"));

        while (reader3.readRecord()) {            // read data line-by-line except header
            csvAuthorPaperList.add(reader3.getValues());
        }
        reader3.close();

        List<String> author = new ArrayList<>();
        Map<String, Integer> author_number = new HashMap<>();


        for (int i = 0; i < csvAuthorPaperList.size(); i++) {
            String tmp = csvAuthorPaperList.get(i)[0];
            author.add(tmp);
            author_number.put(tmp, i);
            System.out.println(tmp);

        }


        File bw_relation_file = new File("D:\\program\\Data\\dataset3\\For store in arangodb, I have add key into author and friend\\author.csv");

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

            for (int j = 0; j < author.size(); j++) {
                data.add(new String[] { String.valueOf(j), author.get(j) });
            }


            writer.writeAll(data);

            // closing writer connection
            writer.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }



        List<String[]> csvAuthorPaperList1 = new ArrayList<>();
        List<String[]> csvAuthorPaperList2 = new ArrayList<>();

        String filePath_Friend = "D:\\program\\Data\\dataset3\\friend.csv";
        CsvReader reader = new CsvReader(filePath_Friend, '|', Charset.forName("UTF-8"));

        while (reader.readRecord()) {            // read data line-by-line except header
            csvAuthorPaperList1.add(reader.getValues());
        }
        reader.close();

        for (int i = 0; i < csvAuthorPaperList1.size(); i++) {

            String tmp1 = csvAuthorPaperList1.get(i)[0];
            String tmp2 = csvAuthorPaperList1.get(i)[1];
            int no_tmp1 = author_number.get(tmp1);
            int no_tmp2 = author_number.get(tmp2);
            csvAuthorPaperList2.add(new String[] { String.valueOf(no_tmp1), String.valueOf(no_tmp2) });

        }


        File bw_relation_file2 = new File("D:\\program\\Data\\dataset3\\For store in arangodb, I have add key into author and friend\\friend.csv");

        try {
            // create FileWriter object with file as parameter
            FileWriter bw_relation = new FileWriter(bw_relation_file2, true);

            // create CSVWriter with '|' as separator
            CSVWriter writer = new CSVWriter(bw_relation, '|',
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);


            writer.writeAll(csvAuthorPaperList2);

            // closing writer connection
            writer.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }
}



