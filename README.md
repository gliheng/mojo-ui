# mojo-ui

A ui library written for clojurescript.

## Overview

This ui library uses reagent and re-frame. So it's tied to a react binding and a MV* library.
The benifit is it saves states in a single ratom.

## Setup

To get an interactive development environment run:

    rlwrap lein figwheel

and open your browser at [localhost:3449](http://localhost:3449/).
This will auto compile and send all changes to the browser without the
need to reload. After the compilation process is complete, you will
get a Browser Connected REPL. An easy way to try it is:

    (js/alert "Am I connected?")

and you should see an alert in the browser window.

To clean all compiled files:

    lein clean

To create a production build run:

    lein do clean, cljsbuild once min

And open your browser in `resources/public/index.html`. You will not
get live reloading, nor a REPL. 

## License

Copyright © 2014

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
