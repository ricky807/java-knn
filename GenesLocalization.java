import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;

public class GenesLocalization {

	public static void main(String[] args) throws FileNotFoundException
	{
		File file;
		Scanner fileScanner;
		
		// read in training set
		file = new File("mainData.csv");
		fileScanner = new Scanner(file);
		String[][] trainingSet = new String[4347][];
		fileToArray(fileScanner, trainingSet);
		fileScanner.close();
		
		//read in data file to predict
		file = new File("predicting.csv");
		fileScanner = new Scanner(file);
		String[][] predictingSet = new String[1930][];
		fileToArray(fileScanner, predictingSet);
		fileScanner.close();
		
		//reads in the keys and puts them in a HashMap
		file = new File("keys.txt");
		fileScanner = new Scanner(file);
		HashMap<String, String> map = fileToHashmap(fileScanner);
		fileScanner.close();
		
		//here we predict using KNN for each row
		for(int x = 1; x < predictingSet.length; x++)
		{
			predictingSet[x][8] = getKNN(predictingSet[x], trainingSet);
		}
		
		output(predictingSet); //print out results onto file
		System.out.println("Results printed to results.txt");
		
		Double ac = accuracy(predictingSet, map); //This brings back the accuracy
		System.out.printf("Accuracy: %.3f%%\n", ac * 100 );
		
	}
	
	public static void fileToArray(Scanner fileScanner, String[][] array)
	{
		int i = 0;
		
		while(fileScanner.hasNext())
		{
			array[i] = fileScanner.nextLine().split(",");
			i++;
		}
	}
	public static HashMap<String, String> fileToHashmap(Scanner fileScanner)
	{	
		HashMap<String, String> map = new HashMap<>();
		while(fileScanner.hasNext())
		{
			String array[] = fileScanner.nextLine().split(",");
			map.put(array[0], array[1]);
		}
		return map;
	}

	public static String getKNN(String[] predictRow, String[][] trainingSet)
	{
		int highestWeight = 0;
		LinkedList<String> NN = new LinkedList<String>();
		for(int x = 1; x < trainingSet.length; x++)
		{
			int currentWeight = 0;
			
			for (int j = 0; j < 7; j++)
            {
                if(predictRow[j].equals(trainingSet[x][j]))
                {
                	currentWeight++;
                }       
            }
			//Clears list because it found a higher weight
			if(currentWeight > highestWeight)
			{
				NN.clear();
				highestWeight = currentWeight;
				NN.add(trainingSet[x][8]);
			}
			//if same weight we just add it to the linked list
			else if(currentWeight == highestWeight)
			{
				NN.add(trainingSet[x][8]);
			}
		}
		return pickNN(NN);
	}
	//Here we pick the one with the most occurrences from the list
	public static String pickNN(LinkedList<String> nn)
	{
		
		HashMap<String, Integer> count = new HashMap<>();
		for(int x = 0; x < nn.size(); x++)
		{
			if(!count.containsKey(nn.get(x)))
			{
				count.put(nn.get(x), 1);
			}
			else
			{
				count.put(nn.get(x), count.get(nn.get(x)) + 1);
			}
		}
		int max = 0;
		String a = "?";
		for (Map.Entry<String, Integer> e : count.entrySet())//go through each element in HashMap
		{
		    if(e.getValue() > max)
		    {
		    	max = e.getValue();
		    	a = e.getKey();
		    }
		}
		return a;
		
		//return nn.getFirst();
	}
	public static double accuracy(String predictive[][], HashMap<String, String> map)
    {
		int correct = 0;
        for (int i = 1; i < predictive.length; i++)
        {
        	String key = predictive[i][0].substring(1, predictive[i][0].length()-1);
        	String prediction = predictive[i][8].substring(1, predictive[i][8].length()-1);
        	String valueInMap = map.get(key);
        	
            if(prediction.equalsIgnoreCase(valueInMap))
            {
            	correct++;
            }
        }
        Double a = correct/ (double)(predictive.length - 1);
        //System.out.println("Correct: "+correct + "/" + (predictive.length - 1));
        return a;
    }
	
	// print out the final result onto a file
	public static void output(String [][] predictionTable) throws FileNotFoundException
	{
		//PrintStreem to print to file results.txt
		PrintStream fileprinter = new PrintStream(new FileOutputStream("results.txt"), true);
		for (int i = 0; i < predictionTable.length; i++)
        {
			//gets geneID and gets rid of ""
        	String key = predictionTable[i][0].substring(1, predictionTable[i][0].length()-1);
        	//gets the prediction and gets rid of ""
        	String prediction = predictionTable[i][8].substring(1, predictionTable[i][8].length()-1);

        	fileprinter.println(key + ", " + prediction);
        }
		fileprinter.close();
	}
}
