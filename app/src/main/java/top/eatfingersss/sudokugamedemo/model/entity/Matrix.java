package top.eatfingersss.sudokugamedemo.model.entity;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import top.eatfingersss.sudokugamedemo.control.PublicInformation;

class NowTime{
    public String DEFAULT_PATTERN="yyyy-MM-dd HH";
    public static Date getNowDate() {
        return new Date();
    }
    public static String toString(Date time,String pattern){
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        String dateString = formatter.format(time);
        return dateString;
    }
    public static Date toDate(String time,String pattern) throws ParseException {
        SimpleDateFormat sdf=new SimpleDateFormat(pattern);
        return sdf.parse(time);
    }
}


class Remark{
    String title;//标签名
    int x,y;//坐标（0——8）
}


public class Matrix implements Serializable {//应该实现吗？？？
    public String mat_id;
    private String name,author,describe,score;
    private int difficulty;//挖空数（难度）
    public int[][] context;//81位的数字串，未给出则用0代替(given)
    public int[][] answer;//已填上去的数字
    List<Remark> remarks=new LinkedList<Remark>();//说不定用不着
    public Date createTime,completeTime,lastUseTime;



    public Matrix(String id, String name, String author, String describe, String score,
                  int difficulty, int[][] context, int[][] answer,
                  Date createTime, Date completeTime, Date lastUseTime) {
        this.mat_id=id;
        this.name = name;
        this.author = author;
        this.describe = describe;
        this.score = score;
        this.difficulty = difficulty;
        this.context = context;
        this.answer = answer;
        this.createTime = createTime;
        this.completeTime = completeTime;
        this.lastUseTime = lastUseTime;
    }

    public Matrix(String id, String name, String author, String describe, String score,
                  int difficulty,int[][] context,
                  Date createTime) {
        this.mat_id=id;
        this.name = name;
        this.author = author;
        this.describe = describe;
        this.score = score;
        this.difficulty = difficulty;
        this.context = context;
        this.createTime = createTime;
        this.completeTime = null;
        this.lastUseTimeIsCreateTime();
    }


    public Matrix(String id, String name, String author, String describe, String score,
                  int difficulty,
                  Date createTime,Date completeTime,Date lastUseTime) {
        this.mat_id=id;
        this.name = name;
        this.author = author;
        this.describe = describe;
        this.score = score;
        this.difficulty = difficulty;
        this.createTime = createTime;
        this.completeTime = completeTime;
        this.lastUseTime = lastUseTime;
    }



    //来自http,没有answer
    public Matrix(String id,
                  int[][] context,
                  Object createTime) {
        this.mat_id = id;
        this.context = context;

        try {
            this.createTime = (Date) createTime;
        }catch (Exception e)
        {
            System.out.println("<Exception>createTime为null:"+e.getMessage());
            this.createTime =null;
        }
        this.score = score;
        this.describe=describe;

        this.answer = new int[9][9];
    }

    public void lastUseTimeIsCreateTime(){
        this.lastUseTime=this.createTime;
    }

    //将字符串合成为Matrix
    public static ReturnInformation jsonToMatrix(String args){
        ReturnInformation result;//=new ReturnInformation();
        List<Matrix> data = new LinkedList<Matrix>();
        try {
            JSONArray jsons = new JSONArray(args);

            for(int i=0;i<jsons.length();i++){
                JSONObject json = jsons.getJSONObject(i);

                String id = json.getString("mat_id");
                String name = json.getString("mat_name");
                String author = json.getString("author_name");
                //将context转化为9*9的int表格
                int[][] context = JSONArrayToArray(
                        json.getJSONArray("mat_context")
                );
                int difficulty = json.getInt("mat_difficulty");

                Date uploadTimeResult=null;
                try {
                    String uploadTime = json.getString("mat_upload_time");

                    if(uploadTime!=null)
                        uploadTimeResult =
                                PublicInformation.SDF_OUT_TIME.parse(uploadTime);////这里还是要改

                }catch (Exception e)
                {
                    e.printStackTrace();
                    uploadTimeResult=null;
                }

                String score = json.getString("mat_score");
                String describe = json.getString("mat_describe");
                Matrix res=new Matrix(
                        id,name,author,describe,score,difficulty,
                        context,uploadTimeResult
                );
                data.add(res);
            }

            result=new ReturnInformation(true,data,"Matrix");
        } catch (JSONException e) {
            args+="\n"+e.getMessage();
            e.printStackTrace();
            result= new ReturnInformation(false,args,"String");
        }
        return result;
    }

