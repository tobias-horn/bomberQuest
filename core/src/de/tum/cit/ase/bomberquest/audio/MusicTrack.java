package de.tum.cit.ase.bomberquest.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

/**
 * This enum is used to manage the music tracks in the game.
 * See the assets/audio folder for the actual music files.
 */

public enum MusicTrack {
    BACKGROUND("mainMenuTrack.mp3", 1f),
    GAMEPLAY_MUSIC("gameplayMusic.mp3", 1f);

    private final Music music;
    private final float originalVolume;
    private boolean isMuted = false;
    private boolean isPaused = false;

    MusicTrack(String fileName, float volume) {
        this.music = Gdx.audio.newMusic(Gdx.files.internal("audio/" + fileName));
        this.music.setLooping(true);
        this.music.setVolume(volume);
        this.originalVolume = volume;

    }


    public void play() {
        this.music.play();
        isPaused = false;
    }

    public void stop() {
        this.music.stop();
    }

    public void mute() {
        if (!isMuted) {
            music.setVolume(0);
            isMuted = true;
        }
    }

    public void unmute() {
        if (isMuted) {
            music.setVolume(originalVolume);
            isMuted = false;
        }
    }

    public void pause(){
        if (!isPaused){
            music.pause();
            isPaused = true;
        }
    }

    public boolean isMuted() {
        return isMuted;
    }

    public void setLooping(boolean shouldLoop) {
        this.music.setLooping(shouldLoop);
    }

    public void setOnCompletionListener(Music.OnCompletionListener listener) {
        this.music.setOnCompletionListener(listener);
    }

}


