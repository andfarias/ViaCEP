package br.com.viacep

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteException
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.viacep.database.dao.EnderecoDAO
import br.com.viacep.database.sqlite.SQLiteHelper
import br.com.viacep.entities.Endereco
import br.com.viacep.ui.endereco.EnderecoDetailsActivity
import br.com.viacep.util.JsonUtil
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.intentFor
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class MainActivity : AppCompatActivity() {
    var context: Context = this
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun buscaCEP(v: View) {
        val conector = ConectorServico()
        val cep = edCEP.text.toString()
        conector.execute(cep)
    }

    @SuppressLint("StaticFieldLeak")
    internal inner class ConectorServico :
        AsyncTask<String?, Void?, String>() {

        override fun doInBackground(vararg params: String?): String {
            return try {
                if (params[0].isNullOrEmpty())
                    return "Informe o CEP"

                val ender = URL("https://viacep.com.br/ws/" + params[0] + "/json")
                val con = ender.openConnection() as HttpsURLConnection
                var json = ""

                if (con.responseCode == 200) { //OK
                    val input = BufferedReader(InputStreamReader(con.inputStream))
                    var linha: String?

                    do {
                        linha = input.readLine()
                        if (linha != null) {
                            json = json + linha
                        }
                    } while (linha != null)
                } else {
                    if (con.responseCode == 400) {
                        return "ERRO: CEP incorreto. Verifique"
                    }
                }
                con.disconnect()
                json
            } catch (t: Throwable) {
                throw Throwable(t.message ?: "ERRO: Falha na conexão:${t.message}".trimIndent())
            }
        }

        override fun onPostExecute(result: String) {
            val cnt = SQLiteHelper(context)
            var enderecoDAO = EnderecoDAO(cnt.writableDatabase)

            if (JsonUtil().isJson(result)) {
                val endereco = Gson().fromJson(result, Endereco::class.java)

                //verifica se já existe o endereço salvo no bd para inserir
                if (!enderecoDAO.exists(endereco)) {
                    try {
                        enderecoDAO.insertOrUpdate(endereco)
                    } catch (e: SQLiteException) {
                        throw Throwable(e.message ?: e.toString())
                    }
                }

                startActivity(intentFor<EnderecoDetailsActivity>(EnderecoDetailsActivity.KEY_ENDERECO to endereco))
            } else {
                Toast.makeText(context, result, Toast.LENGTH_LONG).show()
            }
        }
    }

}