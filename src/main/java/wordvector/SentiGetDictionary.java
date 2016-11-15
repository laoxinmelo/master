package wordvector;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;

public class SentiGetDictionary {

	/**
	 * @Purpose:��ȡ��дʵ��е���дʼ����Ӧ�����ǿ��
	 * @return
	 * @throws Exception
	 */
	public HashMap<String,Integer> getDictionary() 
	{
		HashMap<String,Integer> SentiWord = new HashMap<String,Integer>();

		try{
			File f = new File("E:\\Program Files\\Java\\word2vec\\dic\\ntusd.txt");
			InputStreamReader isr = new InputStreamReader(new FileInputStream(f));
			BufferedReader br = new BufferedReader(isr);
			String temp = br.readLine();
			temp = br.readLine();

			while(temp != null)
			{
				String word = temp.substring(0, temp.indexOf("	")).trim();			
				//String pos = temp.substring(temp.indexOf("	")+1, temp.lastIndexOf("	")).trim();  
				Double strength = Double.valueOf(temp.substring(temp.lastIndexOf("	") + 1).trim()); 

				if(strength==2.0)
					SentiWord.put(word, -1);
				else
					SentiWord.put(word, 1);

				//System.out.println(word + "	" + type);
				temp = br.readLine();
			}
		}catch(Exception e)
		{}
		return SentiWord;
	}
}
