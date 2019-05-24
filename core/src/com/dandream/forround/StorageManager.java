package com.dandream.forround;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class StorageManager {

    private static String TITLE = "ForRoundStorage";
    private static String CURRENT_SCORE = "currentScore";
    private static String BEST_SCORE = "bestScore";

    private static Preferences storage;

    public static void load() {
        storage = Gdx.app.getPreferences(TITLE);
    }

    public static void setScore(int current) {
        if (!storage.contains(BEST_SCORE))
            storage.putInteger(BEST_SCORE, current);

        storage.putInteger(CURRENT_SCORE, current);
        if (storage.getInteger(BEST_SCORE) < current)
            storage.putInteger(BEST_SCORE, current);

        flush();
    }

    public static int getCurrentScore() {
        if (!storage.contains(CURRENT_SCORE)) return -1;
        else return storage.getInteger(CURRENT_SCORE);
    }

    public static int getBestScore() {
        if (!storage.contains(BEST_SCORE)) return -1;
        else return storage.getInteger(BEST_SCORE);
    }

    public static boolean isExistBest() {
        return storage.contains(BEST_SCORE);
    }

    public static void flush() {
        storage.flush();
    }
}
