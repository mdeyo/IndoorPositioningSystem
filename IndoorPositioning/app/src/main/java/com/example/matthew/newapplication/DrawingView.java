package com.example.matthew.newapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matthew on 1/30/2015.
 */
public class DrawingView extends View {

    //paths
    Path futurePath, historyPath, sidePath1, sidePath2, sidePath3;

    private ArrayList<String> QRCodeLocations = new ArrayList<>();
    private ArrayList<String> sideQRCodeLocations = new ArrayList<>();

    //drawing path
    private Path drawPath;
    //drawing and canvas paint
    private Paint drawPaint, canvasPaint;
    //initial color
    private int paintColor = android.graphics.Color.rgb(102, 153, 255);
    //canvas
    private Canvas drawCanvas = new Canvas();
    //canvas bitmap
    private Bitmap canvasBitmap, star, starGold, circle, coin; //map;
    public static Bitmap qr1, qr2, qr3, qr4, qr5, qr6, qr7, qr8, qr9;

    DashPathEffect dashed = new DashPathEffect(new float[]{10, 20}, 0);

    Bitmap arrowUp, arrowRight, arrowLeft, arrowDown;
    Bitmap currentArrow;
    Point lastPosition = new Point(0, 0);

    private ArrayList<Bitmap> coinSequence = new ArrayList<>();

    private float brushSize, lastBrushSize;

    private boolean erase = false;

    private SideViewPreview side;

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();
    }

    private void setupDrawing() {
        futurePath = new Path();
        historyPath = new Path();
        sidePath1 = new Path();
        sidePath2 = new Path();
        sidePath3 = new Path();

        //get drawing area setup for interaction
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);

        drawCanvas = new Canvas();

        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(12);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        canvasPaint = new Paint(Paint.DITHER_FLAG);

        //brushSize = getResources().getInteger(R.integer.medium_size);
        //lastBrushSize = brushSize;
        //drawPaint.setStrokeWidth(brushSize);

        arrowDown = BitmapFactory.decodeResource(getResources(), R.drawable.down_arrow_light);
        arrowUp = BitmapFactory.decodeResource(getResources(), R.drawable.up_arrow_light);
        arrowRight = BitmapFactory.decodeResource(getResources(), R.drawable.right_arrow);
        arrowLeft = BitmapFactory.decodeResource(getResources(), R.drawable.left_arrow);
        star = BitmapFactory.decodeResource(getResources(), R.drawable.star);
        starGold = BitmapFactory.decodeResource(getResources(), R.drawable.star_gold);
        circle = BitmapFactory.decodeResource(getResources(), R.drawable.circle);
        coin = BitmapFactory.decodeResource(getResources(), R.drawable.coin1_1);
        qr1 = BitmapFactory.decodeResource(getResources(), R.drawable.qr1);
        qr2 = BitmapFactory.decodeResource(getResources(), R.drawable.qr2);
        qr3 = BitmapFactory.decodeResource(getResources(), R.drawable.qr3);
        qr4 = BitmapFactory.decodeResource(getResources(), R.drawable.qr4);
        qr5 = BitmapFactory.decodeResource(getResources(), R.drawable.qr5);
        qr6 = BitmapFactory.decodeResource(getResources(), R.drawable.qr6);
        qr7 = BitmapFactory.decodeResource(getResources(), R.drawable.qr7);
        qr8 = BitmapFactory.decodeResource(getResources(), R.drawable.qr8);
        qr9 = BitmapFactory.decodeResource(getResources(), R.drawable.qr9);

//        map = BitmapFactory.decodeResource(getResources(),R.drawable.side_view_clear);

    }

    public void startNew() {
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }

    public void setErase(boolean isErase) {
        //set erase true or false
        erase = isErase;
        if (erase) drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        else drawPaint.setXfermode(null);
    }

    public void setBrushSize(float newSize) {
        //update size
        float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                newSize, getResources().getDisplayMetrics());
        brushSize = pixelAmount;
        drawPaint.setStrokeWidth(brushSize);
    }

    public void setLastBrushSize(float lastSize) {
        lastBrushSize = lastSize;
    }

    public float getLastBrushSize() {
        return lastBrushSize;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //view given size
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //draw view
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
    }

    public void setColor(String newColor) {
        //set color
        invalidate();
        paintColor = Color.parseColor(newColor);
        drawPaint.setColor(paintColor);
    }

