package br.com.viacep.util

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class JsonUtil {

    fun isJson(Json: String?): Boolean {
        try {
            JSONObject(Json)
        } catch (ex: JSONException) {
            try {
                JSONArray(Json)
            } catch (ex1: JSONException) {
                return false
            }
        }
        return true
    }
}