package co.kr.molegame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

public class GameHalfRandomModeSetting extends AppCompatActivity {
    private Spinner mGridSizeSpinner;
    private RadioGroup mHandRadioGroup;
    private EditText mGameTimeEditText;

    private int mGridSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_half_random_mode_setting);
        mHandRadioGroup = findViewById(R.id.half_mode_radio);
        mGameTimeEditText = findViewById(R.id.game_time_edittext);

        mGridSizeSpinner = findViewById(R.id.half_mode_grid);
        ArrayAdapter gridSizeAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.grid_size,
                R.layout.spinner_item
//            android.R.layout.simple_spinner_dropdown_item
        );
        gridSizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mGridSizeSpinner.setAdapter(gridSizeAdapter);

        mGridSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String selectedItem = mGridSizeSpinner.getSelectedItem().toString();

                if(selectedItem.equals("3*3")) mGridSize = 3;
                else if(selectedItem.equals("5*5")) mGridSize = 5;
                else if(selectedItem.equals("7*7")) mGridSize = 7;
                else mGridSize = 9;

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

    }



    public void onClickedStartGame(View v){
        RadioButton handRadioButton = findViewById(mHandRadioGroup.getCheckedRadioButtonId());
        String hand = (handRadioButton.getText().equals("오른손")) ? "right_hand" : "left_hand";


        Intent intent = new Intent(GameHalfRandomModeSetting.this,GameHalfRandomMode.class);
        intent.putExtra("splitScreenCnt",mGridSize);
        intent.putExtra("gameTimeSec",Integer.parseInt(mGameTimeEditText.getText().toString()));
        intent.putExtra("hand", hand);
        startActivity(intent);
    }
}