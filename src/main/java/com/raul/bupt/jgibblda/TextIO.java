package com.raul.bupt.jgibblda;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import java.lang.String;


public class TextIO{
	
	public String readFromOriginalFile(String originalFilePath)throws FileNotFoundException, IOException
	{
		String str = "";
		File infile=new File(originalFilePath);
		BufferedReader Br = new BufferedReader(new FileReader(infile));
		
		String line = null;		
		do {
			line = Br.readLine();
			if (line != null) {
				line = line.trim();
				str = str+" "+line;
			}
		} while (line != null);	
		TermAnalysis ts = new TermAnalysis();
		Br.close();
		return ts.removePunctuation(str);		
	}
	
	public String [] readFromOriginalFiles(String originalFilesPath, int docNum)throws FileNotFoundException, IOException
	{
		String [] strs = new String[docNum];
		for(int i = 0; i < docNum; i++)
			strs[i] = "";
		for(int j = 0; j < docNum; j++)
		{
			strs[j] = this.readFromOriginalFile(originalFilesPath+j+".txt");
		}		
		return strs;
	}
	
	public String [] readFromNewFiles(String newFilePath, int docNum)throws FileNotFoundException, IOException
	{
		String [] strs = new String[docNum];
		for(int i = 0; i < docNum; i++)
			strs[i] = "";
		File infile=new File(newFilePath);
		BufferedReader Br = new BufferedReader(new FileReader(infile));
		String line = null;	
		for(int j = 0; j < docNum; j++)
		{
			line = Br.readLine();
			if(line != null)
			{
				line = line.trim();
				strs[j] = line;
			}
		}
		Br.close();
		return strs;
	}
	
	public String [] readFromNewFiles2(String newFilePath, int docNum)throws FileNotFoundException, IOException
	{
		String [] strs = new String[docNum];
		for(int i = 0; i < docNum; i++)
			strs[i] = "";
		File infile=new File(newFilePath);
		BufferedReader Br = new BufferedReader(new FileReader(infile));
		String line = null;	
		for(int j = 0; j < docNum; j++)
		{
			line = Br.readLine();
			if(line != null)
			{
				line = line.trim();
				if(j==0)
					line = "500";
				strs[j] = line;
			}
		}
		Br.close();
		return strs;
	}
	
	public String [] readFromNewFiles(String newFilePath)throws FileNotFoundException, IOException
	{

		File infile=new File(newFilePath);
		BufferedReader Br = new BufferedReader(new FileReader(infile));
		ArrayList strsList = new ArrayList();
		String line = null;	
		int num = 0;
		do {
			line = Br.readLine();
			if (line != null) {
				line = line.trim();
				strsList.add(line);
				num++;
			}
		} while (line != null);	
			
		String [] strs = new String[num];
		for(int i = 0; i < num; i++)
			strs[i] = (String)strsList.get(i);
		Br.close();
		return strs;
	}
	
	
	public String [] readFromNewFiles2(String newFilePath)throws FileNotFoundException, IOException
	{

		File infile=new File(newFilePath);
		BufferedReader Br = new BufferedReader(new FileReader(infile));
		ArrayList strsList = new ArrayList();
		String line = null;	
		line = Br.readLine();
		int num = 0;
		do {
			line = Br.readLine();
			if (line != null) {
				line = line.trim();
				strsList.add(line);
				num++;
			}
		} while (line != null);	
			
		String [] strs = new String[num];
		for(int i = 0; i < num; i++)
			strs[i] = (String)strsList.get(i);
		Br.close();
		return strs;
	}
	
	
	
	public void writeStringIntoFile(String outputFilePath, String str) throws IOException
	{
		File outfile = new File(outputFilePath);		
		BufferedWriter Bw = new BufferedWriter(new FileWriter(outfile));
		Bw.write(str+"\r\n");
		Bw.close();
	}
	
	public void writeStringsIntoFile(String outputFilePath, String [] strs) throws IOException
	{
		File outfile = new File(outputFilePath);		
		BufferedWriter Bw = new BufferedWriter(new FileWriter(outfile));
		for(int i = 0; i < strs.length; i++)
		{
			Bw.write(strs[i]+"\r\n");
//			System.out.println(i+1);
		}	
		Bw.close();
	}
	
