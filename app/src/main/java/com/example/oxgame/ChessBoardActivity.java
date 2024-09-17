package com.example.oxgame;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.content.res.Resources;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class ChessBoardActivity extends AppCompatActivity implements ChessBoardView.OnCellClickListener {

    private ChessBoardClass chessBoard;
    private Timer timer;
    private int secends = 0;
    TimerTask timerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chess_board);

        ChessBoardView chessBoardView = findViewById(R.id.chessBoardView);

        Intent intent = getIntent();
        int row = intent.getIntExtra("Row", 4);

        chessBoard = new ChessBoardClass(row, getBaseContext());

        // 设置网格的行数和列数
        chessBoardView.setGrid(row, row);
        chessBoardView.setPawnStateMatrix(chessBoard.getMatrix(), chessBoard.getTouchableMatrix());  // 示例：设置为8行8列的网格
        chessBoardView.setOnCellClickListener(this);
        chessBoardView.init();

        setupTimer();
        setupMusic(intent);


        ImageView returnpre = findViewById(R.id.returnpre_image);
        ImageView home = findViewById(R.id.home);
        ImageView setting = findViewById(R.id.setting);

        returnpre.setOnClickListener(view -> {
            finish();
        });

        home.setOnClickListener(view -> {
            Intent intent1 = new Intent(this, StartMenu.class);
            startActivity(intent1);
            finish();
        });

        setting.setOnClickListener(view -> {
            Intent intent2 = new Intent(this, SettingActivity.class);
            startActivity(intent2);
        });
    }

    private void setupMusic(Intent intent) {
        Intent musicIntent = new Intent(this, MusicService.class);
        musicIntent.putExtra("Action", 1);
        int ResourceID = R.raw.longdongyinsui;
        switch (chessBoard.rows)
        {
            case 4:
                ResourceID = R.raw.longdongyinsui;
                break;
            case 6:
                ResourceID = R.raw.ganggangjiji;
                break;
            case 8:
                ResourceID = R.raw.jinqiusongshuang;
                break;
        }
        musicIntent.putExtra("ResourceID", ResourceID);
        startService(musicIntent);
    }

    private void setupTimer() {
        timer = new Timer();

        timerTask = new TimerTask() {
            @Override
            public void run() {
                processTimer(++secends);
                Log.i("timer", "timer called");
            }
        };

        timer.schedule(timerTask,1, 1000);
    }

    @Override
    public void onCellClick(int i, int j) {
        Log.i("CallBack", "Detect Click");
        chessBoard.setPawn(i, j, (chessBoard.getPawn(i, j) + 1) % 3);
        Log.i("CallBack", Arrays.deepToString(chessBoard.getMatrix()));
        if (chessBoard.isWin()) {
            timerTask.cancel();
            TextView timerText = findViewById(R.id.timer);
            Intent WinIntent = new Intent(this, WinActivity.class);
            WinIntent.putExtra("Time", timerText.getText());
            WinIntent.putExtra("Difficulty", chessBoard.rows);
            WinIntent.putExtra("Second",secends);
            startActivity(WinIntent);
            finish();
        }
    }

    @SuppressLint("SetTextI18n")
    private void processTimer(int currentSeconds) {
        int minute = (currentSeconds / 60) % 60;
        int second = currentSeconds % 60;

        TextView timerText = findViewById(R.id.timer);
        String minuteString;
        if (minute<10)
        {
            minuteString = "0" + minute;
        }
        else{
            minuteString = "" + minute;
        }
        String secondString;
        if (second<10)
        {
            secondString = "0" + second;
        }
        else{
            secondString = "" + second;
        }
        timerText.setText(minuteString + ":" + secondString);
    }
}

class ChessBoardClass {
    private int[] matrix;
    private int[][] touchableMatrix;
    public int rows;
    Context context;

