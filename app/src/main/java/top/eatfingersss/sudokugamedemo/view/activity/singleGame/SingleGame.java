package top.eatfingersss.sudokugamedemo.view.activity.singleGame;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import top.eatfingersss.sudokugamedemo.R;
import top.eatfingersss.sudokugamedemo.control.PublicInformation;
import top.eatfingersss.sudokugamedemo.control.Sudoku;
import top.eatfingersss.sudokugamedemo.model.entity.*;
import top.eatfingersss.sudokugamedemo.view.EncapsilationView;
import top.eatfingersss.sudokugamedemo.view.MessageBox;


public class SingleGame extends AppCompatActivity implements EncapsilationView {
    private static int margin[][][] = {
            {
                    {0, 2, 1, 1}, {1, 2, 1, 1}, {1, 2, 2, 1},
                    {2, 2, 1, 1}, {1, 2, 1, 1}, {1, 2, 2, 1},
                    {2, 2, 1, 1}, {1, 2, 1, 1}, {1, 2, 0, 1}
            },
            {
                    {0, 1, 1, 1}, {1, 1, 1, 1}, {1, 1, 2, 1},
                    {2, 1, 1, 1}, {1, 1, 1, 1}, {1, 1, 2, 1},
                    {2, 1, 1, 1}, {1, 1, 1, 1}, {1, 1, 0, 1}
            },
            {
                    {0, 1, 1, 2}, {1, 1, 1, 2}, {1, 1, 2, 2},
                    {2, 1, 1, 2}, {1, 1, 1, 2}, {1, 1, 2, 2},
                    {2, 1, 1, 2}, {1, 1, 1, 2}, {1, 1, 0, 2}
            }
    };

    private static String MATRIX="030800000400000061000000200058000070000020000070000000000705000100000000200060400";
    class SudokuBlock{
        private TextView textView;
        private int txWeight,bcgWeight;
        private Position position;
        private TextView[] textViews;
        private TableRow tableRow;
        private TableRow[] tableRow_1,tableRows_2;
        private Boolean isGuess;

        public SudokuBlock(TableRow tableRow, TextView textView, Position position) {
            this.tableRow = tableRow;
            this.textView = textView;
            this.position = position;
            txWeight= PRE_TX_DEFAULT;
            bcgWeight=PRE_BCG_DEFAULT;
            this.isGuess = false;
        }

        //    final static Map<Integer,BlocksColor> PRE_TO_BLOCKCOLOR
        final static int
                PRE_TX_REPEATED=16,//重复
                PRE_TX_GIVEN=8,//给出
                PRE_TX_UNABLE=4,//禁用
                PRE_TX_SELECT=2,//选中
                PRE_TX_DEFAULT=1;//默认

        final static int
                PRE_BCG_GIVEN=16,
                PRE_BCG_UNABLE=8,
                PRE_BCG_SELECT=4,
                PRE_BCG_DEFAULT=2,
                PRE_BCG_REPEATED=1;

        boolean isContant(int sum,int one){
            sum-=one;
            if(sum>=16 && one != 16)sum-=16;
            if(sum>=8 && one != 8)sum-=8;
            if(sum>=4 && one != 4)sum-=4;
            if(sum>=2 && one != 2)sum-=2;
            if(sum>=1 && one != 1)sum-=1;
            if(sum == 0)return true;
            else return false;
        }

        void addTxPre(int pre){
            if(!isContant(txWeight,pre))
                txWeight+=pre;
        }

        void addBcgPre(int pre){
            if(!isContant(bcgWeight,pre))
                bcgWeight+=pre;
        }

        void minusTxPre(int pre){
            if(isContant(txWeight,pre))
                txWeight-=pre;
        }

        void minusBcgPre(int pre){
            if(isContant(bcgWeight,pre))
                bcgWeight-=pre;
        }

        void addPre(int txPre, int bcgPre){
            addTxPre(txPre);addBcgPre(bcgPre);
            refreshPre();
        }

        public Position getPosition() {
            return position;
        }

        void minusPre(int txPre, int bcgPre){
            minusTxPre(txPre);minusBcgPre(bcgPre);
            refreshPre();
        }

        void setText(String text){
            textView.setText(text);
        }

        //根据坐标位置得出正确的默认块背景色
        private int getRightDefaultColor(Position position){
            int x=position.x,y=position.y;
            if(x<3||x>5)
            {
                if(y<3||y>5) return 1;
                else return 0;
            }
            else{
                if(y<3||y>5) return 0;
                else return 1;
            }
        }

