package Wavelet;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Wavelet {

	static List<Float> compute1 = new ArrayList<Float>();
	static List<Float> lines = new ArrayList<Float>();
	static List<Float> finalArray = new ArrayList<Float>();
	static List<Float> prefixSum = new ArrayList<Float>();

	public static void main(String[] args) {

		String fileName = "C:/Users/Kiran/workspace007/DB/src/Wavelet/input.csv";
		File file = new File(fileName);

		Scanner inputStream;

		try {
			inputStream = new Scanner(file);

			while (inputStream.hasNext()) {
				String line = inputStream.next();
				String[] values = line.split(",");
				for (int i = 0; i < values.length; i++) {
					Float d = Float.parseFloat(values[i]);
					lines.add(Float.valueOf(values[i]));

				}
			}
			System.out.println("Original Data size is: " + lines.size());
			inputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		Wavelet w = new Wavelet();
		lines = w.checkInput(lines);
		prefixSum = w.fillPrefixSum(lines, lines.size());
		Scanner sc = new Scanner(System.in);
		int option = 1;
		w.Query(true);
		while (option == 1 || option == 2) {

			System.out.println(
					"Welcome to Wavelet Analysis! \n Enter 1 for Query (Aggregated Sum).. \n Enter 2 for update. \n Any other number to quit!");
			option = sc.nextInt();
			switch (option) {
			case 1:

				w.insertQuery();

				break;

			case 2:
				List<Float> newWavelet = new ArrayList<Float>();
				List<Float> newPrefix = new ArrayList<Float>();

				System.out.println("Update Query Begins..");
				System.out.print("Enter update Index: ");
				int updateIndex = sc.nextInt();
				System.out.print("Enter update Value: ");
				float indexValue = sc.nextFloat();
				float cellupdates = (updateIndex - 1) / 2;
				int length = lines.size();
				long startTime = System.nanoTime();
				lines.set(updateIndex - 1, indexValue);
				long stopTime = System.nanoTime();
				long elapsedTime = stopTime - startTime;
				System.out.println("Time Required for original data update : " + elapsedTime + " nano seconds!");
				startTime = System.nanoTime();
				prefixSum = new ArrayList<Float>(prefixSum.subList(0, updateIndex - 1));
				int lastindex = updateIndex - 2;
				newPrefix = PrefixUpdate(lines, prefixSum.get(lastindex), updateIndex - 1);
				prefixSum.addAll(newPrefix);
				w.updateQuery();
				System.out.println(
						"Number of wavelet cells updated = " + (length - (int) cellupdates) + " out of " + length);
				stopTime = System.nanoTime();
				elapsedTime = stopTime - startTime;
				System.out.println("Time Required for wavelet update: " + elapsedTime + " nano seconds!");
				break;

			default:
				System.exit(0);
			}

			try {
				FileWriter writer = new FileWriter("output.csv");
				writer.write("\n Original Data : \n");
				for (Float str : lines) {
					writer.write(str.toString()+",");
				}
				
				writer.write("\n Prefix Sum : \n");
				for (Float str : prefixSum) {
					writer.write(str.toString()+",");
				}
				
				writer.write("\n Calculated Wavelet : \n");
				for (Float str : compute1) {
					writer.write(str.toString()+",");
				}

				writer.write("\n Final  Wavelet transformation for Query (P2): \n");
				for (Float str : finalArray) {
					writer.write(str.toString()+",");
				}
				writer.close();
			} catch (IOException e) {
				
				e.printStackTrace();
			}

		}
	}

	// Only calculate the prefix sum of required elements in Update Query.
	private static List<Float> PrefixUpdate(List<Float> arr, Float float1, int index) {
		// TODO Auto-generated method stub
		List<Float> prefixSum = new ArrayList<Float>();
		Float a;
		a = arr.get(index);
		prefixSum.add(a + float1);
		int n = (arr.size() - 1) - (index);
		for (int j = 1; j <= n; j++)
			prefixSum.add(0.0f);
		// Adding present element with previous element
		for (int i = 1; i <= n; i++)
			prefixSum.set(i, prefixSum.get(i - 1) + arr.get(index + i));
		return prefixSum;

	}

	public List<Float> checkInput(List<Float> lines) {

		double dataSize = lines.size();

		double root = Math.log(dataSize) / Math.log(2);
		if ((root == Math.floor(root)) && !Double.isInfinite(root)) {

			return lines;

		} else {
			int expected = (int) root;
			expected++;
			double x;
			x = Math.pow(2, expected);
			double y = x - dataSize;
			for (int i = 0; i < y; i++) {
				lines.add(0f);
			}

			return lines;
		}

	}

	// Performs the Update operation on the Wavelet
	private void updateQuery() {
		// TODO Auto-generated method stub
		List<Float> compute2 = new ArrayList<Float>();
		List<Float> temp = new ArrayList<Float>();
		List<Float> transformArray = new ArrayList<Float>();

		Wavelet w = new Wavelet();

		compute1 = w.transform1(prefixSum);
		compute2 = w.transform2(prefixSum);
		temp = w.transform2(compute1);

		temp.addAll(compute2);
		compute2 = temp;

		while (compute1.size() != 1) {
			compute1 = w.transform1(compute1);
			if (compute2.size() != (prefixSum.size() - 1)) {
				temp = w.transform2(compute1);
				temp.addAll(compute2);
				compute2 = temp;
			}
		}
		compute1.addAll(compute2);

		transformArray = w.transform3(compute1);
		finalArray = w.transform4(transformArray);
	}

	public List<Float> Query(boolean flag) {

		List<Float> compute2 = new ArrayList<Float>();
		List<Float> temp = new ArrayList<Float>();
		List<Float> transformArray = new ArrayList<Float>();

		Wavelet w = new Wavelet();

		compute1 = w.transform1(prefixSum);
		compute2 = w.transform2(prefixSum);
		temp = w.transform2(compute1);

		temp.addAll(compute2);
		compute2 = temp;

		while (compute1.size() != 1) {
			compute1 = w.transform1(compute1);
			if (compute2.size() != (prefixSum.size() - 1)) {
				temp = w.transform2(compute1);
				temp.addAll(compute2);
				compute2 = temp;
			}
		}
		compute1.addAll(compute2);

		transformArray = w.transform3(compute1);
		finalArray = w.transform4(transformArray);

		if (flag == true)
			return finalArray;

		return compute1;
	}

	// Performs the Query and Calculates the time required for query on original
	// and Wavelet
	public void insertQuery() {
		int lowerIndex;
		int higherIndex;

		Scanner sc = new Scanner(System.in);
		System.out.print("Enter lower Index: ");
		lowerIndex = sc.nextInt();
		System.out.print("Enter higher Index: ");
		higherIndex = sc.nextInt();

		long startTime = System.nanoTime();
		Float queryresult = finalArray.get(higherIndex - 1) - finalArray.get(lowerIndex - 1);
		long stopTime = System.nanoTime();
		long elapsedTime = stopTime - startTime;

		System.out.println("Query result: " + queryresult);

		System.out.println("Time Required : " + elapsedTime + " nano seconds!");

		startTime = System.nanoTime();
		float actualsum = 0;

		for (int i = lowerIndex - 1; i < higherIndex; i++) {
			actualsum = actualsum + lines.get(i);

		}

		stopTime = System.nanoTime();
		elapsedTime = stopTime - startTime;
		System.out.println("Actual sum: " + actualsum);
		System.out.println("Time Required : " + elapsedTime + " nano seconds!");

		System.out.println("Error percentage: " + ((actualsum - queryresult) / actualsum) * 100 + "%");

	}

	// Calcultes the final step in wavelet tranform on which we perform queries.
	private List<Float> transform4(List<Float> original) {
		List<Float> result = new ArrayList<Float>();
		List<Float> temp = new ArrayList<Float>();
		int l;
		for (int i = 0; i < 2; i++)
			result.add(original.get(i));

		for (int i = 0; i < result.size(); i++) {
			temp.add(0.0f);
		}
		int k = 0;
		for (int i = 0; i < result.size() / 2; i++) {
			temp.set(k, result.get(i) + result.get((result.size() / 2) + i));
			temp.set(k + 1, result.get(i) - result.get((result.size() / 2) + i));
			k = k + 2;
		}
		result = temp;
		temp = new ArrayList<Float>();

		while (result.size() != original.size()) {
			l = (result.size()) * 2;
			for (int i = result.size(); i < l; i++) {
				result.add(original.get(i));
			}

			l = result.size();
			for (int i = 0; i < l; i++) {
				temp.add(0.0f);
			}
			int j = 0;
			for (int i = 0; i < result.size() / 2; i++) {
				temp.set(j, result.get(i) + result.get((result.size() / 2) + i));
				temp.set(j + 1, result.get(i) - result.get((result.size() / 2) + i));
				j = j + 2;
			}
			result = temp;
			temp = new ArrayList<Float>();

		}

		return result;
	}

	// Used to Calculate top 50 Absolute values int Wavelate cube for reverse
	// transform.
	List<Float> transform3(List<Float> original) {
		// TODO Auto-generated method stub
		List<Float> result = new ArrayList<Float>();
		List<Float> absolutetemp = new ArrayList<Float>();
		int indext;

		for (int i = 0; i < original.size(); i++)
			absolutetemp.add(0.0f);
		for (int i = 0; i < original.size(); i++)
			absolutetemp.set(i, Math.abs(original.get(i)));

		Float[] tempArray = absolutetemp.toArray(new Float[absolutetemp.size()]);
		int nummax = 50;
		Float[] copy = Arrays.copyOf(tempArray, tempArray.length);
		Arrays.sort(copy);
		Float[] copyArray = Arrays.copyOfRange(copy, copy.length - nummax, copy.length);
		int[] indexresult = new int[nummax];
		int resultPos = 0;
		for (int i = 0; i < tempArray.length; i++) {
			Float onTrial = tempArray[i];
			int index = Arrays.binarySearch(copyArray, onTrial);
			if (index < 0)
				continue;
			indexresult[resultPos++] = i;
		}

		for (int i = 0; i < original.size(); i++)
			result.add(0.0f);

		for (int i = 0; i < indexresult.length; i++) {
			indext = indexresult[i];
			result.set(indext, original.get(indext));
		}

		return result;
	}

	// Calculated PrefixSum of the Original Data.
	List<Float> fillPrefixSum(List<Float> arr, long n) {
		List<Float> prefixSum = new ArrayList<Float>();
		Float a;
		a = arr.get(0);
		prefixSum.add(a);

		for (int i = 1; i < n; i++)
			prefixSum.add(0.0f);
		// Adding present element with previous element
		for (int i = 1; i < n; i++)
			prefixSum.set(i, prefixSum.get(i - 1) + arr.get(i));
		return prefixSum;
	}

	// Method Used to compute Haar Wavelet Transform cube (Computes for
	// h=[0.5,0.5]).
	List<Float> transform1(List<Float> arr) {
		List<Float> tarray = new ArrayList<Float>();
		for (int i = 1; i < (arr.size() / 2) + 1; i++)
			tarray.add(0.0f);

		int j = 0;

		for (int i = 0; i < arr.size(); i++) {
			tarray.set(j, (float) (arr.get(i) * 0.5 + arr.get(i + 1) * 0.5));
			i = i + 1;
			j = j + 1;

		}

		return tarray;
	}

	// Method Used to compute Haar Wavelet Transform cube (Computes for
	// g=[0.5,-0.5]).
	List<Float> transform2(List<Float> arr) {
		List<Float> tarray2 = new ArrayList<Float>();
		if (arr.size() == 1)
			tarray2.add(0.0f);
		for (int i = 1; i < (arr.size() / 2) + 1; i++)
			tarray2.add(0.0f);
		int k = 0;
		for (int i = 0; i < arr.size(); i++) {
			tarray2.set(k, (float) ((arr.get(i) * (0.5)) - (arr.get(i + 1) * 0.5)));
			k = k + 1;
			i = i + 1;
		}
		return tarray2;
	}

}
