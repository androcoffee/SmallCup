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
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import groovy.transform.CompileStatic
import net.hapiizland.net.hapiizland.gdxex.BlockInfo
import net.hapiizland.net.hapiizland.gdxex.Entity
import net.hapiizland.net.hapiizland.gdxex.EntityScheme
import net.hapiizland.net.hapiizland.gdxex.FontDrawingScheme
import net.hapiizland.net.hapiizland.gdxex.GdxEx
import net.hapiizland.net.hapiizland.gdxex.LivingScheme
import net.hapiizland.net.hapiizland.gdxex.PhysicalMovingScheme
import net.hapiizland.net.hapiizland.gdxex.PhysicsEx
import net.hapiizland.net.hapiizland.gdxex.SpriteDrawingScheme
import net.hapiizland.net.hapiizland.gdxex.VAMovingScheme
import net.hapiizland.net.hapiizland.gdxex.Vector2Ex

import net.hapiizland.smallcup.entities.Bullet
import org.jbox2d.collision.shapes.CircleShape
import org.jbox2d.collision.shapes.PolygonShape
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.Body
import org.jbox2d.dynamics.BodyDef
import org.jbox2d.dynamics.BodyType
import org.jbox2d.dynamics.FixtureDef
import org.jbox2d.dynamics.contacts.Contact
import org.jbox2d.dynamics.contacts.ContactEdge
import org.jbox2d.dynamics.joints.Joint
import org.jbox2d.dynamics.joints.WeldJoint
import org.jbox2d.dynamics.joints.WeldJointDef

@CompileStatic
class Playing implements Screen {
    Playing() {
    }

