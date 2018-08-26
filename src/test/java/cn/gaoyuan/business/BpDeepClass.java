package cn.gaoyuan.business;

import cn.gaoyuan.core.BpDeep;
import cn.gaoyuan.segmenter.WordSegmenter;
import cn.gaoyuan.segmenter.impl.IkSeg;
import cn.gaoyuan.util.Hex2Unicode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class BpDeepClass {
    public static void main(String[] args) {

        //初始化神经网络的基本配置
        //第一个参数是一个整型数组，表示神经网络的层数和每层节点数，比如{3,10,10,10,10,2}表示输入层是3个节点，输出层是2个节点，中间有4层隐含层，每层10个节点
        //第二个参数是学习步长，第三个参数是动量系数
//1、构造假数据
        //呼吸科  对应二维矩阵[0,1]
        String[] huxike = {
                "1.咳嗽（尤其是长期慢性咳嗽）2.支气管哮喘 3.肺部结节 4.感冒发烧与流行性感冒 5.肺炎（包括细菌性肺炎、隐球菌性肺炎、肺真菌病等）6.慢性阻塞性肺疾病 7.肺癌 8.肺结核 9.支气管扩张 10.烟草依赖与戒烟 11.神经官能症引起的咳嗽、胸闷等肺部症状 12.间质性肺炎 13.肺部罕见病 等各种成人呼吸系统疾病。"
                , "中西医整合治疗呼吸疾病（间质性肺炎、肺纤维化、肺癌、肺炎、肺结节、支气管扩张病变、支气管哮喘、慢性难治性咳嗽、慢性阻塞性肺疾病、肺结核等），中医整体辨证论治治疗（比如晚期肿瘤、咽部不适、口苦口干、胸闷胸痛、腹痛腹泻、心情不舒、小儿咳嗽咨询等）。"
                , "间质性肺病（如间质性肺炎、肺纤维化），肺部感染，慢性阻塞性肺病，肺癌，肺血管炎等的诊疗"
        };

        //皮肤科  对应二维矩阵[1,0]
        String[] pifuke = {
                "皮炎、湿疹、白癜风、银屑病和性病等皮肤科疑难病症。"
                , "常见皮肤病及疑难皮肤病的诊断和治疗，尤其擅长自身免疫及过敏性皮肤病，如红斑狼疮、硬皮病、皮肌炎、荨麻疹、湿疹、特应性皮炎、银屑病、痤疮、白癜风、脱发等"
                , "银屑病、痤疮、荨麻疹、带状疱疹、湿疹、扁平疣、脱发、皮肌炎、白癜风、红斑狼疮"
                , "中西医结合治疗白癜风、性病、皮炎湿疹、急慢性荨麻疹、痤疮、自身免疫性疾病等。采用白癜风自体表皮移植术、激光、光化学疗法等治疗白癜风，应用斑贴试验技术检测皮炎湿疹过敏原，以及二氧化碳激光、冷冻疗法、埋疣手术等疗法治疗各种病毒疣、色素痣、皮肤肿瘤、性病"
        };

//2、分词并对每个词语进行编码


        List<double[]> data=new ArrayList<>();
        List<double[]> target=new ArrayList<>();

//        呼吸科的训练数据组装
        assembleData(data,target,huxike,new double[]{1,0});
//        皮肤科训练数据组装
        assembleData(data,target,pifuke,new double[]{0,1});



        //设置样本数据，对应上面的4个二维坐标数据
//        double[][] data = new double[][]{{1, 2,2,1,2}, {2, 2,3,4,5}, {1, 1,3,2,4}, {2, 1,2,1,3}};
        //设置目标数据，对应4个坐标数据的分类
//        double[][] target = new double[][]{{1, 0}, {0, 1}, {0, 1}, {1, 0}};



        BpDeep bp = new BpDeep(new int[]{5,3,5}, 0.15, 0.8);

        //迭代训练5000次
        for (int n = 0; n < 5000; n++) {
            for (int i = 0; i < data.size(); i++) {
                bp.train(data.get(i), target.get(i));
            }
        }

        //根据训练结果来检验样本数据
        for (int j = 0; j < data.size(); j++) {
            double[] result = bp.computeOut(data.get(j));
            System.out.println(Arrays.toString(data.get(j)) + ":" + Arrays.toString(result));
        }

        //根据训练结果来预测一条新数据的分类
        double[] x = new double[]{5, 1,3,2,1};
        double[] result = bp.computeOut(x);
        System.out.println(Arrays.toString(x) + ":" + Arrays.toString(result));
    }


    public static void assembleData(List<double[]> data,List<double[]> target,String[] oridata,double[]oriTarget){
        WordSegmenter seg = new IkSeg();
        for (String s : oridata) {
            s = s.replaceAll("\\d+", "");
            Map map = seg.segMore(s);
            String[] ss = map.get("智能切分").toString().split(" ");
            for (String s1 : ss) {
                double[] data_=Hex2Unicode.string2Unicode(s1);
                data.add(data_);
                target.add(oriTarget);
            }
        }
    }

}