    public static int[] readDataFile(Resources resources, int resourceId, int rows) throws IOException {
        // 获取输入流
        InputStream inputStream = resources.openRawResource(resourceId);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        // 定义一维数组存储数据（6行6列共36个元素）
        int[] dataArray = new int[rows * rows];

        String line;
        int index = 0; // 用来记录一维数组的当前位置

        // 按行读取文件
        while ((line = reader.readLine()) != null) {
            // 将每行的字符解析为整数并存入一维数组
            for (int col = 0; col < line.length(); col++) {
                dataArray[index] = Character.getNumericValue(line.charAt(col));
                index++;
            }
        }

        reader.close();
        return dataArray;
    }

    public int[][] getTouchableMatrix() {
        return touchableMatrix;
    }

    public ChessBoardClass(int rows, Context context) {
        this.rows = rows;
        matrix = new int[rows * rows];
        touchableMatrix = new int[rows][rows];
        this.context = context;
        if (rows == 4) {
            InitMatrix();
            createRandomMatrix();
            createRandomMatrix();
            createRandomMatrix();
        } else if (rows == 6) {
            try {
                matrix = readDataFile(context.getResources(), R.raw.quiz6, 6);
            } catch (IOException ie) {
                ie.printStackTrace();
            }

            int rotateMode = (int) (Math.random() * 4);
            switch (rotateMode) {
                case 0:
                    matrix = rotate90Clockwise(matrix, rows);
                    break;
                case 1:
                    matrix = rotate90Clockwise(matrix, rows);
                    matrix = rotate90Clockwise(matrix, rows);
                    break;
                case 2:
                    matrix = rotate90Clockwise(matrix, rows);
                    matrix = rotate90Clockwise(matrix, rows);
                    matrix = rotate90Clockwise(matrix, rows);
                    break;

            }
            int transMode = (int) (Math.random() * 2);
            if (transMode == 1) {
                matrix = transpose(matrix, rows);
            }
        } else if (rows == 8) {
            try {
                int fileIndex = (int) (Math.random() * 2);
                if (fileIndex == 0) {
                    matrix = readDataFile(context.getResources(), R.raw.quiz8_1, 8);
                } else {
                    matrix = readDataFile(context.getResources(), R.raw.quiz8_2, 8);
                }

            } catch (IOException ie) {
                ie.printStackTrace();
            }
            int rotateMode = (int) (Math.random() * 4);
            switch (rotateMode) {
                case 0:
                    matrix = rotate90Clockwise(matrix, rows);
                    break;
                case 1:
                    matrix = rotate90Clockwise(matrix, rows);
                    matrix = rotate90Clockwise(matrix, rows);
                    break;
                case 2:
                    matrix = rotate90Clockwise(matrix, rows);
                    matrix = rotate90Clockwise(matrix, rows);
                    matrix = rotate90Clockwise(matrix, rows);
                    break;

            }
            int transMode = (int) (Math.random() * 2);
            if (transMode == 1) {
                matrix = transpose(matrix, rows);
            }
        }


        generatePuzzle();
    }

    public void InitMatrix() {
        int state = 2;
        for (int i = 0; i < rows; i++) {
            state = 3 - state;
            for (int j = 0; j < rows; j++) {
                setPawn(i, j, 3 - state);
                state = 3 - state;
                touchableMatrix[i][j] = 0;
            }
        }
    }

    public int getPawn(int row, int col) {
        return matrix[row * rows + col];
    }

    public void setPawn(int row, int col, int desiredState) {
        matrix[row * rows + col] = desiredState;
    }

    public int[] getMatrixLinear() {
        return matrix;
    }

