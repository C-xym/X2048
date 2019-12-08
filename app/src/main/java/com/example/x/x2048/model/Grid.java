package com.example.x.x2048.model;

import android.content.SharedPreferences;
import android.util.ArrayMap;

import java.util.ArrayList;
import java.util.Arrays;

public class Grid {

    public static final int MOVE_UP = 0;
    public static final int MOVE_RIGHT = 1;
    public static final int MOVE_DOWN = 2;
    public static final int MOVE_LEFT = 3;

    private static Grid sIns;

    private int[] arr = new int[16];
    private int[] old = new int[16];

    public int[] getTrans() {
        return trans;
    }

    private int[] trans =new int[16];
    private int blank;

    public int getNewblock() {
        return mNewblock;
    }

    public int getNewType() {
        return mNewType;
    }

    private int mNewblock;
    private int mNewType;
    private boolean isBack;

    public boolean isGameOver() {
        return isGameOver;
    }

    private boolean isGameOver;

    private ArrayMap<Integer, Integer> mMoveList;
    private ArrayMap<Integer, Integer> mNewList;

    public ArrayList<Integer> getRemoveList() {
        return mRemoveList;
    }

    private ArrayList<Integer> mRemoveList;

    public int[] getArr() {
        return arr;
    }

    public ArrayMap<Integer, Integer> getMoveList() {
        return mMoveList;
    }

    public ArrayMap<Integer, Integer> getNewList() {
        return mNewList;
    }


    private Grid() {
        mMoveList = new ArrayMap<>();
        mNewList = new ArrayMap<>();
        mRemoveList=new ArrayList<>();
        blank = 16;
        isBack = false;
        isGameOver = false;
    }

    public static Grid getInstance() {
        if (sIns == null) {
            sIns = new Grid();
        }
        return sIns;
    }

    public void load(SharedPreferences preferences) {
        for (int i = 0; i < 16; i++) {
            arr[i] = preferences.getInt(String.valueOf(i), 0);
        }
        blank = preferences.getInt("blank", 16);
        isBack = false;
    }

    public void save(SharedPreferences preferences) {
        SharedPreferences.Editor editor = preferences.edit();
        for (int i = 0; i < 16; i++) {
            editor.putInt(String.valueOf(i), arr[i]);
        }
        editor.putInt("blank", blank);
        editor.apply();
    }

    public void back() {
        if (isBack) {
            arr = old.clone();
            isBack = false;
            isGameOver = false;
            int b = 0;
            for (int i = 0; i < 16; i++) {
                if (arr[i] == 0) b++;
            }
            blank = b;
        }
    }

    public void reStart() {
        arr = new int[16];
        blank = 16;
        mNewList.clear();
        mMoveList.clear();
        mRemoveList.clear();
        isBack = false;
        isGameOver = false;
    }

    public void move(int orientation) {

        if (blank == 16) {
            genNewBlock();
            isBack = true;
            return;
        }
        mMoveList.clear();
        mNewList.clear();
        mRemoveList.clear();

        isGameOver = gameOver();
        if (isGameOver) {
            return;
        }

        int[] temp = arr.clone();

        for(int i=0;i<16;i++){
            trans[i]=i;
        }

        switch (orientation) {
            case MOVE_UP:
                moveUp();
                break;
            case MOVE_RIGHT:
                moveRight();
                break;
            case MOVE_DOWN:
                moveDown();
                break;
            case MOVE_LEFT:
                moveLeft();
                break;
            default:
                break;
        }

        if (!mMoveList.isEmpty()) {
            genNewBlock();
            isBack = true;
            if (!Arrays.equals(temp, arr)) {
                old = temp;
            }
        }
    }

    private boolean gameOver() {
        if (blank != 0) return false;
        for (int i = 1; i < 15; i += 4) {
            if (arr[i] == arr[i - 1] || arr[i] == arr[i + 1] || arr[i + 1] == arr[i + 2])
                return false;
        }

        for (int i = 4; i < 8; i++) {
            if (arr[i] == arr[i - 4] || arr[i] == arr[i + 4] || arr[i + 4] == arr[i + 8])
                return false;
        }

        return true;
    }


