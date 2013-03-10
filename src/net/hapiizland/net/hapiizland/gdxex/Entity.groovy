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
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.Body
import org.jbox2d.dynamics.Fixture
import org.jbox2d.dynamics.contacts.ContactEdge

interface IEntity {
    Vector2 getPos()
    void setPos(Vector2 pos)
}

@CompileStatic
interface ILivingScheme {
    int getHp()
    void setHp(int hp)

    void setLastStand(int lastStand)

    void setupLivingScheme(Entity e)
    void updateLivingScheme(Entity e)
}

@CompileStatic
interface IMovingScheme {
    void updatePos(Vector2 pos)

    Vector2 getVel()
    void setVel(Vector2 vel)
    float getVelX()
    void setVelX(float vx)
    float getVelY()
    void setVelY(float vy)

    void applyForce(Vector2 force)
    void applyImpulse(Vector2 impulse)

    float getSpeed()
    void setSpeed(float speed)

    float getVelDegrees()
    void setVelDegrees(float degrees)

    boolean getOnGround()
    void updateOnGround(boolean v)
    boolean getOnCeiling()
    void updateOnCeiling(boolean v)
    boolean getOnRightWall()
    void updateOnRightWall(boolean v)
    boolean getOnLeftWall()
    void updateOnLeftWall(boolean v)

    Body getBody()

    void setupMovingScheme(Entity e)
    void updateMovingScheme(Entity e)
}

@CompileStatic
interface IDrawingScheme {
    boolean isFollowingCamera()
    boolean getFollowingCamera()
    void setFollowingCamera(boolean followingCamera)

    boolean isVisible()
    boolean getVisible()
    void setVisible(boolean visible)

    int getDrawingAlignment()
    void setDrawingAlignment(int alignment)

    Vector2 getDrawingPos()
    void setDrawingPos(Vector2 pos)

    float getZ()
    void setZ(float z)

    float getScaleX()
    float getScaleY()
    void setScale(float scaleX, float scaleY)
    void setScale(float scale)
    Color getColor()
    void setColor(Color color)
    float getDrawingDegrees()
    void setDrawingDegrees(float degrees)

    void setupDrawingScheme(Entity e)
    void updateDrawingScheme(Entity e)
    void drawDrawingScheme(Entity e)

    IDrawingScheme cpy()
}

@CompileStatic
class Entity implements IEntity {
    boolean done = false
    int attribute = 0

    Vector2 pos = new Vector2()
    void setPos(Vector2 p) {
        pos.set(p)
        if (movingScheme) movingScheme.updatePos(p)
    }

    float getPosX() { pos.x }
    void setPosX(float x) {
        pos.x = x
        if (movingScheme) movingScheme.updatePos(pos)
    }

    float getPosY() { pos.y }
    void setPosY(float y) {
        pos.y = y
        if (movingScheme) movingScheme.updatePos(pos)
    }

    @Delegate private ILivingScheme livingScheme = null
    void setLivingScheme(ILivingScheme livingScheme) {
        this.livingScheme = livingScheme
        this.livingScheme.setupLivingScheme(this)
    }
    boolean getHasLivingScheme() { livingScheme ? true : false }

    @Delegate private IMovingScheme movingScheme = null
    void setMovingScheme(IMovingScheme movingScheme) {
        this.movingScheme = movingScheme
        this.movingScheme.setupMovingScheme(this)
    }
    boolean getHasMovingScheme() { movingScheme ? true : false }

    @Delegate private IDrawingScheme drawingScheme = null
    void setDrawingScheme(IDrawingScheme drawingScheme) {
        this.drawingScheme = drawingScheme
        this.drawingScheme.setupDrawingScheme(this)
    }
    boolean getHasDrawingScheme() { drawingScheme ? true : false }

