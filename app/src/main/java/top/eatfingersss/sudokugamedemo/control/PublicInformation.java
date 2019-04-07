package top.eatfingersss.sudokugamedemo.control;

import java.text.SimpleDateFormat;

public class PublicInformation {
    public static boolean developerMode=false;
    public static final String GET_ESTABLISHED_MATRIX_URL="http://www.eatfingersss.top/sudoku/getEstablishedMatrix";
    public static final String GET_RANDOM_MATRIX_URL="http://www.eatfingersss.top/sudoku/getMatrix";
    public static final String GET_RANDOM_NAME_URL="http://www.eatfingersss.top/getRandomName";

    public static final int MAX_RUN_TIME = 3;//秒
    public static final SimpleDateFormat SDF_OUT_TIME =
            new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat SDF_DATA_TIME =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");



}

//if(PublicInformation.developerMode)