        private BlockColor getCurrentBcgPre(){
            if(bcgWeight >= PRE_BCG_GIVEN)
                return blocksColor.givenBlock;
            else if(bcgWeight >= PRE_BCG_UNABLE)
                return blocksColor.ableBlock;
            else if(bcgWeight >= PRE_BCG_SELECT)
                return blocksColor.selectBlock;
            else if(bcgWeight >= PRE_BCG_DEFAULT)
                return blocksColor.defaultBlock[getRightDefaultColor(position)];
            else if(bcgWeight >= PRE_BCG_REPEATED)
                return blocksColor.repeatedBlock;
            return null;
        }

        private BlockColor getCurrentTxPre(){
            if(txWeight>=PRE_TX_REPEATED)
                return blocksColor.repeatedBlock;
            else if(txWeight>=PRE_TX_GIVEN)
                return blocksColor.givenBlock;
            else if(txWeight>=PRE_TX_UNABLE)
                return blocksColor.ableBlock;
            else if(txWeight>=PRE_TX_SELECT)
                return blocksColor.selectBlock;
            else if(txWeight>=PRE_TX_DEFAULT)
                return blocksColor.defaultBlock[getRightDefaultColor(position)];
            return null;
        }

        public void refreshPre(){
            //tx
            BlockColor currentTxPre = getCurrentTxPre();
            //bg
            BlockColor currentBcgPre = getCurrentBcgPre();

            textView.setTextColor(currentTxPre.getTextColor());
            tableRow.setBackgroundColor(currentBcgPre.getBlockColor());
        }

        //切换模式
        public void toGuess(int num){
            isGuess = true;
            textView.setText(" ");
            tableRow.removeAllViews();
            if(textViews == null)
                textViews = new TextView[9];


            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
            layoutParams.setMargins(0,0,0,0);

            TableRow.LayoutParams layoutParamsMatch = new TableRow.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    1
            );

            TableLayout tableLayout = new TableLayout(SingleGame.this);
            tableLayout.setLayoutParams(layoutParams);
            tableRow.addView(tableLayout);

            for(int i=0;i<3;i++){
                TableRow tableRow_1 = new TableRow(SingleGame.this);
                tableRow_1.setLayoutParams(layoutParams);
                for(int j=0;j<3;j++){
                    TableRow tableRow_2 = new TableRow(SingleGame.this);
                    tableRow_2.setLayoutParams(layoutParamsMatch);
                    int k = i*3+j;
                    {
                        textViews[k] = new TextView(SingleGame.this);
                        textViews[k].setLayoutParams(layoutParams);
                        textViews[k].setTextSize(8);
                        textViews[k].setTextColor(Color.RED);//这个先写死
                        if (k + 1 == num) {
                            textViews[k].setText(num + "");
                        } else {
                            textViews[k].setText(" ");
                        }
                        textViews[k].setGravity(View.TEXT_ALIGNMENT_GRAVITY);

                        tableRow_2.addView(textViews[k]);
                    }

                    tableRow_1.addView(tableRow_2);
                }
                tableLayout.addView(tableRow_1);
            }
        }

        public void stillGuess(int num){
            int last = -1;
            try {
                last = Integer.parseInt(textViews[num-1].getText()+"");
            }catch (Exception e){//没有数字
                textViews[num-1].setText(num+"");
                return;
            }
            textViews[num-1].setText(" ");

        }

        public void toNotGuess(){
            isGuess = false;
            tableRow.removeAllViews();
            tableRow.addView(textView);
        }
    }
    class Debug implements EncapsilationView{
        private TextView txTxWeight,txBcgWeight,editTx;

        Debug(){
            nodeGet();
        }

        @Override
        public void nodeGet() {
            LinearLayout linearLayout = findViewById(R.id.debug);
            linearLayout.setVisibility(View.VISIBLE);
            editTx = findViewById(R.id.editText);
            txTxWeight = findViewById(R.id.textView_txWeight_value);
            txBcgWeight = findViewById(R.id.textView_bcgWeight_value);
        }

        @Override
        public void addListenner() {

        }

        void action(){
            String[] data = (editTx.getText()+"").split("x");
            try {

                int i = Integer.parseInt(data[0]);
                int j = Integer.parseInt(data[1]);

                txTxWeight.setText(sudokuBlocks[i][j].txWeight+"");
                txBcgWeight.setText(sudokuBlocks[i][j].bcgWeight+"");
            }catch (Exception e ){
                return;
            }

        }
    }

    private Sudoku sudoku;
    private Map<TableRow,Position> objectToPosition;
    SudokuBlock[][] sudokuBlocks = new SudokuBlock[9][9];
    TextView[][] textBox=new TextView[9][9];//81个块
