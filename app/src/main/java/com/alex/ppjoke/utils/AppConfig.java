package com.alex.ppjoke.utils;

import android.content.res.AssetManager;

import com.alex.ppjoke.model.BottomBar;
import com.alex.ppjoke.model.Destination;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.example.libcommon.global.AppGlobals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class AppConfig {

    private static HashMap<String, Destination> destConfig;

    private static BottomBar bottomBar;

    public static HashMap<String, Destination> getDestConfig() {
        if (destConfig == null) {
            String content = parseFile("destination.json");
            destConfig = JSON.parseObject(content,new TypeReference<HashMap<String,Destination>>(){}.getType());
        }
        return destConfig;
    }

    public static BottomBar getBottomBar(){
        if(bottomBar == null){
            String content = parseFile("main_tabs_config.json");
            bottomBar = JSON.parseObject(content, BottomBar.class);
        }
        return bottomBar;
    }


    private static String parseFile(String fileName) {
        AssetManager assets = AppGlobals.getApplication().getResources().getAssets();

        InputStream stream = null;
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            stream = assets.open(fileName);
            reader = new BufferedReader(new InputStreamReader(stream));
            String line = null;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return builder.toString();
    }

}
