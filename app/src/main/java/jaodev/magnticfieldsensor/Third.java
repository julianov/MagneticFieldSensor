package jaodev.magnticfieldsensor;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class Third extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener ,SensorEventListener {


    private Sensor mAccelerometer;

    ProgressBar px, py, pz;

    float []x;
    float [] y;
    float [] z;
    boolean inicio;

    private Sensor sensorMagnetico;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////COMIENZA ON CREATE////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        px= (ProgressBar) findViewById(R.id.progressBar31);
        py= (ProgressBar) findViewById(R.id.progressBar32);
        pz= (ProgressBar) findViewById(R.id.progressBar33);

        px.setMax(20);
        py.setMax(20);
        pz.setMax(20);


        x= new float[50];
        y= new float [50];
        z= new float [50];

        for(int i=0;i<50;i++)
        {
            x[i]=0;
            y[i]=0;
            z[i]=0;
        }

        SensorManager sensorManager = null;
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorMagnetico=null;
        sensorMagnetico = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if(sensorMagnetico == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Your device does not have a magnetic sensor.")
                    .setTitle("Attention!")
                    .setCancelable(false)
                    .setNeutralButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();

        }

    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////FIN onCreate//////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.third, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_timeDomain) {
            Intent i = new Intent(Third.this, MainActivity.class);
            startActivity(i);
            this.finish();
        } else if (id == R.id.nav_frecuencyDomain) {
            Intent i = new Intent(Third.this, Second.class);
            startActivity(i);
            this.finish();

        } else if (id == R.id.nav_Direction) {

        } else if (id == R.id.nav_howto) {
            Intent i = new Intent(Third.this, Four.class);
            startActivity(i);
            this.finish();
        } else if (id == R.id.nav_share) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Magnetic Field Sensor");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Magnetic Field Sensor app clicke here to visit https://play.google.com/store/apps/details?id=jaodev.magnticfieldsensor");
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        } else if (id == R.id.nav_calify) {
            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=jaodev.magnticfieldsensor");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////Comienzo Sensado///////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onSensorChanged(SensorEvent event) {

        float max1=0;
        float max2=0;
        float max3=0;

        float [] values = event.values;  //0 eje x - 1 eje y - 2 eje z

        for(int i=0;i<49;i++)
        {
            x[i]=x[i+1];
            max1+=x[i];
        }
        max1+=(float)values[0];
        x[49]= (float) values[0];
        //graficar(grafica1, valoresGrafica1, max);

        for(int i=0;i<49;i++)
        {
            y[i]=y[i+1];
            max2+=y[i];
        }
        max2+=(float)values[1];
        y[49]= (float) values[1];

        for(int i=0;i<49;i++)
        {
            z[i]=z[i+1];
            max3+=z[i];
        }
        max3+=(float)values[2];
        z[49]= (float) values[2];

        px.setProgress((int)max1/50);
        py.setProgress((int)max2/50);
        pz.setProgress((int)max3/50);




    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    protected void onResume() {
        super.onResume();
        //Obtiene los datos del sensor.
        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        //obtengo solamente datos del sensor, en este caso es el acelerometro
        List<Sensor> sensors = sm.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);
        if (sensors.size() > 0) //si el dispositivo android tiene acelerometro
        {
            sm.registerListener((SensorEventListener) this, sensors.get(0), SensorManager.SENSOR_DELAY_FASTEST); //sensor_delay_fastest obtiene los cambios los más rápidos posibles
        }
    }

    protected void onPause() {
        super.onPause();
        SensorManager mSensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorManager.unregisterListener((SensorEventListener) this, mAccelerometer);
        super.onPause();
    }
    protected void onStop()
    {
        super.onStop();
        SensorManager mSensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorManager.unregisterListener((SensorEventListener) this, mAccelerometer);
        super.onStop();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////FIN SENSADO////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////

}