    public int[][] getMatrix() {
        int[][] ret = new int[rows][rows];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < rows; j++) {
                ret[i][j] = getPawn(i, j);
            }
        }

        return ret;
    }

    private void swapMatrixElement(int i1, int j1, int i2, int j2) {
        int t = getPawn(i1, j1);
        setPawn(i1, j1, getPawn(i2, j2));
        setPawn(i2, j2, t);
    }

    public boolean isValidColumn(int column) {
        boolean valid = true;
        int maxContinuousO = 0;
        int maxContinuousX = 0;
        int continuousO = 0;
        int continuousX = 0;
        boolean onCountingO = false;
        boolean onCountingX = false;

        for (int j = 0; j < rows; j++) {
            if (getPawn(j, column) == 1) { // 假设 1 表示 O
                if (onCountingO) {
                    continuousO++;
                } else {
                    onCountingO = true;
                    continuousO = 1; // 初始化为1
                }

                if (onCountingX) {
                    onCountingX = false;
                    maxContinuousX = Math.max(maxContinuousX, continuousX);
                    continuousX = 0;
                }
            } else { // 假设 0 表示 X
                if (onCountingX) {
                    continuousX++;
                } else {
                    onCountingX = true;
                    continuousX = 1; // 初始化为1
                }

                if (onCountingO) {
                    onCountingO = false;
                    maxContinuousO = Math.max(maxContinuousO, continuousO);
                    continuousO = 0;
                }
            }
        }

        // 最后一次的连续O或X检查
        maxContinuousO = Math.max(maxContinuousO, continuousO);
        maxContinuousX = Math.max(maxContinuousX, continuousX);

        if (maxContinuousO > 2 || maxContinuousX > 2) {
            valid = false;
        }

        return valid;
    }

    public boolean leftCheck(int x, int y) {
        if (y <= 1) return true;
        int state = getPawn(x, y);
        int continuous = 1;

        for (int i = y - 1; i >= 0; i--) {
            if (getPawn(x, i) != state) {
                return true;
            } else {
                continuous++;
                if (continuous >= 3) {
                    return false;
                }
            }
        }

        return true;
    }

    private int getRowNumO(int row) {
        int ret = 0;
        for (int j = 0; j < rows; j++) {
            if (getPawn(row, j) == 1)
                ret++;
        }
        return ret;
    }

    private int getRowNumX(int row) {
        int ret = 0;
        for (int j = 0; j < rows; j++) {
            if (getPawn(row, j) == 2)
                ret++;
        }
        return ret;
    }

    private int getColumnNumO(int col) {
        int ret = 0;
        for (int j = 0; j < rows; j++) {
            if (getPawn(j, col) == 1)
                ret++;
        }
        return ret;
    }

    private int getColumnNumX(int col) {
        int ret = 0;
        for (int j = 0; j < rows; j++) {
            if (getPawn(j, col) == 2)
                ret++;
        }
        return ret;
    }

    public void createRandomMatrix() {
        int expectToSwap = 0;
        int swapA = -1;
        int swapB = -1;
        int stepB = 0;

        for (int i = 0; i < rows; i++) {
            switch (expectToSwap) {
                case 0:
                    swapA = (int) (Math.random() * (rows - 1));
                    do {
                        stepB = (int) (Math.random() * (rows - 1));
                    } while (stepB % 2 != 1);
                    swapB = (swapA + stepB) % rows;
                    for (int k = 0; k < rows / 2; swapB = (swapB + 2) % rows, k++) {
                        if (getPawn(swapA, i) == getPawn(swapB, i)) continue;
                        swapMatrixElement(swapA, i, swapB, i);
                        if (isValidColumn(i) && leftCheck(swapA, i) && leftCheck(swapB, i)) break;
                        else swapMatrixElement(swapA, i, swapB, i);
                    }
                    break;
                case 1:
                    do {
                        stepB = (int) (Math.random() * (rows - 1));
                    } while (stepB % 2 != 1);
                    swapB = (swapA + 1) % rows;
                    for (int k = 0; k < rows / 2; swapB = (swapB + 2) % rows, k++) {
                        if (getPawn(swapA, i) == getPawn(swapB, i)) continue;
                        swapMatrixElement(swapA, i, swapB, i);
                        if (isValidColumn(i) && leftCheck(swapB, i)) break;
                        else swapMatrixElement(swapA, i, swapB, i);
                    }
                    break;
                default:
                    swapMatrixElement(swapA, i, swapB, i);
                    break;
            }

            expectToSwap = 0;
            int numO = 0;
            int numX = 0;

            if (i >= 1 && i < rows - 1) {
                if (getPawn(swapA, i) == getPawn(swapA, i - 1) && getPawn(swapA, i) == getPawn(swapA, i + 1)) {
                    expectToSwap++;
                }
            }

            //getRowInfo(swapA, numO, numX);
            numO = getRowNumO(swapA);
            numX = getRowNumX(swapA);
            if (numO != numX) expectToSwap++;

            if (i >= 1 && i < rows - 1) {
                if (getPawn(swapB, i) == getPawn(swapB, i - 1) && getPawn(swapB, i) == getPawn(swapB, i + 1)) {
                    expectToSwap++;
                    swapA = swapB;
                }
            }

            numO = getRowNumO(swapA);
            numX = getRowNumX(swapA);

            if (numO != numX) expectToSwap++;
        }
    }

    private void generatePuzzle() {
        // Randomly take away some pawns to create a random game
        int randTakeAway = (int) (rows * rows * 0.9);
        Log.i("Init", "randTakeAway: " + randTakeAway);
        for (int i = 0; i < randTakeAway; i++) {
            int x = (int) (Math.random() * (rows));
            int y = (int) (Math.random() * (rows));
            setPawn(x, y, 0);
            touchableMatrix[x][y] = 1;
        }
    }

    // 检查是否有胜利
    public boolean isWin() {
        boolean win = true;
        System.out.println("Checking if Win");

        // Check Rows
        for (int i = 0; i < rows; i++) {
            win = isValidRow(i);
            // win = !isExistRow(i);
            int numO = getRowNumO(i);
            int numX = getRowNumX(i);
            System.out.printf("Row%d: NumO=%d, NumX=%d%n", rows - 1 - i, numO, numX);

            if (!(numO == rows / 2 && numO == numX)) {
                win = false;
                return win; // 直接返回结果，无需 break
            }
        }

        // Check Columns
        for (int i = 0; i < rows; i++) {
            win = isValidColumn(i);
            // win = !isExistColumn(i);

            int numO = getColumnNumO(i);
            int numX = getColumnNumX(i);
            System.out.printf("Column%d: NumO=%d, NumX=%d%n", i, numO, numX);

            if (!(numO == rows / 2 && numO == numX)) {
                win = false;
                return win; // 直接返回结果，无需 break
            }
        }

        return win;
    }

    // 检查指定行是否有效
    public boolean isValidRow(int row) {
        boolean valid = true;
        int maxContinuousO = 0;
        int maxContinuousX = 0;
        int continuousO = 0;
        int continuousX = 0;
        boolean onCountingO = false;
        boolean onCountingX = false;

        for (int j = 0; j < rows; j++) {
            if (getPawn(row, j) == 1) { // 假设1表示O
                if (onCountingO) {
                    continuousO++;
                } else {
                    onCountingO = true;
                    continuousO = 1; // 初始化为1
                }

                if (onCountingX) {
                    onCountingX = false;
                    maxContinuousX = Math.max(maxContinuousX, continuousX);
                    continuousX = 0;
                }
            } else { // 假设0表示X
                if (onCountingX) {
                    continuousX++;
                } else {
                    onCountingX = true;
                    continuousX = 1; // 初始化为1
                }

                if (onCountingO) {
                    onCountingO = false;
                    maxContinuousO = Math.max(maxContinuousO, continuousO);
                    continuousO = 0;
                }
            }
        }

        // 最后一次的连续O或X检查
        maxContinuousO = Math.max(maxContinuousO, continuousO);
        maxContinuousX = Math.max(maxContinuousX, continuousX);

        if (maxContinuousO > 2 || maxContinuousX > 2) {
            valid = false;
        }
        return valid;
    }

    public static int[] rotate90Clockwise(int[] matrix, int rows) {
        int[] result = new int[rows * rows];

        // 遍历原矩阵的每个元素并映射到旋转后的新位置
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < rows; j++) {
                result[j * rows + (rows - 1 - i)] = matrix[i * rows + j];
            }
        }

        return result;
    }

    public static int[] transpose(int[] matrix, int rows) {
        int[] result = new int[rows * rows];

        // 遍历原矩阵的每个元素并映射到转置后的新位置
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < rows; j++) {
                result[j * rows + i] = matrix[i * rows + j];
            }
        }

        return result;
    }

}