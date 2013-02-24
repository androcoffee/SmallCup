/**
 * Created with IntelliJ IDEA.
 * User: tetsuya
 * Date: 13/02/19
 * Time: 0:53
 * To change this template use File | Settings | File Templates.
 */

package net.hapiizland.net.hapiizland.gdxex

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import groovy.transform.CompileStatic
import com.badlogic.gdx.InputProcessor

@CompileStatic
class InputEx implements InputProcessor {
    boolean isKeyPressed(int keycode) {
        keyStates[keycode]
    }

    boolean isKeyDown(int keycode) {
        keyStates[keycode] && !prevKeyStates[keycode]
    }

    float getMouseX() { Gdx.input.x }
    float getMouseY() { Gdx.graphics.height - Gdx.input.y }

    Vector2 getMousePos() {
        new Vector2(mouseX, mouseY)
    }

    boolean isMousePressed(int button) {
        mouseStates[button]
    }

    boolean isMouseDown(int button) {
        mouseStates[button] && !prevMouseStates[button]
    }

    void update() {
        keyStates.each { int keycode, boolean state ->
            prevKeyStates[keycode] = state
        }

        mouseStates.each { int button, boolean state ->
            prevMouseStates[button] = state
        }
    }

    @Override
    boolean keyDown(int keycode) {
        keyStates[keycode] = true
        false
    }

    @Override
    boolean keyUp(int keycode) {
        keyStates[keycode] = false
        false
    }

    @Override
    boolean keyTyped(char character) {
        false
    }

    @Override
    boolean mouseMoved(int x, int y) {
        false
    }

    @Override
    boolean touchDown (int x, int y, int pointer, int button) {
        mouseStates[button] = true
        false
    }

    @Override
    boolean touchUp (int x, int y, int pointer, int button) {
        mouseStates[button] = false
        false
    }

    @Override
    boolean touchDragged (int x, int y, int pointer) {
        false
    }

    @Override
    boolean touchMoved (int x, int y) {
        false
    }

    @Override
    boolean scrolled (int amount) {
        false
    }

    Map<Integer, Boolean> keyStates = [:]
    Map<Integer, Boolean> prevKeyStates = [:]
    Map<Integer, Boolean> mouseStates = [:]
    Map<Integer, Boolean> prevMouseStates = [:]
}

