package com.jme3vscode.template.inputs;

import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3vscode.template.inputs.AbstractInput;
import com.jme3vscode.template.constants.StandardInputActions;

public class StandardInput extends AbstractInput {
  public StandardInput(InputManager inputManager, AnalogListener analogListener, ActionListener actionListener) {
    super(inputManager, analogListener, actionListener);
    inputManager.addMapping(StandardInputActions.ACTION_PAUSE, StandardInputActions.TRIGGER_PAUSE);
    inputManager.addMapping(StandardInputActions.ACTION_FORW, StandardInputActions.TRIGGER_FORW);
    inputManager.addMapping(StandardInputActions.ACTION_BACK, StandardInputActions.TRIGGER_BACK);
    inputManager.addMapping(StandardInputActions.ACTION_LEFT, StandardInputActions.TRIGGER_LEFT);
    inputManager.addMapping(StandardInputActions.ACTION_RIGHT, StandardInputActions.TRIGGER_RIGHT);
    inputManager.addMapping(StandardInputActions.ACTION_USE, StandardInputActions.TRIGGER_USE,
        StandardInputActions.TRIGGER_USE2);
    inputManager.addMapping(StandardInputActions.ACTION_MOUSE_MOVE_LEFT, new MouseAxisTrigger(MouseInput.AXIS_X, false));
    inputManager.addMapping(StandardInputActions.ACTION_MOUSE_MOVE_RIGHT, new MouseAxisTrigger(MouseInput.AXIS_X, true));
    inputManager.addMapping(StandardInputActions.ACTION_MOUSE_MOVE_UP, new MouseAxisTrigger(MouseInput.AXIS_Y, false));
    inputManager.addMapping(StandardInputActions.ACTION_MOUSE_MOVE_DOWN, new MouseAxisTrigger(MouseInput.AXIS_Y, true));

    //Assign one-time actions
    inputManager.addListener(actionListener, StandardInputActions.ACTION_PAUSE, StandardInputActions.ACTION_USE,
    StandardInputActions.ACTION_FORW, StandardInputActions.ACTION_BACK,
    StandardInputActions.ACTION_LEFT, StandardInputActions.ACTION_RIGHT);

    //Assign actions with duration
      inputManager.addListener(analogListener, StandardInputActions.ACTION_MOUSE_MOVE_DOWN, 
      StandardInputActions.ACTION_MOUSE_MOVE_UP, StandardInputActions.ACTION_MOUSE_MOVE_LEFT, 
      StandardInputActions.ACTION_MOUSE_MOVE_RIGHT);
  }

}
