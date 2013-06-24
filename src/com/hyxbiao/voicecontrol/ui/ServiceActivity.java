
package com.hyxbiao.voicecontrol.ui;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.hyxbiao.voicecontrol.R;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ServiceActivity extends Activity implements OnClickListener, OnInitListener {
    private static final String TAG = "ServiceActivity";

    private TextView mErrorView;

    private TextView mVolumeView;

    private TextView mResultTextView;

    private WebView mWebView;

    private TextView mTTSBodyTextView;

    private android.speech.SpeechRecognizer mSpeechRecognizer;

    private TextToSpeech mTTS;

    private boolean mTTSEnable;

    MyRecognitionListener mMyRecognitionListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_layout);

        this.findViewById(R.id.start_command_btn).setOnClickListener(this);
        this.findViewById(R.id.start_text_btn).setOnClickListener(this);

        mErrorView = (TextView) this.findViewById(R.id.error);
        mVolumeView = (TextView) this.findViewById(R.id.volume);
        mResultTextView = (TextView) this.findViewById(R.id.result);
        mTTSBodyTextView = (TextView) findViewById(R.id.ttsbody);
        mWebView = (WebView) this.findViewById(R.id.result_web);

        ComponentName component = new ComponentName("com.baidu.voiceassistant",
                "com.baidu.voiceassistant.service.BaiduRecognitionService");
        mSpeechRecognizer = android.speech.SpeechRecognizer.createSpeechRecognizer(this, component);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            mTTS = new TextToSpeech(getApplicationContext(), this, "com.baidu.voiceassistant");
        } else {
            Toast.makeText(this, R.string.tts_error, Toast.LENGTH_SHORT).show();
        }
        mMyRecognitionListener = new MyRecognitionListener();
        mSpeechRecognizer.setRecognitionListener(mMyRecognitionListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSpeechRecognizer.destroy();
    }

    private class MyRecognitionListener implements android.speech.RecognitionListener {
        @Override
        public void onReadyForSpeech(Bundle params) {
            mResultTextView.setText("ready for speech");
        }

        @Override
        public void onBeginningOfSpeech() {
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
            if (results == null) {// 最后一句话识别完成后，回调到onResults的结果为null
                return;
            }
            ArrayList<String> resultList = results
                    .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if (resultList == null || resultList.size() < 1) {
                return;
            }
            String result = resultList.get(0);
            if (!showWebContent(result)) {
                showCommonResult(result);
            }
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
            int i = 0;
            ArrayList<String> resultList = partialResults
                    .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            float[] scores = partialResults.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES);
            if (resultList == null) {
                return;
            }
            String result = mResultTextView.getText().toString();
            for (String data : resultList) {
                result += data + "[" + scores[i] + "]\r\n";
                i++;
            }
            showCommonResult(result);
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        int id = v.getId();
        switch (id) {
            case R.id.start_command_btn:
                intent.putExtra("com.baidu.voiceassistant.extras.RECOGNITION_MODE", 2);
                mSpeechRecognizer.startListening(intent);
                break;
            case R.id.start_text_btn:
                mSpeechRecognizer.startListening(intent);
                break;
            default:
                break;
        }
    }

    private boolean showWebContent(String result) {
        try {
            JSONObject jsonContent = new JSONObject(result);
            String command_str = jsonContent.getString("command_str");
            JSONObject jsonCommands = new JSONObject(command_str);
            JSONArray jsonCommandArray = jsonCommands.getJSONArray("commandlist");
            JSONObject oneCommand = jsonCommandArray.getJSONObject(0);
            String ttsBody = oneCommand.optString("ttsbody");
            playTTS(ttsBody);
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

    private void playTTS(String ttsbody) {
        if (mTTSEnable && !TextUtils.isEmpty(ttsbody)) {
            mTTS.speak(ttsbody, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @Override
    public void onInit(int status) {
        mTTSEnable = status == TextToSpeech.SUCCESS;
    }
}
