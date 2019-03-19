package test.ssarl.fcmclient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class IntroActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "IntroActivity";
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    public SharedPreferences prefs;
    public CountDownTimer mCountDown;
    Button btn1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        btn1 = (Button) findViewById(R.id.button);
        prefs = getSharedPreferences("Pref", MODE_PRIVATE);

        // If a notification message is tapped, any data accompanying the notification
        // message is available in the intent extras. In this sample the launcher
        // intent is fired when the notification is tapped, so any accompanying data would
        // be handled here. If you want a different intent fired, set the click_action
        // field of the notification message to the desired intent. The launcher intent
        // is used when no click_action is specified.
        //
        // Handle possible data accompanying notification message.
        // [START handle_data_extras]
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
            }
        }
        // [END handle_data_extras]

        // Get token
        // [START retrieve_current_token]
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        boolean isFirstRun = prefs.getBoolean("isFirstRun", true);//처음실행할때만 데이터 전달
                        if (isFirstRun) {
                            String token = task.getResult().getToken();
                            Log.d(TAG, "GETTOKEN : " + token);
                            databaseReference.child("gettoken").push().setValue(token);//토큰 값 파이어베이스에 푸시
                            prefs.edit().putBoolean("isFirstRun", false).apply();//한번 푸시 한 이후로 다시 푸시안함 -> 삭제 후 재 다운로드(토큰값 변경)시 다시 토큰값 푸시
                        }
                    }
                });
        // [END retrieve_current_token]

        // END FCM PUSH


        // START appear token
        TextView tvv = (TextView) findViewById(R.id.tv);
        tvv.setText(MyFirebaseMessagingService.tokken);
        // END appear token


        // STRAT CountDown
        if (MyFirebaseMessagingService.trigger != null)
            mCountDown = new CountDownTimer(Integer.parseInt(MyFirebaseMessagingService.trigger) * 1000, 1000) {// 매 1초씩 시간이 흐름
                @Override
                public void onTick(long millisUntilFinished) {
                    btn1.setText("Time: " + millisUntilFinished / 1000);
                } // 매 1초씩 마다 호출

                @Override
                public void onFinish() {
                    MyFirebaseMessagingService.trigger = null;
                    Log.e(TAG, "onFinish: ");
                    ActivityCompat.finishAffinity(IntroActivity.this);
                    //android.os.Process.killProcess(android.os.Process.myPid());
                } // 받은 시간이 다 지났을 시
            }.start();
        // END CountDown


        btn1.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.button:
                if (MyFirebaseMessagingService.trigger != null) {//데이터가 있으면
                    Log.d("넘겨조라", MyFirebaseMessagingService.trigger);
                    intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("시간", MyFirebaseMessagingService.trigger);
                    mCountDown.cancel();
                    //MyFirebaseMessagingService.trigger=null;
                    startActivity(intent);
                } else {
                    intent = new Intent(getApplicationContext(), Main2Activity.class);
                    startActivity(intent);
                }
                break;
        }
    }
}
