package kafdrop.config;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.CommonClientConfigs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;


@Component
@ConfigurationProperties(prefix = "kafka")
public final class KafkaConfiguration {
  private static final Logger LOG = LoggerFactory.getLogger(KafkaConfiguration.class);

  private String brokerConnect;
  private String saslMechanism;
  private String securityProtocol;
  private String truststoreFile;
  private String propertiesFile;
  private String keystoreFile;

  public KafkaConfiguration() {
  }

  public KafkaConfiguration(String brokerConnect, String saslMechanism, String securityProtocol, String truststoreFile, String propertiesFile, String keystoreFile) {
    this.brokerConnect = brokerConnect;
    this.saslMechanism = saslMechanism;
    this.securityProtocol = securityProtocol;
    this.truststoreFile = truststoreFile;
    this.propertiesFile = propertiesFile;
    this.keystoreFile = keystoreFile;
  }

  public String getBrokerConnect() {
    return brokerConnect;
  }

  public void setBrokerConnect(String brokerConnect) {
    this.brokerConnect = brokerConnect;
  }

  public String getSaslMechanism() {
    return saslMechanism;
  }

  public void setSaslMechanism(String saslMechanism) {
    this.saslMechanism = saslMechanism;
  }

  public String getSecurityProtocol() {
    return securityProtocol;
  }

  public void setSecurityProtocol(String securityProtocol) {
    this.securityProtocol = securityProtocol;
  }

  public String getTruststoreFile() {
    return truststoreFile;
  }

  public void setTruststoreFile(String truststoreFile) {
    this.truststoreFile = truststoreFile;
  }

  public String getPropertiesFile() {
    return propertiesFile;
  }

  public void setPropertiesFile(String propertiesFile) {
    this.propertiesFile = propertiesFile;
  }

  public String getKeystoreFile() {
    return keystoreFile;
  }

  public void setKeystoreFile(String keystoreFile) {
    this.keystoreFile = keystoreFile;
  }

  public void applyCommon(Properties properties) {
    properties.setProperty(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, brokerConnect);

    if (securityProtocol.equals("SSL")) {
      properties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, securityProtocol);
    }

    LOG.info("Checking truststore file {}", truststoreFile);
    if (new File(truststoreFile).isFile()) {
      LOG.info("Assigning truststore location to {}", truststoreFile);
      properties.put("ssl.truststore.location", truststoreFile);
    }

    LOG.info("Checking keystore file {}", keystoreFile);
    if (new File(keystoreFile).isFile()) {
      LOG.info("Assigning keystore location to {}", keystoreFile);
      properties.put("ssl.keystore.location", keystoreFile);
    }

    LOG.info("Checking properties file {}", propertiesFile);
    Optional<AbstractResource> propertiesResource = StringUtils.isBlank(propertiesFile) ? Optional.empty() :
      Stream.of(new FileSystemResource(propertiesFile),
          new ClassPathResource(propertiesFile))
        .filter(Resource::isReadable)
        .findFirst();
    if (propertiesResource.isPresent()) {
      LOG.info("Loading properties from {}", propertiesFile);
      final var propertyOverrides = new Properties();
      try {
        propertyOverrides.load(propertiesResource.get().getInputStream());
      } catch (IOException e) {
        throw new KafkaConfigurationException(e);
      }
      properties.putAll(propertyOverrides);
    }
  }
}
