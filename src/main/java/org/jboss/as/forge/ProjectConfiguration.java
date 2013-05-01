/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.as.forge;

import java.io.File;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jboss.as.forge.util.Files;
import org.jboss.forge.env.Configuration;
import org.jboss.forge.env.ConfigurationScope;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class ProjectConfiguration {

    /**
     * The default JBOSS_HOME key
     */
    private static final String JBOSS_HOME_HOLDER = "TMP";
    private static final File DEFAULT_JBOSS_HOME;


    /**
     * The default host name
     */
    static final String DEFAULT_HOSTNAME = "localhost";

    /**
     * The default management port
     */
    static final int DEFAULT_PORT = 9999;

    static {
        // Create a temporary directory
        DEFAULT_JBOSS_HOME = new File(Files.getTempDirectory(), "jboss-as-dist");
        // Setup a shutdown hook to recursively delete
        SecurityActions.addShutdownHook(new Thread() {
            @Override
            public void run() {
                Files.deleteRecursively(DEFAULT_JBOSS_HOME);
            }
        });
    }

    @Inject
    private Configuration configuration;

    @Inject
    private Versions versions;

    private String hostname;

    private int port;

    @PostConstruct
    protected void resetDefaults() {
        try {
            hostname = getProperty(PropertyKey.HOSTNAME, DEFAULT_HOSTNAME);
            port = getInt(PropertyKey.PORT, DEFAULT_PORT);
        } catch (Exception ignore) {
            // project may not be set up yet
        }
    }

    /**
     * Clears the configuration completely. Removes all properties associated with the plugin.
     */
    protected void clearConfig() {
        final Configuration configuration = getConfiguration();
        for (PropertyKey key : PropertyKey.values()) {
            configuration.clearProperty(key.key);
        }
        hostname = DEFAULT_HOSTNAME;
        port = DEFAULT_PORT;
    }

    /**
     * Returns the hostname of the management console to attach to.
     * <p/>
     * By default {@link #DEFAULT_HOSTNAME} is returned.
     *
     * @return the hostname
     */
    public String getHostname() {
        return hostname;
    }

    protected void setHostname(final String hostname) {
        setHostname(hostname, false);
    }

    protected void setHostname(final String hostname, final boolean persist) {
        this.hostname = hostname;
        if (persist) {
            setProperty(PropertyKey.HOSTNAME, hostname);
        }
    }

    protected void clearHostname() {
        clearProperty(PropertyKey.HOSTNAME);
        hostname = DEFAULT_HOSTNAME;
    }

    /**
     * Returns the management port to attach to.
     * <p/>
     * By default {@link #DEFAULT_PORT} is returned.
     *
     * @return the management port
     */
    public int getPort() {
        return port;
    }

    protected void setPort(final int port) {
        setPort(port, false);
    }

    protected void setPort(final int port, final boolean persist) {
        this.port = port;
        if (persist) {
            setProperty(PropertyKey.PORT, port);
        }
    }

    protected void clearPort() {
        clearProperty(PropertyKey.PORT);
        this.port = DEFAULT_PORT;
    }

    /**
     * Returns the JBoss home directory.
     * <p/>
     * This is the directory where JBoss application server is installed and cannot be {@code null}.
     *
     * @return the JBoss home directory.
     */
    public File getJbossHome() {
        if (hasProperty(PropertyKey.JBOSS_HOME)) {
            // Get the JBoss Home
            final String jbossHome = getProperty(PropertyKey.JBOSS_HOME);
            if (JBOSS_HOME_HOLDER.equals(jbossHome)) {
                final File tempDir = new File(DEFAULT_JBOSS_HOME, getVersion().getArchiveDir());
                if (!tempDir.exists()) {
                    tempDir.mkdirs();
                }
                return tempDir;
            }
            return new File(jbossHome);

        }
        return null;
    }

    protected void setJbossHome(final File jbossHome) {
        setProperty(PropertyKey.JBOSS_HOME, jbossHome);
    }

    protected void setDefaultJbossHomeJbossHome() {
        setProperty(PropertyKey.JBOSS_HOME, JBOSS_HOME_HOLDER);
    }

    /**
     * Returns the directory to the modules for JBoss Application Server.
     * <p/>
     * By default {@link #getJbossHome() JBOSS_HOME/modules} is returned,
     *
     * @return the modules directory
     */
    public File getModulesDir() {
        return getFileResource(PropertyKey.MODULES_DIR);
    }

    protected void setModulesDir(final File modulesDir) {
        setProperty(PropertyKey.MODULES_DIR, modulesDir);
    }

    /**
     * Returns the directory to the bundles for JBoss Application Server.
     * <p/>
     * By default {@link #getJbossHome() JBOSS_HOME/bundles} is returned,
     *
     * @return the bundles directory
     */
    public File getBundlesDir() {
        return getFileResource(PropertyKey.BUNDLES_DIR);
    }

    protected void setBundlesDir(final File bundlesDir) {
        setProperty(PropertyKey.BUNDLES_DIR, bundlesDir.getAbsolutePath());
    }

    /**
     * Returns an array of the JVM arguments to pass to the Java command when launching the server.
     *
     * @return the JVM arguments or {@code null} if there are none set
     */
    public String[] getJvmArgs() {
        return hasProperty(PropertyKey.JVM_ARGS) ? getStringArray(PropertyKey.JVM_ARGS) : null;
    }

    protected void setJvmArgs(final String[] args) {
        setProperty(PropertyKey.JVM_ARGS, args);
    }

    /**
     * Returns the Java home directory.
     * <p/>
     * By default the {@literal JAVA_HOME} environment variable is used. If {@code null} is returned the server may not
     * properly work.
     *
     * @return the Java home directory
     */
    public String getJavaHome() {
        return getProperty(PropertyKey.JAVA_HOME, System.getenv("JAVA_HOME"));
    }

    protected void setJavaHome(final String javaHome) {
        setProperty(PropertyKey.JAVA_HOME, javaHome);
    }

    /**
     * Returns the server configuration file or {@code null} if using the default configuration file.
     *
     * @return the server configuration file or {@code null}
     */
    public String getServerConfigFile() {
        return getProperty(PropertyKey.SERVER_CONFIG_FILE, null);
    }

    protected void setServerConfigFile(final String path) {
        setProperty(PropertyKey.SERVER_CONFIG_FILE, path);
    }

    /**
     * Returns the timeout to wait for the server to successfully start.
     * <p/>
     * A number greater than 0 should be returned. Any default value greater than 0 is acceptable.
     *
     * @return the timeout
     */
    public long getStartupTimeout() {
        return getLong(PropertyKey.SERVER_STARTUP_TIMEOUT, 60L);
    }

    protected void setStartupTimeout(final long timeout) {
        setProperty(PropertyKey.SERVER_STARTUP_TIMEOUT, timeout);
    }

    /**
     * The version of the JBoss Application Server to use.
     * <p/>
     * By default the {@link org.jboss.as.forge.Versions#defaultVersion()} will be returned.
     *
     * @return the version of the JBoss Application Server to use
     */
    public Version getVersion() {
        return hasProperty(PropertyKey.JBOSS_AS_VERSION) ?
                versions.fromString(getProperty(PropertyKey.JBOSS_AS_VERSION)) :
                versions.defaultVersion();
    }

    public void setVersion(final Version version) {
        setProperty(PropertyKey.JBOSS_AS_VERSION, version.toString());
    }

    boolean hasProperty(final PropertyKey key) {
        try {
            return getConfiguration().containsKey(key.key);
        } catch (Exception ignore) {
        }
        return false;
    }

    String getProperty(final PropertyKey key) {
        return getConfiguration().getString(key.key);
    }

    String getProperty(final PropertyKey key, final String defaultValue) {
        return getConfiguration().getString(key.key, defaultValue);
    }

    int getInt(final PropertyKey key, final int defaultValue) {
        return getConfiguration().getInt(key.key, defaultValue);
    }

    long getLong(final PropertyKey key, final long defaultValue) {
        return getConfiguration().getLong(key.key, defaultValue);
    }

    String[] getStringArray(final PropertyKey key) {
        return getConfiguration().getStringArray(key.key);
    }

    void clearProperty(final PropertyKey key) {
        getConfiguration().clearProperty(key.key);
    }

    void setProperty(final PropertyKey key, final File value) {
        setProperty(key, (value == null ? null : value.getAbsolutePath()));
    }

    void setProperty(final PropertyKey key, final Object value) {
        if (value == null) {
            getConfiguration().clearProperty(key.key);
        } else {
            getConfiguration().setProperty(key.key, value);
        }
    }

    private File getFileResource(final PropertyKey key) {
        final Configuration configuration = getConfiguration();
        return configuration.containsKey(key.key) ? new File(configuration.getString(key.key)) : null;
    }

    private Configuration getConfiguration() {
        return configuration.getScopedConfiguration(ConfigurationScope.PROJECT);
    }
}
