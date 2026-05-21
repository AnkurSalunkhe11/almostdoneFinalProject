package utilities;

import io.cucumber.java.Scenario;
import java.util.HashMap;
import java.util.Map;

/**
 * Shared context to hold the current Cucumber Scenario and other test data.
 * This allows step definitions to access the scenario and share parameters.
 * PicoContainer can inject this class into both Hooks and StepDefinitions.
 */
public class ScenarioContext {
    private Scenario scenario;
    private final Map<String, Object> context = new HashMap<>();

    public ScenarioContext() {
    }

    public void setScenario(Scenario scenario) {
        this.scenario = scenario;
    }

    public Scenario getScenario() {
        return scenario;
    }

    public void setContext(String key, Object value) {
        context.put(key, value);
    }

    public Object getContext(String key) {
        return context.get(key);
    }
}
