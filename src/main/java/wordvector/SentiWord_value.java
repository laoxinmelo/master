package wordvector;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.BufferedWriter;
import java.io.FileWriter;

import java.util.ArrayList;

/**
 * Step_3
 * ��ȡ��ͬ�����¸���������дʵ����ǿ�ȣ��Լ���дʵ�����ͳ��
 * @author Administrator
 *
 */
public class SentiWord_value {
	public static void main(String[] args) throws Exception
	{
		File f = new File("value");
		File[] files = f.listFiles();

		for(File file:files)
		{
			System.out.println(file.getName());
			getWord(file);
		//	break;
		}
	}

	/**
	 * @Purpose:����������дʵ����ǿ��
	 * @param f
	 * @return
	 * @throws Exception
	 */
	static void getWord(File f) throws Exception
	{
		InputStreamReader isr = new InputStreamReader(new FileInputStream(f));
		BufferedReader br = new BufferedReader(isr);
		BufferedWriter bw = new BufferedWriter(new FileWriter("temp\\sentiWord_" + f.getName()));
		String temp = br.readLine();

		ArrayList<String> wordList = new ArrayList<String>(); 
		String wordTemp = temp + "\r\n";


		while(temp != null)
		{
			//System.err.println(temp);
			String word = temp.split("	")[0];

			if(wordTemp.startsWith(word))
				wordTemp += temp + "\r\n";
			else
			{
				wordList.add(wordTemp);
				wordTemp = temp + "\r\n";
			}
			temp = br.readLine();
		}
		wordList.add(wordTemp);
		
		int posNum = 0;
		int negNum = 0;
		int natNum = 0;

		for(int i=0;i<wordList.size();i++)
		{
			temp = wordList.get(i); 
			String word = temp.substring(0, temp.indexOf("	")); 
			String[] array = temp.split("\r\n");
			Double posValue = 0.0;
			Double negValue = 0.0;
			for(int j=0;j<array.length;j++)
			{

				String[] tempArr = array[j].split("	");
				Double value = Double.valueOf(tempArr[tempArr.length-1]);
				if(tempArr[1].equals("���������"))
					negValue += value;
				else
					posValue += value;
			}
			Double result = posValue + negValue;

			if(result>1)
			{
				posNum++;
				String output = word + "	�����	" + result;
				System.out.println(output);
				bw.write(output + "\r\n");
				continue;
			}
			else if(result<-1)
			{
				negNum++;
				String output = word + "	�����	" +result;
				System.out.println(output);
				bw.write(output + "\r\n");
				continue;
			}
			else
				natNum++;
		}
		String basic = getBasicWord(new File("wordList.txt"));
		bw.write(basic);
		
		System.out.println(posNum + "	" + negNum + "	" + natNum);
		bw.flush();  bw.close();
	}
	
	/**
	 * @Purpose:��ȡ������дʵ�
	 * @param f
	 * @return
	 * @throws Exception
	 */
	static String getBasicWord(File f) throws Exception
	{
		InputStreamReader isr = new InputStreamReader(new FileInputStream(f));
		BufferedReader br = new BufferedReader(isr);
		String temp = br.readLine();
		String Output = "";
		
		while(temp != null)
		{
			Output += temp + "\r\n";
			temp = br.readLine();
		}
		br.close();
		return Output;
	}
}
