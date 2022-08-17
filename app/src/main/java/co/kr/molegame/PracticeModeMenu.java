package co.kr.molegame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class PracticeModeMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_mode_menu);

    }

    /**
     * practice1
     */
    public void onClickedPractice1Mode(View v){
        Intent intent = new Intent(PracticeModeMenu.this, GamePractice1ModeSetting.class);
        startActivity(intent);
    }

    /**
     * 반랜덤 모드
     */
    public void onClickedHalfRandomMode(View v){
        Intent intent = new Intent(PracticeModeMenu.this, GameHalfRandomModeSetting.class);
        startActivity(intent);
    }
}