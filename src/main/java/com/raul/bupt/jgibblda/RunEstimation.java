/*
 * Copyright (C) 2007 by
 * 
 * 	Xuan-Hieu Phan
 *	hieuxuan@ecei.tohoku.ac.jp or pxhieu@gmail.com
 * 	Graduate School of Information Sciences
 * 	Tohoku University
 * 
 *  Cam-Tu Nguyen
 *  ncamtu@gmail.com
 *  College of Technology
 *  Vietnam National University, Hanoi
 *
 * JGibbsLDA is a free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * JGibbsLDA is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JGibbsLDA; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */

package com.raul.bupt.jgibblda;

import java.io.*;
import java.util.*;

public class RunEstimation {
	
	private static String basicDir = "./lda/";


	/**
	 * 模型训练
	 * @param topicNum
     */
	public static void modelTraining(EstimatorNew estimator,int topicNum) {
		try{
			estimator.init(basicDir, topicNum);   //模型初始化
			estimator.estimate();
			System.out.println("TopicNum "+topicNum+" completed !");
		}catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public static void main(String args[]){
		
		try {
			ArrayList<Integer> topicNumList = new ArrayList<Integer>();
			for(int i=3;i<=10;i++)
			{
				topicNumList.add(i);
			}
			
			for(int k = 0; k < topicNumList.size(); k++)
			{
				int topicNum = topicNumList.get(k);
				EstimatorNew estimator = new EstimatorNew();
				estimator.init(basicDir, topicNum);   //模型初始化
				estimator.estimate();
				System.out.println("TopicNum "+topicNum+" completed !");
			}			
		}
		catch(Exception e){
			System.out.println("Error in main: " + e.getMessage());
			e.printStackTrace();
			return;
		}


	}
	
	public String [] getQueryList(String queryFileList) throws IOException
	{
		String [] queryList = new String[111];
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
	
	public String [] getArticleIdList(String idListFile) throws IOException
	{
		ArrayList idList = new ArrayList();
		File infile=new File(idListFile);
		BufferedReader Br = new BufferedReader(new FileReader(infile));
		String line = null;	
		int num = 0;
		do {
			line = Br.readLine();
			if (line != null) {
				line = line.trim();
				idList.add(line);
				num++;
			}
		} while (line != null);		
		String [] idArray = new String[idList.size()];
		for(int i = 0; i < idList.size(); i++)
			idArray[i] = (String)idList.get(i);		
		Br.close();
		return idArray;
	}
	
	public LinkedHashMap getWordMapList(String wordMapFile) throws IOException
	{
		LinkedHashMap wordMap = new LinkedHashMap();
		File infile=new File(wordMapFile);
		InputStreamReader read = new InputStreamReader(new FileInputStream(infile),"GBK");
		BufferedReader Br = new BufferedReader(read);
		
		String line1 = null;
		
		do {
			line1 = Br.readLine();
			if (line1 != null) {
//				line1 = line1.trim();
				String[] strs = line1.split("\\s+"); 
				wordMap.put(strs[1], strs[0]);
//				stuList.add(strs);
			}
		} while (line1 != null);	

		Br.close();
		return wordMap;
	}
/*	
	public void getNewTWordFile (String oldTWordFilePath, String newTWordFilePath, LinkedHashMap wordMap)throws IOException
	{

		File infile=new File(oldTWordFilePath);
		InputStreamReader read = new InputStreamReader(new FileInputStream(infile),"GBK");
		BufferedReader Br = new BufferedReader(read);
		File outfile = new File(newTWordFilePath);		
		BufferedWriter writer = new BufferedWriter(new FileWriter(outfile));
		
		String line1 = null;
		
		do {
			line1 = Br.readLine();
			if (line1 != null) {
//				line1 = line1.trim();
				if(line1.startsWith("Topic"))
				
				
				String[] strs = line1.split("\\s+"); 
				wordMap.put(strs[1], strs[0]);
//				stuList.add(strs);
			}
		} while (line1 != null);	
		
		
		Br.close();
		writer.close();
		
	}
	
*/
	
	
	
}
