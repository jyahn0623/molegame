package co.kr.molegame;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;
import android.widget.ImageView;

import org.java_websocket.client.WebSocketClient;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MoleGame {
    private ImageView moleGameImageView;
    private List<Point> gridItemsLoc = new ArrayList<>();
    private Point[] hammer4Loc = new Point[4];
    private Bitmap noMoleImageBitmap; //그리드만 있고 두더지 없음
    private Bitmap noHammerImageBitmap; // 그리드와 두더지는 있지만 망치이미지는 없음
    private int currentMoleGridItemId = -1;
    private int currentHammerItemId = -1; // 최근 해머 위치
    private int gridItemWidth; // 그리드 아이템 사각형 너비
    private int gridItemHeight;// 그리드 아이템 사각형 높이
    private float density;
    private int currentGridNumber; // 마지막 두더지 위치

    private int splitScreenCnt;
    private int goalCnt = 5;
    private int hitCnt = 0;
    private int preHammerGridItemId = -1;
    private int paneCnt = 0;
    private String game_id;
    private String username;
    private String moleDataUri;
    private WebSocketClient moleDataSocket;
    private long startTime = System.currentTimeMillis() / 1000; // 시작 시간

    //망치 이미지 망치 left top x,y위치
    private final double HAMMER_LEFT_TOP_X = 0.40;
    private final double HAMMER_LEFT_TOP_Y = 0.07;

    private final double HAMMER_LEFT_BOTTOM_X = 0.06;
    private final double HAMMER_LEFT_BOTTOM_Y = 0.70;

    private final double HAMMER_RIGHT_TOP_X = 0.65;
    private final double HAMMER_RIGHT_TOP_Y = 0.26;

    private final double HAMMER_RIGHT_BOTTOM_X = 0.37;
    private final double HAMMER_RIGHT_BOTTOM_Y = 0.93;

    private int hammerWidth;
    private int hammerHeight;



    public MoleGame(ImageView moleGameImageView, float density, WebSocketClient socket, String username, String GAME_ID){
        this.moleGameImageView = moleGameImageView;
        this.density = density;
        this.moleDataSocket = socket;
        this.username = username;
        this.game_id = GAME_ID;
    }

    public void setGoalCnt(int goalCnt){this.goalCnt = goalCnt;}

    public void setSplitScreenCnt(int splitScreenCnt){
        this.splitScreenCnt = splitScreenCnt;
        gridItemWidth = moleGameImageView.getWidth() / splitScreenCnt;
        gridItemHeight = moleGameImageView.getHeight() / splitScreenCnt;
    }

    public void updateHitCnt(){hitCnt += 1;}

    /**
     * 기존 망치 위치를 현재 망치위치로 업데이트 및 pane 이동횟수 업데이트
     * @param hammerGridItemId 현재망치 위치의 grid item id
     */
    public void updatePaneCnt(int hammerGridItemId){
        this.preHammerGridItemId = hammerGridItemId;
        // 로그 데이터 서버 전송
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", username);
            jsonObject.put("gameId", game_id);
            jsonObject.put("time", System.currentTimeMillis());
            jsonObject.put("splitScreenCnt", splitScreenCnt);
            jsonObject.put("paneCnt", paneCnt);
            jsonObject.put("hitCnt", hitCnt);
            jsonObject.put("handPosition", preHammerGridItemId);
            jsonObject.put("molePosition", currentMoleGridItemId);
            this.moleDataSocket.send(jsonObject.toString());
        }catch(Exception e){
            e.printStackTrace();
        }
        paneCnt++;
    }

    public int getHitCnt(){ return hitCnt;}
    public int getGoalCnt(){ return goalCnt;}
    public int getCurrentMoleGridItemId(){return currentMoleGridItemId;}
    public int getPreHammerGridItemId(){return preHammerGridItemId;}
    public int getPaneCnt(){return paneCnt;}


    /**
     * 현재 x,y 좌표가 몇번째 pane인지 검색
     * @param x 현재x좌표 퍼센트 (0 ~ 1)
     * @param y 현재y좌표 퍼센트 (0 ~ 1)
     * @return 검색성공하면 해당 pane번호, 실패면 -1
     */
    public int getGridItemId(double x, double y){

        x = (double)moleGameImageView.getWidth() * x;
        y = (double)moleGameImageView.getHeight() * y;

        for(int i=0; i<gridItemsLoc.size(); i++){
            Point point = gridItemsLoc.get(i);

            if (x >= point.x && x < point.x + gridItemWidth &&
                        y >= point.y && y < point.y + gridItemHeight) {
                    return i;
            }
        }

        return -1;
    }
    /**
     * 현재 x,y 좌표가 몇번째 pane인지 검색
     * @param x 현재x좌표 픽셀
     * @param y 현재y좌표 픽셀
     * @return 검색성공하면 해당 pane번호, 실패면 -1
     */
    public int getGridItemIdPx(int x, int y){
        for(int i=0; i<gridItemsLoc.size(); i++){
            Point point = gridItemsLoc.get(i);

            if (x >= point.x && x < point.x + gridItemWidth &&
                    y >= point.y && y < point.y + gridItemHeight) {
                return i;
            }
        }
        return -1;
    }

    public boolean isHits(double x, double y){

        x = (double)moleGameImageView.getWidth() * x;
        y = (double)moleGameImageView.getHeight() * y;

//        List<Point> hammer4Loc = new ArrayList<>();
        hammer4Loc[0] = new Point(
                                (int)(getHammerX(hammerWidth,x, HAMMER_LEFT_TOP_X)),
                                (int)(getHammerY(hammerHeight,y, HAMMER_LEFT_TOP_Y))
                        );

        hammer4Loc[1] = new Point(
                (int)(getHammerX(hammerWidth,x, HAMMER_LEFT_BOTTOM_X)),
                (int)(getHammerY(hammerHeight,y, HAMMER_LEFT_BOTTOM_Y))
        );

        hammer4Loc[2] = new Point(
                (int)(getHammerX(hammerWidth,x, HAMMER_RIGHT_TOP_X)),
                (int)(getHammerY(hammerHeight,y, HAMMER_RIGHT_TOP_Y))
        );

        hammer4Loc[3] =new Point(
                (int)(getHammerX(hammerWidth,x, HAMMER_RIGHT_BOTTOM_X)),
                (int)(getHammerY(hammerHeight,y, HAMMER_RIGHT_BOTTOM_Y))
        );

        Point point = gridItemsLoc.get(currentMoleGridItemId);

        for(Point hammerPoint : hammer4Loc) {
            if (hammerPoint.x >= point.x && hammerPoint.x < point.x + gridItemWidth &&
                    hammerPoint.y >= point.y && hammerPoint.y < point.y + gridItemHeight) {
                   return true;
            }
        }

        return false;
    }

    private double getDrawHammerX(double width, double x, double rate){
        return x + width * density * rate;
    }
    private double getDrawHammerY(double height, double y, double rate){
        return y + height * density * rate;
    }

    private double getHammerX(double width, double x, double rate){
        return x + width  * rate * density;
    }
    private double getHammerY(double height, double y, double rate){
        return y + height * rate * density;
    }

    public boolean isGameOver(int gameTimeSec, int progress){

        if(gameTimeSec == -1 && progress < 100) return true;
        return false;
    }

    public boolean isGameComplete(int progress){
        if(progress == 100) return true;
        return false;
    }


    /**
     * 두더지 이미지를 화면에 그리기
     * @param moleImageBitmap 두더지 이미지
     * @param gridItemId gridItem id
     */
    public void drawMoleImage(Bitmap moleImageBitmap, int gridItemId){

        currentMoleGridItemId = gridItemId;
        Bitmap recizedBitmap = resizeBitmap(moleImageBitmap, density);
        Bitmap bitmap = noMoleImageBitmap.copy(noMoleImageBitmap.getConfig(),true);
        Canvas canvas = new Canvas(bitmap);
        Point point = gridItemsLoc.get(currentMoleGridItemId);
        canvas.drawBitmap(recizedBitmap, point.x ,point.y,new Paint());

        noHammerImageBitmap = bitmap.copy(bitmap.getConfig(), true);

        moleGameImageView.setImageBitmap(bitmap);
    }

    /**
     * 두더지랜덤위치 반환, 단 이전위치와 다른난수
     * @return  0 ~ gridSize 값반환
     */
    public int getRandomGridItemId(){

        int num = splitScreenCnt * splitScreenCnt ;
        int randomNum;
        do{
            randomNum = (int)(Math.random() * num);

        }while(isUnavailableRandomNumber(randomNum));

        return randomNum;
    }

    /**
     * 특정 창위치를 설정하면, 설정한 위치 창아디만 반환
     * @param  paneIdList 특정창id list
     * ex) 3*3 창id
     *  0 1 2
     *  3 4 5
     *  6 7 8
     */
    public int getRandomGridItemId(List<Integer> paneIdList){

        int result;
        do{
            int randomIndex = (int)(Math.random() * paneIdList.size());
            result = paneIdList.get(randomIndex);
        }while(isUnavailableRandomNumber(result));
        return result;
    }

    /**
     * 사용불가능한 난수 인지
     */
    public boolean isUnavailableRandomNumber(int randomNum){

        if(randomNum == currentMoleGridItemId) return true;
        for(int i=0; i<hammer4Loc.length; i++){
            Point point = hammer4Loc[i];
            if(point == null) return false;
            Log.e("main", getGridItemIdPx(point.x, point.y)+"");
            if(getGridItemIdPx(point.x, point.y) == randomNum) return true;
        }
        return false;
    }


    /**
     * 화면에 망치 이미지 그리기
     * @param x 망치위치 x
     * @param y 망치위치 y
     */
    public void drawHammerImage(Bitmap hammerImageBitmap, double x,  double y){

        Bitmap resizedHammerBitmap = resizeBitmap(hammerImageBitmap,density,  0.6);
        hammerWidth = resizedHammerBitmap.getWidth();
        hammerHeight = resizedHammerBitmap.getHeight();

        x = (double) moleGameImageView.getWidth() * x;
        y = (double) moleGameImageView.getHeight() * y;

        Bitmap bitmap = noHammerImageBitmap.copy(noHammerImageBitmap.getConfig(), true);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(resizedHammerBitmap, (int)x, (int)y,new Paint());
        moleGameImageView.setImageBitmap(bitmap);
    }

    /**
     * 그리드 한개창에 맞게 리사이즈하기
     * @param originBitmap 리사이즈할 이미지
     * @return 리사이된 이미지
     */
    private Bitmap resizeBitmap(Bitmap originBitmap, float density){
        float newWidth = (moleGameImageView.getWidth() / splitScreenCnt) / density ;
        float newHeight = (moleGameImageView.getHeight() / splitScreenCnt) / density ;


        Bitmap recizedBitmap = Bitmap.createScaledBitmap(
                originBitmap,
                (int)newWidth,
                (int)newHeight,
                true
        );
        return recizedBitmap;
    }

    /**
     * 그리드창width * rate, 그리드창height * rate
     * @param originBitmap 리사이즈할 이미지
     * @return 리사이된 이미지
     */
    private Bitmap resizeBitmap(Bitmap originBitmap, float density, double rate){
        float newWidth = (moleGameImageView.getWidth() / splitScreenCnt) / density ;
        float newHeight = (moleGameImageView.getHeight() / splitScreenCnt) / density ;


        Bitmap recizedBitmap = Bitmap.createScaledBitmap(
                originBitmap,
                (int) ((double)newWidth * rate),
                (int)((double)newHeight * rate),
                true
        );
        return recizedBitmap;
    }


    /**
     * grid 그리기
     */
    public void drawGrid(){
        int imageViewWidth = moleGameImageView.getWidth();
        int imageViewHeight = moleGameImageView.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(imageViewWidth,imageViewHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(7);
//        canvas.drawColor(Color.YELLOW);

        int xWidth = imageViewWidth / splitScreenCnt;
        int yWidth = imageViewHeight / splitScreenCnt;

        int startX = xWidth;
        int startY = yWidth;

        for(int i=0; i<splitScreenCnt-1; i++){

            //가로선 그리기
            canvas.drawLine(0,startY,imageViewWidth,startY,paint);
            //세로선그리기
            canvas.drawLine(startX,0,startX,imageViewHeight,paint);
            startX += xWidth;
            startY += yWidth;

        }

        noMoleImageBitmap = bitmap;
        initGridItemLoc();
        moleGameImageView.setImageBitmap(bitmap);

    }

    private void initGridItemLoc(){
        int startX = 0;
        int startY = 0;
        for(int i=0; i<splitScreenCnt; i++){
            for(int j=0; j<splitScreenCnt; j++){

                gridItemsLoc.add(new Point(startX,startY));
                startX += gridItemWidth;
            }
            startX = 0;
            startY += gridItemHeight;
        }
    }


}