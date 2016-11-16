package com.ansj.vec;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import love.cq.util.MapCount;

import com.ansj.vec.domain.HiddenNeuron;
import com.ansj.vec.domain.Neuron;
import com.ansj.vec.domain.WordNeuron;
import com.ansj.vec.util.Haffman;

public class Learn {

	private Map<String, Neuron> wordMap = new HashMap<String,Neuron>();
	/**
	 * 训练多少个特征
	 */
	public int layerSize = 100;//表示特征数，隐藏层的节点

	/**
	 * 上下文窗口大小
	 */
	private int window = 5;
	//sample:亚采样概率的参数，亚采样的目的是以一定概率拒绝高频词，使得低频词有更多出镜率
	private double sample = 1e-3;
	private double alpha = 0.025;//learning rate
	private double startingAlpha = alpha;

	public int EXP_TABLE_SIZE = 1000;//缓存f的运算结果，将-6到6等分成1000份

	private Boolean isCbow = false;//判断所用的模型

	private double[] expTable = new double[EXP_TABLE_SIZE];

	private int trainWordsCount = 0;//训练的单词总数

	private int MAX_EXP = 6;//最小计算到f-6，最大计算到f+6

	private static final String vocabularyPath = "library/vocabulary/";

	public Learn(Boolean isCbow, Integer layerSize, Integer window, Double alpha, Double sample) {
		createExpTable();
		if (isCbow != null) {
			this.isCbow = isCbow;
		}
		if (layerSize != null)
			this.layerSize = layerSize;
		if (window != null)
			this.window = window;
		if (alpha != null)
			this.alpha = alpha;
		if (sample != null)
			this.sample = sample;
	}

	public Learn() {
		createExpTable();
	}

	/**
	 * trainModel
	 * @throws IOException
	 */
	private void trainModel(File file) throws IOException {
		try  {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"utf-8"));
			String temp = null;
			long nextRandom = 5;//在后面程序中生成随机数
			int wordCount = 0;//语料中词的数量
			int lastWordCount = 0;//已经训练完的word个数
			int wordCountActual = 0;
			temp = br.readLine();
			while (temp != null)
			{
				if (wordCount - lastWordCount > 10000)
				{
					System.out.println("alpha:" + alpha + "\tProgress: "
							+ (int) (wordCountActual / (double) (trainWordsCount + 1) * 100)
							+ "%");
					wordCountActual += wordCount - lastWordCount;
					lastWordCount = wordCount;
					/**
					 * 调整learning rate
					 */
					alpha = startingAlpha * (1 - wordCountActual / (double) (trainWordsCount + 1));
					if (alpha < startingAlpha * 0.0001)
					{
						alpha = startingAlpha * 0.0001;
					}
				}

				String[] strs = temp.split(" ");
				wordCount += strs.length;//统计语料中词的数量

				List<WordNeuron> sentence = new ArrayList<WordNeuron>();//将该段语料加入到sentence中
				for (int i = 0; i < strs.length; i++)
				{
					Neuron entry = wordMap.get(strs[i]);//将词输入到神经元中
					if (entry == null)
					{
						continue;
					}
					//高频词亚采样，随机删掉一些高频词汇
					if (sample > 0)
					{
						double ran = (Math.sqrt(entry.freq / (sample * trainWordsCount)) + 1)
								* (sample * trainWordsCount) / entry.freq;
						//随机数生成
						nextRandom = nextRandom * 25214903917L + 11;
						//&表示按位进行“与”运算，0xFFFF化为十进制为65535
						if (ran < (nextRandom & 0xFFFF) / (double) 65536) {
							continue;
						}
					}
					sentence.add((WordNeuron) entry);
				}

				//调用CBOW或者skip-gram模型来训练，注意，不是对整个语料建模，而是对每个sentence逐一建模
				for (int index = 0; index < sentence.size(); index++)
				{
					nextRandom = nextRandom * 25214903917L + 11;
					if (isCbow)
					{
						cbowGram(index, sentence, (int) nextRandom % window);
					} else
					{
						skipGram(index, sentence, (int) nextRandom % window);
					}
				}

				temp = br.readLine();
			}

			br.close();

