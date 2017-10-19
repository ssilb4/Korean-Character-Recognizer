package org.adroidtown.automata;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Stack;

/**
 * 페인트보드에 기능 추가
 * 
 * @author Mike
 *
 */
public class GoodPaintBoard extends View {
	int shape;
	int length;
	/**
	 * chainVal과 hangul을 받기 위함
	 */
	Pattern p = new Pattern();
	/**
	 * 해당 chainVal에 대한 모음 혹은 자음 값
	 */
	String hangul = "";
	/**
	 * 좌표값을 chainVal로 리턴받은값
	 */
	String chainVal = "";
	/**
	 * xy 좌표갑 저장
	 */
	String xy = "";
	/**
	 * Undo data
	 */
	Stack undos = new Stack();

	/**
	 * Maximum Undos
	 */
	public static int maxUndos = 10;

	/**
	 * Changed flag
	 */
	public boolean changed = false;	
	
	/**
	 * Canvas instance
	 */
	Canvas mCanvas;
	
	/**
	 * Bitmap for double buffering
	 */
	Bitmap mBitmap;
	
	/**
	 * Paint instance
	 */
	final Paint mPaint;
	
	/**
	 * X coordinate
	 */
	int lastX;
	
	/**
	 * Y coordinate
	 */
	int lastY;

	
	/**
	 * Initialize paint object and coordinates
	 * 
	 * @param c
	 */
	public GoodPaintBoard(Context context) {
		super(context);
		
		// create a new paint object
		mPaint = new Paint();
		mPaint.setColor(Color.BLACK);
		mPaint.setStrokeWidth(2);
		
		lastX = -1;
		lastY = -1;

		Log.i("GoodPaintBoard", "initialized.");
		
	}

	/**
	 * Clear undo
	 */
	public void clearUndo()
	{
		while(true) {
			Bitmap prev = (Bitmap)undos.pop();
			if (prev == null) return;
			
			prev.recycle();
		}
	}	
	
	/**
	 * Save undo
	 */
	public void saveUndo()
	{
		if (mBitmap == null) return;
		
		while (undos.size() >= maxUndos){
			Bitmap i = (Bitmap)undos.get(undos.size()-1);
			i.recycle();
			undos.remove(i);
		}
		
		Bitmap img = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas();
		canvas.setBitmap(img);
		canvas.drawBitmap(mBitmap, 0, 0, mPaint);
		
		undos.push(img);
		
		Log.i("GoodPaintBoard", "saveUndo() called.");
	}
	
	/**
	 * Undo
	 */
	public void undo()
	{
		Bitmap prev = null;
		try {
			while(!undos.empty()) {
				prev = (Bitmap) undos.pop();
			}
			p = new Pattern();
			chainVal = "";
		} catch(Exception ex) {
			Log.e("GoodPaintBoard", "Exception : " + ex.getMessage());
		}
		
		if (prev != null){
			drawBackground(mCanvas);
			mCanvas.drawBitmap(prev, 0, 0, mPaint);
			invalidate();
			prev.recycle();

			//int check = 0;
			chainVal = "";
			p = new Pattern();

			//for(int i=chainVal.length()-1; i>=0; i--) {
			//	if(chainVal.charAt(i) == '0') {
			//		check = i;
			//		break;
			//	}
			//}
			//if(check < 0) {
			//	chainVal = "";
			//	//Toast.makeText(getContext(), chainVal, Toast.LENGTH_LONG).show();
			//}
			//else {
			//	chainVal = chainVal.substring(0, check);
			//	Toast.makeText(getContext(), chainVal, Toast.LENGTH_SHORT).show();
			//}
		}
		
		Log.i("GoodPaintBoard", "undo() called.");
	}	
	
	/**
	 * Paint background
	 * 
	 * @param g
	 * @param w
	 * @param h
	 */
	public void drawBackground(Canvas canvas)
	{
		if (canvas != null) {
			canvas.drawColor(Color.WHITE);
		}
	}	
	
