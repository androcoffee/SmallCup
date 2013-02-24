/**
 * Created with IntelliJ IDEA.
 * User: AndroCofffee
 * Date: 13/02/22
 * Time: 0:47
 * To change this template use File | Settings | File Templates.
 */

package net.hapiizland.net.hapiizland.gdxex

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import groovy.transform.CompileStatic

@CompileStatic
class EntityEx {
    Iterator<Entity> getEntitiesIter() { entities.iterator() }

    void addEntity(Entity e) {
        newEntities << e
    }

    void removeAllEntities() {
        entities.clear()
        newEntities.clear()
    }

    void update() {
        entities.each { Entity e -> e.update() }

        removeDoneEntities()
        addNewEntities()
    }

    void draw() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        def batch = GdxEx.graphicsEx.batch
        batch.projectionMatrix = GdxEx.cameraEx.camera.combined
        batch.begin()
        entities.each { Entity e -> e.draw() }
        batch.end()
    }

    private void removeDoneEntities() {
        entities = entities.findAll { Entity e -> !e.done }.toList()
    }
    private void addNewEntities() {
        entities += newEntities
        newEntities.clear()
    }

    private List<Entity> entities = []
    private List<Entity> newEntities = []
}
