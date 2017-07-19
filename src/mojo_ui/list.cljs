(ns mojo-ui.list
  (:require [mojo-ui.fx :refer [ripple]]
            [mojo-ui.icon :refer [font-icon]])
  (:refer-clojure :exclude [list]))


(defn list
  "a generic list view"
  [& children]
  [:div.ui-list.ui-widget
   (map-indexed (fn
                  [idx item]
                  ;; find a key for each item
                  (let [key (or (:key (second item)) (str idx))]
                    (with-meta item {:key key})))
                children)])

(defn list-item
  "a single list view item"
  ([{:keys [icon right-icon]} text]
   [:div.ui-list-item
    (if icon [font-icon icon] nil)
    text
    [ripple]])

  ([text]
   (list-item {:icon nil :right-icon nil}
              text)))
