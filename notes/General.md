# Notes

The most complete and professional way of doing things (and the most technically rewarding I think) is by
using JACK (JACK Audio Connection Kit) as the mixer instead of developing one ourselves.

JACK can handle both audio and MIDI and allows for JACK compliant applications to communicate data to each
other using JACK, for example our sampler/synthesizer can be plugged into a DAW as if it is a real instrument
plugged into the soundcard's input and the DAW can use the data we send to it, be it MIDI or Audio or both.

JACK is more of a Linux thing but is actually supported on UNIX and Windows though I had never heard of it until
I got into Linux audio stuff, Windows uses ASIO which is Windows only and proprietary closed source crap.

Anyway, JACK is the right way to go and luckily there seems to be an excellent library for JACK Java bindings called
[JNAJack](https://github.com/jaudiolibs/jnajack) written by Neil C Smith which looks excellent and should provide us
with everything we need to communicate with JACK.

Through JACK we can handle the main 2 interfaces with *relative* ease, Audio and MIDI:

## MIDI

MIDI has one problem, a MIDI device such as a USB Keyboard like the 
[KORG Microkey 25](https://www.korg.com/uk/products/computergear/microkey/page_1.php) is registered as an 
*"ALSA MIDI device"* to JACK in Linux, this prevents it from being usable through the main JACK API means, 
(at least those visible to me in JNAJack) which only allow MIDI and Audio.

It seems though that using `a2jmidid` may fix this issue by routing ALSA MIDI devices to become JACK MIDI devices,
see more details here https://askubuntu.com/questions/964909/how-to-connect-usb-midi-keyboard-to-qsynth-using-qjackctl
and here https://manual.ardour.org/setting-up-your-system/setting-up-midi/midi-on-linux/

What this means to us though is that we develop and application that receives MIDI events (and sends them if we want
to in the future) through completely through JACK. Users will unfortunately have to deal with *some* pains with initial 
JACK setup and issues (like we all do), but the result is a very powerful and performant system that is very flexible
and will mean less code for us to write since we don't need to keep track of a hardware mixer and instead pass that duty
to JACK, which itself actually *increases* performance and *decreases* latency, it's a classic win-win-win.

## Audio

Similar to MIDI we register our application through JACK as an application that can write audio data to a writable
stream such as soundcard(s), as well as (maybe) also being able to do something with audio data we receive from JACK,
though I am on the fence on this, since I see no real use for this anymore now that we rely on JACK.

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