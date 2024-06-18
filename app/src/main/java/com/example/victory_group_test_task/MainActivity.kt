package com.example.victory_group_test_task

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.victory_group_test_task.databinding.ActivityMainBinding
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.DrivingOptions
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.directions.driving.DrivingRouter
import com.yandex.mapkit.directions.driving.DrivingRouterType
import com.yandex.mapkit.directions.driving.DrivingSession
import com.yandex.mapkit.directions.driving.VehicleOptions
import com.yandex.mapkit.directions.driving.VehicleType
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.mapview.MapView

class MainActivity : AppCompatActivity(){

    private var binding: ActivityMainBinding? = null

    private lateinit var mapView: MapView
    private var drivingRouter : DrivingRouter? = null
    private var drivingSession: DrivingSession? = null
    private var mapObjects: MapObjectCollection? = null
    private var drivingOptions: DrivingOptions? = null
    private var vehicleOptions: VehicleOptions? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)


        MapKitFactory.initialize(this)
        //setContentView(R.layout.activity_main)
        mapView = binding!!.mapView
        requestLocationPermission()
        val mapKit = MapKitFactory.getInstance()
        mapView.mapWindow.map.move(
            CameraPosition(
                DESTINATION,
                /* zoom = */ 17.0f,
                /* azimuth = */ 0.0f,
                /* tilt = */ 0.0f
            )
        )
        checkGPS()
        val locationOnMapKit = mapKit.createUserLocationLayer(mapView.mapWindow)
        locationOnMapKit.isVisible = true

        buildingRoute()
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
    }

    override fun onStop() {
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    private fun buildingRoute() {
        drivingRouter = DirectionsFactory.getInstance().createDrivingRouter(DrivingRouterType.ONLINE)
        mapObjects = mapView.mapWindow.map.mapObjects.addCollection()

        drivingOptions = DrivingOptions().apply {
            routesCount = 1
        }
        vehicleOptions = VehicleOptions().setVehicleType(VehicleType.TAXI)

        val points = buildList {
            add(RequestPoint(Point(56.777788, 60.663415), RequestPointType.WAYPOINT, null, null))
            add(RequestPoint(DESTINATION, RequestPointType.WAYPOINT, null, null))
        }

        val drivingRouteListener = object : DrivingSession.DrivingRouteListener {
            override fun onDrivingRoutes(drivingRoutes: MutableList<DrivingRoute>) {
                for(route in drivingRoutes) {
                    mapObjects!!.addPolyline(route.geometry)
                }
            }

            override fun onDrivingRoutesError(p0: com.yandex.runtime.Error) {
                val errorMessage = "Unknown error"
                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }

        drivingSession = drivingRouter!!.requestRoutes(
            points,
            drivingOptions!!,
            vehicleOptions!!,
            drivingRouteListener
        )
    }

    private fun requestLocationPermission(){
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION),
                0)
            return
        }
    }

    private fun isGPSEnabled(): Boolean {
        val lm = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun checkGPS(){
        if(!isGPSEnabled()){
            DialogManager.locationSettingsDialog(this, object : DialogManager.Listener{
                override fun onClick() {
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
            })
        }
    }

    companion object{
        private val DESTINATION = Point(56.833742, 60.635716)
    }

}