# CGMES GL Server

[![Actions Status](https://github.com/gridsuite/cgmes-gl-server/actions/workflows/build.yml/badge.svg?branch=main)](https://github.com/gridsuite/cgmes-gl-server/actions)
[![Coverage Status](https://sonarcloud.io/api/project_badges/measure?project=org.gridsuite%3Acgmes-gl-server&metric=coverage)](https://sonarcloud.io/component_measures?id=org.gridsuite%3Acgmes-gl-server&metric=coverage)
[![MPL-2.0 License](https://img.shields.io/badge/license-MPL_2.0-blue.svg)](https://www.mozilla.org/en-US/MPL/2.0/)

## Description

The **cgmes-gl-server** is a microservice of the [GridSuite](https://github.com/gridsuite) platform dedicated to **extracting geographical position data from CGMES cases and pushing them to the geo-data-server**.

It provides the following capabilities:

- **Load a CGMES case** from the case-server and parse its GL (Geographical Location) profile using the PowSyBl CGMES importer with the `cgmesGLImport` post-processor.
- **Extract substation and line positions** from the network IIDM extensions (`SubstationPosition`, `LinePosition`), with optional filtering by country.
- **Push the extracted coordinates** to the geo-data-server supervision endpoints for persistence.

---

## Technical Stack

- Spring Boot (Web, Actuator)
- PowSyBl CGMES conversion (`powsybl-cgmes-conversion`, `powsybl-cgmes-gl`)
- PowSyBl triple store via RDF4J (`powsybl-triple-store-impl-rdf4j`)
- API documentation: OpenAPI / Swagger (`springdoc`)
- Micrometer / Prometheus

---

## Development Scripts

Build Docker image

```shell
mvn install -DskipTests -Dpowsybl.docker.install
```

---

## Interactions with Other Microservices

```
┌─────────────────────┐
│  cgmes-gl-server    │──► case-server      (fetch the CGMES case datasource by UUID)
│                     │──► geo-data-server  (push extracted substation and line coordinates)
└─────────────────────┘
```

---

## Useful Links

- [PowSyBl CGMES documentation](https://powsybl.readthedocs.io/projects/powsybl-core/en/latest/grid_exchange_formats/cgmes/index.html)
