package com.ansj.vec;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.Iterator;

import com.ansj.vec.domain.WordEntry;

public class Word2VEC {

	public static void main(String[] args) throws IOException {

		// Learn learn = new Learn();
		// learn.learnFile(new File("library/xh.txt"));
		// learn.saveModel(new File("library/javaSkip1"));

		Word2VEC vec = new Word2VEC();
		//加载模型
		vec.loadJavaModel("library/javaVector");

		// System.out.println("中国" + "\t" +
		// Arrays.toString(vec.getWordVector("中国")));
		// ;
		// System.out.println("毛泽东" + "\t" +
		// Arrays.toString(vec.getWordVector("毛泽东")));
		// ;
		// System.out.println("足球" + "\t" +
		// Arrays.toString(vec.getWordVector("足球")));

		// Word2VEC vec2 = new Word2VEC();
		// vec2.loadGoogleModel("library/vectors.bin") ;
		//
		//
		String str = "负责";
		long start = System.currentTimeMillis();

		Set wordset = vec.distance(str);
		Iterator it = wordset.iterator();

		if(wordset.size()!=0)
			System.out.println("size:"+wordset.size());

		while(it.hasNext())
		{

			System.out.println(it.next().toString());
		}

		//System.out.println(System.currentTimeMillis() - start);

		System.out.println("查询时间:"+(System.currentTimeMillis() - start));
		// System.out.println(vec2.distance(str));
		//
		//
		// //男人 国王 女人
		// System.out.println(vec2.analogy("毛泽东", "毛泽东思想", "邓小平"));
	}

	private HashMap<String, float[]> wordMap = new HashMap<String, float[]>();

	private int words;
	private int size;
	private int topNSize = 25;

	/**
	 * 加载模型
	 *
	 * @param path
	 *            模型的路径
	 * @throws IOException
	 */
	public void loadGoogleModel(String path) throws IOException {
		DataInputStream dis = null;
		BufferedInputStream bis = null;
		double len = 0;
		float vector = 0;
		try {
			bis = new BufferedInputStream(new FileInputStream(path));
			dis = new DataInputStream(bis);
			// //读取词数
			words = Integer.parseInt(readString(dis));
			// //大小
			size = Integer.parseInt(readString(dis));
			String word;
			float[] vectors = null;
			for (int i = 0; i < words; i++) {
				word = readString(dis);
				vectors = new float[size];
				len = 0;
				for (int j = 0; j < size; j++) {
					vector = readFloat(dis);
					len += vector * vector;
					vectors[j] = (float) vector;
				}
				len = Math.sqrt(len);

				for (int j = 0; j < size; j++) {
					vectors[j] /= len;
				}

				wordMap.put(word, vectors);
				dis.read();
			}
		} finally {
			bis.close();
			dis.close();
		}
	}

	/**
	 * 加载模型，读取语料中所有词的向量，并对其进行归一化
	 *
	 * @param path
	 *            模型的路径
	 * @throws IOException
	 */
	public void loadJavaModel(String path) throws IOException {

		try  {
			DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(path)));
			words = dis.readInt();
			size = dis.readInt();

			float vector = 0;

