package com.jme3vscode.template.application;

import com.jme3.app.SimpleApplication;
import com.jme3.system.AppSettings;
import com.jme3vscode.template.states.MainState;

public class Main extends SimpleApplication {

    public static void main(String[] args){
        Main app = new Main();
        AppSettings settings = new AppSettings(true);
        settings.setTitle("JME3VSCode");
        app.setSettings(settings);
        app.showSettings = false;
        app.setDisplayStatView(false);
        app.setDisplayFps(false);
        app.setPauseOnLostFocus(true);
        app.start(); 
    }

    @Override
    public void simpleInitApp() {
        getStateManager().attach(new MainState());
    }
}
