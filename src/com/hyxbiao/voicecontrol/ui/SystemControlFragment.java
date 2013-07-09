package com.hyxbiao.voicecontrol.ui;

import com.hyxbiao.voicecontrol.client.R;
import com.hyxbiao.voicecontrol.command.VoiceCommandManager;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class SystemControlFragment extends Fragment implements OnClickListener, OnTouchListener {
	
	private final static String TAG = "SystemControlFragment";
	private SurfaceHolder surfaceHolder;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    
    private Button mButtonCursor = null;
    private RelativeLayout.LayoutParams mCenterCursorParams = null;
    private int mLastX;
    private int mLastY;
    private boolean mIsGetRawPos = false;
    private Rect mButtonCursorRawPos = new Rect();
    private int mMaxSize = 40;
    
	public SystemControlFragment() { 
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getActivity().setTitle(getResources().getString(R.string.system_control_title));
		View v = inflater.inflate(R.layout.system_content, null);
		
//		surfaceHolder = ((SurfaceView)v).getHolder();
//		v.setOnTouchListener(this);
		
		v.findViewById(R.id.btn_exit).setOnClickListener(this);
		v.findViewById(R.id.btn_back).setOnClickListener(this);
		v.findViewById(R.id.btn_volume_down).setOnClickListener(this);
		v.findViewById(R.id.btn_volume_up).setOnClickListener(this);
		
		mButtonCursor = (Button) v.findViewById(R.id.btn_cursor);
		mButtonCursor.setOnTouchListener(this);
		mButtonCursor.setOnClickListener(this);
//		mCenterCursorParams = (LayoutParams) mButtonCursor.getLayoutParams();
//		mCenterCursorParams.addRule(RelativeLayout.CENTER_VERTICAL);
//		Log.d(TAG, "button position1: " + mCenterCursorParams.leftMargin + "," + mCenterCursorParams.topMargin);
//		mButtonCursor.setLayoutParams(mCenterCursorParams);
//		mCenterCursorParams = (LayoutParams) mButtonCursor.getLayoutParams();
//		Log.d(TAG, "button position2: " + mCenterCursorParams.leftMargin + "," + mCenterCursorParams.topMargin);
//
//		Log.d(TAG, "1left:" + mButtonCursor.getLeft());
//		Log.d(TAG, "1right:" + mButtonCursor.getRight());
//		Log.d(TAG, "1top:" + mButtonCursor.getTop());
//		Log.d(TAG, "1bottom:" + mButtonCursor.getBottom());
//		return new DrawingView(getActivity().getApplicationContext());
		return v;
	}
	

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onClick(View v) {
		VoiceCommandManager commandManager = ((MyApp) getActivity().getApplicationContext()).getCommandManager();
		int id = v.getId();
        switch (id) {
	        case R.id.btn_exit:
	        	commandManager.execute(getString(R.string.system_home));
	        	break;
	        case R.id.btn_back:
	        	commandManager.execute(getString(R.string.system_back));
	        	break;
	        case R.id.btn_volume_down:
	        	commandManager.execute(getString(R.string.system_volume_down));
	        	break;
	        case R.id.btn_volume_up:
	        	commandManager.execute(getString(R.string.system_volume_up));
	        	break;
        };
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
//		if(event.getAction() == MotionEvent.ACTION_DOWN) {
//            if (surfaceHolder.getSurface().isValid()) {
//                Canvas canvas = surfaceHolder.lockCanvas();
//                canvas.drawColor(Color.BLACK);
//                canvas.drawCircle(event.getX(), event.getY(), 50, paint);
//                surfaceHolder.unlockCanvasAndPost(canvas);
//            }
//        }
//		return false;
		if(!mIsGetRawPos) {
			mButtonCursorRawPos.left = v.getLeft();
			mButtonCursorRawPos.right = v.getRight();
			mButtonCursorRawPos.top = v.getTop();
			mButtonCursorRawPos.bottom = v.getBottom();
			mIsGetRawPos = true;
		}

	    final int X = (int) event.getRawX();
	    final int Y = (int) event.getRawY();
//	    Log.d(TAG, "onTouch, x:" + X + ", y:" + Y);
	    switch (event.getAction() & MotionEvent.ACTION_MASK) {
	        case MotionEvent.ACTION_DOWN:
	            mLastX = X;
	            mLastY = Y;
	            Log.d(TAG, "onTouch Down, x:" + X + ", y:" + Y);
	            break;
	        case MotionEvent.ACTION_UP:
	        	v.layout(mButtonCursorRawPos.left, mButtonCursorRawPos.top, 
	        			mButtonCursorRawPos.right, mButtonCursorRawPos.bottom);
	            break;
	        case MotionEvent.ACTION_POINTER_DOWN:
	            break;
	        case MotionEvent.ACTION_POINTER_UP:
	            break;
	        case MotionEvent.ACTION_MOVE:
	            int dx = X - mLastX;
	            int dy = Y - mLastY;
	            if(dx > mButtonCursorRawPos.left + mMaxSize - v.getLeft()) {
	            	dx = mButtonCursorRawPos.left + mMaxSize - v.getLeft();
	            } else if(dx < mButtonCursorRawPos.left - mMaxSize - v.getLeft()) {
	            	dx = mButtonCursorRawPos.left - mMaxSize - v.getLeft();
	            }
	            if(dy > mButtonCursorRawPos.top + mMaxSize - v.getTop()) {
	            	dy = mButtonCursorRawPos.top + mMaxSize - v.getTop();
	            } else if(dy < mButtonCursorRawPos.top - mMaxSize - v.getTop()) {
	            	dy = mButtonCursorRawPos.top - mMaxSize - v.getTop();
	            }
	            v.layout(v.getLeft()+dx, v.getTop()+dy, v.getRight()+dx, v.getBottom()+dy);
	            mLastX = X;
	            mLastY = Y;
	            Log.d(TAG, "onTouch Move, x:" + X + ", y:" + Y);
	            break;
	    }
	    return false;
	}
	
	class DrawingView extends SurfaceView {

	    private final SurfaceHolder surfaceHolder;
	    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

	    public DrawingView(Context context) {
	        super(context);
	        surfaceHolder = getHolder();
	        paint.setColor(Color.RED);
	        paint.setStyle(Style.FILL);
	    }

	    @Override
	    public boolean onTouchEvent(MotionEvent event) {
	        if(event.getAction() == MotionEvent.ACTION_DOWN) {
	            if (surfaceHolder.getSurface().isValid()) {
	                Canvas canvas = surfaceHolder.lockCanvas();
	                canvas.drawColor(Color.BLACK);
	                canvas.drawCircle(event.getX(), event.getY(), 50, paint);
	                surfaceHolder.unlockCanvasAndPost(canvas);
	            }
	        }
	        return false;
	    }

	}
}
	
