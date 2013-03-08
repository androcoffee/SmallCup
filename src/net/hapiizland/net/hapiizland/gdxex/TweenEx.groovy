/**
 * Created with IntelliJ IDEA.
 * User: tetsuya
 * Date: 13/02/24
 * Time: 23:13
 * To change this template use File | Settings | File Templates.
 */

package net.hapiizland.net.hapiizland.gdxex

import aurelienribon.tweenengine.BaseTween
import aurelienribon.tweenengine.Tween
import aurelienribon.tweenengine.TweenCallback
import aurelienribon.tweenengine.TweenManager
import groovy.transform.CompileStatic

@CompileStatic
class TweenEx {
    @Delegate TweenManager tweenManager = new TweenManager()

    TweenManager getManager() { tweenManager }

    TweenEx() {
        Tween.setCombinedAttributesLimit(4)
        Tween.registerAccessor(Entity.class, new EntityTweener())
    }

    static final Tween call(Closure callback) {
        Tween.call(new LambdaCallback(callback))
    }

    static final Tween callWithRepetition(int times, int delay, Closure callback) {
        this.call(callback).repeat(times, GdxEx.director.spf60 * (delay + 1))
    }
}

@CompileStatic
class LambdaCallback implements TweenCallback {
    LambdaCallback(Closure callback) {
        this.callback = callback
    }

    void onEvent(int type, BaseTween<?> tween) {
        switch (callback.parameterTypes.length) {
            case 0:
                callback()
                break
            case 1:
                callback(count)
                break
            case 2:
                callback(type, tween)
                break
            case 3:
                callback(type, tween, count)
        }
        count++
    }

    private Closure callback
    private int count = 0
}
