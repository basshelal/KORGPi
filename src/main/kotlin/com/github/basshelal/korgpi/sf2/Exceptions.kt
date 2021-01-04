package com.github.basshelal.korgpi.sf2

import java.io.IOException

open class RIFFException(s: String) : IOException(s)

open class RIFFInvalidFormatException(s: String) : RIFFException(s)

open class RIFFInvalidDataException(s: String) : RIFFException(s)