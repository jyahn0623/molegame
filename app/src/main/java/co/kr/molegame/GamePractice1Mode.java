package co.kr.molegame;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.usb.UsbDevice;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.Surface;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jiangdg.usbcamera.UVCCameraHelper;
import com.serenegiant.usb.common.AbstractUVCCameraHandler;
import com.serenegiant.usb.widget.CameraViewInterface;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class GamePractice1Mode extends AppCompatActivity {
    private final String GAME_ID = UUID.randomUUID().toString();
    private ImageView moleGameImageView;
    private String username;
    private boolean isStartGame = false;
    // 소켓 통신
    private String serverUri, moleDataUri;
    private WebSocketClient webSocketClient;
    private WebSocketClient moleDataSocket;
    // --------
    private MoleGame moleGame;
    private int splitScreenCnt;
    private String hand;
    private int direction;

    private TextView currentCntTextView;
    private TextView paneMoveCntTextView;
    private TextView gameTimeTextView;

    private BitmapDrawable hammerBitmapDrawable;
    private BitmapDrawable moleBitmapDrawable;
    private BitmapDrawable backgroundDrawable;

    private Timer gameTimer;
    private int gameTimeSec = 50;
    private int gameTimeSecVal;
    private List<Integer> plannedIdList;

    MoleSoundPool soundPool;
    MediaPlayer mediaPlayer;

    // USB 카메라 관련 변수
    private int YUVIMAGE_QUALITY = 30;  // YUVImage -> jpeg 압축률

    private UVCCameraHelper mCameraHelper;
    private CameraViewInterface mUVCCameraView;
    private AlertDialog mDialog;
    private boolean isRequest;
    private boolean isPreview;
    private CameraViewInterface mTextureView;
    private CameraViewInterface.Callback mCallback;
    private UVCCameraHelper.OnMyDevConnectListener listener;

    private static final int ANALYSIS_DELAY_MS = 1000;
    private static final int INVALID_TIME = -1;
    private long lastAnalysisTime = INVALID_TIME;
    private boolean isProcessingImage = false;
    final int CAMERA_WIDTH = 640;
    final int CAMERA_HEIGHT = 480;
    // -------------------------

    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_practice1_mode);

        hammerBitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.hammer);
        moleBitmapDrawable   = (BitmapDrawable) getResources().getDrawable(R.drawable.mole_perfect);


        moleGameImageView = findViewById(R.id.progressImageView);
        currentCntTextView = findViewById(R.id.current_cnt_text_view);
        paneMoveCntTextView = findViewById(R.id.pane_move_cnt_text_view);
        gameTimeTextView = findViewById(R.id.game_time_textview);

        splitScreenCnt = getIntent().getIntExtra("splitScreenCnt",3);
        gameTimeSec = getIntent().getIntExtra("gameTimeSec",50);
        gameTimeSecVal = gameTimeSec;
        hand = getIntent().getStringExtra("hand");
        direction = getIntent().getIntExtra("direction", 0);


        // Set sound
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.background_bgm); // 게임 배경 음악
        mediaPlayer.setLooping(true);
        soundPool.initSounds(getApplicationContext()); // 효과음
        mediaPlayer.start();
        // ----------
        serverUri = getString(R.string.socket) + "/ws/game/mole";
        URI uri = null;
        try {
            uri = new URI(serverUri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        webSocketClient = new MyWebSocketClient(uri);
        webSocketClient.connect();

        // 두더지 잡기 로그 소켓
        moleDataUri = getString(R.string.server) + "/ws/game/mole/data";
        try {
            moleDataSocket = new MoleDataSocket(new URI(moleDataUri));
            moleDataSocket.connect();
        }catch(Exception e) {
            e.printStackTrace();
        }

        mTextureView = (CameraViewInterface) findViewById(R.id.mTextureView);

//        startGameTimer();
//        setCamera();

        sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        username = sharedPref.getString("userId", "");

        setCamera();

    }

    private void showShortMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        //계획된 위치
        plannedIdList = new ArrayList<>();
        // 가로
        if (direction == 0){
            plannedIdList.add(splitScreenCnt/2 * splitScreenCnt);
            plannedIdList.add((splitScreenCnt/2 * splitScreenCnt) + (splitScreenCnt-1));
        }
        // 세로
        else if (direction == 1) {
            plannedIdList.add(splitScreenCnt / 2);
            plannedIdList.add(((splitScreenCnt * splitScreenCnt) - 1) - splitScreenCnt / 2);
        }
        // 좌상 -> 우하
        else if(direction == 2) {
            plannedIdList.add(0);
            plannedIdList.add(splitScreenCnt * splitScreenCnt - 1);
        }
        // 우상 -> 좌하
        else if(direction == 3) {
            plannedIdList.add(splitScreenCnt - 1);
            plannedIdList.add(splitScreenCnt * splitScreenCnt - splitScreenCnt);
        }

        if(moleGame == null){
            //molegame 객체생성 및 bitmap에 그리드 그리기
            moleGame = new MoleGame(moleGameImageView, displayMetrics.density, moleDataSocket, username, GAME_ID);
            moleGame.setSplitScreenCnt(splitScreenCnt);
            moleGame.drawGrid();

            //그리드 item에 두더지 그리기
            moleGame.drawMoleImage(
                    moleBitmapDrawable.getBitmap(),
                    moleGame.getRandomGridItemId(plannedIdList)
            );
        }
    }

    private void startGameTimer(){
        Timer timer = new Timer();

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if(gameTimeSec == -1){
                    timer.cancel();
                }else{
                    gameTimeTextView.setText(gameTimeSec+"");

                    gameTimeSec --;
                }
            }
        };
        timer.schedule(timerTask, 0, 1000);
    }

    private void setCamera(){
        showShortMsg("카메라를 세팅합니다.");
        // CameraViewInterface.Callback 인터페이스
        mCallback = new CameraViewInterface.Callback() {
            @Override
            public void onSurfaceCreated(CameraViewInterface view, Surface surface) {
                if (!isPreview && mCameraHelper.isCameraOpened()) {
                    mCameraHelper.startPreview(mUVCCameraView);
                    isPreview = true;
                }
            }

            @Override
            public void onSurfaceChanged(CameraViewInterface view, Surface surface, int width, int height) {
                // 여기서 프레임 처리가 되는 건 아닌 듯
            }

            @Override
            public void onSurfaceDestroy(CameraViewInterface view, Surface surface) {
                if (isPreview && mCameraHelper.isCameraOpened()) {
                    mCameraHelper.stopPreview();
                    isPreview = false;
                }
            }
        };
        // UVCCameraHelper.OnMyDevConnectListener 인터페이스
        listener = new UVCCameraHelper.OnMyDevConnectListener() {
            @Override
            public void onAttachDev(UsbDevice device) {
                if (!isRequest) {
                    isRequest = true;
                    if (mCameraHelper != null) {
                        mCameraHelper.requestPermission(getResources().getInteger(R.integer.camera_request_permission)); // requestPermission index가 usb의 숫자인데, usb serial이랑 동시에 연결된 경우 0이 시리얼, 1이 카메라 -> 찾는 코드 구현이 필요
                    }
                }
            }

            @Override
            public void onDettachDev(UsbDevice device) {
                // close camera(must have)
                if (isRequest) {
                    isRequest = false;
                    mCameraHelper.closeCamera();
                }
            }

            @Override
            public void onConnectDev(UsbDevice device, boolean isConnected) {
                if (!isConnected) {
                    showShortMsg("fail to connect,please check resolution params");
                    isPreview = false;
                } else {
                    isPreview = true;
                    showShortMsg("연결 중입니다.");
                    // initialize seekbar
                    // need to wait UVCCamera initialize over
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(2500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Looper.prepare();
                            Looper.loop();
                        }
                    }).start();
                }
            }

            @Override
            public void onDisConnectDev(UsbDevice device) {
                showShortMsg("카메라와 연결이 끊겼습니다.");
            }
        };

        mUVCCameraView = (CameraViewInterface) mTextureView;
        mUVCCameraView.setCallback(mCallback);
        mCameraHelper = UVCCameraHelper.getInstance();
        // preview 사이즈 지정
        mCameraHelper.setDefaultPreviewSize(CAMERA_WIDTH, CAMERA_HEIGHT);
        // set default frame format，default is UVCCameraHelper.Frame_FORMAT_MPEG
        mCameraHelper.setDefaultFrameFormat(UVCCameraHelper.FRAME_FORMAT_YUYV); // 프레임 포맷을 YUYU로 변경
        mCameraHelper.initUSBMonitor(this, mUVCCameraView, listener);
        mCameraHelper.setOnPreviewFrameListener(new AbstractUVCCameraHandler.OnPreViewResultListener() {
            @Override
            public void onPreviewResult(byte[] data) {
                if (isProcessingImage) {
                    return;
                }
                isProcessingImage = true;
                YuvImage yuvImage = new YuvImage(data, ImageFormat.NV21, CAMERA_WIDTH, CAMERA_HEIGHT, null);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                yuvImage.compressToJpeg(new Rect(0, 0, yuvImage.getWidth(), yuvImage.getHeight()), YUVIMAGE_QUALITY, out);
                byte[] imageBytes = out.toByteArray();
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                byte[] bitmap_byte = Bitmap2.bitmapToByteArray(bitmap);

                try{
                    webSocketClient.send(bitmap_byte);
                }catch (WebsocketNotConnectedException e){}

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        // step.2 register USB event broadcast
        // 아래 코드를 추가해야지 USB 연결이 활성화 됨.
        if (mCameraHelper != null) {
            mCameraHelper.registerUSB();
        }

        if (mUVCCameraView != null) {
            mUVCCameraView.onResume();
        }
        // --------------------------------------
    }

    @Override
    protected void onStop() {
        super.onStop();
        // step.3 unregister USB event broadcast
        if (mCameraHelper != null) {
            mCameraHelper.unregisterUSB();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unSetCamera();
        if (webSocketClient != null)
            webSocketClient.close();

        if (mediaPlayer != null){
            mediaPlayer.release();
        }
    }

    private void unSetCamera() {
        /*
        카메라 종료 (액티비티 종료 시··)
         */
        if (mCameraHelper != null) {
            mCameraHelper.release();
        }
    }

    class MyWebSocketClient extends WebSocketClient {

        public MyWebSocketClient(URI serverURI) {
            super(serverURI);
        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            try{
//                webSocketClient.send(String.format("{\"hand\":\"%s\"}", hand));
                webSocketClient.send(String.format("{\"hand\" : \"%s\", \"gameId\":\"%s\", \"username\" : \"%s\"}", hand, GAME_ID, username));
            }catch(WebsocketNotConnectedException e){}
        }

        @Override
        public void onMessage(ByteBuffer bytes) {}


        @Override
        public void onMessage(String message) {
            isProcessingImage = false;
            try {

                JSONObject json = new JSONObject(message);
                double curX = json.getDouble("curX");
                double curY = json.getDouble("curY");

                moleGame.drawHammerImage(
                        hammerBitmapDrawable.getBitmap(),
                        curX,
                        curY);


                // 맞았다면
                if(moleGame.isHits(curX,curY)){
                    if (!isStartGame){
                        startGame();
                        soundPool.play(R.raw.when_hit);
                        moleGame.drawMoleImage(
                                moleBitmapDrawable.getBitmap(),
                                moleGame.getRandomGridItemId(plannedIdList)
                        );
                    }
                    else {
                        soundPool.play(R.raw.when_hit);
                        moleGame.updateHitCnt();
                        moleGame.drawMoleImage(
                                moleBitmapDrawable.getBitmap(),
                                moleGame.getRandomGridItemId(plannedIdList)
                        );
                    }
                }

                if (!isStartGame) {
                    // 게임이 시작되지 않은 경우 아래 로직의 처리는 진행하지 않음
                    return;
                }

                // 기존 망치위치와 현재 망치 위치가 다르고 -1 아니면 pane 이동횟수 업데이트
                // -1이면 현재 망치위치 찾지 못한것
                int gridItemId = moleGame.getGridItemId(curX,curY);
                if(gridItemId != moleGame.getPreHammerGridItemId() && gridItemId != -1){
                    moleGame.updatePaneCnt(gridItemId);
                }
                currentCntTextView.setText(moleGame.getHitCnt()+"");
                paneMoveCntTextView.setText(moleGame.getPaneCnt()+"");

                if(gameTimeSec == -1){
                    soundPool.play(R.raw.when_game_clear);
                    Intent intent = new Intent(GamePractice1Mode.this, MoleGameResult.class);
                    intent.putExtra("action","GAME_COMPLETE");
                    intent.putExtra("time", gameTimeSecVal);
                    intent.putExtra("pane", moleGame.getPaneCnt());
                    intent.putExtra("grid_size", splitScreenCnt);
                    intent.putExtra("goal_count", moleGame.getHitCnt());
                    startActivity(intent);
                    finishGame();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
        }

        @Override
        public void onError(Exception ex) {
        }

    }

    private void startGame(){
        isStartGame = true;
        startGameTimer();
    }


    private void finishGame(){
        webSocketClient.close();
        finish();
    }
}