package com.jme3vscode.template.constants;

import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;

public class StandardInputActions {
  public static String ACTION_PAUSE = "Pause";
  public static KeyTrigger TRIGGER_PAUSE = new KeyTrigger(KeyInput.KEY_P);
  public static String ACTION_LEFT = "Left";
  public static KeyTrigger TRIGGER_LEFT = new KeyTrigger(KeyInput.KEY_LEFT);
  public static String ACTION_RIGHT = "Right";
  public static KeyTrigger TRIGGER_RIGHT = new KeyTrigger(KeyInput.KEY_RIGHT);
  public static String ACTION_USE = "Use";
  public static KeyTrigger TRIGGER_USE = new KeyTrigger(KeyInput.KEY_SPACE);
  public static MouseButtonTrigger TRIGGER_USE2 = new MouseButtonTrigger(MouseInput.BUTTON_RIGHT);
}
