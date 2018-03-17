package com.jme3vscode.template.inputs;

import com.jme3.input.InputManager;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.ActionListener;

public class AbstractInput {
  protected InputManager inputManager;
  protected AnalogListener analogListener;
  protected ActionListener actionListener;

  public AbstractInput(InputManager inputManager, AnalogListener analogListener, ActionListener actionListener) {
    this.inputManager = inputManager;
    this.analogListener = analogListener;
    this.actionListener = actionListener;
  }

  public void cleanup() {
    //clear all mappings and listeners
    inputManager.clearMappings();
  }

}
