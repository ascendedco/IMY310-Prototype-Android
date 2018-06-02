package co.ascended.waiterio

import android.app.Application
import co.ascended.waiterio.database.Seeding

class Waiterio: Application() {

    override fun onCreate() {
        super.onCreate()
        // Seed the database so we have mock data.
        val seeding = Seeding()
    }
}