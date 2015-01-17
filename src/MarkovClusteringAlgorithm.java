import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.Vector;


/*Akshay Data Mining HW3 on Markov Clustering*/

public class MarkovClusteringAlgorithm {

	int e = 2; // Power parameter
	int r = 2; // Inflation parameter

	public String filePath;

	double tolerance = (double) 1.0E-200;

	ArrayList<Integer> attNodes = new ArrayList<Integer>();//stores att nodes
	ArrayList<String> physicsNodes = new ArrayList<String>();//stores physics nodes
	ArrayList<Integer> yeastNodes = new ArrayList<Integer>();//stores yeast nodes

	double[][] attNodeMatrix;
	double[][] physicsNodeMatrix;
	double[][] yeastNodeMatrix;

	String attFilePath; 
	String physicsFilePath;
	String yeastFilePath;
	String cluPath;
	String attCLUFilePath;
	String physicsCLUFilePath;
	String yeastCLUFilePath;

	ArrayList<Vector> clusterList; 

	public static void main(String args[]){
		MarkovClusteringAlgorithm mc = new MarkovClusteringAlgorithm();
		mc.attFilePath = mc.getInfofrmUser("ATT Web");
		mc.physicsFilePath = mc.getInfofrmUser("Physics Collboration");
		mc.yeastFilePath = mc.getInfofrmUser("Yeast undirected");
		mc.cluPath = mc.getInfofrmUser(" Folder for CLU Files " );
		mc.attCLUFilePath = mc.getInfofrmUser("ATT CLU file");
		mc.physicsCLUFilePath = mc.getInfofrmUser("Physics CLU file");
		mc.yeastCLUFilePath = mc.getInfofrmUser("Yeast CLU file");
		
		mc.operations();
	}

	public void operations(){

		//Read all the files and create the mapping arraylists
		readFiles(attFilePath);
		readFiles(physicsFilePath);
		readFiles(yeastFilePath);

		//Initialize all the matrices by reading the file and the corresponding mapping matrix.
		attNodeMatrix = createMatrix(attNodes, attFilePath);
		physicsNodeMatrix = createMatrix(physicsNodes, physicsFilePath);
		yeastNodeMatrix = createMatrix(yeastNodes, yeastFilePath);

		//Initialize the counts
		int attCount=0;
		int physicsCount = 0;
		int yeastCount = 0;
		double[][] temp;
		
		temp = new double[attNodeMatrix.length][attNodeMatrix.length];
		attNodeMatrix = addSelfLoops(attNodeMatrix);
		while(!withinTolerance(temp, attNodeMatrix,0.99910)){
		//while(attCount<15){
			temp = attNodeMatrix;
			attNodeMatrix = NormalizedMatrix(attNodeMatrix);
			attNodeMatrix = ExpansionMatrix(attNodeMatrix);
			attNodeMatrix = InflationMatrix(attNodeMatrix,1.34);
			attNodeMatrix = pruneMatrix(attNodeMatrix);
			attCount++;
		}
		findClusters(attNodeMatrix);
		System.out.println("attCount" + attCount + " " + clusterList.size());
		writeCLUFile(attCLUFilePath, clusterList);

		temp = new double[physicsNodeMatrix.length][physicsNodeMatrix.length];
		physicsNodeMatrix = addSelfLoops(physicsNodeMatrix);
		while(!withinTolerance(temp, physicsNodeMatrix, 0.99930)){
			temp = physicsNodeMatrix;
			physicsNodeMatrix = NormalizedMatrix(physicsNodeMatrix);	
			physicsNodeMatrix = ExpansionMatrix(physicsNodeMatrix);
			physicsNodeMatrix = InflationMatrix(physicsNodeMatrix,1.34);
			physicsNodeMatrix = pruneMatrix(physicsNodeMatrix);
			physicsCount++;
		}
		findClusters(physicsNodeMatrix);
		System.out.println("physicsCount" + physicsCount + " " + clusterList.size());
		writeCLUFile(physicsCLUFilePath, clusterList);

		temp = new double[yeastNodeMatrix.length][yeastNodeMatrix.length];
		yeastNodeMatrix = addSelfLoops(yeastNodeMatrix);
		while(!withinTolerance(temp, yeastNodeMatrix,0.9971)){
			yeastNodeMatrix = NormalizedMatrix(yeastNodeMatrix);
			yeastNodeMatrix = ExpansionMatrix(yeastNodeMatrix);
			yeastNodeMatrix = InflationMatrix(yeastNodeMatrix,1.34);
			yeastNodeMatrix = pruneMatrix(yeastNodeMatrix);
			yeastCount++;
		}
		findClusters(yeastNodeMatrix);
		System.out.println("yeastCount" + yeastCount + " " + clusterList.size());
		writeCLUFile(yeastCLUFilePath, clusterList);

		System.out.println("Done");
	}

