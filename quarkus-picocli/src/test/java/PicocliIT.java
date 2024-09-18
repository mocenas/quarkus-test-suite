import static java.util.concurrent.CompletableFuture.runAsync;

import org.junit.jupiter.api.Test;

import io.quarkus.test.bootstrap.RestService;
import io.quarkus.test.scenarios.QuarkusScenario;
import io.quarkus.test.services.QuarkusApplication;

@QuarkusScenario
public class PicocliIT {

    static final String GREETING_COMMAND_DESC = "greeting";
    static final String GREETING_NAME_OPTION = "--name";
    static final String GREETING_NAME_VALUE = "QE";

    static final String AGE_COMMAND_DESC = "age";
    static final String AGE_OPTION = "--age";
    static final String AGE_VALUE = "30";

    @QuarkusApplication
    static final RestService greeting = new RestService()
            .withProperty("quarkus.args",
                    GREETING_COMMAND_DESC + " " + GREETING_NAME_OPTION + " " + GREETING_NAME_VALUE)
            .setAutoStart(false);

    @QuarkusApplication
    static final RestService age = new RestService()
            .withProperty("quarkus.args",
                    AGE_COMMAND_DESC + " " + AGE_OPTION + " " + AGE_VALUE)
            .setAutoStart(false);

    @QuarkusApplication
    static final RestService blank = new RestService()
            .withProperty("quarkus.args",
                    "" + " " + GREETING_NAME_OPTION + " " + GREETING_NAME_VALUE)
            .setAutoStart(false);

    @QuarkusApplication
    static final RestService invalid = new RestService()
            .withProperty("quarkus.args",
                    GREETING_COMMAND_DESC + " " + "-x" + " " + GREETING_NAME_VALUE)
            .setAutoStart(false);

    @QuarkusApplication
    static final RestService topcommand = new RestService()
            .setAutoStart(false);

    @QuarkusApplication
    static final RestService customized = new RestService()
            .withProperty("quarkus.args", "customized-command")
            .setAutoStart(false);

    @Test
    public void verifyErrorForApplicationScopedBeanInPicocliCommand() {
        String expectedAgeOutput = String.format("Your age is: %s", AGE_VALUE);
        try {
            runAsync(age::start);
            age.logs().assertContains(expectedAgeOutput);
        } finally {
            age.stop();
        }
    }

    @Test
    public void verifyGreetingCommandOutputsExpectedMessage() {
        String expectedOutput = String.format("Hello %s!", GREETING_NAME_VALUE);
        try {
            runAsync(greeting::start);
            greeting.logs().assertContains(expectedOutput);
        } finally {
            greeting.stop();
        }

    }

    @Test
    void verifyErrorForBlankArgumentsInGreetingCommand() {
        String expectedError = "Unmatched arguments from index 0: '', '--name', 'QE'";
        try {
            runAsync(blank::start);
            blank.logs().assertContains(expectedError);
        } finally {
            blank.stop();
        }
    }

    @Test
    void verifyErrorForInvalidArgumentsInGreetingCommand() {
        String expectedError = "Unknown options: '-x', 'QE'";
        try {
            runAsync(invalid::start);
            invalid.logs().assertContains(expectedError);
        } finally {
            invalid.stop();
        }
    }

    /**
     * Chain Commands in a Single Execution is not possible
     */
    @Test
    public void verifyErrorForMultipleCommandsWithoutTopCommand() {
        topcommand
                .withProperty("quarkus.args",
                        GREETING_COMMAND_DESC + " " + GREETING_NAME_OPTION + " " + "EEUU" + " " +
                                AGE_COMMAND_DESC + " " + AGE_OPTION + " " + "247");
        String errorMessage = "Unmatched arguments from index 3: 'age', '--age', '247'";

        try {
            runAsync(topcommand::start);
            topcommand.logs().assertContains(errorMessage);
        } finally {
            topcommand.stop();
        }
    }

    @Test
    public void verifyCustomizedCommandLineBehavior() {
        String expectedOutput = "customized";
        try {
            runAsync(customized::start);
            customized.logs().assertContains(expectedOutput);
        } finally {
            customized.stop();
        }
    }
}
