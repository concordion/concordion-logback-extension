# HTML Logs
Plain text logs supply a lot of useful information but it can take time to trawl though to find the information you want.

This custom layout for the [LOGBack Logger](http://logback.qos.ch) will combine your logs with screenshots, data and stack trace and present the information in an easy to digest fashion.

## [Configuration](-)

Configuring your application to use ConcordionHtmlLayout is a simple matter of updating the logback-includes.xml configuration file and [replacing FILE-PER-TEST Layout section](- "c:assertTrue=configuration()") with the following:

    <encoder class="ch.qos.logback.core.encoder.LayoutWrappingEncoder">
      <layout class="ch.qos.logback.classic.html.HTMLLayout">
        <pattern>%relative%thread%mdc%level%logger%msg</pattern>
      </layout>
    </encoder>

