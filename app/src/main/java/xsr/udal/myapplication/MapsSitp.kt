package xsr.udal.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MapsSitp : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private val sitpRute = mutableListOf<Feature>()
    private lateinit var adapter: SitpAdapter

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        generate_consult()
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://gis.transmilenio.gov.co/arcgis/rest/services/Zonal/consulta_rutas_zonales/FeatureServer/0/")
            .addConverterFactory((MoshiConverterFactory.create()))
            .build()
    }

    private fun searchByWhere(query:String){
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(ApiService::class.java).getSitpByFilter(query)
            val sitps = call.body()
            runOnUiThread{
                if(call.isSuccessful){
                    println("callsitps")
                    println(sitps)
                    println(sitps?.features)
                    val sitp = sitps?.features
                    createPolylines(sitp)
                } else {
                    showErrror()
                }
            }
        }
    }

    private fun showErrror() {
        Toast.makeText(this, "Filtro con valores erroneos", Toast.LENGTH_SHORT).show()
    }

    private fun generate_consult() {
        val extras = intent.extras
        val busquedaname = extras?.getString("usuario")?:"sin usuario"
//        println("bundlebbb")
//        println(busquedaname)
//        val busquedaname : bundle!!.gerString("usuario")
        var url_base = "https://gis.transmilenio.gov.co/arcgis/rest/services/Zonal/consulta_rutas_zonales/FeatureServer/0/query?returnGeometry=true&outSR=4326&f=json&outFields=*&where=route_name_ruta_zonal="
        var url_where = "'$busquedaname'"
//        println(String.format("%s%s", url_base, url_where))
        searchByWhere(String.format("%s%s", url_base, url_where))
    }


    private fun createPolylines(sitps: List<Feature>?){
        var latinit = 40.419173113350965
        var lonInit = -3.705976009368897
        var polylineOptions = PolylineOptions()
        if (!sitps.isNullOrEmpty()){
            lonInit = sitps.get(0).geometry.paths.get(0).get(0).get(0)
            latinit = sitps.get(0).geometry.paths.get(0).get(0).get(1)
            for (item in sitps.get(0).geometry.paths.get(0)) {
                polylineOptions.add(LatLng(
                    item.get(1),
                    item.get(0)
                ))
            }
        }

        val initPlace = LatLng(latinit,lonInit)
        polylineOptions.width(15f)
        polylineOptions.color(ContextCompat.getColor(this, R.color.kotlin))
        map.addPolyline(polylineOptions)
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(initPlace, 12f),
            4000,
            null
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps_sitp)
        createMapFragment()

    }

    private fun createMapFragment() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }




}