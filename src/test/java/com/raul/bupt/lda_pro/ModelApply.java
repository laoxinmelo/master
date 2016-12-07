package com.raul.bupt.lda_pro;

import com.raul.bupt.db.RedisTool;
import com.raul.bupt.db.impl.RedisToolImpl;
import com.raul.bupt.jgibblda.InferencerNew;
import com.raul.bupt.jgibblda.RunInference;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

/**
 * Created by Administrator on 2016/11/14.
 */
public class ModelApply {

    //redis缓存工具
    private static final RedisTool redisTool = new RedisToolImpl();
    //商家反馈数据的存放db
    private static final int dbIndex = 3;
    //分隔符
    private static final String split = "\r\n";
    //模型保存路径
    private static final String modelPath = ".\\lda\\model";

    //抱歉类别
    private static final int[] apologyIndex = {3,7};
    //解释类别
    private static final int[] explainIndex = {2,8,14};
    //感激类别
    private static final int[] thankIndex = {0,5,6,10};


    /**
     * 判断某个索引数组中是否包含该索引
     * @param indexArray
     * @param index
     * @return
     */
    private static boolean indexContain(int[] indexArray,int index) {
        for(int tempIndex:indexArray) {
            if(tempIndex == index) {
                return true;
            }
        }
        return false;
    }

    /**
     * 读取所有已完成分词及去停的反馈数据
     * 并将其导入到模型当中，获取对应的主题向量
     * @return
     */
    private static void processAllReply() {

        Set keys = redisTool.getKeys(dbIndex);
        InferencerNew inferencer = RunInference.modelInit(modelPath);

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("lda/predict.txt"));

            int count = 1;
            for (Object key : keys) {
                String keyStr = String.valueOf(key);
                String reviewId = keyStr.substring(keyStr.lastIndexOf("_")+1);

                String[] totalContent = redisTool.getValue(dbIndex, keyStr).split(split);

                String reply = "";
                if(totalContent.length == 1) {
                    reply = totalContent[0];
                }else {
                    reply = totalContent[1];
                }


                double apologyValue = 0.0;
                double explainValue = 0.0;
                double thankValue = 0.0;

                String[] tempVecArray = RunInference.getDocTheta(inferencer,reply).split(" ");
                for(int i=0;i<tempVecArray.length;i++) {
                    double tempValue = Double.valueOf(tempVecArray[i]);

                    //抱歉类别对应权值
                    if(indexContain(apologyIndex,i)) {
                        if(tempValue>apologyValue) {
                            apologyValue = tempValue;
                        }
                    }

                    //解释类别对应权值
                    if(indexContain(explainIndex,i)) {
                        if(tempValue>explainValue) {
                            explainValue = tempValue;
                        }
                    }

                    //感激类别对应权值
                    if(indexContain(thankIndex,i)) {
                        if(tempValue>thankValue) {
                            thankValue = tempValue;
                        }
                    }


                }

                double sum = apologyValue + explainValue + thankValue;

                bw.write( reviewId + "\t" + apologyValue/sum + "\t" + explainValue/sum + "\t" + thankValue/sum  + "\r\n");
                System.out.println( reviewId + "\t" + apologyValue/sum + "\t" + explainValue/sum + "\t" + thankValue/sum );
                System.out.println("___________________________________");
                count += 1;
            }

            bw.flush(); bw.close();
        }catch(Exception e) {
            e.printStackTrace();
        }

    }


    public static void main(String[] args) {

        processAllReply();

    }

}
