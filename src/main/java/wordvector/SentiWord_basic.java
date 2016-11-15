 package wordvector;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.BufferedWriter;
import java.io.FileWriter;

import com.ansj.vec.Word2VEC;

import java.util.ArrayList;
import java.util.regex.*;
import java.util.HashMap;

/**
 * Step_1
 * ��ȡ���ϴʿ��еĻ�����дʣ���������и�ֵ
 * @author Administrator
 *
 */
public class SentiWord_basic {
	public static void main(String[] args) throws Exception
	{
		Word2VEC vec = new Word2VEC();
		SentiGetDictionary sgd = new SentiGetDictionary();
		vec.loadJavaModel("library/javaVector11");
		
		HashMap<String,Integer> dictionary = sgd.getDictionary();

		int posCount = 0;
		int negCount = 0;
		int count = 0;
		File f = new File("result/vocabulary.txt");
		InputStreamReader isr = new InputStreamReader(new FileInputStream(f),"utf-8");
		BufferedWriter bw = new BufferedWriter(new FileWriter("wordList.txt"));
		BufferedReader br = new BufferedReader(isr);
		String temp = br.readLine();

		while(temp != null)
		{
			String word = temp.substring(0, temp.indexOf(","));

			String regex = "[0-9A-Za-z]";
			Pattern pattern = Pattern.compile(regex);

			if(pattern.matcher(word).find())
				System.err.println(word+" �ô�����ɾ��");
			else if(dictionary.containsKey(word))
			{
				count++;
				Integer value = dictionary.get(word);
				if(value>0)
				{
					bw.write(word + "	" + "�����" + "	" + value + "\r\n");
					System.out.println(word + "	" + "�����" + "	" + value);
					posCount++;
				}
				else
				{
					bw.write(word + "	" + "�����" + "	" + value + "\r\n");
					System.out.println(word + "	" + "�����" + "	" + value);
					negCount++;
				}
			}
			//System.out.println("_________________________________");
			temp = br.readLine();
		}
		br.close();
		bw.flush(); bw.close();
		
		System.err.println(posCount);
		System.err.println(negCount);
		System.err.println(count);
	}
	
	/**
	 * @Purpose:��ȡ������дʴʵ�
	 * @param f
	 * @return
	 * @throws Exception
	 */
	static ArrayList<String> getPositive(File f) throws Exception
	{
		InputStreamReader isr = new InputStreamReader(new FileInputStream(f));
		BufferedReader br = new BufferedReader(isr);
		ArrayList<String> posList = new ArrayList<String>();
		String temp = br.readLine();

		while(temp != null)
		{
			String word = temp.trim();
			posList.add(word);
			temp = br.readLine();
		}
		br.close();

		return posList;
	}

	/**
	 * @Purpose:��ȡ������дʴʵ�
	 * @param f
	 * @return
	 * @throws Exception
	 */
	static ArrayList<String> getNegative(File f) throws Exception
	{
		InputStreamReader isr = new InputStreamReader(new FileInputStream(f));
		BufferedReader br = new BufferedReader(isr);
		ArrayList<String> negList = new ArrayList<String>();
		String temp = br.readLine();

		while(temp != null)
		{
			String word = temp.trim();
			negList.add(word);
			//System.out.println(word);
			temp = br.readLine();
		}
		br.close();

		return negList;
	}
}
