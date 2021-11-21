# Multi-Player High-Low Card Game

## How to run

To start a local singleplayer game:

```
./gradlew :game:run
```

To start a server:

```
./gradlew :server:run --args="--port PORT"
```

To start a client:

```
./gradlew :client:run
```

You can provide additional arguments on the command-line with

```
--args="--username USERNAME --address ADDRESS --port PORT"
```

To run unit tests for individual components:

```
./gradlew :server:test
./gradlew :client:test
./gradlew :game:test
./gradlew :communication:test
```

The same works for the known `spotlessApply`, `checkstyleMain`, etc. if you only want to run it on a single component.

Note: For grading, only Checkstyle and SpotBugs on `:server:` and `:client:` are relevant.
