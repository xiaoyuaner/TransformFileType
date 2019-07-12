package com;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @ authour Gongsheng Yuan
 */
public class SparseMatrix {

    public Map<ArrayList<Integer>, Double> getTriples() {
        return Triples;
    }

    public Map<ArrayList<Integer>,Double> Triples;         //矩阵的三元组表示
    public int rowNum;                                      //矩阵行数
    public int colNum;                                      //矩阵列数


    public int getRowNum() {
        return rowNum;
    }


    public void setRowNum(int rowNum) {
        this.rowNum = rowNum;
    }


    public int getColNum() {
        return colNum;
    }


    public void setColNum(int colNum) {
        this.colNum = colNum;
    }


    /*
     * 构造函数1
     */
    public SparseMatrix(){

    }


    /*
     * 构造函数2
     */
    public SparseMatrix(Map<ArrayList<Integer>, Double> triples, int rowNum, int colNum) {

        Triples = triples;
        this.rowNum = rowNum;
        this.colNum = colNum;
    }

    /*
     * 构造函数3
     */
    public SparseMatrix(Map<ArrayList<Integer>, Double> triples) {

        Triples = triples;
    }


    /*
     * 构造函数4
     */
    public SparseMatrix(int rowNum, int colNum){
        this.Triples = new HashMap<ArrayList<Integer>,Double>();
        this.rowNum = rowNum;
        this.colNum = colNum;
    }



    /*
     * 稀疏矩阵相乘函数
     */
    public SparseMatrix Multiply(SparseMatrix M,SparseMatrix N){

        if(M.colNum != N.rowNum){
            System.out.println("矩阵相乘不满足条件");
            return null;
        }

        Map<ArrayList<Integer>,Double> triples = new HashMap<ArrayList<Integer>,Double>();
        Iterator<Map.Entry<ArrayList<Integer>, Double>> it1 = M.Triples.entrySet().iterator();
        //System.out.println("Multipoly" + M.Triples);
        //System.out.println("Multipoly" + N.Triples);

        while(it1.hasNext()){

            Map.Entry<ArrayList<Integer>, Double> entry = it1.next();
            ArrayList<Integer> position = entry.getKey();
            double value = entry.getValue();
            int positionOne = position.get(1);
            //System.out.println("1:"+position.get(1));

            Iterator<Map.Entry<ArrayList<Integer>, Double>> it2 = N.Triples.entrySet().iterator();
            while(it2.hasNext()){
                Map.Entry<ArrayList<Integer>,Double> entry2 = it2.next();
                ArrayList<Integer> position2 = entry2.getKey();
                double value2 = entry2.getValue();
                int positionTwo = position2.get(0);
                //System.out.println("2:"+position2.get(0));
                //!!!!!!!!!!!!!!!!!!!!!!!position2.get(0) == position2.get(0) 不能写成这个形式，不识别
                if(positionOne == positionTwo){

                    //System.out.println("2:"+position2.get(0));


                    ArrayList<Integer> temp = new ArrayList<Integer>();
                    temp.add(position.get(0));
                    temp.add(position2.get(1));
                    double v = value * value2;
                    if(triples.containsKey(temp)){
                        triples.put(temp, triples.get(temp) + v);
                        //System.out.println(temp+ "\t"+(triples.get(temp) + v));
                    }
                    else{
                        triples.put(temp, v);
                        //System.out.println(temp + "\t" + v);
                    }
                }

            }
        }
        //System.out.println("Multipoly" + triples);
        SparseMatrix s = new SparseMatrix(triples,M.rowNum,N.colNum);
        return s;
    }



    /*
     * 稀疏矩阵相加函数
     */
    public static SparseMatrix Add(SparseMatrix M,SparseMatrix N){
        if(M.colNum != N.colNum || M.rowNum != N.rowNum){
            System.out.println("矩阵相加不满足条件");
            return null;
        }
        //SparseMatrix s = new SparseMatrix();
        Map<ArrayList<Integer>,Double> triples = new HashMap<ArrayList<Integer>,Double>();
        Iterator<Map.Entry<ArrayList<Integer>, Double>> it1 = M.Triples.entrySet().iterator();
        Iterator<Map.Entry<ArrayList<Integer>, Double>> it2 = N.Triples.entrySet().iterator();

        while(it1.hasNext()){
            Map.Entry<ArrayList<Integer>, Double> entry = it1.next();
            ArrayList<Integer> position = entry.getKey();
            double value = entry.getValue();
            if(triples.containsKey(position)){
                triples.put(position, triples.get(position) + value);
            }else{
                triples.put(position, value);
            }


        }

        while(it2.hasNext()){
            Map.Entry<ArrayList<Integer>,Double> entry = it2.next();
            ArrayList<Integer> position = entry.getKey();
            double value = entry.getValue();
            if(triples.containsKey(position)){
                triples.put(position, triples.get(position) + value);
            }else{
                triples.put(position, value);
            }

        }
        SparseMatrix s = new SparseMatrix(triples,M.rowNum,M.colNum);
        return s;
    }


