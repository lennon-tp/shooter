package com.upf.shooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Explosion {
    private Texture[] frames;
    private int currentFrame;
    private float animationTimer;
    private float frameSpeed = 0.1f;
    private boolean active;
    private Vector2 position;
    private Sound explosionSound;

    public Explosion(float x, float y) {
        position = new Vector2(x, y);
        active = true;
        frames = new Texture[7];

        // Charger les textures d'explosion
        for (int i = 0; i < 7; i++) {
            frames[i] = new Texture(Gdx.files.internal("sprites/explosions/Explosion" + (i + 1) + ".png"));
        }

        // Charger le son de l'explosion et le jouer
        explosionSound = Gdx.audio.newSound(Gdx.files.internal("music/Explode1.wav"));
        explosionSound.play();

        currentFrame = 0;
        animationTimer = 0;
    }

    public void update(float delta) {
        animationTimer += delta;
        if (animationTimer >= frameSpeed) {
            animationTimer = 0;
            currentFrame++;
            if (currentFrame >= frames.length) {
                active = false; // Fin de l'animation
            }
        }
    }

    public void draw(SpriteBatch batch) {
        if (active) {
            batch.draw(frames[currentFrame], position.x, position.y);
        }
    }

    public boolean isActive() {
        return active;
    }

    public void dispose() {
        for (Texture frame : frames) {
            frame.dispose();
        }
        explosionSound.dispose();
    }
}
