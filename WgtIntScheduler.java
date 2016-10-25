import java.util.ArrayList;
import java.util.Collections;

public class WgtIntScheduler {

    public static void main(String[] args) {
        int[] stime = {4, 3, 2, 10, 7};
        int[] ftime = {7, 10, 6, 13, 9};
        int[] weight = {6, 6, 5, 2, 8};
        int [] res = getOptSet(stime, ftime, weight);
        for (int i = 1; i < res.length; i++) {
        }
    }
    static int[] getOptSet(int[] stime, int[] ftime, int[] weight) {
        int len = 0;
        for (int i = 0; i < ftime.length; i++) {
            len = Math.max(len, ftime[i]);
        }
        len++;

        int table[] = new int[len];
        int intervalAdded[] = new int[len];

        int lastUse = -1;
        for (int i = 1; i < len; i++) {
            int c1 = table[i-1];
            int c2 = 0;

            for (int j = 0; j < ftime.length; j++) {
                if (ftime[j] == i) {
                    if (table[stime[j]] + weight[j] > c2) {
                        c2 = table[stime[j]] + weight[j];
                        lastUse = j;
                    }
                }
            }

            if (c2 > c1) {
                intervalAdded[i] = lastUse;
                table[i] = c2;
            }
            else {
                intervalAdded[i] = -1;
                table[i] = c1;
            }
        }
        
        //backtrace
        ArrayList<Integer> nums = new ArrayList<>();

        int currentWeight = table[len-1];
        for (int i = len-1; i >= 1; i--) {
            int weightAfter = table[i];
            
            int interval = intervalAdded[i];
            if (interval > -1){ 
                int weightAdded = weight[interval];
                int tempWeight = currentWeight - weightAdded;
                for(int j = i; j >= 1; j--) {
                    if (table[j] == tempWeight) {
                        nums.add(interval + 1);
                        currentWeight = tempWeight;
                        i = j;
                        break;
                    }
                }

            }

        }
        Collections.sort(nums);
        int toRet[] = new int[nums.size()];
        for (int i = 0; i < nums.size(); i++) {
            toRet[i] = nums.get(i);
            System.out.println(toRet[i]);
        }

        return toRet;
    }

}


