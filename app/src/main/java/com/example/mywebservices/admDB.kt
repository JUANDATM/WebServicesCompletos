package com.example.sqlite
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class admDB (context: Context) :SQLiteOpenHelper(context, DATABASE, null, 1)//Se indica que los parametros se envian a la base de datos
{
    companion object
    {
        val DATABASE="Escuela" //definimos la base de datos
    }
    override fun onCreate(db: SQLiteDatabase?) {//funcion importada en la cual vamos a mandar crear la base de datos
        db?.execSQL(
            "Create Table Estudiante(" +
                    "nocontrol text primary key,"+
                    "nombre text, "+
                    "carrera text, "+
                    "telefono text)"
        )
    }
    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {

    }
    fun consulta(select: String):Cursor? {//clase que se creo para enviar las consultas a la base de datos
        try
        {
            val db = this.readableDatabase//se crea esta variable para leer los datos de la base de datos
            return db.rawQuery(select,null)
        }
        catch (ex:Exception)
        {
            return null
        }
    }
    fun Ejecuta(sentencia:String):Int {// Funcion creada para poder escribir dentro de la base de datos
        try
        {
            val db=this.writableDatabase// se abre base datos en base modo lectura
            db.execSQL(sentencia)
            db.close()
            return  1//si se escribe en la base de datos , envia un valor exitoso
        }
        catch (ex: Exception)
        {
            return 0// si no se escribe en la base de datos o falla algo , envia un valor de cero , que significa que no se escribio en la base de datos
        }
    }


}
