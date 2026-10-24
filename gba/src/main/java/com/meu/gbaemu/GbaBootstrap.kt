package com.meu.gbaemu

import android.app.Application
import android.content.SharedPreferences
import com.meu.gbaemu.ui.Backup
import java.io.File

/**
 * Inicialização do módulo GBA. Antes era a classe Application (GbaApp); agora é chamada
 * pela Application do app hospedeiro (EmulatorApplication), que é quem existe no manifesto.
 */
object GbaBootstrap {
    // guardado num campo: o Android só mantém uma referência fraca do listener
    private var onChange: SharedPreferences.OnSharedPreferenceChangeListener? = null

    /** Copia os shaders otimizados (assets/shaders) para a pasta de shaders do app, uma única vez. */
    private fun seedShaders(app: Application) {
        try {
            val seed = app.getSharedPreferences("seed", Application.MODE_PRIVATE)
            if (seed.getInt("shaders", 0) >= 1) return
            val dir = File(app.filesDir, "shaders").also { it.mkdirs() }
            for (n in app.assets.list("shaders") ?: emptyArray()) {
                app.assets.open("shaders/$n").use { i -> File(dir, n).outputStream().use { o -> i.copyTo(o) } }
            }
            seed.edit().putInt("shaders", 1).apply()
        } catch (e: Exception) { }
    }

    /** Qualquer mudança nas configurações (de qualquer tela) é copiada para EmuladorPro/Configuracoes logo depois. */
    @JvmStatic
    fun init(app: Application) {
        if (onChange != null) return
        seedShaders(app)
        val l = SharedPreferences.OnSharedPreferenceChangeListener { _, _ -> Backup.schedule(app) }
        onChange = l
        app.getSharedPreferences("gba", Application.MODE_PRIVATE).registerOnSharedPreferenceChangeListener(l)
    }
}
