/**
 * Created with IntelliJ IDEA.
 * User: tetsuya
 * Date: 13/02/18
 * Time: 22:59
 * To change this template use File | Settings | File Templates.
 */

package net.hapiizland.smallcup

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Manifold
import groovy.transform.CompileStatic
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import net.hapiizland.net.hapiizland.gdxex.Director
import net.hapiizland.net.hapiizland.gdxex.DrawingScheme
import net.hapiizland.net.hapiizland.gdxex.Entity
import net.hapiizland.net.hapiizland.gdxex.GdxEx
import net.hapiizland.net.hapiizland.gdxex.IDirectorScheme
import net.hapiizland.net.hapiizland.gdxex.IGraphicsScheme
import net.hapiizland.net.hapiizland.gdxex.IPhycsScheme
import net.hapiizland.net.hapiizland.gdxex.IStageScheme
import net.hapiizland.net.hapiizland.gdxex.SpriteDrawingScheme
import net.hapiizland.net.hapiizland.gdxex.Stage

@CompileStatic
class DirectorScheme implements IDirectorScheme {
    void initialize(Director d) {
        GdxEx.graphicsEx.loadFont("mplus-2c-regular.fnt")

        ["player.png", "bullet.png"].each { String name ->
            GdxEx.graphicsEx.loadTexture(name)
        }

        d.registerScreen("Opening", new Opening())
        d.registerScreen("Playing", new Playing())

        d.switchScreen("Opening")
    }

    IGraphicsScheme createGraphicsScheme() {
        new IGraphicsScheme() {
            @Override String getTexturePath() { "data/images/" }
            @Override String getFontPath() { "data/fonts/" }
        }
    }

    IStageScheme createStageScheme() {
        new StageScheme()
    }

    IPhycsScheme createPhysicsScheme() {
        new IPhycsScheme() {
            @Override Vector2 getGravity() { new Vector2(0, -300.0f) }
            @Override int getVelocityIterations() { 6 }
            @Override int getPositionIterations() { 2 }
            @Override float getM2pRate() { 32.0f }
            @Override ContactListener createContactListener() {
                new ContactListener() {
                    @Override
                    void beginContact(Contact contact) {
                    }

                    @Override
                    void endContact(Contact contact) {
                    }

                    @Override void preSolve(Contact contact, Manifold oldManifold) {
                        Entity entityA = contact.fixtureA.body.userData as Entity
                        Entity entityB = contact.fixtureB.body.userData as Entity
                        def normal = contact.worldManifold.normal //normal vector is from A to B

                        if (entityA && entityA.class == Entity) {
                            if (normal.y >= 0.5f) entityA.updateOnCeiling(true)
                            if (normal.y <= -0.5f) entityA.updateOnGround(true)
                            if (normal.x >= 0.5f) entityA.updateOnRightWall(true)
                            if (normal.x <= -0.5) entityA.updateOnLeftWall(true)
                        }
                        if (entityB && entityB.class == Entity) {
                            /*if (entityB.hasMovingScheme) {
                                entityB.updateOnGround(false)
                                entityB.updateOnCeiling(false)
                                entityB.updateOnRightWall(false)
                                entityB.updateOnLeftWall(false)
                            }*/
                            if (normal.y >= 0.5f) entityB.updateOnGround(true)
                            if (normal.y <= -0.5f) entityB.updateOnCeiling(true)
                            if (normal.x >= 0.5f) entityB.updateOnLeftWall(true)
                            if (normal.x <= -0.5) entityB.updateOnRightWall(true)
                        }
                    }
                    @Override void postSolve(Contact contact, ContactImpulse impulse) {
                    }
                }
            }
        }
    }
}

@CompileStatic
class StageScheme implements IStageScheme {
    Entity createEntity(Stage stage, Map<String, Object> entity) {
        switch (entity.name) {
            case "Block":
                break
            case "Walker":
                println("walker")
                break
        }
        null
    }

    void createGraphics(Stage stage) {
        def cols = stage.cols
        def rows = stage.rows
        def layer = stage.getLayer("Blocks")
        def chipSize = GdxEx.stageEx.chipSize

        (0..<rows).each { int y ->
            (0..<cols).each { int x ->
                if (layer[y][x]) {
                    def texture = GdxEx.graphicsEx.getTexture("player.png")

                    def entity = new Entity(new Vector2(x * chipSize, y * chipSize), Attributes.BLOCKS)
                    entity.drawingScheme = new SpriteDrawingScheme(new Sprite(texture, 0, 0, 32, 32))
                    entity.drawingAlignment = DrawingScheme.LEFT_BOTTOM
                    entity.scale = 2.0f

                    GdxEx.entityEx.addEntity(entity)
                }
            }
        }
    }

    String getBlockLayer() { "Blocks" }
    String getStagePath() { "data/stages/" }
    int getChipSize() { 64 }
}

class Attributes {
    final static int NO_HIT = 1, PLAYER = 2, ENEMY = 4, SHOT = 8, BULLET = 16, BLOCKS = 32
}

@CompileStatic
class Main {
    static void main(String[] args) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration()
        config.title = "SmallCup Sample v0.1"
        config.useGL20 = true
        config.width = 800
        config.height = 600
        config.resizable = false
        //config.fullscreen = true
        new LwjglApplication(new Director(new DirectorScheme()), config)
    }
}
