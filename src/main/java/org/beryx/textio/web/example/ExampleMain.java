/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.beryx.textio.web.example;

import org.beryx.textio.TextIO;
import org.beryx.textio.web.RatpackTextIoApp;
import org.beryx.textio.web.TextIoApp;
import org.beryx.textio.web.WebTextTerminal;

import java.util.function.BiConsumer;

public class ExampleMain {
    public static void main(String[] args) {
        BiConsumer<TextIO, String> app = new ExampleApp();

        WebTextTerminal webTextTerm = new WebTextTerminal();
        TextIoApp textIoApp = new RatpackTextIoApp(app, webTextTerm);
        WebTextIoExecutor webTextIoExecutor = new WebTextIoExecutor();
        webTextIoExecutor.execute(textIoApp);
    }
}
