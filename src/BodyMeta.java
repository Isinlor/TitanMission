/**
 * Simple interface allowing to hold some special values "metadata" related to bodies.
 *
 * It may be colors, textures, spheres etc.
 *
 * Use generics on Body to have access to specific implementation.
 */
interface BodyMeta {

    /**
     * For repeated simulation bodies may need to be copied.
     *
     * This copy should make sure that copy can exists independently from source.
     * In other words copy and source must not influence each other to avoid one breaking another.
     */
    BodyMeta copy();

}
