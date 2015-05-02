package com.qjl.puzzle;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Application;

public class ExitApplication extends Application {
	private static ExitApplication instance = null;
	private List<Activity> list = new ArrayList<Activity>();
	private ExitApplication(){
		
	}
	public static ExitApplication getInstance(){
		if(instance == null){
			instance = new ExitApplication();
		}
		return instance;
	}
	
	public boolean addActivity(Activity act){
		if(list == null){
			list = new ArrayList<Activity>();
		}
		return list.add(act);
	}
	public void exit(){
		for(Activity act : list){
			if(act != null){
				act.finish();
			}
		}
		if(list != null){
			list.clear();
			list = null;
		}
		 
	}

}
