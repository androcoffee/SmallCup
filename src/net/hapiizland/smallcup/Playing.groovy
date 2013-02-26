/**
 * Created with IntelliJ IDEA.
 * User: tetsuya
 * Date: 13/02/19
 * Time: 18:37
 * To change this template use File | Settings | File Templates.
 */

package net.hapiizland.smallcup

import aurelienribon.tweenengine.BaseTween
import aurelienribon.tweenengine.Timeline
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
import com.badlogic.gdx.math.Vector3
import groovy.transform.CompileStatic
import net.hapiizland.net.hapiizland.gdxex.Director
import net.hapiizland.net.hapiizland.gdxex.DrawingScheme
import net.hapiizland.net.hapiizland.gdxex.Entity
import net.hapiizland.net.hapiizland.gdxex.EntityScheme
import net.hapiizland.net.hapiizland.gdxex.FontDrawingScheme
import net.hapiizland.net.hapiizland.gdxex.GdxEx
import net.hapiizland.net.hapiizland.gdxex.IDrawingScheme
import net.hapiizland.net.hapiizland.gdxex.LivingScheme
import net.hapiizland.net.hapiizland.gdxex.SpriteDrawingScheme
import net.hapiizland.net.hapiizland.gdxex.TweenEx
import net.hapiizland.net.hapiizland.gdxex.VAMovingScheme
import net.hapiizland.net.hapiizland.gdxex.Vector2Ex
import net.hapiizland.smallcup.entities.Bullet

@CompileStatic
class Playing implements Screen {
    Playing() {
    }

