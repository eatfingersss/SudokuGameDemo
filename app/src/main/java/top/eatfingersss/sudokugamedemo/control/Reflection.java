package top.eatfingersss.sudokugamedemo.control;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Reflection {
    public String name;
    private Method method;
    private Class clazz;
    Reflection(String className,String method){
        try {
            name=className;
            clazz = Class.forName(className);
            this.method = clazz.getDeclaredMethod(method);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    public void action() throws
            InvocationTargetException,
            IllegalAccessException,
            InstantiationException {
        Object obje = clazz.newInstance();
        method.invoke(obje);
    }
//    Reflection(String className,String method){
//        try {
//            clazz = Class.forName(className);
//            clazz.getDeclaredMethod(method);
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//            return;
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        }
//    }
//    Reflection(String className,String method){
//        try {
//            clazz = Class.forName(className);
//            clazz.getDeclaredMethod(method);
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//            return;
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        }
//    }
}
