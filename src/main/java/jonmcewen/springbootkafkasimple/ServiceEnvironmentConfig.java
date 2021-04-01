package jonmcewen.springbootkafkasimple;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

/** Configuration properties for the service // todo can this be made immutable? */
@Configuration
@ConfigurationProperties(prefix = "service")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceEnvironmentConfig {
  @NestedConfigurationProperty private TopicsConfig topics;

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class TopicsConfig {

    private String consumeFrom;
    private String produceTo;
  }
}
