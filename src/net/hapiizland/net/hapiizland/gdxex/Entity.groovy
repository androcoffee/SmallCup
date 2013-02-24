/**
 * Created with IntelliJ IDEA.
 * User: tetsuya
 * Date: 13/02/19
 * Time: 22:42
 * To change this template use File | Settings | File Templates.
 */

package net.hapiizland.net.hapiizland.gdxex

import aurelienribon.tweenengine.TweenAccessor
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import groovy.transform.CompileStatic

interface IEntity {
    Vector2 getPos()
    void setPos(Vector2 pos)

    Vector2 getVel()
    void setVel(Vector2 vel)

    Vector2 getAcc()
    void setAcc(Vector2 acc)

    float getSpeed()
    void setSpeed(float speed)

    float getVelDegrees()
    void setVelDegrees(float velDegrees)
}

@CompileStatic
interface ILivingScheme {
    int getHp()
    void setHp(int hp)
}

@CompileStatic
interface IMovingScheme {
    Vector2 getVel()
    void setVel(Vector2 vel)

    Vector2 getAcc()
    void setAcc(Vector2 acc)

    float getSpeed()
    void setSpeed(float speed)

    float getVelDegrees()
    void setVelDegrees(float degrees)

    void setupMovingScheme(Entity e)
    void updateMovingScheme(Entity e)
}

@CompileStatic
interface IDrawingScheme {
    boolean getVisible()
    void setVisible(boolean visible)

    Vector2 getDrawingPos()
    void setDrawingPos(Vector2 pos)
    float getScaleX()
    float getScaleY()
    void setScale(float scaleX, float scaleY)
    void setScale(float scale)
    Color getColor()
    void setColor(Color color)
    float getDegrees()
    void setDegrees(float degrees)

    void setupDrawingScheme(Entity e)
    void updateDrawingScheme(Entity e)
    void drawDrawingScheme(Entity e)

    IDrawingScheme cpy()
}

@CompileStatic
class Entity implements IEntity {
    boolean done = false

    Vector2 pos = new Vector2()
    void setPos(Vector2 p) { pos.set(p) }

    @Delegate ILivingScheme livingScheme = null

    @Delegate IMovingScheme movingScheme = null
    void setMovingScheme(IMovingScheme movingScheme) {
        this.movingScheme = movingScheme
        this.movingScheme.setupMovingScheme(this)
    }

    @Delegate IDrawingScheme drawingScheme = null
    void setDrawingScheme(IDrawingScheme drawingScheme) {
        this.drawingScheme = drawingScheme
        this.drawingScheme.setupDrawingScheme(this)
    }

    Entity() {
    }

    final void addScheme(EntityScheme scheme) {
        schemes << scheme
        scheme.setupScheme(this)
    }

    final void addSubDrawingScheme(IDrawingScheme scheme) {
        subDrawingSchemes << scheme
        scheme.setupDrawingScheme(this)
    }

    final void update() {
        schemes.each { EntityScheme s -> s.updateScheme(this) }
        if (movingScheme) updateMovingScheme(this)
        if (drawingScheme) updateDrawingScheme(this)
        subDrawingSchemes.each { IDrawingScheme s -> s.updateDrawingScheme(this) }
    }

    final void draw() {
        subDrawingSchemes.each { IDrawingScheme s -> if (this.visible) s.drawDrawingScheme(this) }
        if (drawingScheme && this.visible) drawDrawingScheme(this)
    }

    private List<EntityScheme> schemes = []
    private List<IDrawingScheme> subDrawingSchemes = []

    final static int X = 0, Y = 1, XY = 2, SPEED = 3, VELDEGREES = 4, SPD_VDEG = 5, SCALE = 6, SCALE_XY = 7,
            COLOR_RGB = 8, COLOR_RGBA = 9, ALPHA = 10
}


@CompileStatic
class LivingScheme implements ILivingScheme {
    int hp = 1
    int lastStand = -1

    void setOnKilled(Closure c) {
        onKilled = c
    }

    void setOnDying(Closure c) {
        onDying = c
    }

    LivingScheme(int hp) {
        this.hp = hp
    }

    void setupLivingScheme(Entity e) {
    }

    void updateLivingScheme(Entity e) {
        if (hp <= 0 && lastStand == -1) {
            lastStand = 0;
            if (onKilled) onKilled();
        }

        if (lastStand > 0) {
            lastStand--;
            if (onDying) onDying();
        } else if (lastStand == 0) {
            e.done = true
        }
    }

    private Closure onKilled = null
    private Closure onDying = null
}

@CompileStatic
class VAMovingScheme implements IMovingScheme {
    Vector2 vel = new Vector2(0, 0)
    void setVel(Vector2 v) { vel.set(v) }

    Vector2 acc = new Vector2()
    void setAcc(Vector2 a) { acc.set(a) }

    float getSpeed() { vel.len() }
    void setSpeed(float speed) {
        if (this.speed == 0) {
            vel.set(speed, 0)
            vel.rotate(prevVelDegrees)
        } else {
            def newV = vel.nor().mul(speed)
            vel.x = newV.x
            vel.y = newV.y
        }
    }

    float getVelDegrees() {
        if (speed == 0) {
            prevVelDegrees
        } else {
            vel.angle()
        }
    }

    void setVelDegrees(float degrees) {
        vel.angle = degrees
        prevVelDegrees = degrees
    }

    void setupMovingScheme(Entity e) {

    }

    void updateMovingScheme(Entity e) {
        vel.add(acc)
        e.pos.add(vel)
        acc.set(0, 0)
    }

    private float prevVelDegrees = 0
}

@CompileStatic
abstract class DrawingScheme implements IDrawingScheme {
    boolean visible = true

