;;; If this namespace requires macros, remember that ClojureScript's
;;; macros are written in Clojure and have to be referenced via the
;;; :require-macros directive where the :as keyword is required, while in Clojure is optional. Even
;;; if you can add files containing macros and compile-time only
;;; functions in the :source-paths setting of the :builds, it is
;;; strongly suggested to add them to the leiningen :source-paths.
(ns reagi-game.core
  (:require [monet.canvas :as canvas]
            [reagi.core :as r]
            [clojure.set :as set]
            [reagi-game.entities :as entities
             :refer [move-forward! move-backward! rotate-right! rotate-left!
                     fire!]]))

(def canvas-dom (.getElementById js/document "canvas"))

(def monet-canvas (canvas/init canvas-dom "2d"))

(def ship
  (entities/shape-data (/ (.-width (:canvas monet-canvas)) 2)
                       (/ (.-height (:canvas monet-canvas)) 2)
                       0))

(def ship-entity (entities/ship-entity ship))

(canvas/add-entity monet-canvas :ship-entity ship-entity)
(canvas/draw-loop monet-canvas)


(defn foo [greeting]
  (if greeting
    (str greeting "ClojureScript!")
    (str "Hello, ClojureScript!")))

(.write js/document (foo "Welcome to "))



