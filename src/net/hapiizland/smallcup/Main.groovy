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
import net.hapiizland.net.hapiizland.gdxex.Director
import net.hapiizland.net.hapiizland.gdxex.GdxEx
import net.hapiizland.net.hapiizland.gdxex.IDirectorScheme

@CompileStatic
class DirectorScheme implements IDirectorScheme {
    void initialize(Director d) {
        GdxEx.graphicsEx.imagePath = "data/images/"
        GdxEx.graphicsEx.fontPath = "data/fonts/"
        GdxEx.graphicsEx.loadFont("mplus-2c-regular.fnt")

        d.registerScreen("Opening", new Opening())
        d.registerScreen("Playing", new Playing())

        d.switchScreen("Opening")
    }
}

@CompileStatic
class Main {
    static void main(String[] args) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration()
        config.title = "SmallCup Sample v0.1"
        config.useGL20 = true
        config.width = 800
        config.height = 600
        config.resizable = false
        //config.fullscreen = true
        new LwjglApplication(new Director(new DirectorScheme()), config)
    }
}
