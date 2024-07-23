package com.back.partnerback.utils;

import java.util.List;

/** 编辑距离算法-- 两个串最少的距离(字符串1通过最少多少次增删改查的方式变成字符串2)
 * @Author: 楼兰
 * @CreateTime: 2024-07-22
 * @Description:
 */
public class AlgorithmUtil {

    public static int minDistance(List<String> str1, List<String> str2) {
        int len1 = str1.size();
        int len2 = str2.size();

        // 创建一个二维数组dp，其中dp[i][j]表示str1的前i个字符和str2的前j个字符之间的编辑距离
        int[][] dp = new int[len1 + 1][len2 + 1];

        // 初始化第一行和第一列
        for (int i = 0; i <= len1; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= len2; j++) {
            dp[0][j] = j;
        }

        // 填充dp数组
        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                if (str1.get(i - 1).equals(str2.get(j - 1))) {
                    dp[i][j] = dp[i - 1][j - 1]; // 字符相等，不需要编辑
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j], // 插入
                            Math.min(dp[i][j - 1], // 删除
                                    dp[i - 1][j - 1])); // 替换
                }
            }
        }

        // dp[len1][len2]存储了最终的编辑距离
        return dp[len1][len2];
    }
}
