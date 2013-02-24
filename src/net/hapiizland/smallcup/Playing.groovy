/**
 * Created with IntelliJ IDEA.
 * User: tetsuya
 * Date: 13/02/19
 * Time: 18:37
 * To change this template use File | Settings | File Templates.
 */

package net.hapiizland.smallcup

import aurelienribon.tweenengine.Tween
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import groovy.transform.CompileStatic
import net.hapiizland.net.hapiizland.gdxex.Director
import net.hapiizland.net.hapiizland.gdxex.DrawingScheme
import net.hapiizland.net.hapiizland.gdxex.Entity
import net.hapiizland.net.hapiizland.gdxex.EntityScheme
import net.hapiizland.net.hapiizland.gdxex.FontDrawingScheme
import net.hapiizland.net.hapiizland.gdxex.GdxEx
import net.hapiizland.net.hapiizland.gdxex.IDrawingScheme
import net.hapiizland.net.hapiizland.gdxex.SpriteDrawingScheme
import net.hapiizland.net.hapiizland.gdxex.VAMovingScheme

@CompileStatic
class Playing implements Screen {
    Playing() {
    }

    @Override
    void show() {
        GdxEx.graphicsEx.loadTexture("player.png")

        def player = Player.createPlayer(new Vector2(100, 500))
        GdxEx.entityEx.addEntity(player)
        GdxEx.entityEx.addEntity(Player.createPlayerInfo(player))

        GdxEx.entityEx.addEntity(DebugInfo.createDebugInfo())

        //stage = new Stage(0, 0, true)
        //stage.setViewport(Gdx.graphics.width, Gdx.graphics.height, true)
    }

    @Override
    void hide() {
        //stage.dispose()
    }

    void update(float delta) {
        //stage.act(delta)
        if (GdxEx.inputEx.isMouseDown(Input.Buttons.LEFT)) {
            (0..<100).each { int i ->
                GdxEx.entityEx.addEntity(Player.createPlayer(new Vector2(GdxEx.inputEx.mousePos)))
            }
        }
    }

    @Override
    void render(float delta) {
        update(delta)

        //stage.draw()
    }

    @Override void resize(int width, int height) {}

    @Override void pause() {}
    @Override void resume() {}
    @Override void dispose() {}

    //private Image player
    //private Stage stage
}

/*@CompileStatic
class DrawOptions {
    final DrawOptions plus(DrawOptions options) {
        new DrawOptions(batch: options.batch ?: batch)
    }

    final void leftShift(DrawOptions options) {
        batch = options.batch ?: batch
    }
}*/

@CompileStatic
class DebugInfo {
    static Entity createDebugInfo() {
        def entity = new Entity()
        entity.pos.x = 0
        entity.pos.y = Gdx.graphics.height - 32

        def fontDrawingScheme = new FontDrawingScheme(GdxEx.graphicsEx.getFont("mplus-2c-regular.fnt"))
        entity.drawingScheme = fontDrawingScheme

        entity.addScheme(new EntityScheme() {
            @Override
            void setupScheme(Entity e) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            void updateScheme(Entity e) {
                //To change body of implemented methods use File | Settings | File Templates.
                fontDrawingScheme.text = "FPS = ${Gdx.graphics.framesPerSecond}\nEntities = ${GdxEx.entityEx.entitiesIter.size()}"
            }
        })

        entity
    }
}

@CompileStatic
class Player {
    static Entity createPlayer(Vector2 pos) {
        Texture texture = GdxEx.graphicsEx.getTexture("player.png")
        Entity player = new Entity()

        player.pos.x = pos.x
        player.pos.y = pos.y
        def drawingScheme = new SpriteDrawingScheme(new Sprite(texture, 0, 0, 32, 32))
        player.drawingScheme = drawingScheme
        player.color = new Color(player.color.r, player.color.g, player.color.b, 0)

        def afterGrow = new AfterGrowDrawingScheme(drawingScheme, 5, 20)
        //player.addSubDrawingScheme(afterGrow)

        player.movingScheme = new VAMovingScheme()
        player.speed = 2.0f
        player.velDegrees = MathUtils.random(360)

        player.addScheme(new EntityScheme() {
            @Override
            void setupScheme(Entity e) {
                Tween.to(e, Entity.ALPHA, 1.0f).target(1.0f).repeatYoyo(3, 0.0f).start(GdxEx.tweenEx.manager)
            }

            @Override
            void updateScheme(Entity e) {
                e.degrees += 1.0f
                //To change body of implemented methods use File | Settings | File Templates.
                if (GdxEx.inputEx.isKeyPressed(Input.Keys.LEFT)) {
                    e.acc.x -= 0.1f
                }
                if (GdxEx.inputEx.isKeyPressed(Input.Keys.RIGHT)) {
                    e.acc.x += 0.1f
                }

                if (e.pos.x < 0 || e.pos.x > Gdx.graphics.width || e.pos.y < 0 || e.pos.y > Gdx.graphics.height) {
                    e.done = true
                }
                //e.acc.y -= 0.1f
            }
        })

        player
    }

    static Entity createPlayerInfo(Entity player) {
        def playerInfo = new Entity()
        playerInfo.pos.x = 0
        playerInfo.pos.y = Gdx.graphics.height

        def fontDrawingScheme = new FontDrawingScheme(GdxEx.graphicsEx.getFont("mplus-2c-regular.fnt"))
        fontDrawingScheme.text = "pos = ${(int)player.pos.x}, ${(int)player.pos.y}"
        playerInfo.drawingScheme = fontDrawingScheme

        playerInfo.addScheme(new EntityScheme() {
            @Override
            void setupScheme(Entity e) {

            }

            @Override
            void updateScheme(Entity e) {
                //To change body of implemented methods use File | Settings | File Templates.
                fontDrawingScheme.text = "pos = ${(int)player.pos.x}, ${(int)player.pos.y}"
            }
        })

        playerInfo
    }
}

@CompileStatic
class AfterGrowDrawingScheme extends DrawingScheme {
    @Delegate IDrawingScheme scheme

    AfterGrowDrawingScheme(IDrawingScheme scheme, int num, int period) {
        this.scheme = scheme
        this.num = num
        this.period = period

        int total = num * period
        prevDrawingSchemes = [scheme.cpy()] * total
    }

    @Override
    void onSetupDrawingScheme(Entity e) {
    }

    @Override
    void onUpdateDrawingScheme(Entity e) {
        def cpyScheme = scheme.cpy()
        prevDrawingSchemes.add(0, cpyScheme)
        prevDrawingSchemes.pop()
    }

    @Override
    void onDrawDrawingScheme(Entity e) {
        prevDrawingSchemes.reverse().eachWithIndex { IDrawingScheme s, int i ->
            if (i % period == 0) {
                s.drawDrawingScheme(e)
            }
        }
    }

    IDrawingScheme createCopiedScheme() {
        null
    }

    private List<IDrawingScheme> prevDrawingSchemes = []
    int num
    int period
}
