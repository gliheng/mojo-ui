(ns mojo-ui.addons
  (:require [reagent.core :refer [adapt-react-class]]))

;; example:
;; [css-transition-group {:transition-name "ui-accordion"
;;                        :component "div"
;;                        :className "ui-transition-group"
;;                        :transition-enter-timeout 1000
;;                        :transition-leave-timeout 1000}
(def css-transition-group (adapt-react-class js/React.addons.CSSTransitionGroup))

(def transition-group
  (adapt-react-class js/React.addons.TransitionGroup))

