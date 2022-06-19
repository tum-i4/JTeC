package edu.tum.sse.jtec.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import static edu.tum.sse.jtec.util.IOUtils.removeDir;

/**
 * This Mojo is used to clean the jtec directory inside the target directory.
 */
@Mojo(name = "clean", defaultPhase = LifecyclePhase.CLEAN, threadSafe = true)
public class JTeCCleanMojo extends AbstractJTeCMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            removeDir(outputDirectory.toPath());
        } catch (Exception exception) {
            getLog().error("Error cleaning JTeC directory.");
            exception.printStackTrace();
        }
    }
}
