/**
 * Created with IntelliJ IDEA.
 * User: AndroCofffee
 * Date: 13/02/21
 * Time: 22:44
 * To change this template use File | Settings | File Templates.
 */

package net.hapiizland.net.hapiizland.gdxex

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import groovy.transform.CompileStatic

@CompileStatic
interface IGraphicsScheme {
    String getTexturePath()
    String getFontPath()
}

@CompileStatic
class GraphicsEx {
    SpriteBatch batch = new SpriteBatch()

    String texturePath = ""
    void setTexturePath(String path) {
        texturePath = formatPath(path)
    }

    String fontPath = ""
    void setFontPath(String path) {
        fontPath = formatPath(path)
    }

    GraphicsEx(IGraphicsScheme scheme) {
        texturePath = scheme.texturePath
        fontPath = scheme.fontPath
    }

    void loadFont(String filename) {
        fonts[filename] = new BitmapFont(Gdx.files.internal(fontPath + filename), false)
    }

    BitmapFont getFont(String filename) {
        fonts[filename]
    }

    void loadTexture(String filename) {
        textures[filename] = new Texture(Gdx.files.internal(texturePath + filename))
    }

    Texture getTexture(String filename) {
        textures[filename]
    }

    void clearBuffer() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
    }

    void beginBatchDrawing() {
        batch.begin()
        batch.projectionMatrix = GdxEx.cameraEx.camera.combined
    }

    void endBatchDrawing() {
        batch.end()
    }

    void dispose() {
        textures.values().each { Texture texture -> texture.dispose() }
        fonts.values().each { BitmapFont font -> font.dispose() }
        batch.dispose()
    }

    private Map<String, BitmapFont> fonts = [:]
    private Map<String, Texture> textures = [:]

    static private formatPath(String path) {
        path.endsWith("/") ? path : path + "/"
    }
}
