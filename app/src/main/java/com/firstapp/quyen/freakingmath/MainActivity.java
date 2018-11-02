package com.firstapp.quyen.freakingmath;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnExit;
    Button btnExitYes;
    Button btnExitNo;
    Button btnOptions;
    Button btnStart;
    Button btnScore;
    ImageView imgTitle;
    ImageView imgBulb;
    ImageView imgNumbers;
    TextView txtFirstScore;
    TextView txtSecondScore;
    TextView txtThirdScore;
    TextView txtFourthScore;
    TextView txtFifthScore;
    private static MediaPlayer mediaPlayer;
    Dialog exitAppDialog;
    private static final int REQUEST_CODE_OPTIONS = 1;
    private static final int REQUEST_CODE_GAME = 2;
    private boolean databaseInsertDone = false;
    RelativeLayout ScreenMainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnExit = findViewById(R.id.button_Exit);
        btnOptions = findViewById(R.id.button_Options);
        btnStart = findViewById(R.id.button_Start);
        btnScore = findViewById(R.id.button_Score);
        ScreenMainLayout = findViewById(R.id.screen_main_layout);

        btnExit.setOnClickListener(this);
        btnOptions.setOnClickListener(this);
        btnStart.setOnClickListener(this);
        btnScore.setOnClickListener(this);
        imgTitle = findViewById(R.id.imgView_FreakingMath);
        imgBulb = findViewById(R.id.imgView_Bulb);
        imgNumbers = findViewById(R.id.imgView_Numbers);

        SharedPreferences sharedPreferences = getSharedPreferences("sharedOptions", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.commit();
        databaseInsertDone = sharedPreferences.getBoolean("databaseInsertDone",false);
        ScoreSaveHelper scoreSaveHelper = new ScoreSaveHelper(MainActivity.this);
        if(!databaseInsertDone){
            scoreSaveHelper.insertValue(0);
            scoreSaveHelper.insertValue(0);
            scoreSaveHelper.insertValue(0);
            scoreSaveHelper.insertValue(0);
            scoreSaveHelper.insertValue(0);
            databaseInsertDone = true;
            editor.putBoolean("databaseInsertDone",true);
            editor.commit();
        }

        ScreenMainLayout.setBackgroundColor(ContextCompat.getColor(MainActivity.this,sharedPreferences.getInt("color_options", R.color.colorWhite)));
        resetResourceImageView(sharedPreferences.getInt("color_options", R.color.colorWhite));
        playMusic(sharedPreferences.getInt("music_options",-1));

    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.button_Exit:
                displayExitDialog();
                break;
            case R.id.button_Options:
                intent = new Intent(MainActivity.this, Screen_Options.class);
                startActivityForResult(intent, REQUEST_CODE_OPTIONS);
                break;
            case R.id.button_Start:
                intent = new Intent(MainActivity.this, Screen_Game.class);
                startActivityForResult(intent, REQUEST_CODE_GAME);
                break;
            case R.id.button_Score:
                showScoreDialog();
                break;
        }
    }

    public void playMusic(int song){
        if(mediaPlayer == null){
            if(song != -1) {
                mediaPlayer = MediaPlayer.create(MainActivity.this, song );
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.start();
                    }
                });
                mediaPlayer.start();
            }
        }
    }

    public static void stopMusic(){
        if(mediaPlayer != null){
            mediaPlayer.stop();
        }
    }

    public void showScoreDialog(){
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_highscore_layout);
        showScores(dialog);
        dialog.show();
    }

    public void showScores(Dialog dialog){
        txtFirstScore = dialog.findViewById(R.id.textView_FirstScore);
        txtSecondScore = dialog.findViewById(R.id.textView_SecondScore);
        txtThirdScore = dialog.findViewById(R.id.textView_ThirdScore);
        txtFourthScore = dialog.findViewById(R.id.textView_FourthScore);
        txtFifthScore = dialog.findViewById(R.id.textView_FifthScore);

        ArrayList<TextView> arrayListScore = new ArrayList<>(Arrays.asList(txtFirstScore,txtSecondScore,txtThirdScore,txtFourthScore,txtFifthScore));
        ScoreSaveHelper scoreSaveHelper = new ScoreSaveHelper(MainActivity.this);
        Cursor cursor = scoreSaveHelper.getScoresList();
        int i = 0;
        if(cursor.moveToFirst()){
            do{
                arrayListScore.get(i).setText((i+1) + ". " + cursor.getInt(1));
                i++;
            }while (cursor.moveToNext());
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
    }

    public void displayExitDialog(){
        exitAppDialog = new Dialog(MainActivity.this);
        exitAppDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        exitAppDialog.setContentView(R.layout.exit_app_dialog_layout);
        exitAppDialog.setCanceledOnTouchOutside(false);
        btnExitYes = exitAppDialog.findViewById(R.id.button_Yes);
        btnExitNo = exitAppDialog.findViewById(R.id.button_No);
        btnExitYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        });
        btnExitNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitAppDialog.dismiss();
            }
        });
        exitAppDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       // Toast.makeText(MainActivity.this,"requestCode: " + requestCode + ", resultCode: "+resultCode,Toast.LENGTH_SHORT).show();
        if(requestCode == REQUEST_CODE_OPTIONS && resultCode == RESULT_OK && data != null){
            Bundle bundle = data.getBundleExtra("dataFromOptionsScreen");
            if(bundle != null){
                SharedPreferences sharedPreferences = getSharedPreferences("sharedOptions", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("color_options",bundle.getInt("color_options"));
                editor.putInt("music_options",bundle.getInt("music_options"));
                //Toast.makeText(MainActivity.this,"value = " + bundle.getInt("music_options"), Toast.LENGTH_SHORT).show();
                editor.commit();
            }

        }
        else {
            //Toast.makeText(MainActivity.this,"Not Receive",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onRestart() {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedOptions",MODE_PRIVATE);
        ScreenMainLayout.setBackgroundColor(ContextCompat.getColor(MainActivity.this,sharedPreferences.getInt("color_options",R.color.colorWhite)));
        resetResourceImageView(sharedPreferences.getInt("color_options",R.color.colorWhite));
        super.onRestart();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
        super.onBackPressed();
    }

    public void resetResourceImageView(int color){
        if(color == R.color.colorRed){
            imgTitle.setImageResource(R.drawable.freakingmath_white);
            imgBulb.setImageResource(R.drawable.bulb_white);
            imgNumbers.setImageResource(R.drawable.number_mainscreen_while);
        }
        else if(color == R.color.colorWhite){
            imgTitle.setImageResource(R.drawable.freakingmath_black);
            imgBulb.setImageResource(R.drawable.bulb_black);
            imgNumbers.setImageResource(R.drawable.number_mainscreen_black);
        }
        else if(color == R.color.colorPrimaryDark){
            imgTitle.setImageResource(R.drawable.freakingmath_white);
            imgBulb.setImageResource(R.drawable.bulb_white);
            imgNumbers.setImageResource(R.drawable.number_mainscreen_while);
        }
    }
}
