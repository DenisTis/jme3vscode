package com.jme3vscode.template.scenegraph;

import com.jme3.scene.Node;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.math.Plane;
import com.jme3.bullet.collision.shapes.PlaneCollisionShape;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.asset.AssetManager;
import com.jme3.math.FastMath;

public class MainScenegraph {
  private Node rootNode;
  private AssetManager assetManager;
  private PhysicsSpace physicsSpace;
  public Geometry boxGeometry;
  public RigidBodyControl pBox;
  public Geometry characterGeometry;
  public BetterCharacterControl characterControl;
  public Geometry floorGeometry;

  public MainScenegraph(Node rootNode, AssetManager assetManager, PhysicsSpace physicsSpace) {
    this.rootNode = rootNode;
    this.assetManager = assetManager;
    this.physicsSpace = physicsSpace;
  }

  public void initialize() {
    physicsSpace.setGravity(new Vector3f(0, -10, 0));
    // Create object
    Box box = new Box(1, 1, 1);
    boxGeometry = new Geometry("Box", box);
    boxGeometry.setName("TestBox");
    Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    mat.setColor("Color", ColorRGBA.Blue);
    boxGeometry.setMaterial(mat);
    boxGeometry.setLocalTranslation(0, 4, 0);
     rootNode.attachChild(boxGeometry);
    pBox = new RigidBodyControl(1);
    boxGeometry.addControl(pBox);
     physicsSpace.add(pBox);


    //Create floor
    Quad floorQuad = new Quad(20, 20);
    floorGeometry = new Geometry("Floor", floorQuad);
    // otherwise the corner of quad starts in the center of screen
    Material blackMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    blackMaterial.setColor("Color", ColorRGBA.DarkGray);
    floorGeometry.setMaterial(blackMaterial);
    //I have to rotate is as default rotation is like for a wall
    floorGeometry.rotate(-90*FastMath.DEG_TO_RAD, 0, 0);
    floorGeometry.move(-10, 0, 10);
    rootNode.attachChild(floorGeometry);

    //because of Quad rotation, another Plane vector has to be used
    Plane floorInfinite = new Plane(new Vector3f(0, 0, 1), 0);
    PlaneCollisionShape planeCollision = new PlaneCollisionShape(floorInfinite);
    RigidBodyControl pFloor = new RigidBodyControl(planeCollision, 0);
    floorGeometry.addControl(pFloor);
    physicsSpace.add(pFloor);

    //Create character control
    Node characterNode = new Node("characterNode");
    characterGeometry = new Geometry("characterGeometry", new Box(0.5f, 1, 0.5f));
    characterGeometry.setMaterial(mat);
    //add offset to the geometry of the node (otherwise physics shape is half of the model height higher)
    characterGeometry.setLocalTranslation(0, 1, 0);
    rootNode.attachChild(characterNode);
    characterNode.attachChild(characterGeometry);
    

    characterControl = new BetterCharacterControl(0.5f, 2, 1);
    characterControl.setJumpForce(new Vector3f(0, 3, 0));
    characterNode.addControl(characterControl);
    //that is how you position object on the scene (do not use move/setLocalTranslation of the characterNode)
    characterControl.warp(new Vector3f(-2,2,2));
    physicsSpace.add(characterControl);

  }

  public void cleanup() {
    //check as later it can clean menu children by default
    //probably attach all children under Main or Standard Node
    rootNode.detachAllChildren();
  }
}