//    public void setStroke(BasicStroke stroke){
//        invalidate();
//        drawPaint.setStroke();
//    }
//
//    final static float dash1[] = {10.0f};
//    final static BasicStroke dashed =
//            new BasicStroke(1.0f,
//                    BasicStroke.CAP_BUTT,
//                    BasicStroke.JOIN_MITER,
//                    10.0f, dash1, 0.0f);
//    g2.setStroke(dashed);
//    g2.draw(new RoundRectangle2D.Double(x, y,
//    rectWidth,
//    rectHeight,
//            10, 10));

    public void drawStartStar(int x, int y) {
        drawCanvas.drawBitmap(star, x - star.getWidth() / 2, y - star.getHeight() / 2, drawPaint);
    }

    public void drawCurrentCircle(int x, int y) {
        drawCanvas.drawBitmap(circle, x - circle.getWidth() / 2, y - circle.getHeight() / 2, drawPaint);
    }

    public void drawGoldStar(int x, int y) {
        drawCanvas.drawBitmap(starGold, x - starGold.getWidth() / 2, y - starGold.getHeight() / 2, drawPaint);
    }

    public void drawCoin(int x, int y) {
        drawCanvas.drawBitmap(coin, x - coin.getWidth() / 2, y - coin.getHeight() / 2, drawPaint);
    }

    public void updateFuturePath(List<Point> points, String transition) {
        futurePath.reset();
        Boolean first = true;
        Point last = new Point(0, 0);

        for (int i = 0; i < points.size(); i++) {
            Point point = points.get(i);
            if (first) {
                first = false;
                futurePath.moveTo(point.x, point.y);

            } else if (i < points.size() - 1) {
                Point next = points.get(i + 1);
//                path.lineTo(point.x, point.y);
                futurePath.quadTo(point.x, point.y, next.x, next.y);
            } else {
                futurePath.lineTo(point.x, point.y);
                lastPosition = point;
            }
        }

        if (transition.equals(null)) {
            Log.d("transition equals:", "null");
        } else {
            Log.d("transition equals:", transition);
            if (transition.equals("up")) {
                currentArrow = arrowUp;
                lastPosition.y = lastPosition.y - 20;
            } else if (transition.equals("down")) {
                currentArrow = arrowDown;
                lastPosition.y = lastPosition.y + 20;
            } else if (transition.equals("left")) {
                currentArrow = arrowLeft;
                lastPosition.x = lastPosition.x - 20;
            } else if (transition.equals("right")) {
                currentArrow = arrowRight;
                lastPosition.x = lastPosition.x + 20;
            } else {
                currentArrow = null;
            }

            displayPaths();
        }
    }

    public void updateQRCodeLocations(List<QRLocationXY> points) {
//        Path futurePath = new Path();
        QRCodeLocations.clear();
        Boolean first = true;
        Rect rect = new Rect();

        String x, y;
        //actual size of grid
//        int dx = 49;
//        int dy = 52;
        //scaled down for centering
        String dx = "39";
        String dy = "42";

        for (QRLocationXY p : points) {
            x = String.valueOf(p.x + 5);
            y = String.valueOf(p.y + 5);
//            rect.set((int)p.x,(int)p.y,(int)p.x+dx,(int)p.y+dy);
            QRCodeLocations.add("qr" + String.valueOf(p.points) + ":" + x + ":" + y + ":" + dx + ":" + dy);
        }

        displayPaths();
    }

    private void displayPaths() {
        startNew();
        drawPaint.setColor(Color.RED);
        drawPaint.setStrokeWidth(18);

        String[] p;
        Bitmap current = null;
        for (String r : QRCodeLocations) {
            p = r.split(":");
            if (p[0].equals("qr0")) {
                //no points, dont show QR code
            } else {
                if (p[0].equals("qr1")) {
                    current = qr1;
                } else if (p[0].equals("qr2")) {
                    current = qr2;
                } else if (p[0].equals("qr3")) {
                    current = qr3;
                } else if (p[0].equals("qr4")) {
                    current = qr4;
                } else if (p[0].equals("qr5")) {
                    current = qr5;
                } else if (p[0].equals("qr6")) {
                    current = qr6;
                } else if (p[0].equals("qr7")) {
                    current = qr7;
                } else if (p[0].equals("qr8")) {
                    current = qr8;
                } else if (p[0].equals("qr9")) {
                    current = qr9;
                }

                current = Bitmap.createScaledBitmap(current, Integer.parseInt(p[3]), Integer.parseInt(p[4]), false);
                drawCanvas.drawBitmap(current, Float.parseFloat(p[1]), Float.parseFloat(p[2]), drawPaint);
            }
        }

        drawPaint.setColor(paintColor);
        drawPaint.setPathEffect(new PathEffect());
        drawPaint.setStrokeWidth(12);
//        drawPaint.setColor(Color.GREEN);
        drawCanvas.drawPath(futurePath, drawPaint);

        drawPaint.setPathEffect(dashed);
//        drawPaint.setColor(Color.GREEN);
        drawPaint.setStrokeWidth(6);
        drawCanvas.drawPath(historyPath, drawPaint);

        if (currentArrow != null) {
            Log.d("transition draw lp:", String.valueOf(lastPosition.x) + "," + String.valueOf(lastPosition.y));
            drawCanvas.drawBitmap(Bitmap.createScaledBitmap(currentArrow, 50, 50, false), lastPosition.x - 25, lastPosition.y - 25, drawPaint);
        }
//        futurePath.reset();
//        historyPath.reset();


        invalidate();
    }

    public void paintRectangle(int x, int y, int dx, int dy) {
        drawCanvas.drawRect(x, y, x + dx, y + dy, drawPaint);
    }

    public void updatePathHistory(List<Point> points) {
        Boolean first = true;
        historyPath.reset();

        for (int i = 0; i < points.size(); i++) {
            Point point = points.get(i);

            if (first) {
                first = false;
                historyPath.moveTo(point.x, point.y);

            } else if (i < points.size() - 1) {
                Point next = points.get(i + 1);
//                path.lineTo(point.x, point.y);
                historyPath.quadTo(point.x, point.y, next.x, next.y);
            } else {
                historyPath.lineTo(point.x, point.y);
            }
        }

        displayPaths();

        //code from drawing app - skips every other point, not helpful
//        for (int i = 0; i < points.size(); i += 2) {
//            Point point = points.get(i);
//
//            if (first) {
//                first = false;
//                path.moveTo(point.x, point.y);
//            } else if (i < points.size() - 1) {
//                Point next = points.get(i + 1);
//                path.quadTo(point.x, point.y, next.x, next.y);
//            } else {
//                path.lineTo(point.x, point.y);
//            }
//        }

//        drawCanvas.drawBitmap(map,0,0,drawPaint);
    }

    public void updateSidePath(List<Point> points1, List<Point> points2, List<Point> points3) {

        sidePath1.reset();
        sidePath2.reset();
        sidePath3.reset();

        //setup first path

        Boolean first = true;
        Point point, firstPoint, lastPoint;
        firstPoint = new Point(0, 0);
        lastPoint = new Point(0, 0);
        for (int i = 0; i < points1.size(); i++) {
            point = points1.get(i);
            if (first) {
                first = false;
                firstPoint = point;
                sidePath1.moveTo(point.x, point.y);
            } else if (i < points1.size() - 1) {
                Point next = points1.get(i + 1);
                sidePath1.quadTo(point.x, point.y, next.x, next.y);
            } else {
                lastPoint = point;
                sidePath1.lineTo(point.x, point.y);
            }
        }

        //setup second path

        first = true;
        for (int i = 0; i < points2.size(); i++) {
            point = points2.get(i);
            if (first) {
                first = false;
//                firstPoint = point;
                sidePath2.moveTo(point.x, point.y);
            } else if (i < points2.size() - 1) {
                Point next = points2.get(i + 1);
                sidePath2.quadTo(point.x+8, point.y+8, next.x+8, next.y+8);
            } else {
                lastPoint = point;
                sidePath2.lineTo(point.x, point.y);
            }
        }

        //setup third path

        first = true;
        for (int i = 0; i < points3.size(); i++) {
            point = points3.get(i);
            if (first) {
                first = false;
//                firstPoint = point;
                sidePath3.moveTo(point.x, point.y);
            } else if (i < points3.size() - 1) {
                Point next = points3.get(i + 1);
                sidePath3.quadTo(point.x-8, point.y-8, next.x-8, next.y-8);
            } else {
                lastPoint = point;
                sidePath3.lineTo(point.x, point.y);
            }
        }

        startNew();
        drawStartStar((int) firstPoint.x, (int) firstPoint.y);
        drawGoldStar((int) lastPoint.x, (int) lastPoint.y);

        drawPaint.setPathEffect(new PathEffect());
        drawPaint.setStrokeWidth(12);

        drawPaint.setColor(Color.parseColor("#5ce62e"));
        drawCanvas.drawPath(sidePath1, drawPaint);

        drawPaint.setColor(Color.parseColor("#db94ff"));
        drawCanvas.drawPath(sidePath2, drawPaint);

        drawPaint.setColor(Color.parseColor("#33CCFF"));
        drawCanvas.drawPath(sidePath3, drawPaint);
    }

    public void updateSidePath(Point firstPoint,Point currentPoint,List<Point> points1) {
        sidePath1.reset();

        Boolean first = true;
        Point point, lastPoint;
//        firstPoint = new Point(0, 0);
        lastPoint = new Point(0, 0);

        for (int i = 0; i < points1.size(); i++) {
            point = points1.get(i);
            if (first) {
                first = false;
//                firstPoint = point;
                sidePath1.moveTo(point.x, point.y);
            } else if (i < points1.size() - 1) {
                Point next = points1.get(i + 1);
                sidePath1.quadTo(point.x, point.y, next.x, next.y);
            } else {
                lastPoint = point;
                sidePath1.lineTo(point.x, point.y);
            }
        }

        startNew();
        drawStartStar((int) firstPoint.x, (int) firstPoint.y);
        drawCurrentCircle((int)currentPoint.x,(int)currentPoint.y);
        drawGoldStar((int) lastPoint.x, (int) lastPoint.y);
        drawPaint.setPathEffect(new PathEffect());
        drawPaint.setStrokeWidth(12);

        drawPaint.setColor(paintColor);
        drawCanvas.drawPath(sidePath1, drawPaint);
    }

    public void updateSidePath(List<Point> points1) {
        sidePath1.reset();

        Boolean first = true;
        Point point, lastPoint;
//        firstPoint = new Point(0, 0);
        lastPoint = new Point(0, 0);

        for (int i = 0; i < points1.size(); i++) {
            point = points1.get(i);
            if (first) {
                first = false;
//                firstPoint = point;
                sidePath1.moveTo(point.x, point.y);
            } else if (i < points1.size() - 1) {
                Point next = points1.get(i + 1);
                sidePath1.quadTo(point.x, point.y, next.x, next.y);
            } else {
                lastPoint = point;
                sidePath1.lineTo(point.x, point.y);
            }
        }

        startNew();
//        drawStartStar((int) firstPoint.x, (int) firstPoint.y);
//        drawCurrentCircle((int)currentPoint.x,(int)currentPoint.y);
        drawGoldStar((int) lastPoint.x, (int) lastPoint.y);
        drawPaint.setPathEffect(new PathEffect());
        drawPaint.setStrokeWidth(12);

        drawPaint.setColor(paintColor);
        drawCanvas.drawPath(sidePath1, drawPaint);
    }

    public void updateSideViewQR(List<QRLocationXY> points) {
        sideQRCodeLocations.clear();
        String x, y;
        String dx = "40";
        String dy = "40";

        for (QRLocationXY p : points) {
            x = String.valueOf(p.x);
            y = String.valueOf(p.y);
            sideQRCodeLocations.add("qr" + String.valueOf(p.points) + ":" + x + ":" + y + ":" + dx + ":" + dy);
        }
        displaySideView();
    }

    private void displaySideView() {
        drawPaint.setColor(Color.RED);
        drawPaint.setStrokeWidth(18);

        String[] p;
        Bitmap current = null;
        for (String r : sideQRCodeLocations) {
            p = r.split(":");
            if (p[0].equals("qr0")) {
                //no points, do not show QR code
            } else {
                if (p[0].equals("qr1")) {
                    current = qr1;
                } else if (p[0].equals("qr2")) {
                    current = qr2;
                } else if (p[0].equals("qr3")) {
                    current = qr3;
                } else if (p[0].equals("qr4")) {
                    current = qr4;
                } else if (p[0].equals("qr5")) {
                    current = qr5;
                } else if (p[0].equals("qr6")) {
                    current = qr6;
                } else if (p[0].equals("qr7")) {
                    current = qr7;
                } else if (p[0].equals("qr8")) {
                    current = qr8;
                } else if (p[0].equals("qr9")) {
                    current = qr9;
                }

                current = Bitmap.createScaledBitmap(current, Integer.parseInt(p[3]), Integer.parseInt(p[4]), false);
                drawCanvas.drawBitmap(current, Float.parseFloat(p[1]) - current.getWidth() / 2, Float.parseFloat(p[2]) - current.getHeight() / 2, drawPaint);
            }
        }

        invalidate();
    }

