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
	 * ����ĳ��ģ�ͣ�������ĳ������Ϊ���������N����
	 * @param vec
	 */
	private static void SimiWordQuery(Word2VEC vec) {

		int i = 1; //��Ϊ�Ƿ������ѯ�ı�ע
		int j; //��Ϊ�Ƿ񱣴�ı�ע

		//����ģ�ͣ�ͬʱ��ʼ����ر���
		Scanner in = new Scanner(System.in);

		while(torf(i))
		{
			//���������ѯ�Ĵ�
			System.out.print("��������Ҫ��ѯ�Ĵ��");
			String queryword = in.next();

			//��ȡ�ôʵ�����
			try{
			}catch(NullPointerException e)
			{
				System.out.println("�ôʲ����ڣ����������룡��");
				continue;
			}

			//��ȡ������
			Set wordset = vec.distance(queryword);
			String words = "";
			if(wordset.size()!=0)
				System.out.println("size:"+wordset.size());

			//���������
			Iterator it = wordset.iterator();
			while(it.hasNext())
			{
				String temp = it.next().toString();
				words += temp + "\r\n";
				System.out.println(temp);
			}

			System.out.print("�Ƿ���Ҫ�����ѯ���(1��ʾ���棬0��ʾ������):");
			j = in.nextInt();

			//������
			if(torf(j))
			{
				//�������ڸ�ʽ����Ϊ�����ļ�������
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
				String name = df.format(new Date()) + "_" + queryword;

				//����ѯ���ݱ���
				try {
					BufferedWriter bw = new BufferedWriter(new FileWriter("result/query_result/" + name + ".txt"));
					bw.write("size:" + wordset.size() + "\r\n" + words);
					bw.flush();
					bw.close();
				}catch(IOException e) {
					e.printStackTrace();
				}
			}

			System.out.print("�Ƿ���Ҫ������ѯ(1��ʾ������0��ʾ������):");
			i = in.nextInt();

			System.out.println("��������������������������������������������������������������������������������������������������������");
		}
		System.out.println("��������������������������������");
		in.close();
	}


	/**
	 * �жϲ�ѯ�Ƿ����
	 * */
	static boolean torf(int i)
	{
		if(i==1)
			return true;
		else
			return false;
	}


	/**
	 * ����������֮����������ƶ�
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