    abstract void onSetupDrawingScheme(Entity e)
    abstract void onUpdateDrawingScheme(Entity e)
    abstract void onDrawDrawingScheme(Entity e)
    abstract IDrawingScheme createCopiedScheme()

    final void setupDrawingScheme(Entity e) {
        setDrawingPos(e.pos)
        onSetupDrawingScheme(e)
    }

    final void updateDrawingScheme(Entity e) {
        setDrawingPos(e.pos)
        onUpdateDrawingScheme(e)
    }

    final void drawDrawingScheme(Entity e) {
        onDrawDrawingScheme(e)
    }

    final IDrawingScheme cpy() {
        def scheme = createCopiedScheme()
        scheme.visible = visible
        scheme.drawingPos = new Vector2(this.drawingPos)
        scheme.setScale(this.scaleX, this.scaleY)

        scheme
    }
}

@CompileStatic
class SpriteDrawingScheme extends DrawingScheme {
    @Delegate Sprite sprite

    SpriteDrawingScheme(Sprite sprite) {
        this.sprite = sprite
    }

    Vector2 getDrawingPos() {
        new Vector2(sprite.x, sprite.y)
    }

    void setDrawingPos(Vector2 pos) {
        sprite.setPosition(pos.x, pos.y)
    }

    float getDegrees() {
        sprite.rotation
    }

    void setDegrees(float degrees) {
        sprite.rotation = degrees
    }

    void onSetupDrawingScheme(Entity e) {
        sprite.setPosition(e.pos.x, e.pos.y)
    }

    void onUpdateDrawingScheme(Entity e) {
    }

    void onDrawDrawingScheme(Entity e) {
        sprite.draw(GdxEx.graphicsEx.batch)
    }

    IDrawingScheme createCopiedScheme() {
        def sprite = new Sprite(this.sprite)
        def spriteDrawingScheme = new SpriteDrawingScheme(sprite)
        
        spriteDrawingScheme
    }
}

@CompileStatic
class FontDrawingScheme extends DrawingScheme {
    @Delegate BitmapFont font
    String text = ""
    Vector2 drawingPos = new Vector2()

    FontDrawingScheme(BitmapFont font) {
        this.font = font
    }

    void onSetupDrawingScheme(Entity e) {
    }

    void onUpdateDrawingScheme(Entity e) {
    }

    void onDrawDrawingScheme(Entity e) {
        def lines = text.split("\n")

        lines.eachWithIndex { String line, int i ->
            font.draw(GdxEx.graphicsEx.batch, line, drawingPos.x, drawingPos.y - font.lineHeight * i)
        }
    }

    IDrawingScheme createCopiedScheme() {
        def font = new BitmapFont(font.data, font.region, false)
        def fontDrawingScheme = new FontDrawingScheme(font)
        fontDrawingScheme.text = text

        fontDrawingScheme
    }

    float getDegrees() { 0.0f }
    void setDegrees(float degrees) {}
}

@CompileStatic
interface EntityScheme {
    void setupScheme(Entity e)
    void updateScheme(Entity e)
}

@CompileStatic
class EntityTweener implements TweenAccessor<Entity> {
    @Override
    int getValues(Entity target, int tweenType, float[] returnValues) {
        switch (tweenType) {
            case Entity.X:
                returnValues[0] = target.pos.x
                return 1
            case Entity.Y:
                returnValues[0] = target.pos.y
                return 1
            case Entity.XY:
                returnValues[0] = target.pos.x
                returnValues[1] = target.pos.y
                return 2
            case Entity.SPEED:
                returnValues[0] = target.speed
                return 1
            case Entity.VELDEGREES:
                returnValues[0] = target.velDegrees
                return 1
            case Entity.SPD_VDEG:
                returnValues[0] = target.speed
                returnValues[1] = target.velDegrees
                return 2
            case Entity.SCALE:
                returnValues[0] = target.scaleX
                return 1
            case Entity.SCALE_XY:
                returnValues[0] = target.scaleX
                returnValues[1] = target.scaleY
                return 2
            case Entity.COLOR_RGB:
                returnValues[0] = target.color.r
                returnValues[1] = target.color.g
                returnValues[2] = target.color.b
                return 3
            case Entity.COLOR_RGBA:
                returnValues[0] = target.color.r
                returnValues[1] = target.color.g
                returnValues[2] = target.color.b
                returnValues[3] = target.color.a
                return 4
            case Entity.ALPHA:
                returnValues[0] = target.color.a
                return 1
            default:
                return 0
        }
    }

    @Override
    void setValues(Entity target, int tweenType, float[] newValues) {
        switch (tweenType) {
            case Entity.X:
                target.pos.x = newValues[0]
                break
            case Entity.Y:
                target.pos.y = newValues[0]
                break
            case Entity.XY:
                target.pos.x = newValues[0]
                target.pos.y = newValues[1]
                break
            case Entity.SPEED:
                target.speed = newValues[0]
                break
            case Entity.VELDEGREES:
                target.velDegrees = newValues[0]
                break
            case Entity.SPD_VDEG:
                target.speed = newValues[0]
                target.velDegrees = newValues[1]
                break
            case Entity.SCALE:
                target.scale = newValues[0]
                break
            case Entity.SCALE_XY:
                target.setScale(newValues[0], newValues[1])
                break
            case Entity.COLOR_RGB:
                target.color = new Color(newValues[0], newValues[1], newValues[2], target.color.a)
                break
            case Entity.COLOR_RGBA:
                target.color = new Color(newValues[0], newValues[1], newValues[2], newValues[3])
                break
            case Entity.ALPHA:
                target.color = new Color(target.color.r, target.color.g, target.color.b, newValues[0])
                break
        }
    }
}