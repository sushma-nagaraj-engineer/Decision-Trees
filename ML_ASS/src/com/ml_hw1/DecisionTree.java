package com.ml_hw1;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

//here I am using nose to build the tree with right an left branches
class node {
	int mark = -1;
	node left;
	node right;
	node parent;

	boolean leaf_flag = false;
	int targetattr = -1;
	int leftIndices[];
	int rightIndices[];
}

// from here various methods in the class help to build, prune and print
// decision tree
public class DecisionTree {
	private static int count = 0;

	// used this method as it could be called directly to compute the log to
	// base 2 values as it is required in heuristic formula
	private static double computeLogarithm(double fraction) {
		return Math.log10(fraction) / Math.log10(2);
	}

	// method used for selecting the attribute with max information gain
	private static node selectCriteria(node root, int[][] values,
			int[] attr_flag, int attributes, int[] List1, int h) {
		int i = 0;
		int k = 0;
		double maxheuristic = 0;
		int maxLeftIndex[] = null;
		int maxRightIndex[] = null;
		int maxIndex = -1;
		for (; i < attributes; i++) {
			if (attr_flag[i] == 0) {
				double negative = 0;
				double positive = 0;
				double left = 0;
				double right = 0;
				double leftentropy = 0, leftVarianceImpurity = 0, rightVarianceImpurity = 0;
				double rightentropy = 0;
				int[] leftIndex = new int[values.length];
				int[] rightIndex = new int[values.length];
				double entropy = 0, varianceImpurity = 0;
				double rightpositive = 0;
				double heuristic = 0;
				double rightnegative = 0, leftpositive = 0, leftnegative = 0;
				for (k = 0; k < List1.length; k++) {
					if (values[List1[k]][attributes] == 1) {
						positive++;
					} else {
						negative++;
					}
					if (values[List1[k]][i] == 1) {
						rightIndex[(int) right++] = List1[k];
						if (values[List1[k]][attributes] == 1) {
							rightpositive++;
						} else {
							rightnegative++;
						}

					} else {
						leftIndex[(int) left++] = List1[k];
						if (values[List1[k]][attributes] == 1) {
							leftpositive++;
						} else {
							leftnegative++;
						}

					}

				}
				if (h == 1) {
					entropy = (-1 * computeLogarithm(positive / List1.length) * ((positive / List1.length)))
							+ (-1 * computeLogarithm(negative / List1.length) * (negative / List1.length));
					leftentropy = (-1
							* computeLogarithm(leftpositive
									/ (leftpositive + leftnegative)) * (leftpositive / (leftpositive + leftnegative)))
							+ (-1
									* computeLogarithm(leftnegative
											/ (leftpositive + leftnegative)) * (leftnegative / (leftpositive + leftnegative)));
					rightentropy = (-1
							* computeLogarithm(rightpositive
									/ (rightpositive + rightnegative)) * (rightpositive / (rightpositive + rightnegative)))
							+ (-1
									* computeLogarithm(rightnegative
											/ (rightpositive + rightnegative)) * (rightnegative / (rightpositive + rightnegative)));
					if (Double.compare(Double.NaN, entropy) == 0) {
						entropy = 0;
					}
					if (Double.compare(Double.NaN, leftentropy) == 0) {
						leftentropy = 0;
					}
					if (Double.compare(Double.NaN, rightentropy) == 0) {
						rightentropy = 0;
					}
					heuristic = entropy
							- ((left / (left + right) * leftentropy) + (right
									/ (left + right) * rightentropy));
				} else if (h == 0) {
					varianceImpurity = (positive / List1.length)
							* (negative / List1.length);
					leftVarianceImpurity = (leftpositive / (leftpositive + leftnegative))
							* (leftnegative / (leftpositive + leftnegative));
					rightVarianceImpurity = (rightpositive / (rightpositive + rightnegative))
							* (rightnegative / (rightpositive + rightnegative));
					if (Double.compare(Double.NaN, varianceImpurity) == 0) {
						varianceImpurity = 0;
					}
					if (Double.compare(Double.NaN, leftVarianceImpurity) == 0) {
						leftVarianceImpurity = 0;
					}
					if (Double.compare(Double.NaN, rightVarianceImpurity) == 0) {
						rightVarianceImpurity = 0;
					}
					heuristic = varianceImpurity
							- ((left / (left + right) * leftVarianceImpurity) + (right
									/ (left + right) * rightVarianceImpurity));
				}

				if (heuristic >= maxheuristic) {
					maxheuristic = heuristic;
					maxIndex = i;
					int leftTempArray[] = new int[(int) left];
					for (int index = 0; index < left; index++) {
						leftTempArray[index] = leftIndex[index];
					}
					int rightTempArray[] = new int[(int) right];
					for (int index = 0; index < right; index++) {
						rightTempArray[index] = rightIndex[index];
					}
					maxLeftIndex = leftTempArray;
					maxRightIndex = rightTempArray;

				}
			}
		}
		root.targetattr = maxIndex;
		root.leftIndices = maxLeftIndex;
		root.rightIndices = maxRightIndex;
		return root;
	}

