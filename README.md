vse
===

Vehicle Simulation Environment Library

The Vehicle Simulation Environment (VSE) library consists of classes
designed to aid in building and propagating vehicle models. A model is
typically a system of differential equations and propagation generally
means integrating this system over time. The library also contains
utilities to display the model state in a Java3D environment (multiple
models can be loaded at once).

Most of the library package names are a tip of the hat to a powerful
modeling and simulation language developed by many people I have a great
deal of respect for.  Also, we all know deciding on names is the hardest
part of writing code - why not stick with conventions that are more than
50 years old.

c0ntm - Control Modeling
```
In addition to control system related classes, this package contains
model "self determination" functionality (e.g., attitude
determination).
```

cycxm - Clock Modeling
```
Simulation time management (bookeeping) classes are located here.  This
is not the same as modeling different time scales or time formats.
```

enums - Enumerations
```
Enumerations are contained in a package dedicated to only enumerations.
```

envrm - Environmental Modeling
```
Models associated with environmental effects are located in this
package.  For example, the Gravity model resides here.  Time formats and
scales are also handled here.
```

intxm - Integration Modeling
```
Numerical integration, root solving, and basic function concepts are
defined here.
```

j3d - Java3D
```
All Java3D related functionality resides here.  No other packages within
the library layer (vseLib) are allowed to make use of Java3D.  This
minimizes the pain if it ever becomes necessary to rewrite the graphics
portions for some other ICD.
```

math - Mathematics
```
Math and many numerical methods related classes are developed in this
package.
```

sensm - Sensor Modeling
```
Things that take measurements and are subject to noise and/or systematic
error are modeled here.
```

servm - Service Module
```
This package contains classes that may serve many other packages while
not quite fitting into a package of their own.
```

strtm - Structural Modeling
```
Structure and mass properties are modeled here.
```

test - Unit Testing and simple examples

trmtm - Translational/Rotational Modeling
```
Dynamics modeling comes together here - this is the core of the 6DOF
classes.
```

ui - User Interface
```
This is where GUI interfaces used by vseLib, outside of those within the
j3d package, reside.
```

