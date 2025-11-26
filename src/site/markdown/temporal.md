# Usage of the temporal package

The package `eu.fastipletonis.astro.temporal` contains some utilities required
when manipulating time from an astronomical perspective.

## Julian day

The library allows to retrieve the Julian day from a supported temporal
accessor, such as `ZonedDateTime` or `LocalDateTime` either by using the
methods of the `JulianDay` class directly:

```java
  ZonedDateTime sputnikLaunch = ZonedDateTime.parse("1957-10-04T19:26:24Z");

  // Alternatively, we can use JulianDay.getBigDecimalFrom for
  // a higher precision.
  double jd = JulianDay.getDoubleFrom(sputnikLaunch); // 2436116.31
```

or using the queries defined in the `Queries`class:

```java
  ZonedDateTime sputnikLaunch = ZonedDateTime.parse("1957-10-04T19:26:24Z");

  // Alternatively, we can use Queries.HP_JULIAN_DAY for a higher precision.
  // In this case a BigDecimal will be returned.
  // Please note that we use "Double", not "double", as the query will first
  // check if the temporal accessor can be used to retrieve a Julian day and
  // return null if it is not possible.
  Double jd = sputnikLaunch.query(Queries.JULIAN_DAY); // 2436116.31
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

Please note that a decimal time of `0` indicates midnight, not noon.

The methods to get the decimal time from a temporal accessor can also be
accessed through queries:

```java
  LocalTime sputnikLaunch = LocalTime.parse("19:26:24");

  // Queries.HP_DECIMAL_TIME can be used for retrieving a BigDecimal.
  // Please note that we use "Double", not "double", as the query will first
  // check if the temporal accessor can be used to retrieve a decimal time and
  // return null if it is not possible.
  double t = sputnikLaunch.query(Queries.DECIMAL_TIME); // 0.81
```
