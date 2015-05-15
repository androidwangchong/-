package com.stred.puzzle;

import java.io.File;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.NoTitle;
import org.androidannotations.annotations.ViewById;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.stred.util.screen.ScreenUtil;

@NoTitle
@Fullscreen
@EActivity(R.layout.welcome)
public class PuzzleActivity extends BaseActivity {
	@ViewById
	RelativeLayout camerabutton;
	@ViewById
	RelativeLayout albumbutton;
	@ViewById
	RelativeLayout settingbutton;
	@ViewById
	RelativeLayout exitbutton;
	private static DisplayMetrics screenMetric = null;
	private Animation animationTranslate, animationRotate, animationScale;

	@AfterViews
	void init() {
		// initComponent();
		initListener();
		initConfig();
	}

	private void initConfig() {
		SharedPreferences preferences = getSharedPreferences(
				GameConfig.CONFIG_FILENAME, 0);
		GameConfig.initConfig(preferences);
		initCapturePicDir();

	}

	private void initListener() {
		ButtonAction btnAction = new ButtonAction();
		camerabutton.setOnClickListener(btnAction);
		albumbutton.setOnClickListener(btnAction);
		settingbutton.setOnClickListener(btnAction);
		exitbutton.setOnClickListener(btnAction);

	}

	private void initComponent() {
		screenMetric = ScreenUtil.getScreenSize(this);
		int width = screenMetric.widthPixels;
		int widthDig = (width - 130) / 3;

		exitbutton.startAnimation(animTranslate(widthDig * 3, -50f,
				widthDig * 3, 0, 0, 0, exitbutton, 1000));
		settingbutton.startAnimation(animTranslate(widthDig * 2, 50f,
				widthDig * 2, 0, 0, 0, settingbutton, 1000));
		camerabutton.startAnimation(animTranslate(widthDig, -50f, widthDig, 0,
				0, 0, camerabutton, 1000));
		albumbutton.startAnimation(animTranslate(0f, 50f, 0, 0, 0, 0,
				albumbutton, 1000));

	}

	private void callClickAction(View v) {
		if (v == camerabutton) {
			startCaptureActivity();
		} else if (v == albumbutton) {
			startImgContentActivity();
		} else if (v == settingbutton) {
			showOptions();
		} else if (v == exitbutton) {
			exitActivity();
		}

	}

	protected Animation getScaleAnim(final View v) {
		Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);

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

	protected Animation animTranslate(float toX, float toY,
			final int marginLeft, final int marginTop, final int marginRight,
			final int marginBottom, final RelativeLayout button, long durationMillis) {

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

	private void renderSelectPic(Uri picUri) {

		Intent puzzleImgIntent = new Intent(this, PuzzleImgActivity.class);
		puzzleImgIntent.putExtra(KEY_BITMAPEXTRAS, picUri);
		startActivity(puzzleImgIntent);
	}

	@Override
	public void onBackPressed() {
		exitActivity();
	}

}