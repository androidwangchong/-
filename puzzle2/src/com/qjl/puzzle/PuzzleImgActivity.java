package com.qjl.puzzle;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.qjl.util.screen.ScreenUtil;

import android.app.AlertDialog;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

public class PuzzleImgActivity extends BaseActivity {
	private Bitmap imgBitmapForFit = null;
	private Bitmap imgBitmapForScreen = null;
	private PuzzleView puzzleViewScreen = null;// 负责拼图的view 铺满屏幕
	private PuzzleView puzzleViewFit = null;//负责拼图的view 最合适大小
	private Uri imgUri = null;// 拼图所需图片的Uri
	private View puzzleLaoyut;//拼图的view 其中包含了铺满屏幕的一个vew和最合适大小的一个view
	private static DisplayMetrics screenMetric = null;
	private static BitmapFactory.Options bitmapOptions;
	private ImageView originalPicView = null;// 显示原始图片的view
	private Menu menu;
    private boolean scaleScreen = GameConfig.SCALE_SCREEN;//是否铺满屏幕
    private PuzzleView puzzleView;//引用当前拼图的view
    private boolean showBadge = false;//是否显示图片标记
    private static final int REQUESTCODE_CHANGELEVEL = 3;
    public static String FROM_PUZZLEVIEW_VAR = "frompuzzlevar";
    public static final String Extra_ROWCOUNT = "optionrowcount";
    public static final String Extra_COLUMNCOUNT = "optioncolumncount";
	public boolean isShowBadge() {
		return this.showBadge;
	}

	public void setShowBadge(boolean showBadge) {
		this.showBadge = showBadge;
	}

	public boolean isScaleScreen() {
		return this.scaleScreen;
	}

	public void setScaleScreen(boolean scaleScreen) {
		this.scaleScreen = scaleScreen;
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		exitMsgId = R.string.exitCurrentPuzzle;
		init();
		setContentView(R.layout.puzzle);
		initComponent();
		initPuzzlePic();
		initPuzzleView();
	}

	private void init() {
		if (bitmapOptions == null) {
			bitmapOptions = new BitmapFactory.Options();
		}
		if (screenMetric == null)
			screenMetric = ScreenUtil.getScreenSize(this);

		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_4444;
		bitmapOptions.inPurgeable = true;
		bitmapOptions.inInputShareable = true;

	}

	/*
	 * 初始化拼图的view
	 */
	private void initPuzzleView() {
		puzzleView.renderPuzzleImage(this.isScaleScreen()?imgBitmapForScreen:imgBitmapForFit,null,null);

	}

	/*
	 * 初始化需要进行拼图的图片的bitmap
	 */
	private void initPuzzlePic() {
		Bundle extras = getIntent().getExtras();
		imgUri = (Uri) extras.get(PuzzleActivity.KEY_BITMAPEXTRAS);
		initSourceBitmap();

	}

