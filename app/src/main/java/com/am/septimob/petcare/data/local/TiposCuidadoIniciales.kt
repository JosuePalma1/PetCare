package com.am.septimob.petcare.data.local

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

object TiposCuidadoIniciales : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        db.execSQL(
            "INSERT INTO tipos_cuidado (nombre) VALUES " +
                    "('Vacunación'), ('Baño'), ('Desparasitación'), ('Visita veterinaria')"
        )
    }
}