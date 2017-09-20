package com.ontro;

/**
 * Created by umm
 */

public class SharedObjects {
    public static String id;
    public static int animationDuration = 2500;

    public static Integer GetBadge(int i){
        int id = 0;
        switch (i){
            case 1:
                id = R.drawable.badge1;
                break;
            case 2:
                id = R.drawable.badge2;
                break;
            case 3:
                id = R.drawable.badge3;
                break;
            case 4:
                id = R.drawable.badge4;
                break;
            case 5:
                id = R.drawable.badge5;
                break;
        }
        return id;
    }
}
