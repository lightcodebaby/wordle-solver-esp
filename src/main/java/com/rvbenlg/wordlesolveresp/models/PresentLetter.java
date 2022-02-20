package com.rvbenlg.wordlesolveresp.models;

public class PresentLetter extends Letter {

    private int position;

    public PresentLetter(String letter, int position) {
        super(letter);
        setPosition(position);
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
