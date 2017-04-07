package io.github.zizhuowang.ngdcal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Math.*;

public class MainActivity extends AppCompatActivity {

    static String regex = "百度为您找到相关结果约(.*)个";
    static Pattern pattern = Pattern.compile(regex);
    static String url = "http://www.baidu.com/s?wd=";
    EditText input1,input2,output1,output2;
    Button button;
    TextView source;
    String in1,in2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        input1 = (EditText) findViewById(R.id.input1);
        input2 = (EditText) findViewById(R.id.input2);
        output1 = (EditText) findViewById(R.id.output1);
        output2 = (EditText) findViewById(R.id.output2);
        button = (Button) findViewById(R.id.button);
        source = (TextView) findViewById(R.id.source);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                in1 = input1.getText().toString();
                in2 = input2.getText().toString();
                Double result = NGD(in1,in2);
                source.append(in1+"\n"+in2+"\n"+result.toString());
                output2.setText(result.toString());
                if(result<0.3){
                    output1.setText("非常相似");
                }else if(result<0.8){
                    output1.setText("联系密切");
                }else if(result<1.5){
                    output1.setText("关系不大");
                }else{
                    output1.setText("没有联系");
                }
            }
        });
    }
    /**
     * @param name
     * @return 搜索到的网页数量
     */
    private Double searchResult(String name){
        MainActivity.this.source.append("Here1\n");
        try {
//            URL baiduURL = new URL(url+name);
//            MainActivity.this.source.append("Here2\n");
//            URLConnection connection = baiduURL.openConnection();
//            MainActivity.this.source.append("Here3\n");
//            InputStream stream = connection.getInputStream();
//            MainActivity.this.source.append("Here4\n");
//            InputStreamReader reader = new InputStreamReader(connection.getInputStream());
//            MainActivity.this.source.append("Here5\n");
//            BufferedReader in = new BufferedReader(reader);
//            //逐行筛选匹配正则表达式，并将结果中的逗号删除，之后转化成数字。
//            MainActivity.this.source.append("Here6\n");
            URL baiduURL = new URL(url+name);
            MainActivity.this.source.append("Here2\n");
            HttpURLConnection connection = (HttpURLConnection) baiduURL.openConnection();
            MainActivity.this.source.append("Here3\n");
            InputStream inputStream = connection.getInputStream();
            MainActivity.this.source.append("Here4\n");
            InputStreamReader reader = new InputStreamReader(inputStream);
            MainActivity.this.source.append("Here5\n");
            BufferedReader in = new BufferedReader(reader);
            String html = in.readLine();
            while(html!=null){
                Matcher matcher = pattern.matcher(html);
                while(matcher.find()){
                    System.out.println(name.replaceAll("%20", " ")+": "+matcher.group(1));//格式优化
                    String temp = matcher.group(1);
                    return Double.parseDouble(temp.replaceAll(",", ""));
                }
                html = in.readLine();
            }
        } catch (Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            StringBuffer buffer = stringWriter.getBuffer();
            MainActivity.this.source.append(buffer.toString());
        }
        return 1.0;//有可能没有返回值，所以默认写一个1.0
    }
    /**
     * @param a
     * @param b
     * @return The result of NGD
     */
    public Double NGD(String a,String b){
        //删去空格，以防搜索出错
        a=a.replaceAll(" ", "");
        b=b.replaceAll(" ", "");
        //正则表达式匹配个数

        MainActivity.this.source.append(a+"\n"+b+"\n");
        //用于存三次搜索各自的索引量
        Double numA,numB,numC;
        numA=1.0;numB=1.0;numC=1.0;
        try {
            numA = searchResult(a);
            numB = searchResult(b);
            numC = searchResult(a+"%20"+b);
        } catch (Exception e) {
            e.printStackTrace();
        }
        MainActivity.this.source.append(Calculate(numA,numB,numC).toString()+"\n");
        //返回Calculate函数的计算结果
        return Calculate(numA, numB, numC);
    }
    /**
     * @param numA
     * @param numB
     * @param numC
     * @return NGD公式计算结果
     */
    public Double Calculate(Double numA,Double numB,Double numC){
        Double lnx = log(numA);
        Double lny = log(numB);
        Double lnSum = log(25270000000.0);//由于不知具体数值，这里取谷歌搜素最大索引限制
        Double lnxy = log(numC);
        //NGD公式
        if (lnx>lny) {
            return (lnx-lnxy)/(lnSum-lny);
        }else {
            return (lny-lnxy)/(lnSum-lnx);
        }
    }

}
