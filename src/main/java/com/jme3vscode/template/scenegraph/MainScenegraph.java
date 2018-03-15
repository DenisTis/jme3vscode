package com.jme3vscode.template.scenegraph;

import com.jme3.scene.Node;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Quad;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.math.Plane;
import com.jme3.bullet.collision.shapes.PlaneCollisionShape;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.asset.AssetManager;

public class MainScenegraph {
  public Node rootNode;
  public AssetManager assetManager;
  public PhysicsSpace physicsSpace;
  public Geometry boxGeometry;
  public RigidBodyControl pBox;
  public Geometry capsuleGeometry;
  public BetterCharacterControl characterControl;
  public MainScenegraph(Node rootNode, AssetManager assetManager, PhysicsSpace physicsSpace) {
    this.rootNode = rootNode;
    this.assetManager = assetManager;
    this.physicsSpace = physicsSpace;
  }

  public void initialize() {
    Box box = new Box(1, 1, 1);
    boxGeometry = new Geometry("Box", box);
    boxGeometry.setName("TestBox");
    Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    mat.setColor("Color", ColorRGBA.Blue);
    boxGeometry.setMaterial(mat);
    boxGeometry.setLocalTranslation(0, 0, 4);
    // rootNode.attachChild(boxGeometry);
    pBox = new RigidBodyControl(1);
    boxGeometry.addControl(pBox);
    // physicsSpace.add(pBox);

    Quad floorQuad = new Quad(30, 30);
    Geometry floorGeometry = new Geometry("Floor", floorQuad);
    Material blackMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    blackMaterial.setColor("Color", ColorRGBA.Black);
    floorGeometry.setMaterial(blackMaterial);
    floorGeometry.setLocalTranslation(0, 0, 0);
    rootNode.attachChild(floorGeometry);

    PlaneCollisionShape planeCollision = new PlaneCollisionShape(  new Plane(new Vector3f(0, 0, 1), 0));
    RigidBodyControl pFloor = new RigidBodyControl(planeCollision, 0);
    floorGeometry.addControl(pFloor);
    physicsSpace.add(pFloor);


//    characterControl.setJumpForce(new Vector3f(0,0,1));
    Cylinder capsule = new Cylinder(16, 16, 1, 1);
    capsuleGeometry = new Geometry("capsule", new Box(0.5f, 0.5f, 1));
    capsuleGeometry.setMaterial(mat);
    rootNode.attachChild(capsuleGeometry);

    characterControl = new BetterCharacterControl(2, 4, 3);
    characterControl.setGravity(new Vector3f(0, 0, -1));
 //   characterControl.setJumpForce(new Vector3f(0, 0, -1));
    capsuleGeometry.addControl(characterControl);
    characterControl.warp(new Vector3f(0,0,2));
    physicsSpace.add(characterControl);

  }
}
