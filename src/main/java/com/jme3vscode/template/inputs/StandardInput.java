package com.jme3vscode.template.inputs;

import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;

import com.jme3vscode.template.inputs.AbstractInput;
import com.jme3vscode.template.constants.StandardInputActions;

public class StandardInput extends AbstractInput {
  @Override
  public void initialize(InputManager inputManager, AnalogListener analogListener, ActionListener actionListener) {
    super.initialize(inputManager, analogListener, actionListener);
    inputManager.addMapping(StandardInputActions.ACTION_PAUSE, StandardInputActions.TRIGGER_PAUSE);
    inputManager.addMapping(StandardInputActions.ACTION_LEFT, StandardInputActions.TRIGGER_LEFT);
    inputManager.addMapping(StandardInputActions.ACTION_RIGHT, StandardInputActions.TRIGGER_RIGHT);
    inputManager.addMapping(StandardInputActions.ACTION_USE, StandardInputActions.TRIGGER_USE2);

    inputManager.addListener(actionListener, StandardInputActions.ACTION_PAUSE);
    inputManager.addListener(analogListener, StandardInputActions.ACTION_LEFT, StandardInputActions.ACTION_RIGHT,
    StandardInputActions.ACTION_USE);
  }

}
