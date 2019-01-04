/*
 * Javalin - https://javalin.io
 * Copyright 2017 David Åse
 * Licensed under Apache 2.0: https://github.com/tipsy/javalin/blob/master/LICENSE
 */

package io.javalin.examples

import io.javalin.Javalin
import io.javalin.serversentevent.EventSource
import java.util.*
import java.util.concurrent.TimeUnit

fun main(args: Array<String>) {
    val eventSources = ArrayList<EventSource>()

    val app = Javalin.create().start(7000)
    app.get("/") { ctx ->
        ctx.html(
                ""
                        + "<script>" +
                        "var sse = new EventSource('http://localhost:7000/sse');" +
                        "sse.addEventListener('hi', data => console.log(data));"
                        + "</script>"
        )
    }

    app.sse("/sse") { sse ->
        sse.sendEvent("connect", "Connected!")
        eventSources.add(sse) // Save the sse to use outside of this context
        sse.onClose { eventSource -> eventSources.remove(eventSource) } // Remove the sse to not use it outside of this context
    }

    while (true) {
        for (sse in eventSources)
            sse.sendEvent("hi", "hello world")
        TimeUnit.SECONDS.sleep(1)
    }
}