	/**
	 * Update paint properties
	 * 
	 * @param canvas
	 */
	public void updatePaintProperty(int color, int size)
	{
		mPaint.setColor(color);
		mPaint.setStrokeWidth(size);
	}	
	
	/**
	 * Create a new image
	 */
	public void newImage(int width, int height)
	{
		Bitmap img = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas();
		canvas.setBitmap(img);
		
		mBitmap = img;
		mCanvas = canvas;

		drawBackground(mCanvas);
		
		changed = false;
		invalidate();
	}	
	
	/**
	 * Set image
	 * 
	 * @param newImage
	 */
	public void setImage(Bitmap newImage)
	{
		changed = false;
		
		setImageSize(newImage.getWidth(),newImage.getHeight(),newImage);
		invalidate();
	}	
	
	/**
	 * Set image size
	 * 
	 * @param width
	 * @param height
	 * @param newImage
	 */
	public void setImageSize(int width, int height, Bitmap newImage)
	{
		if (mBitmap != null){
			if (width < mBitmap.getWidth()) width = mBitmap.getWidth();
			if (height < mBitmap.getHeight()) height = mBitmap.getHeight();
		}
		
		if (width < 1 || height < 1) return;
		
		Bitmap img = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas();
		drawBackground(canvas);
		
		if (newImage != null) {
			canvas.setBitmap(newImage);
		}
		
		if (mBitmap != null) {
			mBitmap.recycle();
			mCanvas.restore();
		}

		mBitmap = img;
		mCanvas = canvas;
		
		clearUndo();
	}
	
	
	
	/**
	 * onSizeChanged
	 */
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		if (w > 0 && h > 0) {
			newImage(w, h);
		}
	}

	/**
	 * Draw the bitmap
	 */
	protected void onDraw(Canvas canvas) {
		if (mBitmap != null) {
			canvas.drawBitmap(mBitmap, 0, 0, null);
		}
	}

	/**
	 * Handles touch event, UP, DOWN and MOVE
	 */
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		int X = (int) event.getX();
		int Y = (int) event.getY();


		switch (action) {
			case MotionEvent.ACTION_UP:
				changed = true;


				int t_X = lastX;
				int t_Y = lastY;
				// reset coordinates
				lastX = -1;
				lastY = -1;
				xy = xy + "(" + lastX + "," + lastY + ")";


				try {
					/**
					 * 버튼을 뗐을 때 xy값에 해당하는 chainVal 받음
					 */
					if(!xy.equals("(" + t_X + "," + t_Y + ")" + "(-1,-1)")) {
						chainVal = chainVal + "0" + p.makeChainCode(xy);
					}
					shape = p.shape;
					length = p.length_check;
				} catch (IOException e) {
					Toast.makeText(getContext(), "에러발생", Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
				Toast.makeText(getContext(), chainVal, Toast.LENGTH_SHORT).show();
				break;
	
			case MotionEvent.ACTION_DOWN:
				saveUndo();
				/**
				 * 좌표값 초기화
				 */
				xy = "";
				// draw line with the coordinates
				if (lastX != -1) {
					if (X != lastX || Y != lastY) {
						mCanvas.drawLine(lastX, lastY, X, Y, mPaint);
					}
				}

				// set the last coordinates
				lastX = X;
				lastY = Y;
				xy = xy + "(" + lastX + "," + lastY + ")";
				
				break;
	
			case MotionEvent.ACTION_MOVE:
				// draw line with the coordinates
				if (lastX != -1) {
					mCanvas.drawLine(lastX, lastY, X, Y, mPaint);
				}
	
				lastX = X;
				lastY = Y;
				xy = xy + "(" + lastX + "," + lastY + ")";
				break;
		}

		// repaint the screen
		invalidate();

		return true;
	}

	/**
	 * Save this contents into a Jpeg image
	 * 
	 * @param outstream
	 * @return
	 */
	public boolean Save(OutputStream outstream) {
		try {
			mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outstream);
			invalidate();
			
			return true;
		} catch (Exception e) {
			return false;
		}
	}


}