	// method to check if all samples have classification as 1
	public static boolean allPositive(int[] List1, int[][] values, int features) {
		boolean result = true;
		for (int i : List1) {
			if (values[i][features] == 0)
				result = false;
		}
		return result;

	}

	// method to check if all samples have classification as 0
	public static boolean allneg(int[] List1, int[][] values, int features) {
		boolean result1 = true;
		for (int i : List1) {
			if (values[i][features] == 1)
				result1 = false;
		}
		return result1;

	}

	// method used to compute and check if maximum number of ones or zeroes is
	// present in the target attribute.
	public static int detectMax(node root, int[][] values, int features) {
		int onesCount = 0;
		int zeroCount = 0;
		if (root.parent == null) {
			int i = 0;
			for (i = 0; i < values.length; i++) {
				if (values[i][features] == 1) {
					onesCount++;
				} else {
					zeroCount++;
				}
			}
		} else {
			for (int i : root.parent.leftIndices) {
				if (values[i][features] == 1) {
					onesCount++;
				} else {
					zeroCount++;
				}
			}

			for (int i : root.parent.rightIndices) {
				if (values[i][features] == 1) {
					onesCount++;
				} else {
					zeroCount++;
				}
			}
		}
		// using ternary operator to check if positive samples are more or less
		return zeroCount > onesCount ? 0 : 1;

	}

	// method ensures if all attributes are covered
	public static boolean checkProcess(int[] attr_flag) {
		boolean completed = true;
		for (int i : attr_flag) {
			if (i == 0)
				completed = false;
		}
		return completed;
	}

	// this method builds the decison tree using the concept of node where it
	// creates the root left and right children(nodes)
	public static node generateDecisionTree(node root, int[][] values,
			int[] attr_flag, int attr, int[] List1, node parent, int h) {
		if (root == null) {
			root = new node();
			if (List1 == null || List1.length == 0) {
				root.mark = detectMax(root, values, attr);
				root.leaf_flag = true;
				return root;
			}
			if (allPositive(List1, values, attr)) {
				root.mark = 1;
				root.leaf_flag = true;
				return root;
			}
			if (allneg(List1, values, attr)) {
				root.mark = 0;
				root.leaf_flag = true;
				return root;
			}
			if (attr == 1 || checkProcess(attr_flag)) {
				root.mark = detectMax(root, values, attr);
				root.leaf_flag = true;
				return root;
			}
		}
		root = selectCriteria(root, values, attr_flag, attr, List1, h);
		root.parent = parent;
		if (root.targetattr != -1)
			attr_flag[root.targetattr] = 1;
		int leftattr_flag[] = new int[attr_flag.length];
		int rightattr_flag[] = new int[attr_flag.length];
		for (int j = 0; j < attr_flag.length; j++) {
			leftattr_flag[j] = attr_flag[j];
			rightattr_flag[j] = attr_flag[j];

		}

		root.left = generateDecisionTree(root.left, values, leftattr_flag,
				attr, root.leftIndices, root, h);
		root.right = generateDecisionTree(root.right, values, rightattr_flag,
				attr, root.rightIndices, root, h);
		return root;
	}

