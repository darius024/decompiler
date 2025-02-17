//> using scala 3.6
//> using platform jvm

// dependencies
//> using dep com.github.j-mie6::parsley::5.0.0-M10
//> using dep com.github.j-mie6::parsley-cats::1.5.0
//> using dep com.lihaoyi::os-lib::0.11.4
//> using dep org.typelevel::cats-core:2.13.0
//> using test.dep org.scalatest::scalatest::3.2.19
//> using test.dep com.lihaoyi::upickle:4.1.0
//> using test.dep com.github.j-mie6::golden-scalatest::0.1.0-M1

// these are all sensible defaults to catch annoying issues
//> using options -deprecation -unchecked -feature
//> using options -Wimplausible-patterns -Wunused:all
//> using options -Yexplicit-nulls -Wsafe-init -Xkind-projector:underscores

// these will help ensure you have access to the latest parsley releases
// even before they land on maven proper, or snapshot versions, if necessary.
// just in case they cause problems, however, keep them turned off unless you
// specifically need them.
// using repositories sonatype-s01:releases
// using repositories sonatype-s01:snapshots

// these are flags used by Scala native: if you aren't using scala-native, then they do nothing
// lto-thin has decent linking times, and release-fast does not too much optimisation.
// using nativeLto thin
// using nativeGc commix
// using nativeMode release-fast
