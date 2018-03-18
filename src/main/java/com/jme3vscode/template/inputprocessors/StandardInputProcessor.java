package com.jme3vscode.template.inputprocessors;

import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3vscode.template.constants.StandardInputActions;
import com.jme3vscode.template.scenegraph.MainScenegraph;

import com.jme3.collision.CollisionResults;
import com.jme3.collision.CollisionResult;
import com.jme3.math.Ray;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.math.Quaternion;

import java.util.logging.Level;
import java.util.logging.Logger;

public class StandardInputProcessor implements AnalogListener, ActionListener {
    public boolean isPaused = false;
    private InputManager inputManager;
    private Camera camera;
    private MainScenegraph scenegraph;
    private final static int SPEED = 1;
    //private final static Vector3f upVector = new Vector3f(0, 1, 0);
    private final static Quaternion strafeLeft = new Quaternion().fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_Y);
    private final static Quaternion strafeRight = new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_Y);
    private Logger logger = Logger.getLogger(this.getClass().getName());

    public StandardInputProcessor(InputManager inputManager, Camera camera, MainScenegraph scenegraph) {
        this.scenegraph = scenegraph;
        this.camera = camera;
        this.inputManager = inputManager;
    }

    public void onAction(String name, boolean isPressed, float tpf) {
        if (isPaused && !name.equals(StandardInputActions.ACTION_PAUSE)) {
            return;
        }
        logger.log(Level.INFO, "Action called " + name);
        if (isPressed) {
            if (name.equals(StandardInputActions.ACTION_PAUSE)) {
                isPaused = !isPaused;
            } else if (name.equals(StandardInputActions.ACTION_USE)) {
                scenegraph.characterControl.jump();
            }
        }
    }

    public void onAnalog(String name, float value, float tpf) {
        if (isPaused) {
            return;
        }
        if (name.equals(StandardInputActions.ACTION_MOUSE_MOVE_LEFT)
                || name.equals(StandardInputActions.ACTION_MOUSE_MOVE_RIGHT)
                || name.equals(StandardInputActions.ACTION_MOUSE_MOVE_UP)
                || name.equals(StandardInputActions.ACTION_MOUSE_MOVE_DOWN)) {
            Vector2f click2d = inputManager.getCursorPosition().clone();
            Vector3f nearClippingPlane = camera.getWorldCoordinates(click2d, 0f).clone();
            Vector3f farClippingPlane = camera.getWorldCoordinates(click2d, 1f);
            Vector3f direction = farClippingPlane.subtractLocal(nearClippingPlane).normalizeLocal();
            
            Ray ray = new Ray(nearClippingPlane, direction);
            
            CollisionResults data = new CollisionResults();
            logger.log(Level.INFO, "Check collision from "+nearClippingPlane+ " direction "+direction);
            scenegraph.floorGeometry.collideWith(ray, data);
            CollisionResult closest = data.getClosestCollision();
            if (closest != null) {
                Vector3f charDirection = closest.getContactPoint().subtract(scenegraph.characterGeometry.getWorldTranslation());
                charDirection.normalizeLocal().setY(0);
                scenegraph.characterControl.setViewDirection(charDirection);
            }


        } else if (name.equals(StandardInputActions.ACTION_FORW)) {
            Vector3f direction = scenegraph.characterControl.getViewDirection().clone().setY(0);
            direction.cross(scenegraph.characterControl.getWalkDirection());
            direction.normalizeLocal();
            scenegraph.characterControl.setWalkDirection(direction.mult(value / tpf * SPEED));
        } else if (name.equals(StandardInputActions.ACTION_BACK)) {
            Vector3f direction = scenegraph.characterControl.getViewDirection().clone().setY(0).negateLocal();
            direction.cross(scenegraph.characterControl.getWalkDirection());
            direction.normalizeLocal();
            scenegraph.characterControl.setWalkDirection(direction.mult(value / tpf * SPEED));
        } else if (name.equals(StandardInputActions.ACTION_LEFT)) {
            Vector3f direction = scenegraph.characterControl.getViewDirection().clone().setY(0);
            Vector3f newDirection = strafeLeft.mult(direction);
            Vector3f walkDirection = newDirection.add(scenegraph.characterControl.getWalkDirection());
            walkDirection.normalizeLocal();
            scenegraph.characterControl.setWalkDirection(walkDirection.mult(value / tpf * SPEED));
        } else if (name.equals(StandardInputActions.ACTION_RIGHT)) {
            Vector3f direction = scenegraph.characterControl.getViewDirection().clone().setY(0);
            Vector3f newDirection = strafeRight.mult(direction);
            Vector3f walkDirection = newDirection.add(scenegraph.characterControl.getWalkDirection());
            walkDirection.normalizeLocal();
            scenegraph.characterControl.setWalkDirection(walkDirection.mult(value / tpf * SPEED));
            // } else if (name.equals(StandardInputActions.ACTION_BACK)) {
            //     Vector3f direction = scenegraph.characterControl.getViewDirection().clone().negateLocal().normalizeLocal().setY(0);
            //     scenegraph.characterControl.setWalkDirection(direction.mult(value * SPEED));
            //  } else if (name.equals(StandardInputActions.ACTION_LEFT)) {
            //     Vector3f direction = scenegraph.characterControl.getViewDirection().clone().normalizeLocal().setY(0);
            //     Vector3f newDirection = strafeLeft.mult(direction);
            //     scenegraph.characterControl.setWalkDirection(newDirection.mult(value * SPEED));
            // } else if (name.equals(StandardInputActions.ACTION_RIGHT)) {
            //     Vector3f direction = scenegraph.characterControl.getViewDirection().clone().normalizeLocal().setY(0);
            //     Vector3f newDirection = strafeRight.mult(direction);
            //     scenegraph.characterControl.setWalkDirection(newDirection.mult(value * SPEED));
        }
    }
}