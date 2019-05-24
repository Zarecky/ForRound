package com.dandream.forround;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.dandream.forround.utils.GameCallback;

public class StartGame extends BaseGame {

	public static final float DELTA_SHADOW = VIEWPORT_WIDTH * 0.01f;

	public static final int SHOW_BANNER = 1;
	public static final int HIDE_BANNER = 2;
	public static final int LOAD_INTERSTITIAL = 3;
	public static final int SHOW_INTERSTITIAL = 4;
	public static final int OPEN_MARKET = 5;
	public static final int SHARE = 6;

	private GameScreen gameScreen;
    private MenuScreen menuScreen;

	private boolean loadingAssets = false;
	private boolean settingScreen = true;
	private boolean postLoading = false;
	private AssetManager assetManager;

    private Music baseMelody;
	private Sound clickSound;
	private Sound exitSound;
	private Sound hitSound;
	private Sound explSound;
	private Sound touchSound;
	private Sound endSound;
    public boolean isSoundsActive;

	private Texture texLogo;
	private SpriteBatch batch;
	private float width, height;
	private float time = 0;

    private boolean isFade;

	public GameCallback gameCallback;

	public StartGame(GameCallback gameCallback) {
		this.gameCallback = gameCallback;
	}

	@Override
	public void create() {
		VIEWPORT_HEIGHT = Gdx.graphics.getHeight();
		VIEWPORT_WIDTH = Gdx.graphics.getWidth();

		texLogo = new Texture(Gdx.files.internal("images/logo.png"));
		texLogo.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		batch = new SpriteBatch();
		width = VIEWPORT_WIDTH*1.2f;
		height = width * (texLogo.getWidth()/texLogo.getHeight());

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(texLogo, VIEWPORT_WIDTH/2 - width/2, VIEWPORT_HEIGHT/2 - height/2, width, height);
		batch.end();

		loadingAssets = true;
		assetManager = new AssetManager();

        isSoundsActive = true;

        loadFiles();

		StorageManager.load();
	}

