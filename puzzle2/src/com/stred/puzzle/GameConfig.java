package com.stred.puzzle;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class GameConfig {
	public static  int COLUMNCOUNT = 3;
	public static  int ROWCOUNT = 3;
	public static  boolean SCALE_SCREEN = true;
	public static String DIR_CAPTURE_PIC = null;
	public static final String CONFIG_FILENAME = "config";
	private static final String CONFIG_COLUMANCOUNT = "columncount";
	private static final String CONFIG_ROWCOUNT = "rowcount";
	private static final String CONFIG_SCALE_SCREEN = "scalescreen";
    public static final String CAPTURE_PIC_PREFIX = "puzzle_";
	public static final int MINROWCOUNT = 3;
	public static final int MINCOLUMNCOUNT = 3;
	public static void initConfig(SharedPreferences  preferences){
		if(!preferences.contains(CONFIG_COLUMANCOUNT)){
			persistConfig(preferences);
		}
		COLUMNCOUNT =  preferences.getInt(CONFIG_COLUMANCOUNT, COLUMNCOUNT);
		ROWCOUNT =  preferences.getInt(CONFIG_ROWCOUNT, ROWCOUNT);
		SCALE_SCREEN = preferences.getBoolean(CONFIG_SCALE_SCREEN, SCALE_SCREEN);
		
	}
	public static boolean persistConfig(SharedPreferences  preferences){
		Editor editor = preferences.edit();
		editor.putInt(CONFIG_COLUMANCOUNT, COLUMNCOUNT);
		editor.putInt(CONFIG_ROWCOUNT, ROWCOUNT);
		editor.putBoolean(CONFIG_SCALE_SCREEN, SCALE_SCREEN);
		boolean success = editor.commit();
		Log.e("ttt", "terewrewrew"+success);
		return success;
	}
	public static boolean persistConfigColumnCount(SharedPreferences  preferences,int columnCount){
		Editor editor = preferences.edit();
		editor.putInt(CONFIG_COLUMANCOUNT, columnCount);
		boolean rs =  editor.commit();
		if(rs){
			COLUMNCOUNT = columnCount;
		}
		return rs;
	}
	
	public static boolean persistConfigRowCount(SharedPreferences  preferences,int rowcount){
		Editor editor = preferences.edit();
		editor.putInt(CONFIG_ROWCOUNT, rowcount);
		boolean rs =  editor.commit();
		if(rs){
			ROWCOUNT = rowcount;
		}
		return rs;
	}
	public static boolean persistConfigScaleScreen(SharedPreferences  preferences,boolean scaleScreen){
		Editor editor = preferences.edit();
		editor.putBoolean(CONFIG_SCALE_SCREEN, scaleScreen);
		boolean rs =  editor.commit();
		if(rs){
			SCALE_SCREEN = scaleScreen;
		}
		return rs;
	}
}