//    List<TextView> wrongItem=new ArrayList<TextView>();//有重复的块链表
    SudokuBlock selectedBlock=null;//被选中的块
    TextView textTime;//=(TextView) findViewById(R.id.textView_Time);
    Toolbar toolbar;
    String textTimeCache="";
    BlocksColor blocksColor;//记录颜色
    TimerTask timerTask;//时间记录
    Switch switchGuess;//决定是回答还是猜测
    TableLayout tableLayoutMain;

    Debug debug;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_game);
//        addBackButton();
        //得到矩阵数据
        nodeGet();
        givenPrint();
        answerPrint();
        //计时器事件
        timerTask.execute();
    }


    @Override
    public void addListenner() { }

    //获得color和matrix
    private void dataGet(){
        blockColorGet();
        matrixGet();
    }

    //得到颜色的数据信息
    private void blockColorGet(){
        try {
            BlockColor[] defaultBlock=new BlockColor[2];
            defaultBlock[0]=
                new BlockColor(
                        "#ff23212b","#fff5c59d");
            defaultBlock[1]=
                new BlockColor(
                        "#ff23212b","#ffdeb887");

            BlockColor selectColor=
                new BlockColor(
                        "#ff23212b","#ffffffff");

            BlockColor givenColor=
                    new BlockColor(
                            "#ff23212b",
                            "#ff7d6030"
                    );

            BlockColor repeatedColor=
                    new BlockColor(
                            "#ffff0000",
                            "#ffffffff"
                    );

            BlockColor ableColor=
                    new BlockColor(
                            "#ff575757",
                            "#ff9c783c"
                    );

            blocksColor=new BlocksColor(defaultBlock,
                                        selectColor,
                                        givenColor,
                                        repeatedColor,
                                        ableColor);



        }catch (Exception e) {
            MessageBox.showMessage(this,e.getMessage());
        }
    }

    private void matrixGet() {
        Matrix matrix =
                (Matrix)Matrix.stringToMatrix(SingleGame.MATRIX)
                        .information;
        sudoku=new Sudoku(matrix);//数据来源
    }

    public void nodeGet(){
        if(PublicInformation.developerMode)
            debug = new Debug();
        objectToPosition=new HashMap();
        textTime=(TextView) findViewById(R.id.textView_Time);
        timerTask = new TimerTask(textTime);
        tableLayoutMain = findViewById(R.id.singlegame_tablelayout_main);
        switchGuess = findViewById(R.id.single_game_switch_guess);
//        toolbar = (Toolbar) findViewById(R.id.toolbar);

        dataGet();

        //create

        TableRow.LayoutParams layoutParamsMatch = new TableRow.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        layoutParamsMatch.setMargins(0,0,0,0);

        tableLayoutMain.setStretchAllColumns(true);

        for(int i=0;i<9;i++) {
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(layoutParamsMatch);

            for (int j = 0; j < 9; j++) {
                TableRow tempTableRow = new TableRow(SingleGame.this);

                tempTableRow.setOnClickListener(new TableRowOnClick());

                TableRow.LayoutParams layoutParamsTextView =
                        new TableRow.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                        );
                int[] mag = margin[i%3][j%3];
                layoutParamsTextView.setMargins(mag[0]*2,mag[1]*2,mag[2]*2,mag[3]*2);
                tempTableRow.setLayoutParams(layoutParamsTextView);

                TextView textView = new TextView(SingleGame.this);
                textView.getPaint().setFakeBoldText(true);
                textView.setTextSize(24);
                textView.setGravity(View.TEXT_ALIGNMENT_GRAVITY);
                textView.setLayoutParams(layoutParamsMatch);

                tempTableRow.addView(textView);
                tableRow.addView(tempTableRow);

                Position position = new Position(i, j);
                sudokuBlocks[i][j] = new SudokuBlock(
                        tempTableRow,textView, position
                );

                objectToPosition.put(tempTableRow, position);

                sudokuBlocks[i][j].refreshPre();

            }
            tableLayoutMain.addView(tableRow);
        }

        for(int i=0;i<10;i++) {
            Resources res = this.getResources();
            int txv_id = res.getIdentifier("button_"+i,"id",getPackageName());
            findViewById(txv_id).setOnClickListener(new ButtonOnClick());
        }
    }

    private void dataPrint(int[][] arg,int tx,int bcg){
        for(int i=0;i<9;i++)
            for(int j=0;j<9;j++){
                if(arg[i][j] != 0) {
                    sudokuBlocks[i][j].setText(arg[i][j] + "");
                    if(tx != -1 && bcg != -1)
                        sudokuBlocks[i][j].addPre(tx,bcg);
//                    if(color != null) {
//                        textBox[i][j].setTextColor(color.getTextColor());
//                        textBox[i][j].setBackgroundColor(color.getBlockColor());
//                    }

                }
            }
    }

    //在格子上显示参数
    private void givenPrint(){
        dataPrint(
                sudoku.getGiven(),
                SudokuBlock.PRE_TX_GIVEN,
                SudokuBlock.PRE_BCG_GIVEN
        );
    }

    //在格子上显示回答
    private void answerPrint(){
        int[][] arg = sudoku.getAnswer();
        Position temp = new Position();
        for(int i=0;i<9;i++)
            for(int j=0;j<9;j++){
                if(arg[i][j] != 0) {
                    sudokuBlocks[i][j].setText(arg[i][j] + "");
                    judge(temp.reset(i,j));
                }
            }

    }

    //保存数据
