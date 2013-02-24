/**
 * Created with IntelliJ IDEA.
 * User: tetsuya
 * Date: 13/02/24
 * Time: 23:13
 * To change this template use File | Settings | File Templates.
 */

package net.hapiizland.net.hapiizland.gdxex

import aurelienribon.tweenengine.Tween
import aurelienribon.tweenengine.TweenManager

class TweenEx {
    @Delegate TweenManager tweenManager = new TweenManager()

    TweenManager getManager() { tweenManager }

    TweenEx() {
        Tween.registerAccessor(Entity.class, new EntityTweener())
    }
}