	public static void printTree(node tree) {
		if (tree != null) {
			System.out.println("tree.targetattr " + tree.targetattr);
			System.out.println("tree.label " + tree.mark);
			System.out.println("tree.isLeaf " + tree.leaf_flag);
			if (tree.leftIndices != null) {
				System.out.println("tree.leftIndices ");
				for (int i : tree.leftIndices) {
					System.out.print(i + " ");
				}
			}
			if (tree.rightIndices != null) {
				System.out.println("\ntree.rightIndices ");
				for (int i : tree.rightIndices) {
					System.out.print(i + " ");
				}
			}
			System.out.println();
			printTree(tree.left);
			printTree(tree.right);
		}
	}

	// creating a copy of the tree as required by the algorithm for pruning
	public static node makeCopy(node root) {
		if (root == null)
			return root;

		node temp = new node();
		temp.mark = root.mark;
		temp.leaf_flag = root.leaf_flag;
		temp.leftIndices = root.leftIndices;
		temp.rightIndices = root.rightIndices;
		temp.targetattr = root.targetattr;
		temp.parent = root.parent;
		// copying left and right child
		temp.left = makeCopy(root.left);
		temp.right = makeCopy(root.right);
		return temp;
	}

	// method implementing the post pruning algorithm
	public static node prune(String pathName, int L, int K, node root,
			int[][] values, int features) {
		node postPrunedTree = new node();
		int i = 0;
		postPrunedTree = root;
		double maxAccuracy = accuracy_validation(pathName, root);
		System.out.println("Accuracy of orginal tree is " + maxAccuracy);
		for (i = 0; i < L; i++) {
			node newRoot = makeCopy(root);
			// used for generating random numbers as per algorithm
			Random randomNumbers = new Random();
			int M = 1 + randomNumbers.nextInt(K);
			for (int j = 1; j <= M; j++) {
				count = 0;
				int nonLeafCount = nonleafnodeCount(newRoot);
				if (nonLeafCount == 0)
					break;
				node nodeArray[] = new node[nonLeafCount];
				mapping(newRoot, nodeArray);
				int P = randomNumbers.nextInt(nonLeafCount);
				nodeArray[P] = replaceWithLeaf(nodeArray[P], values, features);

			}
			double accuracy = accuracy_validation(pathName, newRoot);

			if (accuracy > maxAccuracy) {
				postPrunedTree = newRoot;
				maxAccuracy = accuracy;
			}

		}
		System.out.println("Accuracy obtained after pruning the tree is  "
				+ maxAccuracy);
		return postPrunedTree;
	}

	// method to accuracy with validation set of data
	private static double accuracy_validation(String pathName, node newRoot) {
		int[][] validationData = generateValidateData(pathName);
		double count = 0;
		for (int i = 1; i < validationData.length; i++) {
			count += correctness(validationData[i], newRoot);
		}
		return count / validationData.length;
	}

	// method to cross check if data has been correctly classified
	private static int correctness(int[] setValues, node newRoot) {
		int index = newRoot.targetattr;
		int flag_correct = 0;
		node testNode1 = newRoot;
		while (testNode1.mark == -1) {
			if (setValues[index] == 1) {
				testNode1 = testNode1.right;
			} else {
				testNode1 = testNode1.left;
			}
			if (testNode1.mark == 1 || testNode1.mark == 0) {
				if (setValues[setValues.length - 1] == testNode1.mark) {
					flag_correct = 1;
					break;
				} else {
					break;
				}
			}
			index = testNode1.targetattr;
		}
		return flag_correct;
	}
	


