package com.stred.puzzle;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.umeng.analytics.MobclickAgent;

public class BaseActivity extends Activity {
	private Context mContext;
	AlertDialog selectImgDialog;
	static final int REQUEST_CODE_IMG = 0;
	static final int REQUEST_CODE_CAPTURE = 1;
	protected static final String KEY_BITMAPEXTRAS = "imgBitmap";
	public static Uri CAPTURE_PIC_URI = null;
	public static final DateFormat picNameForamt = new SimpleDateFormat(
			"yyyyMMdd_hhmmss");
	AlertDialog exitConfirmDialog;
	protected int exitMsgId = R.string.ExitConfirm;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExitApplication.getInstance().addActivity(this);

		mContext = this;
		MobclickAgent.setDebugMode(true);
		// SDK在统计Fragment时，需要关闭Activity自带的页面统计，
		// 然后在每个页面中重新集成页面统计的代码(包括调用了 onResume 和 onPause 的Activity)。
		MobclickAgent.openActivityDurationTrack(false);
		// MobclickAgent.setAutoLocation(true);
		// MobclickAgent.setSessionContinueMillis(1000);
		MobclickAgent.updateOnlineConfig(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	/*
	 * ѡ��ͼƬ
	 */
	void showSelectImgDialog() {
		if (selectImgDialog == null) {
			AlertDialog.Builder diagBuilder = new AlertDialog.Builder(this);
			diagBuilder.setTitle(R.string.selectImg);
			SelectImgAction imgSelectAction = new SelectImgAction();
			diagBuilder.setItems(R.array.imgSelectItems, imgSelectAction);

			diagBuilder.setCancelable(true);
			diagBuilder.setNegativeButton(R.string.cancel, imgSelectAction);
			selectImgDialog = diagBuilder.create();
		}
		selectImgDialog.show();

	}

	class SelectImgAction implements OnClickListener {

		public void onClick(DialogInterface dialog, int which) {
			if (which == 0) {
				startImgContentActivity();
			} else if (which == 1) {
				startCaptureActivity();
			}
		}

	}

	/*
	 * �����������ȡ�µ�ͼƬ����ƴͼ
	 */
	void startCaptureActivity() {
		if (GameConfig.DIR_CAPTURE_PIC == null) {
			AlertDialog.Builder diagBuilder = new AlertDialog.Builder(this);
			diagBuilder.setMessage(R.string.mountSDerror);
		} else {
			Uri picUri = getNewPicUri();
			Intent CaptureIntent = new Intent(
					"android.media.action.IMAGE_CAPTURE");
			CaptureIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
					picUri);
			CAPTURE_PIC_URI = picUri;
			startActivityForResult(CaptureIntent, REQUEST_CODE_CAPTURE);
		}

	}

	/*
	 * ��ȡ�µ�ͼƬ��URI
	 */
	Uri getNewPicUri() {
		String currPicName = GameConfig.CAPTURE_PIC_PREFIX
				+ picNameForamt.format(new Date());
		String filePath = GameConfig.DIR_CAPTURE_PIC + File.separator
				+ currPicName;
		File f = new File(filePath + ".jpg");
		int tryCount = 0;
		while (f.exists()) {
			filePath += tryCount;
			tryCount++;
			f = new File(filePath + ".jpg");
		}
		return Uri.fromFile(f);
	}

	/*
	 * ��ʼ�������������Ƭ�ı���Ŀ¼
	 */
	void initCapturePicDir() {
		try {
			String dir = Environment
					.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
					+ File.separator + "stred_puzzle";
			File file = new File(dir);
			if (!file.exists()) {
				file.mkdir();
			}
			GameConfig.DIR_CAPTURE_PIC = dir;
		} catch (Exception e) {
			Log.e("puzzle", "create app data dir error", e);
		}

	}

	/*
	 * ��ͼ��ѡ��
	 */
	void startImgContentActivity() {
		Intent imgIntent = new Intent(Intent.ACTION_GET_CONTENT);
		imgIntent.addCategory(Intent.CATEGORY_OPENABLE);
		imgIntent.setType("image/*");
		startActivityForResult(imgIntent, REQUEST_CODE_IMG);
	}

	/*
		  * 
		  */
	public void exitActivity() {
		if (exitConfirmDialog == null) {
			AlertDialog.Builder diagBuilder = new AlertDialog.Builder(this);
			diagBuilder.setTitle(R.string.exitTitle);
			diagBuilder.setMessage(exitMsgId);
			diagBuilder.setCancelable(false);
			diagBuilder.setPositiveButton(R.string.button_ok,
					new OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							quit();

						}
					});
			diagBuilder.setNegativeButton(R.string.cancel, null);
			exitConfirmDialog = diagBuilder.create();

		}
		exitConfirmDialog.show();
	}

	public void quit() {
		ExitApplication.getInstance().exit();
	}
}
