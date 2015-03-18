package com.gionee.note.content;

import amigo.app.AmigoActivity;
import android.util.DisplayMetrics;

public class Session {

    private static int screenHeight;
    private static int screenWight;

    public void setScreenSize(AmigoActivity activity) {

        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        setScreenHeight(dm.heightPixels);
        setScreenWight(dm.widthPixels);
    }


    public static int getScreenHeight() {
        return screenHeight;
    }

    public static void setScreenHeight(int screenHeight) {
        Session.screenHeight = screenHeight;
    }

    public static int getScreenWight() {
        return screenWight;
    }

    public static void setScreenWight(int screenWight) {
        Session.screenWight = screenWight;
    }
    

}