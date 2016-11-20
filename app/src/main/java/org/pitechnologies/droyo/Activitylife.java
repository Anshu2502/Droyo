package org.pitechnologies.droyo;

/**
 * Created by Pitech09 on 4/5/2016.
 */

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

public class Activitylife extends Activity {
    Button b1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        tToast("onCreate");

    }

    public void onStart() {
        super.onStart();
        tToast("onStart");
    }

    public void onRestart() {
        super.onRestart();
        tToast("onRestart");
    }

    public void onResume() {
        super.onResume();
        tToast("onResume");
    }

    public void onPause() {
        super.onPause();
        tToast("onPause: bye bye!");
    }

    public void onStop() {
        super.onStop();
        tToast("onStop.");
    }



    private void tToast(String s) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, s, duration);
        toast.show();
    }
}
