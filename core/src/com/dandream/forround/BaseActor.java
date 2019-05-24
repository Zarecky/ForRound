package com.dandream.forround;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;

import java.util.List;

public class BaseActor extends Group {

    protected TextureRegion region;
    private Polygon boundingPolygon;
    protected List<? extends BaseActor> parentList;

    public BaseActor() {
        super();
        region = new TextureRegion();
        boundingPolygon = null;
        parentList = null;
    }

    public BaseActor(Texture texture) {
        super();
        int w = texture.getWidth();
        int h = texture.getHeight();
        setSize(w, h);
        region = new TextureRegion(texture);
        boundingPolygon = null;
    }

    public void setTexture(Texture texture) {
        int w = texture.getWidth();
        int h = texture.getHeight();
        setSize(w, h);
        region.setRegion(texture);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (boundingPolygon != null) {
            boundingPolygon.setScale(getScaleX(), getScaleY());
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Color color = getColor();
        batch.setColor(color);
        if (region.getTexture() != null && isVisible()) {
            batch.draw(region, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(),
                    getScaleX(), getScaleY(), getRotation());
        }

        super.draw(batch, parentAlpha);
    }

    public void setRectangleBoundary() {
        float w = getWidth();
        float h = getHeight();
        float[] vertices = {0, 0, w, 0, w, h, 0, h};
        boundingPolygon = new Polygon(vertices);
        boundingPolygon.setOrigin(getOriginX(), getOriginY());
    }

    public void setEllipseBoundary() {
        int n = 48;
        float w = getWidth();
        float h = getHeight();
        float[] vertices = new float[n*2];
        for (int i = 0; i < n; i++) {
            float t = i * 2 * 6.28f / n;
            vertices[2*i] = w/2 * MathUtils.cos(t) + w/2;
            vertices[2*i+1] = h/2 * MathUtils.sin(t) + h/2;
        }
        boundingPolygon = new Polygon(vertices);
        boundingPolygon.setOrigin(getOriginX(), getOriginY());
    }

    public Polygon getBoundingPolygon() {
        if (boundingPolygon == null) return null;
        boundingPolygon.setPosition(getX(), getY());
        boundingPolygon.setRotation(getRotation());
        return boundingPolygon;
    }

    public boolean overlaps(BaseActor other, boolean resolve) {
        Polygon poly1 = this.getBoundingPolygon();
        Polygon poly2 = other.getBoundingPolygon();

        if (!poly1.getBoundingRectangle().overlaps(poly2.getBoundingRectangle())) return false;

        Intersector.MinimumTranslationVector mtv = new Intersector.MinimumTranslationVector();
        boolean polyOverlaps = Intersector.overlapConvexPolygons(poly1, poly2, mtv);
        if (polyOverlaps && resolve) this.moveBy(mtv.normal.x * mtv.depth, mtv.normal.y  * mtv.depth);
        float significant = 0.5f;
        return polyOverlaps && (mtv.depth > significant);
    }

    public void copy(BaseActor original) {
        if (original.region.getTexture() == null) this.region = new TextureRegion();
        else this.region = new TextureRegion(original.region);
        if (original.boundingPolygon != null) {
            this.boundingPolygon = new Polygon(original.boundingPolygon.getVertices());
            this.boundingPolygon.setOrigin(original.getOriginX(), original.getOriginY());
        }
        this.setPosition(original.getX(), original.getY());
        this.setOrigin(original.getOriginX(), original.getOriginY());
        this.setSize(original.getWidth(), original.getHeight());
        this.setColor(original.getColor());
        this.setVisible(original.isVisible());
    }

    public BaseActor clone() {
        BaseActor newbie = new BaseActor();
        newbie.copy(this);
        return newbie;
    }

    public void setOriginCenter() {
        if (getWidth() == 0) System.err.println("Error: actor size not set");
        setOrigin(getWidth()/2, getHeight()/2);
    }

    public void setParentList(List<? extends BaseActor> parentList) {
        this.parentList = parentList;
    }

    public void destroy() {
        remove();
        if (parentList != null) parentList.remove(this);
    }

    public void moveToOrigin(BaseActor target) {
        this.setPosition(target.getX() + target.getOriginX() - this.getOriginX(),
                target.getY() + target.getOriginY() - this.getOriginY());
    }
}
