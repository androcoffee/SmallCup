/**
 * Created with IntelliJ IDEA.
 * User: tetsuya
 * Date: 13/03/03
 * Time: 1:56
 * To change this template use File | Settings | File Templates.
 */

package net.hapiizland.net.hapiizland.gdxex

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import groovy.transform.CompileStatic
import org.jbox2d.collision.shapes.PolygonShape
import org.jbox2d.collision.shapes.Shape
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.World

@CompileStatic
class PhysicsEx {
    @Delegate World world
    private void setWorld(World world) {
        this.world = world
    }

    PhysicsEx(IPhycsScheme scheme) {
        world = new World(PhysicsEx.p2m(scheme.gravity), true)
        velosityIterations = scheme.velocityIterations
        positionIterations = scheme.positionIterations
        m2pRate = scheme.m2pRate
    }

    void update() {
        updatePhysicalEntities()
        world.step(GdxEx.director.spf60, velosityIterations, positionIterations)
    }

    void dispose() {

    }

    private void updatePhysicalEntities() {
        GdxEx.entityEx.entities.each { Entity e ->
            if (e.hasMovingScheme) {
                e.updateOnGround(false)
                e.updateOnCeiling(false)
                e.updateOnRightWall(false)
                e.updateOnLeftWall(false)
            }
        }
    }

    private int velosityIterations = 6
    private int positionIterations = 2

    final static float m2p(int m) { m * m2pRate }
    final static float m2p(float m) { m * m2pRate }
    final static float m2p(double m) { m * m2pRate }
    final static Vector2 m2p(Vec2 m) {
        new Vector2(m2p(m.x), m2p(m.y))
    }

    final static float p2m(int p) { p / m2pRate }
    final static float p2m(float p) { p / m2pRate }
    final static float p2m(double p) { p / m2pRate }
    final static Vec2 p2m(Vector2 p) {
        new Vec2(p2m(p.x), p2m(p.y))
    }

    static float m2pRate = 32.0f
    private static void setM2pRate(float rate) { m2pRate = rate }

    final static Shape createHalfCircleShape(float radius, float startDegrees, float width) {
        int intWidth = width as int
        int count = intWidth.intdiv(10) + 1
        float mod = width - (count-1) * 10

        Vec2[] vertices = (0..<count).collect { int i ->
            float rad = MathUtils.degRad * i * 10

            if (i == count-1) {
                rad = MathUtils.degRad * ((i-1) * 10 + mod)
            }

            new Vec2(Math.cos(rad) as float, Math.sin(rad) as float)
        } as Vec2[]

        PolygonShape shape = new PolygonShape()
        shape.set(vertices, vertices.size())

        shape
    }
}

interface IPhycsScheme {
    Vector2 getGravity()
    int getVelocityIterations()
    int getPositionIterations()
    float getM2pRate()
}