//    private ReturnInformation saveData(){
//        if(!timerTask.isCancelled())
//            timerTask.cancel(true);
//        ReturnInformation res = sudoku.saveData(new View(this));
//        MessageBox.showMessage(
//                this,
//                res.information.toString()
//        );
//        return res;
//    }

    //给出不重复的数字坐标，恢复字体颜色
    private void resume(List<Position> target){
        for(Position one : target){
            sudokuBlocks[one.x][one.y].minusPre(
                    SudokuBlock.PRE_TX_REPEATED, SudokuBlock.PRE_BCG_REPEATED);
//            textBox[one.x][one.y].setTextColor(
//                    blocksColor.defaultBlock[
//                            getRightDefaultColor(one)]
//                            .getTextColor());
        }
    }

    //将repeated中每个对象映射的数字刷红(雾)
    private void warningWrongNumber(List<Position> repeated){
        for(Position one : repeated){
            sudokuBlocks[one.x][one.y].addPre(
                    SudokuBlock.PRE_TX_REPEATED, SudokuBlock.PRE_BCG_REPEATED);
//            setColorNotBackground(one,blocksColor.repeatedBlock);
            //setColor(one,blocksColor.repeatedBlock);
        }
    }

    //判重
    private void judge(Position target){
//        List<Position> unique=sudoku.judgeFromWrongItem();
//        resume(unique);
//        等同与下面一行
        resume(sudoku.judgeFromWrongItem());

        warningWrongNumber(sudoku.judgeABlock(target.x,target.y));//结果会返回到sudoku.repeated中
    }


    class ButtonOnClick implements View.OnClickListener{
        //十个按钮的点击事件,
        @Override
        public void onClick(View v) {
            if(selectedBlock == null) return;
            Button target=(Button)v;
            Position temp=selectedBlock.getPosition();

            if((target.getText()+"").equals(" ")) {//置空
                //这一块写的不是很好
                selectedBlock.setText(target.getText()+"");//id
                //objectToPosition.get(selectedBlock.getId());
                //控制层输入数据
                sudoku.dataInput(temp,target.getText()+"");
                judge(temp);

                if(PublicInformation.developerMode)
                    debug.action();

                return;
            }

            int num = Integer.parseInt(target.getText()+"");


            if(switchGuess.isChecked()) {//是猜测模式
                if (selectedBlock.isGuess){
                    selectedBlock.stillGuess(num);
                }else{//非猜测转猜测
                    selectedBlock.toGuess(num);
                }
            }else{
                if (selectedBlock.isGuess){//猜测转非猜测
                    selectedBlock.toNotGuess();
                }
                //置值
                selectedBlock.setText(target.getText()+"");//id
                //objectToPosition.get(selectedBlock.getId());
                //控制层输入数据
                sudoku.dataInput(temp,target.getText()+"");
                judge(temp);
            }

            Boolean result = sudoku.isComplete();
            if(result)
            {
                android.support.v7.app.AlertDialog.Builder builder =
                        new android.support.v7.app.AlertDialog.Builder(
                                SingleGame.this);

                builder.setTitle("看起来你已经完成了");
                builder.setMessage("");
                builder.setPositiveButton("保存并退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        if(!saveData().isSuccess)return;
                        dialog.cancel();
                        System.exit(1);
                    }
                });
                builder.setNegativeButton("让我再来一回合", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }

            if(PublicInformation.developerMode)
                debug.action();

        }
    }


    class TableRowOnClick implements View.OnClickListener{
        //八十一个文本框的点击事件
        @Override
        public void onClick(View v) {
            TableRow target=(TableRow)v;
            Position p=objectToPosition.get(target);//target的当前位置

            if(sudoku.isGiven(p))//如果是已给出的，直接返回
                return;

            if(selectedBlock!=null){
                if(selectedBlock.position.equals(p)) {
                    //点到了已选中的色块上
                    //色块恢复
                    sudokuBlocks[p.x][p.y].
                            minusPre(SudokuBlock.PRE_TX_SELECT, SudokuBlock.PRE_BCG_SELECT);
                    selectedBlock=null;
                }else {
                    //在已有选中块的同时选中另一块
                    Position position = selectedBlock.getPosition();
                    //原来选中的块的颜色恢复
                    sudokuBlocks[position.x][position.y].
                            minusPre(SudokuBlock.PRE_TX_SELECT, SudokuBlock.PRE_BCG_SELECT);
                    //新选中块色改变
                    sudokuBlocks[p.x][p.y].addPre(
                            SudokuBlock.PRE_TX_SELECT, SudokuBlock.PRE_BCG_SELECT);
                    selectedBlock=sudokuBlocks[p.x][p.y];//更新选中块
                }
            }
            else{
                //没有块被选中
                sudokuBlocks[p.x][p.y].addPre(
                        SudokuBlock.PRE_TX_SELECT, SudokuBlock.PRE_BCG_SELECT);
                selectedBlock=sudokuBlocks[p.x][p.y];//更新选中块
            }
//            setColorOnlyBackground(p,blocksColor.selectBlock);//当前选择的块改变外观


            if(PublicInformation.developerMode)
                debug.action();
        }
    }
    
    
