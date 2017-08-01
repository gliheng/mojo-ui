(ns mojo-ui.jar-exploder
  (:require [clojure.java.io :refer [copy]])
  (:import [java.io File]
           [java.util.zip ZipFile]
           [java.net URI]))

(defn save-entry
  "write entry to a new directory"
  [zip entry root]
  (let [name (.getName entry)
        tar (File. root name)]
    (if-not (.isDirectory entry)
      (let [parent (.getParentFile tar)]
        (.mkdirs parent)
        (.createNewFile tar)
        (copy (.getInputStream zip entry) tar)))))

(defn explode-jar
  "explode a jar to a temp directory return it's path.
  Delete tmp directory when jvm exits"
  [jar root]
  ;; (println "exploding jar" jar "to" root)
  (let [root-file (File. root)
        file (File. jar)
        zip (ZipFile. file)
        entries (loop [entries (.entries zip)
                       files []]
                  (if (.hasMoreElements entries)
                    (recur entries (conj files (.nextElement entries)))
                    files))]
    (.mkdir root-file)
    (doseq [entry entries]
      (save-entry zip entry root))))

;; exploded jars are put here temporarily
(def temp-root (File. (System/getProperty "java.io.tmpdir")
                        (str (java.util.UUID/randomUUID))))
(.mkdir temp-root)
(.deleteOnExit temp-root)

(def records (atom #{}))
(defn explode-jar-to-temp
  "if the jar has been exploded, it's a no-op"
  [jar]
  (let [jarname (.getName (File. jar))
        temp-root-path (.getCanonicalPath temp-root)
        temp-dir (.getCanonicalPath (File. temp-root-path jarname))]
    (when-not (@records jar)
      (swap! records conj jar)
      (explode-jar jar temp-dir))
    temp-dir))

