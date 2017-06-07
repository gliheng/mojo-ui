(ns mojo-ui.core
  (:require [re-frame.core :refer [dispatch
                                   subscribe
                                   reg-event-db
                                   reg-sub]]
            [reagent.core :refer [atom]]
            [mojo-ui.fx :refer [ripple bulge]]
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

(def get-title (comp :title second))

(defn tabs
  "If tab-index is string, convert it to number first using tab keys."
  [{:key [key]} & children]
  (let [cur @(subscribe [:tab-index key])
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
         (nth title-list i) [ripple]])]
     [:div.ui-line {:style {:left (str (* cur w) "%")
                         :width (str (get-w cur) "%")}}]
     [:div ^{:key cur} (nth children cur)]]))

(defn item
  ""
  [& rest]
  (into [:div] (drop 1 rest)))

(defmulti button map?)

(defmethod button true []
  (let [focus (atom false)
        on-focus (fn [] (reset! focus true))
        on-blur (fn [] (reset! focus false))]
    (fn [{take-focus :take-focus} & rest]
      [:div.ui-button.ui-widget {:tab-index 0
                                 :on-focus on-focus
                                 :on-blur on-blur}
       [:div.ui-bg]
       [ripple]
       (if (and take-focus @focus) [bulge] nil)
       rest])))

(defmethod button false
  [& rest]
  [:div.ui-button.ui-widget
   [:div.ui-bg]
   [ripple]
   rest])

(reg-event-db
 :change-accordion-index
 (fn [db [_ key id]]
   (assoc-in db [:ui (or key :default) :accordion-index] id)))

(reg-sub
 :accordion-index
 (fn [db [_ key]]
   (get-in db [:ui (or key :default) :accordion-index] 0)))

(defn accordion
  ""
  [{:key key} & children]
  (let [cur @(subscribe [:accordion-index key])
        title-list (map get-title children)
        key-map (into {} (map #(if-let [key (get-key %1)]
                                 [key %2])
                              children
                              (iterate inc 0)))
        cur (if (string? cur)
              (key-map cur)
              cur)]
    [:div.ui-accordion.ui-widget
     (map (fn [child i]
            [:div {:key i}
             [:div.ui-title {:on-click #(dispatch [:change-accordion-index key i])} (get-title child)]
             (if (= i cur) [:div child] nil)])
          children
          (iterate inc 0))]))
