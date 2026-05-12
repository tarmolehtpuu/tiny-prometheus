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

public class ReadmeTest {

    @Test
    public void testReadme() {
        var labels = List.of(
            new Label("foo", "123"),
            new Label("foo_type", "bar")
        );

        var m1 = Metric.counter("foo_uptime_total", "Time since last boot in seconds");
        var m2 = Metric.gauge("foo_alive", "If foo is currently alive");

        var items = new ArrayList<MetricWithSamples>();
        var chips = List.of(
            new Chip("chip1", true),
            new Chip("chip2", true),
            new Chip("chip3", false)
        );

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

        var expected = """
            # HELP foo_uptime_total Time since last boot in seconds
            # TYPE foo_uptime_total counter
            foo_uptime_total{foo="123",foo_type="bar"} 12345.0
            # HELP foo_alive If foo is currently alive
            # TYPE foo_alive gauge
            foo_alive{foo="123",foo_type="bar",chip_id="chip1"} 1.0
            foo_alive{foo="123",foo_type="bar",chip_id="chip2"} 1.0
            foo_alive{foo="123",foo_type="bar",chip_id="chip3"} 0.0
            """;

        System.out.println(new PrometheusExporter(items).export());
        assertEquals(expected, new PrometheusExporter(items).export());
    }

    public record Chip(String id, double alive) {
        public Chip(String id, boolean alive) {
            this(id, alive ? 1.0 : 0.0);
        }
    }
}
