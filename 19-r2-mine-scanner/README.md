# Mine scanner

## How to launch

To start project execute in the project folder:

```shell
docker-compose up
```
It is based on Docker multistage build. On the first stage it **runs tests** and build project - creates executable jar.
On the second step docker image is created from the artifacts produced on the first stage.

Tests can be run separately using gradle (JDK 17 is required):

```shell
./gradlew test
```
Or from IDE.

To inspect test cases see MineScannerControllerIntTest.java

## Used technologies

Service is written on Java 17, Spring Boot 2.7.5. 
Library used: Streamex, lombok
For testing: Assert4j, RestAssured, jsonassert

## Performance

Application uses fail fast approach - all validations happens in the early stages, so no unnessesary steps are executed for invalid inputs.
Intensive computations are made in parallel to utilize system resources more efficiently and speed up processing. 
Combining all that high throughput and high latency is achieved.

## Troubleshooting

docker-compose up fails - Maybe tests failed (Service is started on random port for tests, and there is a small possibility that port is already used)
In this case try again. If failed again, modify line 4 in Dockerfile to:

```shell
RUN gradle build -x test --no-daemon
```

Runtime errors:

* Image has invalid color scheme - Make sure that image contains only greyscale colors. (E.g. Ctrl+Shift+G will make image greyscale in Paint.NET)
* Invalid Grid detected - Make sure outer frame is present, all cells are of equal size, all of them are rectangles.
* Image string doesn't have uri prefix - Make sure that image base64 is prefixed with "data:image/png;base64,"

Example of a valid request:

```json
{
    "min_level": 20,
    "image": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACQAAAAdCAMAAAD1qz7PAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAGUExURf///wAAAFXC034AAAAJcEhZcwAADsMAAA7DAcdvqGQAAAAvSURBVDhPYyAOMIIBXmoIKyIKQFXjo4awIqIAVDU+aggrIgpAVeOjhrAigoCBAQDpZALRJiPAOwAAAABJRU5ErkJggg=="
}
```