	// used for extracting the validation set of data
	private static int[][] generateValidateData(String filename2) {
		int[] attributeAndCount = findAttributesWithLength(filename2);
		String inputfile = filename2;
		int[][] validationData = new int[attributeAndCount[1]][attributeAndCount[0]];
		BufferedReader bufferedReader = null;
		String line = "";
		String splitbasis3 = ",";
		try {
			bufferedReader = new BufferedReader(new FileReader(inputfile));
			int i = 0;
			int count = 0;
			while ((line = bufferedReader.readLine()) != null) {
				String[] values = line.split(splitbasis3);
				int j = 0;
				if (count == 0) {
					count++;
					continue;
				} else {
					for (String dataline : values) {
						validationData[i][j++] = Integer.parseInt(dataline);
					}
				}
				i++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return validationData;
	}

	// method used to detect the majority(1 or 0) for a target attribute
	private static int majorityDetector(node root, int[][] values, int features) {
		int onesCount = 0;
		int zeroCount = 0;
		if (root.leftIndices != null) {
			for (int i : root.leftIndices) {
				if (values[i][features] == 1) {
					onesCount++;
				} else {
					zeroCount++;
				}
			}
		}

		if (root.rightIndices != null) {
			for (int i : root.rightIndices) {
				if (values[i][features] == 1) {
					onesCount++;
				} else {
					zeroCount++;
				}
			}
		}
		return zeroCount > onesCount ? 0 : 1;
	}

	// this method will create a leaf node with the one that has maximum number
	// of same target attributes like majority is one or zero
	private static node replaceWithLeaf(node node, int[][] values, int features) {
		node.leaf_flag = true;
		node.mark = majorityDetector(node, values, features);
		node.left = null;
		node.right = null;
		return node;
	}

	// this method is used to map index and respective nodes at that particular
	// index as per the algorithm
	private static void mapping(node root, node[] nodeArray) {
		if (root == null || root.leaf_flag) {
			return;
		}
		nodeArray[count++] = root;
		if (root.left != null) {
			mapping(root.left, nodeArray);
		}
		if (root.right != null) {
			mapping(root.right, nodeArray);
		}
	}

	//method to compute number of non leaf node
	private static int nonleafnodeCount(node root) {
		if (root == null || root.leaf_flag)
			return 0;
		else
			return (1 + nonleafnodeCount(root.left) + nonleafnodeCount(root.right));
	}

	public static void main(String[] args) {
		// Checking if the number of arguments entered is six or not
		if (args.length != 6) {
			System.err.println("You need to enter 6 command line arguments."
					+ " Please refer to the readme file for more information.");
			return;
		}
		try {
			// Using ParseInt as the command line arguments in JAVA takes String
			// values
			int L = Integer.parseInt(args[0]);
			int K = Integer.parseInt(args[1]);
			// method call to find the number of attributes and total number of
			// data samples
			int[] attributesWithLength = findAttributesWithLength(args[2]);
			// creating 2D array view of attributes and total number of sample
			// data and extracting the attribute names into string array
			int[][] view = new int[attributesWithLength[1]][attributesWithLength[0]];
			String[] attributeName = new String[attributesWithLength[0]];
			int count1;
			node root[] = new node[2];
			node pruneTree[] = new node[2];

			for (count1 = 1; count1 >= 0; count1--) {
				int[] attr_flag = new int[attributesWithLength[0]];
				int[] List1 = new int[view.length];

				valueUpload(args[2], view, attributeName, attr_flag, List1,
						attributesWithLength[0]);
				if (count1 == 0)
					System.out
							.println("Decision tree printed using Variance Impurity heuristic Approach:");
				else
					System.out
							.println("Decision tree printed  using Information Gain heuristic Approach:");
				// attributelength-1 is done because the last column in the
				// input file corresponds to classification
				root[count1] = generateDecisionTree(null, view, attr_flag,
						attributesWithLength[0] - 1, List1, null, count1);
				// validation data is used for pruning
				pruneTree[count1] = prune(args[3], L, K, root[count1], view,
						attributesWithLength[0] - 1);
				// test data is used to check the accuracy of the decision tree
				// constructed
				System.out.println("Accuracy computed for the testing data "

				+ computeAccuracyTest(args[4], root[count1]));
				System.out
						.println("Accuracy computed for testing data using pruned trees "
								+ computeAccuracyTest(args[4],
										pruneTree[count1]));
				if (args[5].equalsIgnoreCase("yes")) {
					System.out.println("Resultant Tree obtained before pruning");
					printTree(root[count1], 0, attributeName);
					System.out.println("Resultant Tree obtained after pruning:");
					printTree(pruneTree[count1], 0, attributeName);
				}

			}

		}
		// Handling number format exception that might occur if either the first
		// or the second argument is not a integer
		catch (NumberFormatException e) {
			System.err.println("Argument entered must be an integer.");
			System.exit(1);

		}
	}

	// method implemented to find number of attributes and the number of sample
	// data
	private static int[] findAttributesWithLength(String File1) {
		BufferedReader bufferedReader = null;
		String line = "";
		String splitBasis = ",";
		int total = 0;
		int[] attributeAndCount = new int[2];
		try {

			bufferedReader = new BufferedReader(new FileReader(File1));
			while ((line = bufferedReader.readLine()) != null) {
				if (total == 0) {
					String[] a = line.split(splitBasis);
					attributeAndCount[0] = a.length;
				}
				total++;
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		// the total variable will have a count of total number of test data
		attributeAndCount[1] = total;
		return attributeAndCount;
	}

	// this method is used to extract the actual data from given set of data and
	// sets up a platform upload the values to data structure like tree in JAVA
	private static void valueUpload(String fileName1, int[][] values,
			String[] attributeName, int[] attr_flag, int[] List1, int features) {
		String input = fileName1;
		BufferedReader bufferedReader = null;
		String line = "";
		String splitbasis_a = ",";
		for (int k = 0; k < features; k++) {
			attr_flag[k] = 0;
		}

		for (int k1 = 0; k1 < values.length; k1++) {
			List1[k1] = k1;
		}
		try {

			bufferedReader = new BufferedReader(new FileReader(input));
			int i = 0;
			while ((line = bufferedReader.readLine()) != null) {
				String[] lineParameters = line.split(splitbasis_a);
				int j = 0;
				if (i == 0) {
					for (String lineParameter : lineParameters) {
						attributeName[j++] = lineParameter;
					}
				}

				else {

					for (String lineParameter : lineParameters) {
						values[i][j++] = Integer.parseInt(lineParameter);
					}
				}
				i++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// method used for printing the tree provided yes is given in the command
	// line argument
	private static void printTree(node root, int linePrint,
			String[] attributeName) {
		int record = linePrint;
		if (root.leaf_flag) {
			System.out.println(" " + root.mark);
			return;
		}
		for (int i = 0; i < record; i++) {
			System.out.print("| ");
		}
		if (root.left != null && root.left.leaf_flag && root.targetattr != -1)
			System.out.print(attributeName[root.targetattr] + "= 0 :");
		else if (root.targetattr != -1)
			System.out.println(attributeName[root.targetattr] + "= 0 :");

		linePrint++;
		printTree(root.left, linePrint, attributeName);
		for (int i = 0; i < record; i++) {
			System.out.print("| ");
		}
		if (root.right != null && root.right.leaf_flag && root.targetattr != -1)
			System.out.print(attributeName[root.targetattr] + "= 1 :");
		else if (root.targetattr != -1)
			System.out.println(attributeName[root.targetattr] + "= 1 :");
		printTree(root.right, linePrint, attributeName);
	}

	// method to compute accuracy
	private static double computeAccuracyTest(String pathName, node root) {
		double accuracy = 0;
		int[][] testData = testDataLoad(pathName);
		for (int i = 0; i < testData.length; i++) {
			accuracy += correctness(testData[i], root);
		}
		return accuracy / testData.length;

	}

	// method used to load the test data vlaues, here i have used the same
	// findAttributes method as before
	private static int[][] testDataLoad(String filename) {
		int[] attributeAndCount = findAttributesWithLength(filename);
		String File2 = filename;
		int[][] validationDataSet = new int[attributeAndCount[1]][attributeAndCount[0]];
		BufferedReader bufferReader = null;
		String line = "";
		String splitbased = ",";
		try {

			bufferReader = new BufferedReader(new FileReader(filename));
			int i = 0;
			int count = 0;
			while ((line = bufferReader.readLine()) != null) {
				String[] getdata = line.split(splitbased);
				int j = 0;
				if (count == 0) {
					count++;
					continue;
				}

				else {

					for (String feature : getdata) {
						validationDataSet[i][j++] = Integer.parseInt(feature);
					}
				}
				i++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bufferReader != null) {
				try {
					bufferReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return validationDataSet;
	}
}