    @Override
    void show() {
        GdxEx.stageEx.loadStage("home.tmx")

        def player = Player.createPlayer(new Vector2(200, 250))
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


@CompileStatic
class Player {
    static Entity createPlayer(Vector2 pos) {
        Texture texture = GdxEx.graphicsEx.getTexture("player.png")
        Entity player = new Entity(pos, Attributes.PLAYER)

        player.livingScheme = new LivingScheme(10) {
            @Override
            void onKilled(Entity e) {
                player.lastStand = 60
                Tween.to(player, Entity.SCALE, 1.0f).target(0.0f).start(GdxEx.tweenEx.manager)
            }

            @Override void onDying(Entity e) {}
            @Override void onDead(Entity e) {}
        }

        def drawingScheme = new SpriteDrawingScheme(new Sprite(texture, 0, 0, 32, 32))
        player.drawingScheme = drawingScheme
        player.z = -0.1f

        def afterGrow = DigitAfterGrowDrawingScheme.createInstance(
                scheme: drawingScheme, num: 5, period: 10, alphaScaling: true, sizeScaling: false)
        player.addSubDrawingScheme(afterGrow)

        BodyDef bodyDef = new BodyDef()
        bodyDef.position.set(PhysicsEx.p2m(pos))
        bodyDef.type = BodyType.DYNAMIC
        bodyDef.angularDamping = 0.5f
        //bodyDef.fixedRotation = true

        Body body = GdxEx.physicsEx.createBody(bodyDef)
        FixtureDef fixtureDef = new FixtureDef()
        CircleShape shape = new CircleShape()
        shape.m_radius = PhysicsEx.p2m(28.0f / 2)
        //shape.setAsBox(PhysicsEx.p2m(8.0f / 2), PhysicsEx.p2m(8.0f / 2))
        fixtureDef.shape = shape
        fixtureDef.density = 1.0f
        fixtureDef.friction = 1.0f
        body.createFixture(fixtureDef)

        player.movingScheme = new PhysicalMovingScheme(body)

        player.addScheme(new EntityScheme() {
            @Override
            void setupScheme(Entity e) {
            }

            @Override
            void updateScheme(Entity e) {
                for (ContactEdge contactEdge = e.body.getContactList(); contactEdge; contactEdge = contactEdge.next) {
                    Contact contact = contactEdge.contact

                    if (contact.touching) {
                        e.drawingDegrees = PhysicsEx.m2p(contact.manifold.localNormal).angle() - 90.0f
                        break
                    }
                }

                boolean goLeft = GdxEx.inputEx.isKeyPressed(Input.Keys.LEFT)
                boolean goRight = GdxEx.inputEx.isKeyPressed(Input.Keys.RIGHT)
                boolean goDown = GdxEx.inputEx.isKeyPressed(Input.Keys.DOWN)

                float airForce = 130.0f
                float wallTorque = 2.0f
                float groundForce = 200.0f
                float groundTorque = 3.0f
                float jumpPower = 180.0f

                if (goRight) {
                    if (e.onGround) {
                        e.body.applyTorque(-groundTorque)
                        e.applyForce(new Vector2(groundForce, 0))
                    } else {
                        if (e.onRightWall) {
                            e.applyForce(new Vector2(airForce, 0))
                            e.body.applyTorque(-wallTorque)
                        }
                        e.applyForce(new Vector2(airForce, 0))
                    }
                } else if (goLeft) {
                    if (e.onGround) {
                        e.body.applyTorque(groundTorque)
                        e.applyForce(new Vector2(-groundForce, 0))
                    } else {
                        if (e.onLeftWall) {
                            e.applyForce(new Vector2(-airForce, 0))
                            e.body.applyTorque(wallTorque)
                        }
                        e.applyForce(new Vector2(-airForce, 0))
                    }
                } else if (goDown) {
                    if (e.onRightWall) {
                        e.body.applyTorque(wallTorque * 2.0f as float)
                        e.applyForce(new Vector2(airForce, 0))
                    } else if (e.onLeftWall) {
                        e.body.applyTorque(-wallTorque *  2.0f as float)
                        e.applyForce(new Vector2(-airForce, 0))
                    }
                }

                if (!goLeft && !goRight && e.onGround) {
                    e.body.linearDamping = 2.0f
                    e.body.angularDamping = 2.0f
                } else {
                    e.body.linearDamping = 0.7f
                    e.body.angularDamping = 0.7f
                }

                float dashPower = 50.0f
                if (GdxEx.inputEx.isKeyDown(Input.Keys.X)) {
                    if (e.onGround) {
                        e.applyImpulse(new Vector2(dashPower, 0))
                    }
                }

                if (GdxEx.inputEx.isKeyDown(Input.Keys.Z)) {
                    if (e.onGround) {
                        e.applyImpulse(new Vector2(0, jumpPower))
                    } else if (e.onRightWall) {
                        e.applyImpulse(new Vector2(-jumpPower / 2.0f as float, jumpPower * 1.732f / 2.0f as float))
                    } else if (e.onLeftWall) {
                        e.applyImpulse(new Vector2(jumpPower / 2.0f as float, jumpPower * 1.732f / 2.0f as float))
                    }
                }

                if (!e.onGround && !e.onRightWall && !e.onLeftWall && !e.onCeiling) {
                    e.body.angularVelocity = 0.0f
                }

                if (GdxEx.inputEx.isMouseDown(Input.Buttons.LEFT)) {
                    (0..<10).each { int i ->
                        float spd = 1.0f + i / 10.0f as float
                        def velDegrees =
                            Vector2Ex.calcDegrees(e.pos, GdxEx.cameraEx.calcPosOnWorld(GdxEx.inputEx.mousePos)) + MathUtils.random(-30, 30)
                        GdxEx.entityEx.addEntity(
                                Bullet.createBullet(e.pos, [
                                        speed: spd,
                                        velDegrees: velDegrees,
                                        color: new Color(0.2f, 0.8f, 0.7f, 1.0f),
                                        imageNo: 6,
                                        scheme: new EntityScheme() {
                                            @Override void setupScheme(Entity b) {
                                                //Tween.to(b, Entity.SPEED, 2.0f).target(0.0f).start(GdxEx.tweenEx.manager)
                                                //TweenEx.call { b.hp = 0 }.delay(1.0f as float).start(GdxEx.tweenEx.manager)
                                                //Bullet.aimingTo(b, {-> GdxEx.inputEx.mousePosOnWorld }, 100)
                                                //Tween.to(b, Entity.SPEED, 2.0f).target(i * 2).start(GdxEx.tweenEx.manager)
                                            }

                                            @Override void updateScheme(Entity b) {
                                            }
                                        }
                                ]))
                    }
                }

                Vector2 cameraTarget = GdxEx.stageEx.limitCamera(e.pos)
                GdxEx.cameraEx.camera.position.x = cameraTarget.x
                GdxEx.cameraEx.camera.position.y = cameraTarget.y
            }

            private float keyToDegrees() {
                Vector2 v = new Vector2(0, 0)

                if (GdxEx.inputEx.isKeyPressed(Input.Keys.RIGHT)) {
                    v.x = 1.0f
                } else if (GdxEx.inputEx.isKeyPressed(Input.Keys.LEFT)) {
                    v.x = -1.0f
                } else {
                    v.y = 1.0f
                }

                if (GdxEx.inputEx.isKeyPressed(Input.Keys.UP)) {
                    v.y = 1.0f
                } else if (GdxEx.inputEx.isKeyPressed(Input.Keys.DOWN)) {
                    v.y = -1.0f
                }

                v.angle()
            }
        })

        player
    }

    static Entity createPlayerInfo(Entity player) {
        def playerInfo = new Entity(new Vector2(0, Gdx.graphics.height), Attributes.NO_HIT)

        def fontDrawingScheme = new FontDrawingScheme(GdxEx.graphicsEx.getFont("mplus-2c-regular.fnt"))
        fontDrawingScheme.text = "pos = ${(int)player.posX}, ${(int)player.posY}"
        fontDrawingScheme.setColor(new Color(1.0f, 0.0f, 1.0f, 1.0f))
        playerInfo.drawingScheme = fontDrawingScheme
        playerInfo.followingCamera = true

        playerInfo.addScheme(new EntityScheme() {
            @Override
            void setupScheme(Entity e) {
            }

            @Override
            void updateScheme(Entity e) {
                fontDrawingScheme.text = "pos = ${(int)player.posX}, ${(int)player.posY}\n" +
                        "G = $player.onGround"
            }
        })

        playerInfo
    }
}

@CompileStatic
class DebugInfo {
    static Entity createDebugInfo() {
        def entity = new Entity(new Vector2(0, Gdx.graphics.height - 64), Attributes.NO_HIT)

        def fontDrawingScheme = new FontDrawingScheme(GdxEx.graphicsEx.getFont("mplus-2c-regular.fnt"))
        entity.drawingScheme = fontDrawingScheme
        entity.followingCamera = true
        entity.z = 1.0f

        entity.addScheme(new EntityScheme() {
            @Override
            void setupScheme(Entity e) {
            }

            @Override
            void updateScheme(Entity e) {
                def mouseWorldPos = GdxEx.cameraEx.calcPosOnWorld(GdxEx.inputEx.mousePos)

                fontDrawingScheme.text = "FPS = ${GdxEx.director.fps as int}\n" +
                        "Entities = ${GdxEx.entityEx.entities.size()}\n" +
                        "MousePos = ${GdxEx.inputEx.mousePos.x as int}, ${GdxEx.inputEx.mousePos.y as int}\n" +
                        "MousePos(World) = ${mouseWorldPos.x as int}, ${mouseWorldPos.y as int}\n" +
                        "CameraPos = ${GdxEx.cameraEx.pos.x as int}, ${GdxEx.cameraEx.pos.y as int}\n" +
                        "Bodies = ${GdxEx.physicsEx.bodyCount}"
            }
        })

        entity
    }
}
