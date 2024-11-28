/*
 * Copyright 2017 Kafdrop contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */

package kafdrop.client;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kafdrop.config.MessageFormatConfiguration;
import kafdrop.model.ConsumerVO;
import kafdrop.model.CreateTopicVO;
import kafdrop.model.TopicVO;
import kafdrop.service.KafkaMonitor;
import kafdrop.service.TopicNotFoundException;
import kafdrop.util.MessageFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.List;

public final class TopicClient {
  private final KafkaMonitor kafkaMonitor;
  private final boolean topicDeleteEnabled;
  private final boolean topicCreateEnabled;
  private final MessageFormatConfiguration.MessageFormatProperties messageFormatProperties;

  public TopicClient(KafkaMonitor kafkaMonitor,
                     @Value("${topic.deleteEnabled:true}") Boolean topicDeleteEnabled,
                     @Value("${topic.createEnabled:true}") Boolean topicCreateEnabled,
                     MessageFormatConfiguration.MessageFormatProperties messageFormatProperties) {
    this.kafkaMonitor = kafkaMonitor;
    this.topicDeleteEnabled = topicDeleteEnabled;
    this.topicCreateEnabled = topicCreateEnabled;
    this.messageFormatProperties = messageFormatProperties;
  }

  public TopicVO topicDetails(String topicName, Model model) {
    final MessageFormat defaultFormat = messageFormatProperties.getFormat();
    final MessageFormat defaultKeyFormat = messageFormatProperties.getKeyFormat();

    return kafkaMonitor.getTopic(topicName)
      .orElseThrow(() -> new TopicNotFoundException(topicName));
  }

  public void deleteTopic(String topicName, Model model) {
    if (!topicDeleteEnabled) {
      model.addAttribute("deleteErrorMessage", "Not configured to be deleted.");
    }

    try {
      kafkaMonitor.deleteTopic(topicName);
    } catch (Exception ex) {
      model.addAttribute("deleteErrorMessage", ex.getMessage());
    }
  }


  public TopicVO getTopic(String topicName) {
    return kafkaMonitor.getTopic(topicName)
      .orElseThrow(() -> new TopicNotFoundException(topicName));
  }

  public List<TopicVO> getAllTopics() {
    return kafkaMonitor.getTopics();
  }

  public List<ConsumerVO> getConsumers(String topicName) {
    final var topic = kafkaMonitor.getTopic(topicName)
      .orElseThrow(() -> new TopicNotFoundException(topicName));
    return kafkaMonitor.getConsumersByTopics(Collections.singleton(topic));
  }

  public String createTopic(CreateTopicVO createTopicVO, Model model) {
    model.addAttribute("topicCreateEnabled", topicCreateEnabled);
    model.addAttribute("topicName", createTopicVO.getName());
    if (!topicCreateEnabled) {
      model.addAttribute("errorMessage", "Not configured to be created.");
      return createTopicPage(model);
    }
    try {
      kafkaMonitor.createTopic(createTopicVO);
    } catch (Exception ex) {
      model.addAttribute("errorMessage", ex.getMessage());
    }
    model.addAttribute("brokersCount", kafkaMonitor.getBrokers().size());
    return "topic-create";
  }
}