	/*
	 * 初始化需要拼图的图片的bitmap
	 */
	private void initSourceBitmap() {
		InputStream inputStream = null;
		try {
			inputStream = getContentResolver().openInputStream(imgUri);
			bitmapOptions.inJustDecodeBounds = true;
			bitmapOptions.inSampleSize = 1;
			// 获取图片的大小
			BitmapFactory.decodeStream(inputStream, null, bitmapOptions);
			int picWidth = bitmapOptions.outWidth;
			int picHeight = bitmapOptions.outHeight;
			
			boolean rotate = false;
			/*
			 * 如果是横向的图就设置90度旋转
			 */
			if(picWidth > picHeight){
				int tmpwidth = picWidth;
				picWidth = picHeight;
				picHeight = tmpwidth;
				rotate = true;
			}
			
		
			// 将图片进行缩放
			int screenWidth = screenMetric.widthPixels / 10;
			int screenHeight = screenMetric.heightPixels / 10;
			float scaleW = (float) picWidth / screenWidth;
			float scaleH = (float) picHeight / screenHeight;
			float scale = Math.min(scaleW, scaleH);
			if (scale % 10 != 0) {
				scale += 10;
			}
			int s = (int) (scale / 10);
			if (s < 1) {
				s = 1;
			}
		//	Log.e("testp", picWidth+"<<<<<<<<<<<<"+picHeight+">>>>>>"+s);
			bitmapOptions = new BitmapFactory.Options();
			bitmapOptions.inJustDecodeBounds = false;
			bitmapOptions.inSampleSize = s;
			if (imgBitmapForFit != null && !imgBitmapForFit.isRecycled()) {
				imgBitmapForFit.recycle();
			}
			if (imgBitmapForScreen != null && !imgBitmapForScreen.isRecycled()) {
				imgBitmapForScreen.recycle();
			}
			
			inputStream.close();
			inputStream = getContentResolver().openInputStream(imgUri);
            
			int screenImgWidth;
			int screenImgHeight;
			if(rotate){
				screenImgWidth = screenMetric.heightPixels;
				screenImgHeight = screenMetric.widthPixels;
			}else{
				screenImgWidth = screenMetric.widthPixels;
				screenImgHeight = screenMetric.heightPixels;
			}
			
			imgBitmapForFit = BitmapFactory.decodeStream(inputStream, new Rect(0, 0,
					screenImgWidth, screenImgHeight),
					bitmapOptions);
			inputStream.close();
			if(rotate){
				 Matrix rotateMatrix = new Matrix();
				 rotateMatrix .postRotate( 90 );

				imgBitmapForFit = Bitmap.createBitmap(imgBitmapForFit, 0, 0, imgBitmapForFit.getWidth(), imgBitmapForFit.getHeight(), rotateMatrix, true);
			}
			imgBitmapForScreen = Bitmap.createScaledBitmap(imgBitmapForFit,
						screenMetric.widthPixels, screenMetric.heightPixels,
						true);
			 
				int padding = screenMetric.heightPixels - imgBitmapForFit.getHeight();
			//	Log.e("testp", imgBitmapForFit.getWidth()+">>>>"+imgBitmapForFit.getHeight());
				puzzleViewFit.setPadding(0, padding / 2, 0, padding / 2);
				 
			originalPicView.setImageBitmap(imgBitmapForScreen);

		} catch (Exception e) {
			Log.e("puzzle.img.getinpustream",
					"get seelct img inpustream error!", e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					Log.e("render img", "close img inputstream error", e);
				}
			}
		}

	}

	private ViewGroup mContainer;

	/*
	 * 初始化控件
	 */
	private void initComponent() {
		puzzleLaoyut = (View) findViewById(R.id.puzzleView);
		puzzleViewScreen = (PuzzleView)findViewById(R.id.puzzleviewScreen);
		puzzleViewScreen.setScaleScreen(true);
		puzzleViewFit = (PuzzleView)findViewById(R.id.puzzleviewFit);
		puzzleViewFit.setScaleScreen(false);
		setPuzzleView();
		originalPicView = (ImageView) findViewById(R.id.originalPicView);
		mContainer = (ViewGroup) findViewById(R.id.puzzleContainer);
		mContainer
				.setPersistentDrawingCache(ViewGroup.PERSISTENT_ANIMATION_CACHE);
		originalPicView.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				applyRotation(false, 180, 90);
				menu.findItem(R.id.displayPuzzleView).setVisible(false);
				menu.findItem(R.id.displayOriginalPic).setVisible(true);
			}

		});
	}
	/*
	 * 设置当前拼图的puzzleview
	 */
	private void setPuzzleView(){
		if(this.isScaleScreen()){
			puzzleView = puzzleViewScreen;
			puzzleViewScreen.setVisibility(View.VISIBLE);
			puzzleViewFit.setVisibility(View.INVISIBLE);
		}else{
			puzzleView = puzzleViewFit;
			puzzleViewScreen.setVisibility(View.INVISIBLE);
			puzzleViewFit.setVisibility(View.VISIBLE);
		}
	 
		 puzzleView.setShowBadge(this.showBadge);
		 
	}

	/*
	 * 创建menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = this.getMenuInflater();
		inflater.inflate(R.menu.puzzlemenu, menu);
		this.menu = menu;
		if (puzzleView.isShowBadge()) {
			menu.findItem(R.id.hideBadge).setVisible(true);
			menu.findItem(R.id.displayBadge).setVisible(false);
		}
		if (puzzleView.isScaleScreen()) {
			menu.findItem(R.id.scaleFit).setVisible(true);
			menu.findItem(R.id.scaleScreen).setVisible(false);
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onMenuItemSelected(int, android.view.MenuItem)
	 */
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		int itemId = item.getItemId();
		switch (itemId) {
		case R.id.reStrartGame: {
			restartGame();
			break;
		}
		case R.id.displayOriginalPic: {
			applyRotation(true, 0, 90);
			item.setVisible(false);
			menu.findItem(R.id.displayPuzzleView).setVisible(true);
			break;
		}
		case R.id.displayPuzzleView: {
			showPuzzleView();
			break;
		}
		case R.id.displayBadge: {
			puzzleView.showBadge();
			item.setVisible(false);
			menu.findItem(R.id.hideBadge).setVisible(true);
			this.setShowBadge(true);
			break;
		}
		case R.id.hideBadge: {
			puzzleView.hideBadge();
			item.setVisible(false);
			menu.findItem(R.id.displayBadge).setVisible(true);
			this.setShowBadge(false);
			break;
		}
		case R.id.scaleFit:{
			scaleImgFit();
			break;
		}
		case R.id.scaleScreen:{
			scaleImgScreen();
			break;
		}
		case R.id.changePic:{
			showSelectImgDialog();
			break;
		}
		case R.id.changeLevel:{
			//changePuzzleLevel();
			//puzzleView.showSuccessToast();
			startChangeLevelIntent();
			break;
		}
		}

		return true;
	}
	/*
	 * 设置
	 */
	private void startChangeLevelIntent() {
	 Intent intent = new Intent(this,OptionsActivity.class);
	 intent.putExtra(FROM_PUZZLEVIEW_VAR, "true");
	 intent.putExtra(Extra_COLUMNCOUNT, puzzleView.getColumnCount());
	 intent.putExtra(Extra_ROWCOUNT, puzzleView.getRowCount());
	 startActivityForResult(intent, REQUESTCODE_CHANGELEVEL);
		
	}
	 

	private void changePuzzleLevel(int columncount,int rowcount){
		
		puzzleViewScreen.setColumnCount(columncount);
		puzzleViewScreen.setRowCount(rowcount);
		puzzleViewFit.setColumnCount(columncount);
		puzzleViewFit.setRowCount(rowcount);
		renderNewImage(imgUri);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	 
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode != RESULT_OK){
			return;
		}
		switch(requestCode){
		case REQUEST_CODE_IMG:{
			if(data != null){
				 Uri picUri = data.getData();
				 if(picUri != null){
					 renderNewImage(picUri);
				 }
			}
			
			break;
		}
		case REQUEST_CODE_CAPTURE:{
			if(new File(CAPTURE_PIC_URI.getPath()).exists()){
				 renderNewImage(CAPTURE_PIC_URI);
			}
			break;
		}
		case REQUESTCODE_CHANGELEVEL:{
			if(data != null){
				int assign_rowcount = data.getExtras().getInt(PuzzleImgActivity.Extra_ROWCOUNT);
				int assign_columncount = data.getExtras().getInt(PuzzleImgActivity.Extra_COLUMNCOUNT);
				changePuzzleLevel(assign_columncount,assign_rowcount);
			}
		}
		}
	}
	/*
	 * 渲染新的图片
	 */
	private void renderNewImage(Uri imgUri){
		this.imgUri = imgUri;
		 initSourceBitmap();
		 puzzleViewScreen.setHasRendered(false);
		 puzzleViewFit.setHasRendered(false);
		 puzzleViewScreen.invalidate();
		 puzzleViewFit.invalidate();
		 initPuzzleView();
		
	}
	/*
	 * 显示拼图view
	 */
	private void showPuzzleView(){
		applyRotation(false, 180, 90);
		menu.findItem(R.id.displayPuzzleView).setVisible(false);
		menu.findItem(R.id.displayOriginalPic).setVisible(true);
	}
  

	/*
     * 将图片渲染为合适大小
     */
	private void scaleImgFit() {
		 
		 initScalScreenMenu(false);
	     this.setScaleScreen(false);
		 if(puzzleViewFit.isHasRendered()){
			 puzzleViewFit.resetPosition(puzzleViewScreen.getPositionwrap());
			 puzzleViewFit.setEmptyPostion(puzzleViewScreen.getEmptyPostion());
			 puzzleViewFit.initImageViews();
			
		 }else{
			 puzzleViewFit.renderPuzzleImage(this.isScaleScreen()?imgBitmapForScreen:imgBitmapForFit,puzzleViewScreen.getPositionwrap(),String.valueOf(puzzleViewScreen.getEmptyPostion()));
		 }
		 puzzleViewFit.setMoveCount(puzzleViewScreen.getMoveCount());
		 
		 setPuzzleView();
		
		 initBadeAfterChagne();
	}
	
	private void initBadeAfterChagne(){
		 if(showBadge){
			 puzzleView.showBadge();
		 }else{
			 puzzleView.hideBadge();
		 }
	}
	private void initScalScreenMenu(boolean isScreen){
		 menu.findItem(R.id.scaleFit).setVisible(isScreen);
		 menu.findItem(R.id.scaleScreen).setVisible(!isScreen);
	}
	/*
	 * 将图片渲染为整个屏幕
	 */
	private void scaleImgScreen(){
		 initScalScreenMenu(true);
		 this.setScaleScreen(true);
		 if(puzzleViewScreen.isHasRendered()){
			 puzzleViewScreen.resetPosition(puzzleViewFit.getPositionwrap());
			 puzzleViewScreen.setEmptyPostion(puzzleViewFit.getEmptyPostion());
			 puzzleViewScreen.initImageViews();
		 }else{
			 puzzleViewScreen.renderPuzzleImage(this.isScaleScreen()?imgBitmapForScreen:imgBitmapForFit,puzzleViewFit.getPositionwrap(),String.valueOf(puzzleViewFit.getEmptyPostion()));
		 }
		 puzzleViewScreen.setMoveCount(puzzleViewFit.getMoveCount());
		 
		 setPuzzleView();
		
		 initBadeAfterChagne();
	}

	/*
	 * 重新开始游戏
	 */
	private void restartGame() {
		puzzleView.resetEmpayPosition();
		puzzleView.wrapposition();
		puzzleView.initImageViews();
		puzzleView.setMoveCount(0);

	}

	/**
	 * Setup a new 3D rotation on the container view.
	 * 
	 * @param position
	 *            the item that was clicked to show a picture, or -1 to show the
	 *            list
	 * @param start
	 *            the start angle at which the rotation must begin
	 * @param end
	 *            the end angle of the rotation
	 */
	private void applyRotation(boolean showOriginal, float start, float end) {
		// Find the center of the container
		final float centerX = mContainer.getWidth() / 2.0f;
		final float centerY = mContainer.getHeight() / 2.0f;

		// Create a new 3D rotation with the supplied parameter
		// The animation listener is used to trigger the next animation
		final Rotate3dAnimation rotation = new Rotate3dAnimation(start, end,
				centerX, centerY, 310.0f, true);
		rotation.setDuration(500);
		rotation.setFillAfter(true);
		rotation.setInterpolator(new AccelerateInterpolator());
		rotation.setAnimationListener(new DisplayNextView(showOriginal));

		mContainer.startAnimation(rotation);
	}

	/**
	 * This class listens for the end of the first half of the animation. It
	 * then posts a new action that effectively swaps the views when the
	 * container is rotated 90 degrees and thus invisible.
	 */
	private final class DisplayNextView implements Animation.AnimationListener {
		private final boolean showOriginal;

		private DisplayNextView(boolean showOriginal) {
			this.showOriginal = showOriginal;
		}

		public void onAnimationStart(Animation animation) {
		}

		public void onAnimationEnd(Animation animation) {
			mContainer.post(new SwapViews(showOriginal));
		}

		public void onAnimationRepeat(Animation animation) {
		}
	}

	/**
	 * This class is responsible for swapping the views and start the second
	 * half of the animation.
	 */
	private final class SwapViews implements Runnable {
		private final boolean showOriginal;

		public SwapViews(boolean showOriginal) {
			this.showOriginal = showOriginal;
		}

		public void run() {
			final float centerX = mContainer.getWidth() / 2.0f;
			final float centerY = mContainer.getHeight() / 2.0f;
			Rotate3dAnimation rotation;

			if (showOriginal) {
				puzzleLaoyut.setVisibility(View.GONE);
				originalPicView.setVisibility(View.VISIBLE);
				originalPicView.requestFocus();

				rotation = new Rotate3dAnimation(90, 0, centerX, centerY,
						310.0f, false);
			} else {
				originalPicView.setVisibility(View.GONE);
				puzzleLaoyut.setVisibility(View.VISIBLE);
				puzzleView.requestFocus();

				rotation = new Rotate3dAnimation(90, 0, centerX, centerY,
						310.0f, false);
			}

			rotation.setDuration(500);
			rotation.setFillAfter(true);
			rotation.setInterpolator(new DecelerateInterpolator());

			mContainer.startAnimation(rotation);
		}
	}
	@Override
	public void onBackPressed() {
		 if(originalPicView.isShown()){
			 showPuzzleView();
		 }else{
			 exitActivity();
		 }
	}
	
	public void quit(){
		finish();
	}
}
