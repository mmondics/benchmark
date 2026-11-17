# OpenMessaging Benchmark Framework

[![Build](https://github.com/openmessaging/benchmark/actions/workflows/pr-build-and-test.yml/badge.svg)](https://github.com/openmessaging/benchmark/actions/workflows/pr-build-and-test.yml)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

This repository houses user-friendly, cloud-ready benchmarking suites for messaging platforms including:

* [Apache ActiveMQ Artemis](https://activemq.apache.org/components/artemis/)
* [Apache Bookkeeper](https://bookkeeper.apache.org)
* [Apache Kafka](https://kafka.apache.org)
* [Apache Pulsar](https://pulsar.apache.org)
* [Apache RocketMQ](https://rocketmq.apache.org)
* Generic [JMS](https://javaee.github.io/jms-spec/)
* [KoP (Kafka-on-Pulsar)](https://github.com/streamnative/kop)
* [NATS JetStream](https://docs.nats.io/nats-concepts/jetstream)
* [NATS Streaming (STAN)](https://docs.nats.io/legacy/stan/intro)
* [NSQ](https://nsq.io)
* [Pravega](https://pravega.io/)
* [RabbitMQ](https://www.rabbitmq.com/)
* [Redis](https://redis.com/)
* [MQTT](https://mqtt.org/)

> More details could be found at the [official documentation](http://openmessaging.cloud/docs/benchmarks/).

---

## Fork Extensions & New Features

This fork extends the OpenMessaging Benchmark framework with three key capabilities:

1. **Targeting Pre-Existing Topics (`existingTopic`)**: Bypass topic auto-creation and deletion to benchmark secured, existing, or pre-partitioned topics.
2. **Multi-Payload JSON Array Sampling (`payloadJsonArrayFile`)**: Randomly sample messages from a JSON array pool for realistic payload variance and compression testing.
3. **Producer-Only Workload Probe Bypass**: Automatically skip consumer synchronization probes when running publish-only ingestion tests (`subscriptionsPerTopic: 0`).

### Workload Configuration Options

| Parameter | Type | Required | Description |
| :--- | :--- | :--- | :--- |
| `existingTopic` | `string` | Optional | Name of an existing topic to publish to. Skips topic creation and deletion. Set `topics: 1` when using this flag. |
| `payloadJsonArrayFile` | `string` | Optional | Path to a `.json` file containing a top-level JSON array. Overrides `messageSize` and `payloadFile`. |
| `payloadFile` | `string` | Optional | Path to a single static payload file. Requires `messageSize` to match the exact byte size. |
| `subscriptionsPerTopic` | `int` | Optional | Set to `0` for publish-only (ingestion) benchmarking. |
| `consumerPerSubscription` | `int` | Optional | Set to `0` for publish-only benchmarking. |

### Workload Examples

#### Example 1: JSON Array Multi-Payload Sampling on Existing Topic
```yaml
name: Existing Topic - Multi-Payload JSON Array Sampling

# Target an existing topic (no topics are created or deleted)
existingTopic: my-target-topic
topics: 1
partitionsPerTopic: 1

# Path to the JSON array file (each array element is one distinct payload)
payloadJsonArrayFile: "payload/sample-records.json"

# Ingestion-only test (no consumers)
subscriptionsPerTopic: 0
consumerPerSubscription: 0

# Producer rate and concurrency
producersPerTopic: 4
producerRate: 5000           # Total msg/s across all producers
consumerBacklogSizeGB: 0

warmupDurationMinutes: 1
testDurationMinutes: 5
```

#### Example 2: Static Single File Payload on Existing Topic
```yaml
name: Existing Topic - Single Static Payload

existingTopic: my-target-topic
topics: 1
partitionsPerTopic: 1

# Byte length must match the payload file size exactly
messageSize: 13001
payloadFile: "payload/single-record.json"

subscriptionsPerTopic: 0
consumerPerSubscription: 0
producersPerTopic: 1
producerRate: 1000

warmupDurationMinutes: 0
testDurationMinutes: 2
```

### JSON Array Payload Format

When using `payloadJsonArrayFile`, provide a valid top-level JSON array `[...]`:

```json
[
  {
    "id": "1001",
    "eventType": "ACCOUNT_UPDATE",
    "timestamp": 1700000000000,
    "payload": {
      "status": "ACTIVE",
      "balance": 1500.50
    }
  },
  {
    "id": "1002",
    "eventType": "ACCOUNT_UPDATE",
    "timestamp": 1700000001000,
    "payload": {
      "status": "PENDING",
      "balance": 820.00
    }
  }
]
```

* Each array element is compacted and serialized to UTF-8 bytes at startup.
* For each message sent, a thread-safe random selector picks an element from the pool.

---

## Build

Requirements:

* JDK 17
* Maven 3.8.6+

Common build actions:

| Action | Command |
| :--- | :--- |
| Full build and test | `mvn clean verify` |
| Skip tests | `mvn clean verify -DskipTests` |
| Skip Jacoco test coverage check | `mvn clean verify -Djacoco.skip` |
| Skip Checkstyle standards check | `mvn clean verify -Dcheckstyle.skip` |
| Skip Spotless formatting check | `mvn clean verify -Dspotless.check.skip` |
| Format code | `mvn spotless:apply` |
| Generate license headers | `mvn license:format` |

### Running a Benchmark

```bash
bin/benchmark \
  --drivers driver-kafka/kafka-throughput.yaml \
  workloads/sample-workload.yaml
```

---

## License

Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
