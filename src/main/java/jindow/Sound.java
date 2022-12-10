package jindow;

import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_filename;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.libc.LibCStdlib.free;
import static org.lwjgl.openal.AL10.*;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public class Sound {
	private int bufferID;
	private int sourceID;
	private boolean isPlaying = false;
	
	private String filepath;
	
	public Sound(String filepath, boolean loops) {
		this.filepath = filepath;
		
		// Allocate space to store information returned
		stackPush();
		IntBuffer channelsBuffer = stackMallocInt(1);
		stackPush();
		IntBuffer sampleRateBuffer = stackMallocInt(1);
		
		ShortBuffer rawAudioBuffer = stb_vorbis_decode_filename(filepath, channelsBuffer, sampleRateBuffer);
		if (rawAudioBuffer == null) {
			System.out.println("WARNING: [SOUND] Could not load sound '" + filepath + "'");
			stackPop();
			stackPop();
			return;
		}
		
		// Retrieve extra information stored 
		int channels = channelsBuffer.get();
		int sampleRate = sampleRateBuffer.get();
		
		// Free
		stackPop();
		stackPop();
		
		// Find the correct openAL format
		int format = -1;
		if (channels == 1) {
			format = AL_FORMAT_MONO16;
		} else if (channels == 2) {
			format = AL_FORMAT_STEREO16;
		}
		
		bufferID = alGenBuffers();
		alBufferData(bufferID, format, rawAudioBuffer, sampleRate);
		
		// Generate source
		sourceID = alGenSources();
		
		alSourcei(sourceID, AL_BUFFER, bufferID);
		alSourcei(sourceID, AL_LOOPING, loops ? 1 : 0);
		alSourcei(sourceID, AL_POSITION, 0);
		alSourcef(sourceID, AL_GAIN, .3f);
		
		// Free stb raw audio buffer
		free(rawAudioBuffer);
	}
	
	public void delete() {
		alDeleteBuffers(bufferID);
		alDeleteSources(sourceID);
	}
	
	public void play() {
		int state = alGetSourcei(sourceID, AL_SOURCE_STATE);
		if (state == AL_STOPPED) {
			isPlaying = false;
			alSourcei(sourceID, AL_POSITION, 0);
		}
		
		if (!isPlaying) {
			alSourcePlay(sourceID);
			isPlaying = true;
		}
	}
	
	public void stop() {
		if (isPlaying) {
			alSourceStop(sourceID);
			isPlaying = false;
		}
	}
	
	public String getFilepath() {
		return this.filepath;
	}
	
	public boolean isPlaying() {
		int state = alGetSourcei(sourceID, AL_SOURCE_STATE);
		if (state == AL_STOPPED) {
			isPlaying = false;
		}
		
		return isPlaying;
	}
}
