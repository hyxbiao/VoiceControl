package com.hyxbiao.speech.lib;

public class Algorithm {

	public static float levenshtein(String str1, String str2) {
		int len1 = str1.length();
		int len2 = str2.length();
		
		int[][] matrix = new int[len1 + 1][len2 + 1];
		
		matrix[0][0] = 0;
		for(int i=1; i<=len1; i++) {
			matrix[i][0] = i;
		}
		for(int i=1; i<=len2; i++) {
			matrix[0][i] = i;
		}
		for(int i=1; i<=len1; i++) {
			char c1 = str1.charAt(i-1);
			for(int j=1; j<=len2; j++) {
				char c2 = str2.charAt(j-1);
				
				int cost = (c1 == c2) ? 0 : 1;
				
				int above = matrix[i-1][j] + 1;
				int left = matrix[i][j-1] + 1;
				int diag = matrix[i-1][j-1] + cost;
				
				int min = (above < left) ? above : left;
				matrix[i][j] = (min < diag) ? min : diag;
			}
		}
		int diff = matrix[len1][len2];
		float similarity = 1 - (float)diff / Math.max(len1, len2);
		
		return similarity;
	}
}
