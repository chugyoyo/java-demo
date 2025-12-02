package com.chugyoyo.base.javabase.leetcode.huawei;

import java.util.Scanner;

public class Teat1Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        int m = in.nextInt();
        for (int i = 0; i < n; i++) {
            // 从 i 开始往下走，形成 m 型矩阵
            String[][] arr = tryInner(i, m, n);
            // 列尽可能少
            if (arr != null) {
                // TODO
                System.out.println(arr);
                return;
            }
        }
    }

    public static String[][] tryInner(int i, int m, int n) {
        String[][] arr = new String[i + 1][m];
        int indexI = 0;
        int indexJ = 0;
        // 从 [0,0] 开始遍历，从 1 开始，往右(右 0 下 1 左 2 上 3)
        boolean success = run(arr, indexI, indexJ, 1, 0, n);
        if (success) {
            return arr;
        } else {
            return null;
        }
    }

    public static boolean run(String[][] arr, int indexI, int indexJ, int curr, int pos, int n) {
        // 前后左右都没得走
        if (arr[indexI][indexJ] != null || indexJ >= arr[0].length || indexJ < 0 || indexI >= arr.length || indexI < 0) {
            return curr >= n;
        }
        // 失败标识，就是还没走完
        // 走一步
        arr[indexI][indexJ] = String.valueOf(curr > n ? '*' : curr);
        // 判断下一步走向
        if (pos == 0) {
            // 如果可以走，就继续，否则转向
            if (indexJ + 1 < arr[0].length && arr[indexI][indexJ + 1] != null) {
                // 可以走就继续走
                return run(arr, indexI, indexJ + 1, curr + 1, pos, n);
            } else {
                // 转向
                return run(arr, indexI, indexJ, curr, (pos + 1) % 4, n);
            }
        } else if (pos == 1) {
            // 如果可以走，就继续，否则转向
            if (indexI + 1 < arr.length && arr[indexI + 1][indexJ] != null) {
                // 可以走就继续走
                return run(arr, indexI + 1, indexJ, curr + 1, pos, n);
            } else {
                // 转向
                return run(arr, indexI, indexJ, curr, (pos + 1) % 4, n);
            }
        } else if (pos == 2) {
            // 如果可以走，就继续，否则转向
            if (indexJ - 1 >= 0 && arr[indexI][indexJ - 1] != null) {
                // 可以走就继续走
                return run(arr, indexI, indexJ - 1, curr + 1, pos, n);
            } else {
                // 转向
                return run(arr, indexI, indexJ, curr, (pos + 1) % 4, n);
            }
        } else if (pos == 3) {
            // 如果可以走，就继续，否则转向
            if (indexI - 1 >= 0 && arr[indexI - 1][indexJ] != null) {
                // 可以走就继续走
                return run(arr, indexI - 1, indexJ, curr + 1, pos, n);
            } else {
                // 转向
                return run(arr, indexI, indexJ, curr, (pos + 1) % 4, n);
            }
        }
        return false;
    }
}
