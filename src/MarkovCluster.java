import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream.GetField;

import java.util.*;


public class MarkovCluster {

	public HashMap<Integer,ArrayList<Integer>> IdtoNodes = new HashMap<Integer,ArrayList<Integer>>();
	public HashMap<Integer,Integer> NewNodes = new HashMap<Integer,Integer>();
	int dimensions;
	int clusters;
	public String fileName;
	public double initialMatrix[][];
	public double MMatrix[][];
	public double sLMatrix[][];
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		MarkovCluster m= new MarkovCluster();
		m.getInfofrmUser();
		m.InitialMatrix();
		m.NormalizedMatrix();
		
	}
	
	public void getInfofrmUser(){
		Scanner userInput = new Scanner(System.in);
		System.out.println("*********** IMPLEMENTATION OF Markov(MCL) ALGORITHM **********");
		//fileName="D:/UB 2012-13/Java files/MCLalgo/src/data/yeast_undirected_metabolic.txt";
        System.out.println("Enter the file path"); // to read file path
		InputStreamReader input = new InputStreamReader(System.in);
		BufferedReader reader = new BufferedReader(input);
		try {
			this.fileName= reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		readFile();
        
	}
	
	@SuppressWarnings("deprecation")
	public void readFile(){
		try {
			File file = new File(this.fileName);
			FileInputStream fileInputStream = new FileInputStream(file);
			BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);			
			DataInputStream dataInputStream = new DataInputStream(bufferedInputStream);
			ArrayList<Integer> temp= new ArrayList<Integer>();
			ArrayList<Integer> temp1= new ArrayList<Integer>();
			int newNodeId=0;
			int[] tempNodes;
			
			while (dataInputStream.available() != 0){
				String items[];
        		//if(fileName.equals("yeast_undirected_metabolic.txt"))                        
        		items = dataInputStream.readLine().split("\\s+");
        		/*else
        			items = dataInputStream.readLine().split(" ");*/
     
        		if(IdtoNodes.containsKey(Integer.parseInt(items[0]))){
        			temp =  IdtoNodes.get(Integer.parseInt(items[0]));
        			temp.add(Integer.parseInt(items[1]));
        			temp1.add(Integer.parseInt(items[0]));
        			//System.out.println(items[0]);
        			IdtoNodes.put(Integer.parseInt(items[0]), temp);
        			IdtoNodes.put(Integer.parseInt(items[1]), temp1);
        		}
        		else{
        			temp.add(Integer.parseInt(items[1]));
        			temp1.add(Integer.parseInt(items[0]));
        			IdtoNodes.put(Integer.parseInt(items[0]), temp);
        			IdtoNodes.put(Integer.parseInt(items[1]), temp1);
        			NewNodes.put(Integer.parseInt(items[0]), newNodeId);
        			newNodeId++;
        			//System.out.println(items[0]);	
        		}
        		//tempnodes=
        	}
			fileInputStream.close();
			bufferedInputStream.close();
			dataInputStream.close();
		}catch (Exception er) {
			er.printStackTrace();
		}
		dimensions = IdtoNodes.keySet().size();
		System.out.println("dim"+dimensions);
	}
	public void InitialMatrix(){
		initialMatrix= new double [dimensions][dimensions];
		for (int key:IdtoNodes.keySet()) {
			for (int node:IdtoNodes.get(key)) {
				initialMatrix[key][node]=1; 
				}			
		}
		sLMatrix=new double [dimensions][dimensions];
        for(int i=0;i<dimensions;i++){        
                for(int j=0;j<dimensions;j++)        
                        sLMatrix[i][j]=initialMatrix[i][j];
        }        
                
        for(int i=0;i<dimensions;i++)
        {
                for(int j=0;j<dimensions;j++)
                {
                        if(i==j)
                                sLMatrix[i][j]=1;
                }
                
        }
	}
	public void NormalizedMatrix(){
		double count=0;
		for(int i=0;i<dimensions;i++){        
            for(int j=0;j<dimensions;j++)  {
            	if(sLMatrix[i][j]==1)
            		count++;
            }       
            for(int k=0;k<dimensions;k++)  {
            	if(sLMatrix[i][k]==1){
            		MMatrix[i][k]=1/count;
            		MMatrix[i][k]=(double)Math.round(MMatrix[i][k]*1000)/1000;
            	}
            }
		}
	}
	
}
