package com.hyxbiao.voicecontrol.ui;

import com.hyxbiao.voicecontrol.client.R;
import com.hyxbiao.voicecontrol.command.VoiceCommandManager;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
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
    private int _xDelta;
    private int _yDelta;
    
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
		mCenterCursorParams = (LayoutParams) mButtonCursor.getLayoutParams();
		Log.d(TAG, "button position: " + mCenterCursorParams.leftMargin + "," + mCenterCursorParams.topMargin);
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
	    final int X = (int) event.getRawX();
	    final int Y = (int) event.getRawY();
	    switch (event.getAction() & MotionEvent.ACTION_MASK) {
	        case MotionEvent.ACTION_DOWN:
	            RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) mButtonCursor.getLayoutParams();
	            _xDelta = X - lParams.leftMargin;
	            _yDelta = Y - lParams.topMargin;
	            break;
	        case MotionEvent.ACTION_UP:
	            break;
	        case MotionEvent.ACTION_POINTER_DOWN:
	            break;
	        case MotionEvent.ACTION_POINTER_UP:
	            break;
	        case MotionEvent.ACTION_MOVE:
	            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mButtonCursor.getLayoutParams();
	            layoutParams.leftMargin = X - _xDelta;
	            layoutParams.topMargin = Y - _yDelta;
	            mButtonCursor.setLayoutParams(layoutParams);
	            break;
	    }
	    return true;
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
