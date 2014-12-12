# Welcome to **_Proj4J_**

**_Proj4J_** is a Java library to transform point coordinates from one geographic coordinate system to another, including datum transformations.

The core of this library is a port of the [PROJ.4](http://trac.osgeo.org/proj/) C library.
The projection algorithms, concepts and coordinate system definitions are all taken directly from PROJ.4.
These are wrapped in a Java class model which follows standard Java conventions and OO best practices.

Proj4J is a part of the [MetaCRS](http://trac.osgeo.org/metacrs/wiki/WikiStart) group of projects, hosted by [OSGeo](http://osgeo.org).

## Documentation

*   The [PROJ.4 documentation](http://trac.osgeo.org/proj/#Documentation) is the best reference for details of
the coordinate system specification language, projection algorithms and supported parameters.

*   the [Version History](doc/Proj4J Version History.html) lists the evolution of features of the library

*   the [Javadoc](doc/javadoc/index.html) describes the API

## Mailing List

A mailing list is available for users and developers of Proj4J.

*   Subscription, unsubscription and a web archive are at [http://lists.osgeo.org/mailman/listinfo/proj4j](http://lists.osgeo.org/mailman/listinfo/proj4j).

*   Direct link to the  [mailing list archive](http://lists.osgeo.org/pipermail/proj4j/).

## Development

*   SVN repository: [http://svn.osgeo.org/metacrs/proj4j](http://svn.osgeo.org/metacrs/proj4j).

*   Browse source [here](http://trac.osgeo.org/proj4j/browser).

## Bug Tracking

*   [Submit a new bug report](/proj4j/proj4j/newticket) (you need to [login](http://trac.osgeo.org/proj4j/login) with an [OSGeo Userid](http://www.osgeo.org/osgeo_userid))

*   [List Open Bugs](/proj4j/query?status=%21closed&amp;order=id&amp;desc=1&amp;type=defect)

*   [List Open Enhancement Requests](/proj4j/query?status=%21closed&amp;order=id&amp;desc=1&amp;type=enhancement)

## License

Proj4J is licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

## Related Resources

*   The PROJ.4 site has a [comprehensive list of resources](http://trac.osgeo.org/proj/#RelatedResources) dealing with Coordinate Systems in general and the PROJ.4 library in particular.

## History

The Proj4J codebase is based on a partial port of PROJ.4
carried out by
[JHLabs](http://www.jhlabs.com/java/maps/proj/index.html)
circa 2006.  The JHLabs port provided a large number of the
PROJ.4 projections, but did not support some PROJ.4 features
such as datum transformation.