	public void dispalyArray(double [] array,String format)
	{
		DecimalFormat df = new DecimalFormat(format);
		
		for(int i = 0; i < array.length; i++)
		{
			if(i != array.length-1)
				System.out.print(df.format(array[i])+" ");
			else
				System.out.println(df.format(array[i]));
		}
		
	}
	
	public void dispalyArray(int [] array)
	{
		for(int i = 0; i < array.length; i++)
		{
			if(i != array.length-1)
				System.out.print(array[i]+" ");
			else
				System.out.println(array[i]);
		}
	}
	
	public void write(double [] array, String format, String filePath)throws IOException
	{
		File outfile = new File(filePath);		
		BufferedWriter Bw = new BufferedWriter(new FileWriter(outfile));

		DecimalFormat df = new DecimalFormat(format);
		
		for(int i = 0; i < array.length; i++)
		{
			if(i != array.length-1)
				Bw.write(df.format(array[i])+" ");
			else
				Bw.write(df.format(array[i])+"\r\n");
		}		
		Bw.close();
	}
	
	public void write(int  [] array,String filePath)throws IOException
	{
		File outfile = new File(filePath);		
		BufferedWriter Bw = new BufferedWriter(new FileWriter(outfile));
		
		for(int i = 0; i < array.length; i++)
		{
			if(i != array.length-1)
				Bw.write(array[i]+" ");
			else
				Bw.write(array[i]+"\r\n");
		}		
		Bw.close();
	}
	
	
	
	
	//�������ڵ���fromId��С��ednId��num��������ȵ������
	
	public ArrayList getRandomList(int fromId, int endId, int num)
	{
		ArrayList randomList = new ArrayList(num);
		
		if(randomList.size()==0)
		{		
			Random r = new Random();
			int random = r.nextInt(endId-fromId)+fromId;
			randomList.add(random);
			for(int i = 1; i < num; i++)
			{
				boolean flag = true;
				random = r.nextInt(endId-fromId)+fromId;
				for(int k = 0 ; k < i; k++)
				{
					if(random == ((Integer)randomList.get(k)).intValue())
					{
						flag = false;
						i--;
						break;
					}

				}
				if(flag == true)
				{
					randomList.add(random);
				}
			}
			
		}			
		else
		{
			Random r = new Random();
			for(int i = 0; i < num; i++)
			{
				boolean flag = true;
				int random = r.nextInt(endId-fromId)+fromId;
				for(int k = 0 ; k < i; k++)
				{				
					if(random == ((Integer)randomList.get(k)).intValue())
					{
						i--;
						flag = false;
						break;
					}
				}
				if(flag == true)
					randomList.set(i,random);
			}
		}
		return randomList;
		
	}
	
	public ArrayList getNonemptyRandomList(int fromId, int endId, int num, String [] docs)
	{
		ArrayList randomList = new ArrayList(num);
		
		if(randomList.size()==0)
		{		
			Random r = new Random();
			int random = r.nextInt(endId-fromId)+fromId;
			while(docs[random].equals(""))
			{
				random = r.nextInt(endId-fromId)+fromId;
			}		
			randomList.add(random);
			for(int i = 1; i < num; i++)
			{
				boolean flag = true;
				random = r.nextInt(endId-fromId)+fromId;
				while(docs[random].equals(""))
				{
					random = r.nextInt(endId-fromId)+fromId;
				}	
				for(int k = 0 ; k < i; k++)
				{
					if(random == ((Integer)randomList.get(k)).intValue())
					{
						flag = false;
						i--;
						break;
					}

				}
				if(flag == true)
				{
					randomList.add(random);
				}
			}
			
		}			
		else
		{
			Random r = new Random();
			for(int i = 0; i < num; i++)
			{
				boolean flag = true;
				int random = r.nextInt(endId-fromId)+fromId;
				while(docs[random].equals(""))
				{
					random = r.nextInt(endId-fromId)+fromId;
				}	
				for(int k = 0 ; k < i; k++)
				{				
					if(random == ((Integer)randomList.get(k)).intValue())
					{
						i--;
						flag = false;
						break;
					}
				}
				if(flag == true)
					randomList.set(i,random);
			}
		}
		return randomList;		
	}
	
		
	
