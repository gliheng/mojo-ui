(ns mojo-ui.core
  (:require [re-frame.core :refer [dispatch
                                   subscribe
                                   reg-event-db
                                   reg-sub]]
            [mojo-ui.fx :refer [ripple]]
            [devtools.core :as devtools])
  (:require-macros [mojo-ui.core :refer [require-css]]))

(devtools/install!)
(enable-console-print!)

(require-css "style")

(reg-event-db
 :change-tab-index
 (fn [db [_ key id]]
   (assoc-in db [:ui (or key :default) :tab-index] id)))

(reg-sub
 :tab-index
 (fn [db [_ key]]
   (get-in db [:ui (or key :default) :tab-index] 0)))

(defn get-key
  ""
  [comp]
  (if-let [opts (or (meta comp) (second comp))]
    (:key opts)))

(defn tabs
  "If tab-index is string, convert it to number first using tab keys."
  [{:keys [key]} & children]
  (let [cur @(subscribe [:tab-index key])
        get-title (comp :title second)
        title-list (map get-title children)
        key-map (into {} (map #(if-let [key (get-key %1)]
                                 [key %2])
                              children
                              (iterate inc 0)))
        cur (if (string? cur)
              (key-map cur)
              cur)
        c (count children)
        w (js/Math.floor (/ 100 c))
        get-w #(if (= % (dec c))
                 (- 100 (* w (dec c)))
                 w)]
    [:div.ui-tabs.ui-widget
     [:ul
      (for [i (range c)]
        [:li {:key i
              :class (if (= cur i) "ui-active" "")
              :style {:width (str (get-w i) "%")}
              :on-click #(dispatch [:change-tab-index key i])}
         [ripple (nth title-list i)]])]
     [:div.ui-line {:style {:left (str (* cur w) "%")
                         :width (str (get-w cur) "%")}}]
     [:div ^{:key cur} (nth children cur)]]))

(defn tab
  ""
  [& rest]
  (into [:div] rest))

(defn button
  [& rest]
  [:div.ui-button.ui-widget (into [ripple] rest)])
