package top.eatfingersss.sudokugamedemo.model.entity;

public class ReturnInformation {
    public boolean isSuccess;
    public Object information;
    public String informationType;

    public ReturnInformation(){}
    public ReturnInformation(boolean isSuccess, Object information, String informationType) {
        this.isSuccess = isSuccess;
        this.information = information;
        this.informationType = informationType;
    }

    public ReturnInformation(boolean isSuccess, Object information){
        this.isSuccess=isSuccess;
        this.information=information;
    }

    public void setInformation(Object information,String informationType){
        this.information=information;
        this.informationType=informationType;
    }
}