	public int [] transferArrayListToIntArray(ArrayList ids1)
	{
		int [] ids = new int[ids1.size()];
		for(int i = 0; i < ids1.size(); i++)
		{
			ids[i] = ((Integer)ids1.get(i)).intValue();
		}
		return ids;
	}
	
	public void getRandomSubset(String orgFilePath, String outputFilePath, int positive, int negative, int docNum) throws IOException
	{
		File infile=new File(orgFilePath);
		BufferedReader Br = new BufferedReader(new FileReader(infile));
		File outfile = new File(outputFilePath);		
		BufferedWriter Bw = new BufferedWriter(new FileWriter(outfile));
//		File outfile2 = new File(outputFilePath2);		
//		BufferedWriter Bw2 = new BufferedWriter(new FileWriter(outfile2));
		
		String [] docContents = this.readFromNewFiles(orgFilePath, docNum);
		String [] docResults = new String[positive+negative];
		int [] negativeIds = this.transferArrayListToIntArray(this.getRandomList(0, docNum/2, negative));
		int [] positiveIds = this.transferArrayListToIntArray(this.getRandomList(docNum/2, docNum, positive));
		for(int i = 0; i < negative; i++)
		{
			docResults[i] = docContents[negativeIds[i]];
			Bw.write(docResults[i]+"\r\n");
//			Bw2.write(negativeIds[i]+"\r\n");
		}
		for(int j = negative; j < negative+positive; j++)
		{
			docResults[j] = docContents[positiveIds[j-negative]];
			Bw.write(docResults[j]+"\r\n");
		}		
		Br.close();
		Bw.close();
//		Bw2.close();
	}
	
	public void writeSelectedStringsToFile(String outputFilePath, String [] docContents, int [] selectedIds)throws IOException
	{
		File outfile = new File(outputFilePath);		
		BufferedWriter Bw = new BufferedWriter(new FileWriter(outfile));
		for(int i = 0; i < selectedIds.length; i++)
		{
			String doc = docContents[selectedIds[i]];
			Bw.write(doc+"\r\n");
		}		
		Bw.close();
	}
	
	public void readOriginalReviews(String originalFilePath, String outputFilePath)throws IOException
	{
		File infile = new File(originalFilePath);
		BufferedReader Br = new BufferedReader(new FileReader(infile));
		File outfile = new File(outputFilePath);		
		BufferedWriter Bw = new BufferedWriter(new FileWriter(outfile));
		
		String line = null;	
		ArrayList reviews = new ArrayList();
		int num1 = 0;
		int num2 = 0;
		String review = " ";
		
		do{
			line = Br.readLine();
			if(line != null)
			{
				if(num1 == 0)
				{
					review = line;
					num1++;
				}
				else if(!line.trim().equals("<review>"))
				{
					review = review+" "+line;
					num1++;
				}
				else
				{
					int index1 = review.indexOf("<review_text>");
					int index2 = review.indexOf("</review_text>");
					review = review.substring(index1+13, index2).trim();
					reviews.add(review);
					num2++;
					System.out.println("Review "+num2);
					review = line;
					num1++;
				}
			}
		}while(line != null);
		System.out.println(num1+" : "+num2);
		for(int i = 0; i < reviews.size(); i++)
			Bw.write((String)reviews.get(i)+"\r\n");
		
		Br.close();
		Bw.close();
	}
	
	//����database 7-10 ��ȡpositive reviews (rate = 5)��
	
	public ArrayList getPositiveReviews(String docFilePath)throws IOException
	{
		ArrayList strArray = new ArrayList();
		
		File infile = new File(docFilePath);
		BufferedReader Br = new BufferedReader(new FileReader(infile));
		
		String line = null;	
//		int num = 0;
		do {
			line = Br.readLine();
			if (line != null) {
				line = line.trim();
				int index = line.indexOf("\t");
				line = line.substring(index+1).trim();
				int rate = (new Integer(line.substring(line.length()-1))).intValue();
				if(rate == 5 || rate == 4)     //used for dataset 9 & 10
					strArray.add(line.substring(0,line.length()-1).trim());	
/*				
				if(rate == 5 || rate == 4)     //used for dataset 7 & 8
				{
					int index1 = line.indexOf("_PROS_");
					strArray.add(line.substring(0,index1).trim());	
				}
*/				
//				num++;
			}
		} while (line != null);			
		Br.close();
		
		return strArray;
	}
	
