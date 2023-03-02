# Jlite

![icon](media/icon.png)

![build](https://github.com/fe1fan/Jlite/actions/workflows/gradle.yml/badge.svg)
![version](https://img.shields.io/github/v/release/fe1fan/Jlite)
![license](https://img.shields.io/github/license/fe1fan/Jlite)
![size](https://img.shields.io/github/languages/code-size/fe1fan/Jlite)

[//]: # (![stars]&#40;https://img.shields.io/github/stars/fe1fan/jlite&#41;)
[//]: # (![forks]&#40;https://img.shields.io/github/forks/fe1fan/jlite&#41;)
[//]: # (![issues]&#40;https://img.shields.io/github/issues/fe1fan/jlite&#41;)

### fast and simple web framework for java :)

### latest version

### WARING  ⚠️ ⚠️ ⚠️ 👷

Not yet released

This project is still in development, and the API may change at any time.

### Usage
```gradle
implementation 'io.xka.jlite:jlite-[model]:[latest version]'
```
### Example
```java
package io.xka.jlite.web.example;

import io.xka.jlite.web.Jlite;
import io.xka.jlite.web.JliteApp;
import io.xka.jlite.web.serializer.JsonAdopter;

public class Application {
    public static void main(String[] args) {
        //create from options
        JliteApp app = Jlite.options()
                .host("localhost")
                .port(8080)
                .maxThreads(100)
                .minThreads(10)
                .serializer(JsonAdopter.Engine.JACKSON)
                .quick();
        app.run();
        app.use("/json", hdl -> {
            String authorization = hdl.getHeader("authorization");
            if (authorization == null || authorization.isBlank()) {
                //error
                hdl.result("Unauthorized");
                return false;
            }
            return true;
        });
        app.get("/xml/hello", ctl -> ctl.json(new HashMap<>() {
            {
                put("hello", "world");
            }
        }));
        app.get("/json/hello", ctl -> ctl.json(new HashMap<>() {
            {
                put("hello", "world");
            }
        }));
        app.stop();
    }
}

```
