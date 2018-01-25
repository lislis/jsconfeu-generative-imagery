(ns app.walker)

;; Define an atom with the walker state.
;; Choose either walker-step, walker-prob-right or walker-mouse
;; to define the walker behavior.
;; Then draw the walker.
;; Example below
;;
;; (defonce state
;;   (atom {:walker
;;         {:x (/ width 2) :y (/ height 2)}}))
;;
;; (defn setup []
;;   (js/createCanvas width height))
;;
;; (defn draw []
;;   (swap! state assoc :walker (walker/walker-mouse (:walker @state)))
;;   (walker/walker-draw (:walker @state)))
;;

(defn create [x y]
  {:x x :y y})

(defn seed [num w h]
  (for [i (range num)]
    (let [x (js/map (js/random) 0 1 50 w)
          y (js/map (js/random) 0 1 50 h)
          m (create x y)]
      m)))

(defn walker-draw [walker c]
  (let [x (:x walker)
        y (:y walker)]
    (js/fill c)
    (js/point x y)))

(defn walker-draw-circle [walker]
  (let [x (:x walker)
        y (:y walker)]
    (js/fill 80 200 150 50)
    (js/noStroke)
    (js/ellipse x y 20 20)))

(defn draw-rect [walker c]
  (let [x (:x walker)
        y (:y walker)
        w (js/map (js/randomGaussian) -1 1 30 70)
        h (js/map (js/randomGaussian) -1 1 30 70)]
    (js/noStroke)
    (js/fill c)
    (js/rect x y w h)))

(defn draw-ellip [walker c]
  (let [x (:x walker)
        y (:y walker)
        w (js/map (js/randomGaussian) -1 1 30 60)
        h (js/map (js/randomGaussian) -1 1 30 60)]
    (js/noStroke)
    (js/fill c)
    (js/ellipse x y w h)))


(defn draw-dot [walker c]
  (let [x (:x walker)
        y (:y walker)]
    (js/noStroke)
    (js/fill c)
    (js/ellipse x y 3 3)))


(defn mouse-offset
  "calculate in which direction to update"
  [walker]
  (let [x (:x walker)
        y (:y walker)]
    (-> walker
        (assoc-in [:x] (cond
                         (< x js/mouseX) (inc x)
                         (> x js/mouseX) (dec x)
                         :else x))
        (assoc-in [:y] (cond
                         (< y js/mouseY) (inc y)
                         (> y js/mouseY) (dec y)
                         :else y)))))

(defn walker-step
  "random walker"
  [walker]
  (let [step-x (js/map (js/random -1 1) -1 1 -10 10)
        step-y (js/map (js/random -1 1) -1 1 -10 10)]
    {:x (+ step-x (:x walker))
     :y (+ step-y (:y walker))}))

(defn walker-mouse
  "random walker with preference to mouse position"
  [walker]
  (let [prob (js/random 1)]
    (cond
      (< prob 0.5) (mouse-offset walker)
      :else (walker-step walker))))

(defn walker-prob-right
  "random walker with preference to right"
  [walker]
  (let [prob (js/random 1)]
    (cond
      (< prob 0.4) (update-in walker [:x] inc)
      (< prob 0.6) (update-in walker [:x] dec)
      (< prob 0.8) (update-in walker [:y] inc)
      :else (update-in walker [:y] dec))))

(defn walker-gaussian
  "random walker with Gaussian step size"
  [walker]
  (let [direction (js/Math.floor (js/random 0 4))
        size (js/randomGaussian 2)]
    (cond
      (= direction 0) {:x (+ size (:x walker)) :y (:y walker)}
      (= direction 1) {:x (- size (:x walker)) :y (:y walker)}
      (= direction 2) {:x (:x walker) :y (+ size (:y walker))}
      :else {:x (:x walker) :y (- size (:y walker))})))

(defn probability [num]
  (* num num))

(defn montecarlo []
  (loop []
    (let [r1 (js/random 1)
          prob (probability r1)
          r2 (js/random 1)]
      (if (< r2 prob)
        r1
        (recur)))))

(defn walker-montecarlo
  "taking bigger steps according to Monte Carlo"
  [walker]
  (let [stepsize (* 10 (montecarlo))
        step-x (js/random (- stepsize) stepsize)
        step-y (js/random (- stepsize) stepsize)]
    {:x (+ step-x (:x walker))
     :y (+ step-y (:y walker))}))


;; add xoff and yoff to your state atom!
(defn walker-noise-direction
  "use Perlin noise for location"
  [walker width height]
  (let [x (js/map (js/noise (:xoff walker)) 0 1 0 width)
        y (js/map (js/noise (:yoff walker)) 0 1 0 height)]
    {:x x
     :y y
     :xoff (+ 0.01 (:xoff walker))
     :yoff (+ 0.01 (:yoff walker))}))

(defn walker-noise-step
  "User Perlin noise for step size"
  [walker]
  (let [step-x (js/map (js/noise (:xoff walker)) 0 1 -1 1)
        step-y (js/map (js/noise (:yoff walker)) 0 1 -1 1)
        x (+ step-x (:x walker))
        y (+ step-y (:y walker))]
    ;(js/console.log x)
    {:x x
     :y y
     :xoff (+ 0.01 (:xoff walker))
     :yoff (+ 0.01 (:yoff walker))}))
