package com.chugyoyo.base.javabase.leetcode.huawei;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.HashSet;
import java.util.Set;

// 7
//v02 R20c1B 02
//Vv02R1 c1
// V002R020C101
//v2R20B12c01
//v2R20c01B12ab
//v2 b002R2 0c0 1
//v 0 2c101R20
//v23B23R1
public class Test2Main {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int N = Integer.parseInt(in.nextLine());
        Set<String> strSet = new HashSet<>();
        for (int i = 0; i < N; i++) {
            String str = in.nextLine();
            str = str.replaceAll(" ", "");
            String s = validStr(str);
            if (s != null) {
                strSet.add(s);
            }
        }
        if (!strSet.isEmpty()) {
            strSet.stream().sorted((s1, s2) -> s2.compareTo(s1)).forEach(
                    str -> System.out.println(str));
        } else {
            System.out.println("-1");
        }
    }

    private static String validStr(String str) {
        // TODO
        int i = 0;
        List<String> newStrList = new ArrayList<>();
        // 检查字母
        Set<Character> set = new HashSet<>();
        set.add('V');
        set.add('R');
        set.add('C');
        set.add('B');
        while (i < str.length()) {
            // 记录
            String newStr = "";
            char c = str.charAt(i);
            c = change(c);
            if (set.contains(c)) {
                set.remove(c);
            } else {
               return null;
            }
            newStr += c + "";
            i++;
            // 检查数字 1-3 位
            int numCount = 0;
            String numStr = "";
            while (i < str.length() && str.charAt(i) >= '0' && str.charAt(i) <= '9') {
                numCount++;
                numStr += str.charAt(i);
                i++;
            }
            if (!(numCount >= 1 && numCount <= 3)) {
                return null;
            }
            // 补充 0
            for (int j = 0; j < 3 - numCount; j++) {
                newStr += "0";
            }
            newStr += numStr;
            newStrList.add(newStr);
        }
        if (set.contains('V') || set.contains('R') || set.contains('C')) {
            return null;
        }
        newStrList.sort((s1, s2) -> s2.compareTo(s1));
        String returnStr = "";
        for (String s : newStrList) {
            returnStr += s;
        }
        return returnStr;
    }

    public static char change(char c) {
        if (c == 'v') return 'V';
        else if (c == 'r') return 'R';
        else if (c == 'c') return 'C';
        else if (c == 'b') return 'B';
        return c;
    }
}
