package edu.tum.sse.jtec;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

public class JTeCGradlePlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        Task helloTask = project.task("hello")
                .doLast(task -> System.out.println("JTeC Gradle Plugin running!"));
        Task runTask = project.getTasks().getByName("run");
        Task testTask = project.getTasks().getByName("test");
        testTask.finalizedBy(helloTask);
    }
}
