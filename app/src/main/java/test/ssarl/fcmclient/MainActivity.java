package test.ssarl.fcmclient;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        Log.d("나와라..", intent.getExtras().getString("시간"));

        int time = Integer.parseInt(intent.getExtras().getString("시간"));
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                getIntent().getExtras().clear();
                ActivityCompat.finishAffinity(MainActivity.this);
            }
        }, time * 1000);

    }


    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }
}
