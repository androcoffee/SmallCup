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

    CameraEx() {
        camera = new OrthographicCamera()
        camera.setToOrtho(false, Gdx.graphics.width, Gdx.graphics.height)
    }
}
