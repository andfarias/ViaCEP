package br.com.viacep.entities

import java.io.Serializable

class Endereco(var cep: String? = null,
               var logradouro: String? = null,
               var complemento: String? = null,
               var bairro: String? = null,
               var localidade: String? = null,
               var uf: String? = null,
               var ibge: String? = null,
               var gia: String? = null,
               var ddd: String? = null,
               var siafi: String? = null) : Serializable