	//����database 7-10 ��ȡnegative reviews (rate = 1)����
	
	public ArrayList getNegativeReviews(String docFilePath)throws IOException
	{
		ArrayList strArray = new ArrayList();
		
		File infile = new File(docFilePath);
		BufferedReader Br = new BufferedReader(new FileReader(infile));
		
		String line = null;	
//		int num = 0;
		do {
			line = Br.readLine();
			if (line != null) {
				line = line.trim();
				int index = line.indexOf("\t");
				line = line.substring(index+1).trim();
				int rate = (new Integer(line.substring(line.length()-1))).intValue();
				if(rate == 1 || rate == 2)         //used for dataset 9 & 10
					strArray.add(line.substring(0,line.length()-1).trim());	
				
/*				
				if(rate == 1 || rate == 2)         //used for dataset 7 & 8
				{
					int index1 = line.indexOf("_PROS_");
					strArray.add(line.substring(0,index1).trim());	
				}
*/				
//				num++;
			}
		} while (line != null);			
		Br.close();
		
		return strArray;
	}
	
	
	public String [] getQueryList(String queryFileList) throws IOException
	{
		String [] queryList = new String[99];
		File infile=new File(queryFileList);
		BufferedReader Br = new BufferedReader(new FileReader(infile));
		String line = null;	
		int num = 0;
		do {
			line = Br.readLine();
			if (line != null) {
				line = line.trim();
				queryList[num] = line;
				num++;
			}
		} while (line != null);
		Br.close();
		return queryList;
	}
	
	public ArrayList getAllQueryTermsStemming(String inputFilePath)throws IOException
	{
		ArrayList queryTermList = new ArrayList();
		File infile=new File(inputFilePath);
		BufferedReader Br = new BufferedReader(new FileReader(infile));
		String line = null;		
		do {
			line = Br.readLine();
			if (line != null) {
				line = line.trim();
				String [] queryTerms = line.split("\\s+");
				queryTermList.add(queryTerms);
			}
		} while (line != null);	
				
		Br.close();
		return queryTermList;
	}
	/*
	
	public void writeListResultIntoFile(ArrayList [] list, ArrayList [] simiTable, String outputFilePath)throws IOException
	{
		File outfile = new File(outputFilePath);		
		BufferedWriter Bw = new BufferedWriter(new FileWriter(outfile));
		Algorithm alg = new Algorithm();
		DecimalFormat df = new DecimalFormat("0.000000");
		
		for(int i = 0; i < list.length; i++)
		{
			double contentCov = alg.calContentCoverage(list[i], simiTable[i]);
			double structureCov = alg.calStructureCoverage(list[i], simiTable[i]);
			double totalCov = contentCov*structureCov;
			double redundancy = alg.calInformationRedundancy(list[i], simiTable[i]);
			
			Bw.write(df.format(contentCov)+"\t"+df.format(structureCov)+"\t"+df.format(totalCov)+"\t"+df.format(redundancy)+"\r\n");
		}		
		Bw.close();
	}
	
	public void writeListIntoFile(ArrayList list, ArrayList simiTable, BufferedWriter Bw)throws IOException
	{
		Algorithm alg = new Algorithm();
		DecimalFormat df = new DecimalFormat("0.000000");
		
		double contentCov = alg.calContentCoverage(list, simiTable);
		double structureCov = alg.calStructureCoverage(list, simiTable);
		double totalCov = contentCov*structureCov;
		double redundancy = alg.calInformationRedundancy(list, simiTable);
		for(int i = 0; i < list.size(); i++)
			Bw.write((int)list.get(i)+"\t");
		Bw.write(df.format(contentCov)+"\t"+df.format(structureCov)+"\t"+df.format(totalCov)+"\t"+df.format(redundancy)+"\r\n");
	}
	
	*/
	public ArrayList [] readResultList(String resultFilePath, int k, int queryNum)throws IOException
	{
		ArrayList [] resultList = new ArrayList[queryNum];
		for(int i = 0; i < queryNum; i++)
			resultList[i] = new ArrayList();
		File infile=new File(resultFilePath);
		BufferedReader Br = new BufferedReader(new FileReader(infile));
		String line = null;	
		int num = 0;
		do {
			line = Br.readLine();
			if (line != null) {
				line = line.trim();
				String [] strs = line.split("\t");
				for(int i = 0; i < k; i++)
				{
					int id = (new Integer(strs[i])).intValue();
					resultList[num].add(id);
				}
				num++;
			}
		} while (line != null);			
		Br.close();
		return resultList;
	}
	
	
	public void changeTWordFile(String twordFilePath, String newTWordFilePath)throws IOException
	{
		
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(twordFilePath), "utf-8"));
			
			File outfile = new File(newTWordFilePath);		
			BufferedWriter writer = new BufferedWriter(new FileWriter(outfile));
		String line = null;	
