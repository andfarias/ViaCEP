package br.com.viacep.database.sqlite

import android.content.Context
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.util.*

class SQLiteHelper(private val context: Context) : SQLiteOpenHelper(context, databaseName, null, databaseVersion) {

    override fun onCreate(db: SQLiteDatabase) {
        try {
            for (file in getScriptsFiles(PATH_CREATE_SCRIPTS)) {
                executeScript(db, PATH_CREATE_SCRIPTS + File.separator + file)
            }
        } catch (e: Exception) {
            Log.e(TAG, "onCreate", e)
        }
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        try {
            for (file in getScriptsFiles(PATH_UPGRADE_SCRIPTS)) {
                try {
                    val version = file.replace(".sql", "").toInt()
                    if (version > oldVersion && version <= newVersion) {
                        executeScript(
                            db,
                            PATH_UPGRADE_SCRIPTS + File.separator + file
                        )
                    }
                } catch (e: NumberFormatException) {
                    Log.i(
                        TAG,
                        "Não é um script de migração: $file"
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "onUpgrade", e)
        }
    }

    override fun onDowngrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        //
    }

    private fun executeScript(db: SQLiteDatabase, pathFile: String) {
        try {
            val `is` = context.assets.open(pathFile)
            val reader = BufferedReader(InputStreamReader(`is`))
            val sb = StringBuilder()
            var line: String

            reader.forEachLine {
                line = it
                line = line.replace("\n", " ").replace("\t", " ").trim { it <= ' ' }
                if (!line.isEmpty()) sb.append(line).append(" ")
            }

            for (statement in getStatements(sb.toString())) {
                try {
                    if (!statement.isEmpty()) {
                        db.execSQL(statement)
                    }
                } catch (eSql: SQLException) {
                    Log.e(TAG, "Executar script", eSql)
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, if (e.message != null) e.message else e.toString())
        } catch (ex: Exception) {
            Log.e(TAG,if (ex.message != null) ex.message else ex.toString())
        }
    }

    @Throws(IOException::class)
    private fun getScriptsFiles(path: String): List<String> {
        val lista = context.assets.list(path)
        val files = if (lista != null) Arrays.asList(*lista) else ArrayList<String?>()
        Collections.sort(files)
        return files
    }

    private fun getStatements(content: String): Array<String> {
        return formataSql(content).split(";".toRegex()).toTypedArray()
    }

    private fun formataSql(sql: String): String {
        //Primeiro remove espaços duplos
        val value = sql.replace("  ", " ")
        return if (value.contains("  ")) formataSql(value) else  //Remove espaços desnecessários
            value.replace(" ;", ";")
                .replace(" ,", ",")
                .replace("( ", "(")
                .replace(" )", ")")
                .replace("(\\/\\*([\\s\\S]*?)\\*\\/)|(--(.)*)".toRegex(), "").trim { it <= ' ' }
    }

    companion object {
        private val TAG = SQLiteHelper::class.java.simpleName
        var databaseName = "VIACEP"
        const val databaseVersion = 1
        private const val PATH_CREATE_SCRIPTS = "database_scripts/create"
        private const val PATH_UPGRADE_SCRIPTS = "database_scripts/upgrade"
    }

}