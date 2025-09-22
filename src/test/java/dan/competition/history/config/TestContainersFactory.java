package dan.competition.history.config;

import org.testcontainers.containers.PostgreSQLContainer;
//import org.wiremock.integrations.testcontainers.WireMockContainer;

/**
 * Синглтон фабрика тест контейнеров.
 */
public class TestContainersFactory {

    private static final String POSTGRES_IMAGE = "postgres:17.6-alpine3.21";
//    private static final String WIREMOCK_IMAGE = "wiremock/wiremock:2.33.2";

    public static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(POSTGRES_IMAGE)
            .withDatabaseName("test")
            .withUsername("sa")
            .withPassword("sa");
//
//    public static final WireMockContainer WIREMOCK_CONTAINER = new WireMockContainer(WIREMOCK_IMAGE)
//            .withClasspathResourceMapping("wiremock", "/home/wiremock", BindMode.READ_ONLY)
//            .waitingFor(Wait.forHttp("/__admin").forStatusCode(200));

    static {
        POSTGRES.start();
//        WIREMOCK_CONTAINER.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            POSTGRES.stop();
//            WIREMOCK_CONTAINER.stop();
        }));
    }

    private TestContainersFactory() {
    }
}