			System.out.println("Layersize: " + layerSize);
			System.out.println("Vocab size: " + wordMap.size());
			System.out.println("Words in train file: " + trainWordsCount);
			System.out.println("sucess train over!");
		}catch(Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * skip gram 模型训练
	 * @param index
	 * @param sentence
     * @param b
     */
	private void skipGram(int index, List<WordNeuron> sentence, int b)
	{
		// TODO Auto-generated method stub
		WordNeuron word = sentence.get(index);// 所选定输入的词，index——sentence_position
		int a, c = 0;
		for (a = b; a < window * 2 + 1 - b; a++)
		{
			//去掉index对应的word
			if (a == window) {
				continue;
			}

			c = index - window + a;  //对应到窗口中某个词的position
			if (c < 0 || c >= sentence.size()) {
				continue;
			}

			double[] neu1e = new double[layerSize];//误差累积项

			//HIERARCHICAL SOFTMAX
			List<Neuron> neurons = word.neurons; //保存输入词在二叉树中所对应的路径
			WordNeuron we = sentence.get(c);  //上下文中出现的word

			//遍历路径，进行训练
			for (int i = 0; i < neurons.size(); i++)
			{
				HiddenNeuron out = (HiddenNeuron) neurons.get(i);//在二叉树中对应路径中的节点
				double f = 0;
				// Propagate hidden -> output
				//路径上节点都对应一个f值
				for (int j = 0; j < layerSize; j++) {
					f += we.syn0[j] * out.syn1[j]; //syn1[]的初始值为0
				}
				if (f <= -MAX_EXP || f >= MAX_EXP) {
					continue;
				} else {
					f = (f + MAX_EXP) * (EXP_TABLE_SIZE / MAX_EXP / 2);
					f = expTable[(int) f];
				}

				// 'g' is the gradient multiplied by the learning rate
				// 计算梯度，路径上每个节点都对应一个g值
				double g = (1 - word.codeArr[i] - f) * alpha;
				// Propagate errors output -> hidden
				for (c = 0; c < layerSize; c++)
				{
					neu1e[c] += g * out.syn1[c];//累积误差项
				}

				// Learn weights hidden -> output
				for (c = 0; c < layerSize; c++)
				{
					out.syn1[c] += g * we.syn0[c];//调整hidden->output的权重
				}
			}

			// Learn weights input -> hidden
			for (int j = 0; j < layerSize; j++)
			{
				we.syn0[j] += neu1e[j];
			}
		}

	}

	/**
	 * 词袋模型
	 * @param index
	 * @param sentence
	 * @param b
	 */
	private void cbowGram(int index, List<WordNeuron> sentence, int b) {
		WordNeuron word = sentence.get(index);
		int a, c = 0;

		List<Neuron> neurons = word.neurons;
		double[] neu1e = new double[layerSize];//误差项，表示误差累积项
		double[] neu1 = new double[layerSize];//误差项，表示隐层节点
		WordNeuron last_word;

		for (a = b; a < window * 2 + 1 - b; a++)
			if (a != window)
			{
				c = index - window + a; //对应到窗口中某个词的position
				if (c < 0)
					continue;
				if (c >= sentence.size())
					continue;
				last_word = sentence.get(c);
				if (last_word == null)
					continue;
				for (c = 0; c < layerSize; c++)//求得sentence_position其对应上下文词的权重之和
					neu1[c] += last_word.syn0[c];//syn0[]: input->hidden
			}

		//HIERARCHICAL SOFTMAX
		for (int d = 0; d < neurons.size(); d++)
		{
			HiddenNeuron out = (HiddenNeuron) neurons.get(d);
			double f = 0;
			// Propagate hidden -> output
			for (c = 0; c < layerSize; c++)
				f += neu1[c] * out.syn1[c];

			//使得f在-MAX_EXP与MAX_EXP之间，随后可通过查询得到，并计算得出梯度值g
			if (f <= -MAX_EXP)
				continue;
			else if (f >= MAX_EXP)
				continue;
			else
				//查询指数计算的结果,其中(f + MAX_EXP)/MAX_EXP/2的范围是[0,1]
				f = expTable[(int) ((f + MAX_EXP) * (EXP_TABLE_SIZE / (MAX_EXP * 2)))];
			// 'g' is the gradient multiplied by the learning rate
			//            double g = (1 - word.codeArr[d] - f) * alpha;
			//              double g = f*(1-f)*( word.codeArr[i] - f) * alpha;
			double g = f * (1 - f) * (word.codeArr[d] - f) * alpha;


			for (c = 0; c < layerSize; c++) {
				neu1e[c] += g * out.syn1[c];//误差累加
			}
			// Learn weights hidden -> output
			for (c = 0; c < layerSize; c++) {
				out.syn1[c] += g * neu1[c];//权重调整
			}
		}
		for (a = b; a < window * 2 + 1 - b; a++)
		{
			if (a != window)
			{
				c = index - window + a;
				if (c < 0)
					continue;
				if (c >= sentence.size())
					continue;
				last_word = sentence.get(c);
				if (last_word == null)
					continue;
				for (c = 0; c < layerSize; c++)
					//Learn weights input -> hidden
					last_word.syn0[c] += neu1e[c];
			}

		}
	}

	/**
	 * 逐一读取，统计词频
	 * 同时建立词表
	 * @param file
	 * @throws IOException
	 */
	public void readVocab(File file) throws IOException {
		//自定义的MapCount
		MapCount<String> mc = new MapCount<String>();
		try
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String temp = null;
			while ((temp = br.readLine()) != null)
			{
				String[] split = temp.split(" ");
				trainWordsCount += split.length;
				for (String string : split)
				{
					byte[] bytes = string.getBytes();
					string = new String(bytes);

					//System.out.println(string);
					mc.add(string);
				}
			}
			br.close();
		}catch(IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//将词与其对应的词频全部加入到wordMap当中，同时对各个词所对应的syn0[]赋初值
		//同时建立词表，并按照降序的顺序排列
		String name = file.getName();
		name = name.substring(0, name.lastIndexOf("."));
		BufferedWriter bw = new BufferedWriter(new FileWriter( vocabularyPath + name + ".txt"));
		for (Map.Entry<String, Integer> element : sort(mc.get())) {
			wordMap.put(element.getKey(), new WordNeuron(element.getKey(), element.getValue(),
					layerSize));

			bw.write(element.getKey()+","+element.getValue() + "\r\n");

		}

		bw.flush(); bw.close();
		System.out.println("vocabulary has been built!");
	}

	/**
	 * @排序
	 * @param map
	 * @return
	 */
	public  List<Map.Entry<String, Integer>> sort(Map<String,Integer> map) {
		List<Map.Entry<String, Integer>> infoIds = new ArrayList<Map.Entry<String, Integer>>(
				map.entrySet());   //将其转化为列表形式
		Collections.sort(infoIds, new Comparator<Map.Entry<String, Integer>>()
				{
					public int compare(Map.Entry<String, Integer> o1,Map.Entry<String, Integer> o2)
					{
						return ( o1.getValue()-o2.getValue());
					}
				}
		);

		return infoIds;
	}

	/**
	 * 在训练开始之前要预先算好这些指数值！
	 * Precompute the exp() table
	 * f(x) = x / (x + 1)
	 */
	private void createExpTable() {
		for (int i = 0; i < EXP_TABLE_SIZE; i++) {
			expTable[i] = Math.exp(((i / (double) EXP_TABLE_SIZE * 2 - 1) * MAX_EXP));
			expTable[i] = expTable[i] / (expTable[i] + 1);
		}
	}

	/**
	 * 根据文件学习
	 * @param file
	 * @throws IOException
	 */
	public void learnFile(File file) throws IOException
	{
		readVocab(file); //统计词频
		new Haffman(layerSize).make(wordMap.values());//将所有词全部导入到二叉树当中，构成二叉树

		//构建每个词的路径与编码
		for (Neuron neuron : wordMap.values())
		{
			((WordNeuron)neuron).makeNeurons() ;
		}

		trainModel(file);
	}

	/**
	 * 保存模型
	 */
	public void saveModel(File file) {
		// TODO Auto-generated method stub

		try  {
			DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
			dataOutputStream.writeInt(wordMap.size());
			dataOutputStream.writeInt(layerSize);
			double[] syn0 = null;
			for (Entry<String, Neuron> element : wordMap.entrySet()) {
				dataOutputStream.writeUTF(element.getKey());
				syn0 = ((WordNeuron) element.getValue()).syn0;
				for (double d : syn0) {
					dataOutputStream.writeFloat(((Double) d).floatValue());
				}
			}

			dataOutputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int getLayerSize() {
		return layerSize;
	}

	public void setLayerSize(int layerSize) {
		this.layerSize = layerSize;
	}

	public int getWindow() {
		return window;
	}

	public void setWindow(int window) {
		this.window = window;
	}

	public double getSample() {
		return sample;
	}

	public void setSample(double sample) {
		this.sample = sample;
	}

	public double getAlpha() {
		return alpha;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
		this.startingAlpha = alpha;
	}

	public Boolean getIsCbow() {
		return isCbow;
	}

	public void setIsCbow(Boolean isCbow) {
		this.isCbow = isCbow;
	}

	public static void main(String[] args) throws IOException {

		Learn learn = new Learn();

		if(true)
		{
			long start = System.currentTimeMillis() ;
			learn.learnFile(new File("library/input/xh.txt"));
			System.out.println("use time "+(System.currentTimeMillis()-start));
			learn.saveModel(new File("library/model/javaVector"));
		}
	}
}
