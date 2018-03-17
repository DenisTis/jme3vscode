package com.jme3vscode.template.inputprocessors;

import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3vscode.template.constants.StandardInputActions;
import com.jme3vscode.template.scenegraph.MainScenegraph;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.math.Quaternion;

public class StandardInputProcessor implements AnalogListener, ActionListener {
    public boolean isPaused = false;
    private MainScenegraph scenegraph;

public StandardInputProcessor(MainScenegraph scenegraph) {
this.scenegraph = scenegraph;
}

public void onAction(String name, boolean isPressed, float tpf) {
    /*Important: movement actions cannot be analog:
    Because force is applied to object only at press and removed at release.
    If force would be reset each frame, character will not move smooth
    */
    final int SPEED = 300;
    if (isPaused && !name.equals(StandardInputActions.ACTION_PAUSE)) {
      return;
    }

    if (isPressed) {
      if (name.equals(StandardInputActions.ACTION_PAUSE)) {
        isPaused = !isPaused;
      } else if (name.equals(StandardInputActions.ACTION_USE)) {
        scenegraph.characterControl.jump();
      } else if (name.equals(StandardInputActions.ACTION_FORW)) {
        Vector3f direction = scenegraph.characterGeometry.getLocalRotation().getRotationColumn(2);
        scenegraph.characterControl.setWalkDirection(direction.mult(tpf * SPEED));
      } else if (name.equals(StandardInputActions.ACTION_BACK)) {
        Vector3f direction = scenegraph.characterGeometry.getLocalRotation().getRotationColumn(2);
        scenegraph.characterControl.setWalkDirection(direction.negateLocal().mult(tpf * SPEED));
      } else if (name.equals(StandardInputActions.ACTION_LEFT)) {
        Quaternion rotateLeft = new Quaternion().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_Y);
        Vector3f direction = scenegraph.characterGeometry.getLocalRotation().getRotationColumn(2);
        Vector3f newDirection = rotateLeft.mult(direction);
        scenegraph.characterControl.setWalkDirection(newDirection.mult(tpf * SPEED));
      } else if (name.equals(StandardInputActions.ACTION_RIGHT)) {
        Quaternion rotateRight = new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_Y);
        Vector3f direction = scenegraph.characterGeometry.getLocalRotation().getRotationColumn(2);
        Vector3f newDirection = rotateRight.mult(direction);
        scenegraph.characterControl.setWalkDirection(newDirection.mult(tpf * SPEED));
      }
    } else { // button released - stop all movements
      if (name.equals(StandardInputActions.ACTION_FORW) || name.equals(StandardInputActions.ACTION_BACK) ||
          name.equals(StandardInputActions.ACTION_LEFT) || name.equals(StandardInputActions.ACTION_RIGHT)) {
        scenegraph.characterControl.setWalkDirection(new Vector3f(0, 0, 0));
      }
    }
  }

  public void onAnalog(String name, float value, float tpf) {
    if (isPaused) {
      return;
    }
  }



}