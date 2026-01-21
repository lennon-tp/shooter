package com.upf.shooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;

public class Player {
    private Texture[] textures; // Tableau des textures du vaisseau
    private int currentFrame;
    private float animationTimer;
    private float animationSpeed = 0.1f; // Vitesse de changement d'image

    // Séquence d'animation ping-pong : 1-2-3-4-5-6-7-6-5-4-3-2-1
    private int[] frameSequence = {0, 1, 2, 3, 4, 5, 6, 5, 4, 3, 2, 1};
    private int sequenceIndex;

    private Vector2 position;
    private Vector2 velocity;
    private float speed = 500f; // Vitesse du vaisseau

    private float fireCooldown = 0.5f; // Fixé à 0.5s pour respecter la demande
    private float fireTimer = 0;
    private boolean isShooting = false; // Évite le spam tactile

    public Player() {
        // Charger les textures d'animation
        int numFrames = 7;
        textures = new Texture[numFrames];
        for (int i = 0; i < numFrames; i++) {
            textures[i] = new Texture(Gdx.files.internal("sprites/player/Player" + (i + 1) + ".png"));
        }

        position = new Vector2(Gdx.graphics.getWidth() / 4f, Gdx.graphics.getHeight() / 2f);
        velocity = new Vector2(0, 0);
        sequenceIndex = 0;
        currentFrame = frameSequence[sequenceIndex];
        animationTimer = 0;
    }

    public void update(float delta) {
        handleInput(delta);
        updatePosition(delta);
        updateAnimation(delta);
        fireTimer += delta; // Mise à jour du cooldown de tir
    }

    public void updateWithJoystick(float joystickX, float joystickY, float delta) {
        velocity.set(joystickX * speed, joystickY * speed);
        updatePosition(delta);
        updateAnimation(delta);
        fireTimer += delta;
    }

    private void updatePosition(float delta) {
        position.add(velocity.x * delta, velocity.y * delta);

        // Empêcher de sortir de l'écran
        float width = textures[currentFrame].getWidth();
        float height = textures[currentFrame].getHeight();

        if (position.x < 0) position.x = 0;
        if (position.x > Gdx.graphics.getWidth() - width) position.x = Gdx.graphics.getWidth() - width;
        if (position.y < 0) position.y = 0;
        if (position.y > Gdx.graphics.getHeight() - height) position.y = Gdx.graphics.getHeight() - height;
    }

    private void updateAnimation(float delta) {
        animationTimer += delta;
        if (animationTimer >= animationSpeed) {
            animationTimer = 0;
            sequenceIndex = (sequenceIndex + 1) % frameSequence.length;
            currentFrame = frameSequence[sequenceIndex];
        }
    }

    private void handleInput(float delta) {
        velocity.set(0, 0);

        // Déplacement avec touches
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) velocity.x = -speed;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) velocity.x = speed;
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) velocity.y = speed;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) velocity.y = -speed;

        // Gestion du tir tactile
        if (Gdx.input.isTouched()) {
            if (!isShooting && canFire()) {
                isShooting = true;
                resetFireTimer();
            }
        } else {
            isShooting = false; // Réinitialise l’état du tir quand l’écran est relâché
        }
    }

    public boolean canFire() {
        return fireTimer >= fireCooldown;
    }

    public void resetFireTimer() {
        fireTimer = 0;
    }

    public void draw(SpriteBatch batch) {
        batch.draw(textures[currentFrame], position.x, position.y);
    }

    public Rectangle getBounds() {
        return new Rectangle(position.x, position.y, textures[currentFrame].getWidth(), textures[currentFrame].getHeight());
    }

    public Vector2 getPosition() {
        return position;
    }

    public void dispose() {
        for (Texture texture : textures) {
            texture.dispose();
        }
    }
}
