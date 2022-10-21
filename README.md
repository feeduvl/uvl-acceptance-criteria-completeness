# uvl-acceptance-criteria-completeness

[![License: GPL 3.0](https://img.shields.io/badge/License-GPL%203.0-blue.svg)](https://www.gnu.org/licenses/gpl-3.0.de.html)
## About

This is a microservice that can run in a docker container and checks acceptance criteria for completeness regarding their respective user stories.

## REST API

See [swagger.yaml](../master/swagger.yaml) for details. [This tool](https://editor.swagger.io/?url=https://raw.githubusercontent.com/feeduvl/uvl-acceptance-criteria/main/swagger.yaml) can be used to render the swagger file.

## Method Parameter

`debug` – Whether to include debug information.
`filterUSTopics` - Whether to apply additional filters to the concepts found in user stories, i.e. removing longer concepts when all their components have also been found as concepts.

## License
Free use of this software is granted under the terms of the [GPL version 3](https://www.gnu.org/licenses/gpl-3.0.de.html) (GPL 3.0).
