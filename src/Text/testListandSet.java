package Text;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * @ authour Gongsheng Yuan
 */
public class testListandSet {
    public static void main(String[] args) {

        List<String> map_value = new ArrayList<>();
        map_value.add("a");
        map_value.add("b");
        map_value.add("a");

        LinkedHashSet<String> set = new LinkedHashSet<String>(map_value.size());                    //remove duplicate
        set.addAll(map_value);

        System.out.println(set);


    }

}