//		int num = 0;
		do {
			line = reader.readLine();
			if (line != null) 
			{
				line = line.trim();
				if(line.startsWith("Topic"))
				{
					writer.write("\r\n"+line+"\r\n");
				}
				else
				{
					String [] strs = line.split("\\s+");
					if(strs.length == 2)
						writer.write(strs[0]+" ");
				}
			}
		} while (line != null);			
		reader.close();
		writer.close();
	}
	
	public void transferDateToQuarter(String dateFilePath, String monthToQuarterFilePath, String quarterFilePath)throws IOException
	{
		
			BufferedReader reader1 = new BufferedReader(new InputStreamReader(
					new FileInputStream(dateFilePath), "GBK"));
			BufferedReader reader2 = new BufferedReader(new InputStreamReader(
					new FileInputStream(monthToQuarterFilePath), "GBK"));
			
			File outfile = new File(quarterFilePath);		
			BufferedWriter writer = new BufferedWriter(new FileWriter(outfile));
			
			String [] months = new String[84];
			String [] quarterId = new String[84];
			String line2 = null;
			int count = 0;
			do {
				line2 = reader2.readLine();
				if(line2 != null)
				{
					String [] strs = line2.trim().split(",");
					months[count] = strs[0];
					quarterId[count] = strs[1];
					count++;
					System.out.println(count);
				}
			} while(line2 != null);	
			
				
			String line1 = null;

		do {
			line1 = reader1.readLine();
			if (line1 != null) 
			{
				line1 = line1.trim();
				for(int i = 0; i < months.length; i++)
				{
					if(line1.indexOf(months[i]) != -1)
					{
						writer.write(quarterId[i]+"\r\n");
						break;
					}
				}
			}
		} while (line1 != null);			
		reader1.close();
		reader2.close();
		writer.close();
	}
	
	public void calQuarterProbs(String quarterFilePath, String docToTopicProbFilePath, String quarterProbsFilePath, int [] topicIds, int quarterNum)throws IOException
	{
		
			BufferedReader reader1 = new BufferedReader(new InputStreamReader(
					new FileInputStream(quarterFilePath), "GBK"));
			BufferedReader reader2 = new BufferedReader(new InputStreamReader(
					new FileInputStream(docToTopicProbFilePath), "GBK"));
			
			File outfile = new File(quarterProbsFilePath);		
			BufferedWriter writer = new BufferedWriter(new FileWriter(outfile));
			
			DecimalFormat df = new DecimalFormat("0.000000");
			
			double [] [] quarterProbs = new double[quarterNum][topicIds.length];
			for(int i = 0; i < quarterNum; i++)
			{
				quarterProbs[i] = new double[topicIds.length];
				for(int j = 0; j < topicIds.length; j++)
					quarterProbs[i][j] = 0d;
			}			
				
			String line1 = null;
			String line2 = null;

		do {
			line1 = reader1.readLine();
			line2 = reader2.readLine();
			if (line1 != null && line2 != null) 
			{
				line1 = line1.trim();
				line2 = line2.trim();
				String [] strs = line2.split("\\s+");
				int quarter = (new Integer(line1)).intValue()-1;
				for(int i = 0; i < topicIds.length; i++)
				{
					double prob = (new Double(strs[topicIds[i]])).doubleValue();
					quarterProbs[quarter][i] += prob;
				}				
			}
		} while (line1 != null && line2 != null);	
		
		for(int i = 0; i < quarterNum; i++)
		{
			for(int j = 0; j < topicIds.length; j++)
				writer.write(df.format(quarterProbs[i][j])+"\t");
			writer.write("\r\n");
		}	
		
		reader1.close();
		reader2.close();
		writer.close();
	}
	
	
	
	public void ReadSelectedColumn(String docToTopicProbFilePath, int topicId, String resultFilePath)throws IOException
	{
			BufferedReader reader2 = new BufferedReader(new InputStreamReader(
					new FileInputStream(docToTopicProbFilePath), "GBK"));
			
			File outfile = new File(resultFilePath);		
			BufferedWriter writer = new BufferedWriter(new FileWriter(outfile));
			
			DecimalFormat df = new DecimalFormat("0.000000");

			String line2 = null;

		do {
			line2 = reader2.readLine();
			if (line2 != null) 
			{
				line2 = line2.trim();
				String [] strs = line2.split("\\s+");
				String str = strs[topicId];
				writer.write(str+"\r\n");
			}
		} while (line2 != null);	
			
		
		reader2.close();
		writer.close();
	}
	
	
	
	
	public static void main(String[] args) throws Exception 
	{	

//		String basicDir = "G:/Project/workspace/measures1/data/BBS/LDA/";
		String basicDir = "G:/Project/workspace/measures1/data/gov2013/LDA/";
//		String basicDir = "G:/Project/workspace/measures1/data/MISQ/CalResult/LDA/";
//		String basicDir = "G:/Project/workspace/measures1/data/BMW/LDA/";
		TextIO tio = new TextIO();
		
		
//		int [] topicIds = {80,128,199,44,17};
		int [] topicIds = {161};
		int quarterNum = 27;
		
		for(int i = 8; i <=8; i++)
		{
			if(i==1 || i==2 || i==8)
			{
			String twordFilePath = basicDir+i*25+"/model-final.twords";
			String newTWordFilePath = basicDir+i*25+"/"+i*25+"-topic-words.txt";
			
			
			String docToTopicProbFilePath = basicDir+i*25+"/model-final.theta";
			String quarterProbsFilePath = basicDir+i*25+"/quarterProbs-1.txt";
			String dateFilePath = basicDir+i*25+"/date.txt";
			String monthToQuarterFilePath = basicDir+i*25+"/quarter_month.csv";
			String quarterFilePath = basicDir+i*25+"/quarter.txt";
			String resultFilePath = basicDir+i*25+"/161-prob.txt";
//			tio.ReadSelectedColumn(docToTopicProbFilePath, 161, resultFilePath);
//			tio.transferDateToQuarter(dateFilePath, monthToQuarterFilePath, quarterFilePath);
//			tio.calQuarterProbs(quarterFilePath, docToTopicProbFilePath, quarterProbsFilePath, topicIds, quarterNum);
			tio.changeTWordFile(twordFilePath, newTWordFilePath);
			}
		}
			
			
	}
	
	
	