	private void loadFiles() {
		assetManager.load("images/menu-back.png", Texture.class);
		assetManager.load("images/title.png", Texture.class);
		assetManager.load("images/circle.png", Texture.class);
		assetManager.load("images/border.png", Texture.class);
		assetManager.load("images/background.png", Texture.class);
		assetManager.load("images/menu-back-top.png", Texture.class);
		assetManager.load("images/menu-back-bottom.png", Texture.class);
		assetManager.load("images/music-button-up.png", Texture.class);
		assetManager.load("images/music-button-down.png", Texture.class);
		assetManager.load("images/music-button-checked.png", Texture.class);
		assetManager.load("images/help-button-up.png", Texture.class);
		assetManager.load("images/help-button-down.png", Texture.class);
		assetManager.load("images/share-button-up.png", Texture.class);
		assetManager.load("images/share-button-down.png", Texture.class);
		assetManager.load("images/pause-button-up.png", Texture.class);
		assetManager.load("images/pause-button-down.png", Texture.class);
		assetManager.load("images/pause-button-back.png", Texture.class);
		assetManager.load("images/play-button-up.png", Texture.class);
		assetManager.load("images/play-button-down.png", Texture.class);

		assetManager.load("sounds/click.wav", Sound.class);
		assetManager.load("sounds/exit.wav", Sound.class);
		assetManager.load("sounds/hit.wav", Sound.class);
		assetManager.load("sounds/expl.mp3", Sound.class);
		assetManager.load("sounds/touch.mp3", Sound.class);
		assetManager.load("sounds/end.mp3", Sound.class);
		assetManager.load("sounds/base-melody.mp3", Music.class);

		FileHandleResolver resolver = new InternalFileHandleResolver();
		assetManager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
		assetManager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));
		FreetypeFontLoader.FreeTypeFontLoaderParameter sizeParams = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
		sizeParams.fontFileName = "font.ttf";
		sizeParams.fontParameters.size = (int)(70.0f/480*VIEWPORT_WIDTH);
		assetManager.load("font.ttf", BitmapFont.class, sizeParams);
	}

    private void loadSongs() {
        baseMelody = assetManager.get("sounds/base-melody.mp3", Music.class);
        baseMelody.setVolume(0.25f);
        baseMelody.setLooping(true);

        isFade = false;

		clickSound = assetManager.get("sounds/click.wav", Sound.class);
		exitSound = assetManager.get("sounds/exit.wav", Sound.class);
		hitSound = assetManager.get("sounds/hit.wav", Sound.class);
		explSound = assetManager.get("sounds/expl.mp3", Sound.class);
		touchSound = assetManager.get("sounds/touch.mp3", Sound.class);
		endSound = assetManager.get("sounds/end.mp3", Sound.class);
    }

    private void loadTextures() {
		skin.add("menuBackTex", assetManager.get("images/menu-back.png", Texture.class));

		Texture titleTex = assetManager.get("images/title.png", Texture.class);
		titleTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		skin.add("titleTex", titleTex);

        skin.add("circleTex", assetManager.get("images/circle.png", Texture.class));
        Texture border = assetManager.get("images/border.png", Texture.class);
        border.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        skin.add("borderTex", border);
        skin.add("background", assetManager.get("images/background.png", Texture.class));

        Texture menuBackTop = assetManager.get("images/menu-back-top.png", Texture.class);
        menuBackTop.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        skin.add("menuBackTop", menuBackTop);
        Texture menuBackBottom = assetManager.get("images/menu-back-bottom.png", Texture.class);
        menuBackBottom.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        skin.add("menuBackBottom", menuBackBottom);
	}

	@Override
	public void render() {
		super.render();

		time += Gdx.graphics.getDeltaTime();

		if (loadingAssets) {
			if (assetManager.update()) {
				loadingAssets = false;
				onAssetsLoaded();
				gameScreen = new GameScreen(this);
				menuScreen = new MenuScreen(this);
				postLoading = true;
			}
		}

		if (postLoading && settingScreen && time >= 5) {
			settingScreen = false;
			setMenuScreen();
		}

		if (postLoading && !settingScreen) {
			if (isFade && baseMelody.isPlaying()) {
				baseMelody.setVolume(baseMelody.getVolume() - Gdx.graphics.getDeltaTime() * 0.5f);
				if (baseMelody.getVolume() < 0.001f) {
					baseMelody.stop();
					isFade = false;
				}
			}
		}
	}

	private void onAssetsLoaded() {
		loadSongs();
		loadTextures();

		skin.add("uiFont", assetManager.get("font.ttf", BitmapFont.class));
		generateLabel();
		generatePauseButton();
		generatePlayButton();
		generateMusicButton();
		generateHelpButton();
		generateShareButton();
	}

	private void generateLabel() {
		Label.LabelStyle labelStyle = new Label.LabelStyle(skin.getFont("uiFont"), Color.WHITE);
		skin.add("uiLabelStyle", labelStyle);
	}
    private void generateMusicButton() {
        skin.add("uiMusicButtonUp", assetManager.get("images/music-button-up.png", Texture.class));
        skin.add("uiMusicButtonDown", assetManager.get("images/music-button-down.png", Texture.class));
        skin.add("uiMusicButtonChecked", assetManager.get("images/music-button-checked.png", Texture.class));

        Button.ButtonStyle musicButtonStyle = new Button.ButtonStyle();
        musicButtonStyle.down = skin.getDrawable("uiMusicButtonDown");
        musicButtonStyle.up = skin.getDrawable("uiMusicButtonUp");
        musicButtonStyle.checked = skin.getDrawable("uiMusicButtonChecked");

        skin.add("uiMusicButtonStyle", musicButtonStyle);
    }
    private void generateHelpButton() {
        skin.add("uiHelpButtonUp", assetManager.get("images/help-button-up.png", Texture.class));
        skin.add("uiHelpButtonDown", assetManager.get("images/help-button-down.png", Texture.class));

        Button.ButtonStyle helpButtonStyle = new Button.ButtonStyle();
        helpButtonStyle.down = skin.getDrawable("uiHelpButtonDown");
        helpButtonStyle.up = skin.getDrawable("uiHelpButtonUp");

        skin.add("uiHelpButtonStyle", helpButtonStyle);
    }
    private void generateShareButton() {
        skin.add("uiShareButtonUp", assetManager.get("images/share-button-up.png", Texture.class));
        skin.add("uiShareButtonDown", assetManager.get("images/share-button-down.png", Texture.class));

        Button.ButtonStyle shareButtonStyle = new Button.ButtonStyle();
        shareButtonStyle.down = skin.getDrawable("uiShareButtonDown");
        shareButtonStyle.up = skin.getDrawable("uiShareButtonUp");

        skin.add("uiShareButtonStyle", shareButtonStyle);
    }
	private void generatePauseButton() {
		skin.add("uiPauseButtonUp", assetManager.get("images/pause-button-up.png", Texture.class));
		skin.add("uiPauseButtonDown", assetManager.get("images/pause-button-down.png", Texture.class));
		skin.add("uiPauseButtonBack", assetManager.get("images/pause-button-back.png", Texture.class));

		Button.ButtonStyle pauseButtonStyle = new Button.ButtonStyle();
		pauseButtonStyle.down = skin.getDrawable("uiPauseButtonDown");
		pauseButtonStyle.up = skin.getDrawable("uiPauseButtonUp");

		skin.add("uiPauseButtonStyle", pauseButtonStyle);
	}
	private void generatePlayButton() {
		skin.add("uiPlayButtonUp", assetManager.get("images/play-button-up.png", Texture.class));
		skin.add("uiPlayButtonDown", assetManager.get("images/play-button-down.png", Texture.class));

		Button.ButtonStyle playButtonStyle = new Button.ButtonStyle();
		playButtonStyle.down = skin.getDrawable("uiPlayButtonDown");
		playButtonStyle.up = skin.getDrawable("uiPlayButtonUp");

		skin.add("uiPlayButtonStyle", playButtonStyle);
	}

	public void setGameScreen() {
        setScreen(gameScreen);
    }
    public void setMenuScreen() {
		setScreen(menuScreen);
	}

	public void playClickSound() {
		clickSound.play();
	}
    public void playExitSound() {
        exitSound.play();
    }
	public void playExplosionSound() {
		explSound.play();
	}
	public void stopExplosionSound() {
		explSound.stop();
	}
	public void playHitSound() {
		hitSound.play();
	}
    public void playTouchSound() {
        touchSound.play();
    }
    public void playEndSound() {
        endSound.play();
    }
    public void playBaseMelody() {
		baseMelody.setVolume(0.25f);
		baseMelody.play();
	}
	public boolean isPlayingBaseMelody() {
		return baseMelody.isPlaying();
	}
	public void stopBaseMelody() {
		baseMelody.stop();
	}

	public void setFadeBaseMelody() {
        isFade = true;
    }

	@Override
	public void dispose() {
		StorageManager.flush();
		assetManager.dispose();
		super.dispose();
	}
}
