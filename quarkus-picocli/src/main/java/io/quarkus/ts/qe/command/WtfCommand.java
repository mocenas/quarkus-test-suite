package io.quarkus.ts.qe.command;

import io.quarkus.logging.Log;

import picocli.CommandLine;

//@TopCommand
@CommandLine.Command(name = "customized-command", mixinStandardHelpOptions = true, subcommands = { HelloCommand.class,
        AgeCommand.class })
public class WtfCommand implements Runnable {
    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;

    @Override
    public void run() {
        Log.info("Running Wtf command with name: " + spec.name());
    }
}
