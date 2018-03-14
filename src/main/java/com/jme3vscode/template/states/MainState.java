package com.jme3vscode.template.states;

import com.jme3.app.state.AppStateManager;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;

import com.jme3vscode.template.inputs.StandardInput;
import com.jme3vscode.template.constants.StandardInputActions;

import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.control.RigidBodyControl;

public class MainState extends BulletAppState implements PhysicsTickListener, AnalogListener, ActionListener {
  private SimpleApplication simpleApplication;
  private boolean isPaused = true;
  private Geometry boxGeometry;
  private StandardInput input = new StandardInput();

  @Override
  public void initialize(AppStateManager stateManager, Application app) {
    super.initialize(stateManager, app);
    simpleApplication = (SimpleApplication) app;
    input.initialize(app.getInputManager(), this, this);

    Box box = new Box(1, 1, 1);
    boxGeometry = new Geometry("Box", box);
    boxGeometry.setName("TestBox");
    Material mat = new Material(simpleApplication.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
    mat.setColor("Color", ColorRGBA.Blue);
    boxGeometry.setMaterial(mat);
    simpleApplication.getRootNode().attachChild(boxGeometry);
    RigidBodyControl pBox = new RigidBodyControl(0.0f);
    boxGeometry.addControl(pBox);
    this.getPhysicsSpace().add(pBox);

    simpleApplication.getCamera().setLocation( new Vector3f(0, 0, 20));
    simpleApplication.getFlyByCamera().setDragToRotate(true);

    // this.getPhysicsSpace().addTickListener(this);
    this.setDebugEnabled(true);
    isPaused = false;
  }

  @Override
  public void cleanup() {
    simpleApplication.getRootNode().detachChildNamed("TestBox");
    input.cleanup();
  }

  @Override
  public void update(float tpf) {
     boxGeometry.rotate(tpf, tpf, tpf);
  }

    public void onAction(String name, boolean isPressed, float tpf) {
      if (name.equals(StandardInputActions.ACTION_PAUSE) && !isPressed) {
        isPaused = !isPaused;
      }
    }

    public void onAnalog(String name, float value, float tpf) {
      if (isPaused) {
        return;
      }
      if(name.equals(StandardInputActions.ACTION_USE)) {
        boxGeometry.rotate(tpf, tpf, tpf);
      }
    }

    // @Override
    // public void prePhysicsTick(PhysicsSpace space,
    //         float tpf) {
    // }

    // @Override
    // public void physicsTick(PhysicsSpace space, float f) {
    //   super.update(tpf);
    // }

}
