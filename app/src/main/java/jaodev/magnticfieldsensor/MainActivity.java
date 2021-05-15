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
import android.view.View;
import android.view.WindowManager;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;


import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SensorEventListener {

    private Sensor mAccelerometer;

    ArrayList<Entry> yValues;
    float[] valoresGrafica;
    int flag;

    LineData data;
    LineChart grafica;

    TextView tv, tv2;

    private ShareActionProvider mShareActionProvider;

    float MaxRegistered;

    private Sensor sensorMagnetico;

    double valor_anterior;

    CardView cardView;
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////Comienzo on Create///////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        flag = 0;
        valoresGrafica = new float[150];


        //Comenzamos con la fráfica

        grafica = findViewById(R.id.LineChart);
        // grafica.setOnChartGestureListener(MainActivity.this);
        //grafica.setOnChartValueSelectedListener(MainActivity.this);
        grafica.setDragEnabled(true);
        grafica.getDescription().setEnabled(false); //set invisible description label
        grafica.setAlpha(0);

        yValues = new ArrayList<>();

        tv = (TextView) findViewById(R.id.textView15);
        tv2 = (TextView) findViewById(R.id.textView14);

        cardView= (CardView) findViewById(R.id.cardView);
        cardView.setVisibility(View.INVISIBLE);

        MaxRegistered = 0;

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

        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        valor_anterior=0;

    }
    ////////////////////////////////////////////////////FIN onCreate

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
        getMenuInflater().inflate(R.menu.main, menu);
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

        } else if (id == R.id.nav_frecuencyDomain) {
            Intent i = new Intent(MainActivity.this, Second.class);
            startActivity(i);
            this.finish();
        } else if (id == R.id.nav_Direction) {
            Intent i = new Intent(MainActivity.this, Third.class);
            startActivity(i);
            this.finish();
        } else if (id == R.id.nav_howto) {
            Intent i = new Intent(MainActivity.this, Four.class);
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


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////comienzo Sensado y grafica//////////////////////////////////////////////
////////////LA GRAFICA SE REALIZA EN EL MISMO SENSADO ASÍ COMO LA OBTENCIÓN DE LOS VALORES MAXIMOS///////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void onSensorChanged(SensorEvent event) {

            float max = 0;
            float[] values = event.values;
            double valor;
            double aux;

            valor = (Math.sqrt(Math.pow(values[0], 2) + Math.pow(values[1], 2) + Math.pow(values[2], 2)));
            aux=valor;
            valor-=0.75*valor_anterior;

            //valor= values[0];
            grafica.clear();
            new Thread(new Runnable() {
                public void run() {
                //Aquí ejecutamos nuestras tareas costosas
            }
            }).start();
            for (int i = 0; i < 149; i++) {
                valoresGrafica[i] = valoresGrafica[i + 1];
                max += valoresGrafica[i];
            }
            max += (float) valor;
            valoresGrafica[149] = (float) valor;
            if (flag < 150)
                flag++;

            yValues.clear();
            for (int i = 0; i < 150; i++) {
                yValues.add(new Entry(i, valoresGrafica[i] - (max / 150)));

            }
            LineDataSet set1 = new LineDataSet(yValues, "Values on uT");
            set1.setFillAlpha(110);
            //set1.setColor(Color.RED); para que la gráfica sea roja
            set1.setLineWidth(3f); //cambiamos el grosor de la linea
            set1.setCircleRadius(1);
            set1.setDrawValues(false);

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            LineData data = new LineData(dataSets);

            XAxis xAxis = grafica.getXAxis();
            xAxis.setEnabled(false);


            float maximo = maxValue(valoresGrafica, max / 150);
            float minimo = minValue(valoresGrafica, max / 150);

            if (maximo < 0.1 && minimo < 0.1) {
                grafica.getAxisLeft().setAxisMaximum((float) 5);
                grafica.getAxisLeft().setAxisMinimum((float) -5);
            } else if (maximo > (-minimo)) {
                grafica.getAxisLeft().setAxisMaximum(maximo + 5);
                grafica.getAxisLeft().setAxisMinimum(-maximo - 5);
            } else {
                grafica.getAxisLeft().setAxisMaximum(-minimo + 5);
                grafica.getAxisLeft().setAxisMinimum(minimo - 5);
            }
            //seteamos los máximos y mínimos de la grafica

            //eliminamos el eje y derecho, así nos queda solamente el izquierdo.
            YAxis rightAxis = grafica.getAxisRight();
            rightAxis.setEnabled(false);

            if (flag > 149) {
                grafica.setAlpha(1);
                cardView.setVisibility(View.VISIBLE);

                grafica.setData(data);

                float valorMaximo = 0;
                for (int i = 0; i < 150; i++) {
                    if ((valoresGrafica[i] - (max / 150)) > valorMaximo) {
                        valorMaximo = valoresGrafica[i] - (max / 150);
                    }
                    if ((valoresGrafica[i] - (max / 150)) > MaxRegistered) {
                        MaxRegistered = valoresGrafica[i] - (max / 150);
                    }
                }
                String texto = "Peak " + String.format("%.2f", valorMaximo);
                String texto2 = "Max Peak registered " + String.format("%.2f", MaxRegistered);
                tv.setText(texto);
                tv2.setText(texto2);
            }

            valor_anterior=aux;
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public float maxValue(float[] valores, float max) {
        float maximo = 0;
        for (int i = 0; i < 150; i++) {
            if (valores[i] - (max) > maximo)
                maximo = valores[i] - (max);
        }
        return maximo;
    }


    public float minValue(float[] valores, float max) {
        float min = 0;
        for (int i = 0; i < 150; i++) {
            if (valores[i] - (max) < min)
                min = valores[i] - (max);
        }
        return min;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////fIN DE SENSADO Y DE LA GRAFICA//////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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

        SensorManager mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorManager.unregisterListener((SensorEventListener) this, mAccelerometer);
        super.onPause();
    }

    protected void onStop() {
        super.onStop();
        SensorManager mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorManager.unregisterListener((SensorEventListener) this, mAccelerometer);
        super.onStop();
    }



}