    Entity(Vector2 pos, int attribute) {
        this.pos.set(pos)
        this.attribute = attribute
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
        if (livingScheme) updateLivingScheme(this)
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
abstract class LivingScheme implements ILivingScheme {
    int hp = 1
    int lastStand = -1

    abstract void onKilled(Entity e)
    abstract void onDying(Entity e)
    abstract void onDead(Entity e)

    LivingScheme(int hp) {
        this.hp = hp
    }

    void setupLivingScheme(Entity e) {
    }

    void updateLivingScheme(Entity e) {
        if (hp <= 0 && lastStand == -1) {
            lastStand = 0;
            onKilled(e);
        }

        if (lastStand > 0) {
            lastStand--;
            onDying(e);
        } else if (lastStand == 0) {
            onDead(e)
            e.done = true
        }
    }
}

@CompileStatic
class VAMovingScheme implements IMovingScheme {
    void updatePos(Vector2 pos) {}

    Vector2 vel = new Vector2(0, 0)
    void setVel(Vector2 v) { vel.set(v) }
    float getVelX() { vel.x }
    void setVelX(float vx) { vel.x = vx }
    float getVelY() { vel.y }
    void setVelY(float vy) { vel.y = vy }

    void applyForce(Vector2 force) {
        acc.add(force.div(GdxEx.director.spf60))
    }
    void applyImpulse(Vector2 impulse) {
        acc.add(impulse)
    }

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

    boolean getOnGround() { false }
    void updateOnGround(boolean v) {}
    boolean getOnCeiling() { false }
    void updateOnCeiling(boolean v) {}
    boolean getOnLeftWall() { false }
    void updateOnLeftWall(boolean v) {}
    boolean getOnRightWall() { false }
    void updateOnRightWall(boolean v) {}

    Body getBody() { null }

    void setupMovingScheme(Entity e) {

    }

    void updateMovingScheme(Entity e) {
        vel.add(acc)
        e.pos.add(vel)
        acc.set(0, 0)
    }

    private Vector2 acc = new Vector2()
    private float prevVelDegrees = 0
}

@CompileStatic
class PhysicalMovingScheme implements IMovingScheme {
    Body body

    boolean getOnGround() { onGround }
    void updateOnGround(boolean v) { onGround = v }
    boolean getOnCeiling() { onCeiling }
    void updateOnCeiling(boolean v) { onCeiling = v }
    boolean getOnRightWall() { onRightWall }
    void updateOnRightWall(boolean v) { onRightWall = v }
    boolean getOnLeftWall() { onLeftWall }
    void updateOnLeftWall(boolean v) { onLeftWall = v }

    PhysicalMovingScheme(Body body) {
        this.body = body
    }

    void updatePos(Vector2 pos) {
        body.position.set(PhysicsEx.p2m(pos))
    }

    Vector2 getVel() { PhysicsEx.m2p(body.linearVelocity) }
    void setVel(Vector2 v) { body.setLinearVelocity(PhysicsEx.p2m(v)) }

    float getVelX() { PhysicsEx.m2p(body.linearVelocity.x) }
    void setVelX(float vx) {
        def vy = body.linearVelocity.y
        body.setLinearVelocity(new Vec2(PhysicsEx.p2m(vx), vy))
    }

    float getVelY() { body.linearVelocity.y }
    void setVelY(float vy) {
        def vx = body.linearVelocity.x
        body.setLinearVelocity(new Vec2(vx, PhysicsEx.p2m(vy)))
    }

    void applyForce(Vector2 force) {
        body.applyForce(PhysicsEx.p2m(force), body.position)
    }

    void applyImpulse(Vector2 impulse) {
        body.applyLinearImpulse(PhysicsEx.p2m(impulse), body.position)
    }

    float getSpeed() { vel.len() }
    void setSpeed(float spd) {
        if (this.speed == 0) {
            def newV = new Vector2(spd, 0)
            newV.rotate(prevVelDegrees)
            vel = newV
        } else {
            def newV = vel.nor().mul(spd)
            vel = newV
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
        def v = this.vel
        v.angle = degrees
        this.vel = v
        prevVelDegrees = degrees
    }

    Body getBody() { body }

    void setupMovingScheme(Entity e) {
        body.userData = e
        body.position.set(PhysicsEx.p2m(e.pos))
        body.fixtureList.each { Fixture f ->
            f.filterData.categoryBits = e.attribute as short
        }
    }

    void updateMovingScheme(Entity e) {
        e.pos = PhysicsEx.m2p(body.position)
    }

    private float prevVelDegrees = 0
    private boolean onGround = false
    private boolean onCeiling = false
    private boolean onRightWall = false
    private boolean onLeftWall = false
}

@CompileStatic
abstract class DrawingScheme implements IDrawingScheme {
    boolean visible = true
    boolean followingCamera = false
    float z = 0.0f
    int drawingAlignment = CENTER

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

    static final int LEFT_BOTTOM = 0, CENTER = 1
}

@CompileStatic
class SpriteDrawingScheme extends DrawingScheme {
    @Delegate Sprite sprite

    SpriteDrawingScheme(Sprite sprite) {
        this.sprite = sprite
    }

    Vector2 getDrawingPos() {
        def offset = alignmentOffset
        new Vector2(sprite.x + offset.x, sprite.y + offset.y)
    }

    void setDrawingPos(Vector2 pos) {
        def offset = alignmentOffset
        sprite.setPosition(pos.x - offset.x, pos.y - offset.y)
    }

    private Vector2 getAlignmentOffset() {
        if (drawingAlignment == DrawingScheme.CENTER) {
            return new Vector2(sprite.width / 2.0f as float, sprite.height / 2.0f as float)
        } else {
            return new Vector2()
        }
    }

    float getDrawingDegrees() {
        sprite.rotation
    }

    void setDrawingDegrees(float degrees) {
        sprite.rotation = degrees
    }

    void onSetupDrawingScheme(Entity e) {
        drawingPos.set(e.pos)
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

    float getDrawingDegrees() { 0.0f }
    void setDrawingDegrees(float degrees) {}
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