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

import java.util.HashMap;

public class GamePractice1ModeSetting extends AppCompatActivity {
    private Spinner mGridSizeSpinner, practice1_mode_direction_spinner;
    private RadioGroup mHandRadioGroup;
    private EditText mGameTimeEditText;

    private int mGridSize;
    private int iDirection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_practice1_mode_setting);
        mHandRadioGroup = findViewById(R.id.hand_radio_group);
        mGameTimeEditText = findViewById(R.id.game_time_edittext);

        findViewById(R.id.mole_practice1_start_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioButton handRadioButton = findViewById(mHandRadioGroup.getCheckedRadioButtonId());
                String hand = (handRadioButton.getText().equals("오른손")) ? "right_hand" : "left_hand";


                Intent intent = new Intent(GamePractice1ModeSetting.this, GamePractice1Mode.class);
//        intent.putExtra("goalCnt", goalCnt);
                intent.putExtra("splitScreenCnt", mGridSize);
                intent.putExtra("direction", iDirection);
                intent.putExtra("gameTimeSec", Integer.parseInt(mGameTimeEditText.getText().toString()));
                intent.putExtra("hand", hand);
                startActivity(intent);
            }
        });

        mGridSizeSpinner = findViewById(R.id.half_mode_grid);
        practice1_mode_direction_spinner = findViewById(R.id.practice1_mode_direction_spinner);

        ArrayAdapter gridSizeAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.grid_size,
                R.layout.spinner_item
//                android.R.layout.simple_spinner_dropdown_item
        );
        gridSizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mGridSizeSpinner.setAdapter(gridSizeAdapter);

        mGridSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String selectedItem = mGridSizeSpinner.getSelectedItem().toString();

                if (selectedItem.equals("3*3")) mGridSize = 3;
                else if (selectedItem.equals("5*5")) mGridSize = 5;
                else if (selectedItem.equals("7*7")) mGridSize = 7;
                else mGridSize = 9;

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        // 방향 스피너
        String[] directionKeys = getResources().getStringArray(R.array.practice1_direction_key);
        int[] directionValues = getResources().getIntArray(R.array.practice1_direction_value);
        HashMap<String, Integer> directions = new HashMap<String, Integer>();
        for (int i = 0; i < directionKeys.length; i++) {
            directions.put(directionKeys[i], directionValues[i]);
        }

        ArrayAdapter directionAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.practice1_direction_key,
                R.layout.spinner_item
//                android.R.layout.simple_spinner_dropdown_item
        );
        directionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        practice1_mode_direction_spinner.setAdapter(directionAdapter);

        practice1_mode_direction_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String selectedItem = practice1_mode_direction_spinner.getSelectedItem().toString();
                iDirection = directions.get(selectedItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

    }
}