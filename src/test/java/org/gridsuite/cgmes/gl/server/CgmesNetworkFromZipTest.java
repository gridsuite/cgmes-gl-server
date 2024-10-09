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
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.impl.NetworkFactoryImpl;
import org.gridsuite.cgmes.gl.server.utils.TestUtils;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Chamseddine Benhamed <chamseddine.benhamed at rte-france.com>
 */
class CgmesNetworkFromZipTest {

    @Test
    void test() {
        GridModelReferenceResources gridModel = CgmesConformity1Catalog.microGridBaseCaseBE();

        CgmesImport importer = new CgmesImport();
        Properties properties = new Properties();
        properties.put("iidm.import.cgmes.post-processors", "cgmesGLImport");
        Network network = importer.importData(gridModel.dataSource(), new NetworkFactoryImpl(), properties);
        assertNotNull(network);

        TestUtils.checkExtensions(network, new HashSet<>());
    }
}
