package com.dandream.forround;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;

public class AnimatedActor extends BaseActor {

    private float elapsedTime;
    private Animation<TextureRegion> activeAnimation;
    private String activeName;
    protected HashMap<String, Animation<TextureRegion>> animationStorage;
    private boolean pauseAnim;

    public AnimatedActor() {
        super();
        elapsedTime = 0;
        activeAnimation = null;
        activeName = null;
        animationStorage = new HashMap<String, Animation<TextureRegion>>();
        pauseAnim = false;
    }

    public void storeAnimation(String name, Animation animation) {
        animationStorage.put(name, animation);
        if (activeName == null) setActiveAnimation(name);
    }

    public void storeAnimation(String name, Texture texture) {
        TextureRegion region = new TextureRegion(texture);
        TextureRegion[] frames = {region};
        Animation<TextureRegion> animation = new Animation<TextureRegion>(1.0f, frames);
        storeAnimation(name, animation);
    }

    public void setActiveAnimation(String name) {
        if (!animationStorage.containsKey(name)) {
            System.out.println("No animation: " + name);
            return;
        }

        if (name.equals(activeName)) return;

        activeName = name;
        activeAnimation = animationStorage.get(name);
        elapsedTime = 0;

        if (getWidth() == 0 || getHeight() == 0) {
            Texture texture = activeAnimation.getKeyFrame(0).getTexture();
            setWidth(texture.getWidth());
            setHeight(texture.getHeight());
        }
    }

    public String getAnimationName() {
        return activeName;
    }

    public void act(float delta) {
        super.act(delta);
        if (!pauseAnim) elapsedTime += delta;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (activeAnimation != null) region.setRegion(activeAnimation.getKeyFrame(elapsedTime));
        super.draw(batch, parentAlpha);
    }

    public void copy(AnimatedActor original) {
        super.copy(original);
        this.elapsedTime = 0;
        this.animationStorage = original.animationStorage;
        this.activeName = original.activeName;
        this.activeAnimation = original.activeAnimation;
    }

    public AnimatedActor clone() {
        AnimatedActor newbie = new AnimatedActor();
        newbie.copy(this);
        return newbie;
    }

    public void pauseAnimation() {
        pauseAnim = true;
    }

    public void startAnimation() {
        pauseAnim = false;
    }

    public void setAnimationFrame(int n) {
        elapsedTime = n * activeAnimation.getFrameDuration();
    }
}