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
import com.powsybl.iidm.network.Network;
import org.gridsuite.cgmes.gl.server.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import java.util.HashSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

/**
 * @author Chamseddine Benhamed <chamseddine.benhamed at rte-france.com>
 */
@ExtendWith(MockitoExtension.class)
class CgmesGlServiceTest {

    @Mock
    private RestTemplate geoDataServerRest; //halt http client

    @Mock
    private CaseDataSourceClient caseServerDataSource; //halt database

    @Mock
    private RestTemplateBuilder restTemplateBuilder;

    private CgmesGlService cgmesGlService;

    private static final UUID CASE_UUID = UUID.randomUUID();

    @BeforeEach
    void mockCaseServer() {
        when(restTemplateBuilder.uriTemplateHandler(Mockito.any())).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.build()).thenReturn(geoDataServerRest);
        cgmesGlService = Mockito.spy(new CgmesGlService("https://localhost:8087", "https://localhost:8085", restTemplateBuilder));

        GridModelReferenceResources gridModel = CgmesConformity1Catalog.microGridBaseCaseBE();

        when(caseServerDataSource.newInputStream(anyString())).then(delegatesTo(gridModel.dataSource()));
        when(caseServerDataSource.getBaseName()).then(delegatesTo(gridModel.dataSource()));
        when(caseServerDataSource.listNames(anyString())).then(delegatesTo(gridModel.dataSource()));
        when(caseServerDataSource.exists(anyString())).then(delegatesTo(gridModel.dataSource()));

        doReturn(caseServerDataSource).when(cgmesGlService).createCaseServerDataSource(CASE_UUID);
    }

    @Test
    void test() {
        Network network = cgmesGlService.getNetwork(CASE_UUID);
        assertNotNull(network);

        TestUtils.checkExtensions(network, new HashSet<>());

        cgmesGlService.toGeoDataServer(CASE_UUID, new HashSet<>());
        //TODO missing assertion(s) of result
    }
}
