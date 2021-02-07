package br.com.viacep.database.dao

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import br.com.viacep.entities.Endereco

open class EnderecoDAO(private var db: SQLiteDatabase) {

    private val nomeTabela = "ENDERECO"

    fun exists(entity: Endereco): Boolean {
        val sql = "SELECT COUNT(1) FROM $nomeTabela WHERE CD_CEP = ?"
        db.rawQuery(sql, arrayOf(entity.cep)).use {
            if (it != null) {
                return it.moveToFirst() && it.getLong(0) > 0L
            }
        }
        return false
    }

    private fun fillContentValues(entity: Endereco): ContentValues {
        ContentValues().run {
            put("CD_CEP", entity.cep)
            put("DS_LOGRADOURO ", entity.logradouro)
            put("DS_COMPLEMENTO", entity.complemento)
            put("NM_BAIRRO", entity.bairro)
            put("NM_LOCALIDADE ", entity.localidade)
            put("DS_UF", entity.uf)
            put("DS_IBGE", entity.ibge)
            put("DS_GIA", entity.gia)
            put("DS_DDD", entity.ddd)
            put("DS_SIAFI", entity.siafi)
            return this
        }
    }

    fun insertOrUpdate(entity: Endereco) {
        val values = fillContentValues(entity)
        if (exists(entity)) {
            db.update(nomeTabela, values, "CD_CEP = ?",  arrayOf(entity.cep))
        } else {
            db.insert(nomeTabela, null, values)
        }
    }

}