package com.example.oxgame;

public class ChessBoard {
    private int[] matrix;
    private int rows;

    public ChessBoard(int rows){
        this.rows = rows;
        matrix = new int[rows * rows];
    }

    public void InitMatrix()
    {
        for(int i=0;i<rows;i++)
        {
            matrix[i] = 0;
        }
    }

    public int getPawn(int row, int col)
    {
        return matrix[row * rows + col];
    }

    public void setPawn(int row, int col, int desiredState)
    {
        matrix[row * rows + col] = desiredState;
    }

    public int[] getMatrixLinear()
    {
        return matrix;
    }

    public int[][] getMatrix()
    {
        int[][] ret = new int[rows][rows];
        for(int i=0;i<rows;i++)
        {
            for(int j=0;j<rows;j++)
            {
                ret[i][j] = getPawn(i, j);
            }
        }

        return ret;
    }
}
