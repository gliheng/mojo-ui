(ns mojo-ui.core)

(defmacro require-css [css-file]
  (let [s (slurp (clojure.java.io/resource (str "public/css/" css-file)))]
    `(let [elem# (js/document.createElement "style")]
       (set! (.-innerHTML elem#) ~s)
       (js/document.head.appendChild elem#))))
