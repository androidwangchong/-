package com.stred.puzzle;

import android.app.Activity;
import android.os.Bundle;

public class ExitActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ExitApplication.getInstance().addActivity(this);
	}
	public void quit(){
		ExitApplication.getInstance().exit();
	}

}
