package game;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.FloatControl;

public final class SoundManager {

    public enum Effect {
        BUTTON("Button.wav"),
        GAME_OVER("GameOver.wav"),
        VICTORY_10_WAVE("victory10Wave.wav"),
        HIT("Hit.wav"),
        HURT("Hurt.wav"),
        LEVEL_UP("LevelUp.wav"),
        PICKUP_COIN("pickupCoin.wav"),
        SHOOT("Shoot.wav");

        private final String fileName;

        Effect(String fileName) {
            this.fileName = fileName;
        }
    }

    private static final Map<Effect, Clip> clips = new EnumMap<>(Effect.class);
    private static final Random random = new Random();
    private static final String[] MENU_MUSIC = {
        "BGSound1.wav",
        "BGSound2.wav",
        "BGSound3.wav"
    };
    private static final String[] FIGHT_MUSIC = {
        "BGSound_Fight1.wav",
        "BGSound_Fight2.wav"
    };
    private static Clip musicClip;
    private static MusicCategory musicCategory;
    private static boolean enabled = true;

    private enum MusicCategory {
        MENU,
        FIGHT
    }

    private SoundManager() {
    }

    public static void play(Effect effect) {
        if (!enabled) {
            return;
        }

        Clip clip = clips.get(effect);
        if (clip == null) {
            clip = load(effect);
            if (clip == null) {
                return;
            }
            clips.put(effect, clip);
        }

        if (clip.isRunning()) {
            clip.stop();
        }
        clip.setFramePosition(0);
        clip.start();
    }

    public static void setEnabled(boolean enabled) {
        SoundManager.enabled = enabled;
        if (!enabled) {
            stopMusic();
        }
    }

    public static void updateMusic(GameState state) {
        MusicCategory requestedCategory = getMusicCategory(state);

        if (requestedCategory == null) {
            stopMusic();
        } else if (requestedCategory != musicCategory
                || musicClip == null
                || !musicClip.isOpen()) {
            playRandomMusic(requestedCategory);
        }
    }

    private static MusicCategory getMusicCategory(GameState state) {
        if (state == GameState.PLAYING) {
            return MusicCategory.FIGHT;
        }
        if (state == GameState.TITLE
                || state == GameState.CHAR_SELECT
                || state == GameState.WEAPON_SELECT
                || state == GameState.SHOP
                || state == GameState.PAUSED) {
            return MusicCategory.MENU;
        }
        return null;
    }

    private static void playRandomMusic(MusicCategory category) {
        stopMusic();

        String[] candidates = category == MusicCategory.FIGHT
            ? FIGHT_MUSIC
            : MENU_MUSIC;
        int startIndex = random.nextInt(candidates.length);

        for (int offset = 0; offset < candidates.length; offset++) {
            String candidate = candidates[(startIndex + offset) % candidates.length];
            Clip loadedClip = loadMusic(candidate);
            if (loadedClip != null) {
                musicClip = loadedClip;
                musicCategory = category;
                musicClip.loop(Clip.LOOP_CONTINUOUSLY);
                musicClip.start();
                return;
            }
        }

        System.err.println("Unable to load music for category " + category);
    }

    private static void stopMusic() {
        if (musicClip != null) {
            musicClip.stop();
            musicClip.close();
            musicClip = null;
        }
        musicCategory = null;
    }

    private static Clip loadMusic(String fileName) {
        String resourcePath = "/assets/sounds/bg/" + fileName;

        try {
            AudioInputStream audioStream;
            InputStream resource = SoundManager.class.getResourceAsStream(resourcePath);

            if (resource != null) {
                audioStream = AudioSystem.getAudioInputStream(resource);
            } else {
                File file = new File("assets/sounds/bg/" + fileName);
                if (!file.exists()) {
                    return null;
                }
                audioStream = AudioSystem.getAudioInputStream(file);
            }

            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            audioStream.close();
            setMusicVolume(clip, 0.40f);
            return clip;
        } catch (IOException | UnsupportedAudioFileException
                | LineUnavailableException exception) {
            System.err.println(
                "Unable to load music " + fileName + ": "
                    + exception.getMessage()
            );
            return null;
        }
    }

    private static void setMusicVolume(Clip clip, float volume) {
        if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gain = (FloatControl) clip.getControl(
                FloatControl.Type.MASTER_GAIN
            );
            float decibels = (float) (20.0 * Math.log10(volume));
            gain.setValue(Math.max(gain.getMinimum(), decibels));
        }
    }

    private static Clip load(Effect effect) {
        String resourcePath = "/assets/sounds/" + effect.fileName;

        try {
            AudioInputStream audioStream;
            InputStream resource = SoundManager.class.getResourceAsStream(resourcePath);

            if (resource != null) {
                audioStream = AudioSystem.getAudioInputStream(resource);
            } else {
                audioStream = AudioSystem.getAudioInputStream(
                    new File("assets/sounds/" + effect.fileName)
                );
            }

            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            audioStream.close();
            return clip;
        } catch (IOException | UnsupportedAudioFileException
                | LineUnavailableException exception) {
            System.err.println(
                "Unable to load sound " + effect.fileName + ": "
                    + exception.getMessage()
            );
            return null;
        }
    }
}
