
package com.hyxbiao.voicecontrol.ui;

import com.baidu.android.speech.RecognitionListener;
import com.baidu.android.speech.SpeechRecognizer;
import com.hyxbiao.voicecontrol.client.R;
import com.hyxbiao.voicecontrol.command.VoiceCommandManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.TextView;

import java.util.ArrayList;

public class APIActivity extends Activity implements OnClickListener {
    private static final String TAG = "APIActivity";

    private TextView mErrorView;

    private TextView mVolumeView;

    private TextView mResultTextView;

    private WebView mWebView;

    private TextView mTTSBodyTextView;

    MyRecognitionListener mMyRecognitionListener;

    private String RESULTS_KEY = SpeechRecognizer.EXTRA_RESULTS_RECOGNITION;

    private String PARTIAL_RESULTS_KEY = SpeechRecognizer.EXTRA_RESULTS_RECOGNITION;

    private String PARTIAL_SCORES_KEY = SpeechRecognizer.EXTRA_CONFIDENCE_SCORES;

    private SpeechRecognizer mSpeechRecognizer;
    
    private VoiceCommandManager  mVideoManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.api_layout);

        this.findViewById(R.id.start_command_btn).setOnClickListener(this);
        this.findViewById(R.id.start_text_btn).setOnClickListener(this);
        this.findViewById(R.id.start_commandservice_btn).setVisibility(View.GONE);// .setOnClickListener(this);
        this.findViewById(R.id.start_textservice_btn).setVisibility(View.GONE);// .setOnClickListener(this);
        this.findViewById(R.id.stop_btn).setOnClickListener(this);

        mErrorView = (TextView) this.findViewById(R.id.error);
        mVolumeView = (TextView) this.findViewById(R.id.volume);
        mResultTextView = (TextView) this.findViewById(R.id.result);
        mTTSBodyTextView = (TextView) findViewById(R.id.ttsbody);
        mWebView = (WebView) this.findViewById(R.id.result_web);

        mMyRecognitionListener = new MyRecognitionListener();
        mSpeechRecognizer = SpeechRecognizer.getInstance(this);
        mSpeechRecognizer.setRecognitionListener(mMyRecognitionListener);

        mVideoManager = ((MyApp)getApplicationContext()).getCommandManager();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSpeechRecognizer.destroy();
    }

    private class MyRecognitionListener implements RecognitionListener,
            android.speech.RecognitionListener {
    	
    	private ArrayList<String> mResultList;
    	public MyRecognitionListener() {
    		mResultList = new ArrayList<String>();
    	}
        @Override
        public void onReadyForSpeech(Bundle params) {
            mResultTextView.setText("ready for speech");
        }

        @Override
        public void onBeginningOfSpeech() {
        	mResultList.clear();
            mResultTextView.setText("begin speech...");
        }

        @Override
        public void onRmsChanged(float rmsdB) {
            mVolumeView.setText("volume: " + rmsdB);
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
        }

        @Override
        public void onEndOfSpeech() {
            mResultTextView.setText("end speech");
        }

        @Override
        public void onError(int error) {
            mErrorView.setText("error: " + error);
        }

        @Override
        public void onResults(Bundle results) {
        	Log.d(TAG, "result: " + results);
            if (results == null) {// 最后一句话识别完成后，回调到onResults的结果为null
                return;
            }
            ArrayList<String> resultList = results.getStringArrayList(RESULTS_KEY);
            if (resultList == null || resultList.size() < 1) {
                return;
            }
            String result = resultList.get(0);
//            if (!showWebContent(result)) {
//                showCommonResult(result);
//            }
            Log.d(TAG, "all partial result: " + mResultList);
//            mVideoManager.execute(mResultList);
            mVideoManager.execute(result);
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
        	Log.d(TAG, "partialResult: " + partialResults);
            int i = 0;
            ArrayList<String> resultList = partialResults.getStringArrayList(PARTIAL_RESULTS_KEY);
            float[] scores = partialResults.getFloatArray(PARTIAL_SCORES_KEY);
            if (resultList == null) {
                return;
            }
            String result = mResultTextView.getText().toString();
            for (String data : resultList) {
            	String res = data + "[" + scores[i] + "]\r\n";
                result += res;
                Log.d(TAG, res);
                i++;
            }
            showCommonResult(result);
            mResultList.addAll(resultList);
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
        	Log.d(TAG, "[onEvent]eventType: " + eventType + ", params: " + params);
        }
    }

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();

        int id = v.getId();
        switch (id) {
            case R.id.start_command_btn:
                mSpeechRecognizer.startListening(SpeechRecognizer.SpeechMode.VOICE_TO_COMMAND,
                        bundle);
                break;
            case R.id.start_text_btn:
                mSpeechRecognizer.startListening(SpeechRecognizer.SpeechMode.VOICE_TO_TEXT, bundle);
                break;
            case R.id.stop_btn:
                mSpeechRecognizer.stopListening();
                showCommonResult("");
                break;
            default:
                break;
        }
    }

    private boolean showWebContent(String result) {
        if (!TextUtils.isEmpty(result)) {
            try {
                JSONObject jsonContent = new JSONObject(result);
                String command_str = jsonContent.getString("command_str");
                JSONObject jsonCommands = new JSONObject(command_str);
                JSONArray jsonCommandArray = jsonCommands.getJSONArray("commandlist");
                JSONObject oneCommand = jsonCommandArray.getJSONObject(0);
                String ttsBody = oneCommand.optString("ttsbody");
                if (oneCommand.has("commandcontent")) {
                    JSONObject commandContent = oneCommand.optJSONObject("commandcontent");
                    if (commandContent != null) {
                        if (commandContent.has("web")) {
                            String url = commandContent.optString("baseurl");
                            String content = commandContent.optString("web");
                            mResultTextView.setVisibility(View.GONE);
                            mWebView.setVisibility(View.VISIBLE);
                            mTTSBodyTextView.setVisibility(View.VISIBLE);

                            mWebView.loadDataWithBaseURL(url, content, "text/html", "utf-8", null);
                            mTTSBodyTextView.setText(ttsBody);
                            return true;
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void showCommonResult(String result) {
        mErrorView.setText("");
        mVolumeView.setText("");
        mTTSBodyTextView.setVisibility(View.GONE);
        mWebView.setVisibility(View.GONE);
        mResultTextView.setVisibility(View.VISIBLE);

        mResultTextView.setText(result);
    }
}
