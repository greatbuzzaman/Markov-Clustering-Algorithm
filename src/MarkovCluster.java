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
	public int dimensions;
	int clusters;
	public String fileName;
	public double initialMatrix[][];
	public double MMatrix[][];
	public double sLMatrix[][];
	public ArrayList<Integer> tempNodes = new ArrayList<Integer>();
	
	public static void main(String[] args) throws IOException {
		MarkovCluster m= new MarkovCluster();
		m.getInfofrmUser();
		m.InitialMatrix();
		//m.NormalizedMatrix();

	}
	
	public void getInfofrmUser(){
		Scanner userInput = new Scanner(System.in);
		System.out.println("*********** IMPLEMENTATION OF Markov(MCL) ALGORITHM **********");
		//fileName="D:/UB 2012-13/Java files/MCLalgo/src/data/yeast_undirected_metabolic.txt";
		fileName = System.getProperty("user.dir") + "/src/Data/attweb_net.txt";
        System.out.println("Enter the file path"); // to read file path
		//InputStreamReader input = new InputStreamReader(System.in);
		//BufferedReader reader = new BufferedReader(input);
        this.fileName= fileName;
		readfile();
	}
	
	
	
	public void readfile(){
		try {
			File file = new File(this.fileName);
			FileInputStream fileInputStream = new FileInputStream(file);
			BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);			
			DataInputStream dataInputStream = new DataInputStream(bufferedInputStream);
			while (dataInputStream.available() != 0){
				String items[];
        		//if(fileName.equals("yeast_undirected_metabolic.txt"))                        
        		items = dataInputStream.readLine().split("\\s+");
        		/*else
        			items = dataInputStream.readLine().split(" ");*/
        		if(tempNodes.isEmpty()){
        			tempNodes.add(Integer.parseInt(items[0]));
            		tempNodes.add(Integer.parseInt(items[1]));        			
        		}
        		else{
        			for(int i=0;i<tempNodes.size();i++)
	        		{
	        		     if( !tempNodes.contains(Integer.parseInt(items[0]))) 
	        		     {
	        		    	 tempNodes.add(Integer.parseInt(items[0]));
	        		    
	        		     }
	        		     if( !tempNodes.contains(Integer.parseInt(items[1]))) 
	        		     {
	        		    	 tempNodes.add(Integer.parseInt(items[1]));
	        		     }
	        		}
        		}	        		
			}
			fileInputStream.close();
			bufferedInputStream.close();
			dataInputStream.close();
		}catch (Exception er) {
			er.printStackTrace();
		}
		dimensions =tempNodes.size();
		System.out.println("dim"+dimensions);
	}
	
	@SuppressWarnings("deprecation")
	public void readfile1(){
		try{
			File file = new File(this.fileName);
			FileInputStream fileInputStream = new FileInputStream(file);
			BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);			
			DataInputStream dataInputStream = new DataInputStream(bufferedInputStream);
			int i1 = 0,i2=0;
			initialMatrix= new double [dimensions][dimensions];
			while (dataInputStream.available() != 0){
				String items[];
	    		//if(fileName.equals("yeast_undirected_metabolic.txt"))                        
	    		items = dataInputStream.readLine().split("\\s+");
	    		/*else
	    			items = dataInputStream.readLine().split(" ");*/
	    	
	 			for(int i=0;i<tempNodes.size();i++)
	        		{
	        		     if( tempNodes.get(i).equals(Integer.parseInt(items[0]))) 
	        		     {
	        		    	 i1=i;	        		    
	        		     }
	        		     else if(tempNodes.get(i).equals(Integer.parseInt(items[1]))) 
	        		     {
	        		    	  i2=i;
	        		     }
	        		}
	 			initialMatrix[i1][i2]=1;
	  		    initialMatrix[i2][i1]=1;
	    		}
			fileInputStream.close();
			bufferedInputStream.close();
			dataInputStream.close();
			} catch(Exception er) {
			er.printStackTrace();
		}
}
	public void InitialMatrix(){
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
        NormalizedMatrix(sLMatrix);
	}
	public void NormalizedMatrix(double[][] randMatrix ){
		double count;
		MMatrix= new double[dimensions][dimensions];
		for(int i=0;i<dimensions;i++){   
			count=0;
            for(int j=0;j<dimensions;j++)  {
            	count+=randMatrix[i][j];
            }  
            System.out.println(count);
            for(int k=0;k<dimensions;k++)  {
            	if(randMatrix[i][k]>0){
            		MMatrix[i][k]=1/count;
            		MMatrix[i][k]=(double)Math.round(MMatrix[i][k]*100)/100;
            	}
            }
		}
		ExpansionMatrix(MMatrix);
	}
	
	public  double[][] ExpansionMatrix(double[][] currentMatrix){
		double Val=0.0;
        double[][] expansionMatrix=new double[dimensions][dimensions];
        for(int i=0;i<dimensions;i++)
        {
                for(int j=0;j<dimensions;j++)
                {
                        double sum=0.0;
                        for(int k=0;k<dimensions;k++)
                        {
                                sum+= currentMatrix[i][k]*currentMatrix[k][j];
                        }
                        Val=(double)sum;
                        expansionMatrix[i][j]=(double)Math.round(Val*100)/100;
                }
        }
        InflationMatrix(expansionMatrix);
		return expansionMatrix;
		
	}
	
	public double[][] InflationMatrix(double[][] currentMatrix){
		double sqVal=0.0;
        double[][] inflationMatrix=new double[dimensions][dimensions];
        for(int i=0;i<dimensions;i++)
        {
        	for(int j=0;j<dimensions;j++)
        	{
        		double sqsum=0.0;
        		sqsum+= currentMatrix[i][j]*currentMatrix[i][j];
        		sqVal=(double)sqsum;
        		inflationMatrix[i][j]=(double)Math.round(sqVal*100)/100;
        	}
        }
		return inflationMatrix;
	}
}

