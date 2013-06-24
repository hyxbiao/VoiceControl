
package com.hyxbiao.speech.ui;

import com.baidu.android.speech.SpeechConfig;
import com.baidu.android.speech.tts.TextToSpeech;
import com.baidu.android.speech.tts.UtteranceProgressListener;
import com.baidu.android.speech.ui.BaiduSpeechDialog;
import com.baidu.android.speech.ui.DialogRecognitionListener;
import com.hyxbiao.speech.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * SDK demo 测试Activity.
 */
public class MyActivity extends Activity implements OnClickListener, DialogRecognitionListener,
        UtteranceProgressListener {
    private static final String TAG = "TestActivity";

    private Handler mHandler = new Handler();

    private BaiduSpeechDialog mBaiduSpeechDialog = null;

    private TextView mTTSBodyTextView;

    private TextView mResultTextView;

    private TextView mTTSStateView;

    private WebView mWebView;

    private LocationManager mLocationManager;

    private TextToSpeech mTextToSpeech;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.test);

        // App ID: 959539
        String appKey = "CTzs3b2ZOCaVx9wKSaS116l7";
        String secretKey = "SnMBmwPGoWaS1xq39rcZ3azwnfLx09ih";
        SpeechConfig.setup(this.getApplicationContext(), appKey, secretKey);
        mResultTextView = (TextView) this.findViewById(R.id.result);
        mTTSBodyTextView = (TextView) findViewById(R.id.ttsbody);
        mTTSStateView = (TextView) this.findViewById(R.id.tts_state);
        mWebView = (WebView) this.findViewById(R.id.result_web);
        mLocationManager = LocationManager.getInstance(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        String inputString = ((EditText) this.findViewById(R.id.tts_text)).getText().toString();
        switch (id) {
            case R.id.api_invoke:
                this.startActivity(new Intent(this, APIActivity.class));
                break;
            case R.id.service_invoke:
                this.startActivity(new Intent(this, ServiceActivity.class));
                break;
            case R.id.tts_play_now_btn:
                playTTS(inputString, TextToSpeech.QUEUE_FLUSH);
                break;
            case R.id.tts_play_later_btn:
                playTTS(inputString, TextToSpeech.QUEUE_ADD);
                break;
            case R.id.stop_tts_btn:
                if (mTextToSpeech != null) {
                    mTextToSpeech.stop();
                }
                break;
            case R.id.start_command_btn:
                startSpeechDialog(BaiduSpeechDialog.SpeechMode.VOICE_TO_COMMAND);
                break;
            case R.id.start_text_btn:
                mResultTextView.setText("");
                startSpeechDialog(BaiduSpeechDialog.SpeechMode.VOICE_TO_TEXT);
            default:
                break;
        }
    }

    private void playTTS(String ttsBody, int queueMode) {
        if (!TextUtils.isEmpty(ttsBody)) {
            if (mTextToSpeech == null) {
                mTextToSpeech = new TextToSpeech(this);
                mTextToSpeech.setOnUtteranceProgressListener(this);
            }
            mTextToSpeech.speak(ttsBody, queueMode, null);
        }
    }

    private void startSpeechDialog(int startMode) {
        Bundle bundle = new Bundle();
        bundle.putString(BaiduSpeechDialog.PORMPT_TEXT, "SDK Demo");

        if (mBaiduSpeechDialog == null) {
            synchronized (this) {
                if (mBaiduSpeechDialog == null) {
                    mBaiduSpeechDialog = new BaiduSpeechDialog(this);
                    mBaiduSpeechDialog.setDialogRecognitionListener(this);
                }
            }
        }
        mBaiduSpeechDialog.setParams(startMode, bundle);
        mBaiduSpeechDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mBaiduSpeechDialog != null) {
            mBaiduSpeechDialog.setDialogRecognitionListener(null);
            mBaiduSpeechDialog.dismiss();
            mBaiduSpeechDialog = null;
        }

        if (mTextToSpeech != null) {
            mTextToSpeech.shutdown();
        }

        if (mLocationManager != null) {
            mLocationManager.release();
            mLocationManager = null;
        }
    }

    @Override
    public void onResults(Bundle results) {
        Log.d(TAG, "onResults1: " + results);
        if (results == null) {// �?���?��话识别完成后，回调到onResults的结果为null
            return;
        }

        ArrayList<String> resultList = results
                .getStringArrayList(BaiduSpeechDialog.RESULTS_RECOGNITION);
        if (resultList == null || resultList.size() < 1) {
            return;
        }
        String result = resultList.get(0);
        Log.d(TAG, "onResults2: " + result);
        if (!showWebContent(result)) {
            showCommonResult(result);
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
                        playTTS(ttsBody, TextToSpeech.QUEUE_FLUSH);
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
        mTTSBodyTextView.setVisibility(View.GONE);
        mWebView.setVisibility(View.GONE);
        mResultTextView.setVisibility(View.VISIBLE);

        mResultTextView.setText(result);
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        int i = 0;
        ArrayList<String> resultList = partialResults
                .getStringArrayList(BaiduSpeechDialog.RESULTS_RECOGNITION);
        float[] scores = partialResults.getFloatArray(BaiduSpeechDialog.CONFIDENCE_SCORES);
        String result = mResultTextView.getText().toString();
        for (String data : resultList) {
            result += data + "[" + scores[i] + "]\r\n";
            i++;
        }
        showCommonResult(result);
    }

    @Override
    public void onStart(String utteranceId) {
        showTTSState("TTS playing now.");
    }

    @Override
    public void onDone(String utteranceId) {
        showTTSState("");
    }

    @Override
    public void onError(String utteranceId) {
        showTTSState("TTS error.");
    }

    private void showTTSState(final String stateText) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mTTSStateView.setText(stateText);
            }
        });
    }
}
