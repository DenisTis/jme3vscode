This is a template project for those who want to develop applications with jMonkey Engine in Visual Studio Code.

## Prerequisites

This [tutorial](https://code.visualstudio.com/docs/languages/java) is being implemented.

## Important for code development and fine tuning

1.  In maven projects path to your source code should always be in folder: src/main/java
    This folder is configured in ".classpath" folder.
1.  Your main executable java file has to be configured in .vscode/launch.json in the "mainClass" property

Now it is possible to build exe file for your project including all libraries.
The only dependency left is jdk but it should be possible to solve with launch4j executable wrapper
If you execute "build" in maven, project.exe would be automatically generated
