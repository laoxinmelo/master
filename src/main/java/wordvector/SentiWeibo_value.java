package wordvector;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Step_4
 * �ڲ�ͬ��������д�ѡ�������£��������΢�������ǿ��
 * @author Administrator
 *
 */
public class SentiWeibo_value {
	public static void main(String[] args) throws Exception
	{
		File f = new File("library/xh.txt");
		File basic = new File("temp");
		SentiWeibo_value sv = new SentiWeibo_value();
		
		ArrayList<String> weiboList = sv.getWeibo(f);
		ArrayList<HashMap<String,Double>> mapList = new ArrayList<HashMap<String,Double>>();
		
		BufferedWriter bw = new BufferedWriter(new FileWriter("sentiValue.txt"));
		
		File[] files = basic.listFiles();
		for(File file:files)
		{
			System.out.println(file.getName());
			Thread.sleep(2*1000);
			HashMap<String,Double> sentiMap = sv.getWord(file);
			mapList.add(sentiMap);
		}
		
		for(int i=0;i<weiboList.size();i++)
		{
			String output = "";
			Double[] value = new Double[mapList.size()];
			for(int j=0;j<value.length;j++)
				value[j] = 0.0;
				
			String weibo = weiboList.get(i);
			String[] wordArray = weibo.split(" ");
			
			for(int j=0;j<wordArray.length;j++)
			{
				String word = wordArray[j];
			//	System.out.println(word);
				for(int k=0;k<mapList.size();k++)
				{
					HashMap<String,Double> sentiMap = mapList.get(k);
					if(sentiMap.containsKey(word))
						value[k] += sentiMap.get(word);
				}
			}
			
			for(int j=0;j<value.length;j++)
			{
				output += value[j] + "	";
			}
            System.out.println(output);
			bw.write(output + "\r\n");
		}
		bw.flush(); bw.close();
		
		
	}
	
	/**
	 * @Purpose:��ȡ΢������
	 * @param f
	 * @throws Exception
	 */
	ArrayList<String> getWeibo(File f) throws Exception
	{
		ArrayList<String> weiboList = new ArrayList<String>();
		InputStreamReader isr = new InputStreamReader(new FileInputStream(f),"utf-8");
		BufferedReader br = new BufferedReader(isr);
		String temp = br.readLine();
		
		while(temp != null)
		{
			weiboList.add(temp);
			//System.out.println(temp);
			temp = br.readLine();
		}
		return weiboList;
	}
	
	/**
	 * @Purpose:��ȡ������дʴʵ��Լ����ʵ����ǿ��
	 * @param f
	 * @return
	 * @throws Exception
	 */
	HashMap<String,Double> getWord(File f) throws Exception
	{
		HashMap<String,Double> wordList = new HashMap<String,Double>();
		InputStreamReader isr = new InputStreamReader(new FileInputStream(f));
		BufferedReader br = new BufferedReader(isr);
		String temp = br.readLine();
		
		while(temp != null)
		{
			String[] array = temp.split("	");
			String key = array[0];
			Double value = Double.valueOf(array[2]);
			wordList.put(key, value);
			temp = br.readLine();
		}
		return wordList;
	}
	
	
}
