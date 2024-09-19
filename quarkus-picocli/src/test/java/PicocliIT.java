import static org.junit.jupiter.api.Assertions.assertTrue;

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
    static final RestService age = new RestService()
            .withProperty("quarkus.args",
                    AGE_COMMAND_DESC + " " + AGE_OPTION + " " + AGE_VALUE);

    @Test
    public void verifyErrorForApplicationScopedBeanInPicocliCommand() {
        String expectedAgeOutput = String.format("Your age is: %s", AGE_VALUE);
        String response = age.given().get("/hello/configured").body().asString();

        assertTrue(response.contains(expectedAgeOutput),
                "Response " + response + " should contain expected text: " + expectedAgeOutput);
    }
}
