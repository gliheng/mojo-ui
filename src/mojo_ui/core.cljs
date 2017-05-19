(ns mojo-ui.core
  (:require [reagent.core :refer [render]]
            [re-frame.core :refer [dispatch
                                   subscribe
                                   reg-event-db
                                   reg-sub]]
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
        w (js/Math.floor (/ 100 c))]
    [:div.ui-tabs
     [:ul
      (for [i (range c)]
        [:li {:key i
              :class (if (= cur i) "ui-active" "")
              :style {:width (str w "%")}
              :on-click
              #(dispatch [:change-tab-index key i])}
         (nth title-list i)])]
     [:div.ui-line {:style {:left (str (* cur w) "%")
                         :width (str w "%")}}]
     [:div ^{:key cur} (nth children cur)]]))

(defn tab
  ""
  [& children]
  (into [:div] children))

(defn tab-demo
  [{:keys [key]}]
  [tabs {:key key}
   [tab {:title "Tab 1"}
    [:div "tab1 content"]]
   [tab {:title "Tab 2"}
    [:div "tab2 content"]]
   [tab {:title "Tab 3"}
    [:div "tab3 content"]]])

(defn demo
  []
  [:div.demo
   [:div
    [:h1 "Tab Demo"]
    [tab-demo {:key "tab-demo"}]]])

(defn ^:export run-demo
  []
  (render [demo]
          (js/document.getElementById "app")))

(defn on-js-reload []
  (run-demo))
