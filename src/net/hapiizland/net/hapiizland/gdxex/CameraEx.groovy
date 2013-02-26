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

    private OrthographicCamera uiCamera
}
