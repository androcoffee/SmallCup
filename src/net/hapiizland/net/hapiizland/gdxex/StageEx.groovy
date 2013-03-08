/**
 * Created with IntelliJ IDEA.
 * User: AndroCofffee
 * Date: 13/02/28
 * Time: 1:39
 * To change this template use File | Settings | File Templates.
 */

package net.hapiizland.net.hapiizland.gdxex

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapRenderer
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer2
import com.badlogic.gdx.math.Vector2
import groovy.transform.CompileStatic
import org.jbox2d.collision.shapes.PolygonShape
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.Body
import org.jbox2d.dynamics.BodyDef
import org.jbox2d.dynamics.BodyType
import org.jbox2d.dynamics.FixtureDef

class StageEx {
    @Delegate TiledMap tiledMap
    private OrthogonalTiledMapRenderer2 renderer = null

    int getWidth() { tiledMap.properties["width"] }
    int getHeight() { tiledMap.properties["height"] }

    private void setStage(Stage stage) { this.stage = stage }

    IStageScheme stageScheme
    private void setStageScheme(IStageScheme scheme) {
        stageScheme = scheme
    }

    String getStagePath() {
        formatPath(stageScheme.stagePath)
    }

    int getChipSize() {
        stageScheme.chipSize
    }

    StageEx(IStageScheme scheme) {
        stageScheme = scheme
    }

    final void dispose() {
        if (renderer) renderer.dispose()
    }

    final TiledMap loadStage(String filename) {
        tiledMap = new TmxMapLoader().load(stagePath + filename)
        //atlas = new TileAtlas(tiledMap, Gdx.files.internal("data/images/tiles-packed/"))
        float rate = chipSize / tiledMap.properties["tilewidth"] as float
        renderer = new OrthogonalTiledMapRenderer2(tiledMap, rate)
        //stageScheme.createGraphics(stage)
        createPhysicalBlocks(stageScheme.blockLayer)
        //createEntities(tiledMap, stageScheme)

        return tiledMap
    }

    void draw() {
        if (renderer) {
            renderer.setView(GdxEx.cameraEx.camera)
            renderer.render()
        }
    }

    @CompileStatic
    final Vector2 limitCamera(Vector2 target) {
        TiledMapTileLayer layer = tiledMap.getLayers().getLayer(0) as TiledMapTileLayer
        Vector2 newTarget = new Vector2(target)
        float windowWidth = Gdx.graphics.width
        float windowHeight = Gdx.graphics.height
        Vector2 center = new Vector2(windowWidth / 2.0f as float, windowHeight / 2.0f as float)
        float stagePixelWidth = layer.width * chipSize
        float stagePixelHeight = layer.height * chipSize

        if (stagePixelWidth - chipSize * 2.0f < windowWidth) {
            newTarget.x = stagePixelWidth / 2.0f as float
        } else if (newTarget.x < center.x + chipSize) {
            newTarget.x = center.x + chipSize
        } else if (newTarget.x > stagePixelWidth - center.x - chipSize) {
            newTarget.x = stagePixelWidth - center.x - chipSize
        }

        if (stagePixelHeight - chipSize < windowHeight) {
            newTarget.y = stagePixelHeight / 2.0f as float
        } else if (newTarget.y < center.y + chipSize) {
            newTarget.y = center.y + chipSize
        } else if (newTarget.y > stagePixelHeight - center.y - chipSize) {
            newTarget.y = stagePixelHeight - center.y - chipSize
        }

        return newTarget
    }

    @CompileStatic
    private void createPhysicalBlocks(String name) {
        TiledMapTileLayer layer = tiledMap.getLayers().getLayer(0) as TiledMapTileLayer
        List<List<Integer>> cpy = (0..<layer.height).collect { int y ->
            (0..<layer.width).collect { int x ->
                TiledMapTileLayer.Cell cell = layer.getCell(x, y)
                if (cell) cell.tile.id
                else 0
            }.toList() as List<Integer>
        }.toList()

        (0..<layer.height).each { int y ->
            (0..<layer.width).each { int x ->
                switch (cpy[y][x]) {
                    case 0:
                        break
                    case 1:
                        createBlock(x, y, cpy)
                        break
                    case 2..15:
                        createSlope(x, y, cpy[y][x])
                        break
                    default:
                        break
                }
            }
        }
    }