			String key = null;
			float[] value = null;
			for (int i = 0; i < words; i++) {
				double len = 0;
				key = dis.readUTF();
				//System.out.println(key);
				value = new float[size];
				for (int j = 0; j < size; j++) {
					vector = dis.readFloat();
					len += vector * vector;
					value[j] = vector;
				}

				len = Math.sqrt(len);

				//归一化
				for (int j = 0; j < size; j++) {
					value[j] /= len;
				}

				wordMap.put(key, value);
			}
			dis.close();

		}catch(Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static final int MAX_SIZE = 50;

	/**
	 * 根据三个提示词找到其近义词
	 *
	 * @return
	 */
	public TreeSet<WordEntry> analogy(String word0, String word1, String word2) {
		float[] wv0 = getWordVector(word0);
		float[] wv1 = getWordVector(word1);
		float[] wv2 = getWordVector(word2);

		if (wv1 == null || wv2 == null || wv0 == null) {
			return null;
		}
		float[] wordVector = new float[size];
		for (int i = 0; i < size; i++) {
			wordVector[i] = wv1[i] - wv0[i] + wv2[i];
		}

		float[] tempVector;//在遍历中用来保存词向量
		String name;
		List<WordEntry> wordEntrys = new ArrayList<WordEntry>(topNSize);
		for (Entry<String, float[]> entry : wordMap.entrySet())
		{
			name = entry.getKey();
			if (name.equals(word0) || name.equals(word1) || name.equals(word2))
			{
				continue;
			}
			float dist = 0;
			tempVector = entry.getValue();
			for (int i = 0; i < wordVector.length; i++)
			{
				dist += wordVector[i] * tempVector[i];
			}
			insertTopN(name, dist, wordEntrys);
		}
		return new TreeSet<WordEntry>(wordEntrys);
	}

	/**
	 * 找到与之最为相近的topNSize个词
	 * @param name
	 * @param score
	 * @param wordsEntrys
	 */
	private void insertTopN(String name, float score, List<WordEntry> wordsEntrys) {
		// TODO Auto-generated method stub
		if (wordsEntrys.size() < topNSize)
		{
			wordsEntrys.add(new WordEntry(name, score));//若数量小于topNSize，直接导入
			return;
		}

		//如果数量已经达到topNSize，则将最小的替换掉
		float min = Float.MAX_VALUE;
		int minOffe = 0;
		for (int i = 0; i < topNSize; i++)
		{
			WordEntry wordEntry = wordsEntrys.get(i);
			if (min > wordEntry.score)
			{
				min = wordEntry.score;
				minOffe = i;
			}
		}

		if (score > min)
		{
			wordsEntrys.set(minOffe, new WordEntry(name, score));
		}

	}

	/**
	 * 计算出关联性最强的词
	 * @param queryWord
	 * @return
	 */
	public Set<WordEntry> distance(String queryWord) {

		float[] center = wordMap.get(queryWord);

		//System.out.println(center.length);
		//for(int i=0;i<center.length;i++)
		//{
		//	System.out.println(center[i]);
		//}

		if (center == null) {
			System.out.println("该词不存在!!!!!");
			return Collections.emptySet();
		}

		int resultSize = wordMap.size() < (topNSize+1) ? wordMap.size() : (topNSize+1);
		TreeSet<WordEntry> result = new TreeSet<WordEntry>();
		double min = Float.MIN_VALUE;

		for (Map.Entry<String, float[]> entry : wordMap.entrySet())
		{
			float[] vector = entry.getValue();
			float dist = 0;
			float temp1 = 0;
			float temp2 = 0;
			for (int i = 0; i < vector.length; i++)
			{
				dist += center[i] * vector[i];
				temp1 += center[i]*center[i];
				temp2 += vector[i]*vector[i];
			}
			dist = dist/(float)(Math.sqrt(Double.valueOf(temp1))*Math.sqrt(Double.valueOf(temp2)));

			//System.out.println(entry.getKey() + ":" + dist);

			if (dist > min)
			{
				result.add(new WordEntry(entry.getKey(), dist));
				if (resultSize < result.size())
				{
					result.pollLast();
				}
				//min = result.last().score;
			}
		}

		result.pollFirst();

		return result;
	}

	/**
	 * 找到与其关联性最强近的词+
	 * @param words
	 * @return
	 */
	public Set<WordEntry> distance(List<String> words)
	{
		float[] center = null;

		for (String word : words) {
			center = sum(center, wordMap.get(word));
		}

		if (center == null) {
			System.out.println("该词不存在！");
			return Collections.emptySet();
		}

		int resultSize = wordMap.size() < (topNSize+1) ? wordMap.size() : (topNSize+1);
		TreeSet<WordEntry> result = new TreeSet<WordEntry>();
		double min = Float.MIN_VALUE;


		for (Map.Entry<String, float[]> entry : wordMap.entrySet())
		{
			float[] vector = entry.getValue();
			float dist = 0;
			float temp1 = 0;
			float temp2 = 0;
			for (int i = 0; i < vector.length; i++)
			{
				dist += center[i] * vector[i];
				temp1 += center[i]*center[i];
				temp2 += vector[i]*vector[i];
			}
			dist = dist/(float)(Math.sqrt(Double.valueOf(temp1))*Math.sqrt(Double.valueOf(temp2)));
			//System.out.println(entry.getKey() + ":" + dist);

			if (dist > min)
			{
				result.add(new WordEntry(entry.getKey(), dist));
				if (resultSize < result.size())
				{
					result.pollLast();
				}
				//min = result.last().score;
			}
		}
		result.pollFirst();

		return result;
	}


	//求和
	private float[] sum(float[] center, float[] fs) {
		// TODO Auto-generated method stub

		if (center == null && fs == null) {
			return null;
		}

		if (fs == null) {
			return center;
		}

		if (center == null) {
			return fs;
		}

		for (int i = 0; i < fs.length; i++) {
			center[i] += fs[i];
		}

		return center;
	}

	/**
	 * 得到词向量
	 *
	 * @param word
	 * @return
	 */
	public float[] getWordVector(String word) {
		return wordMap.get(word);
	}

	public static float readFloat(InputStream is) throws IOException {
		byte[] bytes = new byte[4];
		is.read(bytes);
		return getFloat(bytes);
	}

	/**
	 * 读取一个float
	 *
	 * @param b
	 * @return
	 */
	public static float getFloat(byte[] b) {
		int accum = 0;
		accum = accum | (b[0] & 0xff) << 0;
		accum = accum | (b[1] & 0xff) << 8;
		accum = accum | (b[2] & 0xff) << 16;
		accum = accum | (b[3] & 0xff) << 24;
		return Float.intBitsToFloat(accum);
	}

	/**
	 * 读取一个字符串
	 *
	 * @param dis
	 * @return
	 * @throws IOException
	 */
	private static String readString(DataInputStream dis) throws IOException {
		// TODO Auto-generated method stub
		byte[] bytes = new byte[MAX_SIZE];
		byte b = dis.readByte();
		int i = -1;
		StringBuilder sb = new StringBuilder();
		while (b != 32 && b != 10) {
			i++;
			bytes[i] = b;
			b = dis.readByte();
			if (i == 49) {
				sb.append(new String(bytes));
				i = -1;
				bytes = new byte[MAX_SIZE];
			}
		}
		sb.append(new String(bytes, 0, i + 1));
		return sb.toString();
	}

	public int getTopNSize() {
		return topNSize;
	}

	public void setTopNSize(int topNSize) {
		this.topNSize = topNSize;
	}

	public HashMap<String, float[]> getWordMap() {
		return wordMap;
	}

	public int getWords() {
		return words;
	}

	public int getSize() {
		return size;
	}

}