    @Override
    void show() {
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
        entity.followingCamera = true

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

        def livingScheme = new LivingScheme(1)
        livingScheme.onKilled = {->
            player.lastStand = 60
            Tween.to(player, Entity.SCALE, 1.0f).target(0.0f).start(GdxEx.tweenEx.manager)
        }
        player.livingScheme = livingScheme

        def drawingScheme = new SpriteDrawingScheme(new Sprite(texture, 0, 0, 32, 32))
        player.drawingScheme = drawingScheme

        def afterGrow = DigitAfterGrowDrawingScheme.createInstance(
                scheme: drawingScheme, num: 5, period: 10, alphaScaling: true, sizeScaling: true)
        //def afterGrow = new AfterGrowDrawingScheme(drawingScheme, 5, 20, true, true)
        player.addSubDrawingScheme(afterGrow)

        player.movingScheme = new VAMovingScheme()

        player.addScheme(new EntityScheme() {
            @Override
            void setupScheme(Entity e) {
            }

            @Override
            void updateScheme(Entity e) {
                e.drawingDegrees += 1.0f
                //To change body of implemented methods use File | Settings | File Templates.
                if (GdxEx.inputEx.isKeyPressed(Input.Keys.LEFT)) {
                    e.acc.x -= 0.1f
                }
                if (GdxEx.inputEx.isKeyPressed(Input.Keys.RIGHT)) {
                    e.acc.x += 0.1f
                }
                if (GdxEx.inputEx.isKeyPressed(Input.Keys.DOWN)) {
                    e.acc.y -= 0.1f
                }
                if (GdxEx.inputEx.isKeyPressed(Input.Keys.UP)) {
                    e.acc.y += 0.1f
                }

                if (GdxEx.inputEx.isKeyPressed(Input.Keys.Z)) {
                    //GdxEx.cameraEx.camera.zoom += 0.1f
                } else if (GdxEx.inputEx.isKeyPressed(Input.Keys.O)) {
                    //GdxEx.cameraEx.camera.zoom -= 0.1f
                }

                if (GdxEx.inputEx.isMouseDown(Input.Buttons.LEFT)) {
                    (0..<100).each { int i ->
                        GdxEx.entityEx.addEntity(
                                Bullet.createBullet(e.pos, [
                                        speed: 7.0f,
                                        velDegrees: 360 / 100.0f * i as float,
                                        color: new Color(0.9f, 0.3f, 0.7f, 1.0f),
                                        imageNo: 6,
                                        scheme: new EntityScheme() {
                                            @Override void setupScheme(Entity b) {
                                                Timeline.createSequence()
                                                        .pushPause(0.1f)
                                                        .push(TweenEx.lambdaCallback { int type, BaseTween tween ->
                                                    Tween.to(b, Entity.VELDEGREES, 1.0f).target(new Vector2().sub(b.pos).angle()).start(GdxEx.tweenEx.manager)
                                                }).start(GdxEx.tweenEx.manager)
                                            }
                                            @Override void updateScheme(Entity b) {}
                                        }
                                ]))
                    }
                }

                if (e.pos.x < 0 || e.pos.x > Gdx.graphics.width || e.pos.y < 0 || e.pos.y > Gdx.graphics.height) {
                    //e.hp = 0
                }
                //GdxEx.cameraEx.camera.position.set(new Vector3(e.pos.x, e.pos.y, GdxEx.cameraEx.camera.position.z))
                GdxEx.cameraEx.camera.position.x = e.pos.x
                GdxEx.cameraEx.camera.position.y = e.pos.y

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
        playerInfo.followingCamera = true

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

    final static AfterGrowDrawingScheme createInstance(Map kwargs) {
        new AfterGrowDrawingScheme(
                kwargs.scheme as IDrawingScheme,
                kwargs.num as int,
                kwargs.period as int,
                kwargs.alphaScaling as boolean,
                kwargs.sizeScaling as boolean)
    }

    AfterGrowDrawingScheme(IDrawingScheme scheme, int num, int period, boolean alphaScaling, boolean sizeScaling) {
        this.scheme = scheme
        this.num = num
        this.period = period
        this.alphaScaling = alphaScaling
        this.sizeScaling = sizeScaling

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

        def total = num * period

        prevDrawingSchemes.each { IDrawingScheme s ->
            if (alphaScaling) s.color = new Color(s.color.r, s.color.g, s.color.b, s.color.a - (e.color.a - 0.3f) / total as float)
            if (sizeScaling) {
                s.setScale(Math.max(s.scaleX - e.scaleX / total, 0.0) as float, Math.max(s.scaleY - e.scaleY / total, 0.0) as float)
            }}
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
    boolean alphaScaling
    boolean sizeScaling
}

@CompileStatic
class DigitAfterGrowDrawingScheme extends DrawingScheme {
    @Delegate IDrawingScheme scheme

    final static DigitAfterGrowDrawingScheme createInstance(Map kwargs) {
        new DigitAfterGrowDrawingScheme(
                kwargs.scheme as IDrawingScheme,
                kwargs.num as int,
                kwargs.period as int,
                kwargs.alphaScaling as boolean,
                kwargs.sizeScaling as boolean)
    }

    DigitAfterGrowDrawingScheme(IDrawingScheme scheme, int num, int period, boolean alphaScaling, boolean sizeScaling) {
        this.scheme = scheme
        this.num = num
        this.period = period
        this.alphaScaling = alphaScaling
        this.sizeScaling = sizeScaling

        prevDrawingSchemes = [scheme.cpy()] * num
    }

    @Override
    void onSetupDrawingScheme(Entity e) {
    }

    @Override
    void onUpdateDrawingScheme(Entity e) {
        if (GdxEx.director.elapsedFrames % period == 0) {
            def cpyScheme = scheme.cpy()
            prevDrawingSchemes.add(0, cpyScheme)
            prevDrawingSchemes.pop()
        }
        def total = num * period

        prevDrawingSchemes.each { IDrawingScheme s ->
            if (alphaScaling) s.color = new Color(s.color.r, s.color.g, s.color.b, s.color.a - (e.color.a - 0.3f) / total as float)
            if (sizeScaling) {
                s.setScale(Math.max(s.scaleX - e.scaleX / total, 0.0) as float, Math.max(s.scaleY - e.scaleY / total, 0.0) as float)
            }
        }
    }

    @Override
    void onDrawDrawingScheme(Entity e) {
        prevDrawingSchemes.reverse().eachWithIndex { IDrawingScheme s, int i ->
            s.drawDrawingScheme(e)
        }
    }

    IDrawingScheme createCopiedScheme() {
        null
    }

    private List<IDrawingScheme> prevDrawingSchemes = []
    int num
    int period
    boolean alphaScaling
    boolean sizeScaling
}