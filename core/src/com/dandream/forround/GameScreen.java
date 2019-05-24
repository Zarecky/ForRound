package com.dandream.forround;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import java.util.ArrayList;
import java.util.Random;

public class GameScreen extends BaseScreen {

    private BaseActor background;
    private BaseActor playerCircle;
    private BaseActor border;
    private float speed;
    private float scaleSpeed;
    private float scaleSpeedBorder;
    private ArrayList<PhysicsActor> circleList;
    private ArrayList<Float> circleScaleSpeedList;
    private float timeAddCurcle;
    private boolean isInstance;

    private final Color[] colorsCircle = {Color.ORANGE, Color.ROYAL, Color.PURPLE, Color.GOLD,
    Color.SKY, Color.LIGHT_GRAY, Color.TAN, Color.FIREBRICK, Color.FOREST, Color.PINK};
    private final Color[] colorsBack = {Color.BROWN, Color.CORAL, Color.BLUE,Color.GOLDENROD, Color.FOREST, Color.MAGENTA};
    private float timeBack;
    public boolean gameOver;
    private boolean postHidePauseMenu;
    private float timePostPauseMenu;
    private int score;
    private float timeScore;
    private float timeScoreVisible;
    public float timeBegin = 0;

    private Label scoreLabel;
    private Label exitLabel;
    private Label shadowExitLabel;
    private Label restart;
    private Label quit;
    private Label bestPauseLabel;
    private Label scorePauseLabel;
    private Group startingToLabel;
    private Label startingCountLabel;
    private Group pauseButton;
    private BaseActor bgGameOver;
    private BaseActor pauseTrigger;
    private Label updatedBestScore;

    private Image menuBackTop;
    private Image menuBackBottom;

    private float scalePrevPause;
    private float posPrevPause;
    private float scalePrevBorder;

    private boolean isRestarted;
    private boolean isQuited;

    public GameScreen(BaseGame game) {
        super(game);
    }

    @Override
    public void create() {
        createUI();
        createBackground();
        createPlayer();
        createBorder();
    }

