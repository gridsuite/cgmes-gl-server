package org.gridsuite.cgmes.gl.server.utils;

import com.powsybl.iidm.network.Country;
import com.powsybl.iidm.network.Line;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.Substation;
import com.powsybl.iidm.network.extensions.LinePosition;
import com.powsybl.iidm.network.extensions.SubstationPosition;
import org.gridsuite.cgmes.gl.server.dto.LineGeoData;
import org.gridsuite.cgmes.gl.server.dto.SubstationGeoData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class TestUtils {
    private TestUtils() {
        throw new IllegalCallerException("Utility class");
    }

    public static void checkExtensions(Network network, Set<Country> countries) {
        List<SubstationPosition> substationPositions = network.getSubstationStream()
                .map(s -> (SubstationPosition) s.getExtension(SubstationPosition.class))
                .filter(Objects::nonNull)
                .filter(s -> countries.isEmpty() || s.getExtendable().getCountry().map(countries::contains).orElse(false))
                .toList();

        List<LinePosition<Line>> linePositions = new ArrayList<>();
        for (Line line : network.getLines()) {
            LinePosition<Line> linePosition = line.getExtension(LinePosition.class);
            Country country1 = line.getTerminal1().getVoltageLevel().getSubstation().flatMap(Substation::getCountry).orElse(null);
            Country country2 = line.getTerminal2().getVoltageLevel().getSubstation().flatMap(Substation::getCountry).orElse(null);
            if (linePosition != null && (countries.isEmpty() || countries.contains(country1) || countries.contains(country2))) {
                linePositions.add(linePosition);
            }
        }

        List<LineGeoData> lines = linePositions.stream().map(LineGeoData::fromLinePosition).toList();
        assertEquals(2, lines.size());

        List<SubstationGeoData> substations = substationPositions.stream().map(SubstationGeoData::fromSubstationPosition).toList();
        assertEquals(2, substations.size());
    }
}
