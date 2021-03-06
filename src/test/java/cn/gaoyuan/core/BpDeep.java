package cn.gaoyuan.core;

import java.util.Random;

public class BpDeep {
    public double[][] layer;//神经网络各层节点
    public double[][] layerErr;//神经网络各节点误差
    public double[][][] layer_weight;//各层节点权重
    public double[][][] layer_weight_delta;//各层节点权重动量
    public double mobp;//动量系数
    public double rate;//学习系数

    /**
     * 1. 初始化过程：
     * 由于是n层神经网络，我们用二维数组layer记录节点值，第一维为层数，第二维为该层节点位置，数组的值为节点值；
     * 同样，节点误差值layerErr也是相似方式记录。
     * 用三维数组layer_weight记录各节点权重，第一维为层数，第二维为该层节点位置，第三维为下层节点位置,数组的值为某节点到达下层某节点的权重值，初始值为0-1之间的随机数。
     * 为了优化收敛速度，这里采用动量法权值调整，需要记录上一次权值调整量，用三维数组layer_weight_delta来记录，
     * 截距项处理：程序里将截距的值设置为1，这样只需要计算它的权重就可以了
     *
     * @param layernum//代表神经网络是几层
     * @param rate
     * @param mobp
     */
    public BpDeep(int[] layernum, double rate, double mobp) {
        this.mobp = mobp;
        this.rate = rate;
        layer = new double[layernum.length][];
        layerErr = new double[layernum.length][];
        layer_weight = new double[layernum.length][][];
        layer_weight_delta = new double[layernum.length][][];
        Random random = new Random();
        for (int l = 0; l < layernum.length; l++) {
            layer[l] = new double[layernum[l]];
            layerErr[l] = new double[layernum[l]];
            if (l + 1 < layernum.length) {
                layer_weight[l] = new double[layernum[l] + 1][layernum[l + 1]];
                layer_weight_delta[l] = new double[layernum[l] + 1][layernum[l + 1]];
                for (int j = 0; j < layernum[l] + 1; j++)
                    for (int i = 0; i < layernum[l + 1]; i++)
                        layer_weight[l][j][i] = random.nextDouble();//随机初始化权重
            }
        }
    }

    //逐层向前计算输出
    public double[] computeOut(double[] in) {
        //遍历层数
        for (int l = 1; l < layer.length; l++) {
            //遍历每层节点数
            for (int j = 0; j < layer[l].length; j++) {
//                第l-1层
                double z = layer_weight[l - 1][layer[l - 1].length][j];
                for (int i = 0; i < layer[l - 1].length; i++) {
                    layer[l - 1][i] = l == 1 ? in[i] : layer[l - 1][i];
                    z += layer_weight[l - 1][i][j] * layer[l - 1][i];
                }
                layer[l][j] = 1 / (1 + Math.exp(-z));
            }
        }
        return layer[layer.length - 1];
    }

    //逐层反向计算误差并修改权重-->重点
    public void updateWeight(double[] tar) {
        int l = layer.length - 1;
        for (int j = 0; j < layerErr[l].length; j++) {
            //todo 为什么误差用这个公式-不要纠结这个，反正这个能表达出出误差的意义就是了->这个能保证实际值与计算值越接近这个值越小
            layerErr[l][j] = layer[l][j] * (1 - layer[l][j]) * (tar[j] - layer[l][j]);
        }
        while (l-- > 0) {
            for (int j = 0; j < layerErr[l].length; j++) {
                double z = 0.0;
                for (int i = 0; i < layerErr[l + 1].length; i++) {
                    //第一步，求计算误差所需的数据
                    z = z + l > 0 ? layer_weight[l][j][i] * layerErr[l + 1][i] : 0;
                    //第二步，先求动量项再求权重
                    layer_weight_delta[l][j][i] = mobp * layer_weight_delta[l][j][i] + rate * layerErr[l + 1][i] * layer[l][j];//隐含层动量调整
                    layer_weight[l][j][i] += layer_weight_delta[l][j][i];//隐含层权重调整
                    if (j == layerErr[l].length - 1) {
                        layer_weight_delta[l][j + 1][i] = mobp * layer_weight_delta[l][j + 1][i] + rate * layerErr[l + 1][i];//截距动量调整
                        layer_weight[l][j + 1][i] += layer_weight_delta[l][j + 1][i];//截距权重调整
                    }
                }
                //计算误差项
                layerErr[l][j] = z * layer[l][j] * (1 - layer[l][j]);//记录误差
            }
        }
    }

    public void train(double[] in, double[] tar) {
        double[] out = computeOut(in);
        updateWeight(tar);
    }
}