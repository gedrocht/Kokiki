# Logging And Observability

The Spring Boot layer writes structured JSON logs so modern tools can ingest the
events without regular-expression parsing.

## Included building blocks

- Spring Boot Actuator for health, metrics, and readiness information:
  [docs](https://docs.spring.io/spring-boot/reference/actuator/index.html)
- Micrometer Prometheus registry for metrics scraping:
  [docs](https://micrometer.io/docs/registry/prometheus)
- Logstash Logback Encoder for JSON logs:
  [docs](https://github.com/logfellow/logstash-logback-encoder)
- Grafana Loki and Promtail for log aggregation:
  [Loki docs](https://grafana.com/docs/loki/latest/)
- Prometheus for metrics collection:
  [docs](https://prometheus.io/docs/introduction/overview/)

## Beginner explanation

Logs tell you **what happened**. Metrics tell you **how often or how much**.
Tracing tells you **where time was spent**. This repository focuses on logs and
metrics first because those are the most approachable foundations for a small
team.

## Local observability stack

The file `infra/observability/docker-compose.yml` starts a local Grafana, Loki,
Promtail, and Prometheus stack. That gives beginners a visual dashboard without
requiring any commercial tooling.
