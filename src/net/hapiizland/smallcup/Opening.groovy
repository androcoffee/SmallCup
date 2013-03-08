/**
 * Created with IntelliJ IDEA.
 * User: tetsuya
 * Date: 13/02/19
 * Time: 15:33
 * To change this template use File | Settings | File Templates.
 */

package net.hapiizland.smallcup

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.math.Vector2
import groovy.transform.CompileStatic
import net.hapiizland.net.hapiizland.gdxex.Entity
import net.hapiizland.net.hapiizland.gdxex.FontDrawingScheme
import net.hapiizland.net.hapiizland.gdxex.GdxEx

@CompileStatic
class Opening implements Screen {
    Opening() {
    }

    @Override
    void show() {
        GdxEx.entityEx.addEntity(TitleText.createTitleText())
    }

    @Override
    void hide() {
        GdxEx.entityEx.removeAllEntities()
    }

    void update(float delta) {
        if ([Input.Keys.Z, Input.Keys.SPACE, Input.Keys.ENTER].collect { int code -> GdxEx.inputEx.isKeyDown(code) }.contains(true)) {
            GdxEx.director.switchScreen("Playing")
        }
    }

    @Override
    void render(float delta) {
        update(delta)
    }

    @Override void resize(int width, int height) {}
    @Override void pause() {}
    @Override void resume() {}
    @Override void dispose() {}
}

@CompileStatic
class TitleText {
    static Entity createTitleText() {
        def entity = new Entity(new Vector2(100, Gdx.graphics.height - 100), Attributes.NO_HIT)

        def drawingScheme = new FontDrawingScheme(GdxEx.graphicsEx.getFont("mplus-2c-regular.fnt"))
        drawingScheme.text = "小さなカップに夢現の一粒を"
        entity.drawingScheme = drawingScheme

        entity
    }
}