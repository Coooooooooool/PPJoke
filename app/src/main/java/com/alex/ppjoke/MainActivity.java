package com.alex.ppjoke;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import com.alex.ppjoke.model.Destination;
import com.alex.ppjoke.model.User;
import com.alex.ppjoke.ui.login.UserManager;
import com.alex.ppjoke.utils.AppConfig;
import com.alex.ppjoke.view.AppBottomBar;
import com.alex.ppjoke.utils.NavGraphBuilder;
import com.example.libcommon.utils.StatusBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private NavController navController;
    private AppBottomBar navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //由于 启动时设置了 R.style.launcher 的windowBackground属性
        //势必要在进入主页后,把窗口背景清理掉
        setTheme(R.style.AppTheme);

        //启用沉浸式布局，白底黑字
        StatusBar.fitSystemBar(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navView = findViewById(R.id.nav_view);

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
        HashMap<String, Destination> destConfig = AppConfig.getDestConfig();
        Iterator<Map.Entry<String, Destination>> iterator = destConfig.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, Destination> entry = iterator.next();
            Destination value = entry.getValue();
            if(value!=null && !UserManager.get().isLogin()&&value.needLogin && value.id==item.getItemId()){
                UserManager.get().login(this).observe(this, new Observer<User>() {
                    @Override
                    public void onChanged(User user) {
                        navView.setSelectedItemId(item.getItemId());
                    }
                });
                return false;
            }
        }
        navController.navigate(item.getItemId());
        return !TextUtils.isEmpty(item.getTitle());
    }
}
