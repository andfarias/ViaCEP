package br.com.viacep.ui.endereco

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.com.viacep.R
import br.com.viacep.entities.Endereco
import kotlinx.android.synthetic.main.activity_endereco.*

class EnderecoDetailsActivity : AppCompatActivity() {

    private var endereco: Endereco? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_endereco)

        if (intent.hasExtra(KEY_ENDERECO)) {
            endereco = intent.getSerializableExtra(KEY_ENDERECO) as Endereco

            endereco?.let { endereco ->
                txt_cep.text = endereco.cep
                txt_logradouro.text = endereco.logradouro
                txt_bairro.text = endereco.bairro
                txt_complemento.text = endereco.complemento
                txt_localidade.text = endereco.localidade
                txt_uf.text = endereco.uf
            }
        }
        if (endereco == null) {
            finish()
        }
    }

    companion object {
        const val KEY_ENDERECO = "endereco_details_key"
    }
}