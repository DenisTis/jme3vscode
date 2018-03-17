package com.jme3vscode.template.states;

import com.jme3.app.state.AppStateManager;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;

import com.jme3vscode.template.inputs.StandardInput;
import com.jme3vscode.template.inputprocessors.StandardInputProcessor;
import com.jme3vscode.template.scenegraph.MainScenegraph;

import com.jme3.math.Vector3f;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;


/*
REMEMBER: Y has to be always up coordinate, otherwise some controls,
f.e BetterCharacterControl will not work
*/
public class MainState extends BulletAppState implements PhysicsTickListener {
  private SimpleApplication simpleApplication;
  private StandardInput input;
  private StandardInputProcessor inputProcessor;
  private MainScenegraph scenegraph;
  

  @Override
  public void initialize(AppStateManager stateManager, Application app) {
    super.initialize(stateManager, app);
    this.setThreadingType(BulletAppState.ThreadingType.SEQUENTIAL);

    simpleApplication = (SimpleApplication) app;

    simpleApplication.getFlyByCamera().setDragToRotate(true);
    simpleApplication.getFlyByCamera().setMoveSpeed(4f);
    //    simpleApplication.getFlyByCamera().setEnabled(false);
    this.getPhysicsSpace().addTickListener(this);

    scenegraph = new MainScenegraph(simpleApplication.getRootNode(), simpleApplication.getAssetManager(),
        this.getPhysicsSpace());
    scenegraph.initialize();
    this.setDebugEnabled(true);

    simpleApplication.getCamera().setLocation(new Vector3f(10, 20, 0));
    simpleApplication.getCamera().lookAt(scenegraph.boxGeometry.getLocalTranslation(), new Vector3f(10, 20, 0));

    //TODO test if works fine if scenegraph is updated later
    inputProcessor = new StandardInputProcessor(app.getInputManager(), app.getCamera(), scenegraph);
    input = new StandardInput(app.getInputManager(), inputProcessor, inputProcessor);
  }

  @Override
  public void cleanup() {
    scenegraph.cleanup();
    input.cleanup();
  }

  @Override
  public void prePhysicsTick(PhysicsSpace space, float tpf) {
  }
}
