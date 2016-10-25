import java.util.*;
import java.io.*;

public class Knapsack {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner scan = new Scanner(new File(args[0]));

        int items = scan.nextInt();

                int values[] = new int[items];
                int weights[] = new int[items];
        
                for (int i = 0; i < items; i++) {
                    scan.nextInt();
                    values[i] = scan.nextInt();
                    weights[i] = scan.nextInt();
                }
                int capacity = scan.nextInt();

        //bruteForce(values, weights, capacity);
        greedy(values, weights, capacity);
        dynamicProgramming(values, weights, capacity);
        branchAndBound(values, weights, capacity);
    }

    public static void bruteForce(int value[], int weight[], int capacity) {
        long startTime = System.currentTimeMillis();
        int len = value.length;

        // we will update these when a better knapsack is found
        int maxValue = 0;
        int maxWeight = 0;
        boolean maxSet[] = new boolean[0];

        // this is how we will generate our different sets
        // think of it as a binary number
        boolean currentSet[] = new boolean[len];

        do {
            int currentValue = 0;
            int currentWeight = 0;
            // a 1 in currentSet means the item of the respective index is "in", 0 is out
            for (int i = 0; i < len; i++) {
                if (currentSet[i]) {
                    currentValue += value[i];
                    currentWeight += weight[i];
                }
            }
            // if the set we just generated is the new best, update the best values
            if (currentWeight <= capacity && currentValue >= maxValue) {
                maxValue = currentValue;
                maxWeight = currentWeight;
                maxSet = Arrays.copyOf(currentSet, len);
            }
        } while(increment(currentSet));

        System.out.printf("Using Brute force the best feasible solution found: Value %d, Weight %d\n", maxValue, maxWeight);

        for (int i = 0; i < len; i++) {
            if (maxSet[i])
                System.out.print(i+1 + " ");
        }
        System.out.println();

        long endTime = System.currentTimeMillis();
        System.out.printf("Runtime: %f seconds\n\n", (0.0 + endTime - startTime) / 1000);
    }
    // returns false if we are at the last bitstring, otherwise creates the next bitstring
    public static boolean increment(boolean set[]) {
        for (int i = set.length-1; i >= 0; i--) {
            if (!set[i]) {
                set[i] = true;
                return true;
            }
            else 
                set[i] = false;
        }
        return false;
    }

    public static void greedy(int value[], int weight[], int capacity) {
        long startTime = System.currentTimeMillis();
        int len = value.length;

        // populate an array with Item objects
        Item items[] = new Item[len];
        for (int i = 0; i < len; i++) {
            items[i] = new Item(i, value[i], weight[i]);
        }

        // sort the items by their density
        Arrays.sort(items);

        ArrayList<Integer> bag = new ArrayList<Integer>();
        int currentWeight = 0;
        int currentValue = 0;

        // keep adding the next item as long as it fits
        for (int i = 0; i < len; i++) {
            int itemWeight = weight[items[i].index];
            int itemValue = value[items[i].index];
            if (itemWeight + currentWeight <= capacity) {
                bag.add(items[i].index);
                currentWeight += itemWeight;
                currentValue += itemValue;
            }
        }
        System.out.printf("Greedy solution (not necessarily optimal): Value %d, Weight %d\n", currentValue, currentWeight);

        // sort the bag for printing
        Collections.sort(bag);

        for (Integer i : bag) {
            System.out.print(i+1 + " ");
        }
        System.out.println();

        long endTime = System.currentTimeMillis();
        System.out.printf("Runtime: %f seconds\n\n", (0.0 + endTime - startTime) / 1000);
    }

    public static void dynamicProgramming(int value[], int weight[], int capacity) {
        long startTime = System.currentTimeMillis();

        int len = value.length;

        // item by capacity backtracking table
        int table[][] = new int [len + 1][capacity + 1];

        // populate the table
        for (int curItem = 1; curItem <= len; curItem++) {
            for (int curWeight = 1; curWeight <= capacity; curWeight++) {
                int dontAdd = table[curItem-1][curWeight];
                int add = 0;
                if (weight[curItem-1] <= curWeight) 
                    add = value[curItem-1] + table[curItem-1][curWeight - weight[curItem-1]];
                table[curItem][curWeight] = Math.max(add, dontAdd);
            }
        }

        int totalWeight = 0;
        int totalValue = table[len][capacity];

        // Backtrace
        ArrayList<Integer> items = new ArrayList<Integer>();
        int x = capacity;
        for (int y = len; y > 0; y--) {
            if (table[y][x] != table[y - 1][x]) {
                items.add(y);
                x -= weight[y-1];
                totalWeight += weight[y-1];
            }
        }

        Collections.sort(items);
        System.out.printf("Dynamic Programming solution: Value %d, Weight %d\n", totalValue, totalWeight);

        for (Integer i : items) {
            System.out.print(i + " ");
        }
        System.out.println();

        long endTime = System.currentTimeMillis();
        System.out.printf("Runtime: %f seconds\n\n", (0.0 + endTime - startTime) / 1000);
    }

    public static void branchAndBound (int value[], int weight[], int capacity) {
        long startTime = System.currentTimeMillis();

        int len = value.length;

        Item items[] = new Item[len + 1];
        for (int i = 0; i < len; i++) {
            items[i] = new Item(i, value[i], weight[i]);
        }

        Arrays.sort(items, 0, len);
        items[len] = new Item(0,1,0);

        PriorityQueue<Node> tree = new PriorityQueue<>();

        Node currentNode = new Node(null,false,0,0,items[0].density * capacity,-1);

        double currentMax = 0;
        while (currentNode.level < len - 1) {

            if (currentNode.upperBound >= currentMax) {

                Item currentItem = items[currentNode.level + 1];
                Item nextItem = items[currentNode.level + 2];

                // add without
                int bound = currentNode.value;
                int tempWeight = currentNode.weight;
                for (int bc = currentNode.level + 2; bc < len && tempWeight <= capacity; bc++) {
                    bound += items[bc].value;
                    tempWeight += items[bc].weight;
                }

                if (bound >= currentMax)
                    tree.add(new Node(currentNode, false, currentNode.value, currentNode.weight, bound, currentNode.level + 1));

                if (currentNode.weight + currentItem.weight <= capacity) {
                    int addedValues = currentNode.value + currentItem.value;
                    int addedWeights = currentNode.weight + currentItem.weight;

                    bound = addedValues;
                    tempWeight = addedWeights;
                    for (int bc = currentNode.level + 2; bc < len && tempWeight <= capacity; bc++) {
                        bound += items[bc].value;
                        tempWeight += items[bc].weight;
                    }

                    if (bound >= currentMax)
                        tree.add(new Node(currentNode, true, addedValues, addedWeights, bound, currentNode.level + 1));


                    currentMax = Math.max(currentMax, addedValues);
                }

                if ((0.0 + System.currentTimeMillis() - startTime) / 1000 > 60) {
                    break;
                }

            }

            currentNode = tree.remove();
        }
        // currentNode is the solution
        System.out.printf("Using Branch and Bound the best feasible solution found: Value %d, Weight %d\n", currentNode.value, currentNode.weight);

        ArrayList<Integer> bag = new ArrayList<Integer>();
        while (currentNode.lastNode != null) {
            if (currentNode.didAdd)
                bag.add(items[currentNode.level].index + 1);
            currentNode = currentNode.lastNode;
        }

        Collections.sort(bag);
        for (Integer i : bag) {
            System.out.print(i + " ");
        }
        System.out.println();

        long endTime = System.currentTimeMillis();
        System.out.printf("Runtime: %f seconds\n\n", (0.0 + endTime - startTime) / 1000);
    }

}

class Node implements Comparable<Node> {
    public Node lastNode;
    public int value, weight, level;
    public double upperBound;
    public boolean didAdd;

    public Node(Node lastNode, boolean didAdd, int value, int weight, double upperBound, int level) {
        this.lastNode = lastNode;
        this.didAdd = didAdd;
        this.value = value;
        this.weight = weight;
        this.upperBound = upperBound;
        this.level = level;
    }
    public int compareTo(Node other) {
        if (other.upperBound > this.upperBound) 
            return 1;
        else if (this.upperBound > other.upperBound) 
            return -1;
        else  
            return 0;
    }
}
class Item implements Comparable<Item> {
    public double density;
    public int index, value, weight;
    public Item(int index, int value, int weight) {
        this.index = index;
        this.value = value;
        this.weight = weight;
        this.density = (double)(value) / (double)(weight);
    }
    public int compareTo(Item other) {
        if (other.density > this.density) 
            return 1;
        else if (this.density > other.density) 
            return -1;
        return 0;
    }
}
