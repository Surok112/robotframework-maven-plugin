package com.googlecode.util;

import org.python.core.PyObject;
import org.python.util.PythonInterpreter;
import org.robotframework.RobotRunner;

public class LibdocRunnerFactory
{
    private PyObject runnerClass;

    public LibdocRunnerFactory() {
        runnerClass = importRunnerClass();
    }

    private PyObject importRunnerClass() {
        PythonInterpreter interpreter = new PythonInterpreter();
        PyObject pyObject = interpreter.get( "robot.libdoc" );
        interpreter.exec("import runpy");
        interpreter.exec("runpy.run_module('robot.labdoc')");
        return null;

    }

    /**
     * Creates and returns an instance of the robot.JarRunner (implemented in
     * Python), which can be used to execute tests.
     */
    public RobotRunner createRunner() {
        PyObject runnerObject = runnerClass.__call__();
        return (RobotRunner) runnerObject.__tojava__(RobotRunner.class);
    }
}