/*	
	public static void main(String[] args) throws Exception 
	{
		
		String basicDir = "G:/Project/workspace/measures1/data/KDDCUP2005-2012/";
		String queryListPath = basicDir+"queries.txt";
		TextIO io = new TextIO();
		String [] queryList = io.getQueryList(queryListPath);
		DocModeling dm = new DocModeling();
		
		for(int m = 10; m <=30; m=m+10)
		{
			String inFile = basicDir+"Results/Clustering/Clustering-TF_IDF-"+m+".txt";
			String outFile = basicDir+"Results/Clustering/Clustering-TF_IDF-"+m+"-new.txt";
			
			File outfile = new File(outFile);		
			BufferedWriter Bw = new BufferedWriter(new FileWriter(outfile));
			
			
			ArrayList [] resultList = io.readResultList(inFile, m, queryList.length);
			for(int i = 0; i < queryList.length; i++)
			{
				String simiFilePath = basicDir+"QueryData/"+queryList[i]+"/CalResult/simi/simi-TF_IDF.txt";
				ArrayList simiTable = dm.readSimilarities(simiFilePath);
				io.writeListIntoFile(resultList[i], simiTable, Bw);
				System.out.println(m+" : Query "+i);
			}
			Bw.close();
				
		}
		

		
	}
*/	
}