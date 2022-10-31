package xsr.udal.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import xsr.udal.myapplication.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var twoFilter = true
    private lateinit var adapter: SitpAdapter
    private val sitpRutes = mutableListOf<Feature>()

    private fun getRetrofit(): Retrofit{
        return Retrofit.Builder()
            .baseUrl("https://gis.transmilenio.gov.co/arcgis/rest/services/Zonal/consulta_rutas_zonales/FeatureServer/0/")
            .addConverterFactory((MoshiConverterFactory.create()))
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        setContentView(R.layout.activity_main)


        initRecyclerView()

        var btn_clean: Button = findViewById(R.id.btn_clean_filter)
        var btn_filter: Button = findViewById(R.id.btn_find_filter)

        var txt_name: EditText = findViewById(R.id.input_filter_name)
        var txt_locate: EditText = findViewById(R.id.input_filter_locate)
        var txt_locality: EditText = findViewById(R.id.input_filter_locality)
        var txt_zone: EditText = findViewById(R.id.input_filter_zone)

        fun validate_where(text: String): String {
            println("validate_where")
            if (this.twoFilter) {
                this.twoFilter = false
                return text
            } else {
                var test_end = " AND (" + text + ")"
                return test_end
            }
        }

        btn_clean.setOnClickListener{
            val intent = Intent(this, MapsSitp::class.java)
            startActivity(intent)
        }

        btn_filter.setOnClickListener {
            var var_name = txt_name.text.toString()
            var var_locale =txt_locate.text.toString()
            var var_locality =txt_locality.text.toString()
            var var_zone = txt_zone.text.toString()
            var url_base = "https://gis.transmilenio.gov.co/arcgis/rest/services/Zonal/consulta_rutas_zonales/FeatureServer/0/query?returnGeometry=false&outSR=&f=json"
            var url_where = "&outFields=*&where="

            println("EVENTO PERRO btn_filter" + var_name + var_locale + var_locality + var_zone)

            this.twoFilter = true

            if (var_name.isNotEmpty()) {
//                println("var_name empty")
                var completeTextUrl = "route_name_ruta_zonal='" + var_name + "'"
                completeTextUrl = validate_where(completeTextUrl)
                url_where = url_where + completeTextUrl
            }
            if (var_locale.isNotEmpty()) {
//                println("var_locale empty")
                var completeTextUrl = "origen_ruta_zonal LIKE '" + var_locale + "%' OR destino_ruta_zonal LIKE '" + var_locale + "%'"
                completeTextUrl = validate_where(completeTextUrl)
                url_where = url_where + completeTextUrl
            }
            if (var_locality.isNotEmpty()) {
//                println("var_locality empty")
                var completeTextUrl = "localidad_origen_ruta_zonal = " + var_locality + " OR localidad_destino_ruta_zonal = " + var_locality
                completeTextUrl = validate_where(completeTextUrl)
                url_where = url_where + completeTextUrl

            }
            if (var_zone.isNotEmpty()) {
//                println("var_zone empty")
//                println(String.format("%s%s", url_base, url_where))
                var completeTextUrl = "zona_origen_ruta_zonal = " + var_zone + " OR zona_destino_ruta_zonal = " + var_zone
                completeTextUrl = validate_where(completeTextUrl)
                url_where = url_where + completeTextUrl
            }

            println(String.format("%s%s", url_base, url_where))

            searchByWhere(String.format("%s%s", url_base, url_where))

        }
    }

    private fun searchByWhere(query:String){
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(ApiService::class.java).getSitpByFilter(query)
            val sitps = call.body()
            runOnUiThread{
                if(call.isSuccessful){
                    val sitp = sitps?.features ?: emptyList()
                    sitpRutes.clear()
                    sitpRutes.addAll(sitp)
                    println("sitp")
                    println(sitp)
                    adapter.notifyDataSetChanged()
                } else {
                    showErrror()
                }
            }

        }
    }

    private fun showErrror() {
        Toast.makeText(this, "Filtro con valores erroneos", Toast.LENGTH_SHORT).show()
    }

    private fun initRecyclerView() {
        adapter = SitpAdapter(sitpRutes, this)
        binding.rvSitp.layoutManager = LinearLayoutManager(this)
        binding.rvSitp.adapter = adapter
    }

}