    //如果出错了就返回报错信息
    public static ReturnInformation stringToMatrix(String args){
        ReturnInformation result;//=new ReturnInformation();
        try {
            int[][] temp = new int[9][9];

            for(int i=0;i<9;i++) {
                for(int j=0;j<9;j++) {
                    temp[i][j]=Integer.parseInt(args.charAt(i*9+j)+"");
                }
            }
//            JSONObject json = new JSONObject(args);
//
//            JSONArray context = json.getJSONArray("matrix");
//            //将context转化为9*9的int表格
//            int[][] temp=new int[9][9];
//            for(int i=0;i<9;i++) {
//                JSONArray tempArray = context.getJSONArray(i);
//                for (int j = 0; j < 9; j++)
//                    temp[i][j]=tempArray.getInt(j);
//            }
//                    //temp[i][j]=Integer.parseInt(context);//.charAt(i*9+j)+"");
//            Date uploadTimeResult=null;
//            try {
//                String uploadTime = json.getString("upload_time");
//
//                if(uploadTime!=null)
//                    uploadTimeResult =
//                            PublicInformation.SDF_OUT_TIME.parse(uploadTime);////这里还是要改
//
//            }catch (Exception e)
//            {
//                e.printStackTrace();
//                uploadTimeResult=null;
//            }
            Matrix res=new Matrix(
                    "undefined",
                    temp,
                    null
            );

            result=new ReturnInformation(true,res,"Matrix");
        } catch (Exception e) {
            args+="\n"+e.getMessage();
            e.printStackTrace();
            result= new ReturnInformation(false,args,"String");
        }
        return result;
    }

    private static int[][] JSONArrayToArray(JSONArray arg){
        int[][] temp=new int[9][9];
        for(int i=0;i<9;i++) {
            JSONArray tempArray = null;
            try {
                tempArray = arg.getJSONArray(i);
                for (int j = 0; j < 9; j++)
                    temp[i][j]=tempArray.getInt(j);
            } catch (JSONException e) {
                return null;
            }
        }
        return temp;
    }

    public Object[] dataOutForDatabase(){
        Object[] res = new Object[]{
                getName(),getAuthor(),
                getDescribe(),getScore(),
                getDifficulty(),contextToString(),
                "",//从外部得到数据的时候是没有答案的，至于残局以后再说
                createTime,completeTime,
                lastUseTime,
        };
        return res;
    }

    public String getId() {
        return mat_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int[][] getContext() {
        return context;
    }

    public void setContext(int[][] context) {
        this.context = context;
    }

    public String contextToString(){
        String res="";
        for (int[] one :context)
            for(int oneOfOne : one)
                res+=oneOfOne;
        return res;
    }

    public String answerToString(){
        String res="";
        if(answer == null)return "";
        for (int[] one :answer)
            for(int oneOfOne : one)
                res+=oneOfOne;
        return res;
    }

    public static int[][] stringToContext(String arg){
        int[][] res = new int[9][9];
        try {
            //参数arg为长度为81的串
            for (int i = 0; i < 9; i++)
                for (int j = 0; j < 9; j++) {
                    res[i][j] = Integer.parseInt(arg.charAt(i * 9 + j) + "");
                }
        }catch (Exception e){
            Log.i("<Log at Matrix>",e.getMessage());
            return new int[9][9];
        }
        return res;
    }

    public Date getCreateTime() {
        if(createTime !=null)
            return createTime;
        return null;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }


    public ReturnInformation dataReset(int difficulty , String data){
        return dataReset(difficulty,data,NowTime.getNowDate());
    }

    public ReturnInformation dataReset(int difficulty ,String data,Date createTime){
        return dataReset(difficulty,data,createTime,null);
    }

    public ReturnInformation dataReset(int difficulty ,String data,Date createTime ,Date completeTime) {
        //定义难度
        this.difficulty=difficulty;
        //定义矩阵
        for(int i=0;i<9;i++)
            for(int j=0;j<9;j++)
                context[i][j]=
                        Integer.parseInt(data.charAt(i*9+j)+"");
        this.createTime=createTime;//取当前时间
        this.completeTime=completeTime;//置空
        return new ReturnInformation(true,"succeed");
    }

    public int[][] getMatrix(){
        return context;
    }

}