/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.cgmes.gl.server.dto;

import com.powsybl.iidm.network.Country;
import com.powsybl.iidm.network.Substation;
import lombok.*;
import com.powsybl.iidm.network.extensions.SubstationPosition;

/**
 * @author Chamseddine Benhamed <chamseddine.benhamed at rte-france.com>
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class SubstationGeoData {

    private String id;

    private Country country;

    private Coordinate coordinate;

    public static SubstationGeoData fromSubstationPosition(SubstationPosition substationPosition) {
        Substation s = substationPosition.getExtendable();
        return new SubstationGeoData(s.getId(), s.getCountry().orElse(null), new Coordinate(substationPosition.getCoordinate().getLatitude(), substationPosition.getCoordinate().getLongitude()));
    }
}
