package com.mygdx.game.GameManager;

public class Result {
    String name;
    float time;

    public Result(String name, float time) {
        this.name = name;
        this.time = time;
    }
    public Result() {
        this.name = "NONAME";
        this.time = 1000;
    }

    public String toString(){
        return "Name:"+this.name+" Time:"+(int)this.time+"s";
    }
}
