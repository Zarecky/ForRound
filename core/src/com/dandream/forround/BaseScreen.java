package com.dandream.forround;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public abstract class BaseScreen implements Screen, InputProcessor {

    public static int VIEW_WIDTH;
    public static int VIEW_HEIGHT;
    public static int UI_WIDTH;
    public static int UI_HEIGHT;

    protected BaseGame game;

    protected Stage mainStage;
    protected Stage uiStage;
    protected Table uiTable;

    private Color backgroundColor;

    protected InputMultiplexer multiplexer;

    private boolean paused;

    public BaseScreen(BaseGame game) {
        this.game = game;

        VIEW_HEIGHT = Gdx.graphics.getHeight();
        VIEW_WIDTH = Gdx.graphics.getWidth();
        UI_HEIGHT = Gdx.graphics.getHeight();
        UI_WIDTH = Gdx.graphics.getWidth();

        mainStage = new Stage(new StretchViewport(VIEW_WIDTH, VIEW_HEIGHT));
        uiStage = new Stage(new StretchViewport(UI_WIDTH, UI_HEIGHT));
        uiTable = new Table();
        uiTable.setFillParent(true);
        uiStage.addActor(uiTable);

        paused = false;

        backgroundColor = new Color(0, 0, 0, 1);

        multiplexer = new InputMultiplexer(this, uiStage, mainStage);
        Gdx.input.setInputProcessor(multiplexer);

        create();
    }

    public BaseScreen(BaseGame game, float r, float g, float b, float a) {
        this(game);
        backgroundColor = new Color(r, g, b, a);
    }

    public BaseScreen(BaseGame game, Color color) {
        this(game);
        backgroundColor = color;
    }

    public BaseScreen(BaseGame game, int color) {
        this(game);
        backgroundColor = new Color(color);
    }

    public abstract void create();

    public abstract void update(float delta);

    @Override
    public void render(float delta) {
        uiStage.act(delta);

        if (!isPaused()) {
            mainStage.act(delta);
            update(delta);
        } else updateInPause(delta);

        Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glViewport(0, UI_HEIGHT - VIEW_HEIGHT, VIEW_WIDTH, VIEW_HEIGHT);
        mainStage.draw();
        Gdx.gl.glViewport(0, 0, UI_WIDTH, UI_HEIGHT);
        uiStage.draw();
    }

    public void updateInPause(float delta) {}

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean b) {
        paused = b;
    }

    public void tooglePaused() {
        paused = !paused;
    }

    public void setBackgroundColor(Color color) {
        backgroundColor = color;
    }

    public void setBackgroundColor(int color) {
        backgroundColor = new Color(color);
    }

    public void setBackgroundColor(float r, float g, float b, float a) {
        backgroundColor = new Color(r, g, b, a);
    }

    @Override
    public void resize(int width, int height) {
        mainStage.getViewport().update(width, height, true);
        uiStage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() {}
    @Override public void show() {
        multiplexer = new InputMultiplexer(this, uiStage, mainStage);
        Gdx.input.setInputProcessor(multiplexer);
    }
    @Override public void hide() {}


    @Override public boolean keyDown(int keycode) {return false;}
    @Override public boolean keyUp(int keycode) {return false;}
    @Override public boolean keyTyped(char character) {return false;}
    @Override public boolean mouseMoved(int screenX, int screenY) {return false;}
    @Override public boolean scrolled(int amount) {return false;}

    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) {return false;}
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) {return false;}
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) {return false;}
}
