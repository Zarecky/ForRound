package com.dandream.forround;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public abstract class BaseGame extends Game {

    public static int VIEWPORT_WIDTH;
    public static int VIEWPORT_HEIGHT;

    Skin skin;

    public BaseGame() {
        skin = new Skin();
    }

    public abstract void create();

    public void dispose() {
        skin.dispose();
    }
}
