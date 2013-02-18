/**
 * Created with IntelliJ IDEA.
 * User: tetsuya
 * Date: 13/02/19
 * Time: 0:56
 * To change this template use File | Settings | File Templates.
 */

package net.hapiizland.smallcup

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Sprite
import groovy.transform.CompileStatic
import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.GL10
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle
import net.hapiizland.net.hapiizland.gdxex.GdxEx

@CompileStatic
class Director implements ApplicationListener {
    @Override
    void create() {
        GdxEx.initialize()

        playerTexture = new Texture(Gdx.files.internal("data/player.png"))

        camera = new OrthographicCamera()
        camera.setToOrtho(false, 800, 600)

        batch = new SpriteBatch()

        player = new Sprite(playerTexture, 0, 0, 32, 32)
        player.x = 0
        player.y = 0
        player.color = new Color(1.0f, 0.2f, 0.2f, 0.3f)
    }

    void update() {
        player.rotate(1)

        if (GdxEx.inputEx.isKeyDown(Input.Keys.Z)) {
        }

        if (GdxEx.inputEx.isKeyPressed(Input.Keys.LEFT)) {
            player.x -= 3.0f
        }
        if (GdxEx.inputEx.isKeyPressed(Input.Keys.RIGHT)) {
            player.x += 3.0f
        }

        GdxEx.inputEx.update()
    }

    @Override
    void render() {
        update()

        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1)
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT)

        camera.update()

        batch.setProjectionMatrix(camera.combined)
        batch.begin()
        player.draw(batch)
        batch.end()
    }

    @Override
    void dispose() {
        playerTexture.dispose()
        batch.dispose()
    }

    @Override
    void resize(int width, int height) {
    }

    @Override
    void pause() {
    }

    @Override
    void resume() {
    }

    private Texture playerTexture
    private OrthographicCamera camera
    private SpriteBatch batch
    private Sprite player
}

