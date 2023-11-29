package my_test;

import funs.TestFunc;
import funs.algFun;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;

//QHLO新模型，学习过程用pi对个体学习、社会学习控制，个体学习、社会学习算子都采用random()*theta，常规决策算子
public class NQHLO {
    static String algorithmName = "NQHLO-cec17-10D-1";//算法名称,用于文件命名
    static final String n_fun = "17";// 测试函数号，可用的有05/14-20
    static final String func_name = "cec" + n_fun;// 测试集名，如cec05、cec14等等

    static final int popsize = 50;// 种群规模
    static final int bits = 30;// 表示实数变量的二进制串长度
    static final int dim = 10;// 测试的算例维度
    static final int m = dim * bits;//种群中单个的个体二进制串长度
    static final int runtimes = 100;// 算法运行次数
    static final int Gmax = 3000;// 最大迭代次数
    static int rl = 100;//重新学习的阈值
    static final int dn = 7;//二进制转十进制时小数点位数
    static TestFunc func = new TestFunc(func_name);// 实例化一个测试集对象，用于计算适应度值
    // 测试集中的函数号，注意各测试集的函数个数
    public static int[] fun = {
            1, 2, 3, 4, 5,
          //           6, 7, 8, 9, 10,
          //     11,12,13,14,15,
          //          16,17,18,19,20,
           //          21,22,23,24,25,
          //             26,27,28,29,30
    };
    static final double[] xmin = func.Xmin();// 测试集各函数的解的搜索范围最小值（由测试集指定，无特殊情况不要更改）
    static final double[] xmax = func.Xmax();// 测试集各函数的解的搜索范围最大值（由测试集指定，无特殊情况不要更改）
    static final double[] bestf = func.bestf();// 测试集各函数的理论最优适应度值（由测试集指定，无特殊情况不要更改）
    public static void code_run(double para1, double para2, double para3, double para4, double para5){
        //参数
        double Itheta = para1;
        double Stheta = para2;
        double n = para3;
        double pi = para4;
        double Ztheta = para5;
        rl = 100;
        //用于打印参数名
        String str1 = "Itheta";
        String str2 = "Stheta";
        String str3 = "n";
        String str4 = "pi";
        String str5 = "Ztheta";
        String CEC="CEC="+n_fun;
        //用于打印参数值
        double param1 = Itheta;
        double param2 = Stheta;
        double param3 = n;
        double param4 = pi;
        double param5 = Ztheta;
        //写入磁盘路径
        String localName = ".\\t_test\\对比算法\\dim=" + dim + "\\" +algorithmName+"\\";
        //创建文件夹
        File file = new File(localName);
        if(!file.exists())
            file.mkdirs();
        //遍历测试集中每个函数
        for (int jcon = 0; jcon < fun.length; jcon++) {
            int FN = fun[jcon];
            //用于计时
            long start = Calendar.getInstance().getTimeInMillis();
            //打印信息
            System.out.println(algorithmName + " " + str1 + "=" + (float) param1 + "," + str2 + "=" + (float) param2 + "," + str3 + "=" + (float) param3 + "," + str4 + "=" + (float) param4+ "," + str5 + "=" + (float) param5);
            System.out.println(func_name + ",函数" + (fun[jcon]) + ",维度" + dim);
            //保留每次结果
            double[] results = new double[runtimes];
            for(int rt = 0;rt < runtimes;rt++) {
                //变量初始化
                double[][] Qpopus;
                int [][] popus;
                int [][] Zpopus = new int[popsize][m];
                double[] Fit;
                double[] ZFit = new double[popsize] ;
                double[][] popusD;
                int[][] IKD;
                double[] IFit;
                int[] SKD;
                double SFit;
                //计数器，用于记录个体连续不更新的代数
                int[] count = new int[popsize];
                //theta所属的象限
                int k = 0,u=0;

                //种群初始化
                Qpopus = algFun.qIniPopE(popsize,m);
                popus = algFun.qTheta2B(Qpopus);
                popusD = algFun.F2I(popus,dn,dim,xmax[FN-1],xmin[FN-1]); // 二进制转十进制，限幅方式（QHLO系列默认方式）
//                popusD = algFun.Bin2Dec(popus, dim, xmax[FN-1], xmin[FN-1]); // 二进制转十进制，映射方式

                Fit = func.Evfit(popusD,FN);

                IKD = algFun.Meq(popus);
                IFit = algFun.Meq(Fit);

                int tempMindx = algFun.Mindx(IFit);
                SKD = algFun.Meq(IKD[tempMindx]);
                SFit = IFit[tempMindx];

                //迭代
                for(int gt = 0;gt < Gmax;gt++){
                    //量子个体、社会学习
                    for(int d1 = 0;d1 < popsize;d1++){
                        //确定上下文是否实施
                        if(gt >=1 && ZFit[d1] < Fit[d1]){u=1;}
                        else{u=0;}

                        for(int d2 = 0;d2 < m;d2++){
                            //确定第k象限
                            if(Qpopus[d1][d2] >= 0 && Qpopus[d1][d2] < 0.5 * Math.PI) {
                                k = 1;
                            }else if (Qpopus[d1][d2] >= 0.5 * Math.PI && Qpopus[d1][d2] < Math.PI) {
                                k = 2;
                            } else if (Qpopus[d1][d2] >= Math.PI && Qpopus[d1][d2] < 1.5 * Math.PI) {
                                k = 3;
                            } else if (Qpopus[d1][d2] >= 1.5 * Math.PI && Qpopus[d1][d2] < 2 * Math.PI) {
                                k = 4;
                            }
                            //量子个体、社会学习算子
                            if(Math.random() < pi)
                                Qpopus[d1][d2] = Qpopus[d1][d2] + ((k % 2) * 2 - 1) * ((Itheta * (2 * IKD[d1][d2] - 1) * Math.random())+(u * Ztheta * (2 * Zpopus[d1][d2] - 1) * Math.random()));
                            else
                                Qpopus[d1][d2] = Qpopus[d1][d2] + ((k % 2) * 2 - 1) * ((Stheta * (2 * SKD[d2] - 1) * Math.random())+(u * Ztheta * (2 * Zpopus[d1][d2] - 1) * Math.random()));
                            //限幅
                            if(Qpopus[d1][d2] > 0.5 * k * Math.PI){
                                Qpopus[d1][d2] = 0.5 * k * Math.PI + n * (Qpopus[d1][d2] - 0.5 * k * Math.PI);
                            }else if(Qpopus[d1][d2] < 0.5 * (k - 1) * Math.PI){
                                Qpopus[d1][d2] = 0.5 * (k-1) * Math.PI + n * (Qpopus[d1][d2] - 0.5 * (k-1) * Math.PI);
                            }
                            if(Qpopus[d1][d2] >= 2 * Math.PI)
                                Qpopus[d1][d2] = Qpopus[d1][d2] - 2 * Math.PI;
                            else if(Qpopus[d1][d2] < 0){
                                Qpopus[d1][d2] = 2 * Math.PI + Qpopus[d1][d2];
                            }
                        }
                    }

                    //储存现有的适应度
                    Zpopus = algFun.Meq(popus) ;
                    ZFit = algFun.Meq(Fit) ;

                    popus = algFun.qTheta2B(Qpopus);
                    popusD = algFun.F2I(popus,dn,dim,xmax[FN-1],xmin[FN-1]);
//                    popusD = algFun.Bin2Dec(popus, dim, xmax[FN-1], xmin[FN-1]); // 二进制转十进制，映射方式
                    Fit = func.Evfit(popusD,FN);

                    //IKD更新
                    for (int i = 0; i < popsize; i++) {
                        if (Fit[i] < IFit[i]) {
                            IFit[i] = Fit[i];
                            IKD[i] = algFun.Meq(popus[i]);
                            count[i] = 0; //
                        } else {
                            count[i]++;
                        }
                    }

                    //重新学习
                    for (int i = 0; i < popsize; i++) {
                        if (count[i] >= rl){
                            for (int j = 0; j < m; j++) {
                                Qpopus[i][j] = 2 * Math.PI * Math.random();
                            }
                            popus[i] = algFun.qTheta2B(Qpopus[i]);
                            popusD[i] = algFun.F2I(popus[i],dn,dim,xmax[FN-1],xmin[FN-1]);
//                            popusD[i] = algFun.Bin2Dec(popus[i], dim, xmax[FN-1], xmin[FN-1]); // 二进制转十进制，映射方式

                            IFit[i] = func.Evfit(popusD[i],FN);
                            IKD[i] = algFun.Meq(popus[i]);
                            count[i] = 0;
                        }
                    }

                    //SKD更新
                    int tempMindx2 = algFun.Mindx(IFit);
                    if(SFit > IFit[tempMindx2]){
                        SFit = IFit[tempMindx2];
                        SKD = algFun.Meq(IKD[tempMindx2]);
                    }

                    if (SFit <= bestf[FN-1])
                        break;
                }
                results[rt] = Math.abs(SFit - bestf[FN-1]);
                System.out.println("运行第"+(rt+1)+"次"+",最优适应度:" + results[rt]);
            }
            //创建最优值输出的txt文件
            String flname1 = localName + algorithmName + "_"
//            String flname1 = localName + (float)para5 + "_"
                    +str1+"="+(float)param1+","+str2+"="+(float)param2+","+str3+"="+(float)param3+","+str4+"="+(float)param4+","+str5+"="+(float)param5
                    +"," +CEC+",Fun="+ (fun[jcon])+",Dim="+ dim + ",runTimes="+runtimes
                    +"_多次最优值.txt";
            PrintWriter daout1;
            try {
                daout1 = new PrintWriter(new FileWriter(flname1));
                for (int al = 0; al < runtimes; al++) {
                    daout1.println(results[al]);
                }
                daout1.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //控制台显示运行时间
            long end = Calendar.getInstance().getTimeInMillis();
            double timeUsed = Double.parseDouble(Long.toString(end - start)) / 1000;
            System.out.println("运行时间" + timeUsed + " s\n");
        }
    }

    public static void main(String[] args) {
        code_run(0.7460571428571429,0.8412857142857143,0.30956190476190476,0.8016269841269841,0.2223);
    }
}
