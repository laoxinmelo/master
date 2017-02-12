package com.raul.bupt.word_vector.pre;

import com.ansj.vec.Word2VEC;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class SimiQuery {

	public static void main(String[] args) throws Exception
	{
		Word2VEC vec = new Word2VEC();
		vec.loadJavaModel("library/model/ecigarVector");

		SimiWordQuery(vec);
	}


	/**
	 * 加载某个模型，计算与某个词最为相近的其他N个词
	 * @param vec
	 */
	private static void SimiWordQuery(Word2VEC vec) {

		int i = 1; //作为是否继续查询的标注
		int j; //作为是否保存的标注

		//加载模型，同时初始化相关变量
		Scanner in = new Scanner(System.in);

		while(torf(i))
		{
			//输入所需查询的词
			System.out.print("请输入您要查询的词语：");
			String queryword = in.next();

			//获取该词的向量
			try{
			}catch(NullPointerException e)
			{
				System.out.println("该词不存在，请重新输入！！");
				continue;
			}

			//获取其近义词
			Set wordset = vec.distance(queryword);
			String words = "";
			if(wordset.size()!=0)
				System.out.println("size:"+wordset.size());

			//输出其近义词
			Iterator it = wordset.iterator();
			while(it.hasNext())
			{
				String temp = it.next().toString();
				words += temp + "\r\n";
				System.out.println(temp);
			}

			System.out.print("是否需要保存查询结果(1表示保存，0表示不保存):");
			j = in.nextInt();

			//保存结果
			if(torf(j))
			{
				//设置日期格式，作为保存文件的名称
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
				String name = df.format(new Date()) + "_" + queryword;

				//将查询内容保存
				try {
					BufferedWriter bw = new BufferedWriter(new FileWriter("result/query_result/" + name + ".txt"));
					bw.write("size:" + wordset.size() + "\r\n" + words);
					bw.flush();
					bw.close();
				}catch(IOException e) {
					e.printStackTrace();
				}
			}

			System.out.print("是否需要继续查询(1表示继续，0表示不继续):");
			i = in.nextInt();

			System.out.println("————————————————————————————————————————————————————");
		}
		System.out.println("——————结束————————");
		in.close();
	}


	/**
	 * 判断查询是否继续
	 * */
	static boolean torf(int i)
	{
		if(i==1)
			return true;
		else
			return false;
	}


	/**
	 * 计算两个词之间的语义相似度
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