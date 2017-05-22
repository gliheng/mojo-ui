(ns mojo-ui.core
  (:require [reagent.core :refer [render atom]]
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
    [:div.ui-tabs.ui-widget
     [:ul
      (for [i (range c)]
        [:li {:key i
              :class (if (= cur i) "ui-active" "")
              :style {:width (str (if (= i (dec c))
                                    (- 100 (* w (dec c)))
                                    w) "%")}
              :on-click
              #(dispatch [:change-tab-index key i])}
         [ripple (nth title-list i)]])]
     [:div.ui-line {:style {:left (str (* cur w) "%")
                         :width (str w "%")}}]
     [:div ^{:key cur} (nth children cur)]]))

(defn tab
  ""
  [& rest]
  (into [:div] rest))

(defn button
  [& rest]
  [:div.ui-button.ui-widget (into [ripple] rest)])

(defn button-demo
  ""
  []
  [button "Button with ripple effect."])

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
    [tab-demo {:key "tab-demo"}]]
   [:div
    [:h1 "Button Demo"]
    [button-demo]]])

(defn ^:export run-demo
  []
  (render [demo]
          (js/document.getElementById "app")))

(defn on-js-reload []
  (run-demo))
