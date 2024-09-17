package com.example.oxgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.method.Touch;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.util.Log;


import androidx.annotation.NonNull;

import java.util.Arrays;

public class ChessBoardView extends View {

    private Bitmap[][] icons;  // 存储每个网格的图片
    private Bitmap defaultIcon;  // 默认图片
    private Bitmap oActive;
    private Bitmap oInactive;
    private Bitmap xActive;
    private Bitmap xInactive;
    private int[][] PawnStateMatrix;
    private int[][] TouchableState;

    private int rows;      // 默认行数
    private int cols;      // 默认列数
    private OnCellClickListener onCellClickListener;

    public ChessBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init() {
        // 加载默认图片
        defaultIcon = BitmapFactory.decodeResource(getResources(), R.drawable.blank_active);
        oActive = BitmapFactory.decodeResource(getResources(), R.drawable.o_active);
        oInactive = BitmapFactory.decodeResource(getResources(), R.drawable.o_inactive);
        xActive = BitmapFactory.decodeResource(getResources(), R.drawable.x_active);
        xInactive = BitmapFactory.decodeResource(getResources(), R.drawable.x_inactive);

        icons = new Bitmap[rows][cols];
        // 初始化所有网格的图标为默认图片
        updateIcons();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        // 获取视图的宽度和高度
        int viewWidth = getWidth();
        int viewHeight = getHeight();

        // 计算每个网格的大小
        int cellWidth = viewHeight / cols - 10;
        int cellHeight = viewHeight / rows - 10;



        // 计算中心位置
        int startX = (viewWidth - cellWidth * cols) / 2;
        int startY = (viewHeight - cellHeight * rows) / 2;

        // 绘制网格中的每个图片
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Bitmap icon = icons[row][col];
                Bitmap scaledIcon = Bitmap.createScaledBitmap(icon, cellWidth, cellHeight, false);
                int left = startX + col * cellWidth;
                int top = startY + row * cellHeight;
                canvas.drawBitmap(scaledIcon, left, top, null);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            int viewWidth = getWidth();
            int viewHeight = getHeight();
            int cellWidth = viewHeight / cols;
            int cellHeight = viewHeight / rows;

            int startX = (viewWidth - cellWidth * cols) / 2;
            int startY = (viewHeight - cellHeight * rows) / 2;

            int x = (int) event.getX();
            int y = (int) event.getY();

            //Log.i("Touch Location", "(" + x)

            int col = (x - startX) / cellWidth;
            int row = (y - startY) / cellHeight;

            if (row >= 0 && row < rows && col >= 0 && col < cols) {
                if (onCellClickListener != null) {
                    onCellClickListener.onCellClick(row, col);
                }

                if(TouchableState[row][col]==1){
                    PawnStateMatrix[row][col] = (PawnStateMatrix[row][col] + 1) % 3;
                    Log.i("Update State", PawnStateMatrix[row][col] + "");
                    updateIcons();

                    Log.i("View", "Now Click: " + row + ", " + col);
                }
            }
        }
        return true;
    }

    // 设置网格的行数和列数
    public void setGrid(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        icons = new Bitmap[rows][cols];  // 更新图标数组
        // 初始化所有网格的图标为默认图片
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                icons[r][c] = defaultIcon;
            }
        }
        invalidate();  // 刷新视图，重新绘制
    }

    // 设置每个网格的图标
    public void setIcon(int row, int col, int resId) {
        if (row < rows && col < cols) {
            icons[row][col] = BitmapFactory.decodeResource(getResources(), resId);
            invalidate();  // 刷新视图，重新绘制
        }
    }

    public void setPawnStateMatrix(int[][] State, int[][]InitState)
    {
        PawnStateMatrix = State;
        TouchableState = InitState;

        Log.i("Init", Arrays.deepToString(PawnStateMatrix));
        Log.i("Init", Arrays.deepToString(TouchableState));

    }

    public void setOnCellClickListener(OnCellClickListener listener) {
        this.onCellClickListener = listener;
    }

    public interface OnCellClickListener {
        void onCellClick(int i, int j);
    }

    private void updateIcons()
    {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                switch (PawnStateMatrix[r][c])
                {
                    case 0:
                        icons[r][c] = defaultIcon;
                        break;
                    case 1:
                        if(TouchableState[r][c] == 1) {
                            icons[r][c] = oActive;
                        }
                        else {
                            icons[r][c] = oInactive;
                        }
                        break;
                    case 2:
                        if(TouchableState[r][c] == 1) {
                            icons[r][c] = xActive;
                        }
                        else {
                            icons[r][c] = xInactive;
                        }
                }
            }
        }

        invalidate();
    }
}
