package com.stred.puzzle;

import java.text.MessageFormat;
import java.util.Random;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class PuzzleView extends GridView {

	private Drawable[] lumpDrawables = null;// �ָ���ͼƬƬ������
	private Drawable emptyDrawable = null;// ��ȥ����ͼƬƬ����ʹ�õı���
	private int[] positionwrap = null;// ����˳����ͼƬ��Ƭ����
	private int moveCount = 0;// һ��ͼƬ�Ĵ���
	// private Toast successToast = null;
	private int rowCount = GameConfig.ROWCOUNT;// �ָ�ͼƬ������
	private int columnCount = GameConfig.COLUMNCOUNT;// �ָ�ͼƬ������

	public int getRowCount() {
		return this.rowCount;
	}

	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}

	public int getColumnCount() {
		return this.columnCount;
	}

	public void setColumnCount(int columnCount) {
		this.columnCount = columnCount;
	}

	public int getMoveCount() {
		return this.moveCount;
	}

	public void setMoveCount(int moveCount) {
		this.moveCount = moveCount;
	}

	public int[] getPositionwrap() {
		return this.positionwrap;
	}

	public void setPositionwrap(int[] positionwrap) {
		this.positionwrap = positionwrap;
	}

	private int emptyPostion = -1;// ȥ������Ƭ��λ������

	public int getEmptyPostion() {
		return this.emptyPostion;
	}

	public void setEmptyPostion(int emptyPostion) {
		this.emptyPostion = emptyPostion;
		if (emptyPostion == -1) {
			successPuzzle();
		}
	}

	private Random random = new Random();// �û���ȡͼƬ���λ�õ�
	private LumpAdapter adapter = null;

	private TextView[] pieceViews = null;// ��ȾͼƬƬ�ε�view����
	private int pieceWidth;
	private int pieceHeight;
	private boolean showBadge = false;// �Ƿ���ʾ���
	private boolean scaleScreen = GameConfig.SCALE_SCREEN;// �Ƿ�������Ļ
	private boolean hasRendered = false;

	public boolean isHasRendered() {
		return this.hasRendered;
	}

	public void setHasRendered(boolean hasRendered) {
		this.hasRendered = hasRendered;
		moveCount = 0;
	}

	public boolean isScaleScreen() {
		return this.scaleScreen;
	}

	public void setScaleScreen(boolean scaleScreen) {
		this.scaleScreen = scaleScreen;
	}

	public boolean isShowBadge() {
		return this.showBadge;
	}

	public void setShowBadge(boolean showBadge) {
		this.showBadge = showBadge;
	}

	public PuzzleView(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	/*
	 * ���� ����¼���ͼƬλ��
	 */
	@Override
	public boolean performItemClick(View view, int position, long id) {
		if (emptyPostion < 0 || positionwrap[position] == emptyPostion) {
			return true;
		}
		int leftpoint = -1;
		int rightpoint = -1;
		int toppoint = -1;
		int bottompoint = -1;

		if (position % columnCount != 0) {
			leftpoint = position - 1;
			if (positionwrap[leftpoint] == this.emptyPostion) {
				wrapOneStep(leftpoint, position);
				return true;
			}
		}
		if (position / columnCount >= 1) {
			toppoint = position - columnCount;
			if (positionwrap[toppoint] == this.emptyPostion) {
				wrapOneStep(toppoint, position);
				return true;
			}
		}
		if (position % columnCount != columnCount - 1) {
			rightpoint = position + 1;
			if (positionwrap[rightpoint] == this.emptyPostion) {
				wrapOneStep(rightpoint, position);
				return true;
			}
		}
		if (position / columnCount < rowCount - 1) {
			bottompoint = position + columnCount;
			if (positionwrap[bottompoint] == this.emptyPostion) {
				wrapOneStep(bottompoint, position);
				return true;
			}
		}

		return true;
	}

	/*
	 * ����ƴͼͼƬ
	 */
	protected void renderPuzzleImage(Bitmap pluzzleimage,
			int[] positionwrapAsign, String emptyPostionstr) {
		pieceWidth = (int) Math.floor(pluzzleimage.getWidth() / columnCount);
		pieceHeight = (int) Math.floor(pluzzleimage.getHeight() / rowCount);
		lumpDrawables = new Drawable[columnCount * rowCount];
		for (int i = 0; i < lumpDrawables.length; i++) {
			int py = i / columnCount;
			int px = i % columnCount;
			lumpDrawables[i] = new BitmapDrawable(Bitmap.createBitmap(
					pluzzleimage, px * pieceWidth, py * pieceHeight,
					pieceWidth, pieceHeight));
		}
		emptyDrawable = new BitmapDrawable(Bitmap.createBitmap(pieceWidth,
				pieceHeight, Bitmap.Config.ARGB_8888));
		this.positionwrap = new int[columnCount * rowCount];
		for (int i = 0; i < this.positionwrap.length; i++) {
			this.positionwrap[i] = i;
		}

		pieceViews = new TextView[columnCount * rowCount];
		initLumpViews();
		if (positionwrapAsign == null) {
			wrapposition();
		} else {
			for (int i = 0; i < this.positionwrap.length; i++) {
				this.positionwrap[i] = positionwrapAsign[i];
			}
		}
		if (emptyPostionstr == null) {
			emptyPostion = random.nextInt(positionwrap.length);
		} else {
			emptyPostion = Integer.parseInt(emptyPostionstr);
		}

		initImageViews();
		adapter = new LumpAdapter();
		setNumColumns(columnCount);
		this.setAdapter(adapter);
		hasRendered = true;
	}

	protected void resetPosition(int[] positionwrapOther) {
		for (int i = 0; i < this.positionwrap.length; i++) {
			this.positionwrap[i] = positionwrapOther[i];
		}
	}

	/*
	 * ��������emptyPostion
	 */
	protected void resetEmpayPosition() {
		emptyPostion = random.nextInt(positionwrap.length);
	}

	/*
	 * ����grid�ռ��е�ͼƬ
	 */
	protected void initImageViews() {
		int imgIdx = -1;
		for (int i = 0; i < positionwrap.length; i++) {
			imgIdx = positionwrap[i];
			if (this.isShowBadge())
				pieceViews[i].setText(String.valueOf(imgIdx));
			else {
				pieceViews[i].setText(String.valueOf(""));
			}
			if (imgIdx != emptyPostion) {

				pieceViews[i].setBackgroundDrawable(lumpDrawables[imgIdx]);
			} else {

				pieceViews[i].setBackgroundDrawable(emptyDrawable);

			}
		}

	}

	/*
	 * ��ʾͼƬ���
	 */
	protected void showBadge() {
		for (int i = 0; i < positionwrap.length; i++) {
			int imgIdx = positionwrap[i];
			pieceViews[i].setText(String.valueOf(imgIdx));
		}
		setShowBadge(true);
	}

	/*
	 * ����ͼƬ���
	 */
	protected void hideBadge() {
		for (int i = 0; i < positionwrap.length; i++) {
			pieceViews[i].setText(String.valueOf(""));
		}
		setShowBadge(false);
	}

	/*
	 * ����ͼƬ˳��
	 */
	protected void wrapposition() {
		for (int i = 0; i < 10; i++) {
			int p1 = random.nextInt(positionwrap.length);
			int p2 = random.nextInt(positionwrap.length);
			if (p1 != p2) {
				wrapPosition(p1, p2);
			}
		}
		if (isOriginalPosition()) {
			wrapposition();
		}
	}

	/*
	 * ����������Ƭ��λ��
	 */
	private void wrapPosition(int p1, int p2) {
		int pv1 = positionwrap[p1];
		int pv2 = positionwrap[p2];
		positionwrap[p2] = pv1;
		positionwrap[p1] = pv2;
	}

	/*
	 * ��������λ�õ�����
	 */
	private void wrapOneStep(int p1, int p2) {
		moveCount++;
		int pv1 = positionwrap[p1];
		int pv2 = positionwrap[p2];
		wrapPosition(p1, p2);
		if (pv2 != emptyPostion) {
			pieceViews[p1].setBackgroundDrawable(lumpDrawables[pv2]);
			pieceViews[p2].setBackgroundDrawable(emptyDrawable);

		} else {
			pieceViews[p1].setBackgroundDrawable(emptyDrawable);
			pieceViews[p2].setBackgroundDrawable(lumpDrawables[pv1]);

		}
		if (this.isShowBadge()) {
			pieceViews[p1].setText(String.valueOf(pv2));
			pieceViews[p2].setText(String.valueOf(pv1));
		}

		if (isOriginalPosition()) {
			successPuzzle();
		}

	}

	void successPuzzle() {
		pieceViews[emptyPostion]
				.setBackgroundDrawable(lumpDrawables[emptyPostion]);
		this.emptyPostion = -1;
		showSuccessToast();
	}

	void showSuccessToast() {
		String msg = MessageFormat.format(getContext().getResources()
				.getString(R.string.puzzleSuccessfull), System
				.getProperty("line.separator"), String.valueOf(getMoveCount()));
		Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
		// Toast.makeText(getContext(),
		// "fdljflds"+System.getProperty("line.separator")+"fjjfldjflds",
		// 10).show();
	}

	/*
	 * ��ʼ��Ҫ��ȾͼƬ��Ƭ��view
	 */
	@SuppressLint("ResourceAsColor") private void initLumpViews() {
		for (int i = 0; i < lumpDrawables.length; i++) {

			TextView v = new TextView(getContext());
			v.setPadding(2, 2, 2, 2);
			v.setBackgroundColor(0xffffff);
			if (scaleScreen) {
				v.setWidth(pieceWidth);
				v.setHeight(pieceHeight);
			}

			v.setGravity(Gravity.TOP | Gravity.RIGHT);
			v.setTextColor(R.color.badgeColor);
			v.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);

			pieceViews[i] = v;

		}

	}

	/*
	 * �Ƿ���ԭʼ��ƴͼ��Ƭ�����������˵��ƴͼ�ɹ�
	 */
	private boolean isOriginalPosition() {
		boolean rs = true;
		for (int i = 0; rs && i < positionwrap.length; i++) {
			if (positionwrap[i] != i) {
				rs = false;
			}
		}
		return rs;
	}

	/*
	 * ��Ⱦƴͼ�п��������
	 */
	class LumpAdapter extends BaseAdapter {

		public int getCount() {

			return columnCount * rowCount;
		}

		public Object getItem(int position) {

			return position;
		}

		public long getItemId(int position) {

			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			return pieceViews[position];
		}

	}

}
