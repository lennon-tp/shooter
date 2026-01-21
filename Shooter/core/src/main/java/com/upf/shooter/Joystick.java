package com.upf.shooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class Joystick {
    private Stage stage;
    private Touchpad touchpad;

    public Joystick() {
        // Création du skin du joystick
        Skin touchpadSkin = new Skin();
        touchpadSkin.add("joystick_background", new Texture("ui/joystick_background.png"));
        touchpadSkin.add("joystick_knob", new Texture("ui/joystick_knob.png"));

        // Création du style du touchpad
        TouchpadStyle touchpadStyle = new TouchpadStyle();
        Drawable touchBackground = touchpadSkin.getDrawable("joystick_background");
        Drawable touchKnob = touchpadSkin.getDrawable("joystick_knob");
        touchpadStyle.background = touchBackground;
        touchpadStyle.knob = touchKnob;

        // Création du touchpad
        touchpad = new Touchpad(10, touchpadStyle);
        touchpad.setBounds(30, 30, 250, 250); // Position en bas à gauche

        // Création de la scène et ajout du touchpad
        stage = new Stage();
        stage.addActor(touchpad);
        Gdx.input.setInputProcessor(stage);
    }

    public void render() {
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    public void dispose() {
        stage.dispose();
    }

    public float getKnobPercentX() {
        return touchpad.getKnobPercentX();
    }

    public float getKnobPercentY() {
        return touchpad.getKnobPercentY();
    }
}
