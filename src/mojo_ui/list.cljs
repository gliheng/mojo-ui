(ns mojo-ui.list
  (:require [mojo-ui.fx :refer [ripple]])
  (:refer-clojure :exclude [list]))

(defn list
  "a generic list view"
  [& children]
  [:div.ui-list.ui-widget children])

(defn list-item
  "a generic list view"
  ([{:keys [icon right-icon]} text]
   [:div.ui-list-item text [ripple]])

  ([text]
   (list-item {:icon nil :right-icon nil}
              text)))
