import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

import Jama.Matrix;

/*Akshay Data Mining HW3 on Markov Clustering*/

public class MarkovClustering {

	int e = 2; // Power parameter
	int r = 2; // Inflation parameter
	public String filePath;
	ArrayList<Integer> attNodes = new ArrayList<Integer>();//stores att nodes
	ArrayList<String> physicsNodes = new ArrayList<String>();//stores physics nodes
	ArrayList<Integer> yeastNodes = new ArrayList<Integer>();//stores yeast nodes

	Matrix attNodeMatrix;
	Matrix physicsNodeMatrix;
	Matrix yeastNodeMatrix;

	public static void main(String args[]){
		MarkovClustering mc = new MarkovClustering();
		String attFilePath = System.getProperty("user.dir") + "/src/Data/attweb_net.txt";
		String physicsFilePath = System.getProperty("user.dir") + "/src/Data/physics_collaboration_net.txt";
		String yeastFilePath = System.getProperty("user.dir") + "/src/Data/yeast_undirected_metabolic.txt";

		test tes = new test();
		tes.norm();

		mc.readFiles(attFilePath);
		mc.readFiles(physicsFilePath);
		mc.readFiles(yeastFilePath);

		mc.printArrayList(mc.physicsNodes);

		mc.attNodeMatrix = new Matrix(mc.createMatrix(mc.attNodes, attFilePath));
		mc.physicsNodeMatrix= new Matrix(mc.createMatrix(mc.physicsNodes, physicsFilePath));
		mc.yeastNodeMatrix = new Matrix(mc.createMatrix(mc.yeastNodes, yeastFilePath));

		//mc.printMatrix(mc.attNodeMatrix.getArray());

		System.out.println(mc.attNodeMatrix.trace());
		System.out.println(mc.physicsNodeMatrix.trace());
		System.out.println(mc.yeastNodeMatrix.trace());

		mc.addSelfLoops(mc.attNodeMatrix);
		mc.addSelfLoops(mc.physicsNodeMatrix);
		mc.addSelfLoops(mc.yeastNodeMatrix);

		/*System.out.println(mc.attNodeMatrix.trace());
		System.out.println(mc.physicsNodeMatrix.trace());
		System.out.println(mc.yeastNodeMatrix.trace());*/
		
	}
	//Take input from the user. 
	public void getInfofrmUser(){
		Scanner userInput = new Scanner(System.in);
		System.out.println("*********** IMPLEMENTATION OF Markov(MCL) ALGORITHM **********");
		//fileName="D:/UB 2012-13/Java files/MCLalgo/src/data/yeast_undirected_metabolic.txt";
		filePath = System.getProperty("user.dir") + "/src/Data/attweb_net.txt";
        System.out.println("Enter the file path"); // to read file path
		InputStreamReader input = new InputStreamReader(System.in);
		BufferedReader reader = new BufferedReader(input);
	}
	public void readFiles(String filePath){

		FileReader fr;
		try {
			String line;
			fr = new FileReader(filePath);
			BufferedReader br = new BufferedReader(fr);

			while((line = br.readLine())!=null){
				String[] elements = line.split("\\s+"); // Extracting numbers in each line. Nodes per edge in our case.
				for(int i=0;i<elements.length;i++){
					if(filePath.contains("attweb")){
						if(searchIntElement(attNodes, Integer.parseInt(elements[i]))==99999){
							attNodes.add(Integer.parseInt(elements[i]));// add unique nodes to the array list.
						}
					}

					if(filePath.contains("physics")){
						if(searchStringElement(physicsNodes, elements[i])==99999){
							physicsNodes.add(elements[i]);// add unique nodes to the array list.
						}
					}

					if(filePath.contains("yeast")){
						if(searchIntElement(yeastNodes, Integer.parseInt(elements[i]))==99999){
							yeastNodes.add(Integer.parseInt(elements[i]));// add unique nodes to the array list.
						}
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*Returns the index of the element you were looking for. 
	Returns 99999 otherwise considering the arralist has less than that many elements.*/
	public int searchIntElement(ArrayList<Integer> array, int num){
		int retElement = 99999; 
		for(int i=0;i<array.size();i++){
			if(array.get(i) == num){
				retElement = i;
			}
		}
		return retElement;
	}
	public int searchStringElement(ArrayList<String> array, String str){
		int retElement = 99999; 
		for(int i=0;i<array.size();i++){
			if(array.get(i).equals(str)){
				retElement = i;
			}
		}
		return retElement;
	}

	//Print the contents of the array list. 
	public void printArrayList(ArrayList arrList){
		System.out.println(arrList.size());
		for(int i=0;i<arrList.size();i++){
			System.out.println("Index i = " + i + " -- " + arrList.get(i));
		}
	}

	//read nodes from the file and 
	public double[][] createMatrix(ArrayList array, String filePath){
		double[][] matrix = new double[array.size()][array.size()];
		FileReader fr;
		try {
			String line;
			fr = new FileReader(filePath);
			BufferedReader br = new BufferedReader(fr);

			while((line = br.readLine())!=null){
				String[] elements = line.split("\\s+"); // Extracting numbers in each line. Nodes per edge in our case.

				if(filePath.contains("attweb")){
					int m = searchIntElement(attNodes, Integer.parseInt(elements[0])); 
					int n = searchIntElement(attNodes, Integer.parseInt(elements[1]));
					if(m!=99999 && n!=99999){
						matrix[m][n] = 1;//There's an edge so set that index intersection element as 1
						matrix[n][m] = 1;//There's an edge so set that index intersection element as 1
					}
				}

				if(filePath.contains("physics")){
					int m = searchStringElement(physicsNodes, elements[0]); 
					int n = searchStringElement(physicsNodes, elements[1]);
					if(m!=99999 && n!=99999){
						matrix[m][n] = 1;//There's an edge so set that index intersection element as 1
						matrix[n][m] = 1;//There's an edge so set that index intersection element as 1
					}
				}

				if(filePath.contains("yeast")){
					int m = searchIntElement(yeastNodes, Integer.parseInt(elements[0])); 
					int n = searchIntElement(yeastNodes, Integer.parseInt(elements[1]));
					if(m!=99999 && n!=99999){
						matrix[m][n] = 1;//There's an edge so set that index intersection element as 1
						matrix[n][m] = 1;//There's an edge so set that index intersection element as 1
					}
				}
			}
		}catch(FileNotFoundException fne){
			System.out.println("File not found");
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return matrix;
	}

	//Make the diagonal elements = 1
	public void addSelfLoops(Matrix matrix){
		double[][] mat = matrix.getArray();
		for(int i=0;i<mat.length;i++){
			mat[i][i] = 1;
		}
		matrix.times(matrix);
	}

	//Multiplying the matrix with itself - Expansion
	public  double[][] ExpansionMatrix(double[][] currentMatrix, int dimensions){
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
		InflationMatrix(expansionMatrix, dimensions);
		return expansionMatrix;
	}

	//Squaring each element - Inflating
	public double[][] InflationMatrix(double[][] currentMatrix, int dimensions){
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

	//Print a 2D matrix
	void printMatrix(double[][] array){
		for(int i=0;i<array.length;i++){
			for(int j=0;j<array[i].length;j++){
				System.out.print((int)array[i][j] + "\t");
			}
			System.out.println();
		}
	}

	//Dividing each element by the sum of the elements in it's column - Normalizing
	public double[][] NormalizedMatrix(double[][] randMatrix){
		double[] sums = new double[randMatrix.length];

		for(int i =0;i<randMatrix.length;i++){
			for(int j =0; j<randMatrix[i].length;j++){
				sums[i] = sums[i] + randMatrix[j][i]; // Sum of values in a column
			}
			sums[i]=(double)Math.round(sums[i]*100)/100;
			//System.out.println(sums[i]);
		}

		for(int i=0;i<randMatrix.length;i++){   
			for(int j=0;j<randMatrix[i].length;j++)  {
				randMatrix[j][i] = randMatrix[j][i]/sums[j];
				randMatrix[j][i]=(double)Math.round(randMatrix[j][i]*100)/100;
			}
		}
		return randMatrix;
	}
	
	//Replace values less than min as 0 and those greater than max as 1 - Pruning
	public void pruneMatrix(double[][] inputArr, int min, int max){
		for(int i =0;i<inputArr.length;i++){
			for(int j =0; j<inputArr[i].length;j++){
				if(inputArr[i][j] < min)
					inputArr[i][j] = 0;
				if(inputArr[i][j] > max)
					inputArr[i][j] = 1;
			}
		}
	}

	//Verify if onvergence is achieved by checking if there is any value in the matrix that lies between min and max.
	public boolean convergence(double[][] inputArr, int min, int max){
		boolean flag = true;
		for(int i =0;i<inputArr.length;i++){
			for(int j =0; j<inputArr[i].length;j++){
				if(inputArr[i][j] > min || inputArr[i][j] < max)
					flag = false;
			}
		}
		return flag;
	}
}
