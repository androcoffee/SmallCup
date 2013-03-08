/**
 * Created with IntelliJ IDEA.
 * User: AndroCofffee
 * Date: 13/02/27
 * Time: 23:28
 * To change this template use File | Settings | File Templates.
 */

package net.hapiizland.smallcup

import com.badlogic.gdx.graphics.Color
import groovy.transform.CompileStatic
import net.hapiizland.net.hapiizland.gdxex.DrawingScheme
import net.hapiizland.net.hapiizland.gdxex.Entity
import net.hapiizland.net.hapiizland.gdxex.GdxEx
import net.hapiizland.net.hapiizland.gdxex.IDrawingScheme

@CompileStatic
abstract class AfterGrowDrawingScheme extends DrawingScheme {
    @Delegate IDrawingScheme scheme

    abstract int getTotal()

    AfterGrowDrawingScheme(IDrawingScheme scheme, int num, int period, boolean alphaScaling, boolean sizeScaling) {
        this.scheme = scheme
        this.num = num
        this.period = period
        this.alphaScaling = alphaScaling
        this.sizeScaling = sizeScaling

        prevDrawingSchemes = (0..<total).collect { scheme.cpy() }.toList()
        scalingList = prevDrawingSchemes.collect { IDrawingScheme s -> this.calcScaling(s, num * period) }.toList()
    }

    @Override
    void onSetupDrawingScheme(Entity e) {
    }

    IDrawingScheme createCopiedScheme() {
        null
    }

    protected void pushNext() {
        IDrawingScheme cpyScheme = scheme.cpy()
        prevDrawingSchemes.add(0, cpyScheme)
        prevDrawingSchemes.pop()

        scalingList.add(0, calcScaling(cpyScheme, num * period))
        scalingList.pop()
    }

    abstract void updatePrevs()

    protected List<Float> calcScaling(IDrawingScheme s, int total) {
        float aScaling = s.color.a / total as float
        float xScaling = s.scaleX / total as float
        float yScaling = s.scaleY / total as float

        [aScaling, xScaling, yScaling]
    }

    protected List<IDrawingScheme> prevDrawingSchemes = []
    protected List<List<Float>> scalingList = []
    int num
    int period
    boolean alphaScaling
    boolean sizeScaling
}

@CompileStatic
class SmoothAfterGrowDrawingScheme extends AfterGrowDrawingScheme {
    @Override int getTotal() { num * period }

    final static SmoothAfterGrowDrawingScheme createInstance(Map kwargs) {
        new SmoothAfterGrowDrawingScheme(
                kwargs.scheme as IDrawingScheme,
                kwargs.num as int,
                kwargs.period as int,
                kwargs.alphaScaling as boolean,
                kwargs.sizeScaling as boolean)
    }

    SmoothAfterGrowDrawingScheme(IDrawingScheme scheme, int num, int period, boolean alphaScaling, boolean sizeScaling) {
        super(scheme, num, period, alphaScaling, sizeScaling)
    }

    @Override
    void onUpdateDrawingScheme(Entity e) {
        pushNext()

        updatePrevs()
    }

    @Override
    void updatePrevs() {
        (0..<num).each { int i ->
            def index = i * period
            def s = prevDrawingSchemes[index]
            def scalings = scalingList[index]

            if (alphaScaling) {
                s.color = new Color(s.color.r, s.color.g, s.color.b, s.color.a - scalings[0] * index as float)
            }
            if (sizeScaling) {
                s.setScale(s.scaleX - scalings[1] * index as float, s.scaleY - scalings[2] * index as float)
            }
        }
    }

    @Override
    void onDrawDrawingScheme(Entity e) {
        prevDrawingSchemes.reverse().eachWithIndex { IDrawingScheme s, int i ->
            if (i % period == 0) {
                s.drawDrawingScheme(e)
            }
        }
    }
}

@CompileStatic
class DigitAfterGrowDrawingScheme extends AfterGrowDrawingScheme {
    @Override
    int getTotal() { num }

    final static DigitAfterGrowDrawingScheme createInstance(Map kwargs) {
        new DigitAfterGrowDrawingScheme(
                kwargs.scheme as IDrawingScheme,
                kwargs.num as int,
                kwargs.period as int,
                kwargs.alphaScaling as boolean,
                kwargs.sizeScaling as boolean)
    }

    DigitAfterGrowDrawingScheme(IDrawingScheme scheme, int num, int period, boolean alphaScaling, boolean sizeScaling) {
        super(scheme, num, period, alphaScaling, sizeScaling)
    }

    @Override
    void onUpdateDrawingScheme(Entity e) {
        if (GdxEx.director.elapsedFrames % period == 0) {
            pushNext()
        }

        updatePrevs()
    }

    @Override
    void updatePrevs() {
        (0..<num).each { int i ->
            def index = i
            def s = prevDrawingSchemes[index]
            def scalings = scalingList[index]

            if (alphaScaling) {
                s.color = new Color(s.color.r, s.color.g, s.color.b, s.color.a - scalings[0] as float)
            }
            if (sizeScaling) {
                s.setScale(s.scaleX - scalings[1] as float, s.scaleY - scalings[2] as float)
            }
        }
    }

    @Override
    void onDrawDrawingScheme(Entity e) {
        prevDrawingSchemes.reverse().each { IDrawingScheme s ->
            s.drawDrawingScheme(e)
        }
    }
}
