(ns mojo-ui.addons
  (:require [reagent.core :refer [adapt-react-class]]))

(def css-transition-group (adapt-react-class js/React.addons.CSSTransitionGroup))
