
// Author: Jackson Darrow
// Username: jldarrow

import java.util.ArrayList;

public class CombObjects {
    public static ArrayList<String> getGrayCode(int n) {
        ArrayList<String> toReturn;
        
        // base case if we only want 1 character
        if (n == 1) {
            toReturn = new ArrayList<String>();
            toReturn.add("0");
            toReturn.add("1");
            return toReturn;
        } 

        // prepend a 0 and a 1 to the results from a recursive call
        toReturn = getGrayCode(n - 1);

        // only go through the original returned data
        int size = toReturn.size();
        for (int i = 0; i < size; i ++) {
            // take off the head value, put the 0s at the beginning and the 1s at the end
            String next = toReturn.remove(0);
            toReturn.add(size - 1, "0" + next);
            toReturn.add("1" + next);
        }

        return toReturn;
    }

    public static ArrayList<String> getLexPerm(String str) {
        ArrayList<String> lexPerms = new ArrayList<String> ();

        // base case if the string is empty
        if (str.equals("")) { 
            lexPerms.add("");
            return lexPerms;
        }

        for (int i = 0; i < str.length(); i++) {
            // loop through the string taking one letter out at a time
            for (String perm : getLexPerm(str.substring(0, i) + str.substring(i + 1, str.length()))) {
                // add the letter we took out to the beginnin of the other permutations
                lexPerms.add(str.charAt(i) + perm);
            }
        }

        return lexPerms;
    }

    public static ArrayList<String> getMinChgPerm(String str) {
        ArrayList<String> minChg = new ArrayList<String> ();

        // base case if the string is empty
        if (str.equals("")) {
            minChg.add("");
            return minChg;
        }

        // remove the last character
        char rem = str.charAt(str.length() - 1);
        ArrayList<String> temp = getMinChgPerm(str.substring(0, str.length() - 1));

        for (String perm : temp) {
            // if else for alternating rignt->left / left->right insertion
            if (temp.indexOf(perm) % 2 == 1) {
                // insert in all positions left->right
                for (int i = 0; i <= perm.length(); i++) {
                    minChg.add(perm.substring(0, i) + rem + perm.substring(i, perm.length()));
                }
            } else {
                // insert in all positions right->left
                for (int i = perm.length(); i >= 0; i--) {
                    minChg.add(perm.substring(0, i) + rem + perm.substring(i, perm.length()));
                }
            }
        }

        return minChg;
    }

}
