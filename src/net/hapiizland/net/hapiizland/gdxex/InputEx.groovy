/**
 * Created with IntelliJ IDEA.
 * User: tetsuya
 * Date: 13/02/19
 * Time: 0:53
 * To change this template use File | Settings | File Templates.
 */

package net.hapiizland.net.hapiizland.gdxex

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

    void update() {
        keyStates.each { int keycode, boolean state ->
            prevKeyStates[keycode] = state
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
        false
    }

    @Override
    boolean touchUp (int x, int y, int pointer, int button) {
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
}

