# mojo-ui

A ui library written for clojurescript.

## Demo

[Demo Link](https://gliheng.github.com/mojo-ui)

## Overview

This is a pure ui library written using reagent and re-frame.

## Setup

To get an interactive development environment run:

    rlwrap lein figwheel

and open your browser at [localhost:3449](http://localhost:3449/).

To clean all compiled files:

    lein clean

To create a production build run:

    lein do clean, cljsbuild once min

And open your browser in `resources/public/index.html`. You will not
get live reloading, nor a REPL.

## License

Copyright © 2017

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
