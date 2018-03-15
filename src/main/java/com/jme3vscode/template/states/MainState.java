package com.jme3vscode.template.states;

import com.jme3.app.state.AppStateManager;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;

import com.jme3vscode.template.inputs.StandardInput;
import com.jme3vscode.template.constants.StandardInputActions;
import com.jme3vscode.template.scenegraph.MainScenegraph;

import com.jme3.math.Vector3f;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
/*
REMEMBER: Y has to be always up coordinate, otherwise some controls,
f.e BetterCharacterControl will not work

*/
public class MainState extends BulletAppState implements PhysicsTickListener, AnalogListener, ActionListener {
  private SimpleApplication simpleApplication;
  private boolean isPaused = true;
  private StandardInput input = new StandardInput();
  private MainScenegraph scenegraph;

  @Override
  public void initialize(AppStateManager stateManager, Application app) {
    super.initialize(stateManager, app);
    this.setThreadingType(BulletAppState.ThreadingType.SEQUENTIAL);
    this.getPhysicsSpace().setGravity(new Vector3f(0, 0, -10));

    simpleApplication = (SimpleApplication) app;

//    simpleApplication.getFlyByCamera().setDragToRotate(true);
//    simpleApplication.getFlyByCamera().setEnabled(false);
    simpleApplication.getFlyByCamera().setMoveSpeed(4f);

    input.initialize(app.getInputManager(), this, this);
    this.getPhysicsSpace().addTickListener(this);

    //check if this can be rewritten (probably do all in constructor)
    scenegraph = new MainScenegraph(simpleApplication.getRootNode(), simpleApplication.getAssetManager(),
        this.getPhysicsSpace());
    scenegraph.initialize();
    simpleApplication.getCamera().setLocation(new Vector3f(0, 0, 20));
    // simpleApplication.getCamera().lookAt(scenegraph.boxGeometry.getLocalTranslation(), 
    // new Vector3f(0, 20, 0));

    this.setDebugEnabled(true);
    isPaused = false;
  }

  @Override
  public void cleanup() {
    simpleApplication.getRootNode().detachChildNamed("TestBox");
    input.cleanup();
  }

  //Enabling this method will disable physics tick methods!
  // @Override
  // public void update(float tpf) {
  //   scenegraph.pBox.setLinearVelocity(new Vector3f(0, 15, 0));
  //   // scenegraph.boxGeometry.rotate(tpf, tpf, tpf);
  // }

  public void onAction(String name, boolean isPressed, float tpf) {
    if (name.equals(StandardInputActions.ACTION_PAUSE) && !isPressed) {
      isPaused = !isPaused;
    }
  }

  public void onAnalog(String name, float value, float tpf) {
    if (isPaused) {
      return;
    }
    if (name.equals(StandardInputActions.ACTION_USE)) {
      System.out.print("use");
      scenegraph.characterControl.jump();
      //scenegraph.pBox.setLinearVelocity(new Vector3f(0, 0, 4));
    }
  }

  @Override
  public void prePhysicsTick(PhysicsSpace space, float tpf) {
//      scenegraph.pBox.setLinearVelocity(new Vector3f(0, -1, 0));
  }
}
