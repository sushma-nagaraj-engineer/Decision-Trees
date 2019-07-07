# Decision-Trees
In this project I have implemented a Inducing Decision Tree Learning Algorithm and tested it.  
Description:
Each dataset is divided into three sets: the training set, the validation set and the test set. Data sets are in CSV format. The first line in the file gives the attribute names. Each line after that is a training (or test)  that contains a list of attribute values separated by a comma. The last attribute is the class-variable. All attributes take values from the domain [0,1].
In order to select the next attribute I have implemented the following two heuristics:
1. Information gain heuristic. 
2. Variance impurity heuristic described below.
Let K denote the number of examples in the training set. Let K0 denote the number of training examples that have class = 0 and K1 denote the number of training examples that have class = 1.
The variance impurity of the training set S is defined as:
𝑉𝐼(𝑆)= 𝐾0𝐾𝐾1𝐾
The impurity is 0 when the data is pure and the gain is computed using:
𝐺𝑎𝑖𝑛(𝑆:𝑋)=𝑉𝐼(𝑆) − ΣPr(𝑥)𝑉𝐼(𝑆𝑥𝑥 𝜖 𝑉𝑎𝑙𝑢𝑒𝑠(𝑋))
where X is an attribute, 𝑆𝑥 denotes the set of training examples that have X = x and Pr(x) is the fraction of the training examples that have X = x (i.e., the number of training examples that have X = x divided by the number of training examples in S).
I have implemented a function to print the decision tree to standard output in the following format:
wesley = 0 :
| honor = 0 :
| | barclay = 0 : 1
| | barclay = 1 : 0
| honor = 1 :
| | tea = 0 : 0
| | tea = 1 : 1
wesley = 1 : 0

According to this tree, if wesley = 0 and honor = 0 and barclay = 0, then the class value of the corresponding instance should be 1. In other words, the value appearing before a colon is an attribute value, and the value appearing after a colon is a class value.
Once the code is compiled,theprogram takes as input the following six arguments through the command line:
.\program <L> <K> <training-set> <validation-set> <test-set> <to-print>
L: integer (used in the post-pruning algorithm)
K: integer (used in the post-pruning algorithm)
to-print:{yes,no}
It outputs the accuracies on the test set for decision trees constructed using the two heuristics as well as the accuracies for their post-pruned versions for the given values of L and K. If to-print equals yes, it prints the decision tree in the format described above to the standard output.