//    public void onDraw(Canvas canvas) {
//        Path path = new Path();
//        boolean first = true;
//        for(int i = 0; i < points.size(); i += 2){
//                Point point = points.get(i);
//                if(first){
//                    first = false;
//                    path.moveTo(point.x, point.y);
//                }
//
//            else if(i < points.size() - 1){
//                Point next = points.get(i + 1);
//                path.quadTo(point.x, point.y, next.x, next.y);
//            }
//            else{
//                path.lineTo(point.x, point.y);
//            }
//        }
//
//        canvas.drawPath(path, paint);
//    }

    public void setSideView(SideViewPreview side) {
        this.side = side;
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        //detect user touch
//        float touchX = event.getX();
//        float touchY = event.getY();
////        Log.d("rawr", "x:" + touchX + " y:" + touchY);
////        side.rotatingCoin((int) touchX, (int) touchY);
//
//        //
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                drawPath.moveTo(touchX, touchY);
//                break;
//            case MotionEvent.ACTION_MOVE:
//                drawPath.lineTo(touchX, touchY);
//                break;
//            case MotionEvent.ACTION_UP:
//                drawCanvas.drawPath(drawPath, drawPaint);
//                drawPath.reset();
//                break;
//            default:
//                return false;
//        }
//        invalidate();
//        //
//
//        return true;
//    }
}
