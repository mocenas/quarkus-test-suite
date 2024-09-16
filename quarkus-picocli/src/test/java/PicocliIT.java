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
    static final RestService greetingApp = new RestService()
            .withProperty("quarkus.args",
                    GREETING_COMMAND_DESC + " " + GREETING_NAME_OPTION + " " + GREETING_NAME_VALUE)
            .setAutoStart(false);

    @QuarkusApplication
    static final RestService ageApp = new RestService()
            .withProperty("quarkus.args",
                    AGE_COMMAND_DESC + " " + AGE_OPTION + " " + AGE_VALUE)
            .setAutoStart(false);

    @QuarkusApplication
    static final RestService greetingBlankArgumentApp = new RestService()
            .withProperty("quarkus.args",
                    "" + " " + GREETING_NAME_OPTION + " " + GREETING_NAME_VALUE)
            .setAutoStart(false);

    @QuarkusApplication
    static final RestService greetingInvalidArgumentApp = new RestService()
            .withProperty("quarkus.args",
                    GREETING_COMMAND_DESC + " " + "-x" + " " + GREETING_NAME_VALUE)
            .setAutoStart(false);

    @QuarkusApplication
    static final RestService bothTopCommandApp = new RestService()
            .setAutoStart(false);

    @QuarkusApplication
    static final RestService customizedCommandApp = new RestService()
            .withProperty("quarkus.args", "customized-command")
            .setAutoStart(false);

    @Test
    public void verifyErrorForApplicationScopedBeanInPicocliCommand() {
        String expectedAgeOutput = String.format("Your age is: %s", AGE_VALUE);
        try {
            runAsync(ageApp::start);
            ageApp.logs().assertContains(expectedAgeOutput);
        } finally {
            ageApp.stop();
        }
    }

    @Test
    public void verifyGreetingCommandOutputsExpectedMessage() {
        String expectedOutput = String.format("Hello %s!", GREETING_NAME_VALUE);
        try {
            runAsync(greetingApp::start);
            greetingApp.logs().assertContains(expectedOutput);
        } finally {
            greetingApp.stop();
        }

    }

    @Test
    void verifyErrorForBlankArgumentsInGreetingCommand() {
        String expectedError = "Unmatched arguments from index 0: '', '--name', 'QE'";
        try {
            runAsync(greetingBlankArgumentApp::start);
            greetingBlankArgumentApp.logs().assertContains(expectedError);
        } finally {
            greetingBlankArgumentApp.stop();
        }
    }

    @Test
    void verifyErrorForInvalidArgumentsInGreetingCommand() {
        String expectedError = "Unknown options: '-x', 'QE'";
        try {
            runAsync(greetingInvalidArgumentApp::start);
            greetingInvalidArgumentApp.logs().assertContains(expectedError);
        } finally {
            greetingInvalidArgumentApp.stop();
        }
    }

    /**
     * Chain Commands in a Single Execution is not possible
     */
    @Test
    public void verifyErrorForMultipleCommandsWithoutTopCommand() {
        bothTopCommandApp
                .withProperty("quarkus.args",
                        GREETING_COMMAND_DESC + " " + GREETING_NAME_OPTION + " " + "EEUU" + " " +
                                AGE_COMMAND_DESC + " " + AGE_OPTION + " " + "247");
        String errorMessage = "Unmatched arguments from index 3: 'age', '--age', '247'";

        try {
            runAsync(bothTopCommandApp::start);
            bothTopCommandApp.logs().assertContains(errorMessage);
        } finally {
            bothTopCommandApp.stop();
        }
    }

    @Test
    public void verifyCustomizedCommandLineBehavior() {
        String expectedOutput = "customized";
        try {
            runAsync(customizedCommandApp::start);
            customizedCommandApp.logs().assertContains(expectedOutput);
        } finally {
            customizedCommandApp.stop();
        }
    }
}
