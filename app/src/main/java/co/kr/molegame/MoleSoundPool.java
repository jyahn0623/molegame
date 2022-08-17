package co.kr.molegame;


import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.HashMap;

public class MoleSoundPool {
    public static final int CLEAR_SOUND = R.raw.when_game_clear;
    public static final int FAILED_SOUND = R.raw.when_game_failed;
    public static final int HIT_SOUND = R.raw.when_hit;
    public static final int VOICE_MOLE_1 = R.raw.voice_mole_1;

    private static SoundPool soundpool;
    private static HashMap<Integer, Integer> soundpoolMap;

    public static void initSounds(Context context){
        AudioAttributes attributes = new AudioAttributes.Builder()
                                         .setUsage(AudioAttributes.USAGE_GAME)
                                         .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                         .build();
        soundpool = new SoundPool.Builder()
                        .setAudioAttributes(attributes)
                        .build();
        soundpoolMap = new HashMap(3);
        // 두더지 잡기 멘트
        soundpoolMap.put(CLEAR_SOUND, soundpool.load(context, CLEAR_SOUND, 1));
        soundpoolMap.put(FAILED_SOUND, soundpool.load(context, FAILED_SOUND, 1));
        soundpoolMap.put(HIT_SOUND, soundpool.load(context, HIT_SOUND, 1));
        soundpoolMap.put(VOICE_MOLE_1, soundpool.load(context, VOICE_MOLE_1, 1));
    }

    public static void play(int raw_id){
        if (soundpoolMap.containsKey(raw_id))
            soundpool.play(soundpoolMap.get(raw_id), 1, 1, 1, 0, 1);
    }
}
