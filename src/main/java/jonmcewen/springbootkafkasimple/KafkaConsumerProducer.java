package jonmcewen.springbootkafkasimple;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaConsumerProducer {

  private final ServiceEnvironmentConfig serviceEnvironmentConfig;
  private final KafkaTemplate kafkaTemplate;

  @KafkaListener(topics = "#{serviceEnvironmentConfig.topics.consumeFrom}")
  public void consume(ConsumerRecord<String, String> consumerRecord) {
    kafkaTemplate.send(serviceEnvironmentConfig.getTopics().getProduceTo(), consumerRecord.value());
  }
}