    /*
     * 稀疏矩阵求转置矩阵函数
     */
    public SparseMatrix Transposition(){

        Map<ArrayList<Integer>,Double> triples = new HashMap<ArrayList<Integer>,Double>();
        Iterator<Map.Entry<ArrayList<Integer>, Double>> it = this.Triples.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<ArrayList<Integer>, Double> entry = it.next();
            ArrayList<Integer> position = entry.getKey();
            double value = entry.getValue();
            ArrayList<Integer> transP = new ArrayList<Integer>();
            transP.add(position.get(1));
            transP.add(position.get(0));

            triples.put(transP, value);

        }
        SparseMatrix s = new SparseMatrix(triples,this.colNum,this.rowNum);
        return s;
    }

    /*
    数值乘法
     */
    public SparseMatrix ScalarMultiply(SparseMatrix M, double number){

        //SparseMatrix s = new SparseMatrix();
        Map<ArrayList<Integer>,Double> triples = new HashMap<ArrayList<Integer>,Double>();
        Iterator<Map.Entry<ArrayList<Integer>, Double>> it1 = M.Triples.entrySet().iterator();


        while(it1.hasNext()){
            Map.Entry<ArrayList<Integer>, Double> entry = it1.next();
            ArrayList<Integer> position = entry.getKey();
            double value = entry.getValue();
            value *= number;

            triples.put(position, value);

        }
        SparseMatrix s = new SparseMatrix(triples,M.rowNum,M.colNum);
        return s;
    }

    /*
    生成单位矩阵
     */

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

    public void setEntry(int wordPosition, int wordPosition1, double v) {

            ArrayList<Integer> position = new ArrayList<Integer>();
            position.add(wordPosition);
            position.add(wordPosition1);

            this.Triples.put(position, v);

    }

    public double getTrace() {

        Iterator<Map.Entry<ArrayList<Integer>, Double>> it1 = this.Triples.entrySet().iterator();

        double trace = 0;
        while(it1.hasNext()){
            Map.Entry<ArrayList<Integer>, Double> entry = it1.next();
            ArrayList<Integer> position = entry.getKey();
            int a = position.get(0);
            int b = position.get(1);
            //if ( position.get(0).intValue() == position.get(1).intValue() ){
            if ( a == b ){
                //System.out.println("Trace"+position.get(0));
                //System.out.println(position.get(1));
                double value = entry.getValue();
                trace += value;
            }

        }
        return trace;
    }



    /*
     * 加载文本数据为稀疏矩阵三元组形式的函数
     */
    /*
    public SMatrix Load(String file, String delimeter){


        Map<ArrayList<Integer>,Integer> triples = new HashMap<ArrayList<Integer>,Integer>();

        try{
            File f = new File(file);
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);

            String line;

            while((line = br.readLine()) != null){
                String[] str = line.trim().split(delimeter);

                ArrayList<Integer> s = new ArrayList<Integer>();
                for(int i = 0;i < str.length - 1; i++){
                    s.add(Integer.parseInt(str[i]));
                }

                triples.put(s, Integer.parseInt(str[str.length - 1]));

            }


            br.close();
            fr.close();

        }catch(IOException e){
            e.printStackTrace();
        }
        SMatrix sm = new SMatrix(triples);
        return sm;
    }
    */
    /*
     * 打印稀疏矩阵（三元组形式）
     */

    public void Print(){
        Map<ArrayList<Integer>, Double> triples = this.Triples;
        Iterator<Map.Entry<ArrayList<Integer>, Double>> it = triples.entrySet().iterator();
        int num = 0;
        while(it.hasNext()){
            Map.Entry<ArrayList<Integer>, Double> entry = it.next();
            ArrayList<Integer> position = entry.getKey();
            num++;
            System.out.print(num+":");
            for(Integer in:position){
                System.out.print(in + "\t");
            }

            System.out.println(entry.getValue());
        }

    }


}
