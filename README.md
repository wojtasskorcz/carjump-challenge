# carjump-challenge

### Setup and Running

The application doesn't require any additional setup. One thing that can be adjusted before running are the settings in `application.conf` but they already contain sensible defaults.

The server is started from within SBT with the `run` command. It is possible to shut down the server without exiting SBT by pressing <kbd>Enter</kbd>.

### Testing

Tests are started from within SBT with the `test` command.

### Caveats

* As it was not specified explicitly, I assumed that the data fetched from the endpoint can possibly contain strings of length greater than one. Therefore the fetched data is parsed to `Seq[String]` and not `Seq[Char]`. If necessary, switching to `Seq[Char]` should be straightforward.
* Currently the data is quite small. Should it get bigger, the calls to `Compressor` from within `CacheActor` should be wrapped in a `Future` or multiple instances of `CacheActor` should get created behind a router. Otherwise the actor may become unresponsive.
* As it was not allowed by the challenge, there's no logging library in the project. Such library (e.g. slf4j backed by Logback) should definitely be used in production.
