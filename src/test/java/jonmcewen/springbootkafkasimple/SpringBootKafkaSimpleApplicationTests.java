package jonmcewen.springbootkafkasimple;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles(profiles = "local")
@EmbeddedKafka(
    bootstrapServersProperty = "spring.kafka.bootstrap-servers",
    partitions = 1,
    brokerProperties = {"log.dirs=./kafka-logs/"})
@TestInstance(Lifecycle.PER_CLASS)
class SpringBootKafkaSimpleApplicationTests {

  @Autowired ServiceEnvironmentConfig serviceEnvironmentConfig;
  @Autowired private KafkaTemplate<String, String> kafkaTemplate;
  @Autowired private ConsumerFactory<String, String> consumerFactory;
  private final BlockingQueue<String> outputs = new LinkedBlockingQueue<>();

  @BeforeAll
  public void startOutputListener() {
    ContainerProperties containerProperties =
        new ContainerProperties(serviceEnvironmentConfig.getTopics().getProduceTo());
    KafkaMessageListenerContainer<String, String> container =
        new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);

    container.setupMessageListener(
        (MessageListener<String, String>)
            consumerRecord -> {
              outputs.add(consumerRecord.value());
            });
    container.setBeanName("applicationTestListenerContainer");
    container.start();
    ContainerTestUtils.waitForAssignment(container, 1);
  }

  @Test
  void contextLoads() {}

  @Test
  void processesMessage() throws Exception {
    String message = "Hello world";
    kafkaTemplate.send(serviceEnvironmentConfig.getTopics().getConsumeFrom(), message);
    String actual = outputs.poll(1, TimeUnit.SECONDS);
    assertEquals(message, actual);
  }
}