    private void moveUp() {

        boolean[] booleans = new boolean[16];//false
        for (int a = 4; a < 16; a++) {
            int i = a;
            if (arr[i] == 0) continue;
            while (i - 4 >= 0 && arr[i - 4] == 0) {
                i = i - 4;
            }
            if (i - 4 >= 0 && arr[i - 4] == arr[a] && !booleans[i - 4]) {
                arr[a] = 0;
                arr[i - 4] *= 2;
                booleans[i - 4] = true;
                mMoveList.put(a, i - 4);
                trans[i-4]=a;
                mNewList.put(i - 4, arr[i - 4]);
                mRemoveList.add(i-4);
                blank++;
            } else {
                if (a != i) {
                    arr[i] = arr[a];
                    arr[a] = 0;
                    mMoveList.put(a, i);
                    trans[i]=a;
                }
            }
        }
    }

    private void moveDown() {
        boolean[] booleans = new boolean[16];//false
        for (int a = 11; a >= 0; a--) {
            int i = a;
            if (arr[i] == 0) continue;
            while (i + 4 < 16 && arr[i + 4] == 0) {
                i = i + 4;
            }
            if (i + 4 < 16 && arr[i + 4] == arr[a] && !booleans[i + 4]) {
                arr[a] = 0;
                arr[i + 4] *= 2;
                booleans[i + 4] = true;
                mMoveList.put(a, i + 4);
                trans[i+4]=a;
                mNewList.put(i + 4, arr[i + 4]);
                mRemoveList.add(i+4);
                blank++;
            } else {
                if (a != i) {
                    arr[i] = arr[a];
                    arr[a] = 0;
                    mMoveList.put(a, i);
                    trans[i]=a;
                }
            }
        }
    }

    private void moveLeft() {
        boolean[] booleans = new boolean[16];//false
        for (int a = 1; a < 16; a++) {
            int i = a;
            if (arr[i] == 0) continue;
            while (i % 4 - 1 >= 0 && arr[i - 1] == 0) {
                i = i - 1;
            }
            if (i % 4 - 1 >= 0 && arr[i - 1] == arr[a] && !booleans[i - 1]) {
                arr[a] = 0;
                arr[i - 1] *= 2;
                booleans[i - 1] = true;
                mMoveList.put(a, i - 1);
                trans[i-1]=a;
                mNewList.put(i - 1, arr[i - 1]);
                mRemoveList.add(i-1);
                blank++;
            } else {
                if (a != i) {
                    arr[i] = arr[a];
                    arr[a] = 0;
                    mMoveList.put(a, i);
                    trans[i]=a;
                }
            }
        }
    }

    private void moveRight() {
        boolean[] booleans = new boolean[16];//false
        for (int a = 14; a >= 0; a--) {
            int i = a;
            if (arr[i] == 0) continue;
            while (i % 4 + 1 < 4 && arr[i + 1] == 0) {
                i = i + 1;
            }
            if (i % 4 + 1 < 4 && arr[i + 1] == arr[a] && !booleans[i + 1]) {
                arr[a] = 0;
                arr[i + 1] *= 2;
                booleans[i + 1] = true;
                mMoveList.put(a, i + 1);
                trans[i+1]=a;
                mNewList.put(i + 1, arr[i + 1]);
                mRemoveList.add(i+1);
                blank++;
            } else {
                if (a != i) {
                    arr[i] = arr[a];
                    arr[a] = 0;
                    mMoveList.put(a, i);
                    trans[i]=a;
                }
            }
        }
    }

    private void genNewBlock() {
        if (blank == 0) return;
        int n = (int) (Math.random() * 100) % blank;
        int i = 0;
        while (n >= 0) {
            if (arr[i] == 0) {
                --n;
            }
            ++i;
        }
        int f = (int) (Math.random() * 10);
        mNewblock=i-1;
        if (f < 2) {
            arr[i - 1] = 4;
            mNewList.put(i - 1, 4);
            mNewType=4;
        } else {
            arr[i - 1] = 2;
            mNewList.put(i - 1, 2);
            mNewType=2;
        }
        blank--;
    }
}
