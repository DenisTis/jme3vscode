package com.jme3vscode.template.inputprocessors;

import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3vscode.template.constants.StandardInputActions;
import com.jme3vscode.template.scenegraph.MainScenegraph;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.math.Quaternion;

public class StandardInputProcessor implements AnalogListener, ActionListener {
    public boolean isPaused = false;
    private InputManager inputManager;
    private Camera camera;
    private MainScenegraph scenegraph;
    private final static int SPEED = 300;
    private final static Vector3f upVector = new Vector3f(0,1,0);
    private final static Quaternion strafeLeft = new Quaternion().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_Y);
    private final static Quaternion strafeRight = new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_Y);

    public StandardInputProcessor(InputManager inputManager, Camera camera, MainScenegraph scenegraph) {
        this.scenegraph = scenegraph;
        this.camera = camera;
        this.inputManager = inputManager;
    }

    public void onAction(String name, boolean isPressed, float tpf) {
        /*Important: movement actions cannot be analog:
        Because force is applied to object only at press and removed at release.
        If force would be reset each frame, character will not move smooth
        */
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
                Vector3f direction = scenegraph.characterGeometry.getLocalRotation().getRotationColumn(2);
                Vector3f newDirection = strafeLeft.mult(direction);
                scenegraph.characterControl.setWalkDirection(newDirection.mult(tpf * SPEED));
            } else if (name.equals(StandardInputActions.ACTION_RIGHT)) {
                Vector3f direction = scenegraph.characterGeometry.getLocalRotation().getRotationColumn(2);
                Vector3f newDirection = strafeRight.mult(direction);
                scenegraph.characterControl.setWalkDirection(newDirection.mult(tpf * SPEED));
            }
        } else { // button released - stop all movements
            //TODO - that has to negate movement Vector applied earlier for each button
            //But first check how the movement works after character starts to rotate to mose position
            if (name.equals(StandardInputActions.ACTION_FORW) || name.equals(StandardInputActions.ACTION_BACK)
                    || name.equals(StandardInputActions.ACTION_LEFT)
                    || name.equals(StandardInputActions.ACTION_RIGHT)) {
                scenegraph.characterControl.setWalkDirection(new Vector3f(0, 0, 0));
            }
        }
    }

    public void onAnalog(String name, float value, float tpf) {
        if (isPaused) {
            return;
        }
        if (
        name.equals(StandardInputActions.ACTION_MOUSE_MOVE_LEFT) || 
        name.equals(StandardInputActions.ACTION_MOUSE_MOVE_RIGHT) || 
        name.equals(StandardInputActions.ACTION_MOUSE_MOVE_UP) || 
        name.equals(StandardInputActions.ACTION_MOUSE_MOVE_DOWN) ) {
            Vector3f characterPosition = scenegraph.characterGeometry.getLocalTranslation().clone();
            Vector3f mousePosition = camera.getWorldCoordinates(inputManager.getCursorPosition(), 1);
            //System.out.print("character" + characterPosition + " mouse " + mousePosition);
            Vector3f direction = characterPosition.subtractLocal(mousePosition);
            direction.normalizeLocal();
            scenegraph.characterControl.setViewDirection(direction);
        }
    }

    // public Vector3f getMousePosition() {
    //     //CollisionResults results = new CollisionResults();
    //     Vector2f click2d = inputManager.getCursorPosition();
    //     return camera.getWorldCoordinates(click2d, 0f);
    //     // Vector3f direction = cam.getWorldCoordinates(
    //     //         click2d, 1f).subtractLocal(click3d);
    //     // Ray ray = new Ray(click3d, direction);
    //     // simpleApp.getRootNode().collideWith(ray, results);
    //     // if (results.size() > 0) {
    //     //     Geometry clickedObject = results.getClosestCollision().getGeometry();
    //     //     return clickedObject;
    //     // }
    //     // return null;
    // }

}