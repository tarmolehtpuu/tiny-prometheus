/*
   tiny-prometheus - Tiny Prometheus Helpers
   Copyright 2026 Tarmo Lehtpuu

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package ee.moo.tiny.prometheus;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExporterTest {

    @Test
    public void testExport() {
        var labels = List.of(
            new Label("id", 1),
            new Label("type", Type.TYPE_1)
        );

        var m1 = Metric.counter(
            "foo_total",
            "Total number of foo"
        );
        var m2 = Metric.counter(
            "bar_total",
            "Total number of bar"
        );
        var m3 = Metric.gauge(
            "foo_alive",
            "Is foo currently alive"
        );
        var m4 = Metric.gauge(
            "foo_active",
            "Is foo currently active"
        );
        var m5 = Metric.gauge(
            "foo_voltage",
            "Voltage at the moment"
        );
        var m6 = Metric.gauge(
            "foo_current",
            "Current at the moment"
        );
        var m7 = Metric.gauge(
            "foo_overheated",
            "Is foo currently overheated"
        );
        var m8 = Metric.gauge(
            "foo_online",
            "Is foo currently online"
        );
        var m9 = Metric.gauge(
            "foo_humidity",
            "Current humidity for foo"
        );

        var metrics = new ArrayList<MetricWithSamples>();

        metrics.add(new MetricWithSamples(m1, new Sample(4)));
        metrics.add(new MetricWithSamples(m2, new Sample(5.7)));
        metrics.add(new MetricWithSamples(m3, new Sample(true)));
        metrics.add(new MetricWithSamples(m4, new Sample(false)));

        metrics.add(new MetricWithSamples(m5, new Sample(
            List.of(new Label("id", 1)),
            220.0
        )));
        metrics.add(new MetricWithSamples(m6, new Sample(
            List.of(new Label("id", 1)),
            4
        )));
        metrics.add(new MetricWithSamples(m7, List.of(
            new Sample(List.of(new Label("type", "PCB")), false),
            new Sample(List.of(new Label("type", "CHIP")), true)
        )));
        metrics.add(new MetricWithSamples(m8, List.of(
            new Sample(labels, List.of(new Label("type", "PCB")), false),
            new Sample(labels, List.of(new Label("type", "CHIP")), true)
        )));
        metrics.add(new MetricWithSamples(m9, List.of(
            new Sample(labels, List.of(new Label("type", "INSIDE")), 24),
            new Sample(labels, List.of(new Label("type", "OUTSIDE")), 44)
        )));

        var expected = """
            # HELP foo_total Total number of foo
            # TYPE foo_total counter
            foo_total 4.0
            # HELP bar_total Total number of bar
            # TYPE bar_total counter
            bar_total 5.7
            # HELP foo_alive Is foo currently alive
            # TYPE foo_alive gauge
            foo_alive 1.0
            # HELP foo_active Is foo currently active
            # TYPE foo_active gauge
            foo_active 0.0
            # HELP foo_voltage Voltage at the moment
            # TYPE foo_voltage gauge
            foo_voltage{id="1"} 220.0
            # HELP foo_current Current at the moment
            # TYPE foo_current gauge
            foo_current{id="1"} 4.0
            # HELP foo_overheated Is foo currently overheated
            # TYPE foo_overheated gauge
            foo_overheated{type="PCB"} 0.0
            foo_overheated{type="CHIP"} 1.0
            # HELP foo_online Is foo currently online
            # TYPE foo_online gauge
            foo_online{id="1",type="TYPE_1",type="PCB"} 0.0
            foo_online{id="1",type="TYPE_1",type="CHIP"} 1.0
            # HELP foo_humidity Current humidity for foo
            # TYPE foo_humidity gauge
            foo_humidity{id="1",type="TYPE_1",type="INSIDE"} 24.0
            foo_humidity{id="1",type="TYPE_1",type="OUTSIDE"} 44.0
            """;

        assertEquals(expected, new PrometheusExporter(metrics).export());
    }

    public enum Type {
        TYPE_1,
        TYPE_2
    }
}
