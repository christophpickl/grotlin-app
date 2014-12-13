package at.cpickl.agrotlin.service

import at.cpickl.agrotlin.R
import java.util.HashMap
import android.media.SoundPool
import android.media.AudioManager
import android.content.Context
import javax.inject.Singleton
import android.media.MediaPlayer
import org.slf4j.LoggerFactory

// http://java.dzone.com/articles/playing-sounds-android
// The general guidelines on which one to use and when, are that SoundPool is best for short sound clips
// (notifications sounds, sound effects in games),
// while MediaPlayer is better suited for larger sound files like songs.

trait SoundPlayer {
    // hacky design, but necessary because of context dependency on EVERYTHING :-/
    fun init(context: Context)
    fun isInit(): Boolean
    fun play(sound: Sound, volume: Float = 1.0F)
}

Singleton class PooledSoundPlayer : SoundPlayer {
    class object {
        private val LOG = LoggerFactory.getLogger(javaClass<PooledSoundPlayer>())
    }

    private val pool: SoundPool = SoundPool(Sound.values().size, AudioManager.STREAM_MUSIC, /*srcQuality=*/100)
    private var sounds: Map<Sound, Int>? = null

    override fun init(context: Context) {
        LOG.debug("init(context=${context})")
        if (sounds != null) {
            throw IllegalStateException("Already initialized!")
        }
        sounds = initMap(context)
    }
    override fun isInit(): Boolean = sounds != null

    override fun play(sound: Sound, volume: Float) {
        LOG.debug("play(sound=${sound}, volume=${volume})")
        if (sounds == null) {
            throw IllegalStateException("Not yet initialized!")
        }
        // old style
//        val player = MediaPlayer.create(context, R.raw.roll)
//        player.setOnCompletionListener { it.release() }
//        player.start()

        pool.play(sounds!!.get(sound)!!, volume, volume, 1, 0, 1.0F);
    }

    private fun initMap(context: Context): Map<Sound, Int> {
        val result = hashMapOf<Sound, Int>()
        var priority: Int = 1
        for (sound in Sound.values()) {
            result.put(sound, pool.load(context, sound.id, priority++))
        }
        return result
    }
}

enum class Sound(val id: Int) {
    ROLL : Sound(R.raw.roll)
}