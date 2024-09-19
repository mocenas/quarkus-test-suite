package io.quarkus.ts.qe.command;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

import io.quarkus.arc.profile.IfBuildProfile;
import io.quarkus.picocli.runtime.annotations.TopCommand;

@ApplicationScoped
public class Config {
    @Produces
    @TopCommand
    @IfBuildProfile("dev")
    public Object devCommand() {
        return EntryCommand.class;
    }

    @Produces
    @TopCommand
    @IfBuildProfile("prod")
    public Object prodCommand() {
        return WtfCommand.class;
    }
}
