package mx.edu.multimedia

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2

/** Estados explícitos del reproductor de audio. */
enum class Estado { DETENIDO, REPRODUCIENDO, PAUSADO }

class MainActivity : AppCompatActivity() {

    private var player: MediaPlayer? = null
    private var estado = Estado.DETENIDO
    private var volumen = 0.7f
    private lateinit var tvEstado: TextView
    private lateinit var tvPos: TextView
    private lateinit var pager: ViewPager2

    private val slides = listOf(
        Slide(R.drawable.omagai_1, "Producto 1"),
        Slide(R.drawable.elegante_1, "Producto 2"),
        Slide(R.drawable.gato_pro, "Producto 3"),
        Slide(R.drawable.achuali_1, "Producto 4")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tvEstado = findViewById(R.id.tvEstado)
        tvPos = findViewById(R.id.tvPos)
        pager = findViewById(R.id.pager)

        // --- Carrusel de imágenes ---
        pager.adapter = SlideAdapter(slides)
        pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) { tvPos.text = "${position + 1} / ${slides.size}" }
        })
        findViewById<Button>(R.id.btnNext).setOnClickListener {
            pager.currentItem = (pager.currentItem + 1) % slides.size
        }
        findViewById<Button>(R.id.btnPrev).setOnClickListener {
            pager.currentItem = (pager.currentItem - 1 + slides.size) % slides.size
        }

        // --- Controles de audio ---
        findViewById<Button>(R.id.btnPlay).setOnClickListener { reproducir() }
        findViewById<Button>(R.id.btnPause).setOnClickListener { pausar() }
        findViewById<Button>(R.id.btnStop).setOnClickListener { detener() }
        findViewById<SeekBar>(R.id.seekVol).setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(s: SeekBar, p: Int, fromUser: Boolean) {
                volumen = p / 100f
                player?.setVolume(volumen, volumen)
            }
            override fun onStartTrackingTouch(s: SeekBar) {}
            override fun onStopTrackingTouch(s: SeekBar) {}
        })
    }

    private fun reproducir() {
        when (estado) {
            Estado.REPRODUCIENDO -> return
            Estado.PAUSADO -> player?.start()   // reanuda donde se quedó
            Estado.DETENIDO -> {
                player = MediaPlayer.create(this, R.raw.alerta)?.apply {   // res/raw/alerta
                    setVolume(volumen, volumen)
                    setOnCompletionListener { detener() }                  // al terminar, libera
                    start()
                }
            }
        }
        if (player != null) actualizar(Estado.REPRODUCIENDO)
    }

    private fun pausar() {
        if (estado == Estado.REPRODUCIENDO) { player?.pause(); actualizar(Estado.PAUSADO) }
    }

    private fun detener() {
        player?.release()   // libera memoria nativa
        player = null
        actualizar(Estado.DETENIDO)
    }

    private fun actualizar(nuevo: Estado) {
        estado = nuevo
        tvEstado.text = "Audio: " + when (nuevo) {
            Estado.DETENIDO -> "detenido"
            Estado.REPRODUCIENDO -> "reproduciendo"
            Estado.PAUSADO -> "en pausa"
        }
    }

    // Ciclo de vida: no dejar audio sonando ni recursos retenidos
    override fun onStop() { super.onStop(); if (estado == Estado.REPRODUCIENDO) pausar() }
    override fun onDestroy() { super.onDestroy(); player?.release(); player = null }
}
