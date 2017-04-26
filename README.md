## Functional interfaces and higher-order methods for Iterator and Iterable

1. Just one class, so it is easy to integrate in small projects instead of using it as an external library to depend on
2. Functional interfaces take the same names and parameters as those in java.util.function package from Java 8, so an application using jfnlite can easily be refactored to use the functional support in Java 8
3. Higher-order methods are provided for plain Iterator and Iterable objects, so integration with non-functional code is seamless (no conversions from/to Spliterator and Stream are needed)

## When could it be useful to use jfnlite?

When writing a small application that needs to run on legacy equipment (with no Scala or Java 8 runtime) and needs to be easily distributed as a single "one and small" file executable.
I wrote jfnlite not as a silver bullet, but as a niche solution. Basically, I wanted to use functional programming when writing an app with the following requirements:
   * Needs to run not only in modern PCs, but also in old legacy servers running quite old Java versions (such as Java 6, no Scala or Java 8 available).
   * Needs to be distributed as a "one and small" file executable, so neither bundling or depending on Scala, Guava, functionaljava.org or streamsupport libraries
