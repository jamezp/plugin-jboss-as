package org.jboss.as.forge;

/**
* @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
*/
enum PropertyKey {
    BUNDLES_DIR("as7", "bundles-dir"),
    HOSTNAME("as7", "hostname"),
    JAVA_HOME("java-home"),
    JBOSS_HOME("as7", "jboss-home"),
    JBOSS_AS_VERSION("as7", "version"),
    JVM_ARGS("as7", "jvm-args"),
    MODULES_DIR("as7", "modules-dir"),
    PORT("as7", "port"),
    PROJECT_KEY("as7", "installed"),
    SERVER_CONFIG_FILE("as7", "server-config"),
    SERVER_STARTUP_TIMEOUT("as7", "timeout"),
    LEGACY_CONFIGURED("as7", "configured"),;
    final String key;

    PropertyKey(final String... keyParts) {
        StringBuilder result = new StringBuilder("jboss-as");
        for (String arg : keyParts)
            result = result.append(".").append(arg);
        key = result.toString();
    }
}
