/**
 * Created with IntelliJ IDEA.
 * User: tetsuya
 * Date: 13/02/19
 * Time: 0:53
 * To change this template use File | Settings | File Templates.
 */

package net.hapiizland.net.hapiizland.gdxex

import com.badlogic.gdx.math.Vector2
import groovy.transform.CompileStatic
import com.badlogic.gdx.Gdx

@CompileStatic
class GdxEx {
    static InputEx inputEx
    private static InputEx setInputEx(InputEx iex) { inputEx = iex }

    static GraphicsEx graphicsEx
    private static GraphicsEx setGraphicsEx(GraphicsEx gex) { graphicsEx = gex }

    static EntityEx entityEx
    private static EntityEx setEntityEx(EntityEx eex) { entityEx = eex }

    static CameraEx cameraEx
    private static CameraEx setCameraEx(CameraEx cex) { cameraEx = cex }

    static TweenEx tweenEx
    private static TweenEx setTweenEx(TweenEx tex) { tweenEx = tex }

    static Director director
    private static Director setDirector(Director d) { director = d }

    static void initialize(Director director) {
        inputEx = new InputEx()
        Gdx.input.setInputProcessor(this.inputEx)

        graphicsEx = new GraphicsEx()
        entityEx = new EntityEx()
        cameraEx = new CameraEx()
        tweenEx = new TweenEx()

        this.director = director
    }

    static void update() {
        cameraEx.update()
        entityEx.update()
        tweenEx.update(1.0f / 60.0f as float)
    }

    static void draw() {
        entityEx.draw()
    }

    static void dispose() {
        graphicsEx.dispose()
    }
}

@CompileStatic
class Vector2Ex {
    static float calcDegrees(Vector2 from, Vector2 to) {
        to.cpy().sub(from).angle()
    }
}