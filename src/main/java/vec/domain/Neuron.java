package vec.domain;

/**
 *
 * @reader Rauldini
 *
 */
public abstract class Neuron implements Comparable<Neuron> {
    public int freq;//这个词的频数
    public Neuron parent;//父节点？
    public int code;//这个词的编号

//    @Override
    //按照升序排列
    public int compareTo(Neuron o) {
        // TODO Auto-generated method stub
        if (this.freq > o.freq) {
            return 1;
        } else {
            return -1;
        }
    }

}
