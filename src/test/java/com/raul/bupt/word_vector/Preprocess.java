package com.raul.bupt.word_vector;

import com.raul.bupt.db.RedisTool;
import com.raul.bupt.db.impl.RedisToolImpl;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

/**
 * Created by Administrator on 2016/11/16.
 */
public class Preprocess {

    //redis缓存库工具
    private static final RedisTool redisTool = new RedisToolImpl();

    //名词POS
    private static final String nounPos = "NN";

    //词与词之间的分隔符
    private static final String wordSplit = " ";

    //词与词性标注之间的分隔符
    private static final String unitSplit = "#";

    //reviewId之间的分隔符
    private static final String reviewIdSplit = ";";

    //文本之间的分隔符
    private static final String contentSplit = "\r\n";


    /**
     * 将在线评论保存为word2vec模型可以处理的形式
     */
    private static void textSave() throws IOException{

        BufferedWriter writerNoSW = new BufferedWriter(new FileWriter("library/input/noSw.txt"));
        BufferedWriter writerNN = new BufferedWriter(new FileWriter("library/input/nn.txt"));

        Set<String> itemIdSet = redisTool.getKeys(0);
        for(String itemId:itemIdSet) {
            String reviewIdStr = redisTool.getValue(0,itemId);
            String[] reviewIdArray = reviewIdStr.split(reviewIdSplit);

            for(String reviewId:reviewIdArray) {
                for(int dbIndex=1;dbIndex<2;dbIndex++) {
                    String totalContent = redisTool.getValue(dbIndex,reviewId);
                    if(totalContent == null) {
                        continue;
                    }

                    String[] content = totalContent.split(contentSplit);

                    //保存除去停用词后的文本
                    String contentNoSW = content[1];
                    if(!contentNoSW.equals("")) {
                        writerNoSW.write(contentNoSW + contentSplit);
                    }

                    //保留所有名词
                    String contentWithPos = content[2];
                    String[] wordUnitArray = contentWithPos.split(wordSplit);
                    String contentNN = "";
                    for(String wordUnit:wordUnitArray) {
                        String word = wordUnit.substring(0,wordUnit.lastIndexOf(unitSplit));
                        String pos = wordUnit.substring(wordUnit.lastIndexOf(unitSplit)+1);
                        if(!pos.equals(nounPos)) {
                            continue;
                        }

                        contentNN += word + wordSplit;
                    }
                    if(!contentNN.equals("")) {
                        System.out.println(contentNN);
                        writerNN.write(contentNN + contentSplit);
                    }
                }
            }
        }
        writerNN.flush();writerNN.close();
        writerNoSW.flush();writerNoSW.close();
    }


    public static void main(String[] args) {
        try {
            textSave();
        }catch(IOException e) {
            e.printStackTrace();
        }
    }
}
