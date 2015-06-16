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

(def UP 38)
(def RIGHT 39)
(def DOWN 40)
(def LEFT 37)
(def FIRE 32);; space
(def PAUSE 80);; lower-case P

(def canvas-dom (.getElementById js/document "canvas"))

(def monet-canvas (canvas/init canvas-dom "2d"))

(def ship
  (entities/shape-data (/ (.-width (:canvas monet-canvas)) 2)
                       (/ (.-height (:canvas monet-canvas)) 2)
                       0))

(def ship-entity (entities/ship-entity ship))

(canvas/add-entity monet-canvas :ship-entity ship-entity)
(canvas/draw-loop monet-canvas)
;;-------------------------
(defn keydown-stream []
  (let [out (r/events)]
    (set! (.-onkeydown js/document)
          #(r/deliver out [::down (.-keyCode %)]))
    out))

(defn keydup-stream []
  (let [out (r/events)]
    (set! (.-onkeyup js/document)
          #(r/deliver out [::up (.-keyCode %)]))
    out))

(def active-keys-steam
  (->> (r/merge (keydown-stream) (keyup-stream))
       (r/reduce (fn [acc [event-type key-code]]
                   (condp = event-type
                     ::down (conj acc key-code)
                     ::up (disj acc key-code)
                     acc))
                 #{})
       (r/sample 25)))

(defn filter-map [pred f & args]
  (->> active-keys-steam
       (r/filter (partial some pred))
       (r/map (fn [_] (apply f args)))))

(filter-map #{FIRE} fire! monet-canvas ship)
(filter-map #{UP} move-forward! ship)
(filter-map #{DOWN} move-backward! ship)
(filter-map #{RIGHT} rotate-right! ship)
(filter-map #{LEFT} rotate-left! ship)

(defn pause! [_]
  (if @(:updating? monet-canvas)
    (canvas/stop-updating monet-canvas)
    (canvas/start-updating monet-canvas)))

(->> active-keys-steam
     (r/filter (partial some #{PAUSE}))
     (r/throttle 100)
     (r/map pause!))


;;-------------------------
(defn foo [greeting]
  (if greeting
    (str greeting "ClojureScript!")
    (str "Hello, ClojureScript!")))

(.write js/document (foo "Welcome to "))



