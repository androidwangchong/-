package com.stred.puzzle;


import com.qjl.puzzle.R;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

public class OptionsActivity extends ExitActivity {
	private Spinner columnSpinner;
	private Spinner rowSpinner;
	private ToggleButton scaleScreenButton;
	private View scaleOPPanel;
	private View opConsolePanel;
	private Button  okButton;
	private Button cancelButton;
	boolean fromPuzzleView = false;
	private int assign_rowcount = GameConfig.ROWCOUNT;
	private int assign_columncount = GameConfig.COLUMNCOUNT;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.options);
		initComponent();
		
		initAdapter();
		initValues();
		initListener();
	}
	private void initListener() {
		
		if(!fromPuzzleView){
			RowOrcolumnCountSelectedAction selectAction = new RowOrcolumnCountSelectedAction();
			columnSpinner.setOnItemSelectedListener(selectAction);
			rowSpinner.setOnItemSelectedListener(selectAction);
			scaleScreenButton.setOnCheckedChangeListener(new OnCheckedChangeListener(){

				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					if(isChecked == GameConfig.SCALE_SCREEN){
						return;
					}
					 boolean rs = GameConfig.persistConfigScaleScreen(getSharedPreferences(GameConfig.CONFIG_FILENAME, 0),isChecked);
					 showToast(rs); 
					 if(!rs){
						 scaleScreenButton.setChecked(!isChecked);
					 }
				}
				
			});
		}else{
			OnClickListener onClickAction = new OnClickListener(){

				public void onClick(View v) {
					 if(v == okButton){
						 sendNewLevelBackToPuzzleView();
					 }else{
						 finish();
					 }
				}
				
			};
			okButton.setOnClickListener(onClickAction);
			cancelButton.setOnClickListener(onClickAction);
		}
		
		
	}
	/*
	 * �����úõ����������ظ�ƴͼ����
	 */
	private void sendNewLevelBackToPuzzleView(){
		Intent intent = new Intent();
		intent.putExtra(PuzzleImgActivity.Extra_COLUMNCOUNT, columnSpinner.getSelectedItemPosition() + GameConfig.MINCOLUMNCOUNT);
		intent.putExtra(PuzzleImgActivity.Extra_ROWCOUNT, rowSpinner.getSelectedItemPosition()+ GameConfig.MINCOLUMNCOUNT);
		setResult(RESULT_OK, intent);
		finish();
	}
	private void initValues() {
		 columnSpinner.setSelection(assign_columncount- GameConfig.MINCOLUMNCOUNT);
		 rowSpinner.setSelection(assign_rowcount - GameConfig.MINROWCOUNT);
		 scaleScreenButton.setChecked(GameConfig.SCALE_SCREEN);
		 
	}
	/*
	 * ��ʼ������������ѡ���б�
	 */
	private void initAdapter() {
		    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
	                this, R.array.columnOrRowcounts, android.R.layout.simple_spinner_item);
	        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        columnSpinner.setAdapter(adapter);
	        rowSpinner.setAdapter(adapter);
		
	}
	private void initComponent() {
		Bundle extras = getIntent().getExtras();
		 if(extras != null && extras.get(PuzzleImgActivity.FROM_PUZZLEVIEW_VAR) != null){
			 this.fromPuzzleView = true;
			 assign_rowcount = extras.getInt(PuzzleImgActivity.Extra_ROWCOUNT);
			 assign_columncount = extras.getInt(PuzzleImgActivity.Extra_COLUMNCOUNT);
		 }
		columnSpinner = (Spinner)findViewById(R.id.columnSpinner);
		rowSpinner = (Spinner)findViewById(R.id.rowSpinner);
		scaleScreenButton = (ToggleButton)findViewById(R.id.scaleScreen);
		scaleOPPanel = (View)findViewById(R.id.opScaleScreenPanel);
		opConsolePanel = (View)findViewById(R.id.opConsolePanel);
		okButton = (Button)findViewById(R.id.opOkbutton);
		cancelButton = (Button)findViewById(R.id.opCancelbutton);
		
		if(fromPuzzleView){
			scaleOPPanel.setVisibility(View.INVISIBLE);
		}else{
			opConsolePanel.setVisibility(View.INVISIBLE);
		}
	}
	void showToast(boolean success) {
		if(success)
        Toast.makeText(this, R.string.optiontsSaveSuccessfullLabel, Toast.LENGTH_SHORT).show();
		else{
			 Toast.makeText(this, R.string.optionsSaveFailedLabel, Toast.LENGTH_SHORT).show();
		}
    }
	
	class RowOrcolumnCountSelectedAction implements OnItemSelectedListener{

		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			 
		     if(parent == columnSpinner){
		    	 if(position + GameConfig.MINCOLUMNCOUNT == assign_columncount){
		    		 return;
		    	 }
		    	 
		    	boolean rs =  GameConfig.persistConfigColumnCount(getSharedPreferences(GameConfig.CONFIG_FILENAME, 0),position + GameConfig.MINCOLUMNCOUNT);
		    	showToast(rs); 
		     }else{
		    	 if(position + GameConfig.MINROWCOUNT == assign_rowcount){
		    		 return;
		    	 }
		    	 
		    	 boolean rs =  GameConfig.persistConfigRowCount(getSharedPreferences(GameConfig.CONFIG_FILENAME, 0),position + GameConfig.MINROWCOUNT);
			    	showToast(rs); 
		     }
		}

		public void onNothingSelected(AdapterView<?> parent) {
			 
		}
		
	}
}
