/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.cgmes.gl.server.services;

import com.powsybl.cases.datasource.CaseDataSourceClient;
import com.powsybl.cgmes.conformity.CgmesConformity1Catalog;
import com.powsybl.cgmes.model.GridModelReferenceResources;
import org.gridsuite.cgmes.gl.server.CgmesNetworkFromZipTest;
import com.powsybl.iidm.network.Network;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

import java.util.HashSet;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

/**
 * @author Chamseddine Benhamed <chamseddine.benhamed at rte-france.com>
 */
@RunWith(MockitoJUnitRunner.class)
public class CgmesGlServiceTest {

    @Mock
    private RestTemplate geoDataServerRest;

    @Mock
    private CaseDataSourceClient caseServerDataSource;

    @InjectMocks
    private CgmesGlService cgmesGlService =  Mockito.spy(new CgmesGlService("https://localhost:8087", "https://localhost:8085"));

    private static final UUID CASE_UUID = UUID.randomUUID();
    private static final String CASENAME = "CGMES_v2_4_15_MicroGridTestConfiguration_BC_BE_v2.zip";

    @Before
    public void mockCaseServer() {
        GridModelReferenceResources gridModel = CgmesConformity1Catalog.microGridBaseCaseBE();

        when(caseServerDataSource.newInputStream(anyString())).then(delegatesTo(gridModel.dataSource()));
        when(caseServerDataSource.getBaseName()).then(delegatesTo(gridModel.dataSource()));
        when(caseServerDataSource.listNames(anyString())).then(delegatesTo(gridModel.dataSource()));

        doReturn(caseServerDataSource).when(cgmesGlService).createCaseServerDataSource(CASE_UUID);
    }

    @Test
    public void test() {
        Network network = cgmesGlService.getNetwork(CASE_UUID);
        assertNotNull(network);

        CgmesNetworkFromZipTest.checkExtensions(network, new HashSet<>());

        cgmesGlService.toGeoDataServer(CASE_UUID, new HashSet<>());
    }
}
