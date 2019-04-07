package top.eatfingersss.sudokugamedemo.model.entity;

public class Position{
    public int x,y;
    public Position(int x,int y){
        this.x=x;
        this.y=y;
    }

    public Position(){
        this.x=-1;
        this.y=-1;
    }

    public Position reset(int x,int y){
        this.x=x;
        this.y=y;
        return this;
    }

    public Boolean equals(Position position){
        if(this.x == position.x && this.y == position.y){
            return true;
        }
        return false;
    }
}