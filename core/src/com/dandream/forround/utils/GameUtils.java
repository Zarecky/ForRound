package com.dandream.forround.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.utils.Array;

public class GameUtils {

    public static Animation<TextureRegion> parseSpriteSheet(String fileName, int frameCols, int frameRows,
                                                            float frameDuration, Animation.PlayMode mode) {
        Texture t = new Texture(Gdx.files.internal(fileName));
        t.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        int frameWidth = t.getWidth() / frameCols;
        int frameHeight = t.getHeight() / frameRows;

        TextureRegion[][] temp = TextureRegion.split(t, frameWidth, frameHeight);
        TextureRegion[] frames = new TextureRegion[frameCols * frameRows];

        int index = 0;
        for (int i = 0; i < frameRows; i++) {
            for (int j = 0; j < frameCols; j++) {
                frames[index] = temp[i][j];
                index++;
            }
        }

        Array<TextureRegion> framesArray = new Array<TextureRegion>(frames);
        return new Animation<TextureRegion>(frameDuration, framesArray, mode);
    }

    public static Animation<TextureRegion> parseSpriteSheet(String fileName, int frameCols, int frameRows,
                                                            int[] frameIndices, float frameDuration, Animation.PlayMode mode) {
        Texture t = new Texture(Gdx.files.internal(fileName), true);
        t.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        int frameWidth = t.getWidth() / frameCols;
        int frameHeight = t.getHeight() / frameRows;

        TextureRegion[][] temp = TextureRegion.split(t, frameWidth, frameHeight);
        TextureRegion[] frames = new TextureRegion[frameCols*frameRows];

        int index = 0;
        for (int i = 0; i < frameRows; i++) {
            for (int j = 0; j < frameCols; j++) {
                frames[index] = temp[i][j];
                index++;
            }
        }

        Array<TextureRegion> framesArray = new Array<TextureRegion>();
        for (int n = 0; n < frameIndices.length; n++) {
            int i = frameIndices[n];
            framesArray.add(frames[i]);
        }

        return new Animation<TextureRegion>(frameDuration, framesArray, mode);
    }

    public static Animation<TextureRegion> parseImageFiles(String fileNamePrefix, String fileNameSuffix,
                                                           int frameCount, float frameDuration, Animation.PlayMode mode) {
        TextureRegion[] frames  = new TextureRegion[frameCount];

        for (int i = 0; i < frameCount; i++) {
            String fileName = fileNamePrefix + i + fileNameSuffix;
            Texture tex = new Texture(Gdx.files.internal(fileName));
            tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            frames[i] = new TextureRegion(tex);
        }

        Array<TextureRegion> framesArray = new Array<TextureRegion>(frames);
        return new Animation<TextureRegion>(frameDuration, framesArray, mode);
    }

    public static Object getContactObject(Contact theContact, Class theClass) {
        Object objA = theContact.getFixtureA().getBody().getUserData();
        Object objB = theContact.getFixtureB().getBody().getUserData();

        if (objA.getClass().equals(theClass)) return objA;
        else if (objB.getClass().equals(theClass)) return objB;
        else return null;
    }

    public static Object getContactObject(Contact theContact, Class theClass, String fixtureName) {
        Object objA = theContact.getFixtureA().getBody().getUserData();
        String nameA = (String) theContact.getFixtureA().getUserData();
        Object objB = theContact.getFixtureB().getBody().getUserData();
        String nameB = (String) theContact.getFixtureB().getUserData();

        if (objA.getClass().equals(theClass) && nameA.equals(fixtureName)) return objA;
        else if (objB.getClass().equals(theClass) && nameB.equals(fixtureName)) return objB;
        else return null;
    }
}
