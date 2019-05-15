package feign.template;

import static org.assertj.core.api.Assertions.fail;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.template.tck.TestCase;
import feign.template.tck.TestGroup;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test Cases that use the TCK provided by
 * <a href="https://github.com/uri-templates/uritemplate-test">uri-templates/uritemplate-tests</a>
 */
class UriTemplateTckTests {

  private static final Logger logger = LoggerFactory.getLogger(UriTemplateTckTests.class);
  private static final List<String> testDefinitions = Arrays.asList(
      "/template/spec-examples.json",
      "/template/spec-examples-by-section.json",
      "/template/negative-tests.json",
      "/template/extended-tests.json",
      "/template/feign-tests.json");
  private static Map<String, TestGroup> examples = new LinkedHashMap<>();

  /**
   * Read in the {@link feign.template.tck.TestExamples} defined in the test definitions.
   */
  @BeforeAll
  static void prepareTestDefinitions() {
    Class<?> clazz = UriTemplateTckTests.class;
    ObjectMapper objectMapper = new ObjectMapper();

    for (String definition : testDefinitions) {
      try {
        TypeReference<HashMap<String, TestGroup>> typeReference =
            new TypeReference<HashMap<String, TestGroup>>() {};
        Map<String, TestGroup> map = objectMapper.readValue(
            clazz.getResourceAsStream(definition), typeReference);
        examples.putAll(map);
      } catch (Exception ex) {
        throw new RuntimeException(ex);
      }
    }
  }

  @Test
  void verify() {
    AtomicInteger failureCount = new AtomicInteger(0);
    examples.forEach((name, testGroup) -> {

      /* create a new uri template from test group */
      Map<String, Object> variables = testGroup.getVariables();
      for (TestCase testCase : testGroup.getTestCases()) {
        try {
          UriTemplate uriTemplate = UriTemplate.create(testCase.getExpression());
          String result = uriTemplate.expand(variables).toString();
          if (!testCase.getResults().contains(result)) {
            throw new IllegalStateException("Result: " + result + " not found in: " + testCase.getResults());
          } else {
            logger.info("Test Passed: " + name + ": " + testCase.getExpression() + ": " + result);
          }
        } catch (Exception ex) {
          if (!testCase.getResults().contains("false")) {
            failureCount.incrementAndGet();
            System.err.println("Test Case Failed: " + name + ":" + testGroup.getLevel() + ": " + testCase.getExpression() + ": " + ex.getClass().getName() + ":" + ex.getMessage());
          } else {
            logger.info("Test Passed: " + name + ": " + testCase.getExpression() + ": failure");
          }
        }
      }
    });
    if (failureCount.intValue() != 0) {
      fail("UriTemplate Verification Failed.");
    }
  }
}
