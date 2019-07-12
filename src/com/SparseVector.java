package com;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @ authour Gongsheng Yuan
 */
public class SparseVector {

    public Map<Integer,Double> Triples;                         //向量的三元组表示
    public int dimension;                                       //向量维度


    public int getDimension() {
        return dimension;
    }
    public void setDimension(int dimension) {
        this.dimension = dimension;
    }


    /*
     * 构造函数1
     */
    public SparseVector(){

    }


    /*
     * 构造函数2
     */
    public SparseVector(Map<Integer, Double> triples, int dimension) {

        Triples = triples;
        this.dimension = dimension;
    }

    /*
     * 构造函数3
     */
    public SparseVector(Map<Integer, Double> triples) {
        Triples = triples;
    }


    /*
     * 构造函数4
     */
    public SparseVector(int dimension){
        this.Triples = new HashMap<>();
        this.dimension = dimension;
    }



    /*
     * 稀疏向量相乘函数
     */
    public SparseMatrix Multiply(SparseVector M,SparseVector N){

        Map<ArrayList<Integer>,Double> triples = new HashMap<ArrayList<Integer>,Double>();
        Iterator<Map.Entry<Integer, Double>> it1 = M.Triples.entrySet().iterator();

        while(it1.hasNext()){

            Map.Entry<Integer, Double> entry = it1.next();
            int position = entry.getKey();
            double value = entry.getValue();

            Iterator<Map.Entry<Integer, Double>> it2 = N.Triples.entrySet().iterator();
            while(it2.hasNext()){

                Map.Entry<Integer, Double> entry2 = it2.next();
                int position2 = entry2.getKey();
                double value2 = entry2.getValue();

                ArrayList<Integer> temp = new ArrayList<Integer>();
                temp.add(position);
                temp.add(position2);
                double v = value * value2;

                triples.put(temp, v);
            }
        }
        SparseMatrix s = new SparseMatrix(triples,M.dimension,N.dimension);
        return s;
    }



    /*
     * 稀疏向量相加函数
     */
    public static SparseVector Add(SparseVector M,SparseVector N){

        if(M.dimension != N.dimension){
            System.out.println("向量相加不满足条件");
            return null;
        }

        //SparseMatrix s = new SparseMatrix();
        Map<Integer,Double> triples = new HashMap<>();
        Iterator<Map.Entry<Integer, Double>> it1 = M.Triples.entrySet().iterator();
        Iterator<Map.Entry<Integer, Double>> it2 = N.Triples.entrySet().iterator();

        while(it1.hasNext()){
            Map.Entry<Integer, Double> entry = it1.next();
            int position = entry.getKey();
            double value = entry.getValue();
            if(triples.containsKey(position)){
                triples.put(position, triples.get(position) + value);
            }else{
                triples.put(position, value);
            }


        }

        while(it2.hasNext()){
            Map.Entry<Integer, Double> entry = it2.next();
            int position = entry.getKey();
            double value = entry.getValue();
            if(triples.containsKey(position)){
                triples.put(position, triples.get(position) + value);
            }else{
                triples.put(position, value);
            }

        }
        SparseVector s = new SparseVector(triples,M.dimension);
        return s;
    }



    /*
    数值乘法
     */
    public SparseVector ScalarMultiply(SparseVector M, double number){

        //SparseMatrix s = new SparseMatrix();
        Map<Integer,Double> triples = new HashMap<>();
        Iterator<Map.Entry<Integer, Double>> it1 = M.Triples.entrySet().iterator();


        while(it1.hasNext()){
            Map.Entry<Integer, Double> entry = it1.next();
            int position = entry.getKey();
            double value = entry.getValue();
            value *= number;

            triples.put(position, value);

        }
        SparseVector s = new SparseVector(triples,M.dimension);
        return s;
    }

    /*
    生成单位矩阵


    public SparseMatrix IdentityMatrix(int dimension){

        Map<ArrayList<Integer>,Double> triples = new HashMap<ArrayList<Integer>,Double>();


        for (int i = 0; i < dimension; i++) {
            ArrayList<Integer> position = new ArrayList<Integer>();
            position.add( i );
            position.add( i );

            triples.put(position, 1.0);
        }

        SparseMatrix s = new SparseMatrix(triples,dimension,dimension);
        return s;
    }

*/

    public void setEntry(int wordPosition,  double v) {

        this.Triples.put(wordPosition, v);

    }

    /*
     if(M.dimension != N.dimension){
            System.out.println("向量相乘不满足条件");
            return null;
        }
     */
}
