package com.ansj.vec.application;

import com.ansj.vec.Word2VEC;

import java.io.BufferedWriter;
import java.io.FileWriter;

import java.io.IOException;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

import java.lang.NullPointerException;

import java.util.Date;
import java.text.SimpleDateFormat;
/**
 * ???????????N??
 * @author Administrator
 *
 */
public class DistanceQuery {

	public static void main(String[] args) throws Exception
	{

		Word2VEC vec = new Word2VEC();
		vec.loadJavaModel("library/javaVector_n");


	}



	private static void SimiWordQuery(Word2VEC vec) {

		int i = 1; //???????????
		int j; //?????????

		Scanner in = new Scanner(System.in);

		while(torf(i))
		{
			//????????
			System.out.print("???????????");
			String queryword = in.next();

			//???????
			try{
			}catch(NullPointerException e)
			{
				System.out.println("?????????????");
				continue;
			}

			//??????
			Set wordset = vec.distance(queryword);
			String words = "";
			if(wordset.size()!=0)
				System.out.println("size:"+wordset.size());

			//??????
			Iterator it = wordset.iterator();
			while(it.hasNext())
			{
				String temp = it.next().toString();
				words += temp + "\r\n";
				System.out.println(temp);
			}

			System.out.print("??????????(1?????0?????):");
			j = in.nextInt();

			//????
			if(torf(j))
			{
				//????????????????
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
				String name = df.format(new Date()) + "_" + queryword;

				//???????
				try {
					BufferedWriter bw = new BufferedWriter(new FileWriter("result/query_result/" + name + ".txt"));
					bw.write("size:" + wordset.size() + "\r\n" + words);
					bw.flush();
					bw.close();
				}catch (IOException e) {
					e.printStackTrace();
				}
			}

			System.out.print("????????(1?????0?????):");
			i = in.nextInt();

			System.out.println("????????????????????????????????????????????????????");

		}
		System.out.println("????????????????");

		in.close();



	}


	/**
	 * ?????????????
	 * */
	private static boolean torf(int i)
	{
		if(i==1)
			return true;
		else
			return false;
	}


	/**
	 * ?????????????
	 * @param word2VEC
	 * @param word1
	 * @param word2
     * @return
     */
	private static float getSimilarity(Word2VEC word2VEC,String word1,String word2) {

		float[] f1 = word2VEC.getWordVector(word1);
		float[] f2 = word2VEC.getWordVector(word2);

		float dist = 0;
		float temp1 = 0;
		float temp2 = 0;
		for (int i = 0; i < f1.length; i++)
		{
			dist += f1[i] * f2[i];
			temp1 += f1[i] * f1[i];
			temp2 += f2[i] * f2[i];
		}
		dist = dist/(float)(Math.sqrt(Double.valueOf(temp1))*Math.sqrt(Double.valueOf(temp2)));

		return dist;
	}
}
