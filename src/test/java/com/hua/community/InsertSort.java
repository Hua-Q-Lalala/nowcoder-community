package com.hua.community;

import javax.xml.bind.ValidationEvent;
import java.util.Arrays;

/**
 * @create 2022-04-04 18:52
 */
public class InsertSort {

    public static void main(String[] args) {

        int[] nums = {9, 5, 6, 10, 1};

        for (int i = 1; i < nums.length; i++) {
            int value = nums[i];
            int index1 = i - 1;

            while (index1 >= 0 && nums[index1] > value) {
                nums[index1 + 1] = nums[index1--];
            }

            nums[++index1] = value;
            System.out.println(Arrays.toString(nums));
        }

    }
}
