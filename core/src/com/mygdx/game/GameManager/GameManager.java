package com.mygdx.game.GameManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

public class GameManager {
    //private static final String ANIMATED = "Animated";
    //private static final String SOUND = "Sound";
    //public String userID;

    public Json json;
    public Results results;
    public static final GameManager INSTANCE = new GameManager();

    public void loadResults() {
        FileHandle file = Gdx.files.local("results.json");
        if (!file.exists()) {
            results = new Results();
            System.out.println("NEW file");
        } else {
            results = json.fromJson(Results.class, file.readString());
        }
    }
    private GameManager() {
        json = new Json();
        loadResults();
    }
    public void addResult(String name, float time){
        results.results.add(new Result(name, time));
        FileHandle file = Gdx.files.local("results.json");
        file.writeString(json.toJson(results), false);
    }

    public String getResults() {
        return results.toString();
    }
}
