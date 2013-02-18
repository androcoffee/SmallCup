/**
 * Created with IntelliJ IDEA.
 * User: tetsuya
 * Date: 13/02/19
 * Time: 0:53
 * To change this template use File | Settings | File Templates.
 */

package net.hapiizland.net.hapiizland.gdxex

import groovy.transform.CompileStatic
import com.badlogic.gdx.Gdx

@CompileStatic
class GdxEx {
    static InputEx getInputEx() { _inputEx }

    static void initialize() {
        _inputEx = new InputEx()
        Gdx.input.setInputProcessor(this.inputEx)
    }

    private static InputEx _inputEx
}
