(ns mojo-ui.core
  (:require [re-frame.core :refer [dispatch
                                   subscribe]]
            [reagent.core :refer [atom create-class props] :as reagent]
            [mojo-ui.fx :refer [ripple bulge]]
            [mojo-ui.list :refer [list list-item]]
            [mojo-ui.events]
            [mojo-ui.subs]
            [mojo-ui.fadein-view :refer [fadein-view]]
            [devtools.core :as devtools]
            [goog.events :as events]
            [mojo-ui.addons :refer [transition-group]])
  (:require-macros [mojo-ui.core :refer [require-css]]))


(devtools/install!)
(enable-console-print!)

(require-css "style" mojo-ui)


(defn get-key
  ""
  [comp]
  (if-let [opts (or (meta comp) (second comp))]
    (:key opts)))

(def get-title (comp :title second))

(defn tabs
  "A tab component,
  If tab-index is string, convert it to number first using tab keys."
  [{:keys [key id class]} & children]
  (let [cur @(subscribe [:mojo-ui.subs/tab-index key])
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
     {:id id :class class}
     [:ul
      (for [i (range c)]
        [:li {:key i
              :class (if (= cur i) "ui-active" "")
              :style {:width (str (get-w i) "%")}
              :on-click #(dispatch [:mojo-ui.events/change-tab-index key i])}
         (nth title-list i) [ripple]])]
     [:div.ui-line {:style {:left (str (* cur w) "%")
                         :width (str (get-w cur) "%")}}]
     [:div ^{:key cur} (nth children cur)]]))

(defn view
  "view provide a title attribute on their 1st argument"
  [& rest]
  (into [:div.ui-view] (drop 1 rest)))

(defmulti button map?)

(defmethod button true []
  (let [btn (atom nil)
        focus (atom false)
        on-focus (fn [] (reset! focus true))
        on-blur (fn [] (reset! focus false))]
    (create-class {:component-did-mount (fn [this] (if (:auto-focus (props this))
                                                     (.focus @btn)))
                   :reagent-render (fn [{:keys [take-focus on-click auto-focus]} & rest]
                                     [:button.ui-button.ui-widget {:tab-index 0
                                                                   :ref (fn [_btn] (reset! btn _btn))
                                                                   :on-click on-click
                                                                   :on-focus on-focus
                                                                   :on-blur on-blur}
                                      [:div.ui-bg]
                                      [ripple]
                                      (if (and take-focus @focus) [bulge] nil)
                                      rest])})))

(defmethod button false
  [& rest]
  [:button.ui-button.ui-widget
   [:div.ui-bg]
   [ripple]
   rest])


(defn accordion
  "an accordion component"
  [{:keys [key id class]} & children]
  (let [cur @(subscribe [:mojo-ui.subs/accordion-index key])
        title-list (map get-title children)
        key-map (into {} (map #(if-let [key (get-key %1)]
                                 [key %2])
                              children
                              (iterate inc 0)))
        cur (into #{}
                  (map (fn [item]
                         (if (string? item)
                           (key-map item)
                           item)) cur))]
    [:div.ui-accordion.ui-widget
     {:id id :class class}
     (map (fn [child i]
            [:div {:key i}
             [:div.ui-title {:on-click
                             #(dispatch [:mojo-ui.events/change-accordion-index key i])}
              (get-title child)]
             [transition-group {:component "div"}
              (if (cur i)
                [fadein-view child] nil)]])
          children
          (iterate inc 0))]))
