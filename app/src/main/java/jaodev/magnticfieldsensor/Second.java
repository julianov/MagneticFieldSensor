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

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.navigation.NavigationView;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Second extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SensorEventListener {

    ArrayList<Entry> yValues ;
    float [] valoresGrafica ;
    float [] fft;
    long [] tiempos;
    float maximovalor;
    int cantidadValoresGrafica;

    int flag;

    LineData data;
    LineChart grafica;

    TextView tv;
    TextView frecuenciMuestreo;

    private static final String TOAST_TEXT = "Few seconds to load data";

    private Sensor sensorMagnetico;

    float valor_anterior;

    long ahora_mismo;

    float tiempo;

    ///////////////////////////COMIENZA on Create
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //toast message
        Toast.makeText(this, TOAST_TEXT, Toast.LENGTH_LONG).show();

        tv=(TextView) findViewById(R.id.textView22);
        frecuenciMuestreo=(TextView) findViewById(R.id.textView10);

        flag=0;
        valoresGrafica = new float[300];
        fft = new float[150];
        maximovalor=0;
        tiempos = new long[300];

        for(int i=0;i<300;i++)
        {
            valoresGrafica[i]=0;
            tiempos[i]=0;
        }
        //Comenzamos con la fráfica


        grafica= findViewById(R.id.LineChart2);
        // grafica.setOnChartGestureListener(MainActivity.this);
        //grafica.setOnChartValueSelectedListener(MainActivity.this);
        grafica.setDragEnabled(true);
        grafica.getDescription().setEnabled(false); //set invisible description label

        yValues = new ArrayList<>();

        cantidadValoresGrafica=0;
        graficarIni();

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
        valor_anterior=0;

        ahora_mismo=System.currentTimeMillis();


        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        //obtengo solamente datos del sensor, en este caso es el acelerometro
        List<Sensor> sensors = sm.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);
        if (sensors.size() > 0) //si el dispositivo android tiene acelerometro
        {
            sm.registerListener((SensorEventListener) this, sensors.get(0), SensorManager.SENSOR_DELAY_FASTEST); //sensor_delay_fastest obtiene los cambios los más rápidos posibles
        }

    }

    ////////////////////////FIN onCreate

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
        getMenuInflater().inflate(R.menu.second, menu);
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
            Intent i = new Intent(Second.this, MainActivity.class);
            startActivity(i);
            this.finish();
        } else if (id == R.id.nav_frecuencyDomain) {

        } else if (id == R.id.nav_Direction) {
            Intent i = new Intent(Second.this, Third.class);
            startActivity(i);
            this.finish();
        } else if (id == R.id.nav_howto) {
            Intent i = new Intent(Second.this, Four.class);
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

    //////Comienza sensado
    @Override
    public void onSensorChanged(SensorEvent event) {

            float[] values = event.values;

            float valor, aux;
            valor=(float) Math.sqrt(Math.pow(values[0], 2) + Math.pow(values[1], 2) + Math.pow(values[2], 2));
           // aux=valor;
            //valor-=0.75*valor_anterior;

            valoresGrafica[cantidadValoresGrafica] = valor;
            tiempos[cantidadValoresGrafica] = (System.currentTimeMillis()- ahora_mismo);

            /*    long tiempo=System.currentTimeMillis();
                System.out.println("tiempo: "+tiempo);
                System.out.println("ahora: "+ahora_mismo);
                long tiempo2=  ((tiempo-ahora_mismo));
                System.out.println("cantidad valor grafica: "+tiempo2);
*/




            ahora_mismo=System.currentTimeMillis();

        if (cantidadValoresGrafica == 299) {
                fft();
                graficar();
                cantidadValoresGrafica = 0;

        }else{
            cantidadValoresGrafica++;

        }
           // valor_anterior=aux;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    protected void onResume() {
        super.onResume();
        SensorManager sm2 = (SensorManager) getSystemService(SENSOR_SERVICE);
        //obtengo solamente datos del sensor, en este caso es el acelerometro
        List<Sensor> sensors = sm2.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);
        if (sensors.size() > 0) //si el dispositivo android tiene acelerometro
        {
            sm2.registerListener((SensorEventListener) this, sensors.get(0), SensorManager.SENSOR_DELAY_FASTEST); //sensor_delay_fastest obtiene los cambios los más rápidos posibles
        }
    }

    protected void onPause() {
        super.onPause();
        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        sm.unregisterListener(this,sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD));
    }
    protected void onStop()
    {
        super.onStop();
        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        sm.unregisterListener(this,sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD));
        super.onStop();
    }

    /////////////////////////FIN sensado

    public void fft() {

        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        sm.unregisterListener(this,sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD));

        double sum;
        double sumR=0;
        double sumIm=0;
        float max=0;
        tiempo=0;
        for(int i=0;i<300;i++)
        {
            max+=valoresGrafica[i];
            tiempo+=tiempos[i];

        }

        max=max/300;
        tiempo=(tiempo/300);


        // en realidad la frecuencia de muestreo es 1/(0.001*tiempo)
        // de esa manera la i debe ir de 0 a  (1/(0.001*tiempo) )/2
        // y la j debe ir de 0 a (1/(0.001*tiempo) )/2antes

        System.out.println("el tiempo de muestreo es:"+Math.round(tiempo));
        for(int i=0;i<150;i++) {
            for (int j = 0; j < 300; j++) {
                sumR=sumR+ (  valoresGrafica[j]-(max))*Math.cos(- ((2*Math.PI)/(300))* j *i);
                sumIm=sumIm+( valoresGrafica[j]-(max))*Math.sin(- ((2*Math.PI)/(300)) *j *i);
            }
            sum=Math.sqrt(Math.pow(sumR,2)+Math.pow(sumIm,2));

            fft[i]= (float) ( (sum));

            sumR=0;
            sumIm=0;
        }

        SensorManager sm2 = (SensorManager) getSystemService(SENSOR_SERVICE);
        //obtengo solamente datos del sensor, en este caso es el acelerometro
        List<Sensor> sensors = sm2.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);
        if (sensors.size() > 0) //si el dispositivo android tiene acelerometro
        {
            sm2.registerListener((SensorEventListener) this, sensors.get(0), SensorManager.SENSOR_DELAY_FASTEST); //sensor_delay_fastest obtiene los cambios los más rápidos posibles
        }

        for(int i=0;i<300;i++)
        {
            tiempos[i]=0;
        }

    }

    ///////////////////////////funcion graficar
    public void graficar()
    {
        maximovalor=0;
        grafica.clear();

    /*   float max=0;
        for(int i=0;i<1024;i++)
        {
            max+=fft[i];
        }
        max=max/1024;*/
        yValues.clear();
        //200 is the sample rate eso lo supongo

        int position=0;

        for (int i=0;i<150;i++)
        {
            if(fft[i]>maximovalor){
                maximovalor=fft[i];
                position=i;
            }

        }
        for(int i=0;i<150;i++)
        {
            yValues.add(new Entry((int) (i*0.44), fft[i]/maximovalor)) ;

        }
        LineDataSet set1 = new LineDataSet(yValues,"Amplitude - Hz");
        set1.setFillAlpha(110);
        //set1.setColor(Color.RED); para que la grÃ¡fica sea roja
        set1.setLineWidth(3f); //cambiamos el grosor de la linea
        set1.setCircleRadius(1);
        set1.setDrawValues(false);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        LineData data = new LineData(dataSets);

        //seteamos los mÃ¡ximos y mÃ­nimos de la grafica

        //eliminamos el eje y derecho, asÃ­ nos queda solamente el izquierdo.
        YAxis rightAxis = grafica.getAxisRight();
        rightAxis.setEnabled(false);

        grafica.setData(data);


        float frecuencia_de_muestreo= (float) (1/(0.001*tiempo));

        String frecuencia="Sample frecuency: "+ String.format("%.2f",frecuencia_de_muestreo);

        //int nueva_posicion= (int) (position*((frecuencia_de_muestreo)/(300)));
        int nueva_posicion= (int) 0.44*position;
        String texto="Peak at " + String.valueOf(nueva_posicion)+ " Hz";
        tv.setText(texto);
        frecuenciMuestreo.setText(frecuencia);


    }


    ///fin funcion graficar



    public void graficarIni()
    {

        for(int i=0;i<70;i++)
        {
            yValues.add(new Entry(i, 0)) ;

        }
        LineDataSet set1 = new LineDataSet(yValues,"Amplitude - Hz");
        set1.setFillAlpha(110);
        //set1.setColor(Color.RED); para que la grÃ¡fica sea roja
        set1.setLineWidth(3f); //cambiamos el grosor de la linea
        set1.setCircleRadius(1);
        set1.setDrawValues(false);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        LineData data = new LineData(dataSets);



        //seteamos los mÃ¡ximos y mÃ­nimos de la grafica


        //eliminamos el eje y derecho, asÃ­ nos queda solamente el izquierdo.
        YAxis rightAxis = grafica.getAxisRight();
        rightAxis.setEnabled(false);

        grafica.setData(data);

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

}
