(ns mojo-ui.core
  (:require [clojure.java.io :refer [resource reader]]
            [clojure.string :refer [join]])
  (:import [java.security MessageDigest]))

(defn compile-sass
  "compile sass file and get output as string"
  [url]
  (let [cmd (str "sassc " url)
        stream (.. (Runtime/getRuntime) (exec cmd) getInputStream)]
    (with-open [rdr (reader stream)]
      (join "\n" (line-seq rdr)))))

(def config {:style {:root "css/"
                     :exts [".scss" ".sass" ".css"]}})


(defn resolve-file
  [fn type]
  (let [conf (config type)
        exts (conf :exts)
        root (conf :root)
        file (first (map #(resource (str root fn %)) exts))]
    (if file (.getFile file))))


(defn int->hex
  [d]
  (subs (Integer/toString (+ d 0x100), 16) 1))

(defn bytes->string
  ""
  [bytes]
  (join "" (map int->hex bytes)))

(defn md5
  "get md5 for a string and return as string"
  [s]
  (let [d (MessageDigest/getInstance "md5")]
    (bytes->string (.digest d (.getBytes s)))))

(defmacro require-css [css-file]
  (let [url (resolve-file css-file :style)
        id (md5 url)
        text (compile-sass url)]
    `(do
       (if-let [elem# (js/document.getElementById ~id)]
         (.remove elem#))
       (let [elem# (js/document.createElement "style")]
         (set! (.-id elem#) ~id)
         (set! (.-innerHTML elem#) ~text)
         (js/document.head.appendChild elem#)))))
