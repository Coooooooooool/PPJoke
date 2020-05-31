package com.alex.ppjoke.utils;

import android.content.ComponentName;

import androidx.fragment.app.FragmentActivity;
import androidx.navigation.ActivityNavigator;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavGraphNavigator;
import androidx.navigation.NavigatorProvider;
import androidx.navigation.fragment.FragmentNavigator;

import com.alex.ppjoke.model.Destination;
import com.alex.ppjoke.navigator.FixFragmentNavigator;
import com.example.libcommon.global.AppGlobals;

import java.util.HashMap;

public class NavGraphBuilder {

    public static void build(NavController controller, FragmentActivity activity, int containerId){
        NavigatorProvider provider = controller.getNavigatorProvider();
        NavGraph navGraph = new NavGraph(new NavGraphNavigator(provider));

//        FragmentNavigator fragmentNavigator = provider.getNavigator(FragmentNavigator.class);
        //使用自定义的导航器
        FixFragmentNavigator fragmentNavigator = new FixFragmentNavigator(activity,activity.getSupportFragmentManager(),containerId);
        provider.addNavigator(fragmentNavigator);
        ActivityNavigator activityNavigator = provider.getNavigator(ActivityNavigator.class);


        HashMap<String, Destination> destConfig = AppConfig.getDestConfig();
        for (Destination value : destConfig.values()) {
            if(value.isFragment){
                FragmentNavigator.Destination destination = fragmentNavigator.createDestination();
                destination.setClassName(value.clazName);
                destination.setId(value.id);
                destination.addDeepLink(value.pageUrl);

                navGraph.addDestination(destination);
            }else{
                ActivityNavigator.Destination destination = activityNavigator.createDestination();
                destination.setComponentName(new ComponentName(AppGlobals.getApplication().getPackageName(),value.clazName));
                destination.setId(value.id);
                destination.addDeepLink(value.pageUrl);

                navGraph.addDestination(destination);
            }

            if(value.asStarter){
                navGraph.setStartDestination(value.id);
            }
        }

        controller.setGraph(navGraph);
    }

}