    private void createBlock(int x, int y, List<List<Integer>> cpy) {
        int width = cpy[0].size()
        int height = cpy.size()
        int blockWidth = 1
        int blockHeight = 0
        boolean first = true

        while (true) {
            int localY = y + blockHeight
            int left = x

            int right = cpy[localY].findIndexOf(x) { int v -> v == 0 }
            if (right == -1) {
                right = width
            }

            int newWidth = right - left

            if (newWidth != blockWidth && !first) {
                break
            } else {
                blockWidth = newWidth
                blockHeight++
                first = false

                (left..<right).each { int i -> cpy[localY][i] = 0 }

                if (localY == height - 1) break
            }
        }

        float chipSize = GdxEx.stageEx.chipSize

        float boxWidth = blockWidth * chipSize / 2.0f as float
        float boxHeight = blockHeight * chipSize / 2.0f as float

        BodyDef bodyDef = new BodyDef()
        bodyDef.type = BodyType.STATIC
        bodyDef.position.set(PhysicsEx.p2m(new Vector2(x * chipSize + boxWidth as float, y * chipSize + boxHeight as float)))
        Body body = GdxEx.physicsEx.createBody(bodyDef)

        PolygonShape boxShape = new PolygonShape()
        boxShape.setAsBox(PhysicsEx.p2m(boxWidth), PhysicsEx.p2m(boxHeight));

        FixtureDef fixtureDef = new FixtureDef()
        fixtureDef.friction = 0.4f
        fixtureDef.shape = boxShape

        body.createFixture(fixtureDef)
    }

    private void createSlope(int x, int y, int id) {
        float chipSize = GdxEx.stageEx.chipSize
        float half = chipSize / 2.0f as float
        float meterHalf = PhysicsEx.p2m(half)

        BodyDef bodyDef = new BodyDef()
        bodyDef.type = BodyType.STATIC
        bodyDef.position.set(PhysicsEx.p2m(new Vector2(x * chipSize + half as float, y * chipSize + half as float)))
        Body body = GdxEx.physicsEx.createBody(bodyDef)

        FixtureDef fixtureDef = new FixtureDef()
        fixtureDef.friction = 0.4f

        PolygonShape shape = new PolygonShape()

        switch (id) {
            case 2:
                shape.set([new Vec2(meterHalf, -meterHalf),
                        new Vec2(meterHalf, meterHalf),
                        new Vec2(-meterHalf, -meterHalf)] as Vec2[], 3)
                break
            case 3:
                shape.set([new Vec2(-meterHalf, -meterHalf),
                        new Vec2(meterHalf, -meterHalf),
                        new Vec2(-meterHalf, meterHalf)] as Vec2[], 3)
                break
            case 4:
                shape.set([new Vec2(meterHalf, -meterHalf),
                        new Vec2(meterHalf, 0.0f),
                        new Vec2(-meterHalf, -meterHalf)] as Vec2[], 3)
                break
            case 5:
                shape.set([new Vec2(-meterHalf, -meterHalf),
                        new Vec2(meterHalf, -meterHalf),
                        new Vec2(meterHalf, meterHalf),
                        new Vec2(-meterHalf, 0.0f)] as Vec2[], 4)
                break
            case 6:
                shape.set([new Vec2(-meterHalf, -meterHalf),
                        new Vec2(meterHalf, -meterHalf),
                        new Vec2(meterHalf, 0.0f),
                        new Vec2(-meterHalf, meterHalf)] as Vec2[], 4)
                break
            case 7:
                shape.set([new Vec2(-meterHalf, -meterHalf),
                        new Vec2(meterHalf, -meterHalf),
                        new Vec2(-meterHalf, 0.0f)] as Vec2[], 3)
                break
            case 10:
                shape.set([new Vec2(meterHalf, meterHalf),
                        new Vec2(-meterHalf, meterHalf),
                        new Vec2(meterHalf, -meterHalf)] as Vec2[], 3)
                break
            case 11:
                shape.set([new Vec2(-meterHalf, meterHalf),
                        new Vec2(-meterHalf, -meterHalf),
                        new Vec2(meterHalf, meterHalf)] as Vec2[], 3)
                break
            case 12:
                shape.set([new Vec2(meterHalf, meterHalf),
                        new Vec2(-meterHalf, meterHalf),
                        new Vec2(meterHalf, 0.0f)] as Vec2[], 3)
                break
            case 13:
                shape.set([new Vec2(meterHalf, meterHalf),
                        new Vec2(-meterHalf, meterHalf),
                        new Vec2(-meterHalf, 0.0f),
                        new Vec2(meterHalf, -meterHalf)] as Vec2[], 4)
                break
            case 14:
                shape.set([new Vec2(-meterHalf, meterHalf),
                        new Vec2(-meterHalf, -meterHalf),
                        new Vec2(meterHalf, 0.0f),
                        new Vec2(meterHalf, meterHalf)] as Vec2[], 4)
                break
            case 15:
                shape.set([new Vec2(-meterHalf, meterHalf),
                        new Vec2(-meterHalf, 0.0f),
                        new Vec2(meterHalf, meterHalf)] as Vec2[], 3)
                break
            default:
                break
        }
        fixtureDef.shape = shape

        body.createFixture(fixtureDef)
    }

    private void createEntities(Stage stage, IStageScheme scheme) {
        stage.allEntities.values().each { List<Map<String, Object>> eList ->
            eList.each { Map<String, Object> e ->
                //GdxEx.entityEx.addEntity(scheme.createEntity(stage, e))
            }
        }
    }

    static private formatPath(String path) {
        path.endsWith("/") ? path : path + "/"
    }
}

interface IStageScheme {
    void createGraphics(Stage stage)
    String getBlockLayer()
    String getStagePath()
    int getChipSize()
    Entity createEntity(Stage stage, Map<String, Object> entity)
}