/**
 * Created with IntelliJ IDEA.
 * User: tetsuya
 * Date: 13/02/19
 * Time: 0:56
 * To change this template use File | Settings | File Templates.
 */

package net.hapiizland.net.hapiizland.gdxex

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import groovy.transform.CompileStatic

@CompileStatic
interface IDirectorScheme {
    void initialize(Director d)
}

@CompileStatic
class Director extends Game {
    Map<String, Screen> screens = [:]
    private void setScreens(Map<String, Screen> map) {}

    Director(IDirectorScheme directorScheme) {
        this.directorScheme = directorScheme
    }

    void registerScreen(String name, Screen screen) {
        screens[name] = screen
    }

    void switchScreen(String name) {
        this.screen = screens[name]
    }

    @Override
    void create() {
        GdxEx.initialize(this)

        directorScheme.initialize(this)
    }

    @Override
    void dispose() {
        GdxEx.dispose()
    }

    void update() {
        GdxEx.update()

        if (GdxEx.inputEx.isKeyDown(Input.Keys.ESCAPE)) {
            Gdx.app.exit()
        }
    }

    @Override
    void render() {
        update()
        super.render()

        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        GdxEx.draw()

        GdxEx.inputEx.update()
    }

    private IDirectorScheme directorScheme
}
