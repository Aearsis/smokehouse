/**
 * Codecs are responsible for encoding the nodes' value onto the wire and back.
 * <p>
 * The key idea behind codecs is, that some values can be represented more efficiently,
 * knowing the real backend for them - to give (the only) example, thermometer values in
 * the smokehouse are very unlikely to exceed the interval [0, 256]. So we encode them into
 * two bytes, covering all possible values and saving (at least) 50% space.
 */
package cz.eideo.smokehouse.common.api.codec;