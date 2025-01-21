MainActivity.java
package com.example.app;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText etUsername = findViewById(R.id.etUsername);
        EditText etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                if (username.equals("amulya") && password.equals("amu")) {
                    Intent intent = new Intent(MainActivity.this, HospitalListActivity.class);
                    startActivity(intent);
                } 


else {
                    Toast.makeText(MainActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

HospitalListActivity.java
package com.example.app;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class HospitalListActivity extends AppCompatActivity {

    // Sample hospital data including latitude and longitude
    String[] hospitals = {"Manipal Hospitals", "Apollo Hospitals", "Aster Hospitals", "Medanta Hospitals", "Fortis Hospitals", "Profile Hospitals", "M S Ramaiah Hospitals", "Citi Hospital", "Max Hospital", "Sparsh Hospital"};
    double[] hospitalLatitudes = {12.9716, 13.0827, 12.9719, 28.6139, 13.0878, 12.9621, 13.0027, 12.9544, 12.9715, 13.0105}; // Example latitudes
    double[] hospitalLongitudes = {77.5946, 80.2707, 77.5949, 77.2090, 80.2785, 77.5868, 77.5972, 77.6581, 77.5944, 77.5611}; // Example longitudes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_list);

        ListView lvHospitals = findViewById(R.id.lvHospitals);

        // Creating the adapter to display hospitals in a ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, hospitals);
        lvHospitals.setAdapter(adapter);

        // Set the OnItemClickListener to handle hospital selection
        lvHospitals.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Create an Intent to navigate to HospitalLocationActivity
                Intent intent = new Intent(HospitalListActivity.this, HospitalLocationActivity.class);

                // Pass hospital details (latitude, longitude, hospital name) to the new activity
                intent.putExtra("hospitalLat", hospitalLatitudes[position]);
                intent.putExtra("hospitalLng", hospitalLongitudes[position]);
                intent.putExtra("hospitalName", hospitals[position]);

                // Start the HospitalLocationActivity
                startActivity(intent);
            }
        });
    }
}






HospitalLocationActivity.java
package com.example.app;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class HospitalLocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap googleMap;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private double hospitalLat = 12.9716; // Example Latitude
    private double hospitalLng = 77.5946; // Example Longitude
    private String hospitalName = "Manipal Hospital";
    private Button btnNavigate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location);

        mapView = findViewById(R.id.mapView);
        btnNavigate = findViewById(R.id.btnNavigate);  // Initialize Navigate button

        // Initialize the MapView
        Bundle mapViewBundle = savedInstanceState != null ? savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY) : null;
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        // Get the hospital details from the intent
        hospitalLat = getIntent().getDoubleExtra("hospitalLat", 0);
        hospitalLng = getIntent().getDoubleExtra("hospitalLng", 0);
        hospitalName = getIntent().getStringExtra("hospitalName");

        // Set up the button click listener to open Google Maps for navigation
        btnNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGoogleMapsNavigation(hospitalLat, hospitalLng);
            }
        });

        // Use Handler to delay transitioning to the DoctorListActivity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // After the map is displayed for a short time, navigate to DoctorListActivity
                Intent intent = new Intent(HospitalLocationActivity.this, DoctorListActivity.class);
                intent.putExtra("hospital", hospitalName); // Pass the selected hospital name
                startActivity(intent);
                finish(); // Finish the current activity to prevent the user from coming back
            }
        }, 30000); // 3 seconds delay
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        LatLng hospitalLocation = new LatLng(hospitalLat, hospitalLng);
        googleMap.addMarker(new MarkerOptions().position(hospitalLocation).title(hospitalName));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hospitalLocation, 15));
    }

    private void openGoogleMapsNavigation(double latitude, double longitude) {
        // Create the URI to open Google Maps with the given coordinates
        String uri = "google.navigation:q=" + latitude + "," + longitude;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            // Handle the case where Google Maps is not installed
            Toast.makeText(this, "Google Maps is not installed.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }
        mapView.onSaveInstanceState(mapViewBundle);
    }
}





BookAppointmentActivity.java
package com.example.app;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BookAppointmentActivity extends AppCompatActivity {

    private String doctorName;
    private String specialization;
    private String hospitalName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);

        // Retrieve doctor's name, specialization, and hospital name from the intent
        doctorName = getIntent().getStringExtra("doctor");
        specialization = getIntent().getStringExtra("specialization");
        hospitalName = getIntent().getStringExtra("hospital");

        // Initialize views
        TextView tvDoctorDetails = findViewById(R.id.tvDoctorDetails);
        EditText etPatientName = findViewById(R.id.etPatientName);
        Button btnBookAppointment = findViewById(R.id.btnBookAppointment);

        // Display doctor's name and specialization
        tvDoctorDetails.setText("Doctor: " + doctorName + " (" + specialization + ")");

        // Handle button click for booking appointment
        btnBookAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String patientName = etPatientName.getText().toString();

                if (patientName.isEmpty()) {
                    Toast.makeText(BookAppointmentActivity.this, "Please enter patient name", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Get current date and time
                String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

                // Show booking confirmation message
                Toast.makeText(
                        BookAppointmentActivity.this,
                        "Appointment booked for " + patientName + "\nDoctor: " + doctorName + "\nSpecialization: " + specialization + "\nHospital: " + hospitalName + "\nDate & Time: " + currentDateTime,
                        Toast.LENGTH_LONG
                ).show();

                // Create an Intent to navigate to AppointmentSuccessActivity
                Intent intent = new Intent(BookAppointmentActivity.this, AppointmentSuccessActivity.class);
                intent.putExtra("doctor", doctorName);
                intent.putExtra("specialization", specialization);
                intent.putExtra("hospital", hospitalName);
                intent.putExtra("patientName", patientName);
                intent.putExtra("appointmentTime", currentDateTime);

                // Start the AppointmentSuccessActivity
                startActivity(intent);
                finish();  // Close the current activity
            }
        });
    }
}
AppointmentSuccessActivity.java
package com.example.app;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AppointmentSuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_success);

        // Get appointment details passed from the previous activity
        String doctorName = getIntent().getStringExtra("doctor");
        String specialization = getIntent().getStringExtra("specialization");
        String hospitalName = getIntent().getStringExtra("hospital");
        String patientName = getIntent().getStringExtra("patientName");
        String appointmentTime = getIntent().getStringExtra("appointmentTime");

        // Initialize views
        TextView tvSuccessMessage = findViewById(R.id.tvSuccessMessage);

        // Display the successful appointment details
        String successMessage = "Appointment Booked Successfully!\n\n" +
                "Patient: " + patientName + "\n" +
                "Doctor: " + doctorName + " (" + specialization + ")\n" +
                "Hospital: " + hospitalName + "\n" +
                "Date & Time: " + appointmentTime;

        tvSuccessMessage.setText(successMessage);
    }
}
