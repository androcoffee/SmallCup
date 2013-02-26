/**
 * Created with IntelliJ IDEA.
 * User: tetsuya
 * Date: 13/02/25
 * Time: 21:10
 * To change this template use File | Settings | File Templates.
 */

package net.hapiizland.smallcup.entities

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import groovy.transform.CompileStatic
import net.hapiizland.net.hapiizland.gdxex.Entity
import net.hapiizland.net.hapiizland.gdxex.EntityScheme
import net.hapiizland.net.hapiizland.gdxex.GdxEx
import net.hapiizland.net.hapiizland.gdxex.SpriteDrawingScheme
import net.hapiizland.net.hapiizland.gdxex.VAMovingScheme
import net.hapiizland.smallcup.AfterGrowDrawingScheme
import net.hapiizland.smallcup.DigitAfterGrowDrawingScheme

@CompileStatic
class Bullet {
    static final Entity createBullet(Vector2 pos, Map kwargs) {
        def entity = new Entity()

        entity.pos = pos

        def imageNo = (kwargs?.imageNo ?: 0) as int
        def imgX = imageNo % 8 as int
        def imgY = imageNo.intdiv(8) as int
        def drawingScheme = new SpriteDrawingScheme(
                new Sprite(GdxEx.graphicsEx.getTexture("bullet.png"), imgX * 32, imgY * 32, 32, 32))
        entity.drawingScheme = drawingScheme
        entity.addSubDrawingScheme(
                DigitAfterGrowDrawingScheme.createInstance(scheme: drawingScheme, num: 5, period: 5, sizeScaling: true, alphaScaling: false))

        entity.movingScheme = new VAMovingScheme()
        entity.speed = (kwargs?.speed ?: 1.0f) as float
        entity.velDegrees = (kwargs?.velDegrees ?: 0) as float

        def color = (kwargs?.color ?: new Color(1.0f, 1.0f, 1.0f, 0.7f)) as Color
        color.a = 0.6f
        entity.color = color

        entity.addScheme(new EntityScheme() {
            @Override
            void setupScheme(Entity e) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
           @Override
            void updateScheme(Entity e) {
                //To change body of implemented methods use File | Settings | File Templates.
                e.drawingDegrees = e.velDegrees
            }
        })

        if (kwargs?.scheme) {
            entity.addScheme(kwargs.scheme as EntityScheme)
        }

        entity
    }
}
