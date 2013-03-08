/**
 * Created with IntelliJ IDEA.
 * User: tetsuya
 * Date: 13/02/25
 * Time: 21:10
 * To change this template use File | Settings | File Templates.
 */

package net.hapiizland.smallcup.entities

import aurelienribon.tweenengine.BaseTween
import aurelienribon.tweenengine.Timeline
import aurelienribon.tweenengine.Tween
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import groovy.transform.CompileStatic
import net.hapiizland.net.hapiizland.gdxex.Entity
import net.hapiizland.net.hapiizland.gdxex.EntityScheme
import net.hapiizland.net.hapiizland.gdxex.GdxEx
import net.hapiizland.net.hapiizland.gdxex.LivingScheme
import net.hapiizland.net.hapiizland.gdxex.SpriteDrawingScheme
import net.hapiizland.net.hapiizland.gdxex.TweenEx
import net.hapiizland.net.hapiizland.gdxex.VAMovingScheme
import net.hapiizland.smallcup.AfterGrowDrawingScheme
import net.hapiizland.smallcup.Attributes
import net.hapiizland.smallcup.DigitAfterGrowDrawingScheme
import net.hapiizland.smallcup.SmoothAfterGrowDrawingScheme

@CompileStatic
class Bullet {
    static final Entity createBullet(Vector2 pos, Map kwargs) {
        def entity = new Entity(pos, Attributes.BULLET)

        def imageNo = (kwargs?.imageNo ?: 0) as int
        def imgX = imageNo % 8 as int
        def imgY = imageNo.intdiv(8) as int
        def drawingScheme = new SpriteDrawingScheme(
                new Sprite(GdxEx.graphicsEx.getTexture("bullet.png"), imgX * 32, imgY * 32, 32, 32))
        entity.drawingScheme = drawingScheme
        def color = (kwargs?.color ?: new Color(1.0f, 1.0f, 1.0f, 0.7f)) as Color
        color.a = 0.6f
        entity.color = color

        entity.addSubDrawingScheme(
                SmoothAfterGrowDrawingScheme.createInstance(scheme: drawingScheme, num: 5, period: 15, sizeScaling: true, alphaScaling: true))

        entity.movingScheme = new VAMovingScheme()
        entity.speed = (kwargs?.speed ?: 1.0f) as float
        entity.velDegrees = (kwargs?.velDegrees ?: 0) as float

        entity.livingScheme = new LivingScheme(1) {
            @Override void onKilled(Entity e) {
                Timeline.createParallel()
                        //.push(Tween.to(e, Entity.ALPHA, 0.5f).target(0))
                        .push(Tween.to(e, Entity.SCALE, 1.0f).target(0.0f))
                        .start(GdxEx.tweenEx.manager)
                e.lastStand = 60
            }
            @Override void onDying(Entity e) {}
            @Override void onDead(Entity e) {}
        }

        entity.addScheme(new EntityScheme() {
            @Override
            void setupScheme(Entity e) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            void updateScheme(Entity e) {
                //To change body of implemented methods use File | Settings | File Templates.
                e.drawingDegrees = e.velDegrees

                if (!GdxEx.cameraEx.getBounds().contains(e.pos.x, e.pos.y)) {
                    e.hp = 0
                }
            }
        })

        if (kwargs?.scheme) {
            entity.addScheme(kwargs.scheme as EntityScheme)
        }

        entity
    }

    final static void aimingTo(Entity bullet, Vector2 target, int term) {
        this.aimingTo(bullet, {-> target }, term)
    }

    final static void aimingTo(Entity bullet, Closure target, int term) {
        TweenEx.callWithRepetition(term, 0) { int type, BaseTween tween ->
            def angle = (target() as Vector2).cpy().sub(bullet.pos).angle()
            def d1 = (angle - bullet.velDegrees).abs()
            def d2 = (angle + 360 - bullet.velDegrees).abs()
            def d3 = (angle - 360 - bullet.velDegrees).abs()

            if (d1 > d2) {
                angle += 359.9f
            } else if (d1 > d3) {
                angle -= 359.9f
            }
            def now = tween.step / 2
            bullet.velDegrees += (angle - bullet.velDegrees) / (term+1 - now) as float
        }.start(GdxEx.tweenEx.manager)
    }
}
