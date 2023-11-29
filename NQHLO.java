package my_test;

import funs.TestFunc;
import funs.algFun;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;

//QHLO��ģ�ͣ�ѧϰ������pi�Ը���ѧϰ�����ѧϰ���ƣ�����ѧϰ�����ѧϰ���Ӷ�����random()*theta�������������
public class NQHLO {
    static String algorithmName = "NQHLO-cec17-10D-1";//�㷨����,�����ļ�����
    static final String n_fun = "17";// ���Ժ����ţ����õ���05/14-20
    static final String func_name = "cec" + n_fun;// ���Լ�������cec05��cec14�ȵ�

    static final int popsize = 50;// ��Ⱥ��ģ
    static final int bits = 30;// ��ʾʵ�������Ķ����ƴ�����
    static final int dim = 10;// ���Ե�����ά��
    static final int m = dim * bits;//��Ⱥ�е����ĸ�������ƴ�����
    static final int runtimes = 100;// �㷨���д���
    static final int Gmax = 3000;// ����������
    static int rl = 100;//����ѧϰ����ֵ
    static final int dn = 7;//������תʮ����ʱС����λ��
    static TestFunc func = new TestFunc(func_name);// ʵ����һ�����Լ��������ڼ�����Ӧ��ֵ
    // ���Լ��еĺ����ţ�ע������Լ��ĺ�������
    public static int[] fun = {
            1, 2, 3, 4, 5,
          //           6, 7, 8, 9, 10,
          //     11,12,13,14,15,
          //          16,17,18,19,20,
           //          21,22,23,24,25,
          //             26,27,28,29,30
    };
    static final double[] xmin = func.Xmin();// ���Լ��������Ľ��������Χ��Сֵ���ɲ��Լ�ָ���������������Ҫ���ģ�
    static final double[] xmax = func.Xmax();// ���Լ��������Ľ��������Χ���ֵ���ɲ��Լ�ָ���������������Ҫ���ģ�
    static final double[] bestf = func.bestf();// ���Լ�������������������Ӧ��ֵ���ɲ��Լ�ָ���������������Ҫ���ģ�
    public static void code_run(double para1, double para2, double para3, double para4, double para5){
        //����
        double Itheta = para1;
        double Stheta = para2;
        double n = para3;
        double pi = para4;
        double Ztheta = para5;
        rl = 100;
        //���ڴ�ӡ������
        String str1 = "Itheta";
        String str2 = "Stheta";
        String str3 = "n";
        String str4 = "pi";
        String str5 = "Ztheta";
        String CEC="CEC="+n_fun;
        //���ڴ�ӡ����ֵ
        double param1 = Itheta;
        double param2 = Stheta;
        double param3 = n;
        double param4 = pi;
        double param5 = Ztheta;
        //д�����·��
        String localName = ".\\t_test\\�Ա��㷨\\dim=" + dim + "\\" +algorithmName+"\\";
        //�����ļ���
        File file = new File(localName);
        if(!file.exists())
            file.mkdirs();
        //�������Լ���ÿ������
        for (int jcon = 0; jcon < fun.length; jcon++) {
            int FN = fun[jcon];
            //���ڼ�ʱ
            long start = Calendar.getInstance().getTimeInMillis();
            //��ӡ��Ϣ
            System.out.println(algorithmName + " " + str1 + "=" + (float) param1 + "," + str2 + "=" + (float) param2 + "," + str3 + "=" + (float) param3 + "," + str4 + "=" + (float) param4+ "," + str5 + "=" + (float) param5);
            System.out.println(func_name + ",����" + (fun[jcon]) + ",ά��" + dim);
            //����ÿ�ν��
            double[] results = new double[runtimes];
            for(int rt = 0;rt < runtimes;rt++) {
                //������ʼ��
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
                //�����������ڼ�¼�������������µĴ���
                int[] count = new int[popsize];
                //theta����������
                int k = 0,u=0;

                //��Ⱥ��ʼ��
                Qpopus = algFun.qIniPopE(popsize,m);
                popus = algFun.qTheta2B(Qpopus);
                popusD = algFun.F2I(popus,dn,dim,xmax[FN-1],xmin[FN-1]); // ������תʮ���ƣ��޷���ʽ��QHLOϵ��Ĭ�Ϸ�ʽ��
//                popusD = algFun.Bin2Dec(popus, dim, xmax[FN-1], xmin[FN-1]); // ������תʮ���ƣ�ӳ�䷽ʽ

                Fit = func.Evfit(popusD,FN);

                IKD = algFun.Meq(popus);
                IFit = algFun.Meq(Fit);

                int tempMindx = algFun.Mindx(IFit);
                SKD = algFun.Meq(IKD[tempMindx]);
                SFit = IFit[tempMindx];

                //����
                for(int gt = 0;gt < Gmax;gt++){
                    //���Ӹ��塢���ѧϰ
                    for(int d1 = 0;d1 < popsize;d1++){
                        //ȷ���������Ƿ�ʵʩ
                        if(gt >=1 && ZFit[d1] < Fit[d1]){u=1;}
                        else{u=0;}

                        for(int d2 = 0;d2 < m;d2++){
                            //ȷ����k����
                            if(Qpopus[d1][d2] >= 0 && Qpopus[d1][d2] < 0.5 * Math.PI) {
                                k = 1;
                            }else if (Qpopus[d1][d2] >= 0.5 * Math.PI && Qpopus[d1][d2] < Math.PI) {
                                k = 2;
                            } else if (Qpopus[d1][d2] >= Math.PI && Qpopus[d1][d2] < 1.5 * Math.PI) {
                                k = 3;
                            } else if (Qpopus[d1][d2] >= 1.5 * Math.PI && Qpopus[d1][d2] < 2 * Math.PI) {
                                k = 4;
                            }
                            //���Ӹ��塢���ѧϰ����
                            if(Math.random() < pi)
                                Qpopus[d1][d2] = Qpopus[d1][d2] + ((k % 2) * 2 - 1) * ((Itheta * (2 * IKD[d1][d2] - 1) * Math.random())+(u * Ztheta * (2 * Zpopus[d1][d2] - 1) * Math.random()));
                            else
                                Qpopus[d1][d2] = Qpopus[d1][d2] + ((k % 2) * 2 - 1) * ((Stheta * (2 * SKD[d2] - 1) * Math.random())+(u * Ztheta * (2 * Zpopus[d1][d2] - 1) * Math.random()));
                            //�޷�
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

                    //�������е���Ӧ��
                    Zpopus = algFun.Meq(popus) ;
                    ZFit = algFun.Meq(Fit) ;

                    popus = algFun.qTheta2B(Qpopus);
                    popusD = algFun.F2I(popus,dn,dim,xmax[FN-1],xmin[FN-1]);
//                    popusD = algFun.Bin2Dec(popus, dim, xmax[FN-1], xmin[FN-1]); // ������תʮ���ƣ�ӳ�䷽ʽ
                    Fit = func.Evfit(popusD,FN);

                    //IKD����
                    for (int i = 0; i < popsize; i++) {
                        if (Fit[i] < IFit[i]) {
                            IFit[i] = Fit[i];
                            IKD[i] = algFun.Meq(popus[i]);
                            count[i] = 0; //
                        } else {
                            count[i]++;
                        }
                    }

                    //����ѧϰ
                    for (int i = 0; i < popsize; i++) {
                        if (count[i] >= rl){
                            for (int j = 0; j < m; j++) {
                                Qpopus[i][j] = 2 * Math.PI * Math.random();
                            }
                            popus[i] = algFun.qTheta2B(Qpopus[i]);
                            popusD[i] = algFun.F2I(popus[i],dn,dim,xmax[FN-1],xmin[FN-1]);
//                            popusD[i] = algFun.Bin2Dec(popus[i], dim, xmax[FN-1], xmin[FN-1]); // ������תʮ���ƣ�ӳ�䷽ʽ

                            IFit[i] = func.Evfit(popusD[i],FN);
                            IKD[i] = algFun.Meq(popus[i]);
                            count[i] = 0;
                        }
                    }

                    //SKD����
                    int tempMindx2 = algFun.Mindx(IFit);
                    if(SFit > IFit[tempMindx2]){
                        SFit = IFit[tempMindx2];
                        SKD = algFun.Meq(IKD[tempMindx2]);
                    }

                    if (SFit <= bestf[FN-1])
                        break;
                }
                results[rt] = Math.abs(SFit - bestf[FN-1]);
                System.out.println("���е�"+(rt+1)+"��"+",������Ӧ��:" + results[rt]);
            }
            //��������ֵ�����txt�ļ�
            String flname1 = localName + algorithmName + "_"
//            String flname1 = localName + (float)para5 + "_"
                    +str1+"="+(float)param1+","+str2+"="+(float)param2+","+str3+"="+(float)param3+","+str4+"="+(float)param4+","+str5+"="+(float)param5
                    +"," +CEC+",Fun="+ (fun[jcon])+",Dim="+ dim + ",runTimes="+runtimes
                    +"_�������ֵ.txt";
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

            //����̨��ʾ����ʱ��
            long end = Calendar.getInstance().getTimeInMillis();
            double timeUsed = Double.parseDouble(Long.toString(end - start)) / 1000;
            System.out.println("����ʱ��" + timeUsed + " s\n");
        }
    }

    public static void main(String[] args) {
        code_run(0.7460571428571429,0.8412857142857143,0.30956190476190476,0.8016269841269841,0.2223);
    }
}
