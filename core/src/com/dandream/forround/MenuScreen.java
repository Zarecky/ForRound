package com.dandream.forround;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class MenuScreen extends BaseScreen {

    private BaseActor border;
    private BaseActor playerCircle;
    private Image background;
    private Image title;
    private Button playButton;
    private Button musicButton;
    private Button helpButton;
    private Button shareButton;
    private Label bestScoreLabel;

    private boolean isPlayed;

    public MenuScreen(BaseGame game) {
        super(game);
    }

    @Override
    public void create() {
    }

    private void createBackground() {
        background = new Image(game.skin, "menuBackTex");
        background.setBounds(0, 0, VIEW_WIDTH, VIEW_HEIGHT);
        uiStage.addActor(background);
    }

    private void createTitle() {
        title = new Image(game.skin, "titleTex");
        float k = title.getHeight() / title.getWidth();
        title.setSize(VIEW_HEIGHT * 0.5f, VIEW_HEIGHT *0.5f * k);
        title.setPosition(VIEW_WIDTH/2 - title.getWidth()/2, VIEW_HEIGHT/2);
        title.setVisible(false);
        uiStage.addActor(title);

        title.addAction(Actions.sequence(
                Actions.delay(0.3f),
                Actions.alpha(0),
                Actions.visible(true),
                Actions.fadeIn(1)
        ));
    }

    private void createPlayer() {
        playerCircle = new BaseActor();
        playerCircle.setSize(VIEW_HEIGHT * 0.15f, VIEW_HEIGHT * 0.15f);
        playerCircle.setOriginCenter();
        playerCircle.scaleBy(0.3f);
        playerCircle.setEllipseBoundary();
        playerCircle.setPosition(VIEW_WIDTH/2 - playerCircle.getOriginX(), VIEW_HEIGHT/2 - playerCircle.getOriginY());

        BaseActor shadow = new BaseActor(game.skin.get("circleTex", Texture.class));
        shadow.setColor(Color.BLACK);
        shadow.setSize(playerCircle.getWidth(), playerCircle.getHeight());
        shadow.setOriginCenter();
        shadow.setPosition(playerCircle.getOriginX() - shadow.getWidth()/2 + playerCircle.getWidth()*0.01f,
                playerCircle.getOriginY() - shadow.getHeight()/2 - playerCircle.getHeight()*0.01f);
        playerCircle.addActor(shadow);

        BaseActor colorCircle = new BaseActor(game.skin.get("circleTex", Texture.class));
        colorCircle.setColor(Color.RED);
        colorCircle.setSize(playerCircle.getWidth(), playerCircle.getHeight());
        colorCircle.setOriginCenter();
        colorCircle.setPosition(playerCircle.getOriginX() - colorCircle.getWidth()/2,
                playerCircle.getOriginY() - colorCircle.getHeight()/2);
        playerCircle.addActor(colorCircle);

        uiStage.addActor(playerCircle);
    }

    private void createBorder() {
        Texture borderTex = game.skin.get("borderTex", Texture.class);
        border = new BaseActor(borderTex);
        border.setSize(VIEW_HEIGHT * 0.15f, VIEW_HEIGHT * 0.15f);
        border.setOriginCenter();
        border.scaleBy(0.3f);
        border.setPosition(VIEW_WIDTH/2 - border.getOriginX(), VIEW_HEIGHT/2 - border.getOriginY());
        uiStage.addActor(border);
    }

    private void createPlayButton() {
        playButton = new Button(game.skin, "uiPlayButtonStyle");
        playButton.setSize(playerCircle.getWidth(), playerCircle.getHeight());
        playButton.setPosition(VIEW_WIDTH/2 - playButton.getWidth()/2, VIEW_HEIGHT/2 - playButton.getHeight()/2);
        playButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (!isPlayed) return true;
                return false;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                isPlayed = true;

                if (((StartGame) game).isSoundsActive) ((StartGame)game).playClickSound();
                playButton.addAction(Actions.sequence(
                        Actions.fadeOut(1)
                ));
                border.addAction(Actions.scaleTo(1, 1, 1));
                playerCircle.addAction(Actions.sequence(
                        Actions.scaleTo(10, 10, 1),
                        Actions.run(new Runnable() {
                            @Override
                            public void run() {
                                ((StartGame)game).gameCallback.sendMessage(StartGame.SHOW_INTERSTITIAL);
                                ((StartGame)game).setGameScreen();
                            }
                        })));

                bestScoreLabel.addAction(Actions.sequence(
                        Actions.moveTo(VIEW_WIDTH/2 - bestScoreLabel.getPrefWidth()/2, VIEW_HEIGHT, 0.3f)
                ));
            }
        });
        uiStage.addActor(playButton);
        playButton.setVisible(false);

        playButton.addAction(Actions.sequence(
                Actions.alpha(0),
                Actions.visible(true),
                Actions.fadeIn(1)
        ));
    }

    private void createMusicButton() {
        musicButton = new Button(game.skin, "uiMusicButtonStyle");
        musicButton.setSize(VIEW_WIDTH * 0.15f, VIEW_WIDTH * 0.15f);
        musicButton.setPosition(VIEW_WIDTH * 0.02f, musicButton.getHeight()*1.5f);
        musicButton.setChecked(!((StartGame)game).isSoundsActive);
        musicButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                ((StartGame)game).playTouchSound();
                ((StartGame) game).isSoundsActive = !musicButton.isChecked();
                if (!musicButton.isChecked()) ((StartGame) game).playBaseMelody();
                else ((StartGame) game).stopBaseMelody();

            }
        });

        uiStage.addActor(musicButton);

        musicButton.setVisible(false);
        musicButton.addAction(Actions.sequence(
                Actions.alpha(0),
                Actions.visible(true),
                Actions.fadeIn(1)
        ));
    }

    private void createHelpButton() {
        helpButton = new Button(game.skin, "uiHelpButtonStyle");
        helpButton.setSize(VIEW_WIDTH * 0.15f, VIEW_WIDTH * 0.15f);
        helpButton.setPosition(VIEW_WIDTH * 0.02f + musicButton.getWidth(), helpButton.getHeight()*2f);
        helpButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (((StartGame) game).isSoundsActive) ((StartGame)game).playTouchSound();
            }
        });

        uiStage.addActor(helpButton);

        helpButton.setVisible(false);
        helpButton.addAction(Actions.sequence(
                Actions.alpha(0),
                Actions.visible(true),
                Actions.fadeIn(1)
        ));
    }

    private void createShareButton() {
        shareButton = new Button(game.skin, "uiShareButtonStyle");
        shareButton.setSize(VIEW_WIDTH * 0.15f, VIEW_WIDTH * 0.15f);
        shareButton.setPosition(VIEW_WIDTH * 0.02f + 2 * musicButton.getWidth(), helpButton.getHeight()*2f + (helpButton.getY() - musicButton.getY()));
        shareButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (((StartGame) game).isSoundsActive) ((StartGame)game).playTouchSound();
            }
        });

        uiStage.addActor(shareButton);

        shareButton.setVisible(false);
        shareButton.addAction(Actions.sequence(
                Actions.alpha(0),
                Actions.visible(true),
                Actions.fadeIn(1)
        ));
    }

    private void createBestScoreLabel() {
        bestScoreLabel = new Label("SCORE " + String.valueOf(StorageManager.getBestScore()), game.skin, "uiLabelStyle");
        bestScoreLabel.setFontScale(0.7f);
        bestScoreLabel.setPosition(VIEW_WIDTH/2 - bestScoreLabel.getPrefWidth()/2, VIEW_HEIGHT);
        uiStage.addActor(bestScoreLabel);

        if (StorageManager.isExistBest())
            bestScoreLabel.addAction(Actions.sequence(
                    Actions.delay(2f),
                    Actions.moveTo(VIEW_WIDTH/2 - bestScoreLabel.getPrefWidth()/2, VIEW_HEIGHT - bestScoreLabel.getPrefHeight() - VIEW_WIDTH*0.03f, 0.3f)
            ));
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public void show() {
        super.show();

        ((StartGame)game).gameCallback.sendMessage(StartGame.LOAD_INTERSTITIAL);
        ((StartGame)game).gameCallback.sendMessage(StartGame.SHOW_BANNER);

        isPlayed = false;

        createBackground();
        createBestScoreLabel();
        createTitle();
        createMusicButton();
        createHelpButton();
        createShareButton();
        createPlayer();
        createBorder();
        createPlayButton();

        if (((StartGame) game).isSoundsActive) ((StartGame)game).playBaseMelody();
    }




}
