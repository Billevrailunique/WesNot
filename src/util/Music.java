/**
 * The {@code Music} class provides methods to play background music and sound effects
 * from WAV files located in the "ressource/sound/" directory.
 * 
 * It uses Java's {@link javax.sound.sampled} package to handle audio playback.
 */
package util;

import java.io.*;
import javax.sound.sampled.*;

public class Music {
   private static File file;
   private static AudioInputStream audioIn;
   private static Clip clip; // Clip for background music
   private static Clip clipSFX; // Clip for sound effects
   private static FloatControl gainControlMusic;
   private static FloatControl gainControlSFX;
   private static float volumeSFX;
   private static float volumeMusic;

   /**
    * Constructs a {@code Music} object and starts playing the default menu music.
    * The default music file is "MenuMusic.wav" located in "ressource/sound/".
    */
   public Music() {
      try {
         // Initialiser le clip de musique
         file = new File("ressource/sound/MenuMusic.wav");
         audioIn = AudioSystem.getAudioInputStream(file);
         clip = AudioSystem.getClip();
         clip.open(audioIn);
         gainControlMusic = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
         gainControlMusic.setValue(volumeMusic);
         clip.start();

         // Initialiser un clip SFX vide pour avoir le contrôle dès le début
         clipSFX = AudioSystem.getClip();

      } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
         e.printStackTrace();
      }
   }

   /**
    * Plays a specified music file from the "ressource/sound/" directory.
    * If a track is already playing, it stops before playing the new one.
    *
    * @param s the name of the WAV file (without extension) to be played.
    */
   public void play(String s) {
      if (clip.isRunning()) {
         clip.stop();
      }
      try {
         file = new File("ressource/sound/" + s + ".wav");
         audioIn = AudioSystem.getAudioInputStream(file);
         clip = AudioSystem.getClip();
         clip.open(audioIn);
         gainControlMusic = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
         gainControlMusic.setValue(volumeMusic);
         clip.loop(Clip.LOOP_CONTINUOUSLY);
         clip.start();
      } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
         e.printStackTrace();
      }
   }

   /**
    * Plays a short sound effect (SFX) from the "ressource/sound/" directory.
    *
    * @param s the name of the WAV file (without extension) to be played as an SFX.
    */

   public void playSFX(String s) {
      try {
         if (clipSFX != null) {
            clipSFX.stop();
            clipSFX.close(); // Fermer le clip avant de le réutiliser
        }
        
        file = new File("ressource/sound/" + s + ".wav");
        audioIn = AudioSystem.getAudioInputStream(file);
        clipSFX = AudioSystem.getClip();
        clipSFX.open(audioIn);
        gainControlSFX = (FloatControl) clipSFX.getControl(FloatControl.Type.MASTER_GAIN);
        gainControlSFX.setValue(volumeSFX);
        clipSFX.start();
        
        // Fermer l'AudioInputStream après utilisation
        audioIn.close();
    } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
        e.printStackTrace();
    }
   }

   public void changeSFXVolume(float volume) {
      volumeSFX = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
      if (gainControlSFX != null) {
         gainControlSFX.setValue(volumeSFX);
      }
   }

   public void changeMusicVolume(float volume) {
      volumeMusic = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
      if (gainControlMusic != null) {
         gainControlMusic.setValue(volumeMusic);
      }
   }
}
