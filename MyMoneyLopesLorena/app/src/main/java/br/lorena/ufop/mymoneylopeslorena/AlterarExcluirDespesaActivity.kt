package br.lorena.ufop.mymoneylopeslorena

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_alterar_excluir_despesa.*
import java.lang.NumberFormatException
import java.util.*

class AlterarExcluirDespesaActivity : Activity(), View.OnClickListener  {


    private val TAG = "AlterarDespesaActivity"
    private var firestoreDB: FirebaseFirestore? = null
    internal var id = ""
    var tipo = 0
    private lateinit var calendar: Calendar



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alterar_excluir_despesa)

        button_Alterar.setOnClickListener(this)
        button_Deletar.setOnClickListener(this)


        firestoreDB = FirebaseFirestore.getInstance()

        val bundle = intent.extras
        if (bundle != null) {

            id = (bundle.getString("UpdateId"))
            edit_Valor.setText((bundle.getString("UpdateValor").toDouble()*(-1)).toString())
            edit_Dia.setText(bundle.getString("UpdateDia"))
            edit_Mes.setText(bundle.getString("UpdateMes"))
            edit_Ano.setText(bundle.getString("UpdateAno"))
            edit_extra.setText(bundle.getString("UpdateDescricao"))
            tipo = (bundle.getInt("UpdateTipo"))

            if (tipo == 1) {
                button_alimentaçao.isChecked = true
            } else if (tipo == 2) {
                button_cartao.isChecked = true
            } else if (tipo == 3) {
                button_lazer.isChecked = true
            } else if (tipo == 4) {
                button_moradia.isChecked = true
            } else if (tipo == 5) {
                button_saude.isChecked = true
            } else {
                button_outros.isChecked = true
            }
        }

        calendar = Calendar.getInstance()

        edit_Dia.setOnClickListener(this)
        edit_Mes.setOnClickListener(this)
        edit_Ano.setOnClickListener(this)

    }
    override fun onClick(v: View) {

        val idB = v.id
        if (idB == R.id.button_Alterar) {

            val valor = edit_Valor.text.toString()
            val dia = edit_Dia.text.toString()
            val mes = edit_Mes.text.toString()
            val ano = edit_Ano.text.toString()
            val extra = edit_extra.text.toString()


            if (button_alimentaçao.isChecked) {
                tipo = 1
            } else if (button_cartao.isChecked) {
                tipo = 2
            } else if (button_lazer.isChecked) {
                tipo = 3
            } else if (button_moradia.isChecked) {
                tipo = 4
            } else if (button_saude.isChecked) {
                tipo = 5
            } else {
                tipo = 6
            }


            if (valor.isNotEmpty() && dia.isNotEmpty() && mes.isNotEmpty() && ano.isNotEmpty() && valor != "." &&
                valor.toDouble() >= 0 && dia.toInt() > 0 && dia.toInt() <= 31 && mes.toInt() > 0 && mes.toInt() <= 12 && ano.toInt() >= 2018
                && ano.length == 4 && tipo!=0 && tipo == 6 && extra.isNotEmpty() && extra != "" && id.isNotEmpty()) {
                try {
                  update(id, valor.toDouble(), dia.toInt(), mes.toInt(), ano.toInt(), extra, tipo)
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()

                } catch (nfe: NumberFormatException) {
                    Toast.makeText(applicationContext, getString(R.string.app_valoresvalidos), Toast.LENGTH_LONG)
                        .show()
                }
            }

            else if (valor.isNotEmpty() && dia.isNotEmpty() && mes.isNotEmpty() && ano.isNotEmpty() && valor != "." &&
                valor.toDouble() >= 0 && dia.toInt() > 0 && dia.toInt() <= 31 && mes.toInt() > 0 && mes.toInt() <= 12 && ano.toInt() >= 2018
                && ano.length == 4 && tipo!=0 && tipo !=6 &&id.isNotEmpty()) {
                try {
                    update(id, valor.toDouble(), dia.toInt(), mes.toInt(), ano.toInt(), extra, tipo)
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()

                } catch (nfe: NumberFormatException) {
                    Toast.makeText(applicationContext, getString(R.string.app_valoresvalidos), Toast.LENGTH_LONG)
                        .show()
                }
            }

            else {
                Toast.makeText(applicationContext, getString(R.string.app_validos), Toast.LENGTH_LONG)
                    .show()
            }

        } else if (idB == R.id.button_Deletar) {
            delete(id)

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        else if (idB == R.id.edit_Dia || idB == R.id.edit_Mes || idB == R.id.edit_Ano){
            val dateSetListener = DatePickerDialog.OnDateSetListener {
                    view, year, monthOfYear, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                edit_Dia.text = dayOfMonth.toString()
                edit_Mes.text = ((monthOfYear)+1).toString()
                edit_Ano.text = year.toString()
            }

            DatePickerDialog(this, dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

    }


   fun update(id: String, valor: Double, dia: Int, mes: Int, ano: Int, extra: String, tipo: Int) {
        val valor = HistoricoActivity(id,valor * -1, dia, mes, ano, extra, tipo).toMap()

        firestoreDB!!.collection("historico")
            .document(id)
            .set(valor)
            .addOnSuccessListener {
                Log.e(TAG, "Despesa alterada com sucesso")
                Toast.makeText(applicationContext, "Despesa alterada", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Erro", e)
                Toast.makeText(applicationContext, "Erro ao alterar despesa", Toast.LENGTH_SHORT).show()
            }
    }

    private fun delete(id: String) {
        firestoreDB!!.collection("historico").document(id).delete().isSuccessful
        Toast.makeText(this, "Despesa deletada com sucesso", Toast.LENGTH_SHORT).show()
    }
}