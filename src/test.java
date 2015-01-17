import Jama.Matrix;

public class test {
	int count = 0;
	public void norm(){
		double[][] array = {{1,1,1,1},{1,1,0,1},{1,0,1,0},{1,1,0,1}};
	
		Matrix A = new Matrix(array);
		/*Matrix b = Matrix.random(3,1);
		Matrix x = A.solve(b);
		Matrix Residual = A.times(x).minus(b);
		double rnorm = Residual.normInf();*/
		
		while(!(convergence(array, 0.97, 0.02) && count!=0)){
			array = NormalizedMatrix(array);
			array = ExpansionMatrix(array,array.length);
			array = InflationMatrix(array,array.length);
			array = pruneMatrix(array,0.02);
			printMatrix(array);
			count++;
		}
		System.out.println(count);
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
				expansionMatrix[i][j]=(double)Math.round(Val*100000)/100000;
			}
		}
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
				inflationMatrix[i][j]=(double)Math.round(sqVal*100000)/100000;
			}
		}
		return inflationMatrix;
	}
	
	//Print a 2D matrix
	void printMatrix(double[][] array){
		for(int i=0;i<array.length;i++){
			for(int j=0;j<array[i].length;j++){
				System.out.print(array[i][j] + " ");
			}
			System.out.println();
		}
	}
	
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
            	randMatrix[i][j] = randMatrix[i][j]/sums[j];
            	randMatrix[i][j]=(double)Math.round(randMatrix[i][j]*100000)/100000;
            }
		}
		return randMatrix;
	}
	
	public boolean convergence(double[][] inputArr, double max, double min){
		for(int i =0;i<inputArr.length;i++){
			for(int j =0; j<inputArr[i].length;j++){
				if(inputArr[i][j] > min && inputArr[i][j] < max)
					return false;
			}
		}
		return true;
	}
	public double[][] pruneMatrix(double[][] inputArr, double d){
		for(int i =0;i<inputArr.length;i++){
			for(int j =0; j<inputArr[i].length;j++){
				if(inputArr[i][j] < d)
					inputArr[i][j] = 0;
			}
		}
		return inputArr;
	}
}
