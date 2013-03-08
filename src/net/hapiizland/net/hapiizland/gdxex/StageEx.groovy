/**
 * Created with IntelliJ IDEA.
 * User: AndroCofffee
 * Date: 13/02/28
 * Time: 1:39
 * To change this template use File | Settings | File Templates.
 */

package net.hapiizland.net.hapiizland.gdxex

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import groovy.transform.CompileStatic

class StageEx {
    @Delegate Stage stage
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

    final Stage loadStage(String filename) {
        stage = new Stage(stagePath + filename)
        stageScheme.createGraphics(stage)
        createPhysicalBlocks(stageScheme.blockLayer)
        createEntities(stage, stageScheme)

        return stage
    }

    @CompileStatic
    final Vector2 limitCamera(Vector2 target) {
        Vector2 newTarget = new Vector2(target)
        float windowWidth = Gdx.graphics.width
       float windowHeight = Gdx.graphics.height
        Vector2 center = new Vector2(windowWidth / 2.0f as float, windowHeight / 2.0f as float)
        float stagePixelWidth = this.cols * chipSize
        float stagePixelHeight = this.rows * chipSize

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

        return newTarget;
    }

    private void createPhysicalBlocks(String name) {
        stage.createPhysicalBlocks(name)
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