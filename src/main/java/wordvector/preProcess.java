package wordvector;

import java.io.*;

public class preProcess 
{
	public static void main(String[] args) throws Exception
	{
		File f = new File("originText.txt");
		InputStreamReader isr = new InputStreamReader(new FileInputStream(f));
		BufferedReader br = new BufferedReader(isr);
		BufferedWriter bw = new BufferedWriter(new FileWriter("temp.txt"));
		String temp = br.readLine();
		while(temp != null)
		{
			temp = temp.replaceAll("#[^#]+#", "").replaceAll("@[^:]+:", "").replaceAll("//", " ");
			System.out.println(temp);
			bw.write(temp + "\r\n");
			temp = br.readLine();
		}
		br.close();
		bw.flush(); bw.close();
	}
}
