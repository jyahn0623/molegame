package co.kr.molegame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MoleGameResult extends AppCompatActivity {

    private TextView grid_size_textview,
            pane_cnt_textview,
            catched_mole_textview,
            game_result_textview,
            game_result_text_textview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mole_game_result);

        // init View
        grid_size_textview = findViewById(R.id.grid_size_textview);
        pane_cnt_textview = findViewById(R.id.pane_cnt_textview);
        catched_mole_textview = findViewById(R.id.catched_mole_textview);
        game_result_textview = findViewById(R.id.game_result_textview);
        game_result_text_textview = findViewById(R.id.game_result_text_textview);


        Intent intent = getIntent();
        String action = intent.getStringExtra("action");
        int time = intent.getIntExtra("time", 0);
        int paneCount = intent.getIntExtra("pane", 0);
        int grid_size = intent.getIntExtra("grid_size", 3);
        int goal_count = intent.getIntExtra("goal_count", 5);

        grid_size_textview.setText(String.valueOf(grid_size));
        pane_cnt_textview.setText(String.valueOf(paneCount));
        catched_mole_textview.setText(String.valueOf(goal_count));

        switch (action){
            case "GAME_OVER":
                game_result_textview.setText("게임 클리어에 실패했습니다.");
                game_result_text_textview.setText("다시 시작하여 성공해 보시는 건 어떨까요?");
                break;
            case "GAME_COMPLETE":
                game_result_textview.setText("게임을 클리어 했습니다!");
                game_result_text_textview.setText("정말 대단한 플레이였습니다!");
                break;
        }

        findViewById(R.id.mole_result_restartBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }
}