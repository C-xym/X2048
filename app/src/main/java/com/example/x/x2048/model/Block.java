package com.example.x.x2048.model;

public class Block {

    private int mId;
    int mPostion;//0-15
    int x;//0-3
    int y;//0-3
    int mValue;//2,4,8,.....4096

    Block(int id, int pos, int value) {
        this.mId = id;
        this.mPostion = pos;
        this.x = pos % 4;
        this.y = pos / 4;
        this.mValue = value;
    }

    void set(int pos, int value) {
        this.mPostion = pos;
        this.x = pos % 4;
        this.y = pos / 4;
        this.mValue = value;
    }

    int getId() {
        return mId;
    }
}
