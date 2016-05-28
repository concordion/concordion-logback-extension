# HTML Logs
Plain text logs supply a lot of useful information but it can take time to trawl though to find the information you want.

This custom layout for the [LOGBack Logger](http://logback.qos.ch) will combine your logs with screenshots, data and stack trace and present the information in an easy to digest fashion.

## [Configuration](-)

Configuring your application to use ConcordionHtmlLayout is a simple matter of updating the logback-includes.xml configuration file and replacing the [Layout section of the FILE-PER-TEST sifting appender](- "c:assertTrue=configuration()") with the following:

    <encoder class="ch.qos.logback.core.encoder.LayoutWrappingEncoder">
      <layout class="org.concordion.ext.loggingFormatter.HTMLLayout" />
    </encoder>

The content of the table columns are specified using a conversion pattern. For more information about this layout, please refer to the online manual at <http://logback.qos.ch/manual/layouts.html#ClassicHTMLLayout>.

    <layout class="org.concordion.ext.loggingFormatter.HTMLLayout" />
      <pattern>%relative%thread%mdc%level%logger%msg</pattern>
    </layout>


## [Exceptions](-)

Exceptions are formatted within a [collapsible section](- "c:assertTrue=throwException()") that presents the error message by default but will allow the user to drill down into the stack trace.  


## [Actions](-)

Log statements are grouped into steps...


## [Screenshots](-)

Screenshots can be included...


## [Text Based Data](-)

Text based data such as CSV, XML, JSON, HTML, etc can be included...

