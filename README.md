# tiny-prometheus ![Static Badge](https://img.shields.io/badge/version-0.0.5-blue) ![Endpoint Badge](https://img.shields.io/endpoint?url=https%3A%2F%2Fgist.githubusercontent.com%2Ftarmolehtpuu%2Fc4dcc625848d9d5fe24d9671308939e4%2Fraw%2Ff049433b9f8896eab32731c13a325b571e6b7dfe%2Ftiny-prometheus-junit-tests.json) ![Endpoint Badge](https://img.shields.io/endpoint?url=https%3A%2F%2Fgist.githubusercontent.com%2Ftarmolehtpuu%2Fc4dcc625848d9d5fe24d9671308939e4%2Fraw%2Ff049433b9f8896eab32731c13a325b571e6b7dfe%2Ftiny-prometheus-jacoco-coverage.json)


Tiny Prometheus library for use in apps (mainly expoters) built with GraalVM Native Image. This library uses no reflection
and is very tiny.

Supported metric types:
- counter
- gauge

Supports exporting metrics in Prometheus format (version 0.0.5). Also planning to add some simple query methods in
a future version.

## Import

### Maven

```xml
<repositories>
    <repository>
        <id>moo</id>
        <url>https://repo.repsy.io/moo/maven</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>ee.moo</groupId>
        <artifactId>tiny-prometheus</artifactId>
        <version>0.0.5</version>
    </dependency>
</dependencies>
```

### Gradle

```kotlin
repositories {
    mavenCentral()
    maven {
        name = "moo"
        url = uri("https://repo.repsy.io/moo/maven")
    }
}

dependencies {
    implementation("ee.moo:tiny-prometheus:0.0.5")
}
```

## Usage

### Export Metrics

```java


public String export() {
    var labels = List.of(
        new Label("foo", "123"),
        new Label("foo_type", "bar")
    );

    var m1 = Metric.counter(
        "foo_uptime_total",
        "Time since last boot in seconds"
    );
    var m2 = Metric.gauge(
        "foo_alive",
        "If foo is currently alive"
    );

    var chips = List.of(
        new Chip("chip1", true),
        new Chip("chip2", true),
        new Chip("chip3", false)
    );

    var items = new ArrayList<MetricWithSamples>();
    
    items.add(new MetricWithSamples(m1, new Sample(labels, 12345)));
    items.add(new MetricWithSamples(
        m2,
        chips.stream()
            .map(chip -> new Sample(
                labels,
                List.of(new Label("chip_id", chip.id)),
                chip.alive)
            )
            .toList()
    ));
    
    System.out.println(new MetricsExporter(metrics).export());
}
```
### Output

```prometheus
# HELP foo_uptime_total Time since last boot in seconds
# TYPE foo_uptime_total counter
foo_uptime_total{foo="123",foo_type="bar"} 12345.0
# HELP foo_alive If foo is currently alive
# TYPE foo_alive gauge
foo_alive{foo="123",foo_type="bar",chip_id="chip1"} 1.0
foo_alive{foo="123",foo_type="bar",chip_id="chip2"} 1.0
foo_alive{foo="123",foo_type="bar",chip_id="chip3"} 0.0
```

### Query Prometheus

- TODO