//    class TextOnClick implements View.OnClickListener{
//        //八十一个文本框的点击事件
//        @Override
//        public void onClick(View v) {
//            TextView target=(TextView)v;
//            Position p=objectToPosition.get(target.getId());//target的当前位置
//
//            if(sudoku.isGiven(p))//如果是已给出的，直接返回
//                return;
//
//            if(selectedBlock!=null){
////                String[] temp=idToName.get(selectedText.getId()+"").split(",");
////                int i=Integer.parseInt(temp[0]),j=Integer.parseInt(temp[1]);
//                Position position=selectedBlock.getPosition();
////                System.out.println(i+" "+j);
//                //原来选中的块的颜色恢复
//                sudokuBlocks[position.x][position.y].
//                        minusPre(SudokuBlock.PRE_TX_SELECT,SudokuBlock.PRE_BCG_SELECT);
////                if(sudoku.isRepeated(position)) {
////                    //红色字体
////
////                    setColorNotBackground(position, blocksColor.repeatedBlock);
////                    //背景为default
////                    setColorOnlyBackground(position,
////                            blocksColor.defaultBlock[
////                                    getRightDefaultColor(position)]
////                    );
////                }
////                else{
////                    setColor(position,
////                            blocksColor.defaultBlock[
////                                    getRightDefaultColor(position)]
////                    );
////恢复时只能恢复背景
////                }
//                selectedBlock=null;
//            }
//
//            sudokuBlocks[p.x][p.y].addPre(
//                    SudokuBlock.PRE_TX_SELECT,SudokuBlock.PRE_BCG_SELECT);
//
////            setColorOnlyBackground(p,blocksColor.selectBlock);//当前选择的块改变外观
//            selectedBlock=sudokuBlocks[p.x][p.y];//更新选中块
//
//            if(PublicInformation.developerMode)
//                debug.action();
//        }
//    }

    class TimerTask extends AsyncTask<Integer,String,Integer>{
        private TextView txTime;
        SimpleDateFormat dateFormat= new SimpleDateFormat("mm:ss");
        Date originDate = new Date();
        Date currentDate;
        TimerTask(TextView target){
            this.txTime = target;
        }

        public void showTime(String time){
            txTime.setText(time);
        }

        @Override
        protected Integer doInBackground(Integer... integers) {
//            timer.scheduleAtFixedRate(task,new Date(),1000);
            while(true) {
                try {
                    Thread.sleep(1000);
                    currentDate = new Date();//取现在时间
                    String cache = dateFormat.format(
                            currentDate.getTime()-originDate.getTime()
                    );
                    publishProgress(cache);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        }

        @Override
        protected void onProgressUpdate(String... strings){
            showTime(strings[0]);
        }
    }

}
