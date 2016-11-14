package com.raul.bupt.lda_pro;

import com.raul.bupt.db.RedisTool;
import com.raul.bupt.db.impl.RedisToolImpl;
import com.raul.bupt.jgibblda.InferencerNew;
import com.raul.bupt.jgibblda.RunInference;

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

    /**
     * 读取所有已完成分词及去停的反馈数据
     * 并将其导入到模型当中，获取对应的主题向量
     * @return
     */
    private static void processAllReply() {

        Set keys = redisTool.getKeys(dbIndex);
        InferencerNew inferencer = RunInference.modelInit(modelPath);

        for(Object key:keys) {
            String reviewId = String.valueOf(key);

            String totalContent = redisTool.getValue(dbIndex,reviewId);
            String reply = totalContent.split(split)[1];

            System.out.println(reviewId + "\t" + RunInference.getDocTheta(inferencer,reply));
        }

    }


    public static void main(String[] args) {

        processAllReply();

    }

}
