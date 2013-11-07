# loopage

Variations of `for' that do other stuff besides building a lazy
sequence.

## Usage

(ns foo
  (:use [loopage.core :only [for-fold]]))

(for-fold [s 0] [x (range 10) y [8 -3 2]] 
  (+ s (* x y)))

## License

Copyright © 2013 Jason Feng

Distributed under the Eclipse Public License, the same as Clojure.
