# Usage of the temporal package

The package `eu.fastipletonis.astro.temporal` contains some utilities required
when manipulating time from an astronomical perspective.

## Julian day

The library allows to retrieve the Julian day from a supported temporal
accessor, such as `ZonedDateTime` or `LocalDateTime` either by using the
methods of the `JulianDayHelper` class directly:

```java
  ZonedDateTime sputnikLaunch = ZonedDateTime.parse("1957-10-04T19:26:24Z");

  // Alternatively, we can use JulianDayHelper.getBigDecimalFrom for
  // a higher precision.
  double jd = JulianDayHelper.getDoubleFrom(sputnikLaunch); // 2436116.31
```

or using the queries defined in the `Queries`class:

```java
  ZonedDateTime sputnikLaunch = ZonedDateTime.parse("1957-10-04T19:26:24Z");

  // Alternatively, we can use Queries.HP_JULIAN_DAY for a higher precision.
  double jd = sputnikLaunch.query(Queries.JULIAN_DAY); // 2436116.31
```

## Decimal Time

In Meeus' book date-times are often specified with a decimal time. For example
the Sputnik launch date and time is given as _1957 October 4.81_. Amongst
methods that can convert to/from temporal accessors, visible in the API
reference, there is also a parsing method for handling such dates and
convert them in the JDK `LocalDateTime` object:

```java
  String sputnikLaunch = "1957-10-04.81";

  // Check the API reference for details on the accepted format.
  LocalDateTime dt = DecimalTime.parseDateTime(sputnikLaunch); // 1957-10-04T19:26:24
```
