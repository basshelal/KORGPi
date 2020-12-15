# Synthesizer Notes

A Synthesizer is nothing more than just a processor, a connection between MIDI in and Audio out,
meaning it receives or listens for MIDI events and handles them by sending out Audio data.

In this sense then, a Synthesizer has a single JACK MIDI in port instance and a single Audio out 
port instance. The user can thus configure through JACK how these will work, for example the user
can send multiple MIDI keyboards into one synthesizer which can play to multiple different audio outputs
such as speakers and a DAW. 

We can then eventually allow for *multiple* synthesizers meaning multiple instruments, thus 
creating many more ideas. For example, a single MIDI keyboard can play single synthesizer to an audio card while
another MIDI keyboard can play that instrument and another instrument as well to all audio outs.

I write this because this is the correct way of thinking about a synthesizer, and the justification is 
this potential that JACK gives us.