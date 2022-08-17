package co.kr.molegame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class GameMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_menu);
    }

    /**
     * 랜덤모드로 이동
     */
    public void onClickedRandomMode(View v){
        Intent intent = new Intent(GameMenuActivity.this, MoleGameSetting.class);
        startActivity(intent);
    }

    /**
     * 훈련모드로 이동
     */
    public void onClickedPracticeMode(View v){
        Intent intent = new Intent(GameMenuActivity.this, PracticeModeMenu.class);
        startActivity(intent);
    }
}