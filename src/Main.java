import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    static private String[][] matrix;
    static private String query;
    static private List<String> terminalRules;
    static private List<String> baseRules;


    static public void main(String[] args){
        String filename = args[0];
        query = args[1];
        terminalRules = loadTerminalRules(filename);
        baseRules = loadRules(filename);
        int length = args[1].length();
        matrix = new String[length][length];

        for(int index=0;index<length;index++){
            for(int i=0;i<length;i++){
                matrix[index][i]="";
            }
        }

        int routes = cykCheck(0,length-1,"S");

        if(routes>0){
            matrix[0][length-1]="S";
            System.out.println(query+" can be generated.");
            System.out.println(query+" has "+routes+" parse trees.\n");

            beginSampleTree(0,length-1,"S");

        }
        else{
            System.out.println(query+" cannot be generated.");
        }
        if(args.length>2){
            for(int index=0;index<length;index++){
                System.out.print("["+matrix[index][0]);
                for(int i=1;i<length;i++){
                    System.out.print(","+matrix[index][i]);
                }
                System.out.print("]\n");
            }
        }


    }

    private static List<String> loadTerminalRules(String fileName){
        List<String> L = new ArrayList<>();
        String tP = "([A-Z])->([a-z])";
        try{
            File file = new File(fileName);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while((line = br.readLine()) != null){
                if(line.matches(tP)){
                    L.add(line);
                }
            }
            fr.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }

        return L;
    }

    private static List<String> loadRules(String fileName){
        List<String> m = new ArrayList<>();
        String tP = "([A-Z])->([A-Z][A-Z])";
        try{
            File file = new File(fileName);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while((line = br.readLine()) != null){
                if(line.matches(tP)){
                    m.add(line);
                }
            }
            fr.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return m;
    }

    private static int cykCheck(int row, int col, String X){
        if(row==col){
            if(terminalCheck(row,X)) return 1;
            return 0;
        }
        else{
            List <Integer> routes= new ArrayList<>();
            String pattern = X+"->([A-Z])([A-Z])";
            for(int i=row;i<col;i++){
                for(String x: baseRules){
                    if(x.matches(pattern)){
                        String L=x.replaceAll(pattern,"$1");
                        String R=x.replaceAll(pattern,"$2");
                        int count=cykCheck(row,i,L)*cykCheck(i+1,col,R);
                        routes.add(count);
                        if(count>=1){
                            if(!matrix[row][i].contains(L)) matrix[row][i]+=L;
                            if(!matrix[i+1][col].contains(R)) matrix[i+1][col]+=R;
                        }
                    }
                }
            }
            int total=0;
            for(Integer y: routes){
                total+=y;
            }
            return total;
        }
    }

    private static boolean terminalCheck(int row,String X){
        String w = query.substring(row,row+1);
        return terminalRules.contains(X+"->"+w);
    }

    private static void terminalPrint(int row,String X){
        String w = query.substring(row,row+1);
        if(terminalRules.contains(X+"->"+w)) System.out.println(X+"->"+w);
    }

    private static void beginSampleTree(int row, int col, String X){
        if(row==col) terminalPrint(row,X);
        else{
            boolean trip=false;
            String pattern = X+"->([A-Z])([A-Z])";
            for(int i=row;i<col;i++){
                for(String x: baseRules){
                    if(x.matches(pattern)&&!trip){
                        String L=x.replaceAll(pattern,"$1");
                        String R=x.replaceAll(pattern,"$2");
                        int count=cykCheck(row,i,L)*cykCheck(i+1,col,R);
                        if(count>0){
                            System.out.println(X+"->"+L+R);
                            beginSampleTree(row,i,L);
                            beginSampleTree(i+1,col,R);
                            trip=true;
                        }

                    }
                }
            }
        }
    }
}

