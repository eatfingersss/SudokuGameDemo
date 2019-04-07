package top.eatfingersss.sudokugamedemo.control;

import android.view.View;

import java.util.LinkedList;
import java.util.List;

//import top.eatfingersss.sudokugamedemo.model.SQLiteDAO;
import top.eatfingersss.sudokugamedemo.model.entity.Matrix;
import top.eatfingersss.sudokugamedemo.model.entity.Position;
import top.eatfingersss.sudokugamedemo.model.entity.ReturnInformation;

public class Sudoku {
    private boolean complete=false;
    private Matrix matrix;//全部引入
    private int[][] context;//包含context+answer,用来进行逻辑运算
    private LinkedList<Position> repeated = new LinkedList<Position>(){};
    private int num=0;//填入数字的数量

    public Sudoku(Matrix matrix){
        this.matrix=matrix;
        this.context=addContextAndAnswer(matrix);

        for(int[] one :matrix.answer)
            for(int oneOfOne:one)
                if(oneOfOne!= 0)
                    num++;
//        if(matrix.completeTime!=null)complete=true;//若已经完成了则进入'观赏模式'
    }

    //将给定matrix的answer与context合体并返回
    private int[][] addContextAndAnswer(Matrix matrix){
        int[][] result = new int[9][9];
        //clone对于一维数组是深克隆
        for(int i=0;i<9;i++){
            result[i] = matrix.context[i].clone();
        }

        for(int i=0;i<9;i++)
            for(int j=0;j<9;j++)
                if(matrix.answer[i][j]!=0)
                    result[i][j]=matrix.answer[i][j];
        return result;
    }

    public int[][] getGiven(){
        return matrix.context;
    }

    public int[][] getAnswer(){
        return matrix.answer;
    }

    //判断给定点是否为重复
    public boolean isRepeated(Position target){
        for(Position one:repeated){
            if(one.equals(target))
                return true;
        }
        return false;
    }


    //判断是否完成
    public boolean isComplete(){
        if (!repeated.isEmpty())return false;//非空一定没完成
        if(num == matrix.getDifficulty()) return true;
        return false;
    }


    //判断给定点是否为参数点
    public boolean isGiven(Position target){
        for(int i=0;i<9;i++)
            for(int j=0;j<9;j++)
            {
                if(matrix.context[i][j]!=0)//为已给出的数
                    if(target.x==i && target.y==j)
                        return true;
            }
        return false;
    }


    //给定位置塞数据
    public void dataInput(Position target,String value){
        if(value.equals(" ")) {
            if(context[target.x][target.y]!=0)
                num--;
            context[target.x][target.y] = 0;
            matrix.answer[target.x][target.y] = 0;

        }else{
            int val=Integer.parseInt(value);
            if(context[target.x][target.y] == 0)
                num++;
            context[target.x][target.y] = val;
            matrix.answer[target.x][target.y] = val;
        }
    }


    //从重复的列表中找到不重复的并剔除(只是找到),
    //         返回剔除的所有坐标集合unique
    public List<Position> judgeFromWrongItem(){
        String flag="";
        List<Position> unique=new LinkedList<Position>();
        for(Position value : repeated)
        {
            //x,y代表重复列表中某个数的坐标（0——8）
            int x=value.x,y=value.y;
            boolean flag_=false;
            //按列判断
            for(int i=0;i<9;i++) {
                if(i==x)continue;//跳过自身
                if(context[i][y] == context[x][y]){
                    flag_=true;
                }
            }
            //按行判断
            for(int i=0;i<9;i++){
                if(i==y)continue;//跳过自身
                if(context[x][i] == context[x][y]){
                    flag_=true;
                }
            }
            //按宫判断
            for(int i=x/3*3;i<x/3*3+3;i++)
                for(int j=y/3*3;j<y/3*3+3;j++)
                {
                    if(i==x&&j==y)continue;//跳过自身
                    if(context[x][y]==context[i][j]){
                        flag_=true;
                    }
                }
            if(!flag_)//该数字无重复
            {
                //context[i][j].setTextColor(blocksColor.defaultBlock[0].getTextColor());//flag==false
                //System.out.println(i+" "+j+" 恢复");
//                flag+=i;
//                flag+=j;
                unique.add(new Position(x,y));//所有无重复的数字加入unique列表中
            }
        }
        
        for(Position item : unique){
            //手写remove
            //迭代的时候只读
            for(int i=0;i<repeated.size();i++){
                if(repeated.get(i).equals(item)) {
                    repeated.remove(repeated.get(i));
                }
            }
        }
        return unique;
    }


    //重复数字集合：repeated 检查是否存在与给定Position对象相同的对象
    private boolean contains(Position item) {
        for(Position one :this.repeated){
            if(one.x==item.x&&one.y==item.y)return true;
        }
        return false;
    }


//    private void itsRepeated(Position target){
//        this.repeated.add(target);
//    }

//判断给定坐标数字是否重复，并把检测出来的所有重复坐标塞进repeated并返回
    public List<Position> judgeABlock(int x,int y) {
        if(context[x][y]==0)return repeated;
        for(int i=0;i<9;i++) {
            //判断列
            if(context[i][y] == context[x][y]&&i!=x){
                //System.out.println("x="+x+"j="+j);
                if(!contains(new Position(x, y)))repeated.add(new Position(x, y));
                if(!contains(new Position(i, y)))repeated.add(new Position(i, y));
            }
            //判断行
            if(context[x][i] == context[x][y]&&i!=y){
                //System.out.println("i="+i+"x="+x);
                if(!contains(new Position(x, y)))repeated.add(new Position(x, y));
                if(!contains(new Position(x, i)))repeated.add(new Position(x, i));
            }
        }
        //判断宫
        for(int i=x/3*3;i<x/3*3+3;i++)
            for(int j=y/3*3;j<y/3*3+3;j++)
            {
                if((context[i][j] == context[x][y]) && (i!=x||j!=y)){
                    //System.out.println("x="+x+"y="+y);
//                    context[i][j].setTextColor(blocksColor.errorBlock.getTextColor());
//                    context[x][y].setTextColor(blocksColor.errorBlock.getTextColor());
                    if(!contains(new Position(x, y)))repeated.add(new Position(x, y));
                    if(!contains(new Position(i, j)))repeated.add(new Position(i, j));
                }
            }
        return repeated;
    }


    public void start(String[] data){

    }

//    public ReturnInformation saveData(View view){
////        class SaveMatrix extends AsyncTask<View,Integer, ReturnInformation> {
//////
//////            @Override
//////            protected ReturnInformation doInBackground(View... views) {
//////
//////            }
//////        }
//////        SaveMatrix saveMatrix = new SaveMatrix();
//////
//        try {
//            SQLiteDAO.storeMatrixInDatabase(view,matrix);
//        }catch (Exception e)
//        {
//            return new ReturnInformation(false,e.getMessage());
//        }
//        return new ReturnInformation(true,"success");
//    }
}
