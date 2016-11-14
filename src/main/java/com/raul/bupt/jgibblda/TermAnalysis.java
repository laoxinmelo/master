package com.raul.bupt.jgibblda;

import java.io.*;
import java.util.*;
import java.lang.String;
import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.standard.*;

public class TermAnalysis{

//	private String englishStopwordsFilePath = "G:/Project/workspace/measures1/data/KDDCUP2005-2012/util/english-stopword-list.txt";
//	private String POS_TrainedTagFilePath = "E:/Project/workspace/SentimentAnalysis/share/wsj-0-18-left3words-distsim.tagger";

	public static Token[] tokensFromAnalysis (Analyzer analyzer, String text) throws IOException {
		TokenStream stream = analyzer.tokenStream("text", new StringReader(text));
		ArrayList tokenList = new ArrayList();
			while (true) 
			{
				Token token = stream.next();
				if (token == null) break;
				tokenList.add(token);
			}
			return (Token[]) tokenList.toArray(new Token[0]); //need to take care of !!!!
		}

//		public String removeStopwords(String s) throws IOException
//		{
//			String rs = "";
//			if(s != null)
//			{
//				File stopwordsFile = new File(this.englishStopwordsFilePath);
//				Analyzer analyzer = new StandardAnalyzer(stopwordsFile);
//				Token [] tokens = this.tokensFromAnalysis(analyzer,s.trim());
//
//				for(int i = 0; i < tokens.length; i++)
//				{
//					//				System.out.println(tokens[i].term());
//					rs = tokens[i].term().trim()+"\t"+rs;
//				}
//			}
//			return rs;
//		}

//		public String stemming(String str)     //Could used to stem a list of words as String
//		{
//			String result = str;
//			result = result.toLowerCase().trim();
//			String [] terms = result.split("\t");  //����str����Tab�ָ����ַ��������Ը����������
//			result = "";
//			for(int i = 0; i < terms.length; i++)
//			{
//				PorterStemmer ps = new PorterStemmer();
//				terms[i] = ps.stem(terms[i]);
//				result = result+terms[i]+"\t";
//			}
//			result = result.trim();
//			return result;
//		}

		public String fromOrgToTab(String str)
		{
			String result = "";
			String [] strs = str.trim().split("\\s+");
			for(int i = 0; i < strs.length; i++)
			{
				if(i == 0)
					result = strs[i];
				else
					result = result+"\t"+strs[i];
			}
			return result;
		}



		//from http://blog.csdn.net/telnetor/article/details/6041323
		public String removePunctuation(String str)
		{
			return str.replaceAll("\\pP|\\pS", " ");
		}

//		public String [] POS_Tagger2(String [] strs) throws IOException, ClassNotFoundException
//		{
//			String [] taggedString = new String[strs.length];
//			for(int i = 0; i < strs.length; i++)
//				strs[i] = "./.";
//			MaxentTagger tagger = new MaxentTagger(this.POS_TrainedTagFilePath);
//			for(int i = 0; i < strs.length; i++)
//			{
//				taggedString[i] = tagger.tagString(strs[i]);
//			}
//			return taggedString;
//		}
//
//		public String POS_Tagger(String str) throws IOException, ClassNotFoundException
//		{
//			String taggedString = "./.";
//			MaxentTagger tagger = new MaxentTagger(this.POS_TrainedTagFilePath);
//			taggedString = tagger.tagString(str);
//			return taggedString;
//		}

		public String pickAdjAndAdvWords(String str)
		{
			String usefulStr = "";
			if(str != null)
			{
				String strs[] = str.trim().split("\\s+");
				for(int i = 0; i < strs.length; i++)
				{
					String [] strs2 = strs[i].split("/");
					if(strs2[1].equals("JJ") || strs2[1].equals("JJR") || strs2[1].equals("JJS")
							|| strs2[1].equals("RB")||strs2[1].equals("RBR")||strs2[1].equals("RBS"))
						usefulStr = strs2[0]+"\t"+usefulStr;
				}
			}
			return usefulStr;
		}

		public static void main(String[] args) throws Exception {

			String str = "author's	st	ad	";
			TermAnalysis ta = new TermAnalysis();
			String [] strs = str.trim().split("\t");
			System.out.println(strs.length);
		}

	}

