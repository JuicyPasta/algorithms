import java.util.Scanner;
import java.util.Stack;
import java.io.File;
import java.io.IOException;

public class EditDistance {

    public static void main(String [] args) throws IOException {
        Scanner scan = new Scanner(new File(args[0]));
        String s1 = scan.nextLine();
        String s2 = scan.nextLine();

        String similar = EditDistance.LCS(s1, s2, false);

    }

    public static String LCS(String s1, String s2, boolean print) {
        int s1Len = s1.length();
        int s2Len = s2.length();


        // Table construction:
        
        // we use a two dimensional table for this dynamic 
        // programming problem
        int [][] table = new int [s1Len + 1][s2Len + 1];

        // Iterate through the table like a page in a book
        for (int y = 1; y <= s1Len; y++) {
            for (int x = 1; x <= s2Len; x++) {
                // If the two characters match, fill the current entry
                // with the value of the state when both strings were 
                // exactly one less, and increase that by one.
                if (s1.charAt(y-1) == s2.charAt(x-1)) {
                    table[y][x] = table[y-1][x-1] + 1;
                }

                // If the last two characters did not match, the current 
                // LCS is the most it ever was in the past. This is a 
                // typical pattern in dynamic programming.
                else {
                    int up1 = table[y-1][x];
                    int left1 = table[y][x-1];
                    table[y][x] = Math.max(up1, left1);
                }
            }
        }


        // Backtrace: 


        // start the backtrace in the bottom right corner
        int x = s1Len;
        int y = s2Len;

        // this will be longest common subsequence
        String toRet = "";
        int editDistance = 0;

        // The information from the backtrace gets revealed in 
        // reverse order so we use a stack to correct that
        Stack<String> display = new Stack<String>();

        // while the backtrace has not reached a "base case" section in our table 
        while (x > 0 && y > 0) {

            // Case where ending characters match, go diagonally up and to the left
            if (s1.charAt(x-1) == s2.charAt(y-1)) {
                char matchingChar = s1.charAt(x-1);
                toRet = matchingChar + toRet;
                String nextMessage = matchingChar + " " + matchingChar + " 0";
                display.push(nextMessage);
                x--;
                y--;
            }

            // Case where we can go diagonally up-left on the table without changing the count
            // this means that we can ignore the last character and cooresponds with 
            // an addition of 1 to the edit distance
            else if (table[x][y] == table[x-1][y-1]) {
                String nextMessage = s1.charAt(x-1) + " " + s2.charAt(y-1) + " 1";
                display.push(nextMessage);
                editDistance++;
                x--;
                y--;
            }

            // the next two cases are for when we have to move just up, or just left
            // up the table. This means that we are only eating one character but not
            // the other. In this case we increase the edit distance by 2.
            else if (table[x-1][y] == table[x][y]) {
                String nextMessage = s1.charAt(x-1) + " - 2";
                display.push(nextMessage);
                editDistance += 2;
                x--;
            }
            else if(table[x][y-1] == table[x][y]) {
                String nextMessage = "- " + s2.charAt(y-1) + " 2";
                display.push(nextMessage);
                editDistance += 2;
                y--;
            }
        }

        // when x or y gets to an edge, go ahead and finish the job
        while (x > 0) {
            String nextMessage = s1.charAt(x-1) + " - 2";
            display.push(nextMessage);
            editDistance += 2;
            x--;
        }
        while (y > 0) {
            String nextMessage = "- " + s2.charAt(y-1) + " 2";
            display.push(nextMessage);
            editDistance += 2;
            y--;
        }

        System.out.printf("Edit Distance = %d\n", editDistance);
        
        // print the output out in the correct order
        while(print && !display.isEmpty()) {
            System.out.println(display.pop());
        }

        return toRet;
    }
}
