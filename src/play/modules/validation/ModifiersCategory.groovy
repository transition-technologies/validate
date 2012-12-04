package play.modules.validation

import javassist.CtBehavior
import javassist.CtMethod

import java.lang.reflect.Modifier

/**
 * @author Marek Piechut <m.piechut@tt.com.pl>
 */
class ModifiersCategory {
    static def hasModifier(behavior, int ... modifiers) {
        int modifier = 0
        modifiers.each { modifier = modifier | it }
        return (behavior.modifiers & modifier) == modifier
    }

    static def hasExactModifier(behavior, int ... modifiers) {
        int modifier = 0
        modifiers.each { modifier = modifier | it }
        return behavior.modifiers == modifier
    }
}
