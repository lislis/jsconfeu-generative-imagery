(ns app.main
  (:require [app.vector :as vector]
            [app.walker :as walker]
            [app.mover :as mover]))

(def width 1914) ; 1914
(def height 1074) ; 1074

(def pink "#E10079")
(def blue "#5f57c4")
(def creme "#F2EBD9")
(def ice "#DDE4F2")
(def steel "#303F62")
(def peach "#FFC4BC")

(defonce state
  (atom {:pink-list []
         :blue-list []
         :ice-list []
         :creme-list []
         :steel-list []
         :peach-list []}))

(defn setup []
  (js/createCanvas width height)
  (swap! state assoc :pink-list (walker/seed 14 (- width 50) (- height 50)))
  (swap! state assoc :blue-list (walker/seed 14 (- width 50) (- height 50)))
  (swap! state assoc :ice-list (walker/seed 9 (- width 50) (- height 50)))
  (swap! state assoc :creme-list (walker/seed 9 (- width 50) (- height 50)))
  (swap! state assoc :steel-list (walker/seed 15 (- width 50) (- height 50)))
  )

(defn draw []
  (let [pink-list (:pink-list @state)
        blue-list (:blue-list @state)
        ice-list (:ice-list @state)
        creme-list (:creme-list @state)
        steel-list (:steel-list @state)]
    (swap! state assoc :pink-list (mapv walker/walker-step pink-list))
    (swap! state assoc :blue-list (mapv walker/walker-step blue-list))
    (swap! state assoc :ice-list (mapv walker/walker-gaussian ice-list))
    (swap! state assoc :creme-list (mapv walker/walker-gaussian creme-list))
    (swap! state assoc :steel-list (mapv walker/walker-montecarlo steel-list))
    (dorun
     (for [m pink-list]
       (walker/draw-ellip m pink)))
    (dorun
     (for [m blue-list]
       (walker/draw-ellip m blue)))
    (dorun
     (for [i ice-list]
       (walker/draw-ellip i ice)))
    (dorun
     (for [i creme-list]
       (walker/draw-ellip i creme)))
    (dorun
     (for [i steel-list]
       (walker/draw-dot i steel)))))

(defn setup-mouse []
  (js/createCanvas width height)
  (swap! state assoc :pink-list (mover/seed 6 width height))
  (swap! state assoc :blue-list (mover/seed 6 width height))
  (swap! state assoc :ice-list (mover/seed 3 width height))
  (swap! state assoc :creme-list (mover/seed 3 width height))
  (swap! state assoc :steel-list (mover/seed 3 width height)))

(defn draw-mouse []
  (let [pink-list (:pink-list @state)
        blue-list (:blue-list @state)
        ice-list (:ice-list @state)
        creme-list (:creme-list @state)
        steel-list (:steel-list @state)]

    (swap! state assoc :pink-list (mapv mover/accelerate-to-mouse pink-list))
    (swap! state assoc :blue-list (mapv mover/accelerate-to-mouse blue-list))
    (swap! state assoc :ice-list (mapv mover/accelerate-to-mouse ice-list))
    (swap! state assoc :creme-list (mapv mover/accelerate-to-mouse creme-list))
    (swap! state assoc :steel-list (mapv mover/accelerate-to-mouse  steel-list))
    (dorun
     (for [i pink-list]
       (let [location (:location i)]
         (mover/draw-big location pink))))
    (dorun
     (for [i blue-list]
       (let [location (:location i)]
         (mover/draw-big location blue))))
    (dorun
     (for [i ice-list]
       (let [location (:location i)]
         (mover/draw-smol location ice))))
    (dorun
     (for [i creme-list]
       (let [location (:location i)]
         (mover/draw-smol location creme))))
    (dorun
     (for [i steel-list]
       (let [location (:location i)]
         (mover/draw-dot location steel))))))


(defonce env
  (atom {:wind (vector/create 0 0)
         :gravity (vector/create 0 0.2)}))

