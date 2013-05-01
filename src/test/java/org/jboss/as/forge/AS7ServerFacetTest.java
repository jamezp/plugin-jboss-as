package org.jboss.as.forge;

import static org.junit.Assert.*;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.packaging.PackagingType;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@RunWith(Arquillian.class)
public class AS7ServerFacetTest extends AbstractShellTest {
    @Test
    public void testAS7Setup() throws Exception {
        Project project = initializeProject(PackagingType.WAR);
        System.out.println(AS7Plugin.class);
        assertFalse(project.hasFacet(AS7ServerFacet.class));
        queueInputLines("");
        getShell().execute("set ACCEPT_DEFAULTS true");
        queueInputLines("");
        getShell().execute("setup as7");
        assertTrue(project.hasFacet(AS7ServerFacet.class));
    }

}