    @Override
    public void show() {
        super.show();

        ((StartGame)game).gameCallback.sendMessage(StartGame.LOAD_INTERSTITIAL);

        MathUtils.random = new Random(System.currentTimeMillis());
        score = 100;
        timeScore = 0;
        timeScoreVisible = 0;
        timeBegin = 0;
        postHidePauseMenu = false;
        timePostPauseMenu = 10;

        isRestarted = false;
        isQuited = false;

        mainStage.clear();
        mainStage.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (pointer == 0) {
                    scaleSpeed *= -1;
                    return true;
                }
                return false;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                scaleSpeed *= -1;
            }
        });

        showUI();
        showBackground();
        showPlayer();
        showBorder();
        createCircles();

        if (((StartGame) game).isSoundsActive && !((StartGame)game).isPlayingBaseMelody()) ((StartGame)game).playBaseMelody();

        scoreLabel.toFront();
        playerCircle.toFront();
        border.toFront();

        setPaused(false);
        gameOver = false;
    }

    private void showUI() {
        pauseTrigger.setVisible(false);
        shadowExitLabel.setVisible(false);
        exitLabel.setVisible(false);
        bgGameOver.setVisible(false);
        pauseButton.setVisible(true);
        pauseButton.setPosition(VIEW_WIDTH - pauseButton.getWidth(), VIEW_HEIGHT - pauseButton.getHeight());
        restart.setPosition(-restart.getWidth()*restart.getFontScaleX()*2, VIEW_HEIGHT/2 - VIEW_HEIGHT/2*0.4f);
        quit.setPosition(VIEW_WIDTH + quit.getWidth()*quit.getScaleX()*2, VIEW_HEIGHT/2 - VIEW_HEIGHT/2*0.6f);
        menuBackTop.setPosition(-VIEW_WIDTH, 0);
        menuBackBottom.setPosition(VIEW_WIDTH, 0);

        scorePauseLabel.setText(String.valueOf(StorageManager.getBestScore()));

        scoreLabel.setVisible(true);
        scoreLabel.setText(String.valueOf(score));
        scoreLabel.setPosition(VIEW_WIDTH/2 - scoreLabel.getPrefWidth()/2, VIEW_HEIGHT * 0.7f);
        scoreLabel.addAction(Actions.alpha(1));

        updatedBestScore.setVisible(false);
        updatedBestScore.setPosition(VIEW_WIDTH/2 - updatedBestScore.getPrefWidth()/2, scoreLabel.getY() + scoreLabel.getPrefHeight());

        bestPauseLabel.setFontScale(0.4f);
        scorePauseLabel.setFontScale(0.5f);
        bestPauseLabel.setPosition(VIEW_WIDTH/2 - bestPauseLabel.getPrefWidth()/2, exitLabel.getY() - bestPauseLabel.getPrefHeight()*1.5f);
        scorePauseLabel.setPosition(VIEW_WIDTH/2 - scorePauseLabel.getPrefWidth()/2, bestPauseLabel.getY() - scorePauseLabel.getPrefHeight()*0.8f);
        bestPauseLabel.setVisible(false);
        scorePauseLabel.setVisible(false);
    }

    private void createUI() {
        bgGameOver = new BaseActor(new Texture(Gdx.files.internal("images/background.png")));
        bgGameOver.setBounds(0, 0, VIEW_WIDTH, VIEW_HEIGHT);
        bgGameOver.setOriginCenter();
        uiStage.addActor(bgGameOver);

        pauseTrigger = new BaseActor();
        pauseTrigger.setBounds(0, 0, VIEW_WIDTH, VIEW_HEIGHT);
        pauseTrigger.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                hidePauseMenuWithAnim();
            }
        });
        uiStage.addActor(pauseTrigger);

        Button pauseButtonFront = new Button(game.skin, "uiPauseButtonStyle");
        pauseButtonFront.setSize(VIEW_WIDTH * (1.0f/7), VIEW_WIDTH * (1.0f/7));
        BaseActor pauseButtonBack = new BaseActor(game.skin.get("uiPauseButtonBack", Texture.class));
        pauseButtonBack.setSize(pauseButtonFront.getWidth(), pauseButtonFront.getHeight());
        pauseButtonFront.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if ((!isPaused() || timePostPauseMenu < 4.0f) && timeBegin >= 1.0f) return true;
                return false;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                showPauseMenuWithAnim();
            }
        });
        pauseButton = new Group();
        pauseButton.addActor(pauseButtonBack);
        pauseButton.addActor(pauseButtonFront);
        pauseButton.setSize(pauseButtonFront.getWidth(), pauseButtonFront.getHeight());
        uiStage.addActor(pauseButton);

        scoreLabel = new Label(String.valueOf(score), game.skin, "uiLabelStyle");

        startingToLabel = new Group();
        Label startingLabel = new Label("STARTING IN", game.skin, "uiLabelStyle");
        startingLabel.setFontScale(0.7f);
        startingCountLabel = new Label("3", game.skin, "uiLabelStyle");
        startingCountLabel.setFontScale(0.7f);
        startingToLabel.setPosition(0, 0);
        startingToLabel.addActor(startingLabel);
        startingToLabel.addActor(startingCountLabel);
        startingLabel.setPosition(VIEW_WIDTH/2 - startingLabel.getPrefWidth()/2, startingCountLabel.getPrefHeight() + startingCountLabel.getPrefHeight()*0.2f);
        startingCountLabel.setPosition(VIEW_WIDTH/2 - startingCountLabel.getPrefWidth()/2, 0);
        startingToLabel.setPosition(0, -startingCountLabel.getPrefWidth() * 4);
        uiStage.addActor(startingToLabel);

        exitLabel = new Label("QUIT", game.skin, "uiLabelStyle");
        exitLabel.setPosition(VIEW_WIDTH/2 - exitLabel.getWidth()/2, VIEW_HEIGHT/2 - exitLabel.getHeight()/2 - VIEW_HEIGHT*0.03f);
        shadowExitLabel = new Label("QUIT", game.skin, "uiLabelStyle");
        shadowExitLabel.setColor(Color.BLACK);
        shadowExitLabel.setPosition(exitLabel.getX() + exitLabel.getHeight() * 0.03f,
                exitLabel.getY() - exitLabel.getHeight() * 0.03f);
        exitLabel.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (!isQuited) {
                    shadowExitLabel.setVisible(true);
                    return true;
                }
                return false;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                isQuited = true;

                if (((StartGame) game).isSoundsActive) ((StartGame)game).playExitSound();
                destroyCircles();
                shadowExitLabel.setVisible(false);
                exitLabel.addAction(Actions.sequence(
                        Actions.alpha(1),
                        Actions.fadeOut(0.5f),
                        Actions.visible(false)
                ));
                scoreLabel.addAction(Actions.sequence(
                        Actions.alpha(1),
                        Actions.fadeOut(0.5f),
                        Actions.visible(false)
                ));
                scorePauseLabel.addAction(Actions.sequence(
                        Actions.alpha(1),
                        Actions.fadeOut(0.5f),
                        Actions.visible(false)
                ));
                bestPauseLabel.addAction(Actions.sequence(
                        Actions.alpha(1),
                        Actions.fadeOut(0.5f),
                        Actions.visible(false)
                ));
                playerCircle.addAction(Actions.scaleTo(1.3f, 1.3f, 1));

                menuBackBottom.toFront();
                menuBackTop.toFront();
                playerCircle.toFront();
                border.toFront();
                border.addAction(Actions.scaleTo(1.3f, 1.3f, 1));
                menuBackTop.addAction(Actions.moveTo(0, 0, 1));
                menuBackBottom.addAction(Actions.sequence(
                        Actions.moveTo(0, 0, 1),
                        Actions.run(new Runnable() {
                            @Override
                            public void run() {
                                ((StartGame)game).setMenuScreen();
                            }
                        })
                ));

                scoreLabel.toFront();
                exitLabel.toFront();
                bestPauseLabel.toFront();
                scorePauseLabel.toFront();
            }
        });

        bestPauseLabel = new Label("best", game.skin, "uiLabelStyle");
        scorePauseLabel = new Label(String.valueOf(StorageManager.getBestScore()), game.skin, "uiLabelStyle");
        uiStage.addActor(bestPauseLabel);
        uiStage.addActor(scorePauseLabel);

        uiStage.addActor(shadowExitLabel);
        uiStage.addActor(exitLabel);

        restart = new Label("RESTART", game.skin, "uiLabelStyle");
        restart.setFontScaleX(0.7f);
        restart.setPosition(-restart.getWidth()*restart.getFontScaleX()*2, VIEW_HEIGHT/2 - VIEW_HEIGHT/2*0.4f);

        uiStage.addActor(restart);
        restart.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (!isRestarted) return true;
                return false;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                restart();
            }
        });

        quit = new Label("QUIT", game.skin, "uiLabelStyle");
        quit.setPosition(VIEW_WIDTH + quit.getWidth()*quit.getScaleX()*2, VIEW_HEIGHT/2 - VIEW_HEIGHT/2*0.6f);
        quit.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (!isQuited) return true;
                return false;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                quit();
            }
        });
        uiStage.addActor(quit);
        uiStage.addActor(scoreLabel);

        menuBackBottom = new Image(game.skin.getDrawable("menuBackBottom"));
        menuBackBottom.setSize(VIEW_WIDTH, VIEW_HEIGHT);
        uiStage.addActor(menuBackBottom);
        menuBackTop = new Image(game.skin.getDrawable("menuBackTop"));
        menuBackTop.setSize(VIEW_WIDTH, VIEW_HEIGHT);
        uiStage.addActor(menuBackTop);

        updatedBestScore = new Label("NEW THE BEST SCORE!", game.skin, "uiLabelStyle");
        updatedBestScore.setFontScale(0.5f);
        uiStage.addActor(updatedBestScore);
    }

    private void restart() {
        isRestarted = true;

        if (((StartGame) game).isSoundsActive) ((StartGame)game).playClickSound();
        playerCircle.toFront();
        border.toFront();
        border.addAction(Actions.scaleTo(1, 1, 1));
        playerCircle.addAction(
                Actions.sequence(
                        Actions.scaleTo(0, 0),
                        Actions.visible(true),
                        Actions.scaleTo(10, 10, 1f),
                        Actions.run(new Runnable() {
                            @Override
                            public void run() {
                                ((StartGame)game).setGameScreen();
                            }
                        })
                )
        );
    }

    private void quit() {
        isQuited = true;

        if (((StartGame) game).isSoundsActive) ((StartGame)game).playExitSound();
        menuBackBottom.toFront();
        menuBackTop.toFront();
        playerCircle.toFront();
        border.toFront();
        border.addAction(Actions.scaleTo(1.3f, 1.3f, 1));
        menuBackTop.addAction(Actions.moveTo(0, 0, 1));
        menuBackBottom.addAction(Actions.sequence(
                Actions.moveTo(0, 0, 1),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        ((StartGame)game).setMenuScreen();
                    }
                })
        ));
        playerCircle.addAction(
                Actions.sequence(
                        Actions.scaleTo(0, 0),
                        Actions.visible(true),
                        Actions.scaleTo(1.3f, 1.3f, 1f)
                )
        );
    }

    private void createBackground() {
        background = new BaseActor(game.skin.get("background", Texture.class));
        background.setBounds(0, 0, VIEW_WIDTH, VIEW_HEIGHT);
        background.setOriginCenter();
        background.setName("bg");
    }

    private void showBackground() {
        timeBack = 0;
        background.setColor(colorsBack[MathUtils.random(0, colorsBack.length-1)]);
        Color color = colorsBack[MathUtils.random(0, colorsBack.length - 1)];
        background.addAction(Actions.color(color, 60));
        mainStage.addActor(background);
    }

    private void createBorder() {
        border = new BaseActor(game.skin.get("borderTex", Texture.class));
        border.setSize(VIEW_HEIGHT * 0.15f, VIEW_HEIGHT * 0.15f);
        border.setOriginCenter();
        border.setEllipseBoundary();
        border.setPosition(VIEW_WIDTH/2 - border.getOriginX(), VIEW_HEIGHT/2 - border.getOriginY());
        uiStage.addActor(border);

        scaleSpeedBorder = 0.05f;
    }

    private void showBorder() {
        border.addAction(Actions.scaleTo(1, 1f));
    }

    private void createCircles() {
        circleList = new ArrayList<PhysicsActor>();
        circleScaleSpeedList = new ArrayList<Float>();
        timeAddCurcle = 0;
        isInstance = false;

        for (int i = 0; i < MathUtils.random(7, 14); i++) {
            PhysicsActor circle = createCircle(game.skin.get("circleTex", Texture.class));

            float x;
            if (MathUtils.random(1, 2) == 1) x = MathUtils.random(circle.getWidth(), VIEW_WIDTH / 2);
            else x = -MathUtils.random(circle.getWidth() + border.getWidth()/2, VIEW_WIDTH / 2);

            float y;
            if (MathUtils.random(1, 2) == 1) y = MathUtils.random(circle.getHeight(), VIEW_HEIGHT / 2);
            else y = -MathUtils.random(circle.getHeight() + border.getHeight()/2, VIEW_HEIGHT / 2);
            circle.setPosition(VIEW_WIDTH / 2 + x, VIEW_HEIGHT / 2 + y);

            float speed = MathUtils.random(100, 150);
            circle.setVelocityAS(MathUtils.random(0, 360), speed);

            circleScaleSpeedList.add(MathUtils.random() * 10f + 4);
            circleList.add(circle);
            circle.setName("circle");
            mainStage.addActor(circle);
        }
    }

    private PhysicsActor createCircle(Texture circTex) {
        PhysicsActor circle = new PhysicsActor();
        float size = MathUtils.random(0.05f, 0.3f);
        circle.setSize(VIEW_HEIGHT * size, VIEW_HEIGHT * size);
        circle.setOriginCenter();
        circle.setEllipseBoundary();

        BaseActor shadow = new BaseActor(circTex);
        shadow.setColor(Color.BLACK);
        shadow.setSize(circle.getWidth(), circle.getHeight());
        shadow.setOriginCenter();
        shadow.setPosition(circle.getOriginX() - shadow.getWidth()/2 + circle.getWidth()*0.01f,
                circle.getOriginY() - shadow.getHeight()/2 - circle.getHeight()*0.01f);
        circle.addActor(shadow);

        BaseActor colorCircle = new BaseActor(circTex);
        Color color = new Color(colorsCircle[MathUtils.random(0, colorsCircle.length-1)]);
        color.a = 0.9f;
        colorCircle.setColor(color);
        colorCircle.setSize(circle.getWidth(), circle.getHeight());
        colorCircle.setOriginCenter();
        colorCircle.setPosition(circle.getOriginX() - colorCircle.getWidth()/2,
                circle.getOriginY() - colorCircle.getHeight()/2);
        circle.addActor(colorCircle);

        return circle;
    }

    private void createPlayer() {
        playerCircle = new BaseActor();
        playerCircle.setSize(VIEW_HEIGHT * 0.15f, VIEW_HEIGHT * 0.15f);
        playerCircle.setOriginCenter();
        playerCircle.setEllipseBoundary();

        BaseActor shadow = new BaseActor(game.skin.get("circleTex", Texture.class));
        shadow.setColor(Color.BLACK);
        shadow.setSize(playerCircle.getWidth(), playerCircle.getHeight());
        shadow.setOriginCenter();
        shadow.setPosition(playerCircle.getWidth()*0.01f, -playerCircle.getHeight()*0.01f);
        playerCircle.addActor(shadow);

        BaseActor colorCircle = new BaseActor(game.skin.get("circleTex", Texture.class));
        colorCircle.setColor(Color.RED);
        colorCircle.setSize(playerCircle.getWidth(), playerCircle.getHeight());
        colorCircle.setOriginCenter();
        colorCircle.setPosition(0, 0);
        playerCircle.addActor(colorCircle);

        scaleSpeed = -1;
        speed = 0.45f;
        uiStage.addActor(playerCircle);
    }

    private void showPlayer() {
        playerCircle.setPosition(VIEW_WIDTH/2 - playerCircle.getOriginX(), VIEW_HEIGHT/2 - playerCircle.getOriginY());
        playerCircle.setVisible(true);
        playerCircle.addAction(Actions.sequence(
                Actions.scaleTo(10, 10),
                Actions.scaleTo(1, 1, 1f),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        scoreLabel.toFront();
                        ((StartGame)game).gameCallback.sendMessage(StartGame.HIDE_BANNER);
                    }
                })
        ));
    }

    public void showPauseMenuWithAnim() {
        ((StartGame)game).gameCallback.sendMessage(((StartGame) game).SHOW_BANNER);
        setPaused(true);
        if (((StartGame) game).isSoundsActive) ((StartGame)game).playTouchSound();
        playerCircle.toBack();
        scalePrevBorder = border.getScaleX();
        border.addAction(Actions.scaleTo(2.5f, 2.5f, 1));
        scalePrevPause = playerCircle.getScaleX();
        playerCircle.addAction(Actions.scaleTo(2.5f, 2.5f, 1));
        posPrevPause = scoreLabel.getY();
        scoreLabel.setVisible(true);
        scoreLabel.addAction(Actions.moveTo(scoreLabel.getX(), VIEW_HEIGHT/2 + VIEW_HEIGHT*0.04f, 1));
        exitLabel.addAction(Actions.sequence(
                Actions.alpha(0),
                Actions.visible(true),
                Actions.fadeIn(1)
        ));

        if (StorageManager.isExistBest()) {
            bestPauseLabel.addAction(Actions.sequence(
                    Actions.alpha(0),
                    Actions.visible(true),
                    Actions.fadeIn(1)
            ));
            scorePauseLabel.setX(VIEW_WIDTH/2 - scorePauseLabel.getPrefWidth()/2);
            scorePauseLabel.addAction(Actions.sequence(
                    Actions.alpha(0),
                    Actions.visible(true),
                    Actions.fadeIn(1)
            ));
        }
        pauseTrigger.addAction(Actions.sequence(Actions.delay(1), Actions.visible(true)));
        pauseButton.addAction(Actions.moveBy(pauseButton.getWidth()*0.7f, pauseButton.getHeight()*0.7f, 1));

        if (timePostPauseMenu <= 4.3) {
            timePostPauseMenu = 6;
            startingToLabel.addAction(Actions.moveTo(0, -startingCountLabel.getPrefHeight() * 3, 0.3f));
        }
        exitLabel.toFront();
    }

    public void showPauseMenu() {
        ((StartGame)game).gameCallback.sendMessage(((StartGame) game).SHOW_BANNER);
        setPaused(true);
        playerCircle.toBack();
        scalePrevBorder = border.getScaleX();
        border.setScale(scalePrevBorder + (2.5f - scalePrevBorder), scalePrevBorder + (2.5f - scalePrevBorder));
        scalePrevPause = playerCircle.getScaleX();
        playerCircle.setScale(scalePrevPause + (2.5f - scalePrevPause), scalePrevPause + (2.5f - scalePrevPause));
        posPrevPause = scoreLabel.getY();
        scoreLabel.setPosition(scoreLabel.getX(), scoreLabel.getY() + ((VIEW_HEIGHT/2 + VIEW_HEIGHT*0.03f) - scoreLabel.getY()));
        scoreLabel.setVisible(true);
        exitLabel.setVisible(true);
        if (StorageManager.isExistBest()) {
            scorePauseLabel.setX(VIEW_WIDTH/2 - scorePauseLabel.getPrefWidth()/2);
            scorePauseLabel.setVisible(true);
            bestPauseLabel.setVisible(true);
        }
        uiStage.addActor(shadowExitLabel);
        uiStage.addActor(exitLabel);
        pauseTrigger.setVisible(true);
        pauseButton.addAction(Actions.moveBy(pauseButton.getWidth()*0.7f, pauseButton.getHeight()*0.7f));

        if (timePostPauseMenu <= 4.3) {
            timePostPauseMenu = 6;
            startingToLabel.setPosition(0, -startingCountLabel.getPrefHeight() * 3);
        }
        exitLabel.setVisible(true);
        exitLabel.toFront();
    }

    public void hidePauseMenuWithAnim() {
        if (((StartGame) game).isSoundsActive) ((StartGame)game).playTouchSound();
        timeBegin = 0;
        border.addAction(Actions.scaleTo(1f, 1f, 1));
        playerCircle.addAction(Actions.scaleTo(scalePrevPause, scalePrevPause, 1));
        scoreLabel.addAction(Actions.moveTo(scoreLabel.getX(), posPrevPause, 1));
        border.addAction(Actions.scaleTo(scalePrevBorder, scalePrevBorder, 1));
        shadowExitLabel.setVisible(false);
        pauseButton.addAction(Actions.moveBy(-pauseButton.getWidth()*0.7f, -pauseButton.getHeight()*0.7f, 1));
        pauseTrigger.setVisible(false);
        exitLabel.addAction(Actions.sequence(
                Actions.alpha(1),
                Actions.fadeOut(1),
                Actions.visible(false)
        ));

        if (StorageManager.isExistBest()) {
            scorePauseLabel.addAction(Actions.sequence(
                    Actions.alpha(1),
                    Actions.fadeOut(1),
                    Actions.visible(false)
            ));
            bestPauseLabel.addAction(Actions.sequence(
                    Actions.alpha(1),
                    Actions.fadeOut(1),
                    Actions.visible(false)
            ));
        }

        postHidePauseMenu = true;
        timePostPauseMenu = 0;
        startingCountLabel.setText("3");
        startingCountLabel.setPosition(VIEW_WIDTH/2 - startingCountLabel.getPrefWidth()/2, startingCountLabel.getY());
        startingToLabel.addAction(Actions.sequence(
                Actions.delay(0.7f),
                Actions.moveTo(0, VIEW_HEIGHT * 0.2f, 0.3f)
        ));

    }

    public void hidePauseMenu() {
        ((StartGame)game).gameCallback.sendMessage(((StartGame) game).HIDE_BANNER);
        border.scaleBy(1f, 1f);
        playerCircle.scaleBy(scalePrevPause, scalePrevPause);
        scoreLabel.setPosition(scoreLabel.getX(), posPrevPause, 1);
        border.scaleBy(scalePrevBorder, scalePrevPause);
        shadowExitLabel.setVisible(false);
        exitLabel.setVisible(false);
        pauseTrigger.setVisible(false);
        setPaused(false);
    }

    @Override
    public void update(float delta) {
        timeBegin += delta;

        if (!gameOver) {
            updateBackColor(delta);

            if (timeBegin > 1f)
                updateScore(delta);

            updateScalePlayer(delta);
            updateCircles(delta);
        }
    }

    @Override
    public void updateInPause(float delta) {
        updatePostHidePause(delta);
    }

    private void updatePostHidePause(float delta) {
        timePostPauseMenu += delta;

        if (timePostPauseMenu <= 4.3f) {
            if (timePostPauseMenu >= 2.0f && timePostPauseMenu <= 2.3f) {
                startingCountLabel.setText("2");
                startingCountLabel.setPosition(VIEW_WIDTH/2 - startingCountLabel.getPrefWidth()/2, startingCountLabel.getY());
            } else if (timePostPauseMenu >= 3.0f && timePostPauseMenu <= 3.3f) {
                startingCountLabel.setText("1");
                startingCountLabel.setPosition(VIEW_WIDTH/2 - startingCountLabel.getPrefWidth()/2, startingCountLabel.getY());
            } else if (timePostPauseMenu >= 4.0f && timePostPauseMenu <= 4.3f) {
                startingCountLabel.setText("0");
                startingCountLabel.setPosition(VIEW_WIDTH/2 - startingCountLabel.getPrefWidth()/2, startingCountLabel.getY());
                timePostPauseMenu -= delta;
                startingToLabel.addAction(Actions.sequence(
                        Actions.moveTo(0, -startingCountLabel.getPrefHeight() * 3, 0.3f),
                        Actions.run(new Runnable() {
                            @Override
                            public void run() {
                                ((StartGame)game).gameCallback.sendMessage(((StartGame) game).HIDE_BANNER);
                            }
                        })));
                setPaused(false);
            }


        }
    }

    private void updateScore(float delta) {
        timeScore += delta;
        timeScoreVisible += delta;

        if (playerCircle.getScaleX() > border.getScaleX()) {
            scoreLabel.setVisible(true);
            if (playerCircle.getScaleX()*playerCircle.getScaleX() * 5  >= 1 / timeScore) {
                timeScore = 0;
                score++;
                scoreLabel.setText(Integer.toString(score));
                scoreLabel.setX(VIEW_WIDTH/2 - scoreLabel.getPrefWidth()/2);
            }
        } else {
            if (3/playerCircle.getScaleX() >= 0.5f/timeScore) {
                timeScore = 0;
                score--;
                scoreLabel.setText(Integer.toString(score));
                scoreLabel.setX(VIEW_WIDTH/2 - scoreLabel.getPrefWidth()/2);
            }

            if (timeScoreVisible >= playerCircle.getScaleX()/3) {
                timeScoreVisible = 0;
                scoreLabel.setVisible(!scoreLabel.isVisible());
            }
        }
    }

    private void updateBackColor(float delta) {
        timeBack += delta;
        if (timeBack > 60f) {
            timeBack = 0;
            Color color = colorsBack[MathUtils.random(0, colorsBack.length - 1)];
            background.addAction(Actions.color(color, 60f));
        }
    }

    private void updateScalePlayer(float delta) {
        if (scaleSpeed < 0) scaleSpeed = -playerCircle.getScaleX()*speed;
        else scaleSpeed = playerCircle.getScaleX()*speed;
        playerCircle.scaleBy(scaleSpeed * delta);

        if (playerCircle.getScaleX() < 0.1f || score <= 0) {
            gameOver = true;
            if (((StartGame) game).isSoundsActive) {
                ((StartGame) game).playEndSound();
                ((StartGame) game).setFadeBaseMelody();
            }
            for (PhysicsActor circle : circleList)
                circle.remove();
            scoreLabel.setVisible(true);
            playerCircle.setVisible(false);
            bgGameOver.setColor(Color.WHITE);
            bgGameOver.setVisible(true);
            pauseButton.setVisible(false);
            bgGameOver.addAction(Actions.sequence(
                    Actions.delay(0.1f),
                    Actions.color(Color.BLACK),
                    Actions.delay(0.07f),
                    Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            ((StartGame)game).gameCallback.sendMessage(StartGame.SHOW_INTERSTITIAL);
                        }
                    })
            ));
            ((StartGame)game).gameCallback.sendMessage(StartGame.SHOW_BANNER);
            restart.addAction(Actions.sequence(
                    Actions.delay(1f),
                    Actions.moveTo(VIEW_WIDTH/2 - restart.getWidth()/2*restart.getFontScaleX(), VIEW_HEIGHT/2 - VIEW_HEIGHT/2*0.4f, 0.5f)
            ));
            quit.addAction(Actions.sequence(
                    Actions.delay(1f),
                    Actions.moveTo(VIEW_WIDTH/2 - quit.getWidth()/2, VIEW_HEIGHT/2 - VIEW_HEIGHT/2*0.6f, 0.5f)
            ));
        }
    }

    private void updateCircles(float delta) {
        timeAddCurcle += delta;

        if ((int)(timeAddCurcle) % 60 * 2 > 0) isInstance = false;

        if (!isInstance && timeAddCurcle >= 5 && (int)(timeAddCurcle) % 60 * 2 == 0) {
            isInstance = true;
            PhysicsActor circle = createCircle(game.skin.get("circleTex", Texture.class));

            float x, y;
            if (MathUtils.random(1, 2) == 2) {
                if (MathUtils.random(1, 2) == 2) x = -circle.getWidth();
                else x = VIEW_WIDTH;

                y = MathUtils.random(-circle.getHeight(), VIEW_HEIGHT);
            } else {
                if (MathUtils.random(1, 2) == 2) y = -circle.getHeight();
                else y = VIEW_HEIGHT;

                x = MathUtils.random(-circle.getWidth(), VIEW_WIDTH);
            }
            circle.setPosition(x, y);

            float speed = MathUtils.random(100, 150);
            circle.setVelocityAS(MathUtils.random(0, 360), speed);

            circleScaleSpeedList.add(MathUtils.random() * 6.0f);
            circleList.add(circle);
            mainStage.addActor(circle);
        }

        PhysicsActor circle;
        for (int i = 0; i < circleList.size(); i++) {
            circle = circleList.get(i);
            circle.setSpeed(circle.getSpeed() + delta*delta * circleScaleSpeedList.get(i));

            if (circle.getX() < -circle.getWidth()) circle.setVelocityXY(-circle.getVelocity().x, circle.getVelocity().y);
            if (circle.getX() > VIEW_WIDTH) circle.setVelocityXY(-circle.getVelocity().x, circle.getVelocity().y);
            if (circle.getY() < -circle.getHeight()) circle.setVelocityXY(circle.getVelocity().x, -circle.getVelocity().y);
            if (circle.getY() > VIEW_HEIGHT) circle.setVelocityXY(circle.getVelocity().x, -circle.getVelocity().y);

            if (circle.overlaps(border, true)) {
                if (((StartGame) game).isSoundsActive) ((StartGame)game).playHitSound();
                Vector2 n = new Vector2((border.getX() + border.getOriginX()) - (circle.getX() + circle.getOriginX()),
                        (border.getY() + border.getOriginY()) - (circle.getY() + circle.getOriginY()));
                n.nor();
                float plexus = circle.getVelocity().dot(n) / n.len();
                n.scl(-plexus*2);
                Vector2 res = n.add(circle.getVelocity());
                circle.setVelocityXY(res.x, res.y);
            }

            if (timeBegin > 1.1f && circle.overlaps(playerCircle, true))
                setGameOver();
        }

    }

    private void setGameOver() {
        if (((StartGame) game).isSoundsActive) {
            ((StartGame)game).playExplosionSound();
            ((StartGame)game).setFadeBaseMelody();
        }

        gameOver = true;
        destroyCircles();
        playerCircle.addAction(Actions.sequence(
                Actions.delay(1f),
                Actions.visible(false)
        ));

        playerCircle.setZIndex(bgGameOver.getZIndex() - 1);
        bgGameOver.setColor(Color.WHITE);
        bgGameOver.addAction(
                Actions.sequence(
                        Actions.delay(0.1f),
                        Actions.visible(true),
                        Actions.delay(0.07f),
                        Actions.visible(false),
                        Actions.delay(0.12f),
                        Actions.visible(true),
                        Actions.delay(0.03f),
                        Actions.visible(false),
                        Actions.delay(0.05f),
                        Actions.visible(true),
                        Actions.delay(0.15f),
                        Actions.visible(false),
                        Actions.delay(0.08f),
                        Actions.visible(true),
                        Actions.delay(0.07f),
                        Actions.visible(false),
                        Actions.visible(true),
                        Actions.run(new Runnable() {
                            @Override
                            public void run() {
                                pauseButton.setVisible(false);
                                if (((StartGame) game).isSoundsActive) ((StartGame)game).stopExplosionSound();
                                ((StartGame)game).gameCallback.sendMessage(StartGame.SHOW_INTERSTITIAL);
                                ((StartGame)game).gameCallback.sendMessage(StartGame.SHOW_BANNER);
                            }
                        }),
                        Actions.color(Color.BLACK)
                )
        );

        restart.addAction(Actions.sequence(
                Actions.delay(2f),
                Actions.moveTo(VIEW_WIDTH/2 - restart.getWidth()/2*restart.getFontScaleX(), VIEW_HEIGHT/2 - VIEW_HEIGHT/2*0.4f, 0.5f)
        ));
        quit.addAction(Actions.sequence(
                Actions.delay(2f),
                Actions.moveTo(VIEW_WIDTH/2 - quit.getWidth()/2, VIEW_HEIGHT/2 - VIEW_HEIGHT/2*0.6f, 0.5f)
        ));

        updateBestScore();
    }

    private void destroyCircles() {
        for (PhysicsActor circle : circleList) {
            circle.setSpeed(0);
            circle.addAction(
                    Actions.sequence(
                            Actions.delay(1),
                            Actions.removeActor()
                    )
            );
        }
    }

    private void saveScore() {
        StorageManager.setScore(score);
    }

    private void updateBestScore() {
        if (!StorageManager.isExistBest() || (StorageManager.isExistBest() && StorageManager.getBestScore() < score)) {
            updatedBestScore.addAction(Actions.sequence(
                    Actions.delay(2f),
                    Actions.repeat(3,
                            Actions.sequence(Actions.visible(true),
                            Actions.delay(0.3f),
                            Actions.visible(false),
                            Actions.delay(0.3f))),
                    Actions.visible(true)
            ));
        } else if (StorageManager.isExistBest()) {
            scorePauseLabel.setY(scoreLabel.getY() + scoreLabel.getPrefHeight());
            bestPauseLabel.setY(scorePauseLabel.getY() + scorePauseLabel.getPrefHeight());
            scorePauseLabel.addAction(Actions.sequence(
                    Actions.delay(2f),
                    Actions.alpha(0),
                    Actions.visible(true),
                    Actions.fadeIn(0.5f)
            ));
            bestPauseLabel.addAction(Actions.sequence(
                    Actions.delay(2f),
                    Actions.alpha(0),
                    Actions.visible(true),
                    Actions.fadeIn(0.5f)
            ));
        }

        saveScore();
    }

    public boolean isPostPause() {
        return timePostPauseMenu <= 4.3;
    }

    @Override
    public void pause() {
        super.pause();
        if (!gameOver && !isPaused() && timeBegin > 1f) {
            showPauseMenu();
        }
    }

    @Override
    public void resume() {
        super.resume();
    }

    @Override
    public void hide() {
        super.hide();
    }
}