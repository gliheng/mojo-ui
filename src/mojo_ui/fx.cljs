(ns mojo-ui.fx
  (:require [reagent.core :refer [atom]]))

(defn ripple
  ""
  [& children]
  (let [ripples (atom (list))
        remove-ripple (fn []
                        (swap! ripples rest))
        add-ripple (fn [evt]
                     (let [target (.-currentTarget evt)
                           rect (.getBoundingClientRect target)
                           x (- (.-clientX evt) (.-left rect))
                           y (- (.-clientY evt) (.-top rect))]
                       (swap! ripples concat (list {:timestamp (. (new js/Date) getTime)
                                                    :pos {:left x :top y}})))
                     (js/setTimeout remove-ripple 1000))]
    (fn []
      [:div.ui-ripple {:on-click add-ripple}
       (map #(vector :div.ui-ripple-fx {:key (:timestamp %)
                                        :style (:pos %)}) @ripples)
       children])))


(defn bulge
  ""
  []
  [:div.ui-bulge])