	public void writeFile(String filePath, double[][] inputArr){
		try {
			int dimensions = inputArr.length;
			File file = new File(filePath);
			if(!file.exists())
				file.createNewFile();
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			for(int i=0;i<dimensions;i++){
				for(int j=0;j<dimensions;j++){
					bw.write(inputArr[i][j] + "\t");
				}
				bw.newLine();
			}
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeCLUFile(String filePath, ArrayList<Vector> inputArr){
		try {

			int dimensions = 0;
			if(filePath.contains("attweb")){
				dimensions = attNodes.size();
			}
			if(filePath.contains("physics")){
				dimensions = physicsNodes.size();
			}
			if(filePath.contains("yeast")){
				dimensions = yeastNodes.size();
			}

			File file = new File(filePath);
			if(!file.exists())
				file.createNewFile();
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			int count = 0; //counter for node / line you're writing data for.
			bw.write("*Vertices " + new Integer(dimensions).toString());
			while(count<dimensions){
				for(int i=0;i<inputArr.size();i++){
					Vector vect = inputArr.get(i);
					for(int j=0;j<vect.size();j++){
						if((int)vect.get(j) == count){
							bw.newLine();
							bw.write(new Integer(i).toString());
							count++;
						}
					}
				}
			}
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//Take input from the user. 
	public String getInfofrmUser(String str){
		Scanner userInput = new Scanner(System.in);
		System.out.println("*********** IMPLEMENTATION OF Markov(MCL) ALGORITHM **********");
		System.out.println("Enter the file path for " + str ); // to read file path
		Scanner scanner = new Scanner(System.in);
		String filePath = scanner.nextLine();
		return filePath;
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

	public boolean searchVectorElement(ArrayList<Vector> array, Vector vect){
		boolean flag = false;
		if(array!=null && array.size() > 0){
			for(int i=0;i<array.size();i++){
				Vector vect1 = array.get(i);
				if(vect1.size() == vect.size()){
					flag = true;
					Collections.sort(vect);
					Collections.sort(vect1);
					for(int j=0;j<vect1.size();j++){
						if(vect1.get(j)!=vect.get(j)){
							flag = false;
							break;
						}
					}
				}
			}
		}else{
			flag = false;
		}
		return flag;
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
	public double[][] addSelfLoops(double[][] mat){
		for(int i=0;i<mat.length;i++){
			mat[i][i] = 1;
		}
		return mat;
	}

	//Multiplying the matrix with itself - Expansion
	public  double[][] ExpansionMatrix(double[][] inputMatrix){
		int dimensions = inputMatrix.length;

		double Val=0.0;
		double[][] expansionMatrix=new double[dimensions][dimensions];
		for(int i=0;i<dimensions;i++)
		{
			for(int j=0;j<dimensions;j++)
			{
				double sum=0.0;
				for(int k=0;k<dimensions;k++)
				{
					sum+= inputMatrix[i][k]*inputMatrix[k][j];
				}
				expansionMatrix[i][j] = sum;
			}
		}
		return expansionMatrix;
	}

	//Squaring each element - Inflating
	public double[][] InflationMatrix(double[][] inputMatrix, double gamma){
		int dimensions = inputMatrix.length;
		double sqVal=0.0;
		double[][] inflationMatrix=new double[dimensions][dimensions];
		for(int i=0;i<dimensions;i++){
			for(int j=0;j<dimensions;j++){
				double sqsum=0.0;
				sqsum+= Math.pow(inputMatrix[i][j], gamma);
				inflationMatrix[i][j] = sqsum;
			}
		}
		return inflationMatrix;
	}

	//Print a 2D matrix
	void printMatrix(double[][] array){
		for(int i=0;i<array.length;i++){
			for(int j=0;j<array[i].length;j++){
				System.out.print(array[i][j] + "\t");
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
		}

		for(int i=0;i<randMatrix.length;i++){   
			for(int j=0;j<randMatrix[i].length;j++)  {
				randMatrix[i][j] = randMatrix[i][j]/sums[j];
			}
		}
		return randMatrix;
	}

	//Replace values less than min as 0 and those greater than max as 1 - Pruning
	public double[][] pruneMatrix(double[][] inputArr){
		for(int i =0;i<inputArr.length;i++){
			for(int j =0; j<inputArr[i].length;j++){
				if(inputArr[i][j] < tolerance)
					inputArr[i][j] = 0;
			}
		}
		return inputArr;
	}

	public void findClusters(double[][] inputArr){
		clusterList = new ArrayList<>();
		int  count = 0;
		for(int i =0;i<inputArr.length;i++){
			if(inputArr[i][i]>0){
				count++;
				Vector vect = new Vector();
				for(int j =0; j<inputArr[i].length;j++){
					if(inputArr[i][j] > 0)
						vect.add(j); // add all the nodes in the row to the cluster.
				}
				if(!searchVectorElement(clusterList, vect))
					clusterList.add(vect); //add it to the list only if not found above.
			}
		}
		System.out.println("** " + count);
	}

	public boolean withinTolerance(double[][] oldMat, double[][] newMat, double ratio){
		int dimensions = oldMat.length;
		int equalElements=0;
		equalElements=0;
		boolean flag = false;
		int count = 0;
		for(int i=0;i<dimensions;i++){
			for(int j=0;j<dimensions;j++){
				count++;//count total elements in the matrix for finding the ratio later on
				if(oldMat[i][j] == newMat[i][j]){
					equalElements++;
				}
			}
		}   
		double calcRatio = (double)equalElements/count;
		
		if(calcRatio > ratio){
			flag = true;
		}
		return flag;
	}
}