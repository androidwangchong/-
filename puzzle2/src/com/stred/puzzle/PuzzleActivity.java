package com.stred.puzzle;

import java.io.File;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.RelativeLayout.LayoutParams;

import com.stred.util.screen.ScreenUtil;

public class PuzzleActivity extends BaseActivity {
	/** Called when the activity is first created. */
	// private Button startButton;
	// private Button helpButton;
	private Button cameraButton;
	private Button albumButton;
	private Button optionsButton;
	private Button exitButton;
	// private Button[] buttons;
	private static DisplayMetrics screenMetric = null;
	// private AlertDialog selectImgDialog;
	private Animation animationTranslate, animationRotate, animationScale;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.welcome);
		System.out.println("---------------------onCreate---------------------");
		initComponent();
		initListener();
		initConfig();
	}

	private void initConfig() {
		SharedPreferences preferences = getSharedPreferences(
				GameConfig.CONFIG_FILENAME, 0);
		GameConfig.initConfig(preferences);
		initCapturePicDir();

	}

	protected void onStart() {
		super.onStart();

	}

	/*
	 * ��ʼ���ؼ������¼�
	 */
	private void initListener() {
		ButtonAction btnAction = new ButtonAction();
		cameraButton.setOnClickListener(btnAction);
		albumButton.setOnClickListener(btnAction);
		optionsButton.setOnClickListener(btnAction);
		exitButton.setOnClickListener(btnAction);

	}

	/*
	 * ��ʼ���ؼ�
	 */
	private void initComponent() {
		cameraButton = (Button) findViewById(R.id.camerabutton);
		albumButton = (Button) findViewById(R.id.albumbutton);
		optionsButton = (Button) findViewById(R.id.settingbutton);
		exitButton = (Button) findViewById(R.id.exitbutton);

		screenMetric = ScreenUtil.getScreenSize(this);
		int width = screenMetric.widthPixels;
		int widthDig = (width - 130) / 3;

		exitButton.startAnimation(animTranslate(widthDig * 3, -50f,
				widthDig * 3, 0, 0, 0, exitButton, 1000));
		optionsButton.startAnimation(animTranslate(widthDig * 2, 50f,
				widthDig * 2, 0, 0, 0, optionsButton, 1000));
		cameraButton.startAnimation(animTranslate(widthDig, -50f, widthDig, 0,
				0, 0, cameraButton, 1000));
		albumButton.startAnimation(animTranslate(0f, 50f, 0, 0, 0, 0,
				albumButton, 1000));

		// buttons = new Button[4];
		// buttons[0] = cameraButton;
		// buttons[1] = albumButton;
		// buttons[2] = optionsButton;
		// buttons[3] = exitButton;

	}

	private void callClickAction(View v) {
		if (v == cameraButton) {
			startCaptureActivity();
		} else if (v == albumButton) {
			startImgContentActivity();
		} else if (v == optionsButton) {
			showOptions();
		} else if (v == exitButton) {
			exitActivity();
		}

	}

	protected Animation getScaleAnim(final View v) {
		Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);

		// animationScale = new ScaleAnimation(1f, toX, 1f, toY,
		// Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.45f);
		// animationScale.setInterpolator(PuzzleActivity.this,
		// anim.accelerate_decelerate_interpolator);
		// animationScale.setDuration(500);
		// animationScale.setFillAfter(false);
		shake.setAnimationListener(new AnimationListener() {

			public void onAnimationStart(Animation animation) {

			}

			public void onAnimationRepeat(Animation animation) {

			}

			public void onAnimationEnd(Animation animation) {
				callClickAction(v);

			}

		});
		return shake;

	}

	// �ƶ��Ķ���Ч��
	/*
	 * TranslateAnimation(float fromXDelta, float toXDelta, float fromYDelta,
	 * float toYDelta)
	 * 
	 * float fromXDelta:��������ʾ������ʼ�ĵ��뵱ǰView X����ϵĲ�ֵ��
	 * 
	 * ���� * float toXDelta, ��������ʾ��������ĵ��뵱ǰView X����ϵĲ�ֵ��
	 * 
	 * ���� * float fromYDelta, ��������ʾ������ʼ�ĵ��뵱ǰView Y����ϵĲ�ֵ��
	 * 
	 * ���� * float toYDelta)��������ʾ������ʼ�ĵ��뵱ǰView Y����ϵĲ�ֵ��
	 */
	protected Animation animTranslate(float toX, float toY,
			final int marginLeft, final int marginTop, final int marginRight,
			final int marginBottom, final Button button, long durationMillis) {

		animationTranslate = new TranslateAnimation(0, toX, 0, toY);
		animationTranslate.setAnimationListener(new AnimationListener() {

			public void onAnimationStart(Animation animation) {

			}

			public void onAnimationRepeat(Animation animation) {

			}

			public void onAnimationEnd(Animation animation) {
				// animation.
				LayoutParams params = new LayoutParams(0, 0);
				params.height = button.getHeight();
				params.width = button.getWidth();
				params.setMargins(marginLeft, marginTop, marginRight,
						marginBottom);
				button.setLayoutParams(params);
				button.clearAnimation();

			}
		});
		animationTranslate.setDuration(durationMillis);
		return animationTranslate;
	}

	protected Animation animRotate(float toDegrees, float pivotXValue,
			float pivotYValue) {

		animationRotate = new RotateAnimation(0, toDegrees,
				Animation.RELATIVE_TO_SELF, pivotXValue,
				Animation.RELATIVE_TO_SELF, pivotYValue);
		animationRotate.setAnimationListener(new AnimationListener() {

			public void onAnimationStart(Animation animation) {

			}

			public void onAnimationRepeat(Animation animation) {

			}

			public void onAnimationEnd(Animation animation) {
				animation.setFillAfter(true);
			}
		});
		return animationRotate;
	}

	class ButtonAction implements android.view.View.OnClickListener {

		public void onClick(View v) {
			v.startAnimation(getScaleAnim(v));

		}

	}

	/*
	 * ��ʾ���ý���
	 */
	private void showOptions() {
		Intent intent = new Intent(this, OptionsActivity.class);
		startActivity(intent);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case REQUEST_CODE_IMG: {
			if (data != null) {
				Uri picUri = data.getData();
				if (picUri != null) {
					renderSelectPic(picUri);
				}
			}

			break;
		}
		case REQUEST_CODE_CAPTURE: {
			if (new File(CAPTURE_PIC_URI.getPath()).exists()) {
				renderSelectPic(CAPTURE_PIC_URI);
			}
			break;
		}
		}
	}

	/*
	 * ��Ⱦ��ͼ��ѡ���ͼƬ
	 */
	private void renderSelectPic(Uri picUri) {

		Intent puzzleImgIntent = new Intent(this, PuzzleImgActivity.class);
		puzzleImgIntent.putExtra(KEY_BITMAPEXTRAS, picUri);
		startActivity(puzzleImgIntent);
	}

	// @Override
	// protected void onStop() {
	// if(exitConfirmDialog == null){
	// AlertDialog.Builder diagBuilder = new AlertDialog.Builder(this);
	// diagBuilder.setTitle(R.string.exitTitle);
	// diagBuilder.setMessage(R.string.ExitConfirm);
	//
	// diagBuilder.setNegativeButton(R.string.cancel, imgSelectAction);
	// exitConfirmDialog = diagBuilder.create();
	// exitConfirmDialog.
	// }
	// super.onStop();
	// }
	@Override
	public void onBackPressed() {
		exitActivity();
	}

}