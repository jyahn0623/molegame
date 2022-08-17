package co.kr.molegame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MoleGameSetting extends AppCompatActivity {

    MediaPlayer mediaPlayer;
    boolean mediaPlayerRelease;

    private void initMediaPlayer() {
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.voice_mole_1);
        mediaPlayerRelease = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*
        앱의 기본 시작 로직
        EX) 데이터를 목록에 바인딩, 활동을 ViewModel에 연결, 클래스 범위 변수 인스턴스화
        savedInstanceState는 활동의 이전 저장 상태가 포함된 것으로, 처음 생성된 것이라면 null임
         */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mole_game_setting);
        initMediaPlayer();

        EditText goalCntEditText = findViewById(R.id.goal_cnt_edit_text);
        EditText splitScreenCntEditText = findViewById(R.id.sprit_screen_cnt_edit_text);
        EditText gameTimeEditText = findViewById(R.id.game_time_edittext);

        findViewById(R.id.mole_setting_restartBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int goalCnt = Integer.parseInt(goalCntEditText.getText().toString());
                int spritScreenCnt = Integer.parseInt(splitScreenCntEditText.getText().toString());
                int gameTimeSec = Integer.parseInt(gameTimeEditText.getText().toString());
                if (goalCnt <= 0)
                    Toast.makeText(getApplicationContext(), "목표개수를 1이상으로 입력해주세요.", Toast.LENGTH_LONG).show();

                else if (spritScreenCnt < 3 || spritScreenCnt > 10)
                    Toast.makeText(getApplicationContext(), "3이상 10이하 값으로 입력해주세요.", Toast.LENGTH_LONG).show();
                else if (gameTimeSec < 3)
                    Toast.makeText(getApplicationContext(), "게임시간은 3초이상으로 입력해주세요.", Toast.LENGTH_LONG).show();
                else {
                    Intent intent = new Intent(MoleGameSetting.this, MoleGameActivity.class);
                    intent.putExtra("goalCnt", goalCnt);
                    intent.putExtra("splitScreenCnt", spritScreenCnt);
                    intent.putExtra("gameTimeSec", gameTimeSec);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!mediaPlayerRelease) {
                    // release가 되지 않았을 경우에만 재상
                    // release가 되지 않은 경우는 초기 액티비티 생성 시만.
                    mediaPlayer.start();
                }
            }
        }, 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayerRelease = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}