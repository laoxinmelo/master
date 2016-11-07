package com.raul.bupt.segment.proxy;

import kevin.zhang.NLPIR;

import java.io.UnsupportedEncodingException;

/**
 * Created by Administrator on 2016/11/6.
 */
public class NLPIRProxy {

    private NLPIR nlpir = new NLPIR();

    public NLPIRProxy() {
        try {
            // 分词工具初始化
            if (!NLPIR.NLPIR_Init("./".getBytes("utf-8"), 1)) {
                System.out.println("NLPIR初始化失败...");
            }
        }catch(UnsupportedEncodingException e) {
                System.err.println("NLPIR Encoding Error...");
        }
    }

    /**
     * 获取一个完成了初始化的NLPIR对象实例
     * @return
     */
    public static NLPIR getInstance() {
        NLPIRProxy nlpirProxy = new NLPIRProxy();
        return nlpirProxy.nlpir;
    }

}
