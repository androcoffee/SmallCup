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

    static final Tween lambdaCallback(Closure callback) {
        Tween.call(new LambdaCallback(callback))
    }
}

@CompileStatic
class LambdaCallback implements TweenCallback {
    LambdaCallback(Closure callback) {
        this.callback = callback
    }

    void onEvent(int type, BaseTween<?> tween) {
        callback(type, tween)
    }

    private Closure callback
}
