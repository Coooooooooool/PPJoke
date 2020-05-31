package com.alex.ppjoke;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;

import com.alex.ppjoke.view.AppBottomBar;
import com.alex.ppjoke.utils.NavGraphBuilder;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppBottomBar navView = findViewById(R.id.nav_view);

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = NavHostFragment.findNavController(fragment);

        navView.setOnNavigationItemSelectedListener(this);

        NavGraphBuilder.build(navController, this, fragment.getId());

//        GetRequest<JSONObject> request = new GetRequest<>("www.mooc.com");
//        request.excute();
//
//        request.excute(new JsonCallback<JSONObject>() {
//            @Override
//            public void onSuccess(ApiResponse<JSONObject> response) {
//                super.onSuccess(response);
//            }
//        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        navController.navigate(item.getItemId());
        return !TextUtils.isEmpty(item.getTitle());
    }
}
