(ns loopage.core)

(defn for-loop-specs
  "Takes a list of partitioned for-loop specs [[v1 s1] [:while expr] [v2 s2] ...].
   Returns loop specs grouped with modifiers."
  [specs]
  (if (not-empty specs)
    (cons
     (cons (first specs) (take-while (comp keyword? first) (rest specs)))
     (for-loop-specs (drop-while (comp keyword? first) (rest specs))))))

(defn build-fold-modifiers [[[mod x] & more] acc iter-var proceed]
  (if mod
    (case mod
      :let `(let ~x ~(build-fold-modifiers more acc iter-var proceed))
      :when (build-fold-modifiers more acc iter-var `(if u~x ~proceed ~acc))
      :while `(if ~x
                ~(build-fold-modifiers more acc iter-var proceed)
                ~acc))
    `(recur (rest ~iter-var) ~proceed)))

(defn build-fold-loops [acc init [loop & more-loops] body]
  (let [[v s] (first loop)
        modifiers (rest loop)
        iter-var (gensym)]
    `(loop [~iter-var ~s ~acc ~init]
       (if (empty? ~iter-var)
         ~acc
         (let [~v (first ~iter-var)]
           ~(build-fold-modifiers
             modifiers acc iter-var
             (if more-loops
               (build-fold-loops acc acc more-loops body)
               `(do ~@body))))))))

(defmacro for-fold [[acc-var init] specs & body]
  (build-fold-loops
   acc-var init (for-loop-specs (partition 2 specs)) body))
