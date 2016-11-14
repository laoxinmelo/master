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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

//import org.kohsuke.args4j.*;

public class RunInference {
	
	public static void main(String args[]){
				
		try 
		{
			String basicDir = "E:\\Program Files\\Java\\JGibbsLDA_New\\5\\";			

			RunInference ri = new RunInference();
			TextIO io = new TextIO();
			
			String modelDir = basicDir;
			InferencerNew inferencer = new InferencerNew();
			inferencer.init(modelDir);
			
			String dataFilePath = basicDir+"test.txt";
			
			String [] newData = io.readFromNewFiles(dataFilePath);

			
			ModelNew newModel = inferencer.inference(newData, modelDir);
			

			/*
			String queryTermFilePath = basicDir+"QueryTermsStemming.txt";
			String queryListFilePath = basicDir+"queries.txt";			
			ArrayList queryTermList = ri.getAllQueryTermsStemming(queryTermFilePath);
			String [] queryList = ri.getQueryList(queryListFilePath);
			
			
			for(int m = 10; m <= 30; m = m+10)
			{			
				for(int n = 0; n < queryList.length; n++)
				{
					String basicDir2 = basicDir + "QueryData/" + queryList[n] + "/CalResult/LDA/";
					String modelDir = basicDir + "QueryData/" + queryList[n] + "/CalResult/LDA/"+m+"/";
//					String dataFilePath = basicDir2+"articesBlank.txt";
					InferencerNew inferencer = new InferencerNew();
					inferencer.init(modelDir);
//					String [] newData = io.readFromNewFiles2(dataFilePath);

					String queryTerms = (String)queryTermList.get(n);
					String [] queries = new String[1];
					queries[0] = queryTerms;
					ModelNew newModel = inferencer.inference(queries, modelDir);
//					ModelNew newModel = inferencer.inference(newData, modelDir);
					System.out.println("Inferring: TopicNum "+m+" : Finishing query "+n+" ...");
*/					
/*			
					for (int i = 0; i < newModel.phi.length; ++i)
					{
					//phi: K * V
						System.out.println("-----------------------\ntopic" + i  + " : ");
						for (int j = 0; j < 10; ++j)
						{
							System.out.println(inferencer.globalDict.id2word.get(j) + "\t" + newModel.phi[i][j]);
						}
					}
					
				}
			}
			*/	
		}
		catch (Exception e){
			System.out.println("Error in main: " + e.getMessage());
			e.printStackTrace();
			return;
		}
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
				queryTermList.add(line);
			}
		} while (line != null);	
				
		Br.close();
		return queryTermList;
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
	
}
