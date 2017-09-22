(ns mojo-ui.list
  (:require [mojo-ui.fx :refer [ripple]]
            [mojo-ui.icon :refer [font-icon]])
  (:refer-clojure :exclude [list]))

(defmulti list map?)
(defmethod list true
  [{:keys [class id]} & children]
  [:div.ui-list.ui-widget {:id id :class class}
   (map-indexed (fn
                  [idx item]
                  ;; find a key for each item
                  (let [key (or (:key (second item)) (str idx))]
                    (with-meta item {:key key})))
                children)])

(defmethod list false
  [& children]
  (apply list {} children))

(defn list-item
  "a single list view item"
  ([{:keys [icon right-icon on-click class]} text]
   [:div.ui-list-item {:class class
                       :on-click on-click}
    (if icon [font-icon icon] nil)
    text
    [ripple]])

  ([text]
   (list-item {:icon nil :right-icon nil}
              text)))
