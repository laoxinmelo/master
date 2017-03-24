package vec.util;

import java.util.Collection;
import java.util.TreeSet;

import vec.domain.HiddenNeuron;
import vec.domain.Neuron;

/**
 * 构建Haffman编码树   @author ansj
 *
 */
public class Haffman {
    private int layerSize;

    //返回霍夫曼树的层级数
    public Haffman(int layerSize) {
        this.layerSize = layerSize;
    }

    private TreeSet<Neuron> set = new TreeSet<Neuron>();

    /**
     * 将所有的词都加入到二叉树结构当中，形成二叉树
     * @param neurons
     */
    public void make(Collection<Neuron> neurons) {
        set.addAll(neurons);
        while (set.size() > 1) {
            merger();
        }
    }


    private void merger() {
        // TODO Auto-generated method stub
        HiddenNeuron hn = new HiddenNeuron(layerSize);
        Neuron min1 = set.pollFirst();//频数大的code为0
        Neuron min2 = set.pollFirst();//频数小的code为1
        hn.freq = min1.freq + min2.freq;
        min1.parent = hn;
        min2.parent = hn;
        min1.code = 0;
        min2.code = 1;
        set.add(hn);
    }

}
