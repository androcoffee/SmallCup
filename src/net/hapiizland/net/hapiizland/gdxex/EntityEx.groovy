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
import org.jbox2d.dynamics.contacts.Contact

@CompileStatic
class EntityEx {
    List<Entity> getEntities() { entities }

    void addEntity(Entity e) {
        newEntities << e
    }

    void removeAllEntities() {
        entities.clear()
        newEntities.clear()
    }

    void update() {
        updateTouchings()

        entities.each { Entity e -> e.update() }

        sortEntities()
        removeDoneEntities()
        addNewEntities()
    }

    void draw() {
        def batch = GdxEx.graphicsEx.batch
        entities.each { Entity e ->
            if (e.isFollowingCamera()) {
                batch.projectionMatrix = GdxEx.cameraEx.uiCamera.combined
                e.draw()
                batch.projectionMatrix = GdxEx.cameraEx.camera.combined
            } else {
                e.draw()
            }
        }
    }

    private void sortEntities() {
        entities = entities.sort { Entity e -> e.z }
    }

    private void removeDoneEntities() {
        entities = entities.findAll { Entity e -> !e.done }.toList()
    }

    private void addNewEntities() {
        entities += newEntities
        newEntities.clear()
    }

    private void updateTouchings() {
        entities.findAll { Entity e -> e.hasMovingScheme }.each { Entity e ->
            e.updateOnGround(false)
            e.updateOnCeiling(false)
            e.updateOnRightWall(false)
            e.updateOnLeftWall(false)
        }

        for (Contact contact = GdxEx.physicsEx.getContactList(); contact; contact = contact.next) {
            if (contact.touching) {
                Object userDataA = contact.fixtureA.body.userData
                Object userDataB = contact.fixtureB.body.userData
                def normal = contact.manifold.localNormal //normal vector is from A to B

                if (userDataA && userDataA.class == Entity) {
                    //if (normal.y >= 0.5f) entityA.updateOnCeiling(true)
                    //if (normal.y <= -0.5f) entityA.updateOnGround(true)
                    //if (normal.x >= 0.5f) entityA.updateOnRightWall(true)
                    //if (normal.x <= -0.5) entityA.updateOnLeftWall(true)
                }
                if (userDataB && userDataB.class == Entity) {
                    Entity entityB = userDataB as Entity
                    if (normal.y >= 0.5f) entityB.updateOnGround(true)
                    if (normal.y <= -0.5f) entityB.updateOnCeiling(true)
                    if (normal.x >= 0.5f) entityB.updateOnLeftWall(true)
                    if (normal.x <= -0.5) entityB.updateOnRightWall(true)
                }
            }

        }
    }

    private List<Entity> entities = []
    private List<Entity> newEntities = []
}
