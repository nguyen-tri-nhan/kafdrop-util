package com.nhannguyen

import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import kafdrop.model.TopicVO
import kafdrop.service.KafkaMonitor
import kafdrop.service.KafkaMonitorImpl

@Path("/kafdrop")
class ExampleResource(
  private val kafkaMonitor: KafkaMonitorImpl
) {

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  fun getKafkaMonitor(): List<TopicVO> {
    return kafkaMonitor.topics
  }

}
