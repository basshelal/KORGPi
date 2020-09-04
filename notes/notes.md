# Notes

We should fork and explore exactly how JSyn does things, it seems that JSyn uses PortAudio as the Audio Engine
and Java Sound as the MIDI Input and latency is quite fine from what I recall last year but I need to test
this properly again, especially on a Pi, if I can even get audio playback :/ In any case Jsyn is an excellent
starting point for something that works, is fairly complete and decently documented

https://github.com/philburk/jsyn

There are 3 main components to worry about and 2 of them being fairly advanced and require external dependencies

## Midi Input

Though Java Sound already handles this fairly well, we should also try using RtMidi as a fast, native, cross platform solution
if possible, also, try and compare speeds between Java Sound MIDI and RtMidi, I suspect the difference will not be major but it's 
worth playing around with for fun and learning how JNI works.

https://github.com/thestk/rtmidi

If we do manage to get something working with decent performance we can release it as a JVM port/binding of RtMidi even if Java Sound
has better performance, the options are nice to have

## Audio Engine

Java Sound seems worse in this regard, especially in comparison to the alternatives. Here we can use another native cross platform
solution for audio playback that is compatible with Linux (ALSA and JACK). I found 2 that look promising, Phil Burk's PortAudio which
is used in JSyn (called JPortAudio), and OpenAL, although OpenAL is way overkill for what we want and is better suited for 3D stuff.
But essentially any native cross platform audio playback solution will suffice, I suspect PortAudio to be more than sufficient.

http://www.portaudio.com/

Phil Burk has a .jar of JPortAudio with JSyn 

https://github.com/philburk/jsyn/blob/master/libs/jportaudio.jar

The source is here

https://app.assembla.com/spaces/portaudio/git/source/master/bindings/java/jportaudio/src/com/portaudio

But according to [this document](https://app.assembla.com/spaces/portaudio/git/source/master/bindings/java/jportaudio.dox)
JPortAudio is in alpha and will **NOT** work on Linux, so we may have to do things ourselves with regard to a JVM port of PortAudio

## Portable Format (SF2)

Java Sound apparently does not officially support SF2 according to [this](http://jsresources.sourceforge.net/faq_midi.html#soundfont)
but I haven't personally tested this myself at all nor do I currently know too much technically about SF2. The Korg does use 
SF2s for its sample sets with actual samples often being just PCMs. In any case SF2 is extremely popular and is a good portable solution
to sample based synthesis so we definitely should be using it but for this we may have to do it ALL ourselves unless Java Sound
actually does support SF2.

Java technically has an SF2 class in `com.sun.media.sound.SF2Soundbank` but I've had trouble using it because of module stuff and I 
suspect it's an internal class not meant to be used, we can always backport it or create our own implementation of an SF2 reader 
using the old Sun code as a reference
 
Worst case scenario though there are non-Java SF2 readers and players out there including good ole JavaScript so there's lots 
of mitigations for this