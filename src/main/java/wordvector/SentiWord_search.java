package wordvector;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.BufferedWriter;
import java.io.FileWriter;

import com.ansj.vec.Word2VEC;

import java.util.Set;
import java.util.Iterator;
import java.util.regex.*;
import java.util.HashMap;


/**
 * Step_2
 * �ҳ��������¿��ܴ��ڵ�������дʼ����Ӧ����ֵ
 * @author Administrator
 *
 */
public class SentiWord_search {
	public static void main(String[] args) throws Exception
	{
		Word2VEC vec = new Word2VEC();
		vec.loadJavaModel("library/javaVector11");

		SentiGetDictionary sgd = new SentiGetDictionary();
		HashMap<String,Integer> dictionary = sgd.getDictionary();

		int[] size = {5,10,20,30,40,50};

		for(int i=0;i<size.length;i++)
		{
			vec.setTopNSize(size[i]);
			File f = new File("result/vocabulary.txt");
			InputStreamReader isr = new InputStreamReader(new FileInputStream(f),"utf-8");
			BufferedWriter bw = new BufferedWriter(new FileWriter("value/" +size[i]+ ".txt"));
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
					Integer value = dictionary.get(word);
					if(value>0)
					{
						System.out.println(word + "	" + "�����" + "	" + value);
					}
					else
						System.out.println(word + "	" + "�����" + "	" + value);
				}
				else
				{
					Set wordset = vec.distance(word);
					Iterator iterator = wordset.iterator();
					while(iterator.hasNext())
					{
						String relate = String.valueOf(iterator.next());
						String relWord = relate.substring(0, relate.indexOf("	"));
						Double relValue = Double.valueOf(relate.substring(relate.indexOf("	")+1));

						if(dictionary.containsKey(relWord))
						{
							Integer value = dictionary.get(relWord);

							if(value>0)
							{
								String text = word + "	���������	"+ Double.valueOf(relValue)*value;
								bw.write(text + "\r\n");
								System.err.println(text);
							}
							else
							{
								String text = word + "	���������	"+ Double.valueOf(relValue)*value;
								bw.write(text + "\r\n");
								System.err.println(text);
							}
						}
					}
				}
				
				System.out.println("_________________________________");
				temp = br.readLine();
			}

			br.close();
			bw.flush(); bw.close();
			break;
		}
	}
}
