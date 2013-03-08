/**
 * Created with IntelliJ IDEA.
 * User: AndroCofffee
 * Date: 13/02/28
 * Time: 1:21
 * To change this template use File | Settings | File Templates.
 */

package net.hapiizland.net.hapiizland.gdxex

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import groovy.transform.CompileStatic
import groovy.util.slurpersupport.GPathResult
import groovy.util.slurpersupport.NodeChild
import org.jbox2d.collision.shapes.PolygonShape
import org.jbox2d.dynamics.Body
import org.jbox2d.dynamics.BodyDef
import org.jbox2d.dynamics.BodyType
import org.jbox2d.dynamics.FixtureDef

@CompileStatic
class Stage {
    int getRows() { ogmoReader.rows }
    int getCols() { ogmoReader.cols }

    Map<String, List<Map<String, Object>>> getAllEntities() {
        ogmoReader.allEntities
    }

    List<Map<String, Object>> getEntities(String name) {
        ogmoReader.entities[name]
    }

    Map<String, List<List<Boolean>>> getAllLayers() {
        ogmoReader.layers
    }

    List<List<Boolean>> getLayer(String name) {
        ogmoReader.layers[name]
    }

    Stage(String filename) {
        ogmoReader = new OgmoReader(filename)
    }

    void createPhysicalBlocks(String name) {
        List<List<Boolean>> layer = ogmoReader.getLayer(name).collect { List<Boolean> row ->
            row.collect { boolean b -> b }.toList()
        }.toList()

        (0..<rows).each { int y ->
            (0..<cols).each { int x ->
                if (layer[y][x]) {
                    int bcols = 1
                    int brows = 0
                    boolean first = true;

                    while (true) {
                        int localY = y + brows;
                        int left = x

                        int right = layer[localY].findIndexOf(x) { boolean v -> !v }
                        if (right == -1) {
                            right = cols
                        }

                        int newCols = right - left

                        if (newCols != bcols && !first) {
                            break
                        } else {
                            bcols = newCols
                            brows++
                            first = false

                            (left..<right).each { int i -> layer[localY][i] = false }

                            if (localY == rows - 1) break
                        }
                    }

                    def chipSize = GdxEx.stageEx.chipSize

                    float boxWidth = bcols * chipSize / 2.0f as float
                    float boxHeight = brows * chipSize / 2.0f as float

                    BodyDef bodyDef = new BodyDef()
                    bodyDef.type = BodyType.STATIC
                    bodyDef.position.set(PhysicsEx.p2m(new Vector2(x * chipSize + boxWidth, y * chipSize + boxHeight)))
                    Body body = GdxEx.physicsEx.createBody(bodyDef)

                    PolygonShape boxShape = new PolygonShape()
                    boxShape.setAsBox(PhysicsEx.p2m(boxWidth), PhysicsEx.p2m(boxHeight));

                    FixtureDef fixtureDef = new FixtureDef()
                    fixtureDef.friction = 0.4f
                    fixtureDef.shape = boxShape

                    body.createFixture(fixtureDef)
                }
            }
        }
    }

    private OgmoReader ogmoReader

    class OgmoReader {
        int cols
        int rows

        private void setCols(int c) { cols = c }
        private void setRows(int r) { rows = r }

        Map<String, List<List<Boolean>>> getAllLayers() {
            layers
        }

        List<List<Boolean>> getLayer(String name) {
            layers[name]
        }

        Map<String, List<Map<String, Object>>> getAllEntities() {
            entities
        }

        List<Map<String, Object>> getEntities(String name) {
            entities[name]
        }

        OgmoReader(String filename) {
            node = new XmlSlurper().parse(filename)

            node.children().each { GPathResult child ->
                if (child["@exportMode"] != "") {
                    def layer = parseBitstring(child.text())
                    calcAndSetSize(layer)
                    layers[child.name()] = layer
                } else {
                    entities[child.name()] =
                        child.children().collect { NodeChild entity ->
                            Map attrs = [name: entity.name()]
                            entity.attributes().collect { String k, String v ->
                                attrs[k] = parseElement(v)
                            }
                            attrs
                        }.toList() as List<Map<String, Object>>
                }
            }
        }

        private void calcAndSetSize(List<List<Boolean>> layer) {
            cols = layer[0].size()
            rows = layer.size()
        }

        private Object parseElement(String str) {
            try {
                float v = Float.parseFloat(str)
                return v
            } catch (NumberFormatException e) {
                if (str == "True" || str == "False") {
                    return Boolean.parseBoolean(str)
                }
                return str
            }
            return null
        }

        List<List<Boolean>> parseBitstring(String text) {
            text.split("\n").collect { String line ->
                line.collect { String c -> c.toBoolean() }.toList()
            }.reverse().toList()
        }

        private Map<String, List<List<Boolean>>> layers = [:]
        private Map<String, List<Map<String, Object>>> entities = [:]
        private GPathResult node
    }
}