(defn setup-wind []
  (js/createCanvas width height)
  (swap! state assoc :pink-list (mover/seed-bounceball 40 width height))
  (swap! state assoc :blue-list (mover/seed-bounceball 40 width height))
  (swap! state assoc :creme-list (mover/seed-bounceball 15 width height))
  (swap! state assoc :ice-list (mover/seed-bounceball 15 width height))
  (swap! state assoc :peach-list (mover/seed-bounceball 3 width height))
  (swap! state assoc :steel-list (mover/seed-bounceball 10 width height)))

(defn draw-wind []
  (js/background 255 255 255 80)
  (let [pink-list (:pink-list @state)
        blue-list (:blue-list @state)
        creme-list (:creme-list @state)
        ice-list (:ice-list @state)
        peach-list (:peach-list @state)
        steel-list (:steel-list @state)]
    (swap! state assoc :pink-list (mapv #(mover/apply-force % @env width height) pink-list))
    (swap! state assoc :blue-list (mapv #(mover/apply-force % @env width height) blue-list))
    (swap! state assoc :creme-list (mapv #(mover/apply-force % @env width height) creme-list))
    (swap! state assoc :ice-list (mapv #(mover/apply-force % @env width height) ice-list))
    (swap! state assoc :peach-list (mapv #(mover/apply-force % @env width height) peach-list))
    (swap! state assoc :steel-list (mapv #(mover/apply-force % @env width height) steel-list))
    (js/noStroke)
    (dorun
     (for [i pink-list]
       (let [location (:location i)]
         (js/fill pink)
         (js/ellipse (:x location) (:y location) (* 1 60) (* 1 60)))))
    (dorun
     (for [i blue-list]
       (let [location (:location i)]
         (js/fill blue)
         (js/ellipse (:x location) (:y location) (* 1 50) (* 1 50)))))
    (dorun
     (for [i creme-list]
       (let [location (:location i)]
         (js/fill creme)
         (js/ellipse (:x location) (:y location) (* 1 20) (* 1 20)))))
    (dorun
     (for [i peach-list]
       (let [location (:location i)]
         (js/fill peach)
         (js/ellipse (:x location) (:y location) (* 1 100) (* 1 100)))))
    (dorun
     (for [i steel-list]
       (let [location (:location i)]
         (js/fill steel)
         (js/ellipse (:x location) (:y location) (* 1 10) (* 1 10)))))
    (dorun
     (for [i ice-list]
       (let [location (:location i)]
         (js/fill ice)
         (js/ellipse (:x location) (:y location) (* 1 20) (* 1 20)))))))

;; were used together with key press
(defn accelerate []
  (let [x (:x (:wind @env))
        new-x (+ x 0.01)]
    (js/console.log new-x)
    (swap! env assoc-in [:wind :x] new-x)))

(defn decelerate []
  (let [x (:x (:wind @env))
        new-x (- x 0.01)]
    (js/console.log new-x)
    (swap! env assoc-in [:wind :x] new-x)))

(defn antigravity []
  (let [y (:y (:gravity @env))
        new-y (- y 0.1)]
    (swap! env assoc-in [:gravity :y] new-y)))

(defn gravity []
  (let [y (:y (:gravity @env))
        new-y (+ y 0.1)]
    (swap! env assoc-in [:gravity :y] new-y)))


(defn keypressed []
  (let [left 37
        right 39
        up 38
        down 40]
    (condp = js/keyCode
      right (accelerate)
      left (decelerate)
      up (antigravity)
      down (gravity)
      nil)))


;; start stop pattern as described in
;; https://github.com/thheller/shadow-cljs/wiki/ClojureScript-for-the-browser
(defn start []
  (doto js/window
    (aset "setup" setup)
    (aset "draw" draw))
  (js/console.log "START"))

(defn stop []
  (js/clear)
  (js/console.log "STOP"))

(defn ^:export init []
  (js/console.log "INIT")
  (start))

(defn ^:export mouse []
  (js/console.log "Hey there")
  (doto js/window
    (aset "setup" setup-mouse)
    (aset "draw" draw-mouse)))

(defn ^:export wind []
  (js/console.log "wind, schoooo")
  (doto js/window
    (aset "setup" setup-wind)
    (aset "keyPressed" keypressed)
    (aset "draw" draw-wind)))
