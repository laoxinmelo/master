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


public class RunInference {
	
	public static void main(String args[]){
				
		try 
		{
			String basicDir = "E:\\Program Files\\Java\\JGibbsLDA_New\\5\\";
			TextIO io = new TextIO();
			String modelDir = basicDir;
			InferencerNew inferencer = new InferencerNew();
			inferencer.init(modelDir);
			
			String dataFilePath = basicDir+"test.txt";
			
			String [] newData = io.readFromNewFiles(dataFilePath);

			
			ModelNew newModel = inferencer.inference(newData, modelDir);
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
