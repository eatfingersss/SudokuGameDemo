package top.eatfingersss.sudokugamedemo.model.entity;

import android.graphics.Color;

public class BlockColor{//每种块的颜色
    int textColor;
    int blockColor;
    public BlockColor(String textColor, String blockColor){// throws ColorParseException {
        this.textColor=Color.parseColor(textColor) ;
        this.blockColor=Color.parseColor(blockColor);
    }
    public int getTextColor(){
        return textColor;
    }
    public void setTextColor(String textColor) throws ColorParseException {
        this.textColor = Color.parseColor(textColor);
    }
    public int getBlockColor() {
        return blockColor;
    }
    public void setBlockColor(String blockColor) throws ColorParseException  {
        this.blockColor = Color.parseColor(blockColor);
    }
    class ColorParseException extends Exception//多半用不到的异常类
    {
        public ColorParseException(){}
        public ColorParseException(String message){super(message);}

    }
}