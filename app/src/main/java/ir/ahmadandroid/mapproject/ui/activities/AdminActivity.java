package ir.ahmadandroid.mapproject.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.time.Period;

import ir.ahmadandroid.mapproject.R;
import ir.ahmadandroid.mapproject.model.Person;
import ir.ahmadandroid.mapproject.ui.fragments.AdminHomeFragment;
import ir.ahmadandroid.mapproject.ui.fragments.PersonListFragment;
import ir.ahmadandroid.mapproject.ui.fragments.ProfileFragment;
import ir.ahmadandroid.mapproject.utils.MyActivity;
import ir.ahmadandroid.mapproject.utils.Utility;

public class AdminActivity extends MyActivity{

    private SharedPreferences preferences;
    private String TAG = "AdminActivity";
    private Button btnProfile, btnHome, btnPerson, btnTaskList;
    private RelativeLayout fragmentLayout;
    private AdminHomeFragment adminHomeFragment;
    private BottomNavigationView bottomNavView;
    private NavController navController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        BottomNavigationView bottomNavView=findViewById(R.id.nav_view);
        NavController navController= Navigation.findNavController(this,R.id.nav_host_fragment);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        AppBarConfiguration appBarConfiguration=new AppBarConfiguration.Builder(
//                R.id.nav_home,R.id.nav_person_list,R.id.nav_service_list,
//                R.id.nav_profile
//        ).build();
//        NavigationUI.setupActionBarWithNavController(this,navController,appBarConfiguration);
        NavigationUI.setupWithNavController(bottomNavView,navController);


    }

}