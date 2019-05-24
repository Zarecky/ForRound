package com.dandream.forround;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class PhysicsActor extends AnimatedActor {

    protected Vector2 velocity;
    private Vector2 acceleration;
    private float maxSpeed;
    private float deceleration;
    private boolean autoAngle;
    private float angularVelocity;

    public PhysicsActor()
    {
        super();
        velocity = new Vector2();
        acceleration = new Vector2();
        maxSpeed = 9999;
        deceleration = 0;
        autoAngle = false;
    }

    @Override
    public void act(float delta) {
        super.act(delta);


        velocity.add(acceleration.x * delta, acceleration.y * delta);

        if (acceleration.len() < 0.01) {
            float decelerateAmount = deceleration * delta;
            if (getSpeed() < decelerateAmount) setSpeed(0);
            else setSpeed(getSpeed() - decelerateAmount);
        }

        if (getSpeed() > maxSpeed) setSpeed(maxSpeed);

        moveBy(velocity.x * delta, velocity.y * delta);

        if (autoAngle && getSpeed() > 0) setRotation(getMotionAngle());
    }

    public void setVelocityXY(float vx, float vy) {
        velocity.set(vx, vy);
    }

    public void addVelocityXY(float vx, float vy) {
        velocity.add(vx, vy);
    }

    public void setVelocityAS(float angleDeg, float speed) {
        velocity.x = speed * MathUtils.cosDeg(angleDeg);
        velocity.y = speed * MathUtils.sinDeg(angleDeg);
    }

    public void setAccelerationXY(float ax, float ay) {
        acceleration.set(ax, ay);
    }

    public void addAccelerationXY(float ax, float ay) {
        acceleration.add(ax, ay);
    }

    public void setAccelerationAS(float angleDeg, float speed) {
        acceleration.x = speed * MathUtils.cosDeg(angleDeg);
        acceleration.y = speed * MathUtils.sinDeg(angleDeg);
    }

    public void addAccelerationAS(float angle, float amount) {
        acceleration.add(amount * MathUtils.cosDeg(angle), amount * MathUtils.sinDeg(angle));
    }

    public void setDeceleration(float d) {
        deceleration = d;
    }

    public float getSpeed() {
        return velocity.len();
    }

    public void setSpeed(float speed) {
        velocity.setLength(speed);
    }

    public void setMaxSpeed(float speed) {
        maxSpeed = speed;
    }

    public float getMotionAngle() {
        return MathUtils.atan2(velocity.y, velocity.x) * MathUtils.radiansToDegrees;
    }

    public void setAutoAngle(boolean b) {
        autoAngle = b;
    }

    public void accerateForward(float speed) {
        setAccelerationAS(getRotation(), speed);
    }

    public void copy(PhysicsActor original) {
        super.copy(original);
        this.velocity = new Vector2(original.velocity);
        this.acceleration = new Vector2(original.acceleration);
        this.maxSpeed = original.maxSpeed;
        this.deceleration = original.deceleration;
        this.autoAngle = original.autoAngle;
    }

    public PhysicsActor clone() {
        PhysicsActor newbie = new PhysicsActor();
        newbie.copy(this);
        return newbie;
    }

    public float getAngularVelocity() {
        return angularVelocity;
    }

    public Vector2 getVelocity() {return velocity;}

    public void setAngularVelocity(float angularVelocity) {
        this.angularVelocity = angularVelocity;
    }
}
