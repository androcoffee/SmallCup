/**
 * Created with IntelliJ IDEA.
 * User: tetsuya
 * Date: 13/02/18
 * Time: 22:59
 * To change this template use File | Settings | File Templates.
 */

package net.hapiizland.smallcup

import groovy.transform.CompileStatic
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration

@CompileStatic
class Main {
    static void main(String[] args) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration()
        config.title = "SmallCup Sample v0.1"
        config.useGL20 = true
        config.width = 800
        config.height = 600
        new LwjglApplication(new Director(), config)
    }
}
