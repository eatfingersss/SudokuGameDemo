package top.eatfingersss.sudokugamedemo.view;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class MessageBox {
    public static void showMessage(Context context,String str){
        Toast toast = Toast.makeText(context,str,Toast.LENGTH_SHORT);
        toast.show();
        Log.i("log",str);
    }
}
