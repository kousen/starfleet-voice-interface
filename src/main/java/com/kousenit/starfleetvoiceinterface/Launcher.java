package com.kousenit.starfleetvoiceinterface;

// ponytail: separate entry point that does NOT extend Application, so JavaFX
// launches from the classpath instead of the module path. Avoids the JVM
// deriving automatic modules for every jar — one MCP SDK jar has a hyphenated
// Automatic-Module-Name that isn't a valid module name and breaks module-path launch.
public class Launcher {
    public static void main(String[] args) {
        StarfleetVoiceInterfaceApplication.main(args);
    }
}
