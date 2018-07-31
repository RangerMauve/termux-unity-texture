package moe.mauve.ranger.termuxunitytexture;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.unity3d.player.UnityPlayerActivity;

/**
 * Created by Mauve on 2018-05-12.
 */

public class TerminalActivity extends UnityPlayerActivity {
    protected void onCreate(Bundle savedInstanceState) {
        // call UnityPlayerActivity.onCreate()
        super.onCreate(savedInstanceState);
        // print debug message to logcat
        Log.d("OverrideActivity", "onCreate called!");
    }
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        if(keyEvent.getAction() != KeyEvent.ACTION_UP) {
            TerminalInstance.processKey(keyEvent);
        }
        return super.dispatchKeyEvent(keyEvent);
    }
    public void onBackPressed()
    {
        // instead of calling UnityPlayerActivity.onBackPressed() we just ignore the back button event
        // super.onBackPressed();
    }
}
