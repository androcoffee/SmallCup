/**
 * Created with IntelliJ IDEA.
 * User: AndroCofffee
 * Date: 13/02/21
 * Time: 22:44
 * To change this template use File | Settings | File Templates.
 */

package net.hapiizland.net.hapiizland.gdxex

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import groovy.transform.CompileStatic

@CompileStatic
class GraphicsEx {
    SpriteBatch batch = new SpriteBatch()
    Map<String, BitmapFont> fonts = [:]
    Map<String, Texture> textures = [:]

    private setFonts(Map<String, BitmapFont> map) {}
    private setTextures(Map<String, Texture> map) {}

    String imagePath = ""
    void setImagePath(String path) {
        imagePath = formatPath(path)
    }

    String fontPath = ""
    void setFontPath(String path) {
        fontPath = formatPath(path)
    }

    void loadFont(String filename) {
        fonts[filename] = new BitmapFont(Gdx.files.internal(fontPath + filename), false)
    }

    BitmapFont getFont(String filename) {
        fonts[filename]
    }

    void loadTexture(String filename) {
        textures[filename] = new Texture(Gdx.files.internal(imagePath + filename))
    }

    Texture getTexture(String filename) {
        textures[filename]
    }

    void dispose() {
        textures.values().each { Texture texture -> texture.dispose() }
        fonts.values().each { BitmapFont font -> font.dispose() }
        batch.dispose()
    }

    static private formatPath(String path) {
        path.endsWith("/") ? path : path + "/"
    }
}
