package Text;

import org.apache.commons.math3.linear.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @ authour Gongsheng Yuan
 */
public class LearnMatrix {
    public static void main(String[] args) {

        RealVector vector = new OpenMapRealVector(2);
        //vector.set(1);
        vector.setEntry(0, 1.0/Math.sqrt(2));

        ///////////////////////

        RealVector vector1 = new OpenMapRealVector(2);

        vector1.setEntry(1, 1);

        ///////////////////

        RealMatrix matrix1 = vector.outerProduct(vector);

        RealMatrix matrix2 = vector1.outerProduct(vector1);

        for (int i = 0; i < vector.getDimension(); i++) {
            System.out.println(vector.getEntry(i));
        }
        for (int i = 0; i < vector.getDimension(); i++) {
            System.out.println(vector1.getEntry(i));
        }


        matrix1.scalarMultiply(0.75);   // The values of matrix1 don't change.

        matrix2.scalarMultiply(0.25);

        RealMatrix matrix3 = matrix1.scalarMultiply(0.75).add(matrix2.scalarMultiply(0.25));



        System.out.println(matrix1);
        System.out.println("matrix1: getData()");
        double[][] matrix1_array = matrix1.getData();
        for (int i = 0; i < matrix1_array.length; i++) {
            for (int j = 0; j < matrix1_array[i].length; j++) {
                System.out.println(matrix1_array[i][j]);
            }

        }


        System.out.println(matrix2);
        System.out.println(matrix3);


        RealMatrix matrix4 = new OpenMapRealMatrix(2,2);
        matrix4.setEntry(0, 0, 1);

        System.out.println("Matrix4:");
        System.out.println(matrix4);

        List<String> list1 = new ArrayList<>();
        list1.add("a");
        list1.add("b");
//        list1.add("c");
//        list1.add("d");

        String[] strings = (String[]) list1.subList(0, 0+2).toArray(new String[3]);
        System.out.println(strings.length);

        for (int i = 0; i < strings.length; i++) {
            System.out.println(strings[i]);
        }

        int dimension = 3;
        RealMatrix identity = MatrixUtils.createRealIdentityMatrix(dimension);

        System.out.println(identity);


        RealMatrix matrix5 = MatrixUtils.createRealMatrix(2, 2);
        System.out.println(matrix5);

    }
}
