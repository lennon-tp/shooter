package com.upf.shooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;
import java.util.Random;

public class Enemy {
    private Texture[] textures;
    private int currentFrame;
    private int[] frameSequence = {0, 1, 2, 3, 2, 1}; // Animation ping-pong
    private int sequenceIndex;
    private float animationTimer;
    private float animationSpeed = 0.1f;

    private Vector2 position;
    private Vector2 velocity;
    private float speed;
    private int type; // 1, 2 ou 3
    private boolean isActive;

    public Enemy(int type) {
        this.type = type;
        this.isActive = true;

        // Charger les sprites du type sélectionné
        textures = new Texture[4];
        for (int i = 0; i < 4; i++) {
            textures[i] = new Texture(Gdx.files.internal("sprites/enemies/Ennemi" + type + "-" + (i + 1) + ".png"));
        }

        // Position initiale (hors écran à droite)
        Random rand = new Random();
        position = new Vector2(Gdx.graphics.getWidth(), rand.nextInt(Gdx.graphics.getHeight() - 100));

        // Vitesse aléatoire et direction
        speed = rand.nextFloat() * 200 + 100;
        velocity = new Vector2(-speed, 0); // Déplacement de droite à gauche

        sequenceIndex = 0;
        currentFrame = frameSequence[sequenceIndex];
        animationTimer = 0;
    }

    public void update(float delta) {
        position.add(velocity.x * delta, velocity.y * delta);

        // Mettre à jour l'animation
        animationTimer += delta;
        if (animationTimer >= animationSpeed) {
            animationTimer = 0;
            sequenceIndex = (sequenceIndex + 1) % frameSequence.length;
            currentFrame = frameSequence[sequenceIndex];
        }

        // Désactiver si hors écran
        if (position.x < -textures[currentFrame].getWidth()) {
            isActive = false;
        }
    }

    public void draw(SpriteBatch batch) {
        if (isActive) {
            batch.draw(textures[currentFrame], position.x, position.y);
        }
    }

    public boolean isActive() {
        return isActive;
    }

    public void dispose() {
        for (Texture texture : textures) {
            texture.dispose();
        }
    }

    // Ajout de getBounds() pour gérer les collisions
    public Rectangle getBounds() {
        return new Rectangle(position.x, position.y, textures[currentFrame].getWidth(), textures[currentFrame].getHeight());
    }

    // Ajout de getPosition() pour positionner l'explosion
    public Vector2 getPosition() {
        return position;
    }
}
