import javax.sound.sampled.AudioFormat
import kotlin.math.PI

const val HALF_PI = PI / 2.0

const val SAMPLE_RATE = 44100
const val SAMPLE_RATE_POWER_OF_TWO = 65536

val MAX_AMPLITUDE = Byte.MAX_VALUE.toDouble()

const val MINIMUM_DIFFERENCE = 1E-3
const val DESIRED_DIFFERENCE = 1E-7

val WAVE_DEFAULT_FORMAT = AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100F, 16, 1, 2, 44100F, false)
val EASY_FORMAT = AudioFormat(SAMPLE_RATE.toFloat(), 8, 1, true, true)

val minDouble = Double.MIN_VALUE
val maxDouble = Double.MAX_VALUE
val maxLong = Long.MAX_VALUE
val minLong = Long.MIN_VALUE
val maxInt = Int.MAX_VALUE
val minInt = Int.MIN_VALUE
val minByte = Byte.MIN_VALUE
val maxByte = Byte.MAX_VALUE