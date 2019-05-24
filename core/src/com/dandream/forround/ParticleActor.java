package com.dandream.forround;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class ParticleActor extends Actor {

    private ParticleEffect pe;

    public ParticleActor() {
        super();
        pe = new ParticleEffect();
    }

    public void load(String pfxFile, String imageDirectory) {
        pe.load(Gdx.files.internal(pfxFile), Gdx.files.internal(imageDirectory));
    }

    public void start() {
        pe.start();
    }

    public void stop() {
        pe.allowCompletion();
    }

    public boolean isRunning() {
        return !pe.isComplete();
    }

    public void setPosition(float px, float py) {
        for (ParticleEmitter e : pe.getEmitters())
            e.setPosition(px, py);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        pe.update(delta);
        if (pe.isComplete() && !pe.getEmitters().first().isContinuous());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        pe.draw(batch);
    }

    public ParticleActor clone() {
        ParticleActor newbie = new ParticleActor();
        newbie.pe = new ParticleEffect(this.pe);
        return newbie;
    }
}
