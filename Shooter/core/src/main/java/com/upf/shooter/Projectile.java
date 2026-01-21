package com.upf.shooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;

public class Projectile {
    private Texture[] textures;
    private int currentFrame;
    private float animationTimer;
    private float animationSpeed = 0.1f; // Changement rapide de sprite

    private Vector2 position;
    private float speed = 800f;
    private boolean active = true;

    public Projectile(float x, float y) {
        // Charger les textures pour l'animation
        textures = new Texture[2];
        textures[0] = new Texture(Gdx.files.internal("sprites/lasers/Laser1.png"));
        textures[1] = new Texture(Gdx.files.internal("sprites/lasers/Laser2.png"));

        position = new Vector2(x, y);
        currentFrame = 0;
        animationTimer = 0;
    }

    public void update(float delta) {
        position.x += speed * delta;

        // Mise à jour de l'animation
        animationTimer += delta;
        if (animationTimer >= animationSpeed) {
            animationTimer = 0;
            currentFrame = (currentFrame + 1) % textures.length; // Alterne entre Laser1 et Laser2
        }

        if (position.x > Gdx.graphics.getWidth()) active = false; // Désactiver quand hors écran
    }

    public void draw(SpriteBatch batch) {
        if (active) batch.draw(textures[currentFrame], position.x, position.y);
    }

    public boolean isActive() {
        return active;
    }

    public Rectangle getBounds() {
        return new Rectangle(position.x, position.y, textures[currentFrame].getWidth(), textures[currentFrame].getHeight());
    }

    public void dispose() {
        for (Texture texture : textures) {
            texture.dispose();
        }
    }
}
