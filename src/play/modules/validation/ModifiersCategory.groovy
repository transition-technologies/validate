package play.modules.validation

/**
 * Helper methods to check for modifiers in
 * Javassist {@link javassist.CtMethod} and {@link javassist.CtClass}
 * @author Marek Piechut <m.piechut@tt.com.pl>
 */
class ModifiersCategory {

    /**
     * Check if method/class has all passed in modifiers
     *
     * @param behavior {@link javassist.CtMethod} or {@link javassist.CtClass} to check
     * (needs to have {@code int getModifiers()} method)
     * @param modifiers check if passed in behavior has these modifiers
     *
     * @return if method has passed in modifiers set
     */
    static def hasModifier(behavior, int ... modifiers) {
        int modifier = 0
        modifiers.each { modifier = modifier | it }
        return (behavior.modifiers & modifier) == modifier
    }

    /**
     * Check if method/class has exactly same as passed in modifiers
     * @param behavior {@link javassist.CtMethod} or {@link javassist.CtClass} to check
     * (needs to have {@code int getModifiers()} method)
     * @param modifiers check if passed in behavior has exactly these modifiers
     *
     * @return if modifiers are equal
     */
    static def hasExactModifier(behavior, int ... modifiers) {
        int modifier = 0
        modifiers.each { modifier = modifier | it }
        return behavior.modifiers == modifier
    }
}
