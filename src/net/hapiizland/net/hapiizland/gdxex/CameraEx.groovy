/**
 * Created with IntelliJ IDEA.
 * User: AndroCofffee
 * Date: 13/02/22
 * Time: 1:54
 * To change this template use File | Settings | File Templates.
 */

package net.hapiizland.net.hapiizland.gdxex

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import groovy.transform.CompileStatic

@CompileStatic
class CameraEx {
    @Delegate OrthographicCamera camera
    OrthographicCamera getUiCamera() { uiCamera }

    CameraEx() {
        camera = new OrthographicCamera()
        camera.setToOrtho(false, Gdx.graphics.width, Gdx.graphics.height)

        uiCamera = new OrthographicCamera()
        uiCamera.setToOrtho(false, Gdx.graphics.width, Gdx.graphics.height)
    }

    void update() {
        camera.update()
        uiCamera.update()
    }

    Vector2 getPos() {
        new Vector2(camera.position.x, camera.position.y)
    }

    Vector2 calcPosOnCamera(Vector2 pos) {
        new Vector2(pos.x - this.pos.x + camera.viewportWidth / 2.0f as float,
                pos.y - this.pos.y + camera.viewportHeight / 2.0f as float)
    }

    Vector2 calcPosOnWorld(Vector2 pos) {
        new Vector2(pos.x + this.pos.x - camera.viewportWidth / 2.0f as float,
                pos.y + this.pos.y - camera.viewportHeight / 2.0f as float)
    }

    Rectangle getBounds() {
        def x = camera.position.x
        def y = camera.position.y
        def width = camera.viewportWidth
        def height = camera.viewportHeight

        new Rectangle(
                x - width / 2.0f as float,
                y - height / 2.0f as float,
                width,
                height)
    }

    private OrthographicCamera uiCamera
}
