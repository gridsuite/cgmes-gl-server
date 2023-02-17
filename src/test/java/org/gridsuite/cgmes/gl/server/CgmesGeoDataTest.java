/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.cgmes.gl.server;

import com.powsybl.cgmes.conformity.CgmesConformity1Catalog;
import com.powsybl.cgmes.conversion.CgmesImport;
import com.powsybl.cgmes.model.GridModelReferenceResources;
import org.gridsuite.cgmes.gl.server.dto.LineGeoData;
import org.gridsuite.cgmes.gl.server.dto.SubstationGeoData;
import com.powsybl.iidm.network.extensions.LinePosition;
import com.powsybl.iidm.network.extensions.SubstationPosition;
import com.powsybl.iidm.network.Line;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.impl.NetworkFactoryImpl;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

/**
 * @author Chamseddine Benhamed <chamseddine.benhamed at rte-france.com>
 */
public class CgmesGeoDataTest {
    private static final Logger LOG = LoggerFactory.getLogger(CgmesGeoDataTest.class);

    @Test
    public void test() {
        GridModelReferenceResources gridModel = CgmesConformity1Catalog.microGridBaseCaseBE();

        Properties properties = new Properties();
        properties.put("iidm.import.cgmes.post-processors", "cgmesGLImport");

        CgmesImport cgmesImporter = new CgmesImport();
        Network network = cgmesImporter.importData(gridModel.dataSource(), new NetworkFactoryImpl(), properties);

        List<SubstationPosition> substationPositions = network.getSubstationStream()
                .map(s -> (SubstationPosition) s.getExtension(SubstationPosition.class))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        List<LinePosition<Line>> linePositions = new ArrayList<>();
        for (Line line : network.getLines()) {
            LinePosition<Line> linePosition = line.getExtension(LinePosition.class);
            if (linePosition != null) {
                linePositions.add(linePosition);
            }
        }

        assertEquals(2, substationPositions.size());
        assertEquals(2, linePositions.size());

        List<LineGeoData> lines = linePositions.stream()
                .map(LineGeoData::fromLinePosition)
                .collect(Collectors.toList());
        List<SubstationGeoData> substations = substationPositions.stream().map(SubstationGeoData::fromSubstationPosition).collect(Collectors.toList());

        assertEquals(2, substations.size());
        assertEquals(2, lines.size());

        assertEquals(51.3251, substations.get(0).getCoordinate().getLat(), 0);
        assertEquals(4.25926, substations.get(0).getCoordinate().getLon(), 0);
    }
}
